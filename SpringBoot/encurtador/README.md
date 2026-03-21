# Encurtador de URLs

Um projeto em **Java + Spring Boot** que cria URLs curtas a partir de URLs longas e permite redirecionamento rápido. Ideal para aprender Spring Boot, JPA, H2 e manipulação de HTTP.

---

## Funcionalidades

* Gerar URL curta a partir de uma URL longa.
* Redirecionar usuários para a URL original usando a URL curta.
* Validação básica de URLs.
* Armazenamento em banco de dados **H2 (em memória)**.

---

## Tecnologias

* Java 17
* Spring Boot 4
* Spring Data JPA
* H2 Database (em memória)
* Maven

---

## Endpoints

### Gerar URL curta

```
GET /shortenUrl?url=<URL_ORIGINAL>
```

**Exemplo:**

```bash
curl --location 'http://127.0.0.1:8080/shortenUrl?url=https://google.com'
```

**Resposta:**

```json
{
  "id": 3,
  "codeUrl": "430bd7",
  "originalUrl": "https://google.com/",
  "newUrl": "http://127.0.0.1:8080/r/430bd7"
}
```

---

### Redirecionar para URL original

```
GET /r/{code}
```

**Exemplo:**

```bash
curl -v http://127.0.0.1:8080/r/430bd7
```

**Resposta:**

* Status HTTP: `302 Found`
* Header `Location`: `https://google.com/`

O navegador ou cliente HTTP redireciona automaticamente para a URL original.

---

## Estrutura do Projeto

```
src/main/java/com/wesleyruan/encurtador/
├─ controller/
│  └─ UrlController.java       # Endpoints REST
├─ service/
│  └─ UrlService.java          # Lógica de negócio
├─ repository/
│  └─ UrlRepository.java       # Comunicação com o banco
└─ model/
   └─ UrlModel.java            # Entidade JPA
```

3. Acesse os endpoints via navegador ou `curl`.

