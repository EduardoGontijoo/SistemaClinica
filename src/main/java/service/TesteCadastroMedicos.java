package service;

import model.CadastroMedicos;
import model.Medico;

public class TesteCadastroMedicos {

    public static void main(String[] args) {

        CadastroMedicos cadastro = new CadastroMedicos();

        cadastro.cadastrarMedico(new Medico("Carlos", "Cardiologia", 1));
        cadastro.cadastrarMedico(new Medico("Ana", "Pediatria", 2));
        cadastro.cadastrarMedico(new Medico("Pedro", "Cardiologia", 3));

        System.out.println("Médicos cadastrados:");

        for (Medico medico : cadastro.listarMedicos()) {
            System.out.println(medico.getNome() + " - "
                    + medico.getEspecialidade() + " - Consultório "
                    + medico.getConsultorio());
        }

        System.out.println("\nMédicos de Cardiologia:");

        for (Medico medico : cadastro.buscarPorEspecialidade("Cardiologia")) {
            System.out.println(medico.getNome());
        }

        System.out.println("\nMédico do consultório 2:");

        Medico medico = cadastro.buscarPorConsultorio(2);

        if (medico != null) {
            System.out.println(medico.getNome());
        } else {
            System.out.println("Nenhum médico encontrado.");
        }
    }
}

