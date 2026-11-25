
package service;

import model.Paciente;
import model.Senha;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class SenhasPersistencia {

    public static int proximoNumero() throws SQLException {

        String sql = "SELECT COALESCE(MAX(numero), 0) + 1 AS proximo FROM senhas";

        try (Connection conexao = ConexaoMySQL.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {

            if (resultado.next()) {
                return resultado.getInt("proximo");
            }
        }

        return 1;
    }

    public static int salvar(
            int pacienteId,
            Paciente paciente,
            Senha senha,
            String especialidade
    ) throws SQLException {

        String sql = """
            INSERT INTO senhas
            (paciente_id, medico_id, numero, tipo, prioridade,
             status, especialidade, data_emissao, data_chamada)
            VALUES (?, NULL, ?, ?, ?, 'AGUARDANDO', ?,
                    CURRENT_TIMESTAMP, NULL)
            """;

        try (Connection conexao = ConexaoMySQL.conectar();
             PreparedStatement comando = conexao.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            comando.setInt(1, pacienteId);
            comando.setInt(2, senha.getNumero());
            comando.setString(3, senha.getTipo());
            comando.setString(4, obterPrioridade(paciente));
            comando.setString(5, especialidade);

            comando.executeUpdate();

            try (ResultSet resultado = comando.getGeneratedKeys()) {
                if (resultado.next()) {
                    return resultado.getInt(1);
                }
            }
        }

        throw new SQLException("Não foi possível salvar a senha.");
    }

    public static void carregarFila(
            SistemaSenhas sistema,
            Map<Senha, String> dadosFila
    ) throws SQLException {

        if (!sistema.consultarFila().isEmpty()) {
            return;
        }

        String sql = """
            SELECT
                s.numero,
                s.tipo,
                p.nome,
                p.idade,
                p.possui_deficiencia,
                p.gestante,
                p.possui_obesidade,
                p.crianca_de_colo,
                s.especialidade
            FROM senhas s
            INNER JOIN pacientes p ON p.id = s.paciente_id
            WHERE s.status = 'AGUARDANDO'
            ORDER BY
                CASE s.prioridade
                    WHEN '80+' THEN 0
                    WHEN '60+' THEN 1
                    WHEN 'PCD' THEN 2
                    WHEN 'GESTANTE' THEN 3
                    WHEN 'OBESIDADE' THEN 4
                    WHEN 'CRIANCA_DE_COLO' THEN 5
                    ELSE 6
                END,
                s.numero
            """;

        try (Connection conexao = ConexaoMySQL.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {

            while (resultado.next()) {

                String nome = resultado.getString("nome");
                int idade = resultado.getInt("idade");

                boolean deficiencia =
                        resultado.getBoolean("possui_deficiencia");

                boolean gestante =
                        resultado.getBoolean("gestante");

                boolean obesidade =
                        resultado.getBoolean("possui_obesidade");

                boolean crianca =
                        resultado.getBoolean("crianca_de_colo");

                String especialidade =
                        resultado.getString("especialidade");

                int numero = resultado.getInt("numero");
                String tipo = resultado.getString("tipo");

                Paciente paciente = new Paciente(
                        nome, idade, deficiencia,
                        gestante, obesidade, crianca
                );

                sistema.recuperarSenha(paciente, numero, tipo);

                List<Senha> senhas = sistema.consultarFila();
                Senha senha = senhas.get(senhas.size() - 1);

                String registro =
                        codificar(nome) + "|"
                                + idade + "|"
                                + deficiencia + "|"
                                + gestante + "|"
                                + obesidade + "|"
                                + crianca + "|"
                                + codificar(especialidade) + "|"
                                + numero + "|"
                                + tipo;

                dadosFila.put(senha, registro);
            }
        }
    }

    private static String obterPrioridade(Paciente paciente) {

        if (paciente.getIdade() >= 80) return "80+";
        if (paciente.getIdade() >= 60) return "60+";
        if (paciente.isPossuiDeficiencia()) return "PCD";
        if (paciente.isGestante()) return "GESTANTE";
        if (paciente.isPossuiObesidade()) return "OBESIDADE";
        if (paciente.isCriancaDeColo()) return "CRIANCA_DE_COLO";

        return "NORMAL";
    }

    private static String codificar(String texto) {
        return Base64.getEncoder().encodeToString(
                texto.getBytes(StandardCharsets.UTF_8)
        );
    }
}
