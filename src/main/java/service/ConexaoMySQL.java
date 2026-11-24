
package service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoMySQL {

    private static final String URL =
            "jdbc:mysql://localhost:3306/sistema_clinica";

    private static final String USUARIO =
            System.getenv().getOrDefault("CLINICA_DB_USER", "root");

    private static final String SENHA =
            System.getenv("CLINICA_DB_PASSWORD");

    public static Connection conectar() throws SQLException {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            if (SENHA == null || SENHA.isBlank()) {
                throw new SQLException(
                        "Configure a variável CLINICA_DB_PASSWORD."
                );
            }

            return DriverManager.getConnection(
                    URL,
                    USUARIO,
                    SENHA
            );

        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "Driver MySQL não encontrado!", e
            );
        }
    }
}
