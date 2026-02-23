import java.util.ArrayList;
import java.util.List;

public abstract class Atracao {
    private String nome;
    private int capacidadeMaxima;
    private boolean emOperacao;
    private List<Visitante> visitantesAtuais;

    public Atracao(String nome, int capacidadeMaxima){
        this.nome = nome;
        this.capacidadeMaxima = capacidadeMaxima;
        this.visitantesAtuais = new ArrayList<>();
        this.emOperacao = true; 
    }

    public abstract boolean verificarRequisitos(Visitante v);
    public abstract double getPreco();

    public boolean adicionarVisitante(Visitante v){
        // Verificar se atingiu capacidade máxima
        if (visitantesAtuais.size() >= capacidadeMaxima) {
            System.out.println("Atração " + nome + " está lotada!");
            return false;
        }
        
        // Verificar se o visitante já está na atração
        for (Visitante visitante : visitantesAtuais) {
            if (visitante.getId().equals(v.getId())) {
                System.out.println("Visitante já está na atração!");
                return false;
            }
        }
        
        this.visitantesAtuais.add(v);
        System.out.println(v.getNome() + " entrou na atração " + nome);
        return true;
    }

    public void removerVisitante(Visitante v){
        for (int i = 0; i < this.visitantesAtuais.size(); i++){
            if (this.visitantesAtuais.get(i).getId().equals(v.getId())){
                this.visitantesAtuais.remove(i);
                System.out.println(v.getNome() + " saiu da atração " + nome);
                break; 
            }
        }
    }

    public String getNome(){
        return this.nome;
    }

    public void setNome(String nome){
        this.nome = nome;
    }

    public int getCapacidadeMaxima(){
        return this.capacidadeMaxima;
    }

    public void setCapacidadeMaxima(int capacidadeMaxima){
        this.capacidadeMaxima = capacidadeMaxima;
    }

    public boolean isEmOperacao(){
        return this.emOperacao;
    }

    public void setEmOperacao(boolean emOperacao){
        this.emOperacao = emOperacao;
    }

    public List<Visitante> getVisitantesAtuais(){
        return this.visitantesAtuais;
    }
}