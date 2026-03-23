Perfeito. Vamos atuar como uma equipe de desenvolvimento de uma empresa de tecnologia para projetar este sistema, que chamaremos de **"Elysium"** , um nome que remete a um espaço exclusivo e de conexão.

Aqui está o projeto completo, seguindo todas as suas exigências e pensando em escalabilidade, segurança e boas práticas de mercado.

---

### 1. Descrição do sistema
O **Elysium** é uma plataforma digital (Web App / PWA) que conecta Clientes (Contratantes) e Acompanhantes (Profissionais) de forma geolocalizada, segura e discreta. A plataforma opera sob um modelo de match baseado em disponibilidade em tempo real, onde o Cliente visualiza as profissionais em um mapa, envia uma solicitação de encontro, e a profissional tem o poder de aceitar ou recusar com base nas informações detalhadas do perfil do Cliente.

### 2. Objetivo do projeto
Criar um ecossistema que priorize a segurança, o consenso e a transparência na conexão entre clientes e acompanhantes, oferecendo:
- **Para o Cliente:** Visibilidade clara das profissionais ativas, transparência de serviços e preços, e agilidade na conexão.
- **Para a Acompanhante:** Controle total sobre quais clientes aceitar, filtragem por perfil e localização, e ferramentas de segurança (check-in/check-out, botão de pânico).

### 3. Requisitos funcionais
1.  **Gestão de Cadastro e Perfil:**
    *   Cadastro de usuários com dois tipos distintos: `CLIENTE` e `ACOMPANHANTE`.
    *   Upload de fotos (múltiplas) com moderação inicial (ou por IA) para evitar conteúdo proibido.
    *   Configuração detalhada de preferências e atributos específicos para cada perfil.
2.  **Geolocalização:**
    *   Visualização de mapa interativo com a localização das acompanhantes disponíveis.
    *   Atualização da localização em tempo real (via GPS) enquanto a profissional estiver "Online".
3.  **Match e Solicitação:**
    *   Cliente seleciona uma acompanhante no mapa e envia uma "Solicitação de Encontro".
    *   Acompanhante recebe uma notificação push com os dados do Cliente (perfil resumido).
    *   Acompanhante pode **Aceitar** ou **Recusar** a solicitação.
4.  **Chat e Negociação:**
    *   Chat em tempo real liberado *após* a aceitação da solicitação.
    *   Possibilidade de envio de mídia (fotos/texto) dentro do chat.
5.  **Segurança e Verificação:**
    *   Botão de pânico (envia localização para contato de emergência cadastrado).
    *   Sistema de check-in e check-out do encontro (temporizador).
    *   Avaliação mútua após o encontro (feedback apenas textual, sem estrelas para evitar viés negativo).

### 4. Requisitos não funcionais
1.  **Performance:** O mapa e a notificação push devem ter latência inferior a 2 segundos.
2.  **Disponibilidade:** Sistema deve operar com 99.5% de uptime, priorizando estabilidade em horários de pico (fins de semana, noites).
3.  **Segurança:**
    *   Criptografia de ponta a ponta (E2EE) no chat.
    *   Geolocalização não pode ser exposta a terceiros; apenas durante o match.
    *   Ocultação automática de localização quando o app estiver em background ou offline.
4.  **Escalabilidade:** Capacidade de suportar de 10k a 100k usuários simultâneos, escalando horizontalmente.
5.  **Privacidade:** O sistema não deve armazenar logs de conversas após 30 dias de inatividade do match.
6.  **Compatibilidade:** Funcionar como PWA (Progressive Web App) para Android/iOS e versão Web Desktop.

### 5. Regras de negócio
1.  **Disponibilidade:** Apenas acompanhantes com status "Online" e "Disponível" aparecem no mapa dos clientes.
2.  **Idade Mínima:** Todos os usuários devem ter mais de 18 anos. Verificação por documento (selfie com documento) é obrigatória para se tornar uma acompanhante ativa.
3.  **Consentimento Explícito:** A transação (endereço, tempo) só pode ser combinada via chat após o match. A plataforma não realiza pagamentos (apenas recomenda métodos seguros, mas não gerencia transações financeiras para evitar classificação como serviço de pagamento ou lenocínio).
4.  **Rate Limiting:** Clientes não podem enviar mais de 10 solicitações por hora para evitar spam.
5.  **Moderação:** Perfis sem foto de verificação (selfie segurando um código) não ficam visíveis.

### 6. Arquitetura do sistema
**Arquitetura: Microservices (Híbrido)**
Para garantir escalabilidade e separação de responsabilidades, utilizaremos microservices, mas com uma API Gateway unificada.

*   **API Gateway:** Ponto único de entrada (Kong ou Nginx).
*   **Service de Usuários:** Gerencia cadastro, perfis, autenticação.
*   **Service de Geolocalização:** Gerencia posições em tempo real (WebSockets), atualização de mapa. (Alta carga).
*   **Service de Match:** Lida com solicitações, aceitações e status de encontro.
*   **Service de Chat:** Serviço independente (WebSocket) para mensagens em tempo real.
*   **Service de Notificação:** Gerencia envio de Push Notifications (Firebase FCM / APNS).
*   **Message Broker:** Apache Kafka para comunicação assíncrona entre serviços (ex: quando um match é aceito, dispara criação de chat).
*   **Banco de Dados:** PostgresSQL (relacional) para dados mestres. Redis (Cache/Geolocalização/Sessions). MongoDB (para logs de chat arquivados).

### 7. Tecnologias recomendadas
*   **Backend:** Node.js (NestJS) - para melhor performance I/O e facilidade de manutenção com TypeScript.
*   **Geolocalização/WebSocket:** Socket.io ou uWebSockets.
*   **Banco de Dados:** PostgreSQL (via Prisma ou TypeORM), Redis (Geospatial indexing - `GEOADD`), MongoDB (logs).
*   **Infra:** Docker, Kubernetes (GKE/EKS), GitHub Actions (CI/CD).
*   **Armazenamento:** AWS S3 / Google Cloud Storage para fotos.
*   **Mapas:** Mapbox ou Google Maps Platform (SDK Web e Mobile).
*   **Frontend:** React.js (Web) e React Native (PWA ou App Wrapper).

### 8. Entidades do sistema com atributos

**User (Base)**
*   `id`: UUID
*   `email`: String (único)
*   `phone`: String (único)
*   `password_hash`: String
*   `role`: Enum [CLIENT, COMPANION]
*   `is_verified`: Boolean (verificação de identidade)
*   `is_active`: Boolean (banido ou não)
*   `created_at`: Timestamp

**ClientProfile**
*   `id`: UUID (FK para User)
*   `name`: String (nome visível)
*   `birth_date`: Date
*   `gender`: Enum [MASC, FEM, NB, OTHER]
*   `height_cm`: Int
*   `penis_size_cm`: Int
*   `sexual_roles`: Array[Enum] (ex: [ACTIVE, PASSIVE, FLEX])
*   `bio`: String
*   `profile_picture_url`: String

**CompanionProfile**
*   `id`: UUID (FK para User)
*   `name`: String (nome artístico)
*   `birth_date`: Date
*   `height_cm`: Int
*   `weight_kg`: Int
*   `services`: Array[Enum] (ex: [ORAL, ANAL, VAGINAL, MASSAGE])
*   `kinks`: Array[Enum] (ex: [BDSM, ROLEPLAY, LIGHT_SM])
*   `restrictions`: String (Text) - "Não aceito fumantes", "Local seguro obrigatório"
*   `hourly_rate`: Decimal
*   `is_online`: Boolean

**Photo**
*   `id`: UUID
*   `user_id`: UUID
*   `url`: String
*   `is_verified`: Boolean (foto de verificação)

**Location (Redis/DB)**
*   `user_id`: UUID (index GEO)
*   `latitude`: Double
*   `longitude`: Double
*   `updated_at`: Timestamp

**Request (Match)**
*   `id`: UUID
*   `client_id`: UUID
*   `companion_id`: UUID
*   `status`: Enum [PENDING, ACCEPTED, REJECTED, EXPIRED, COMPLETED, CANCELLED]
*   `message_initial`: String
*   `created_at`: Timestamp
*   `responded_at`: Timestamp

**Conversation**
*   `id`: UUID
*   `request_id`: UUID (único)
*   `created_at`: Timestamp

**Message**
*   `id`: UUID
*   `conversation_id`: UUID
*   `sender_id`: UUID
*   `content`: String (criptografada)
*   `type`: Enum [TEXT, IMAGE]
*   `sent_at`: Timestamp

**SecurityCheck**
*   `id`: UUID
*   `request_id`: UUID
*   `client_checkin_at`: Timestamp (cliente avisa que chegou)
*   `companion_checkin_at`: Timestamp
*   `emergency_triggered`: Boolean
*   `scheduled_end`: Timestamp

### 9. Relacionamentos entre entidades
*   **User** (1) ---- (1) **ClientProfile** (ou **CompanionProfile**)
*   **User** (1) ---- (N) **Photo**
*   **User** (1) ---- (1) **Location** (atualização constante)
*   **ClientProfile** (1) ---- (N) **Request** (como solicitante)
*   **CompanionProfile** (1) ---- (N) **Request** (como solicitado)
*   **Request** (1) ---- (1) **Conversation**
*   **Conversation** (1) ---- (N) **Message**
*   **Request** (1) ---- (1) **SecurityCheck**

### 10. Endpoints da API (REST)

**Auth**
*   `POST /auth/register` - Cadastro inicial
*   `POST /auth/login` - Login
*   `POST /auth/refresh-token` - Refresh Token
*   `POST /auth/verify-document` - Envio de documento para verificação

**Profile**
*   `GET /profile` - Obter perfil do logado
*   `PATCH /profile` - Atualizar perfil base
*   `PUT /profile/client` - Atualizar perfil específico de cliente
*   `PUT /profile/companion` - Atualizar perfil de acompanhante
*   `POST /profile/photos` - Upload de fotos

**Mapa e Geolocalização**
*   `GET /map/companions` - Lista acompanhantes próximas (latitude/longitude + raio) (retorna dados para plotagem no mapa)
*   `POST /location` - Atualiza localização atual
*   `PATCH /companion/status` - Alterna status Online/Offline

**Solicitações (Match)**
*   `POST /requests` - Cliente envia solicitação para acompanhante (body: companionId, initialMessage)
*   `GET /requests/sent` - Cliente vê solicitações enviadas
*   `GET /requests/received` - Acompanhante vê solicitações recebidas
*   `PATCH /requests/{id}/accept` - Acompanhante aceita
*   `PATCH /requests/{id}/reject` - Acompanhante recusa
*   `PATCH /requests/{id}/cancel` - Cliente ou Acompanhante cancela

**Chat (WebSocket)**
*   `WS /chat/{conversation_id}` - Conexão WebSocket para troca de mensagens em tempo real.

**Segurança**
*   `POST /security/checkin` - Realiza check-in do encontro
*   `POST /security/panic` - Aciona botão de pânico (envia SMS/localização)

### 11. Formato de entrada (Request DTO)

**POST /auth/register**
```json
{
  "email": "user@example.com",
  "phone": "+5511999999999",
  "password": "SecurePass123!",
  "role": "COMPANION",
  "birth_date": "1995-05-20",
  "name": "Isabella"
}
```

**POST /requests (Cliente)**
```json
{
  "companion_id": "uuid-da-acompanhante",
  "initial_message": "Olá, gostaria de saber sua disponibilidade para as 20h.",
  "location_lat": -23.5505,
  "location_lng": -46.6333
}
```

**PUT /profile/companion (Acompanhante)**
```json
{
  "name": "Isabella Fox",
  "height_cm": 165,
  "weight_kg": 55,
  "services": ["ORAL", "ANAL", "VAGINAL"],
  "kinks": ["BDSM", "LIGHT_SM"],
  "restrictions": "Não fumo, local com estacionamento obrigatório.",
  "hourly_rate": 250.00
}
```

### 12. Formato de saída (response DTO)

**GET /map/companions (Padrão Mapa)**
```json
{
  "companions": [
    {
      "id": "uuid",
      "name": "Isabella Fox",
      "profile_picture_thumb": "url_thumb",
      "distance_meters": 850,
      "location": { "lat": -23.5505, "lng": -46.6333 },
      "is_online": true,
      "hourly_rate": 250.00
    }
  ]
}
```

**GET /requests/received (Para Acompanhante)**
```json
{
  "requests": [
    {
      "id": "req-uuid",
      "status": "PENDING",
      "created_at": "2023-10-01T20:00:00Z",
      "client": {
        "id": "client-uuid",
        "name": "João",
        "age": 28,
        "profile_picture": "url",
        "height_cm": 175,
        "penis_size_cm": 16,
        "sexual_roles": ["ACTIVE"]
      }
    }
  ]
}
```

### 13. Estrutura de pastas do backend (NestJS)
```
backend/
├── apps/
│   ├── api-gateway/        # Proxy reverso e roteamento
│   └── services/
│       ├── auth-service/   # Autenticação e Users
│       ├── profile-service/ # Gerenciamento de perfis
│       ├── geo-service/     # Mapa e localização (WebSocket)
│       ├── match-service/   # Lógica de solicitações e status
│       ├── chat-service/    # WebSocket e mensagens
│       └── notification-service/ # Push notifications
├── libs/
│   ├── common/             # DTOs, interfaces, enums
│   ├── database/           # Prisma/TypeORM entities
│   └── security/           # JWT, Guards, Criptografia
├── docker-compose.yml
└── package.json
```

### 14. Padrão de resposta da API
Todas as respostas seguirão o padrão JSON API:

*   **Sucesso:**
    ```json
    {
      "statusCode": 200,
      "success": true,
      "data": { ... },
      "timestamp": "2023-10-01T20:00:00Z"
    }
    ```
*   **Erro:**
    ```json
    {
      "statusCode": 400,
      "success": false,
      "error": "Bad Request",
      "message": "Acompanhante não está disponível no momento.",
      "timestamp": "2023-10-01T20:00:00Z"
    }
    ```
*   **Lista Paginada:**
    ```json
    {
      "statusCode": 200,
      "success": true,
      "data": [ ... ],
      "meta": {
        "page": 1,
        "limit": 20,
        "total": 150,
        "totalPages": 8
      }
    }
    ```

### 15. Estratégia de autenticação e autorização
*   **Autenticação:** OAuth 2.0 / JWT (JSON Web Tokens) com tokens de curta duração (15 minutos) e refresh tokens (7 dias) armazenados em HTTP-only cookies (para web) ou Secure Storage (mobile). Uso de `passport-jwt`.
*   **Autorização:** RBAC (Role-Based Access Control).
    *   `ROLE_CLIENT`: Pode acessar endpoints de mapa, enviar solicitações, cancelar, chat.
    *   `ROLE_COMPANION`: Pode acessar endpoints de recebimento de solicitações, aceitar/recusar, atualizar status online/offline.
    *   Middleware verifica se o ID do recurso pertence ao usuário logado (ex: só pode ver seu próprio perfil).

### 16. Possíveis melhorias futuras
1.  **Sistema de Pagamento Integrado:** Implementar uma carteira digital (escrow) para garantir segurança financeira, com taxa de serviço para a plataforma (tornando o modelo de negócio viável).
2.  **Inteligência Artificial (IA):**
    *   Verificação de fotos automática para evitar duplicatas ou conteúdo proibido.
    *   Sistema de recomendação de acompanhantes baseado no histórico de preferências do cliente.
3.  **Modo "Incógnito":** Para clientes com alta privacidade, permitir navegação no mapa sem ser visto por outras acompanhantes até enviar a solicitação.
4.  **Agendamento:** Permitir agendamento futuro (calendário) para acompanhantes muito requisitadas.
5.  **Verificação de antecedentes:** Integração com APIs de verificação de identidade para aumentar a segurança.
6.  **Bot de Moderação:** Para detectar automaticamente linguagem ofensiva ou tentativas de negociação fora da plataforma.

### 17. Nível do projeto
**Profissional**

Este projeto é classificado como **Profissional** devido a:
*   **Complexidade de Domínio:** Regras de negócio sensíveis, exigindo forte compliance legal e de segurança.
*   **Arquitetura:** Uso de microservices, WebSockets (baixa latência), geolocalização em tempo real e mensageria assíncrona.
*   **Segurança:** Necessidade de criptografia de ponta a ponta, gestão de tokens, anonimização de dados e recursos de emergência (botão de pânico).
*   **Escalabilidade:** Requer planejamento para lidar com picos de demanda (finais de semana) e infraestrutura cloud robusta.
