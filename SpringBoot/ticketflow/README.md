# 🎫 TicketFlow API

API REST desenvolvida em **Java + Spring Boot** para gerenciamento de tickets de suporte, com autenticação JWT, controle de usuários, comentários e permissões por papel (ROLE).

Este projeto foi criado com foco em **boas práticas de arquitetura**, separação em camadas (Controller, Service, Repository, DTO), e uso de **Spring Security + JWT**.

---

## 🚀 Tecnologias utilizadas

* Java 17+
* Spring Boot
* Spring Web
* Spring Data JPA
* Spring Security
* JWT (Json Web Token)
* Maven
* H2 / MySQL (configurável)
* Swagger / OpenAPI
* Lombok

---

## 📂 Estrutura do projeto

```
src/main/java/com/wesleyruam/ticketflow

Config/
controller/
dto/
model/
repository/
security/
service/
setup/
```

### Camadas

* **Controller** → Endpoints REST
* **Service** → Regras de negócio
* **Repository** → Acesso ao banco
* **DTO** → Transferência de dados
* **Model** → Entidades JPA
* **Security** → JWT / Roles / Permissões
* **Config** → CORS / configs
* **Setup** → Inicialização de dados

---

## 🔐 Autenticação

A API usa **JWT Token**

Endpoint:

```
POST /api/auth/login
```

Request:

```json
{
  "email": "admin@email.com",
  "password": "123456"
}
```

Response:

```json
{
  "success": true,
  "data": {
    "token": "JWT_TOKEN",
    "userId": 1,
    "name": "Admin",
    "role": "ADMIN"
  }
}
```

Enviar token:

```
Authorization: Bearer TOKEN
```

---

## 👤 Usuários

### Criar usuário

```
POST /api/user
```

### Listar usuários

```
GET /api/user
```

### Buscar por id

```
GET /api/user/{id}
```

### Atualizar

```
PUT /api/user/{id}
```

### Deletar

```
DELETE /api/user/{id}
```

Roles:

* ADMIN
* MANAGER
* USER

---

## 🎫 Tickets

### Criar ticket

```
POST /api/ticket
```

### Listar tickets

```
GET /api/ticket
```

### Buscar por id

```
GET /api/ticket/{id}
```

### Atualizar

```
PUT /api/ticket/{id}
```

### Deletar

```
DELETE /api/ticket/{id}
```

Status:

* ABERTO
* EM_ANDAMENTO
* FECHADO
* CANCELADO

Prioridade:

* ALTA
* MEDIA
* BAIXA

---

## 💬 Comentários

### Criar comentário

```
POST /api/comment
```

### Buscar comentário

```
GET /api/comment/{id}
```

### Atualizar

```
PUT /api/comment/{id}
```

### Deletar

```
DELETE /api/comment/{id}
```

### Listar comentários de um ticket

```
GET /api/ticket/{ticketId}/comments
```

---

## ⚙️ Como rodar o projeto

### 1. Clonar

```
git clone https://github.com/seu-usuario/ticketflow.git
```

### 2. Entrar na pasta

```
cd ticketflow
```

### 3. Rodar

```
./mvnw spring-boot:run
```

ou

```
mvn spring-boot:run
```

---

## 🌐 URL da API

```
http://localhost:8080
```

Swagger:

```
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON:

```
http://localhost:8080/v3/api-docs
```

---

## 🔑 Permissões

Sistema possui controle por ROLE:

| Role    | Permissões               |
| ------- | ------------------------ |
| ADMIN   | Tudo                     |
| MANAGER | Gerenciar tickets        |
| USER    | Criar tickets / comentar |

---

## 🧠 Arquitetura usada

* DTO Pattern
* Service Layer Pattern
* Repository Pattern
* JWT Authentication
* Role Based Authorization
* Clean Controller
* Response Wrapper (ServiceResponse)

---

## 📌 Objetivo do projeto

Projeto criado para estudo e portfólio com foco em:

* Backend profissional
* Spring Boot avançado
* Segurança com JWT
* Organização de código
* API REST real

---

## 👨‍💻 Autor

Wesley Ruan

GitHub:
https://github.com/wesleyruam

---
