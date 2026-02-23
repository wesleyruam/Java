import java.util.ArrayList;
import java.util.List;

public class Visitante extends Pessoa {
    private double saldoCartao;
    private List<Ingresso> ingressos;
    private double altura;

    public Visitante(String nome, int idade, double altura, String id, double saldoInicial) {
        super(nome, idade, id);
        this.altura = altura;
        this.saldoCartao = saldoInicial;
        this.ingressos = new ArrayList<>();
    }

    public double getSaldoCartao() {
        return this.saldoCartao;
    }

    public void setSaldoCartao(double saldo) {
        this.saldoCartao = saldo;
    }

    public List<Ingresso> getIngressos() {
        return this.ingressos; 
    }

    public double getAltura() {
        return this.altura;
    }

    public void setAltura(double altura) {
        this.altura = altura;
    }

    public boolean comprarIngresso(Ingresso ingresso) {
        double preco = ingresso.getPrecoPago();
        
        // Verifica se o saldo é suficiente
        if (this.saldoCartao >= preco) {
            // Debita o valor do cartão
            this.saldoCartao -= preco;
            
            this.ingressos.add(ingresso);
            
            return true; // Compra realizada com sucesso
        } else {
            return false; // Saldo insuficiente
        }
    }


    public double calcularPrecoIngresso() {
        if (this.getIdade() < 12) {
            return 0.5; // 50% do preço (meia)
        } else if (this.getIdade() >= 60) {
            return 0.6; // 60% do preço (40% de desconto)
        } else {
            return 1.0; // 100% do preço (preço cheio) - CORRIGIDO!
        }
    }


    public void listarIngressos() {
        System.out.println("Ingressos de " + this.getNome() + ":");
        if (ingressos.isEmpty()) {
            System.out.println("  Nenhum ingresso comprado ainda.");
        } else {
            for (Ingresso ing : ingressos) {
                System.out.println("  - " + ing.getAtracao().getNome() + 
                                   " | R$" + ing.getPrecoPago() + 
                                   " | " + ing.getDataHora());
            }
        }
    }

    @Override
    public String getTipo() {
        return "Visitante";
    }
}