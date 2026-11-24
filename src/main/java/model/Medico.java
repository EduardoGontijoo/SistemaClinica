
package model;

public class Medico {

    private String nome;
    private String especialidade;
    private int consultorio;
    private String usuario;
    private String senha;

    public Medico(String nome, String especialidade, int consultorio) {
        this(nome, especialidade, consultorio, "", "");
    }

    public Medico(String nome, String especialidade, int consultorio,
                  String usuario, String senha) {
        this.nome = nome;
        this.especialidade = especialidade;
        this.consultorio = consultorio;
        this.usuario = usuario;
        this.senha = senha;
    }

    public String getNome() {
        return nome;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public int getConsultorio() {
        return consultorio;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getSenha() {
        return senha;
    }
}
