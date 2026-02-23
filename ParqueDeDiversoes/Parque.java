import java.util.ArrayList;
import java.util.List;

public class Parque {
    private String nome;
    private List<Atracao> atracoes;
    private List<Visitante> visitantes;
    private List<Funcionario> funcionarios;
    private double caixa; // saldo financeiro do parque

    public Parque(String nome){
        this.nome = nome;
        this.atracoes = new ArrayList<>();
        this.visitantes = new ArrayList<>();
        this.funcionarios = new ArrayList<>();
        this.caixa = 0.0;
    }

    public void cadastrarAtracao(Atracao a){
        this.atracoes.add(a);
        System.out.println("Atração '" + a.getNome() + "' cadastrada com sucesso!");
    }

    public void cadastrarVisitante(Visitante v){
        this.visitantes.add(v);
        System.out.println("Visitante '" + v.getNome() + "' cadastrado com sucesso!");
    }

    public void cadastrarFuncionario(Funcionario f){
        this.funcionarios.add(f);
        System.out.println("Funcionário '" + f.getNome() + "' cadastrado com sucesso!");
    }


    public boolean venderIngresso(Visitante v, Atracao a){
        if (!atracoes.contains(a)) {
            System.out.println("Erro: Atração não encontrada no parque!");
            return false;
        }
        
        if (!visitantes.contains(v)) {
            System.out.println("Erro: Visitante não cadastrado no parque!");
            return false;
        }
        
        if (!a.verificarRequisitos(v)) {
            System.out.println("Erro: Visitante não atende aos requisitos da atração " + a.getNome());
            return false;
        }
        
        double precoBase = a.getPreco();
        
        double multiplicador = v.calcularPrecoIngresso();
        double precoFinal = precoBase * multiplicador;
        
        Ingresso ingresso = new Ingresso(a, v, precoFinal);
        
        if (v.comprarIngresso(ingresso)) {
            this.caixa += precoFinal;
            
            System.out.println("Ingresso vendido com sucesso para " + v.getNome() + 
                               " na atração " + a.getNome() + 
                               " por R$" + String.format("%.2f", precoFinal));
            return true;
        } else {
            System.out.println("Falha na venda: Saldo insuficiente do visitante " + v.getNome());
            return false;
        }
    }

    public void exibirRelatorio(){
        System.out.println("\n" + "=".repeat(50));
        System.out.println("📊 RELATÓRIO DO PARQUE: " + this.nome);
        System.out.println("=".repeat(50));
        
        System.out.println("\n💰 CAIXA: R$" + String.format("%.2f", this.caixa));
        
        System.out.println("\n🎢 ATRAÇÕES (" + atracoes.size() + "):");
        if (atracoes.isEmpty()) {
            System.out.println("  Nenhuma atração cadastrada.");
        } else {
            for (Atracao a : atracoes) {
                String status = a.isEmOperacao() ? "🟢 Operando" : "🔴 Parada";
                System.out.println("  - " + a.getNome() + 
                                   " | Capacidade: " + a.getCapacidadeMaxima() +
                                   " | Preço: R$" + String.format("%.2f", a.getPreco()) +
                                   " | Status: " + status +
                                   " | Visitantes atuais: " + a.getVisitantesAtuais().size());
            }
        }
        
        System.out.println("\n👥 VISITANTES (" + visitantes.size() + "):");
        if (visitantes.isEmpty()) {
            System.out.println("  Nenhum visitante cadastrado.");
        } else {
            for (Visitante v : visitantes) {
                System.out.println("  - " + v.getNome() + 
                                   " | Idade: " + v.getIdade() +
                                   " | Altura: " + v.getAltura() + "m" +
                                   " | Saldo: R$" + String.format("%.2f", v.getSaldoCartao()) +
                                   " | Ingressos: " + v.getIngressos().size());
                
                if (!v.getIngressos().isEmpty()) {
                    for (Ingresso i : v.getIngressos()) {
                        System.out.println("      📌 " + i.getAtracao().getNome() + 
                                           " - R$" + String.format("%.2f", i.getPrecoPago()) +
                                           " - " + i.getDataHora());
                    }
                }
            }
        }
        
        System.out.println("\n👨‍💼 FUNCIONÁRIOS (" + funcionarios.size() + "):");
        if (funcionarios.isEmpty()) {
            System.out.println("  Nenhum funcionário cadastrado.");
        } else {
            for (Funcionario f : funcionarios) {
                System.out.println("  - " + f.getNome() + 
                                   " | Cargo: " + f.getCargo() +
                                   " | Salário: R$" + String.format("%.2f", f.getSalario()));
            }
        }
        
        System.out.println("\n" + "=".repeat(50));
    }

    public String getNome(){
        return this.nome;
    }
    
    public double getCaixa() {
        return this.caixa;
    }
    
    public List<Atracao> getAtracoes() {
        return atracoes;
    }
    
    public List<Visitante> getVisitantes() {
        return visitantes;
    }
    
    public List<Funcionario> getFuncionarios() {
        return funcionarios;
    }
}