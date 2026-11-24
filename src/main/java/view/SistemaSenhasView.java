package view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Paciente;
import model.Senha;
import service.SistemaSenhas;
import javafx.scene.control.ScrollPane;

public class SistemaSenhasView extends Application {

    @Override
    public void start(Stage stage) {
        SistemaSenhas sistema = new SistemaSenhas();

        Label titulo = new Label("Sistema de Senhas da Clínica");

        TextField campoNome = new TextField();
        campoNome.setPromptText("Nome do paciente");

        TextField campoIdade = new TextField();
        campoIdade.setPromptText("Idade do paciente");

        CheckBox possuiDeficiencia = new CheckBox("Pessoa com deficiência");
        CheckBox gestante = new CheckBox("Gestante");

        Button botaoEmitir = new Button("Emitir senha");
        Button botaoChamar = new Button("Chamar próximo");
        Button botaoAtualizar = new Button("Atualizar fila");

        Label resultado = new Label();
        Label quantidade = new Label("Pacientes na fila: 0");
        Label pacienteChamado = new Label("Nenhum paciente chamado.");

        ListView<String> listaFila = new ListView<>();
        ListView<String> historico = new ListView<>();

        Runnable atualizarFila = () -> {
            listaFila.getItems().clear();

            for (Senha senha : sistema.consultarFila()) {
                Paciente paciente = senha.getPaciente();

                if (paciente != null) {
                    listaFila.getItems().add(
                            senha.getSenhaFormatada() + " - "
                                    + paciente.getNome() + " - "
                                    + paciente.getIdade() + " anos"
                    );
                }
            }

            quantidade.setText(
                    "Pacientes na fila: " + sistema.quantidadeNaFila()
            );
        };

        botaoEmitir.setOnAction(event -> {
            String nome = campoNome.getText().trim();

            if (nome.isBlank() || campoIdade.getText().isBlank()) {
                resultado.setText("Preencha o nome e a idade!");
                return;
            }

            try {
                int idade = Integer.parseInt(campoIdade.getText());

                if (idade < 0 || idade > 120) {
                    resultado.setText("Digite uma idade válida!");
                    return;
                }

                Paciente paciente = new Paciente(
                        nome,
                        idade,
                        possuiDeficiencia.isSelected(),
                        gestante.isSelected(),
                        false,
                        false
                );

                Senha senha = sistema.emitirSenha(paciente);

                resultado.setText(
                        "Senha emitida: " + senha.getSenhaFormatada()
                );

                campoNome.clear();
                campoIdade.clear();
                possuiDeficiencia.setSelected(false);
                gestante.setSelected(false);

                atualizarFila.run();

            } catch (NumberFormatException e) {
                resultado.setText("A idade deve ser um número!");
            }
        });

        botaoChamar.setOnAction(event -> {
            Senha senha = sistema.chamarProximoPaciente();

            if (senha == null) {
                resultado.setText("Não há pacientes na fila!");
            } else {
                Paciente paciente = senha.getPaciente();

                String mensagem = "Chamando: "
                        + senha.getSenhaFormatada()
                        + " - " + paciente.getNome();

                resultado.setText(mensagem);
                pacienteChamado.setText(mensagem);
                historico.getItems().add(mensagem);
            }

            atualizarFila.run();
        });

        botaoAtualizar.setOnAction(event -> atualizarFila.run());

        VBox layout = new VBox(
                10,
                titulo,
                campoNome,
                campoIdade,
                possuiDeficiencia,
                gestante,
                botaoEmitir,
                resultado,
                new Label("Paciente chamado:"),
                pacienteChamado,
                quantidade,
                new Label("Fila de espera:"),
                listaFila,
                botaoChamar,
                botaoAtualizar,
                new Label("Histórico de atendimentos:"),
                historico
        );

        layout.setStyle("-fx-padding: 20;");

        ScrollPane scroll = new ScrollPane(layout);
        scroll.setFitToWidth(true);

        Scene scene = new Scene(scroll, 500, 650);

        stage.setTitle("Senhas da Clínica");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

