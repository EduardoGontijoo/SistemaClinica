package model;

public class Senha {

    private int numero;
    private String tipo;
    private boolean atendida;
    private Paciente paciente;

    public Senha(int numero, String tipo) {
        if (numero <= 0) {
            throw new IllegalArgumentException(
                    "O número da senha deve ser positivo."
            );
        }

        if (tipo == null
                || (!tipo.equals("N") && !tipo.equals("P"))) {
            throw new IllegalArgumentException(
                    "O tipo deve ser N ou P."
            );
        }

        this.numero = numero;
        this.tipo = tipo;
        this.atendida = false;
    }

    public int getNumero() {
        return numero;
    }

    public String getTipo() {
        return tipo;
    }

    public boolean isAtendida() {
        return atendida;
    }

    public void marcarComoAtendida() {
        atendida = true;
    }

    public String getSenhaFormatada() {
        return tipo + String.format("%03d", numero);
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }
}