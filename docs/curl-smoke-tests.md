# Curl Smoke Tests

## 1) Login

```bash
curl -s -X POST "http://localhost:8081/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"doctor1","password":"doctor123"}'
```

Guarde o `accessToken` retornado.

## 2) Criar consulta

```bash
curl -s -X POST "http://localhost:8081/api/v1/appointments" \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "patientId":"11111111-1111-1111-1111-111111111111",
    "doctorId":"22222222-2222-2222-2222-222222222222",
    "scheduledAt":"2026-05-20T10:00:00Z",
    "notes":"Consulta de validação"
  }'
```

## 3) Listar consultas

```bash
curl -s "http://localhost:8081/api/v1/appointments" \
  -H "Authorization: Bearer <TOKEN>"
```

## 4) Consultar histórico em GraphQL

```bash
curl -s -X POST "http://localhost:8083/graphql" \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "query":"query { consultationHistory(page:0,size:10){ totalElements items { appointmentId patientId doctorId scheduledAt status notes } } }"
  }'
```

## 5) Healthchecks

```bash
curl -s "http://localhost:8081/actuator/health"
curl -s "http://localhost:8082/actuator/health"
curl -s "http://localhost:8083/actuator/health"
```
