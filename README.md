# Hospital Platform

Backend modular hospitalar para o Tech Challenge Fase 3, estruturado como um monorepo Java 21 + Spring Boot 3.x com PostgreSQL, RabbitMQ, JWT stateless, Flyway, REST para comandos e GraphQL restrito ao histórico.

## Arquitetura

- Monorepo para reduzir overhead operacional e simplificar avaliação local.
- PostgreSQL único com separação lógica por schema.
- `appointment-service` concentra autenticação, autorização RBAC e CRUD de consultas.
- `notification-service` processa eventos de forma assíncrona com idempotência e retry simples.
- `patient-history-service` mantém projeção local própria e expõe consultas flexíveis via GraphQL.
- `shared-kernel` contém somente contratos de eventos, enums globais e DTOs mínimos realmente necessários.

## Estrutura

```text
hospital-platform/
├── appointment-service
├── notification-service
├── patient-history-service
├── shared-kernel
├── docker-compose.yml
└── README.md
```

## Requisitos atendidos

- Java 21
- Spring Boot 3.x
- PostgreSQL
- RabbitMQ
- JWT stateless
- Docker Compose
- REST para autenticação e comandos
- GraphQL apenas no histórico
- Flyway SQL-first
- Scheduler simples com `@Scheduled`
- Actuator, healthchecks, logs estruturados e correlation-id

## Fluxos principais

1. Login via REST no `appointment-service`.
2. CRUD de consultas via REST no `appointment-service`.
3. Publicação de eventos versionados `v1.appointment.created` e `v1.appointment.updated`.
4. Consumo assíncrono no `notification-service`.
5. Projeção local e consulta GraphQL no `patient-history-service`.

## RBAC

- `ROLE_DOCTOR`
- `ROLE_NURSE`
- `ROLE_PATIENT`

Regras aplicadas:

- paciente acessa apenas seus próprios dados;
- médico pode visualizar e editar histórico;
- enfermeiro pode registrar consultas e visualizar histórico.

## Como executar

### Infraestrutura

```bash
copy .env.local.example .env.local
docker compose --env-file .env.local up -d postgres rabbitmq
```

Se quiser subir tudo em um comando, mantenha os valores locais em `.env.local` para evitar segredos inline no compose.

Quando trocar credenciais de banco ou broker, recrie os volumes locais para reaplicar usuários/senhas iniciais:

```bash
docker compose --env-file .env.local down -v
```

### Aplicações

```bash
mvn -pl appointment-service spring-boot:run
mvn -pl notification-service spring-boot:run
mvn -pl patient-history-service spring-boot:run
```

### Ou tudo via Compose

```bash
docker compose --env-file .env.local up --build
```

## Portas

- `appointment-service`: `8081`
- `notification-service`: `8082`
- `patient-history-service`: `8083`
- PostgreSQL: `5432`
- RabbitMQ management: `15672`

## Seeds de desenvolvimento

- Médico: `doctor1` / `doctor123`
- Enfermeiro: `nurse1` / `nurse123`
- Paciente: `patient1` / `patient123`

## Endpoints principais

- `POST /api/v1/auth/login`
- `POST /api/v1/appointments`
- `PUT /api/v1/appointments/{id}`
- `GET /api/v1/appointments/{id}`
- `GET /api/v1/appointments`
- `DELETE /api/v1/appointments/{id}`
- `POST /graphql`

## Validação e recuperação

- Se o Docker Compose falhar na primeira subida, verifique se `.env.local` existe e se o Docker Desktop está ativo.
- Se um token JWT for inválido ou expirado, a API responde com `401 Unauthorized`.
- O serviço de notificações limita tentativas de reprocessamento e encerra eventos recorrentes em estado `DEAD_LETTER`.
- O histórico GraphQL limita paginação a um tamanho máximo de 100 itens por página.

## Swagger

O Swagger/OpenAPI do `appointment-service` fica em `/swagger-ui.html`.

## GraphQL

Exemplo de consulta:

```graphql
query HistoryPage($patientId: ID!, $page: Int!, $size: Int!) {
	consultationHistory(filter: { patientId: $patientId }, page: $page, size: $size) {
		items {
			appointmentId
			patientId
			doctorId
			scheduledAt
			status
			notes
		}
		page
		size
		totalElements
	}
}
```

## Artefatos

- Arquitetura: [docs/architecture.md](docs/architecture.md)
- Modelagem: [docs/modeling.md](docs/modeling.md)
- GraphQL: [docs/graphql-examples.graphql](docs/graphql-examples.graphql)
- Postman: [docs/postman/hospital-platform.postman_collection.json](docs/postman/hospital-platform.postman_collection.json)
- Postman Environment: [docs/postman/hospital-platform.local.postman_environment.json](docs/postman/hospital-platform.local.postman_environment.json)

## Teste rápido com Postman

1. Suba o ambiente com `docker compose --env-file .env.local up -d --build`.
2. Importe a collection e o environment local da pasta `docs/postman`.
3. Selecione o environment `Hospital Platform Local`.
4. Execute a pasta `00 - Auth` para popular o token automaticamente.
5. Execute em sequência: `Appointments - Create`, `Appointments - List`, `Appointments - Get By Id`, `Appointments - Update` e `History - GraphQL`.

A collection já possui scripts de teste para validar status HTTP (200/201) e salvar `appointmentId` para os próximos requests.

## Observabilidade

Implementado apenas o necessário para o desafio:

- Spring Boot Actuator
- healthchecks
- logs estruturados com correlation-id

## Nota de escopo

A decisão mais sensível foi manter o histórico como projeção local consumida por eventos, em vez de consultar o write model do `appointment-service`. Isso reduz acoplamento e respeita a separação funcional pedida no documento norteador.