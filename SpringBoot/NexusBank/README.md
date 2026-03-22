---

### 1. Descrição do sistema

O **Nexus Bank** é uma plataforma bancária digital (fintech) que permite aos usuários realizar operações financeiras básicas de forma 100% online. O sistema oferece abertura de conta, gerenciamento de saldo, transferências entre usuários (estilo Pix), depósitos, pagamento de contas e consulta de extrato. A segurança é garantida por autenticação JWT e separação de papéis (roles), proporcionando uma experiência fluida e segura, similar a aplicações como PicPay, Nubank ou Banco do Brasil.

### 2. Objetivo do projeto

Criar uma aplicação backend robusta, escalável e segura para um banco virtual, capaz de processar transações financeiras de forma confiável, mantendo a integridade dos dados (consistência ACID) e fornecendo uma API clara e eficiente para consumo por aplicações mobile e web.

### 3. Requisitos funcionais

**Módulo de Usuário e Conta**
- RF01: O sistema deve permitir o cadastro de novos usuários (pessoa física) com nome, CPF, e-mail, data de nascimento e senha.
- RF02: O sistema deve permitir que o usuário faça login utilizando e-mail e senha, retornando um token JWT.
- RF03: O sistema deve criar automaticamente uma conta bancária para o usuário no momento do cadastro.

**Módulo de Transações**
- RF04: O sistema deve permitir que um usuário autenticado realize transferências de valores entre contas (usuários).
- RF05: O sistema deve permitir que um usuário autenticado realize depósitos em sua própria conta.
- RF06: O sistema deve permitir que um usuário autenticado realize pagamentos de contas (boleto, etc) informando um código de barras.
- RF07: O sistema deve listar o extrato financeiro do usuário com filtros por data e tipo de transação.

**Módulo de Saldo e Segurança**
- RF08: O sistema deve exibir o saldo atual da conta do usuário logado.
- RF09: O sistema deve bloquear a conta ou impedir transações caso o limite de segurança seja atingido (ex: 3 tentativas de senha inválidas).
- RF10: O sistema deve permitir que o usuário visualize e atualize seus dados pessoais.

### 4. Requisitos não funcionais

- **Segurança:** As senhas devem ser armazenadas utilizando hash (bcrypt). Todas as comunicações devem ocorrer via HTTPS. Tokens JWT devem ter expiração curta (15min a 1h) e refresh token rotativo.
- **Disponibilidade:** O sistema deve ter disponibilidade mínima de 99.5% (dependendo do ambiente de deploy).
- **Consistência:** Todas as operações que envolvem crédito/débito (transferências, pagamentos) devem ser transacionais (ACID). Não pode haver inconsistência de saldo.
- **Performance:** O endpoint de consulta de saldo deve responder em menos de 100ms. O endpoint de transferência deve processar em menos de 500ms.
- **Escalabilidade:** A arquitetura deve permitir escalabilidade horizontal dos serviços de API.
- **Auditoria:** Todas as transações financeiras devem ser registradas em logs imutáveis (tabela de transações).

### 5. Regras de negócio

- RN01: Uma conta não pode ter saldo negativo. Transferências e pagamentos só são permitidos se o saldo for suficiente.
- RN02: Transferências para a própria conta devem ser bloqueadas.
- RN03: Para realizar uma transferência, o usuário deve estar autenticado e a conta de origem deve estar ativa.
- RN04: O CPF e e-mail cadastrados devem ser únicos no sistema.
- RN05: Usuários menores de 18 anos só podem abrir contas com autorização de um responsável (aplicação futura, mas o campo data_nascimento é obrigatório).
- RN06: Após 5 tentativas consecutivas de login com senha errada, a conta é bloqueada por 30 minutos.
- RN07: Depósitos não podem ter valor negativo ou zero.
- RN08: Toda transferência gera um registro na tabela de transações, e também uma notificação (caso exista serviço de notificação).

### 6. Arquitetura do sistema

A arquitetura será **Monolítica Modular** (também conhecida como *Modular Monolith*), evoluindo para **Microservices** se necessário.

- **Camadas:**
    1.  **Controller/API Layer:** Responsável por receber as requisições HTTP, validar entrada (DTOs) e retornar respostas.
    2.  **Service Layer (Use Cases):** Contém toda a lógica de negócio. É a camada mais importante, onde as regras de negócio são aplicadas.
    3.  **Repository/DAO Layer:** Responsável pela comunicação com o banco de dados.
    4.  **Domain/Entity Layer:** Define as entidades de negócio e os *Value Objects*.
    5.  **Infrastructure Layer:** Configurações de segurança, JWT, logging, e clientes HTTP externos (se houver).

**Padrão de Design:** **Clean Architecture** simplificada (Separação de responsabilidades, Dependency Inversion).

### 7. Tecnologias recomendadas

- **Linguagem:** Java 17+ ou Kotlin (pela robustez e ecossistema bancário) / Node.js (TypeScript) como alternativa ágil.
- **Framework:** Spring Boot (Spring Security, Spring Data JPA) ou NestJS.
- **Banco de Dados:** PostgreSQL (relacional, ACID forte) para dados transacionais.
- **Cache:** Redis (para controle de bloqueio de tentativas de login, tokens revogados, etc).
- **Message Broker:** Apache Kafka ou RabbitMQ (para processamento assíncrono de notificações e logs, desacoplando a operação principal da transferência).
- **Infra/Deploy:** Docker + Kubernetes ou Docker Compose.
- **Autenticação:** JWT (Access Token + Refresh Token).

### 8. Entidades do sistema com atributos

**User (Usuário)**
- `id` (UUID)
- `name` (String)
- `cpf` (String, unique)
- `email` (String, unique)
- `password_hash` (String)
- `date_of_birth` (LocalDate)
- `phone` (String)
- `status` (Enum: ACTIVE, BLOCKED, INACTIVE)
- `created_at` (Timestamp)
- `updated_at` (Timestamp)

**Account (Conta)**
- `id` (UUID)
- `user_id` (UUID, FK)
- `account_number` (String, unique - gerado automaticamente)
- `agency` (String - ex: "0001")
- `balance` (Decimal(15,2))
- `type` (Enum: CHECKING, SAVINGS)
- `created_at` (Timestamp)

**Transaction (Transação)**
- `id` (UUID)
- `source_account_id` (UUID, FK) - Pode ser nulo se for depósito externo
- `destination_account_id` (UUID, FK) - Pode ser nulo se for pagamento de conta
- `amount` (Decimal(15,2))
- `type` (Enum: TRANSFER, DEPOSIT, PAYMENT, WITHDRAWAL)
- `status` (Enum: PENDING, COMPLETED, FAILED, REVERSED)
- `description` (String)
- `reference_id` (String) - Para idempotência (ex: ID do pagamento, chave Pix)
- `created_at` (Timestamp)

**Bill (Conta a Pagar)**
- `id` (UUID)
- `account_id` (UUID, FK)
- `bar_code` (String)
- `amount` (Decimal(15,2))
- `due_date` (LocalDate)
- `status` (Enum: PENDING, PAID, OVERDUE)
- `paid_at` (Timestamp)

### 9. Relacionamentos entre entidades

- **User (1) : (1) Account** - Um usuário possui uma conta primária (relação one-to-one). *Melhoria futura: Um usuário pode ter várias contas.*
- **Account (1) : (N) Transaction** - Uma conta pode ser origem ou destino de várias transações.
- **Account (1) : (N) Bill** - Uma conta pode ter várias contas a pagar.

### 10. Endpoints da API (REST)

**Auth**
- `POST /api/v1/auth/register` - Cadastro de usuário
- `POST /api/v1/auth/login` - Login
- `POST /api/v1/auth/refresh` - Refresh token

**User**
- `GET /api/v1/users/me` - Buscar dados do próprio usuário
- `PUT /api/v1/users/me` - Atualizar dados do próprio usuário

**Account**
- `GET /api/v1/accounts/me` - Buscar dados da própria conta (saldo, número, agência)
- `GET /api/v1/accounts/me/balance` - Consultar saldo

**Transactions**
- `POST /api/v1/transactions/transfer` - Realizar transferência entre contas
- `POST /api/v1/transactions/deposit` - Realizar depósito
- `POST /api/v1/transactions/pay` - Pagar conta (boleto)
- `GET /api/v1/transactions/statement` - Listar extrato (com paginação)

### 11. Formato de entrada (request DTO)

**RegisterRequest**
```json
{
  "name": "João da Silva",
  "email": "joao@email.com",
  "cpf": "123.456.789-00",
  "password": "SenhaForte123!",
  "dateOfBirth": "1990-01-01",
  "phone": "11999999999"
}
```

**TransferRequest**
```json
{
  "destinationAccountNumber": "123456",
  "amount": 150.00,
  "description": "Pagamento do aluguel",
  "idempotencyKey": "uuid-unico-para-nao-processar-duplicado"
}
```

**DepositRequest**
```json
{
  "amount": 500.00,
  "description": "Depósito em espécie"
}
```

**PaymentRequest**
```json
{
  "barCode": "12345678901234567890123456789012345678901234",
  "amount": 99.90
}
```

### 12. Formato de saída (response DTO)

**AccountResponse**
```json
{
  "accountNumber": "123456",
  "agency": "0001",
  "balance": 1250.50,
  "type": "CHECKING"
}
```

**TransactionResponse**
```json
{
  "id": "uuid",
  "amount": 150.00,
  "type": "TRANSFER",
  "status": "COMPLETED",
  "description": "Transferência para 654321",
  "createdAt": "2023-10-27T10:30:00Z"
}
```

**ErrorResponse**
```json
{
  "timestamp": "2023-10-27T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Saldo insuficiente para realizar a transferência.",
  "path": "/api/v1/transactions/transfer"
}
```

### 13. Estrutura de pastas do backend (Spring Boot)

```
src/main/java/com/nexusbank/
├── NexusBankApplication.java
├── config/
│   ├── SecurityConfig.java
│   ├── JwtConfig.java
│   ├── RedisConfig.java
│   └── SwaggerConfig.java
├── controller/
│   ├── AuthController.java
│   ├── UserController.java
│   ├── AccountController.java
│   └── TransactionController.java
├── dto/
│   ├── request/
│   │   ├── RegisterRequest.java
│   │   ├── LoginRequest.java
│   │   ├── TransferRequest.java
│   │   └── ...
│   └── response/
│       ├── AccountResponse.java
│       ├── TransactionResponse.java
│       └── ...
├── entity/
│   ├── User.java
│   ├── Account.java
│   ├── Transaction.java
│   └── Bill.java
├── repository/
│   ├── UserRepository.java
│   ├── AccountRepository.java
│   ├── TransactionRepository.java
│   └── ...
├── service/
│   ├── AuthService.java
│   ├── UserService.java
│   ├── AccountService.java
│   ├── TransactionService.java
│   └── impl/
│       └── ...
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── BusinessException.java
│   └── ...
├── security/
│   ├── JwtAuthenticationFilter.java
│   ├── JwtTokenProvider.java
│   └── CustomUserDetailsService.java
└── util/
    └── IdempotencyUtil.java
```

### 14. Padrão de resposta da API

Adotaremos o padrão RESTful com envelopes consistentes.

**Sucesso (200/201):**
```json
{
  "data": { ... },
  "message": "Operação realizada com sucesso",
  "timestamp": "2023-10-27T10:30:00Z"
}
```

**Lista com Paginação:**
```json
{
  "data": [ ... ],
  "page": 0,
  "size": 20,
  "totalElements": 100,
  "totalPages": 5
}
```

**Erro (4xx/5xx):**
```json
{
  "timestamp": "2023-10-27T10:30:00Z",
  "status": 400,
  "error": "Validation Error",
  "message": "Campo 'amount' deve ser maior que zero",
  "path": "/api/v1/transactions/transfer"
}
```

### 15. Estratégia de autenticação e autorização

- **Mecanismo:** JWT (JSON Web Token) com assinatura HMAC256 ou RSA.
- **Fluxo:**
    1.  Usuário envia `email` e `password` para `/login`.
    2.  Servidor valida as credenciais, gera um **Access Token** (curta duração: 15-30 min) e um **Refresh Token** (longa duração: 7 dias) armazenado em *HttpOnly Cookie* ou no banco de dados.
    3.  O Access Token é enviado no header `Authorization: Bearer <token>`.
    4.  Um filtro de segurança intercepta as requisições, valida o token e carrega o contexto de autenticação.
- **Autorização:** Baseada em roles (`ROLE_USER`, `ROLE_ADMIN`). Endpoints administrativos exigem `ROLE_ADMIN`.
- **Segurança Extra:**
    - **Idempotência:** Utilização de `idempotencyKey` no header ou body para evitar duplicidade de transferências.
    - **CORS:** Configurado restritivamente para permitir apenas os domínios do frontend (web e mobile).
    - **Rate Limiting:** Limitação de requisições por IP/Usuário (ex: 10 transferências por minuto) utilizando Redis.

### 16. Possíveis melhorias futuras

1.  **Pix Integration:** Implementar integração com o Banco Central para transações instantâneas via chaves Pix (aleatória, CPF, e-mail, celular).
2.  **2FA (Two-Factor Authentication):** Adicionar verificação em duas etapas para login e transações sensíveis via e-mail ou app autenticador.
3.  **Microservices:** Separar o módulo de *Notificações* e *Processamento de Pagamentos* em serviços independentes utilizando Kafka para garantir resiliência.
4.  **Anti-Fraud:** Implementar um serviço de machine learning para detectar transações suspeitas (alto valor, localização inesperada).
5.  **Conta Conjunta:** Permitir que duas ou mais pessoas sejam titulares da mesma conta.
6.  **Conta PJ:** Criar uma hierarquia para Pessoa Jurídica com limites de saque/transferência diferentes.
7.  **Webhooks:** Permitir que parceiros (ex: e-commerces) recebam notificações de pagamento.
8.  **Deploy Multi-Região:** Distribuir réplicas do banco de dados e API em diferentes regiões para alta disponibilidade.

### 17. Nível do projeto

**Intermediário para Profissional.**

*   **Justificativa:** Embora a ideia inicial seja de um banco simples, a implementação exige conhecimentos sólidos de segurança (JWT, bcrypt), transações ACID, modelagem de banco de dados relacional, tratamento de concorrência (evitar double spending), e estratégias de idempotência. O uso de Redis para cache/rate-limit e a estrutura modular apontam para um nível que excede o estritamente iniciante, sendo adequado para um desenvolvedor Júnior avançado ou Pleno.
