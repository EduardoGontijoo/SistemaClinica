package service;

import java.sql.Connection;
import java.sql.SQLException;

public class TesteConexaoMySQL {

    public static void main(String[] args) {

        try (Connection conexao = ConexaoMySQL.conectar()) {

            System.out.println("Conexão com MySQL realizada com sucesso!");

        } catch (SQLException e) {

            System.out.println("Erro ao conectar com MySQL:");
            e.printStackTrace();

        }
    }
}

