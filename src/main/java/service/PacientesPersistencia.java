
package service;

import model.Paciente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PacientesPersistencia {

    public static int salvar(
            Paciente paciente,
            String especialidadeEscolhida,
            String senha
    ) throws SQLException {

        String sql = """
            INSERT INTO pacientes
            (nome, idade, possui_deficiencia, gestante,
             possui_obesidade, crianca_de_colo, especialidade, senha)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conexao = ConexaoMySQL.conectar();
             PreparedStatement comando = conexao.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            comando.setString(1, paciente.getNome());
            comando.setInt(2, paciente.getIdade());
            comando.setBoolean(3, paciente.isPossuiDeficiencia());
            comando.setBoolean(4, paciente.isGestante());
            comando.setBoolean(5, paciente.isPossuiObesidade());
            comando.setBoolean(6, paciente.isCriancaDeColo());
            comando.setString(7, especialidadeEscolhida);
            comando.setString(8, senha);

            comando.executeUpdate();

            try (ResultSet resultado = comando.getGeneratedKeys()) {
                if (resultado.next()) {
                    return resultado.getInt(1);
                }
            }
        }

        throw new SQLException("Não foi possível salvar o paciente.");
    }
}
