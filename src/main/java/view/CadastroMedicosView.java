
package view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import model.CadastroMedicos;
import model.Medico;
import service.MedicosPersistencia;

import java.sql.SQLException;

public class CadastroMedicosView extends Application {

    @Override
    public void start(Stage stage) {

        CadastroMedicos cadastro = new CadastroMedicos();

        Label titulo = new Label("Sistema da Clínica");

        TextField campoNome = new TextField();
        campoNome.setPromptText("Nome do médico");

        TextField campoUsuario = new TextField();
        campoUsuario.setPromptText("Nome de usuário");

        PasswordField campoSenha = new PasswordField();
        campoSenha.setPromptText("Senha");

        ComboBox<String> especialidade = new ComboBox<>();
        especialidade.getItems().addAll(
                "Clínica Geral",
                "Pediatria",
                "Ginecologia",
                "Cardiologia",
                "Psiquiatria"
        );
        especialidade.setPromptText("Especialidade");

        TextField campoConsultorio = new TextField();
        campoConsultorio.setPromptText("Número do consultório");

        Button botaoCadastrar = new Button("Cadastrar");
        Label resultado = new Label();

        ListView<String> listaMedicos = new ListView<>();

        // Carregar médicos do banco
        try {
            MedicosPersistencia.carregar(cadastro);

            for (Medico medico : cadastro.listarMedicos()) {
                adicionarNaLista(listaMedicos, medico);
            }

            resultado.setText("Médicos carregados do banco.");

        } catch (SQLException | IllegalArgumentException e) {
            resultado.setText("Erro ao carregar médicos: " + e.getMessage());
        }

        // Cadastrar médico
        botaoCadastrar.setOnAction(event -> {

            String nome = campoNome.getText().trim();
            String usuario = campoUsuario.getText().trim();
            String senha = campoSenha.getText();
            String esp = especialidade.getValue();

            if (nome.isBlank() || usuario.isBlank()
                    || senha.isBlank() || esp == null
                    || campoConsultorio.getText().isBlank()) {

                resultado.setText("Preencha todos os campos!");
                return;
            }

            try {
                int consultorio = Integer.parseInt(
                        campoConsultorio.getText().trim()
                );

                Medico medico = new Medico(
                        nome,
                        esp,
                        consultorio,
                        usuario,
                        senha
                );

                // Validar sem alterar o cadastro original
                CadastroMedicos validacao = new CadastroMedicos();

                for (Medico existente : cadastro.listarMedicos()) {
                    validacao.cadastrarMedico(existente);
                }

                validacao.cadastrarMedico(medico);

                // Salvar primeiro no MySQL
                MedicosPersistencia.salvar(medico);

                // Atualizar cadastro em memória
                cadastro.cadastrarMedico(medico);

                adicionarNaLista(listaMedicos, medico);

                resultado.setText("Médico cadastrado com sucesso!");

                campoNome.clear();
                campoUsuario.clear();
                campoSenha.clear();
                campoConsultorio.clear();
                especialidade.setValue(null);

            } catch (NumberFormatException e) {

                resultado.setText("Digite um consultório válido!");

            } catch (IllegalArgumentException | SQLException e) {

                resultado.setText("Erro: " + e.getMessage());
            }
        });

        // Buscar por especialidade
        TextField buscaEspecialidade = new TextField();
        buscaEspecialidade.setPromptText("Buscar por especialidade");

        Button botaoBuscarEspecialidade =
                new Button("Buscar especialidade");

        botaoBuscarEspecialidade.setOnAction(event -> {

            listaMedicos.getItems().clear();

            for (Medico medico :
                    cadastro.buscarPorEspecialidade(
                            buscaEspecialidade.getText().trim())) {

                adicionarNaLista(listaMedicos, medico);
            }

            resultado.setText("Busca por especialidade concluída.");
        });

        // Buscar por consultório
        TextField buscaConsultorio = new TextField();
        buscaConsultorio.setPromptText("Número do consultório");

        Button botaoBuscarConsultorio =
                new Button("Buscar consultório");

        botaoBuscarConsultorio.setOnAction(event -> {

            try {
                int numero = Integer.parseInt(
                        buscaConsultorio.getText().trim()
                );

                Medico medico = cadastro.buscarPorConsultorio(numero);

                listaMedicos.getItems().clear();

                if (medico != null) {
                    adicionarNaLista(listaMedicos, medico);
                    resultado.setText("Médico encontrado!");
                } else {
                    resultado.setText("Nenhum médico encontrado.");
                }

            } catch (NumberFormatException e) {
                resultado.setText("Digite um número válido!");
            }
        });

        // Listar todos
        Button botaoListarTodos = new Button("Listar todos");

        botaoListarTodos.setOnAction(event -> {

            listaMedicos.getItems().clear();

            for (Medico medico : cadastro.listarMedicos()) {
                adicionarNaLista(listaMedicos, medico);
            }

            resultado.setText("Todos os médicos cadastrados.");
        });

        VBox layout = new VBox(
                10,
                titulo,
                campoNome,
                campoUsuario,
                campoSenha,
                especialidade,
                campoConsultorio,
                botaoCadastrar,
                new Label("Buscar médico:"),
                buscaEspecialidade,
                botaoBuscarEspecialidade,
                buscaConsultorio,
                botaoBuscarConsultorio,
                botaoListarTodos,
                resultado,
                new Label("Lista de médicos:"),
                listaMedicos
        );

        layout.setStyle("-fx-padding: 20;");

        ScrollPane scroll = new ScrollPane(layout);
        scroll.setFitToWidth(true);

        Scene scene = new Scene(scroll, 500, 850);

        stage.setMaximized(true);
        stage.setTitle("Sistema da Clínica");
        stage.setScene(scene);
        stage.show();
    }

    private void adicionarNaLista(
            ListView<String> lista,
            Medico medico
    ) {
        lista.getItems().add(
                medico.getNome() + " - "
                        + medico.getEspecialidade()
                        + " - Consultório "
                        + medico.getConsultorio()
        );
    }

    public static void main(String[] args) {
        launch(args);
    }
}
