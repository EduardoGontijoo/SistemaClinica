package model.model;

import model.Medico;

import java.util.ArrayList;
import java.util.List;

public class CadastroMedicos {
    private final List<model.Medico> medicos = new ArrayList<>();

    private final String[] especialidades = {
            "Clínica Geral", "Pediatria", "Ginecologia",
            "Cardiologia", "Psiquiatria"
    };

    public void cadastrarMedico(model.Medico medico) {
        if (medico == null) throw new IllegalArgumentException("Médico inválido.");
        if (medico.getNome() == null || medico.getNome().isBlank())
            throw new IllegalArgumentException("O nome é obrigatório.");
        if (medico.getConsultorio() <= 0)
            throw new IllegalArgumentException("O consultório deve ser positivo.");
        if (medico.getEspecialidade() == null || medico.getEspecialidade().isBlank())
            throw new IllegalArgumentException("Selecione uma especialidade.");
        if (medico.getUsuario() == null || medico.getUsuario().isBlank())
            throw new IllegalArgumentException("O usuário é obrigatório.");
        if (medico.getSenha() == null || medico.getSenha().isBlank())
            throw new IllegalArgumentException("A senha é obrigatória.");

        String especialidadeValida = null;
        for (String especialidade : especialidades) {
            if (especialidade.equalsIgnoreCase(medico.getEspecialidade().trim())) {
                especialidadeValida = especialidade;
                break;
            }
        }
        if (especialidadeValida == null)
            throw new IllegalArgumentException("Especialidade não permitida.");
        if (buscarPorConsultorio(medico.getConsultorio()) != null)
            throw new IllegalArgumentException("Consultório já cadastrado.");
        if (buscarPorEspecialidade(especialidadeValida).size() >= 3)
            throw new IllegalArgumentException("Essa especialidade já possui 3 médicos.");
        if (buscarPorUsuario(medico.getUsuario()) != null)
            throw new IllegalArgumentException("Esse usuário já está cadastrado.");

        medicos.add(new model.Medico(medico.getNome().trim(), especialidadeValida,
                medico.getConsultorio(), medico.getUsuario().trim(), medico.getSenha()));
    }

    // Usado apenas pela persistência para saber se já existem dados carregados.
    public boolean estaVazio() { return medicos.isEmpty(); }

    public List<model.Medico> listarMedicos() { return new ArrayList<>(medicos); }

    public List<model.Medico> buscarPorEspecialidade(String especialidade) {
        List<model.Medico> encontrados = new ArrayList<>();
        if (especialidade == null) return encontrados;
        for (model.Medico medico : medicos) {
            if (medico.getEspecialidade().equalsIgnoreCase(especialidade.trim()))
                encontrados.add(medico);
        }
        return encontrados;
    }

    public model.Medico buscarPorConsultorio(int consultorio) {
        for (model.Medico medico : medicos)
            if (medico.getConsultorio() == consultorio) return medico;
        return null;
    }

    public model.Medico buscarPorUsuario(String usuario) {
        if (usuario == null) return null;
        for (model.Medico medico : medicos)
            if (medico.getUsuario().equalsIgnoreCase(usuario.trim())) return medico;
        return null;
    }

    public model.Medico autenticar(String usuario, String senha) {
        Medico medico = buscarPorUsuario(usuario);
        return medico != null && medico.getSenha().equals(senha) ? medico : null;
    }
}
