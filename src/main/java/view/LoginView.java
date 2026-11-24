
package view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import model.Medico;
import service.MedicosPersistencia;

import java.sql.SQLException;

public class LoginView extends Application {

    @Override
    public void start(Stage stage) {

        Label titulo = new Label("Sistema da Clínica");
        titulo.setStyle(
                "-fx-font-size: 25px;" +
                        "-fx-font-weight: bold;"
        );

        Label subtitulo = new Label("Acesse sua conta");

        TextField usuario = new TextField();
        usuario.setPromptText("Usuário");
        usuario.setMaxWidth(250);

        PasswordField senha = new PasswordField();
        senha.setPromptText("Senha");
        senha.setMaxWidth(250);

        Button entrar = new Button("Entrar");
        entrar.setMaxWidth(250);

        Label mensagem = new Label();

        entrar.setOnAction(event -> {

            String nomeUsuario = usuario.getText().trim();
            String senhaDigitada = senha.getText();

            if (nomeUsuario.isBlank() || senhaDigitada.isBlank()) {
                mensagem.setText("Preencha usuário e senha!");
                return;
            }

            // Administrador
            if (nomeUsuario.equals("admin")
                    && senhaDigitada.equals("admin123")) {

                new AdminView().mostrar(stage);
                return;
            }

            // Paciente
            if (nomeUsuario.equals("paciente")
                    && senhaDigitada.equals("paciente123")) {

                new PacienteView().mostrar(stage);
                return;
            }

            // Médico
            try {

                Medico medico = MedicosPersistencia.autenticar(
                        nomeUsuario,
                        senhaDigitada
                );

                if (medico != null) {

                    new MedicoView().mostrar(stage, medico);

                } else {

                    mensagem.setText(
                            "Usuário ou senha incorretos!"
                    );
                }

            } catch (SQLException e) {

                mensagem.setText(
                        "Erro ao acessar o banco de dados."
                );

                e.printStackTrace();
            }
        });

        VBox layout = new VBox(
                15,
                titulo,
                subtitulo,
                usuario,
                senha,
                entrar,
                mensagem
        );

        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));

        Scene scene = new Scene(layout, 400, 350);

        stage.setTitle("Login - Sistema da Clínica");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
