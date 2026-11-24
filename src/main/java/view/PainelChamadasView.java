
package view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import service.HistoricoCompartilhado;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PainelChamadasView {

    public void mostrar(Stage stage) {

        Label titulo = new Label("PAINEL DE CHAMADAS");
        titulo.setStyle(
                "-fx-font-size: 26px; -fx-font-weight: bold;"
        );

        Label ultimaChamada = new Label(
                "Aguardando chamada de pacientes..."
        );
        ultimaChamada.setStyle(
                "-fx-font-size: 20px; -fx-font-weight: bold;"
        );

        ListView<String> listaChamadas = new ListView<>();
        Label mensagem = new Label();

        Button atualizar = new Button("Atualizar painel");

        Runnable atualizarPainel = () -> {
            try {
                List<String> registros =
                        HistoricoCompartilhado.listar();

                List<String> chamadas = new ArrayList<>();

                for (String registro : registros) {
                    if (registro.contains("PACIENTE CHAMADO")) {
                        chamadas.add(registro);
                    }
                }

                Collections.reverse(chamadas);
                listaChamadas.getItems().setAll(chamadas);

                if (!chamadas.isEmpty()) {
                    ultimaChamada.setText(
                            "ÚLTIMA CHAMADA:\n" + chamadas.get(0)
                    );
                } else {
                    ultimaChamada.setText(
                            "Aguardando chamada de pacientes..."
                    );
                }

                mensagem.setText("");

            } catch (IOException e) {
                mensagem.setText(
                        "Erro ao carregar as chamadas: " + e.getMessage()
                );
            }
        };

        atualizar.setOnAction(event -> atualizarPainel.run());

        atualizarPainel.run();

        VBox layout = new VBox(
                15,
                titulo,
                ultimaChamada,
                new Label("Chamadas anteriores:"),
                listaChamadas,
                atualizar,
                mensagem
        );

        layout.setPadding(new Insets(25));

        stage.setTitle("Painel de Chamadas - Clínica");
        stage.setScene(new Scene(layout, 800, 600));
        stage.show();
    }
}
