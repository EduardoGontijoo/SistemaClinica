package service;

import model.Paciente;
import model.Senha;

public class TesteSistema {
    public static void main(String[] args) {
        SistemaSenhas sistema = new SistemaSenhas();

        Paciente paciente1 = new Paciente("João", 30, false, false, false, false);
        Paciente paciente2 = new Paciente("Maria", 75, false, false, false, false);
        Paciente paciente3 = new Paciente("Ana", 25, false, true, false, false);


        System.out.println(paciente1.getNome() + " tem prioridade? " + paciente1.temPrioridade());
        System.out.println(paciente2.getNome() + " tem prioridade? " + paciente2.temPrioridade());
        System.out.println(paciente3.getNome() + " tem prioridade? " + paciente3.temPrioridade());

        Senha senha1 = sistema.emitirSenha(paciente1);
        Senha senha2 = sistema.emitirSenha(paciente2);
        Senha senha3 = sistema.emitirSenha(paciente3);

        System.out.println(senha1.getSenhaFormatada());
        System.out.println(senha2.getSenhaFormatada());
        System.out.println(senha3.getSenhaFormatada());

        System.out.println("Pessoas na fila: " + sistema.quantidadeNaFila());

        Senha chamada = sistema.chamarProximoPaciente();

        System.out.println("Senha chamada: " + chamada.getSenhaFormatada());
        System.out.println("Paciente: " + chamada.getPaciente().getNome());

        System.out.println("Pessoas restantes: " + sistema.quantidadeNaFila());
    }
}
