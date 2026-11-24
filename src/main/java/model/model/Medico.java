package model.model;

public class Medico {
    private final String nome;
    private final String especialidade;
    private final int consultorio;
    private final String usuario;
    private final String senha;

    // Mantém compatibilidade com trechos antigos do projeto.
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

    public String getNome() { return nome; }
    public String getEspecialidade() { return especialidade; }
    public int getConsultorio() { return consultorio; }
    public String getUsuario() { return usuario; }
    public String getSenha() { return senha; }
}
