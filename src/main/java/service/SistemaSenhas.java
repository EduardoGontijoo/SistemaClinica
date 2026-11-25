
package service;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import model.Paciente;
import model.Senha;

public class SistemaSenhas {

    private final FilaSenhas fila = new FilaSenhas();

    public void definirProximaSenha(int numero) {
        fila.definirProximaSenha(numero);
    }

    public Senha emitirSenhaNormal(Paciente paciente) {
        Objects.requireNonNull(paciente, "Paciente obrigatório.");

        Senha senha = fila.gerarSenhaNormal();
        senha.setPaciente(paciente);
        return senha;
    }

    public Senha emitirSenhaPrioritaria(Paciente paciente) {
        Objects.requireNonNull(paciente, "Paciente obrigatório.");

        Senha senha = fila.gerarSenhaPrioritaria();
        senha.setPaciente(paciente);
        return senha;
    }

    public Senha emitirSenha(Paciente paciente) {
        Objects.requireNonNull(paciente, "Paciente obrigatório.");

        if (paciente.temPrioridade()) {
            return emitirSenhaPrioritaria(paciente);
        }

        return emitirSenhaNormal(paciente);
    }

    public Senha chamarProximoPaciente() {
        return fila.chamarProximaSenha();
    }

    public Senha chamarProximoPaciente(
            String especialidade,
            Map<Senha, String> dadosFila) {

        return fila.chamarProximaSenha(especialidade, dadosFila);
    }

    public int quantidadeNaFila() {
        return fila.quantidadeNaFila();
    }

    public ArrayList<Senha> consultarFila() {
        return fila.getSenhas();
    }

    public void recuperarSenha(
            Paciente paciente, int numero, String tipo) {

        Objects.requireNonNull(paciente, "Paciente obrigatório.");

        if (numero <= 0) {
            throw new IllegalArgumentException(
                    "O número da senha deve ser positivo.");
        }

        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException(
                    "O tipo da senha é obrigatório.");
        }

        Senha senha = new Senha(numero, tipo);
        senha.setPaciente(paciente);
        fila.adicionarSenha(senha);
    }
}
