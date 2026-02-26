Pedi para o ChatGPT me dizer um projeto fácil, porém funcional para eu fazer em Java utilizando Spring Boot.
Não pedi nenhum tipo de código, apenas a ideia de projeto básico.

---

## 📚 Projeto: API de Gerenciamento de Tarefas (Task Manager)

### 🎯 Objetivo

Criar uma API REST que permita cadastrar, listar, atualizar e excluir tarefas.

Esse é um projeto simples, mas ensina praticamente tudo que você precisa no início com Spring Boot.

---

## 🧱 Entidades principais

### 📝 Tarefa (Task)

Campos sugeridos:

* **id** → identificador único
* **titulo** → nome da tarefa
* **descricao** → detalhes da tarefa
* **status** → exemplo: PENDENTE, EM_ANDAMENTO, CONCLUIDA
* **dataCriacao**
* **dataConclusao** (opcional)

---

## ⚙️ Funcionalidades (Endpoints)

Você deve implementar:

### 1. Criar tarefa

* Recebe título e descrição
* Define status inicial como PENDENTE

### 2. Listar todas as tarefas

* Retorna todas as tarefas cadastradas

### 3. Buscar tarefa por ID

* Retorna uma tarefa específica

### 4. Atualizar tarefa

* Permite alterar título, descrição ou status

### 5. Deletar tarefa

* Remove a tarefa do sistema

---

## 🧩 Componentes do Spring Boot que você vai usar

Você vai trabalhar com:

* Controller → recebe requisições HTTP
* Service → lógica de negócio
* Repository → acesso ao banco
* Entity → representa a tabela
* DTO (opcional, mas recomendado)

---

## 🗄️ Banco de dados -> Vou tentar utilizar sessões, creio que no momento seja mais fácil a implementação de Sessões do que um DB.

Você pode usar:

* H2 (mais fácil, recomendado para começar)
  ou
* PostgreSQL / MySQL

---

## 🌐 Exemplos de rotas

* POST `/tarefas`
* GET `/tarefas`
* GET `/tarefas/{id}`
* PUT `/tarefas/{id}`
* DELETE `/tarefas/{id}`
