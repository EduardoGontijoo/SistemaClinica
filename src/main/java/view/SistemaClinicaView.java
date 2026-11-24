
package view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import model.CadastroMedicos;
import model.Medico;
import model.Paciente;
import model.Senha;

import service.SistemaSenhas;
import service.SistemaSenhasCompartilhado;
import service.CadastroMedicosCompartilhado;
import service.HistoricoCompartilhado;
import service.MedicosPersistencia;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.sql.SQLException;

public class SistemaClinicaView extends Application {

    private final Path pasta = Path.of("dados_clinica");
    private final Path arquivoFila = pasta.resolve("fila.txt");

    private final CadastroMedicos cadastro =
            CadastroMedicosCompartilhado.getInstancia();

    private final SistemaSenhas sistema =
            SistemaSenhasCompartilhado.getInstancia();

    private final Map<Senha, String> dadosFila =
            SistemaSenhasCompartilhado.getDadosFila();

    private final List<String> atendimentos = new ArrayList<>();

    private final Map<String, Integer> indiceMedicoPorEspecialidade =
            new LinkedHashMap<>();

    private final String[] especialidades = {
            "Clínica Geral",
            "Pediatria",
            "Ginecologia",
            "Cardiologia",
            "Psiquiatria"
    };

    private final int minutosPorPaciente = 30;

    private Label estimativasEspera;

    @Override
    public void start(Stage stage) {

        Label titulo = new Label("Sistema da Clínica");

        TextField nomeMedico = new TextField();
        nomeMedico.setPromptText("Nome do médico");

        ComboBox<String> especialidadeMedico = new ComboBox<>();
        especialidadeMedico.getItems().addAll(especialidades);
        especialidadeMedico.setPromptText("Especialidade");

        TextField consultorio = new TextField();
        consultorio.setPromptText("Número do consultório");

        TextField usuarioMedico = new TextField();
        usuarioMedico.setPromptText("Usuário de acesso do médico");

        PasswordField senhaMedico = new PasswordField();
        senhaMedico.setPromptText("Senha de acesso do médico");

        Button cadastrarMedico = new Button("Cadastrar médico");
        Label resultadoMedico = new Label();
        ListView<String> listaMedicos = new ListView<>();

        TextField nomePaciente = new TextField();
        nomePaciente.setPromptText("Nome do paciente");

        TextField idadePaciente = new TextField();
        idadePaciente.setPromptText("Idade do paciente");

        ComboBox<String> especialidadePaciente = new ComboBox<>();
        especialidadePaciente.getItems().addAll(especialidades);
        especialidadePaciente.setPromptText("Especialidade desejada");

        CheckBox deficiencia = new CheckBox("Pessoa com deficiência");
        CheckBox gestante = new CheckBox("Gestante");
        CheckBox obesidade = new CheckBox("Pessoa com obesidade");
        CheckBox criancaDeColo = new CheckBox("Criança de colo");

        Button emitirSenha = new Button("Cadastrar e emitir senha");
        Button chamarProximo = new Button("Chamar próximo paciente");
        Button atualizar = new Button("Atualizar fila");
        Button abrirPainel = new Button("Abrir painel de chamadas");
        Button abrirEstatisticas =
                new Button("Abrir estatísticas");

        abrirEstatisticas.setOnAction(event ->
                new EstatisticasView().mostrar(new Stage())
        );

        Label resultadoPaciente = new Label();
        Label pacienteChamado =
                new Label("Nenhum paciente chamado.");
        Label quantidade = new Label("Pacientes na fila: 0");

        estimativasEspera =
                new Label("Estimativa de espera por especialidade:");

        ListView<String> listaFila = new ListView<>();
        ListView<String> historico = new ListView<>();

        abrirPainel.setOnAction(event -> {
            Stage janelaPainel = new Stage();
            PainelChamadasView painel = new PainelChamadasView();
            painel.mostrar(janelaPainel);
        });

        try {
            MedicosPersistencia.carregar(cadastro);
            carregarFila();

            atendimentos.addAll(HistoricoCompartilhado.listar());

            atualizarListaMedicos(listaMedicos);
            atualizarFila(listaFila, quantidade);
            historico.getItems().setAll(atendimentos);

        } catch (IOException | SQLException | IllegalArgumentException e) {
            resultadoPaciente.setText(
                    "Erro ao carregar os dados: " + e.getMessage()
            );
        }

        // CADASTRAR MÉDICO
        cadastrarMedico.setOnAction(event -> {

            String nome = nomeMedico.getText().trim();
            String especialidade = especialidadeMedico.getValue();
            String usuario = usuarioMedico.getText().trim();
            String senhaAcesso = senhaMedico.getText();

            if (nome.isBlank()
                    || especialidade == null
                    || consultorio.getText().isBlank()
                    || usuario.isBlank()
                    || senhaAcesso.isBlank()) {

                resultadoMedico.setText("Preencha todos os campos!");
                return;
            }

            try {
                int numero = Integer.parseInt(
                        consultorio.getText().trim()
                );

                if (numero <= 0) {
                    resultadoMedico.setText("Consultório inválido!");
                    return;
                }

                if (cadastro.buscarPorConsultorio(numero) != null) {
                    resultadoMedico.setText(
                            "Consultório já cadastrado!"
                    );
                    return;
                }

                if (cadastro.buscarPorEspecialidade(especialidade).size() >= 3) {
                    resultadoMedico.setText(
                            "Essa especialidade já possui 3 médicos!"
                    );
                    return;
                }

                Medico medico = new Medico(
                        nome,
                        especialidade,
                        numero,
                        usuario,
                        senhaAcesso
                );

                if (cadastro.buscarPorUsuario(usuario) != null) {
                    resultadoMedico.setText(
                            "Esse usuário já está cadastrado!"
                    );
                    return;
                }

                MedicosPersistencia.salvar(medico);

                cadastro.cadastrarMedico(medico);

                atualizarListaMedicos(listaMedicos);
                atualizarListaMedicos(listaMedicos);

                resultadoMedico.setText("Médico cadastrado!");

                nomeMedico.clear();
                consultorio.clear();
                usuarioMedico.clear();
                senhaMedico.clear();
                especialidadeMedico.setValue(null);

            } catch (NumberFormatException e) {
                resultadoMedico.setText(
                        "Digite um número válido para o consultório!"
                );

            } catch (IllegalArgumentException e) {
                resultadoMedico.setText(e.getMessage());

            } catch (SQLException e) {
                resultadoMedico.setText(
                        "Erro ao salvar no MySQL: " + e.getMessage()
                );
            }
        });

        // CADASTRAR PACIENTE E EMITIR SENHA
        emitirSenha.setOnAction(event -> {

            String nome = nomePaciente.getText().trim();
            String especialidade = especialidadePaciente.getValue();

            if (nome.isBlank()
                    || idadePaciente.getText().isBlank()
                    || especialidade == null) {

                resultadoPaciente.setText(
                        "Preencha os campos obrigatórios!"
                );
                return;
            }

            try {
                int idade = Integer.parseInt(
                        idadePaciente.getText().trim()
                );

                if (idade < 0 || idade > 120) {
                    resultadoPaciente.setText("Idade inválida!");
                    return;
                }

                if (cadastro.buscarPorEspecialidade(especialidade).isEmpty()) {
                    resultadoPaciente.setText(
                            "Cadastre um médico dessa especialidade primeiro!"
                    );
                    return;
                }

                Paciente paciente = new Paciente(
                        nome,
                        idade,
                        deficiencia.isSelected(),
                        gestante.isSelected(),
                        obesidade.isSelected(),
                        criancaDeColo.isSelected()
                );

                Senha senha = sistema.emitirSenha(paciente);

                String registro = codificar(nome) + "|"
                        + idade + "|"
                        + deficiencia.isSelected() + "|"
                        + gestante.isSelected() + "|"
                        + obesidade.isSelected() + "|"
                        + criancaDeColo.isSelected() + "|"
                        + codificar(especialidade) + "|"
                        + senha.getNumero() + "|"
                        + senha.getTipo();

                dadosFila.put(senha, registro);

                salvarFila();
                atualizarFila(listaFila, quantidade);

                resultadoPaciente.setText(
                        "Senha emitida: " + senha.getSenhaFormatada()
                                + "\nEspecialidade: " + especialidade
                );

                nomePaciente.clear();
                idadePaciente.clear();
                especialidadePaciente.setValue(null);
                deficiencia.setSelected(false);
                gestante.setSelected(false);
                obesidade.setSelected(false);
                criancaDeColo.setSelected(false);

            } catch (NumberFormatException e) {
                resultadoPaciente.setText("Digite uma idade válida!");

            } catch (IOException e) {
                resultadoPaciente.setText("Erro ao salvar a fila.");
            }
        });

        // CHAMAR PRÓXIMO PACIENTE
        chamarProximo.setOnAction(event -> {

            Senha senha = sistema.chamarProximoPaciente();

            if (senha == null) {
                resultadoPaciente.setText("Não há pacientes na fila!");
                return;
            }

            String registro = dadosFila.remove(senha);
            String nome = "Paciente";
            String especialidade = "";

            if (registro != null) {
                String[] partes = registro.split("\\|", -1);

                if (partes.length > 0) {
                    nome = decodificar(partes[0]);
                }

                if (partes.length == 9) {
                    especialidade = decodificar(partes[6]);
                } else if (partes.length == 7) {
                    especialidade = decodificar(partes[4]);
                }
            }

            String mensagem = "PACIENTE CHAMADO - Senha "
                    + senha.getSenhaFormatada()
                    + " - " + nome;

            if (!especialidade.isBlank()) {
                List<Medico> medicos =
                        cadastro.buscarPorEspecialidade(especialidade);

                if (!medicos.isEmpty()) {
                    int indice = indiceMedicoPorEspecialidade.getOrDefault(
                            especialidade, 0
                    );

                    indice = indice % medicos.size();
                    Medico medico = medicos.get(indice);

                    mensagem += " - Dr(a). " + medico.getNome()
                            + " - Consultório " + medico.getConsultorio();

                    indiceMedicoPorEspecialidade.put(
                            especialidade,
                            (indice + 1) % medicos.size()
                    );
                }
            }

            pacienteChamado.setText(mensagem);
            resultadoPaciente.setText("Paciente chamado!");

            try {
                salvarFila();
                HistoricoCompartilhado.adicionar(mensagem);

                atendimentos.clear();
                atendimentos.addAll(HistoricoCompartilhado.listar());
                historico.getItems().setAll(atendimentos);

                atualizarFila(listaFila, quantidade);

            } catch (IOException e) {
                resultadoPaciente.setText(
                        "Paciente chamado, mas houve erro ao salvar."
                );
            }
        });

        // ATUALIZAR FILA E HISTÓRICO
        atualizar.setOnAction(event -> {
            atualizarFila(listaFila, quantidade);

            try {
                atendimentos.clear();
                atendimentos.addAll(HistoricoCompartilhado.listar());
                historico.getItems().setAll(atendimentos);

            } catch (IOException e) {
                resultadoPaciente.setText(
                        "Erro ao atualizar o histórico."
                );
            }
        });

        // MONTAR TELA
        VBox layout = new VBox(
                10,
                titulo,

                new Label("Cadastro de Médicos"),
                nomeMedico,
                especialidadeMedico,
                consultorio,
                usuarioMedico,
                senhaMedico,
                cadastrarMedico,
                resultadoMedico,
                new Label("Médicos cadastrados:"),
                listaMedicos,

                new Label("Cadastro de Pacientes"),
                nomePaciente,
                idadePaciente,
                especialidadePaciente,
                deficiencia,
                gestante,
                obesidade,
                criancaDeColo,
                emitirSenha,
                resultadoPaciente,

                new Label("Último paciente chamado:"),
                pacienteChamado,
                abrirPainel,
                abrirEstatisticas,

                new Label("Fila de atendimento:"),
                quantidade,
                listaFila,
                chamarProximo,
                atualizar,

                estimativasEspera,

                new Label("Histórico de atendimentos:"),
                historico
        );

        layout.setStyle("-fx-padding: 20;");

        ScrollPane scroll = new ScrollPane(layout);
        scroll.setFitToWidth(true);

        stage.setTitle("Sistema da Clínica");
        stage.setScene(new Scene(scroll, 600, 850));
        stage.setMaximized(true);
        stage.show();
    }

    // CARREGAR FILA SALVA
    private void carregarFila() throws IOException {

        Files.createDirectories(pasta);

        if (!sistema.consultarFila().isEmpty()
                || !Files.exists(arquivoFila)) {
            return;
        }

        for (String linha :
                Files.readAllLines(arquivoFila, StandardCharsets.UTF_8)) {

            String[] partes = linha.split("\\|", -1);

            if (partes.length != 9 && partes.length != 7) {
                continue;
            }

            String nome = decodificar(partes[0]);
            int idade = Integer.parseInt(partes[1]);
            boolean deficiencia = Boolean.parseBoolean(partes[2]);
            boolean gestante = Boolean.parseBoolean(partes[3]);

            boolean obesidade = partes.length == 9
                    && Boolean.parseBoolean(partes[4]);

            boolean criancaDeColo = partes.length == 9
                    && Boolean.parseBoolean(partes[5]);

            String especialidade = decodificar(
                    partes.length == 9 ? partes[6] : partes[4]
            );

            int numero = Integer.parseInt(
                    partes.length == 9 ? partes[7] : partes[5]
            );

            String tipo = partes.length == 9 ? partes[8] : partes[6];

            Paciente paciente = new Paciente(
                    nome,
                    idade,
                    deficiencia,
                    gestante,
                    obesidade,
                    criancaDeColo
            );

            sistema.recuperarSenha(paciente, numero, tipo);

            List<Senha> senhas = sistema.consultarFila();
            Senha senha = senhas.get(senhas.size() - 1);

            String registro = codificar(nome) + "|"
                    + idade + "|"
                    + deficiencia + "|"
                    + gestante + "|"
                    + obesidade + "|"
                    + criancaDeColo + "|"
                    + codificar(especialidade) + "|"
                    + numero + "|"
                    + tipo;

            dadosFila.put(senha, registro);
        }
    }

    // SALVAR FILA
    private void salvarFila() throws IOException {

        Files.createDirectories(pasta);

        List<String> linhas = new ArrayList<>();

        for (Map.Entry<Senha, String> entrada : dadosFila.entrySet()) {
            Senha senha = entrada.getKey();
            String[] partes = entrada.getValue().split("\\|", -1);

            if (partes.length == 9) {
                linhas.add(
                        partes[0] + "|"
                                + partes[1] + "|"
                                + partes[2] + "|"
                                + partes[3] + "|"
                                + partes[4] + "|"
                                + partes[5] + "|"
                                + partes[6] + "|"
                                + senha.getNumero() + "|"
                                + senha.getTipo()
                );
            }
        }

        Files.write(arquivoFila, linhas, StandardCharsets.UTF_8);
    }

    // ATUALIZAR LISTA DE MÉDICOS
    private void atualizarListaMedicos(ListView<String> lista) {

        lista.getItems().clear();

        for (Medico medico : cadastro.listarMedicos()) {
            lista.getItems().add(
                    medico.getNome()
                            + " - " + medico.getEspecialidade()
                            + " - Consultório " + medico.getConsultorio()
                            + " - Usuário: " + medico.getUsuario()
            );
        }
    }

    // ATUALIZAR FILA E ESTIMATIVA DE ESPERA
    private void atualizarFila(
            ListView<String> lista,
            Label quantidade
    ) {
        lista.getItems().clear();

        List<Senha> filaOrdenada =
                new ArrayList<>(sistema.consultarFila());

        filaOrdenada.sort(
                Comparator.comparingInt(this::obterNivelPrioridade)
        );

        for (Senha senha : filaOrdenada) {
            Paciente paciente = senha.getPaciente();

            if (paciente == null) {
                continue;
            }

            String prioridade;

            if (paciente.getIdade() >= 80) {
                prioridade = " - PRIORIDADE 80+";
            } else if (paciente.getIdade() >= 60) {
                prioridade = " - PRIORIDADE IDOSO";
            } else if (paciente.isPossuiDeficiencia()) {
                prioridade = " - PRIORIDADE PCD";
            } else if (paciente.isGestante()) {
                prioridade = " - PRIORIDADE GESTANTE";
            } else if (paciente.isPossuiObesidade()) {
                prioridade = " - PRIORIDADE OBESIDADE";
            } else if (paciente.isCriancaDeColo()) {
                prioridade = " - PRIORIDADE CRIANÇA DE COLO";
            } else {
                prioridade = " - NORMAL";
            }

            String especialidade = obterEspecialidade(senha);

            lista.getItems().add(
                    senha.getSenhaFormatada()
                            + " - " + paciente.getNome()
                            + " - " + paciente.getIdade() + " anos"
                            + (especialidade.isBlank()
                            ? "" : " - " + especialidade)
                            + prioridade
            );
        }

        quantidade.setText(
                "Pacientes na fila: " + sistema.quantidadeNaFila()
        );

        atualizarEstimativasEspera();
    }

    private String obterEspecialidade(Senha senha) {

        String registro = dadosFila.get(senha);

        if (registro == null) {
            return "";
        }

        String[] partes = registro.split("\\|", -1);

        if (partes.length == 9) {
            return decodificar(partes[6]);
        }

        if (partes.length == 7) {
            return decodificar(partes[4]);
        }

        return "";
    }

    private void atualizarEstimativasEspera() {

        Map<String, Integer> quantidadePorEspecialidade =
                new LinkedHashMap<>();

        for (String especialidade : especialidades) {
            quantidadePorEspecialidade.put(especialidade, 0);
        }

        for (Senha senha : sistema.consultarFila()) {
            String especialidade = obterEspecialidade(senha);

            if (quantidadePorEspecialidade.containsKey(especialidade)) {
                quantidadePorEspecialidade.put(
                        especialidade,
                        quantidadePorEspecialidade.get(especialidade) + 1
                );
            }
        }

        StringBuilder texto = new StringBuilder(
                "Estimativa de espera por especialidade:\n"
        );

        for (String especialidade : especialidades) {
            int total = quantidadePorEspecialidade.get(especialidade);

            texto.append(especialidade)
                    .append(": ")
                    .append(total)
                    .append(" paciente(s) - ")
                    .append(total * minutosPorPaciente)
                    .append(" minuto(s)\n");
        }

        estimativasEspera.setText(texto.toString());
    }

    private int obterNivelPrioridade(Senha senha) {

        Paciente paciente = senha.getPaciente();

        if (paciente == null) {
            return senha.getTipo().equals("P") ? 1 : 6;
        }

        if (paciente.getIdade() >= 80) return 0;
        if (paciente.getIdade() >= 60) return 1;
        if (paciente.isPossuiDeficiencia()) return 2;
        if (paciente.isGestante()) return 3;
        if (paciente.isPossuiObesidade()) return 4;
        if (paciente.isCriancaDeColo()) return 5;

        return 6;
    }

    private String codificar(String texto) {
        return Base64.getEncoder().encodeToString(
                texto.getBytes(StandardCharsets.UTF_8)
        );
    }

    private String decodificar(String texto) {
        return new String(
                Base64.getDecoder().decode(texto),
                StandardCharsets.UTF_8
        );
    }

    public static void main(String[] args) {
        launch(args);
    }
}
