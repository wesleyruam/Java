public abstract class Pessoa {
    private String nome;
    private int idade;
    private String id;

    public Pessoa(String nome, int idade, String id){
        this.nome = nome;
        this.idade = idade;
        this.id = id;
    }

    public String getNome(){
        return this.nome;
    }

    public void setNome(String nome){
        this.nome = nome;
    }

    public int getIdade(){
        return this.idade;
    }

    public void setIdade(int idade){
        this.idade = idade;
    }

    public String getId(){
        return this.id;
    }

    public void setId(String id){
        this.id = id;
    }

    public abstract String getTipo();

    
}

