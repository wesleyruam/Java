import java.util.Set;

public class Funcionario extends Pessoa{
    private String cargo;
    private double salario;
    private Set<String> habilidades;

    public Funcionario(String nome, int idade, String id, String cargo, double salario, Set<String>habilidades){
        super(nome, idade, id);
        this.cargo = cargo;
        this.salario = salario;
        this.habilidades = habilidades;
    }

    public void executarTrabalho(Atracao atracao){

    }

    @Override
    public String getTipo(){
        return "Funcionario";
    }

    public String getCargo(){
        return this.cargo;
    }

    public void setCargo(String cargo){
        this.cargo = cargo;
    }

    public double getSalario(){
        return this.salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    public Set<String> getHabilidades(){
        return this.habilidades;
    }

    public void setHabilidades(Set<String> habilidades){
        this.habilidades = habilidades;
    }
}
