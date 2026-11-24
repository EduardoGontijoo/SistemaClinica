
package service;

import java.util.ArrayList;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import model.Paciente;
import model.Senha;

public class FilaSenhas {

    private final ArrayList<Senha> senhas = new ArrayList<>();

    private int proximaSenhaNormal = 1;
    private int proximaSenhaPrioritaria = 1;

    public void adicionarSenha(Senha senha) {
        if (senha == null) {
            throw new IllegalArgumentException("Senha inválida.");
        }

        if (!"N".equals(senha.getTipo())
                && !"P".equals(senha.getTipo())) {
            throw new IllegalArgumentException("Tipo de senha inválido.");
        }

        for (Senha existente : senhas) {
            if (existente == senha) {
                return;
            }
        }

        senhas.add(senha);

        int numero = senha.getNumero();

        if ("N".equals(senha.getTipo())
                && numero >= proximaSenhaNormal) {
            proximaSenhaNormal = numero + 1;
        }

        if ("P".equals(senha.getTipo())
                && numero >= proximaSenhaPrioritaria) {
            proximaSenhaPrioritaria = numero + 1;
        }
    }

    public Senha gerarSenhaNormal() {
        Senha senha = new Senha(proximaSenhaNormal, "N");
        adicionarSenha(senha);
        return senha;
    }

    public Senha gerarSenhaPrioritaria() {
        Senha senha = new Senha(proximaSenhaPrioritaria, "P");
        adicionarSenha(senha);
        return senha;
    }

    public int quantidadeNaFila() {
        return senhas.size();
    }

    public ArrayList<Senha> getSenhas() {
        return new ArrayList<>(senhas);
    }

    public Senha chamarProximaSenha() {
        Senha proxima = null;

        for (Senha senha : senhas) {
            if (proxima == null
                    || obterPrioridade(senha) < obterPrioridade(proxima)) {
                proxima = senha;
            }
        }

        if (proxima != null) {
            senhas.remove(proxima);
        }

        return proxima;
    }

    public Senha chamarProximaSenha(
            String especialidade,
            Map<Senha, String> dadosFila) {

        if (especialidade == null || dadosFila == null) {
            return null;
        }

        Senha proxima = null;

        for (Senha senha : senhas) {
            String registro = dadosFila.get(senha);

            if (registro == null) {
                continue;
            }

            String[] partes = registro.split("\\|", -1);
            String especialidadeSenha;

            try {
                if (partes.length == 9) {
                    especialidadeSenha = decodificar(partes[6]);
                } else if (partes.length == 7) {
                    especialidadeSenha = decodificar(partes[4]);
                } else {
                    continue;
                }
            } catch (IllegalArgumentException e) {
                continue;
            }

            if (!especialidade.equalsIgnoreCase(especialidadeSenha)) {
                continue;
            }

            if (proxima == null
                    || obterPrioridade(senha) < obterPrioridade(proxima)) {
                proxima = senha;
            }
        }

        if (proxima != null) {
            senhas.remove(proxima);
        }

        return proxima;
    }

    private int obterPrioridade(Senha senha) {
        Paciente paciente = senha.getPaciente();

        if (paciente == null) {
            return "P".equals(senha.getTipo()) ? 1 : 6;
        }

        if (paciente.getIdade() >= 80) return 0;
        if (paciente.getIdade() >= 60) return 1;
        if (paciente.isPossuiDeficiencia()) return 2;
        if (paciente.isGestante()) return 3;
        if (paciente.isPossuiObesidade()) return 4;
        if (paciente.isCriancaDeColo()) return 5;

        return 6;
    }

    private String decodificar(String texto) {
        return new String(
                Base64.getDecoder().decode(texto),
                StandardCharsets.UTF_8
        );
    }
}
