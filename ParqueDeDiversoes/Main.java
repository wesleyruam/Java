import java.util.HashSet;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        Parque parque = new Parque("Fantasy World");

        // Cadastro de atrações
        Atracao montanhaRussa = new AtracaoRadical("Montanha Russa", 20, 1.40, 12);
        Atracao carrossel = new AtracaoInfantil("Carrossel", 30, 10);
        parque.cadastrarAtracao(montanhaRussa);
        parque.cadastrarAtracao(carrossel);

        // Cadastro de visitantes
        Visitante ana = new Visitante("Ana", 8, 1.77, "123", 100.0);
        Visitante joao = new Visitante("João", 35, 1.80, "456", 200.0);
        parque.cadastrarVisitante(ana);
        parque.cadastrarVisitante(joao);

        // Cadastro de funcionário
        Set<String> habilidades = new HashSet<>();
        habilidades.add("opera montanha-russa");
        Funcionario pedro = new Funcionario("Pedro", 28, "789", "operador", 2500.0, habilidades);
        parque.cadastrarFuncionario(pedro);

        parque.venderIngresso(ana, carrossel); 
        parque.venderIngresso(joao, montanhaRussa); 

        pedro.executarTrabalho(montanhaRussa);

        parque.exibirRelatorio();
    }
}