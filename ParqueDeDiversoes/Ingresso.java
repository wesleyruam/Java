import java.time.LocalDateTime;

public class Ingresso {
    private Atracao atracao;
    private Visitante visitante;
    private LocalDateTime dataHora;
    private double precoPago;

    public Ingresso(Atracao atracao, Visitante visitante, double precoPago){
        this.atracao = atracao;
        this.visitante = visitante;
        this.precoPago = precoPago;
        this.dataHora = LocalDateTime.now();
    }

    public Atracao getAtracao(){
        return this.atracao;
    }

    public Visitante getVisitante(){
        return this.visitante;
    }

    public LocalDateTime getDataHora(){
        return this.dataHora;
    }

    public double getPrecoPago(){
        return this.precoPago;
    }

    public void setAtracao(Atracao atracao){
        this.atracao = atracao;
    }

    public void setVisitante(Visitante visitante){
        this.visitante = visitante;
    }

    public void setDataHora(LocalDateTime dataHora){
        this.dataHora = dataHora;
    }

    public void setPrecoPago(double precoPago){
        this.precoPago = precoPago;
    }
}
