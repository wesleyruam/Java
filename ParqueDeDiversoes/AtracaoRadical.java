public class AtracaoRadical extends Atracao {
    private double alturaMinima;
    private int idadeMinima;
    
    public AtracaoRadical(String nome, int capacidade, double alturaMin, int idadeMin) {
        super(nome, capacidade);
        this.alturaMinima = alturaMin;
        this.idadeMinima = idadeMin;
    }
    
    @Override
    public boolean verificarRequisitos(Visitante v) {
        return v.getIdade() >= idadeMinima && v.getAltura() >= alturaMinima;
    }
    
    @Override
    public double getPreco() {
        return 30.00; // R$ 30,00
    }
    
    public double getAlturaMinima() {
        return alturaMinima;
    }
    
    public void setAlturaMinima(double alturaMinima) {
        this.alturaMinima = alturaMinima;
    }
    
    public int getIdadeMinima() {
        return idadeMinima;
    }
    
    public void setIdadeMinima(int idadeMinima) {
        this.idadeMinima = idadeMinima;
    }
}