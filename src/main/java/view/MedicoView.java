
package view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import model.Medico;
import model.Paciente;
import model.Senha;
import service.ConexaoMySQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MedicoView {

    private int medicoId;
    private int senhaIdEmAtendimento;
    private int atendimentoId;

    private Senha senhaEmAtendimento;
    private String nomePacienteEmAtendimento;

    private final Map<Senha, Integer> idsSenhas = new HashMap<>();

    public void mostrar(Stage stage, Medico medico) {

        if (medico == null) {
            new LoginView().start(stage);
            return;
        }

        Label titulo = new Label("Área do Médico");

        Label dadosMedico = new Label(
                "Dr(a). " + medico.getNome()
                        + "\nEspecialidade: " + medico.getEspecialidade()
                        + "\nConsultório: " + medico.getConsultorio()
        );

        ListView<String> listaFila = new ListView<>();
        ListView<String> historico = new ListView<>();

        Label mensagem = new Label();
        Label pacienteChamado =
                new Label("Nenhum paciente em atendimento.");

        Button atualizar = new Button("Atualizar fila");
        Button chamar = new Button("Chamar próximo paciente");
        Button finalizar = new Button("Finalizar atendimento");
        Button sair = new Button("Sair");

        finalizar.setDisable(true);

        try {
            medicoId = buscarIdMedico(medico);

            // Recupera atendimento que ficou em andamento.
            recuperarAtendimento(pacienteChamado, finalizar);

            atualizarFila(medico, listaFila);
            carregarHistorico(historico);

        } catch (SQLException e) {
            mensagem.setText("Erro ao carregar dados: " + e.getMessage());
            e.printStackTrace();
        }

        atualizar.setOnAction(event -> {
            try {
                atualizarFila(medico, listaFila);
                carregarHistorico(historico);
                mensagem.setText("Fila atualizada.");

            } catch (SQLException e) {
                mensagem.setText("Erro ao atualizar: " + e.getMessage());
            }
        });

        chamar.setOnAction(event -> {

            if (senhaEmAtendimento != null) {
                mensagem.setText(
                        "Finalize o atendimento atual antes de chamar outro paciente."
                );
                return;
            }

            try {
                Senha proxima = buscarProximaSenha(medico);

                if (proxima == null) {
                    mensagem.setText(
                            "Não há pacientes aguardando nessa especialidade."
                    );
                    return;
                }

                int idSenha = idsSenhas.get(proxima);
                String nomePaciente = proxima.getPaciente().getNome();

                String sqlAtualizar = """
                        UPDATE senhas
                        SET status = 'EM_ATENDIMENTO',
                            medico_id = ?,
                            data_chamada = CURRENT_TIMESTAMP
                        WHERE id = ? AND status = 'AGUARDANDO'
                        """;

                String sqlAtendimento = """
                        INSERT INTO atendimentos
                        (senha_id, medico_id, data_inicio, status)
                        VALUES (?, ?, CURRENT_TIMESTAMP, 'EM_ATENDIMENTO')
                        """;

                try (Connection conexao = ConexaoMySQL.conectar()) {

                    conexao.setAutoCommit(false);

                    try {
                        try (PreparedStatement comando =
                                     conexao.prepareStatement(sqlAtualizar)) {

                            comando.setInt(1, medicoId);
                            comando.setInt(2, idSenha);

                            if (comando.executeUpdate() == 0) {
                                throw new SQLException(
                                        "Essa senha já foi chamada por outro médico."
                                );
                            }
                        }

                        try (PreparedStatement comando =
                                     conexao.prepareStatement(
                                             sqlAtendimento,
                                             Statement.RETURN_GENERATED_KEYS)) {

                            comando.setInt(1, idSenha);
                            comando.setInt(2, medicoId);
                            comando.executeUpdate();

                            try (ResultSet resultado =
                                         comando.getGeneratedKeys()) {

                                if (resultado.next()) {
                                    atendimentoId = resultado.getInt(1);
                                } else {
                                    throw new SQLException(
                                            "Não foi possível registrar o atendimento."
                                    );
                                }
                            }
                        }

                        conexao.commit();

                    } catch (SQLException e) {
                        conexao.rollback();
                        throw e;

                    } finally {
                        conexao.setAutoCommit(true);
                    }
                }

                senhaIdEmAtendimento = idSenha;
                senhaEmAtendimento = proxima;
                nomePacienteEmAtendimento = nomePaciente;

                pacienteChamado.setText(
                        "Em atendimento:\n"
                                + "Senha: " + proxima.getSenhaFormatada()
                                + "\nPaciente: " + nomePaciente
                                + "\nConsultório: " + medico.getConsultorio()
                );

                finalizar.setDisable(false);
                mensagem.setText("Paciente chamado com sucesso!");

                atualizarFila(medico, listaFila);
                carregarHistorico(historico);

            } catch (SQLException e) {
                mensagem.setText(
                        "Erro ao chamar paciente: " + e.getMessage()
                );
                e.printStackTrace();
            }
        });

        finalizar.setOnAction(event -> {

            if (senhaEmAtendimento == null) {
                mensagem.setText("Nenhum atendimento em andamento.");
                return;
            }

            String sqlAtendimento = """
                    UPDATE atendimentos
                    SET data_fim = CURRENT_TIMESTAMP,
                        status = 'FINALIZADO'
                    WHERE id = ? AND status = 'EM_ATENDIMENTO'
                    """;

            String sqlSenha = """
                    UPDATE senhas
                    SET status = 'ATENDIDA'
                    WHERE id = ? AND status = 'EM_ATENDIMENTO'
                    """;

            try (Connection conexao = ConexaoMySQL.conectar()) {

                conexao.setAutoCommit(false);

                try {
                    try (PreparedStatement comando =
                                 conexao.prepareStatement(sqlAtendimento)) {

                        comando.setInt(1, atendimentoId);

                        if (comando.executeUpdate() == 0) {
                            throw new SQLException(
                                    "Atendimento não encontrado ou já finalizado."
                            );
                        }
                    }

                    try (PreparedStatement comando =
                                 conexao.prepareStatement(sqlSenha)) {

                        comando.setInt(1, senhaIdEmAtendimento);

                        if (comando.executeUpdate() == 0) {
                            throw new SQLException(
                                    "Não foi possível atualizar o status da senha."
                            );
                        }
                    }

                    conexao.commit();

                } catch (SQLException e) {
                    conexao.rollback();
                    throw e;

                } finally {
                    conexao.setAutoCommit(true);
                }

                pacienteChamado.setText(
                        "Último atendimento finalizado:\n"
                                + senhaEmAtendimento.getSenhaFormatada()
                                + " - " + nomePacienteEmAtendimento
                );

                senhaEmAtendimento = null;
                nomePacienteEmAtendimento = null;
                senhaIdEmAtendimento = 0;
                atendimentoId = 0;

                finalizar.setDisable(true);
                mensagem.setText("Atendimento finalizado com sucesso!");

                atualizarFila(medico, listaFila);
                carregarHistorico(historico);

            } catch (SQLException e) {
                mensagem.setText(
                        "Erro ao finalizar atendimento: " + e.getMessage()
                );
                e.printStackTrace();
            }
        });

        sair.setOnAction(event -> new LoginView().start(stage));

        VBox layout = new VBox(
                12,
                titulo,
                dadosMedico,
                new Label("Fila da sua especialidade:"),
                listaFila,
                chamar,
                finalizar,
                atualizar,
                new Label("Atendimento atual:"),
                pacienteChamado,
                new Label("Histórico de atendimentos:"),
                historico,
                mensagem,
                sair
        );

        layout.setPadding(new Insets(20));

        stage.setTitle("Área do Médico - " + medico.getNome());
        stage.setScene(new Scene(layout, 650, 750));
        stage.show();
    }

    public void mostrar(Stage stage) {
        new LoginView().start(stage);
    }

    private int buscarIdMedico(Medico medico) throws SQLException {

        String sql = "SELECT id FROM medicos WHERE usuario = ?";

        try (Connection conexao = ConexaoMySQL.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, medico.getUsuario());

            try (ResultSet resultado = comando.executeQuery()) {
                if (resultado.next()) {
                    return resultado.getInt("id");
                }
            }
        }

        throw new SQLException("Médico não encontrado no banco de dados.");
    }

    private void recuperarAtendimento(
            Label pacienteChamado,
            Button finalizar
    ) throws SQLException {

        String sql = """
                SELECT
                    a.id AS atendimento_id,
                    s.id AS senha_id,
                    s.numero,
                    s.tipo,
                    p.nome,
                    p.idade,
                    p.possui_deficiencia,
                    p.gestante,
                    p.possui_obesidade,
                    p.crianca_de_colo
                FROM atendimentos a
                INNER JOIN senhas s ON s.id = a.senha_id
                INNER JOIN pacientes p ON p.id = s.paciente_id
                WHERE a.medico_id = ?
                  AND a.status = 'EM_ATENDIMENTO'
                ORDER BY a.id DESC
                LIMIT 1
                """;

        try (Connection conexao = ConexaoMySQL.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, medicoId);

            try (ResultSet resultado = comando.executeQuery()) {

                if (resultado.next()) {

                    atendimentoId =
                            resultado.getInt("atendimento_id");

                    senhaIdEmAtendimento =
                            resultado.getInt("senha_id");

                    nomePacienteEmAtendimento =
                            resultado.getString("nome");

                    Paciente paciente = new Paciente(
                            nomePacienteEmAtendimento,
                            resultado.getInt("idade"),
                            resultado.getBoolean("possui_deficiencia"),
                            resultado.getBoolean("gestante"),
                            resultado.getBoolean("possui_obesidade"),
                            resultado.getBoolean("crianca_de_colo")
                    );

                    senhaEmAtendimento = new Senha(
                            resultado.getInt("numero"),
                            resultado.getString("tipo")
                    );

                    senhaEmAtendimento.setPaciente(paciente);

                    pacienteChamado.setText(
                            "Atendimento recuperado:\n"
                                    + "Senha: "
                                    + senhaEmAtendimento.getSenhaFormatada()
                                    + "\nPaciente: "
                                    + nomePacienteEmAtendimento
                    );

                    finalizar.setDisable(false);
                }
            }
        }
    }

    private List<Senha> consultarFila(Medico medico)
            throws SQLException {

        idsSenhas.clear();

        List<Senha> fila = new ArrayList<>();

        String sql = """
                SELECT
                    s.id AS senha_id,
                    s.numero,
                    s.tipo,
                    p.nome,
                    p.idade,
                    p.possui_deficiencia,
                    p.gestante,
                    p.possui_obesidade,
                    p.crianca_de_colo
                FROM senhas s
                INNER JOIN pacientes p ON p.id = s.paciente_id
                WHERE s.especialidade = ?
                  AND s.status = 'AGUARDANDO'
                ORDER BY
                    CASE s.prioridade
                        WHEN '80+' THEN 0
                        WHEN '60+' THEN 1
                        WHEN 'PCD' THEN 2
                        WHEN 'GESTANTE' THEN 3
                        WHEN 'OBESIDADE' THEN 4
                        WHEN 'CRIANCA_DE_COLO' THEN 5
                        ELSE 6
                    END,
                    s.numero
                """;

        try (Connection conexao = ConexaoMySQL.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, medico.getEspecialidade());

            try (ResultSet resultado = comando.executeQuery()) {

                while (resultado.next()) {

                    Paciente paciente = new Paciente(
                            resultado.getString("nome"),
                            resultado.getInt("idade"),
                            resultado.getBoolean("possui_deficiencia"),
                            resultado.getBoolean("gestante"),
                            resultado.getBoolean("possui_obesidade"),
                            resultado.getBoolean("crianca_de_colo")
                    );

                    Senha senha = new Senha(
                            resultado.getInt("numero"),
                            resultado.getString("tipo")
                    );

                    senha.setPaciente(paciente);
                    fila.add(senha);

                    idsSenhas.put(
                            senha,
                            resultado.getInt("senha_id")
                    );
                }
            }
        }

        return fila;
    }

    private Senha buscarProximaSenha(Medico medico)
            throws SQLException {

        List<Senha> fila = consultarFila(medico);

        if (fila.isEmpty()) {
            return null;
        }

        return fila.get(0);
    }

    private void atualizarFila(
            Medico medico,
            ListView<String> lista
    ) throws SQLException {

        lista.getItems().clear();

        List<Senha> fila = consultarFila(medico);

        for (Senha senha : fila) {

            Paciente paciente = senha.getPaciente();

            String prioridade;

            if (paciente.getIdade() >= 80) {
                prioridade = "IDOSO 80+";
            } else if (paciente.getIdade() >= 60) {
                prioridade = "IDOSO";
            } else if (paciente.isPossuiDeficiencia()) {
                prioridade = "PCD";
            } else if (paciente.isGestante()) {
                prioridade = "GESTANTE";
            } else if (paciente.isPossuiObesidade()) {
                prioridade = "OBESIDADE";
            } else if (paciente.isCriancaDeColo()) {
                prioridade = "CRIANÇA DE COLO";
            } else {
                prioridade = "NORMAL";
            }

            lista.getItems().add(
                    senha.getSenhaFormatada()
                            + " - " + paciente.getNome()
                            + " - " + prioridade
            );
        }

        if (fila.isEmpty()) {
            lista.getItems().add("Nenhum paciente aguardando.");
        }
    }

    private void carregarHistorico(
            ListView<String> historico
    ) throws SQLException {

        historico.getItems().clear();

        String sql = """
                SELECT
                    s.numero,
                    s.tipo,
                    p.nome,
                    m.nome AS nome_medico,
                    a.data_inicio,
                    a.data_fim,
                    a.status
                FROM atendimentos a
                INNER JOIN senhas s ON s.id = a.senha_id
                INNER JOIN pacientes p ON p.id = s.paciente_id
                INNER JOIN medicos m ON m.id = a.medico_id
                WHERE a.medico_id = ?
                ORDER BY a.id DESC
                """;

        try (Connection conexao = ConexaoMySQL.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, medicoId);

            try (ResultSet resultado = comando.executeQuery()) {

                while (resultado.next()) {

                    String registro =
                            resultado.getString("tipo")
                                    + String.format(
                                    "%03d",
                                    resultado.getInt("numero")
                            )
                                    + " - "
                                    + resultado.getString("nome")
                                    + " - "
                                    + resultado.getString("status")
                                    + " - Início: "
                                    + resultado.getString("data_inicio")
                                    + " - Fim: "
                                    + resultado.getString("data_fim");

                    historico.getItems().add(registro);
                }
            }
        }
    }
}
