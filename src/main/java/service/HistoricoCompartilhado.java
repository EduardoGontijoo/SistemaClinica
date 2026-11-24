
package service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HistoricoCompartilhado {

    private static final Path PASTA =
            Path.of("dados_clinica");

    private static final Path ARQUIVO =
            PASTA.resolve("historico.txt");

    private static final List<String> atendimentos =
            new ArrayList<>();

    private static boolean carregado = false;

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static synchronized List<String> listar()
            throws IOException {

        carregar();
        return new ArrayList<>(atendimentos);
    }

    public static synchronized void adicionar(String atendimento)
            throws IOException {

        carregar();
        Files.createDirectories(PASTA);

        String dataHora = LocalDateTime.now().format(FORMATO);
        String registro = dataHora + " - " + atendimento;

        Files.write(
                ARQUIVO,
                List.of(registro),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );

        atendimentos.add(registro);
    }

    public static synchronized void salvar(List<String> registros)
            throws IOException {

        Files.createDirectories(PASTA);

        Files.write(
                ARQUIVO,
                registros,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );

        atendimentos.clear();
        atendimentos.addAll(registros);
        carregado = true;
    }

    private static void carregar() throws IOException {

        if (carregado) {
            return;
        }

        Files.createDirectories(PASTA);

        if (Files.exists(ARQUIVO)) {
            atendimentos.addAll(
                    Files.readAllLines(
                            ARQUIVO,
                            StandardCharsets.UTF_8
                    )
            );
        }

        carregado = true;
    }
}
