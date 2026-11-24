package model;

public class Paciente {

    private String nome;
    private int idade;
    private boolean possuiDeficiencia;
    private boolean gestante;
    private boolean possuiObesidade;
    private boolean criancaDeColo;

    public Paciente(
            String nome,
            int idade,
            boolean possuiDeficiencia,
            boolean gestante,
            boolean possuiObesidade,
            boolean criancaDeColo) {

        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException(
                    "O nome do paciente é obrigatório."
            );
        }

        if (idade < 0 || idade > 120) {
            throw new IllegalArgumentException(
                    "A idade deve estar entre 0 e 120 anos."
            );
        }

        this.nome = nome.trim();
        this.idade = idade;
        this.possuiDeficiencia = possuiDeficiencia;
        this.gestante = gestante;
        this.possuiObesidade = possuiObesidade;
        this.criancaDeColo = criancaDeColo;
    }

    public String getNome() {
        return nome;
    }

    public int getIdade() {
        return idade;
    }

    public boolean isPossuiDeficiencia() {
        return possuiDeficiencia;
    }

    public boolean isGestante() {
        return gestante;
    }

    public boolean isPossuiObesidade() {
        return possuiObesidade;
    }

    public boolean isCriancaDeColo() {
        return criancaDeColo;
    }

    public boolean temPrioridadeEspecial() {
        return idade >= 80;
    }

    public boolean temPrioridade() {
        return idade >= 60
                || possuiDeficiencia
                || gestante
                || possuiObesidade
                || criancaDeColo;
    }
}