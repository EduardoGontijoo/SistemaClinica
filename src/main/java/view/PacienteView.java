
package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import model.Paciente;
import model.Senha;
import service.PacientesPersistencia;
import service.SenhasPersistencia;
import service.SistemaSenhas;
import service.SistemaSenhasCompartilhado;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class PacienteView {

    private final SistemaSenhas sistema =
            SistemaSenhasCompartilhado.getInstancia();

    private final Map<Senha, String> dadosFila =
            SistemaSenhasCompartilhado.getDadosFila();

    private final TextField nome = new TextField();
    private final TextField idade = new TextField();

    private final ComboBox<String> especialidade = new ComboBox<>();

    private final CheckBox deficiencia =
            new CheckBox("Pessoa com deficiência");

    private final CheckBox gestante =
            new CheckBox("Gestante");

    private final CheckBox obesidade =
            new CheckBox("Pessoa com obesidade");

    private final CheckBox criancaDeColo =
            new CheckBox("Criança de colo");

    private final Label resultado = new Label();
    private final ListView<String> listaFila = new ListView<>();

    public void mostrar(Stage stage) {

        carregarFilaDoBanco();

        Label titulo = new Label("Atendimento da Clínica");
        titulo.setStyle(
                "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold;"
        );

        nome.setPromptText("Nome completo");
        idade.setPromptText("Idade");

        especialidade.getItems().addAll(
                "Clínica Geral",
                "Pediatria",
                "Ginecologia",
                "Cardiologia",
                "Psiquiatria"
        );

        especialidade.setPromptText("Selecione a especialidade");
        especialidade.setMaxWidth(Double.MAX_VALUE);

        Button emitir = new Button("Emitir senha");
        emitir.setMaxWidth(Double.MAX_VALUE);
        emitir.setOnAction(event -> emitirSenha());

        Button atualizar = new Button("Atualizar fila");
        atualizar.setMaxWidth(Double.MAX_VALUE);
        atualizar.setOnAction(event -> atualizarFila());

        resultado.setWrapText(true);

        Label tituloFila = new Label("Pacientes aguardando");
        tituloFila.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        VBox formulario = new VBox(
                10,
                titulo,
                nome,
                idade,
                especialidade,
                new Label("Informações de prioridade:"),
                deficiencia,
                gestante,
                obesidade,
                criancaDeColo,
                emitir,
                resultado
        );

        formulario.setPadding(new Insets(15));
        formulario.setMaxWidth(350);

        VBox painelFila = new VBox(
                10,
                tituloFila,
                listaFila,
                atualizar
        );

        painelFila.setPadding(new Insets(15));
        painelFila.setPrefWidth(350);
        VBox.setVgrow(listaFila, Priority.ALWAYS);

        HBox principal = new HBox(20, formulario, painelFila);
        principal.setPadding(new Insets(20));
        principal.setAlignment(Pos.CENTER);

        Scene scene = new Scene(principal, 800, 550);

        stage.setTitle("Sistema da Clínica - Paciente");
        stage.setScene(scene);
        stage.show();

        atualizarFila();
    }

    private void emitirSenha() {

        String nomePaciente = nome.getText().trim();
        String idadeTexto = idade.getText().trim();
        String especialidadeEscolhida = especialidade.getValue();

        if (nomePaciente.isEmpty()
                || idadeTexto.isEmpty()
                || especialidadeEscolhida == null) {

            resultado.setText(
                    "Preencha o nome, a idade e a especialidade."
            );
            return;
        }

        int idadePaciente;

        try {
            idadePaciente = Integer.parseInt(idadeTexto);

            if (idadePaciente < 0 || idadePaciente > 120) {
                resultado.setText("Informe uma idade válida.");
                return;
            }

        } catch (NumberFormatException e) {
            resultado.setText("A idade deve ser um número.");
            return;
        }

        Paciente paciente = new Paciente(
                nomePaciente,
                idadePaciente,
                deficiencia.isSelected(),
                gestante.isSelected(),
                obesidade.isSelected(),
                criancaDeColo.isSelected()
        );

        try {

            // Consulta o banco antes de gerar a próxima senha.
            int proximoNumero = SenhasPersistencia.proximoNumero();

            sistema.definirProximaSenha(proximoNumero);

            Senha senha = sistema.emitirSenha(paciente);

            int pacienteId = PacientesPersistencia.salvar(
                    paciente,
                    especialidadeEscolhida,
                    senha.getSenhaFormatada()
            );

            SenhasPersistencia.salvar(
                    pacienteId,
                    paciente,
                    senha,
                    especialidadeEscolhida
            );

            String dados = codificar(nomePaciente)
                    + "|" + idadePaciente
                    + "|" + deficiencia.isSelected()
                    + "|" + gestante.isSelected()
                    + "|" + obesidade.isSelected()
                    + "|" + criancaDeColo.isSelected()
                    + "|" + codificar(especialidadeEscolhida)
                    + "|" + senha.getNumero()
                    + "|" + senha.getTipo();

            dadosFila.put(senha, dados);

            resultado.setText(
                    "Senha emitida com sucesso: "
                            + senha.getSenhaFormatada()
            );

            limparFormulario();
            atualizarFila();

        } catch (Exception e) {

            resultado.setText(
                    "Erro ao emitir senha: " + e.getMessage()
            );

            e.printStackTrace();
        }
    }

    private void carregarFilaDoBanco() {

        try {
            SenhasPersistencia.carregarFila(sistema, dadosFila);

        } catch (Exception e) {

            resultado.setText(
                    "Erro ao carregar fila: " + e.getMessage()
            );

            e.printStackTrace();
        }
    }

    private void atualizarFila() {

        listaFila.getItems().clear();

        List<Senha> senhas = sistema.consultarFila();

        for (Senha senha : senhas) {

            Paciente paciente = senha.getPaciente();

            if (paciente != null) {

                listaFila.getItems().add(
                        senha.getSenhaFormatada()
                                + " - "
                                + paciente.getNome()
                );
            }
        }

        if (listaFila.getItems().isEmpty()) {
            listaFila.getItems().add("Nenhum paciente aguardando.");
        }
    }

    private void limparFormulario() {

        nome.clear();
        idade.clear();
        especialidade.setValue(null);

        deficiencia.setSelected(false);
        gestante.setSelected(false);
        obesidade.setSelected(false);
        criancaDeColo.setSelected(false);
    }

    private String codificar(String texto) {

        return Base64.getEncoder().encodeToString(
                texto.getBytes(StandardCharsets.UTF_8)
        );
    }
}
