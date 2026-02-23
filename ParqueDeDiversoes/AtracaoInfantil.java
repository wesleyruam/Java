public class AtracaoInfantil extends Atracao {
    private int idadeMaxima;
    
    public AtracaoInfantil(String nome, int capacidade, int idadeMax) {
        super(nome, capacidade);
        this.idadeMaxima = idadeMax;
    }
    
    @Override
    public boolean verificarRequisitos(Visitante v) {
        return v.getIdade() <= idadeMaxima;
    }
    
    @Override
    public double getPreco() {
        return 15.00; // R$ 15,00
    }
    
    public int getIdadeMaxima() {
        return idadeMaxima;
    }
    
    public void setIdadeMaxima(int idadeMaxima) {
        this.idadeMaxima = idadeMaxima;
    }
}