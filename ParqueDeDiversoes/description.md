## Projeto: Sistema de Gerenciamento de Parque de Diversões

Este projeto tem como objetivo criar um sistema para administrar um parque de diversões, permitindo o cadastro de atrações, visitantes e funcionários, além de controlar a compra de ingressos e a interação dos visitantes com as atrações. O sistema aplica todos os quatro pilares da Programação Orientada a Objetos (POO): **abstração, encapsulamento, herança e polimorfismo**.

### Descrição Geral
O parque possui diferentes tipos de atrações (radicais e infantis) e pessoas (visitantes e funcionários). Cada atração tem regras específicas de uso (idade, altura) e preço. Visitantes podem comprar ingressos para as atrações, e funcionários são responsáveis pela operação e manutenção. O sistema gerencia o caixa, os acessos e exibe relatórios.

---

## Estrutura de Classes

### 1. Classe Abstrata `Pessoa`
Representa uma pessoa genérica no sistema. Serve como base para `Visitante` e `Funcionario`.

**Atributos:**
- `private String nome`: Nome da pessoa.
- `private int idade`: Idade em anos.
- `private String id`: Documento de identificação (ex: CPF).

**Métodos:**
- `public Pessoa(String nome, int idade, String id)`: Construtor.
- `public String getNome()`: Retorna o nome.
- `public void setNome(String nome)`: Altera o nome.
- `public int getIdade()`: Retorna a idade.
- `public void setIdade(int idade)`: Altera a idade.
- `public String getId()`: Retorna o ID.
- `public void setId(String id)`: Altera o ID.
- `public abstract String getTipo()`: Método abstrato que retorna o tipo de pessoa (a ser implementado nas subclasses).

---

### 2. Classe `Visitante` (herda de `Pessoa`)
Representa um visitante do parque.

**Atributos:**
- `private double saldoCartao`: Saldo disponível no cartão do visitante para compras.
- `private List<Ingresso> ingressos`: Lista de ingressos adquiridos pelo visitante.

**Métodos:**
- `public Visitante(String nome, int idade, String id, double saldoInicial)`: Construtor.
- `public double getSaldoCartao()`: Retorna o saldo.
- `public void setSaldoCartao(double saldo)`: Atualiza o saldo.
- `public List<Ingresso> getIngressos()`: Retorna a lista de ingressos.
- `public boolean comprarIngresso(Ingresso ingresso)`: Verifica se o saldo é suficiente para o preço do ingresso. Se sim, debita o valor, adiciona o ingresso à lista e retorna `true`; caso contrário, retorna `false`.
- `public double calcularPrecoIngresso()`: Calcula o preço base de um ingresso considerando a idade da pessoa (polimorfismo):
  - Se idade < 12: retorna 50% do preço base (meia).
  - Se idade >= 60: retorna 40% de desconto (60% do preço base).
  - Caso contrário: retorna preço cheio (100%).
- `@Override public String getTipo()`: Retorna a string `"Visitante"`.

---

### 3. Classe `Funcionario` (herda de `Pessoa`)
Representa um funcionário do parque.

**Atributos:**
- `private String cargo`: Cargo do funcionário (ex: "operador", "mecânico").
- `private double salario`: Salário mensal.
- `private Set<String> habilidades`: Conjunto de habilidades (ex: "opera montanha-russa", "reparos elétricos").

**Métodos:**
- `public Funcionario(String nome, int idade, String id, String cargo, double salario, Set<String> habilidades)`: Construtor.
- Getters e setters para todos os atributos.
- `public void executarTrabalho(Atracao atracao)`: Executa uma ação conforme o cargo (polimorfismo):
  - Se cargo for "operador", exibe mensagem que está operando a atração.
  - Se cargo for "mecânico", exibe mensagem que está fazendo manutenção.
  - (Poderia ser expandido com lógica real).
- `@Override public String getTipo()`: Retorna `"Funcionario"`.

---

### 4. Classe Abstrata `Atracao`
Representa uma atração do parque.

**Atributos:**
- `private String nome`: Nome da atração.
- `private int capacidadeMaxima`: Número máximo de visitantes simultâneos.
- `private boolean emOperacao`: Indica se a atração está funcionando.
- `private List<Visitante> visitantesAtuais`: Lista de visitantes que estão usando a atração no momento.

**Métodos:**
- `public Atracao(String nome, int capacidade)`: Construtor.
- Getters e setters para os atributos (exceto visitantesAtuais, que pode ter apenas getter).
- `public abstract boolean verificarRequisitos(Visitante v)`: Verifica se o visitante atende aos requisitos para usar a atração (a ser implementado nas subclasses).
- `public abstract double getPreco()`: Retorna o preço para usar a atração.
- `public boolean adicionarVisitante(Visitante v)`: Se `verificarRequisitos(v)` for `true` e a capacidade não foi atingida, adiciona o visitante à lista e retorna `true`; caso contrário, retorna `false`.
- `public void removerVisitante(Visitante v)`: Remove o visitante da lista (quando ele sai da atração).

---

### 5. Classe `AtracaoRadical` (herda de `Atracao`)
Atração voltada para aventura, com restrições de altura e idade.

**Atributos:**
- `private double alturaMinima`: Altura mínima em metros.
- `private int idadeMinima`: Idade mínima.

**Métodos:**
- `public AtracaoRadical(String nome, int capacidade, double alturaMin, int idadeMin)`: Construtor.
- Getters e setters.
- `@Override public boolean verificarRequisitos(Visitante v)`: Retorna `true` se o visitante tiver idade >= idadeMinima e altura >= alturaMinima; caso contrário, `false`.
- `@Override public double getPreco()`: Retorna um valor fixo, por exemplo, **R$ 30.00**.

---

### 6. Classe `AtracaoInfantil` (herda de `Atracao`)
Atração destinada a crianças.

**Atributos:**
- `private int idadeMaxima`: Idade máxima permitida.

**Métodos:**
- `public AtracaoInfantil(String nome, int capacidade, int idadeMax)`: Construtor.
- Getters e setters.
- `@Override public boolean verificarRequisitos(Visitante v)`: Retorna `true` se a idade do visitante for <= idadeMaxima; caso contrário, `false`.
- `@Override public double getPreco()`: Retorna um valor fixo menor, por exemplo, **R$ 15.00**.

---

### 7. Classe `Ingresso`
Representa um ingresso comprado para uma atração específica.

**Atributos:**
- `private Atracao atracao`: Atração associada.
- `private Visitante visitante`: Visitante que comprou.
- `private LocalDateTime dataHora`: Data e hora da compra.
- `private double precoPago`: Valor efetivamente pago (já com descontos).

**Métodos:**
- `public Ingresso(Atracao atracao, Visitante visitante, double precoPago)`: Construtor (preenche dataHora com o momento atual).
- Getters para todos os atributos.

---

### 8. Classe `Parque` (Classe principal de controle)
Gerencia o parque como um todo.

**Atributos:**
- `private String nome`: Nome do parque.
- `private List<Atracao> atracoes`: Lista de atrações cadastradas.
- `private List<Visitante> visitantes`: Lista de visitantes cadastrados.
- `private List<Funcionario> funcionarios`: Lista de funcionários.
- `private double caixa`: Saldo financeiro do parque.

**Métodos:**
- `public Parque(String nome)`: Construtor.
- `public void cadastrarAtracao(Atracao a)`: Adiciona uma atração à lista.
- `public void cadastrarVisitante(Visitante v)`: Adiciona um visitante.
- `public void cadastrarFuncionario(Funcionario f)`: Adiciona um funcionário.
- `public boolean venderIngresso(Visitante v, Atracao a)`: 
  1. Calcula o preço base da atração com `a.getPreco()`.
  2. Aplica o desconto do visitante chamando `v.calcularPrecoIngresso()` (que retorna um multiplicador).
  3. Calcula o preço final: `precoBase * multiplicador`.
  4. Cria um objeto `Ingresso` com esse valor.
  5. Tenta executar `v.comprarIngresso(ingresso)`. Se bem-sucedido, adiciona o valor ao caixa e retorna `true`; senão, retorna `false`.
- `public void exibirRelatorio()`: Exibe na console informações sobre atrações (nome, status, visitantes atuais), visitantes (nome, saldo, ingressos) e caixa.

---

## Aplicação dos Pilares da POO

### Abstração
- A classe `Pessoa` é abstrata e define o conceito genérico de pessoa, com o método abstrato `getTipo()`. As subclasses concretas implementam esse método, escondendo os detalhes de como cada tipo se identifica.
- A classe `Atracao` também é abstrata, definindo a ideia de uma atração com métodos abstratos `verificarRequisitos` e `getPreco`. As subclasses concretizam esses comportamentos conforme o tipo de atração.

### Encapsulamento
- Todos os atributos das classes são privados (`private`). O acesso a eles é controlado por métodos públicos getters e setters, garantindo que modificações indevidas não ocorram sem validação.
- Exemplo: em `Visitante`, o método `comprarIngresso` encapsula a lógica de verificação de saldo e adição do ingresso, protegendo a integridade dos dados.

### Herança
- `Visitante` e `Funcionario` herdam de `Pessoa`, reaproveitando atributos (nome, idade, id) e o método abstrato `getTipo`, que cada um implementa de forma específica.
- `AtracaoRadical` e `AtracaoInfantil` herdam de `Atracao`, herdando atributos como `nome` e `capacidade`, e implementando os métodos abstratos conforme suas regras de negócio.

### Polimorfismo
- **Sobrescrita de métodos:**
  - `getTipo()` em `Visitante` e `Funcionario` retornam strings diferentes, permitindo tratar objetos de `Pessoa` de forma genérica e obter o tipo correto em tempo de execução.
  - `verificarRequisitos` e `getPreco` são implementados de maneiras distintas nas subclasses de `Atracao`. O método `adicionarVisitante` da classe `Atracao` chama `verificarRequisitos` sem saber qual é a atração concreta, e o comportamento adequado é executado.
- **Sobrecarga (exemplo adicional):** Em `Funcionario`, poderíamos ter múltiplas versões de `executarTrabalho` com diferentes parâmetros, mas optamos por manter o foco no polimorfismo por sobrescrita.

---

## Exemplo de Uso (pseudo-código)
```java
public class Main {
    public static void main(String[] args) {
        Parque parque = new Parque("Fantasy World");

        // Cadastro de atrações
        Atracao montanhaRussa = new AtracaoRadical("Montanha Russa", 20, 1.40, 12);
        Atracao carrossel = new AtracaoInfantil("Carrossel", 30, 10);
        parque.cadastrarAtracao(montanhaRussa);
        parque.cadastrarAtracao(carrossel);

        // Cadastro de visitantes
        Visitante ana = new Visitante("Ana", 8, "123", 100.0);
        Visitante joao = new Visitante("João", 35, "456", 200.0);
        parque.cadastrarVisitante(ana);
        parque.cadastrarVisitante(joao);

        // Cadastro de funcionário
        Set<String> habilidades = new HashSet<>();
        habilidades.add("opera montanha-russa");
        Funcionario pedro = new Funcionario("Pedro", 28, "789", "operador", 2500.0, habilidades);
        parque.cadastrarFuncionario(pedro);

        // Venda de ingressos
        parque.venderIngresso(ana, carrossel); // Ana compra ingresso para Carrossel (com desconto de criança)
        parque.venderIngresso(joao, montanhaRussa); // João compra ingresso para Montanha Russa (preço cheio)

        // Executar trabalho do funcionário
        pedro.executarTrabalho(montanhaRussa); // Exibe: "Pedro está operando Montanha Russa"

        // Relatório
        parque.exibirRelatorio();
    }
}
```

Este projeto demonstra de forma clara e prática todos os pilares da POO, sendo facilmente extensível para novas funcionalidades, como tipos adicionais de atrações ou diferentes categorias de funcionários.