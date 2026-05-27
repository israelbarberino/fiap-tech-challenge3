# RELATORIO FINAL ACADEMICO

## Capa

INSTITUICAO: FIAP

CURSO: Tech Challenge - Backend Hospitalar Modular

DISCIPLINA: Tech Challenge Fase 3

AUTORES: Equipe do projeto

TITULO: Plataforma Backend Hospitalar Modular com Java 21, Spring Boot, PostgreSQL, RabbitMQ e GraphQL

CIDADE: Sao Paulo

ANO: 2026

## Folha de Rosto

Plataforma Backend Hospitalar Modular com Java 21, Spring Boot, PostgreSQL, RabbitMQ e GraphQL.

Relatorio tecnico-academico apresentado a disciplina Tech Challenge Fase 3, da FIAP, como requisito para avaliacao.

Orientador(a): Professor(a) da disciplina Tech Challenge Fase 3

Local e data: Sao Paulo, 2026

## Resumo

Este relatorio apresenta o desenvolvimento de uma plataforma backend hospitalar modular, concebida para atender aos requisitos do Tech Challenge Fase 3 com enfase em pragmatismo arquitetural, simplicidade operacional e consistencia tecnica. A solucao foi implementada como monorepo Java 21 com Spring Boot 3.4.1, PostgreSQL, Flyway, RabbitMQ, autenticacao JWT stateless e GraphQL aplicado ao dominio de historico clinico. A estrategia de construcao priorizou baixo acoplamento entre servicos, alta coesao funcional e evitacao de sobreengenharia.

A arquitetura foi estruturada em quatro modulos: shared-kernel, appointment-service, notification-service e patient-history-service. O appointment-service concentra autenticacao, autorizacao por papeis e operacoes transacionais de consultas. O notification-service processa eventos assincronamente com idempotencia e retry. O patient-history-service mantem projecao local orientada a eventos e expoe consulta paginada via GraphQL. A persistencia adota banco unico com separacao logica por schemas, decisao alinhada ao escopo academico e a necessidade de reprodutibilidade local.

A validacao contemplou testes unitarios e de integracao, evidencias operacionais via Docker Compose, healthchecks, fluxo fim a fim com autenticacao e manipulacao de consultas, alem de consulta GraphQL funcional do historico. Os resultados indicam aderencia ao norteador da fase, robustez tecnica para o contexto proposto e base evolutiva para endurecimento em ambientes produtivos.

As validacoes atuais tambem consideram limites praticos do ambiente Windows local, especialmente a dependencia de Docker/Testcontainers para algumas suites de integracao. Quando Docker nao esta disponivel, os testes de integracao dependentes de containers sao reportados como indisponiveis, mas os testes unitarios e a validacao de build continuam executaveis.

Palavras-chave: arquitetura modular; microsservicos; backend; Java; Spring Boot; JWT; RabbitMQ; GraphQL; Docker.

## Abstract

This report presents the development of a modular hospital backend platform designed to meet Tech Challenge Phase 3 requirements with emphasis on architectural pragmatism, operational simplicity, and technical consistency. The solution was implemented as a Java 21 monorepo using Spring Boot 3.4.1, PostgreSQL, Flyway, RabbitMQ, stateless JWT authentication, and GraphQL for the clinical history domain. The implementation strategy prioritized low coupling, high cohesion, and avoidance of overengineering.

The architecture is organized into four modules: shared-kernel, appointment-service, notification-service, and patient-history-service. The appointment-service centralizes authentication, role-based authorization, and transactional appointment operations. The notification-service processes events asynchronously with idempotency and retry. The patient-history-service maintains an event-driven local projection and exposes paginated GraphQL queries. Persistence uses a single PostgreSQL instance with logical schema separation, a decision aligned with academic scope and local reproducibility.

Validation covered unit and integration tests, operational evidence through Docker Compose, health checks, end-to-end flows for authentication and appointment operations, and functional GraphQL history queries. Results indicate adherence to phase guidelines, technical robustness for the intended context, and a solid foundation for production-oriented hardening.

Keywords: modular architecture; microservices; backend; Java; Spring Boot; JWT; RabbitMQ; GraphQL; Docker.

## Lista de Abreviaturas e Siglas

API - Application Programming Interface

CI/CD - Continuous Integration / Continuous Delivery

CSRF - Cross-Site Request Forgery

DTO - Data Transfer Object

JWT - JSON Web Token

RBAC - Role-Based Access Control

REST - Representational State Transfer

SQL - Structured Query Language

## Sumario

1 Introducao

2 Objetivos

3 Delimitacao do problema e escopo

4 Metodologia de desenvolvimento

5 Fundamentacao tecnica e arquitetural

6 Arquitetura da solucao

7 Modelagem de dominio e responsabilidades

8 Seguranca e controle de acesso

9 Persistencia e versionamento de banco

10 Integracao assincrona por eventos

11 Consulta de historico com GraphQL

12 Estrategia de testes e validacao

13 Resultados e evidencias

14 Correcao de findings e melhoria de qualidade

15 Trade-offs arquiteturais

16 Limitacoes e riscos conhecidos

17 Propostas de evolucao

18 Conclusao

Referencias

Apendice A - Prompt academico estruturado

## 1 Introducao

A area da saude exige sistemas que conciliem confiabilidade, seguranca, rastreabilidade e capacidade de evolucao. Em cenarios hospitalares, o aumento de complexidade funcional tende a pressionar arquiteturas monoliticas, elevando acoplamento e dificultando manutencao de longo prazo. Diante desse contexto, este trabalho propoe uma plataforma backend modular como resposta pragmatica a necessidade de organizar responsabilidades de negocio com fronteiras claras.

No escopo do Tech Challenge Fase 3, o foco nao foi apenas implementar funcionalidades isoladas, mas construir uma base tecnica coerente, testavel e justificavel do ponto de vista de engenharia de software. A solucao buscou equilibrar dois objetivos frequentemente conflitantes em ambientes academicos: densidade tecnica suficiente para demonstrar maturidade arquitetural e simplicidade suficiente para viabilizar execucao, validacao e defesa do projeto sem dependencia de infraestrutura complexa.

## 2 Objetivos

### 2.1 Objetivo geral

Desenvolver e validar um backend hospitalar modular, com autenticacao stateless, persistencia relacional versionada, comunicacao assincrona por eventos e consulta flexivel de historico clinico.

### 2.2 Objetivos especificos

- Implementar autenticacao e autorizacao por papeis clinicos.
- Disponibilizar operacoes de consultas medicas por API REST.
- Publicar eventos versionados de criacao e atualizacao de consultas.
- Consumir eventos de forma assincrona para notificacao e projecao de historico.
- Expor consulta de historico via GraphQL com filtros e paginacao.
- Garantir reproducibilidade local por meio de Docker Compose.
- Validar comportamento funcional e tecnico com testes automatizados e evidencias fim a fim.

## 3 Delimitacao do problema e escopo

O problema central abordado foi organizar um backend hospitalar com multiplas responsabilidades funcionais sem comprometer clareza estrutural. A proposta exigiu separar dominios de escrita, notificacao e leitura historica, mantendo interoperabilidade por eventos.

O escopo cobriu autenticacao, RBAC, CRUD de consultas, publicacao e consumo de eventos, projecao de historico, consulta GraphQL, testes e documentacao tecnica. Permaneceram fora de escopo: hardening completo para producao, governanca corporativa de segredos, observabilidade distribuida em nivel enterprise, pipeline CI/CD multietapas e segregacao fisica de banco por servico.

## 4 Metodologia de desenvolvimento

A metodologia adotada foi incremental e orientada por validacao continua. Cada bloco funcional foi implementado com ciclos curtos de codificacao, teste e verificacao operacional. Essa abordagem reduziu risco de regressao tardia e permitiu incorporar ajustes com base em feedback tecnico.

Do ponto de vista de engenharia, o processo combinou:

- implementacao guiada por contratos de API e eventos;
- uso de migracoes Flyway como fonte de verdade estrutural;
- verificacao por testes unitarios e integracoes;
- validacao operacional com containers e smoke tests;
- revisao de findings para tratar riscos reais de corretude e consistencia.

## 5 Fundamentacao tecnica e arquitetural

A escolha por Java 21 e Spring Boot 3.4.1 foi sustentada pela maturidade do ecossistema, robustez de bibliotecas e produtividade de desenvolvimento. PostgreSQL foi selecionado pela confiabilidade transacional e ampla integracao com JPA/Flyway. RabbitMQ foi adotado para desacoplar produtor e consumidores no fluxo de eventos. JWT stateless permitiu controle de acesso sem estado de sessao no servidor. GraphQL foi empregado exclusivamente no historico por oferecer flexibilidade de consulta orientada ao consumidor.

A combinacao dessas tecnologias refletiu uma decisao de equilibrio: evitar tecnologia em excesso e, ao mesmo tempo, demonstrar padroes arquiteturais modernos com criterio de aplicacao.

## 6 Arquitetura da solucao

A solucao foi organizada em monorepo Maven multi-modulo. Essa estrategia favoreceu padronizacao de build, versoes e convencoes, reduzindo custo de coordenacao no contexto academico.

### 6.1 Modulos e papeis

- shared-kernel: contratos de eventos e tipos compartilhados minimos.
- appointment-service: autenticacao, RBAC e operacoes de consulta.
- notification-service: processamento assincrono de eventos e controle de entrega.
- patient-history-service: projecao local para leitura historica e endpoint GraphQL.

### 6.2 Fluxo principal

1. Usuario autentica no appointment-service.
2. Operacao de consulta gera alteracao de estado transacional.
3. Evento de dominio e publicado no exchange de integracao.
4. notification-service e patient-history-service consomem o evento.
5. Historico e consultado via GraphQL com filtros e paginacao.

Esse desenho promove separacao entre comandos e consultas, reduz acoplamento sincrono e sustenta evolucao de consumidores sem impacto direto no servico de escrita.

## 7 Modelagem de dominio e responsabilidades

O modelo de dominio foi delimitado por responsabilidades claras. O appointment-service concentra regras de negocio associadas a consulta medica. O patient-history-service trabalha com projecao eventual para consulta. O notification-service armazena tentativa, status e reprocessamento de eventos.

No controle de acesso, os papeis ROLE_DOCTOR, ROLE_NURSE e ROLE_PATIENT traduzem o requisito de seguranca por perfil assistencial. Essa modelagem foi implementada com foco em restricao de escopo funcional por ator, preservando o principio do menor privilegio no que coube ao desafio.

## 8 Seguranca e controle de acesso

A autenticacao foi implementada com JWT stateless, com emissao de token em endpoint dedicado e validacao em filtros de seguranca. A autorizacao por RBAC foi aplicada nos pontos de acesso sensiveis.

A exposicao de endpoints de healthcheck sem autenticacao foi mantida por necessidade operacional de monitoramento. O CSRF foi desabilitado por compatibilidade com a natureza stateless das APIs e uso de token bearer.

## 9 Persistencia e versionamento de banco

A camada de dados utiliza PostgreSQL com versionamento Flyway em abordagem SQL-first. Cada servico opera em schema proprio, preservando fronteira logica e facilitando rastreabilidade de alteracoes.

A decisao por banco unico com schemas separados foi pragmatica: reduz complexidade de infraestrutura local e preserva separacao funcional suficiente para o escopo da fase. Tambem foi incorporada a dependencia flyway-database-postgresql para compatibilidade com a versao de banco utilizada.

## 10 Integracao assincrona por eventos

A comunicacao entre servicos foi estruturada com RabbitMQ e eventos versionados, com destaque para v1.appointment.created e v1.appointment.updated. Essa abordagem elimina dependencia sincrona direta entre produtor e consumidores, elevando resiliencia e desacoplamento.

No notification-service, foram implementados idempotencia por fingerprint unico e retry. Em etapa de refinamento, foi tratada explicitamente a condicao de corrida em concorrencia para duplicidade de evento, fortalecendo consistencia de processamento.

## 11 Consulta de historico com GraphQL

O uso de GraphQL no patient-history-service foi uma decisao de adequacao ao caso de uso de leitura. A consulta consultationHistory permite filtragem e paginacao sem multiplicar endpoints REST dedicados.

Foram realizados ajustes de tipagem e parse de filtros temporais para OffsetDateTime, mitigando risco de comparacoes invalidas e elevando robustez do criterio de busca.

## 12 Estrategia de testes e validacao

A estrategia de qualidade combinou testes unitarios, integracoes e validacao operacional:

- testes unitarios para regras de autenticacao e comportamentos especificos;
- testes de integracao com JPA;
- testes de integracao com RabbitMQ e persistencia;
- testes de GraphQL com Testcontainers;
- smoke tests por curl e colecao Postman.

### Tabela de validacao

| Cenario | Resultado esperado | Evidencia |
|---|---|---|
| Login com credenciais validas | Token JWT e perfil retornados | [appointment-service/src/main/java/br/com/fiap/hospital/appointment/controller/AuthController.java](appointment-service/src/main/java/br/com/fiap/hospital/appointment/controller/AuthController.java) |
| Token invalido | Resposta 401 sem prosseguir a cadeia | [appointment-service/src/main/java/br/com/fiap/hospital/appointment/security/JwtAuthenticationFilter.java](appointment-service/src/main/java/br/com/fiap/hospital/appointment/security/JwtAuthenticationFilter.java) |
| Agendamento com data passada | Falha de validacao | [appointment-service/src/main/java/br/com/fiap/hospital/appointment/dto/AppointmentRequest.java](appointment-service/src/main/java/br/com/fiap/hospital/appointment/dto/AppointmentRequest.java) |
| Consulta GraphQL com pagina grande | Pagina limitada e totalElements seguro | [patient-history-service/src/main/java/br/com/fiap/hospital/history/graphql/HistoryGraphqlController.java](patient-history-service/src/main/java/br/com/fiap/hospital/history/graphql/HistoryGraphqlController.java) |
| Reprocessamento de notificacao | Tentativas limitadas com estado terminal | [notification-service/src/main/java/br/com/fiap/hospital/notification/service/NotificationService.java](notification-service/src/main/java/br/com/fiap/hospital/notification/service/NotificationService.java) |

As suites de integracao com Testcontainers foram preparadas para nao bloquear desenvolvimento quando Docker estiver indisponivel, preservando fluidez de trabalho sem abrir mao de validacao robusta quando a infraestrutura estiver ativa.

## 13 Resultados e evidencias

A validacao operacional registrou:

- orquestracao de servicos e infraestrutura em Docker Compose;
- endpoints de health retornando status UP;
- autenticacao funcional via login;
- criacao e listagem de consultas funcionais;
- retorno consistente da consulta GraphQL de historico;
- execucao de testes Maven com sucesso.

Essas evidencias indicam convergencia entre desenho arquitetural, implementacao e comportamento observado em execucao real.

### 13.1 Aderencia objetiva ao ADTJ (Fase 3)

Com base no enunciado oficial do ADTJ, a implementacao atende aos eixos obrigatorios:

- seguranca com autenticacao e autorizacao por papeis (medico, enfermeiro, paciente);
- separacao em servicos de agendamento, notificacao e historico;
- comunicacao assincrona com RabbitMQ entre agendamento e consumidores;
- consulta de historico por GraphQL com paginação;
- evidencias operacionais com endpoints e colecoes de teste.

Na validacao mais recente de runtime, os resultados consolidados foram:

- login: 200;
- create: 200;
- list: 200;
- get: 200;
- update: 200;
- graphql: 200;
- token invalido: 401 (esperado).

As evidencias completas estao registradas em `tmp/api-results.json` e sumarizadas em `docs/validation-evidence.md`.

## 14 Correcao de findings e melhoria de qualidade

A evolucao do projeto incorporou correcoes relevantes para qualidade tecnica:

- ajustes de dependencias no monorepo;
- correcoes em Dockerfiles multi-modulo;
- padronizacao de execucao em runtime distroless;
- fortalecimentos de configuracao de JWT em ambiente local;
- inclusao de ObjectMapper no notification-service;
- ajustes de schema GraphQL e paginacao;
- correcao de idempotencia concorrente no processamento de notificacao;
- correcao do parse de datas no filtro GraphQL;
- saneamento de versionamento com retirada de artefatos indevidos.

A incorporacao desses ajustes reforca a maturidade do processo de revisao e a capacidade de resposta a riscos concretos de implementacao.

## 15 Trade-offs arquiteturais

O principal trade-off do trabalho foi adotar modularidade suficiente sem elevar custo cognitivo e operacional acima do necessario. O monorepo simplificou build e governanca tecnica local, ao custo de maior disciplina para preservar fronteiras entre modulos.

A opcao por banco unico com schemas favoreceu viabilidade de entrega e reproducibilidade, mas nao equivale a isolamento fisico total. O uso de RabbitMQ adicionou complexidade de infraestrutura, compensada pelo ganho de desacoplamento. O GraphQL foi restrito ao historico para evitar complexificacao desnecessaria das operacoes transacionais.

## 16 Limitacoes e riscos conhecidos

Como projeto academico com foco em demonstracao local, permanecem limites conhecidos:

- credenciais e segredos padrao em ambiente de desenvolvimento;
- ausencia de cofre centralizado de segredos;
- observabilidade ainda basica, sem tracing distribuido completo;
- pipeline CI/CD corporativo nao implementado;
- separacao logica por schema sem isolamento fisico por servico.

Esses pontos nao invalidam os objetivos da fase, mas constituem backlog tecnico para etapas futuras.

O ambiente de validacao local tambem depende de Docker para as suites de integracao com Testcontainers. Sem Docker, o projeto continua compilando e os testes unitarios continuam cobrindo a base critica, mas algumas evidencias de integracao deixam de ser reproduziveis no mesmo formato.

O reprocessamento de notificacoes agora possui limite maximo por evento e estado terminal DEAD_LETTER, reduzindo risco de retry infinito.

O ambiente de validacao local tambem depende de Docker para as suites de integracao com Testcontainers. Sem Docker, o projeto continua compilando e os testes unitarios continuam cobrindo a base critica, mas algumas evidencias de integracao deixam de ser reproduziveis no mesmo formato.

O reprocessamento de notificacoes agora possui limite maximo por evento e estado terminal DEAD_LETTER, reduzindo risco de retry infinito.

## 17 Propostas de evolucao

Para aproximacao de um cenario produtivo, recomenda-se:

- externalizacao de segredos em plataforma especializada;
- hardening de seguranca e politicas de credenciais;
- instrumentacao de metricas e tracing distribuido;
- pipeline CI/CD com gates de qualidade, seguranca e entrega;
- avaliacao de segregacao fisica de dados por servico conforme criticidade.

## 18 Conclusao

O trabalho atingiu o objetivo de construir um backend hospitalar modular tecnicamente consistente, com separacao de responsabilidades, seguranca funcional e integracao assincrona efetiva. A solucao demonstrou aderencia ao norteador da fase e apresentou validacao pratica satisfatoria.

A principal contribuicao do projeto foi demonstrar que e possivel combinar rigor tecnico e pragmatismo de entrega, mantendo clareza arquitetural sem sobreengenharia. O resultado estabelece base solida para evolucao incremental em direcao a requisitos de producao.

## Referencias

- IETF. RFC 7519: JSON Web Token.
- POSTGRESQL GLOBAL DEVELOPMENT GROUP. PostgreSQL Documentation.
- REDGATE. Flyway Documentation.
- SPRING. Spring Boot Reference Documentation.
- SPRING. Spring for GraphQL Reference.
- SPRING. Spring Security Reference.
- TESTCONTAINERS. Testcontainers Documentation.
- VMWARE. RabbitMQ Documentation.

## Apendice A - Prompt academico estruturado

Utilize o texto deste relatorio como base para expansao em formato institucional final, solicitando a ferramenta de escrita academica:

- manutencao de fidelidade tecnica ao projeto implementado;
- preservacao da estrutura numerada e coesao argumentativa;
- ampliacao de densidade teorica sem inventar funcionalidades;
- adequacao ao template oficial da instituicao, caso existente.
