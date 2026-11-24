
package service;

import model.CadastroMedicos;
import model.Medico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MedicosPersistencia {

    public static void salvar(Medico medico) throws SQLException {

        String sqlVerificar = """
                SELECT COUNT(*) FROM medicos
                WHERE usuario = ? OR consultorio = ?
                """;

        String sqlLimite = """
                SELECT COUNT(*) FROM medicos
                WHERE especialidade = ?
                """;

        String sqlInserir = """
                INSERT INTO medicos
                (nome, usuario, senha, especialidade, consultorio)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conexao = ConexaoMySQL.conectar()) {

            try (PreparedStatement comando =
                         conexao.prepareStatement(sqlVerificar)) {

                comando.setString(1, medico.getUsuario());
                comando.setInt(2, medico.getConsultorio());

                try (ResultSet resultado = comando.executeQuery()) {
                    resultado.next();

                    if (resultado.getInt(1) > 0) {
                        throw new SQLException(
                                "Usuário ou consultório já cadastrado."
                        );
                    }
                }
            }

            try (PreparedStatement comando =
                         conexao.prepareStatement(sqlLimite)) {

                comando.setString(1, medico.getEspecialidade());

                try (ResultSet resultado = comando.executeQuery()) {
                    resultado.next();

                    if (resultado.getInt(1) >= 3) {
                        throw new SQLException(
                                "Essa especialidade já possui 3 médicos."
                        );
                    }
                }
            }

            try (PreparedStatement comando =
                         conexao.prepareStatement(sqlInserir)) {

                comando.setString(1, medico.getNome());
                comando.setString(2, medico.getUsuario());
                comando.setString(3, medico.getSenha());
                comando.setString(4, medico.getEspecialidade());
                comando.setInt(5, medico.getConsultorio());

                comando.executeUpdate();
            }
        }
    }

    public static void carregar(CadastroMedicos cadastro)
            throws SQLException {

        if (!cadastro.estaVazio()) {
            return;
        }

        String sql = """
                SELECT nome, usuario, senha, especialidade, consultorio
                FROM medicos
                """;

        try (Connection conexao = ConexaoMySQL.conectar();
             PreparedStatement comando =
                     conexao.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {

            while (resultado.next()) {

                Medico medico = new Medico(
                        resultado.getString("nome"),
                        resultado.getString("especialidade"),
                        resultado.getInt("consultorio"),
                        resultado.getString("usuario"),
                        resultado.getString("senha")
                );

                cadastro.cadastrarMedico(medico);
            }
        }
    }

    public static Medico autenticar(String usuario, String senha)
            throws SQLException {

        String sql = """
                SELECT nome, usuario, senha, especialidade, consultorio
                FROM medicos
                WHERE usuario = ? AND senha = ?
                """;

        try (Connection conexao = ConexaoMySQL.conectar();
             PreparedStatement comando =
                     conexao.prepareStatement(sql)) {

            comando.setString(1, usuario);
            comando.setString(2, senha);

            try (ResultSet resultado = comando.executeQuery()) {

                if (resultado.next()) {

                    return new Medico(
                            resultado.getString("nome"),
                            resultado.getString("especialidade"),
                            resultado.getInt("consultorio"),
                            resultado.getString("usuario"),
                            resultado.getString("senha")
                    );
                }
            }
        }

        return null;
    }
}
