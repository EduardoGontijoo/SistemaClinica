
package service;

import model.CadastroMedicos;
import model.Medico;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public class CadastroMedicosCompartilhado {

    private static final CadastroMedicos cadastro = new CadastroMedicos();
    private static boolean carregado = false;

    public static synchronized CadastroMedicos getInstancia() {
        if (!carregado) {
            carregarMedicos();
            carregado = true;
        }

        return cadastro;
    }

    private static void carregarMedicos() {
        Path arquivo = Path.of("dados_clinica", "medicos.txt");

        if (!Files.exists(arquivo)) {
            return;
        }

        try {
            for (String linha : Files.readAllLines(
                    arquivo, StandardCharsets.UTF_8)) {

                try {
                    String[] partes = linha.split("\\|", -1);

                    if (partes.length != 3) {
                        continue;
                    }

                    String nome = decodificar(partes[0]);
                    String especialidade = decodificar(partes[1]);
                    int consultorio = Integer.parseInt(partes[2]);

                    Medico medico = new Medico(
                            nome, especialidade, consultorio);

                    cadastro.cadastrarMedico(medico);

                } catch (IllegalArgumentException e) {
                    System.err.println(
                            "Médico ignorado por dados inválidos: " + linha);
                }
            }
        } catch (IOException e) {
            System.err.println(
                    "Erro ao carregar médicos: " + e.getMessage());
        }
    }

    private static String decodificar(String texto) {
        return new String(
                Base64.getDecoder().decode(texto),
                StandardCharsets.UTF_8);
    }
}
