
package model;

import java.util.ArrayList;
import java.util.List;

public class CadastroMedicos {

    private final List<Medico> medicos = new ArrayList<>();

    private final String[] especialidades = {
            "Clínica Geral",
            "Pediatria",
            "Ginecologia",
            "Cardiologia",
            "Psiquiatria"
    };

    public void cadastrarMedico(Medico medico) {
        if (medico == null) {
            throw new IllegalArgumentException("Médico inválido.");
        }

        if (medico.getNome() == null || medico.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome é obrigatório.");
        }

        if (medico.getConsultorio() <= 0) {
            throw new IllegalArgumentException(
                    "O consultório deve ser um número positivo."
            );
        }

        if (medico.getEspecialidade() == null
                || medico.getEspecialidade().isBlank()) {
            throw new IllegalArgumentException("Selecione uma especialidade.");
        }

        if (medico.getUsuario() == null || medico.getUsuario().isBlank()) {
            throw new IllegalArgumentException("O usuário é obrigatório.");
        }

        if (medico.getSenha() == null || medico.getSenha().isBlank()) {
            throw new IllegalArgumentException("A senha é obrigatória.");
        }

        String especialidadeValida = null;

        for (String especialidade : especialidades) {
            if (especialidade.equalsIgnoreCase(
                    medico.getEspecialidade().trim())) {
                especialidadeValida = especialidade;
                break;
            }
        }

        if (especialidadeValida == null) {
            throw new IllegalArgumentException(
                    "Especialidade não permitida."
            );
        }

        if (buscarPorConsultorio(medico.getConsultorio()) != null) {
            throw new IllegalArgumentException(
                    "Consultório já cadastrado."
            );
        }

        if (buscarPorEspecialidade(especialidadeValida).size() >= 3) {
            throw new IllegalArgumentException(
                    "Essa especialidade já possui 3 médicos."
            );
        }

        if (buscarPorUsuario(medico.getUsuario()) != null) {
            throw new IllegalArgumentException(
                    "Esse nome de usuário já está cadastrado."
            );
        }

        medicos.add(new Medico(
                medico.getNome().trim(),
                especialidadeValida,
                medico.getConsultorio(),
                medico.getUsuario().trim(),
                medico.getSenha()
        ));
    }

    public List<Medico> listarMedicos() {
        return new ArrayList<>(medicos);
    }

    public List<Medico> buscarPorEspecialidade(String especialidade) {
        List<Medico> encontrados = new ArrayList<>();

        if (especialidade == null) {
            return encontrados;
        }

        for (Medico medico : medicos) {
            if (medico.getEspecialidade()
                    .equalsIgnoreCase(especialidade.trim())) {
                encontrados.add(medico);
            }
        }

        return encontrados;
    }

    public Medico buscarPorConsultorio(int consultorio) {
        for (Medico medico : medicos) {
            if (medico.getConsultorio() == consultorio) {
                return medico;
            }
        }

        return null;
    }

    public Medico buscarPorUsuario(String usuario) {
        if (usuario == null) {
            return null;
        }

        for (Medico medico : medicos) {
            if (medico.getUsuario().equalsIgnoreCase(usuario.trim())) {
                return medico;
            }
        }

        return null;
    }

    public Medico autenticar(String usuario, String senha) {
        Medico medico = buscarPorUsuario(usuario);

        if (medico != null && medico.getSenha().equals(senha)) {
            return medico;
        }

        return null;
    }
    public boolean estaVazio() {
        return medicos.isEmpty();
    }
}
