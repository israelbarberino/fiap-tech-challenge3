# Revisão Arquitetural e de Qualidade

## Aderência ao norteador ADJT/Tech Challenge Fase 3

- Monorepo com microsserviços leves: atendido.
- Spring Boot + modularização backend: atendido.
- Segurança com RBAC por perfil: atendido em `appointment-service` e `patient-history-service`.
- Comunicação assíncrona com RabbitMQ para notificações e projeções: atendido.
- GraphQL restrito ao histórico: atendido.
- Postman e documentação técnica: atendido.

## Checagem de arquitetura

- Baixo acoplamento entre serviços: cada serviço mantém schema próprio e integra por eventos.
- Alta coesão por responsabilidade: scheduling/auth, notification, history.
- Shared-kernel mínimo: contém contratos de evento e enum global, sem lógica de domínio.
- Estratégia de consistência: eventual consistency para histórico/notificação e consistência imediata para CRUD transacional.

## Code smells observados e ações

- Smell: ausência de testes de integração para mensageria e GraphQL.
  - Ação: adicionados testes de integração para JPA, RabbitMQ e GraphQL.
- Smell: artefatos operacionais dispersos.
  - Ação: adicionados `.env.local.example`, ambiente Postman local e smoke tests `curl`.
- Smell: scanner aponta risco em imagens base.
  - Ação: runtime migrado para distroless não-root para reduzir superfície de ataque.

## GoF / System Design: viabilidade e necessidade

### Decisão

- Não há necessidade de introduzir padrões GoF adicionais neste estágio.

### Justificativa

- O domínio atual é simples e as regras principais já estão claras no fluxo `Controller -> Service -> Repository`.
- Introduzir Strategy/Factory/Chain adicionais agora aumentaria complexidade sem ganho arquitetural concreto.
- O desafio pede pragmatismo e evita overengineering; portanto, manter estrutura direta é a escolha correta.

### Onde GoF poderia ser aplicado futuramente (se houver crescimento real)

- Strategy para múltiplos canais de notificação (e-mail, SMS, push).
- Factory para construção de payloads de eventos por versão.
- Template Method para pipelines de validação mais complexos.

No momento, a recomendação é **não aplicar GoF extra** além dos padrões já nativos do ecossistema Spring.
