
package view;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import model.Medico;
import service.ConexaoMySQL;
import service.MedicosPersistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminView {

    private final TextField nome = new TextField();
    private final TextField usuario = new TextField();
    private final PasswordField senha = new PasswordField();
    private final TextField consultorio = new TextField();

    private final ComboBox<String> especialidade = new ComboBox<>();

    private final Label mensagem = new Label();

    private final ObservableList<Medico> medicos =
            FXCollections.observableArrayList();

    private final TableView<Medico> tabela = new TableView<>();

    public void mostrar(Stage stage) {

        Label titulo = new Label("Painel Administrativo");
        titulo.setStyle(
                "-fx-font-size: 25px; -fx-font-weight: bold;"
        );

        nome.setPromptText("Nome do médico");
        usuario.setPromptText("Usuário de acesso");
        senha.setPromptText("Senha de acesso");
        consultorio.setPromptText("Número do consultório");

        especialidade.getItems().addAll(
                "Clínica Geral",
                "Pediatria",
                "Ginecologia",
                "Cardiologia",
                "Psiquiatria"
        );

        especialidade.setPromptText("Especialidade");
        especialidade.setMaxWidth(Double.MAX_VALUE);

        Button cadastrar = new Button("Cadastrar médico");
        cadastrar.setMaxWidth(Double.MAX_VALUE);
        cadastrar.setOnAction(e -> cadastrarMedico());

        Button excluir = new Button("Excluir médico selecionado");
        excluir.setMaxWidth(Double.MAX_VALUE);
        excluir.setOnAction(e -> excluirMedico());

        Button atualizar = new Button("Atualizar lista");
        atualizar.setMaxWidth(Double.MAX_VALUE);
        atualizar.setOnAction(e -> carregarMedicos());

        configurarTabela();

        VBox formulario = new VBox(
                10,
                new Label("Cadastro de médicos"),
                nome,
                usuario,
                senha,
                especialidade,
                consultorio,
                cadastrar,
                mensagem
        );

        formulario.setPadding(new Insets(15));
        formulario.setPrefWidth(280);

        VBox painelTabela = new VBox(
                10,
                new Label("Médicos cadastrados"),
                tabela,
                atualizar,
                excluir
        );

        painelTabela.setPadding(new Insets(15));
        VBox.setVgrow(tabela, Priority.ALWAYS);

        HBox principal = new HBox(
                20,
                formulario,
                painelTabela
        );

        principal.setPadding(new Insets(20));
        principal.setAlignment(Pos.CENTER);

        Scene scene = new Scene(principal, 950, 600);

        stage.setTitle("Sistema da Clínica - Administrador");
        stage.setScene(scene);
        stage.show();

        carregarMedicos();
    }

    private void configurarTabela() {

        TableColumn<Medico, String> colunaNome =
                new TableColumn<>("Nome");

        colunaNome.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getNome())
        );

        TableColumn<Medico, String> colunaUsuario =
                new TableColumn<>("Usuário");

        colunaUsuario.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getUsuario())
        );

        TableColumn<Medico, String> colunaEspecialidade =
                new TableColumn<>("Especialidade");

        colunaEspecialidade.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getEspecialidade())
        );

        TableColumn<Medico, Number> colunaConsultorio =
                new TableColumn<>("Consultório");

        colunaConsultorio.setCellValueFactory(d ->
                new SimpleIntegerProperty(
                        d.getValue().getConsultorio()
                )
        );

        tabela.getColumns().addAll(
                colunaNome,
                colunaUsuario,
                colunaEspecialidade,
                colunaConsultorio
        );

        tabela.setItems(medicos);
        tabela.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );
    }

    private void cadastrarMedico() {

        String nomeMedico = nome.getText().trim();
        String usuarioMedico = usuario.getText().trim();
        String senhaMedico = senha.getText();
        String especialidadeMedico = especialidade.getValue();
        String consultorioTexto = consultorio.getText().trim();

        if (nomeMedico.isEmpty()
                || usuarioMedico.isEmpty()
                || senhaMedico.isEmpty()
                || especialidadeMedico == null
                || consultorioTexto.isEmpty()) {

            mensagem.setText("Preencha todos os campos.");
            return;
        }

        int numeroConsultorio;

        try {
            numeroConsultorio = Integer.parseInt(consultorioTexto);

            if (numeroConsultorio <= 0) {
                mensagem.setText("Informe um consultório válido.");
                return;
            }

        } catch (NumberFormatException e) {
            mensagem.setText("O consultório deve ser um número.");
            return;
        }

        try {

            if (usuarioExiste(usuarioMedico)) {
                mensagem.setText("Esse usuário já está cadastrado.");
                return;
            }

            if (consultorioOcupado(
                    especialidadeMedico,
                    numeroConsultorio
            )) {
                mensagem.setText(
                        "Esse consultório já está ocupado nessa especialidade."
                );
                return;
            }

            if (quantidadePorEspecialidade(especialidadeMedico) >= 3) {
                mensagem.setText(
                        "Essa especialidade já possui 3 médicos."
                );
                return;
            }

            Medico medico = new Medico(
                    nomeMedico,
                    especialidadeMedico,
                    numeroConsultorio,
                    usuarioMedico,
                    senhaMedico
            );

            MedicosPersistencia.salvar(medico);

            mensagem.setText("Médico cadastrado com sucesso!");

            limparCampos();
            carregarMedicos();

        } catch (SQLException e) {

            mensagem.setText(
                    "Erro ao cadastrar: " + e.getMessage()
            );

            e.printStackTrace();
        }
    }

    private boolean usuarioExiste(String usuarioMedico)
            throws SQLException {

        String sql = """
                SELECT COUNT(*)
                FROM medicos
                WHERE usuario = ?
                """;

        try (Connection conexao = ConexaoMySQL.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, usuarioMedico);

            try (ResultSet resultado = stmt.executeQuery()) {
                resultado.next();
                return resultado.getInt(1) > 0;
            }
        }
    }

    private boolean consultorioOcupado(
            String especialidadeMedico,
            int numeroConsultorio
    ) throws SQLException {

        String sql = """
                SELECT COUNT(*)
                FROM medicos
                WHERE especialidade = ?
                AND consultorio = ?
                """;

        try (Connection conexao = ConexaoMySQL.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, especialidadeMedico);
            stmt.setInt(2, numeroConsultorio);

            try (ResultSet resultado = stmt.executeQuery()) {
                resultado.next();
                return resultado.getInt(1) > 0;
            }
        }
    }

    private int quantidadePorEspecialidade(String especialidadeMedico)
            throws SQLException {

        String sql = """
                SELECT COUNT(*)
                FROM medicos
                WHERE especialidade = ?
                """;

        try (Connection conexao = ConexaoMySQL.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, especialidadeMedico);

            try (ResultSet resultado = stmt.executeQuery()) {
                resultado.next();
                return resultado.getInt(1);
            }
        }
    }

    private void carregarMedicos() {

        medicos.clear();

        String sql = """
                SELECT nome, usuario, senha, especialidade, consultorio
                FROM medicos
                ORDER BY especialidade, nome
                """;

        try (Connection conexao = ConexaoMySQL.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet resultado = stmt.executeQuery()) {

            while (resultado.next()) {

                Medico medico = new Medico(
                        resultado.getString("nome"),
                        resultado.getString("especialidade"),
                        resultado.getInt("consultorio"),
                        resultado.getString("usuario"),
                        resultado.getString("senha")
                );

                medicos.add(medico);
            }

        } catch (SQLException e) {

            mensagem.setText(
                    "Erro ao carregar médicos: " + e.getMessage()
            );

            e.printStackTrace();
        }
    }

    private void excluirMedico() {

        Medico selecionado = tabela.getSelectionModel()
                .getSelectedItem();

        if (selecionado == null) {
            mensagem.setText("Selecione um médico para excluir.");
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);

        confirmacao.setTitle("Excluir médico");
        confirmacao.setHeaderText("Deseja excluir este médico?");
        confirmacao.setContentText(selecionado.getNome());

        confirmacao.showAndWait().ifPresent(resposta -> {

            if (resposta == ButtonType.OK) {

                String sql = """
                        DELETE FROM medicos
                        WHERE usuario = ?
                        """;

                try (Connection conexao = ConexaoMySQL.conectar();
                     PreparedStatement stmt =
                             conexao.prepareStatement(sql)) {

                    stmt.setString(1, selecionado.getUsuario());
                    stmt.executeUpdate();

                    mensagem.setText("Médico excluído com sucesso!");
                    carregarMedicos();

                } catch (SQLException e) {

                    mensagem.setText(
                            "Erro ao excluir: " + e.getMessage()
                    );

                    e.printStackTrace();
                }
            }
        });
    }

    private void limparCampos() {

        nome.clear();
        usuario.clear();
        senha.clear();
        consultorio.clear();
        especialidade.setValue(null);
    }
}
