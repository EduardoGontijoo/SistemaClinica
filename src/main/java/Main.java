import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Pos;

public class Main extends Application {
    private int numeroSenha = 1;
    private int numeroSenhaPrioritaria = 1;

    @Override
    public void start(Stage tela) {
        Label titulo = new Label("Sistema de Senhas - Clínica");
        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #245B58;");
        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #245B58;");
        Button botaoSenha = new Button("Emitir senha");
        botaoSenha.setPrefWidth(220);
        botaoSenha.setPrefHeight(40);
        botaoSenha.setStyle("-fx-background-color: #2E8B83; -fx-text-fill: white; -fx-background-radius: 10;");
        Button botaoPrioritario = new Button("Emitir senha prioritária");
        botaoPrioritario.setPrefWidth(220);
        botaoPrioritario.setPrefHeight(40);
        botaoPrioritario.setStyle("-fx-background-color: #D98E36; -fx-text-fill: white; -fx-background-radius: 10;");


        botaoSenha.setOnAction(e -> {
            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Sistema da Clínica");
            alerta.setContentText("Senha: N" + String.format("%03d", numeroSenha));
            alerta.showAndWait();
            numeroSenha++;
        });

        botaoPrioritario.setOnAction(e -> {
            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Sistema da Clínica");
            alerta.setContentText("Senha: P" + String.format("%03d", numeroSenhaPrioritaria));
            alerta.showAndWait();

            numeroSenhaPrioritaria++;
        });

        VBox layout = new VBox(titulo, botaoSenha, botaoPrioritario);
        layout.setSpacing(20);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #EAF4F4;");
        Scene cena = new Scene(layout, 600, 400);

        tela.setTitle("Sistema da Clínica");
        tela.setScene(cena);
        tela.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
