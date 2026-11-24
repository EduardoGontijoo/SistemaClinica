
package view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import model.Senha;
import service.SistemaSenhas;
import service.SistemaSenhasCompartilhado;
import service.HistoricoCompartilhado;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EstatisticasView {

    public void mostrar(Stage stage) {

        Label titulo = new Label("ESTATÍSTICAS DA CLÍNICA");
        titulo.setStyle(
                "-fx-font-size: 25px; -fx-font-weight: bold;"
        );

        Label aguardando = new Label();
        Label atendidos = new Label();

        aguardando.setStyle("-fx-font-size: 18px;");
        atendidos.setStyle("-fx-font-size: 18px;");

        ListView<String> listaHistorico = new ListView<>();
        Label mensagem = new Label();

        Button atualizar = new Button("Atualizar estatísticas");

        Runnable carregarDados = () -> {
            try {
                SistemaSenhas sistema =
                        SistemaSenhasCompartilhado.getInstancia();

                List<Senha> fila = sistema.consultarFila();
                List<String> historico =
                        HistoricoCompartilhado.listar();

                aguardando.setText(
                        "Pacientes aguardando: " + fila.size()
                );

                atendidos.setText(
                        "Pacientes atendidos: " + historico.size()
                );

                List<String> chamadas =
                        new ArrayList<>(historico);

                Collections.reverse(chamadas);
                listaHistorico.getItems().setAll(chamadas);

                mensagem.setText("");

            } catch (IOException e) {
                mensagem.setText(
                        "Erro ao carregar dados: " + e.getMessage()
                );
            }
        };

        atualizar.setOnAction(event -> carregarDados.run());

        HBox indicadores = new HBox(
                30,
                aguardando,
                atendidos
        );

        VBox layout = new VBox(
                20,
                titulo,
                indicadores,
                new Label("Histórico de atendimentos:"),
                listaHistorico,
                atualizar,
                mensagem
        );

        layout.setPadding(new Insets(25));

        carregarDados.run();

        stage.setTitle("Estatísticas da Clínica");
        stage.setScene(new Scene(layout, 850, 600));
        stage.show();
    }
}
