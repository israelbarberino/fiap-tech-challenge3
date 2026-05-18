# Evidencias de Validacao
Data: 2026-05-17 21:21:19 -03:00

## Docker Compose
```text
NAME                                             IMAGE                                          COMMAND                  SERVICE                   CREATED       STATUS                 PORTS
fiap-tech-challenge3-appointment-service-1       fiap-tech-challenge3-appointment-service       "java -jar /app/app.ÔÇª"   appointment-service       3 hours ago   Up 3 hours             0.0.0.0:8081->8081/tcp, [::]:8081->8081/tcp
fiap-tech-challenge3-notification-service-1      fiap-tech-challenge3-notification-service      "java -jar /app/app.ÔÇª"   notification-service      3 hours ago   Up 3 hours             0.0.0.0:8082->8082/tcp, [::]:8082->8082/tcp
fiap-tech-challenge3-patient-history-service-1   fiap-tech-challenge3-patient-history-service   "java -jar /app/app.ÔÇª"   patient-history-service   3 hours ago   Up 3 hours             0.0.0.0:8083->8083/tcp, [::]:8083->8083/tcp
hospital-postgres                                postgres:15-alpine                             "docker-entrypoint.sÔÇª"   postgres                  3 hours ago   Up 3 hours (healthy)   0.0.0.0:5432->5432/tcp, [::]:5432->5432/tcp
hospital-rabbitmq                                rabbitmq:3.13-management-alpine                "docker-entrypoint.sÔÇª"   rabbitmq                  3 hours ago   Up 3 hours (healthy)   0.0.0.0:5672->5672/tcp, [::]:5672->5672/tcp, 0.0.0.0:15672->15672/tcp, [::]:15672->15672/tcp
```

## Healthchecks
```text
8081: {"status":"UP"}
8082: {"status":"UP"}
8083: {"status":"UP"}
```

## Fluxo E2E (login, create, list, graphql)
```text
login.token: eyJhbGci...WzIDbvx1
login.userId: 03bff2fe-60d1-4357-8e4e-71c31e552989
createdId: 1d36128f-ddd8-4169-a067-7e02f3585bce
createResponse: {"id":"1d36128f-ddd8-4169-a067-7e02f3585bce","patientId":"11111111-1111-1111-1111-111111111111","doctorId":"03bff2fe-60d1-4357-8e4e-71c31e552989","scheduledAt":"2026-05-20T10:00:00Z","notes":"Evidencia final","status":"SCHEDULED","createdAt":"2026-05-18T00:21:21.034752708Z","updatedAt":"2026-05-18T00:21:21.034752708Z"}
listResponse: {"content":[{"id":"af37b386-e72d-46cb-bc7c-4c2ba7cabc0e","patientId":"11111111-1111-1111-1111-111111111111","doctorId":"22222222-2222-2222-2222-222222222222","scheduledAt":"2026-05-20T10:00:00Z","notes":"Consulta de validacao","status":"SCHEDULED","createdAt":"2026-05-17T21:12:48.06746Z","updatedAt":"2026-05-17T21:12:48.06746Z"},{"id":"fdf47833-7554-4c57-a97b-bc5882ee149c","patientId":"11111111-1111-1111-1111-111111111111","doctorId":"22222222-2222-2222-2222-222222222222","scheduledAt":"2026-05-20T10:00:00Z","notes":"Consulta de validacao","status":"SCHEDULED","createdAt":"2026-05-17T21:17:11.580773Z","updatedAt":"2026-05-17T21:17:11.580773Z"},{"id":"47563505-335a-4979-b805-69797c8efba8","patientId":"11111111-1111-1111-1111-111111111111","doctorId":"03bff2fe-60d1-4357-8e4e-71c31e552989","scheduledAt":"2026-05-20T10:00:00Z","notes":"Consulta de validacao","status":"SCHEDULED","createdAt":"2026-05-17T21:23:36.970757Z","updatedAt":"2026-05-17T21:23:36.970757Z"},{"id":"c8c2787b-17c4-446f-8e07-d7218d437590","patientId":"11111111-1111-1111-1111-111111111111","doctorId":"03bff2fe-60d1-4357-8e4e-71c31e552989","scheduledAt":"2026-05-20T10:00:00Z","notes":"Consulta curl final","status":"SCHEDULED","createdAt":"2026-05-17T21:39:02.435831Z","updatedAt":"2026-05-17T21:39:02.435831Z"},{"id":"944e3d00-4bb3-4a85-8573-07b6c2cd564f","patientId":"11111111-1111-1111-1111-111111111111","doctorId":"03bff2fe-60d1-4357-8e4e-71c31e552989","scheduledAt":"2026-05-20T10:00:00Z","notes":"Consulta curl final 3","status":"SCHEDULED","createdAt":"2026-05-17T21:39:30.605967Z","updatedAt":"2026-05-17T21:39:30.605967Z"},{"id":"1d36128f-ddd8-4169-a067-7e02f3585bce","patientId":"11111111-1111-1111-1111-111111111111","doctorId":"03bff2fe-60d1-4357-8e4e-71c31e552989","scheduledAt":"2026-05-20T10:00:00Z","notes":"Evidencia final","status":"SCHEDULED","createdAt":"2026-05-18T00:21:21.034753Z","updatedAt":"2026-05-18T00:21:21.034753Z"}],"pageable":{"pageNumber":0,"pageSize":20,"sort":{"sorted":false,"empty":true,"unsorted":true},"offset":0,"paged":true,"unpaged":false},"last":true,"totalElements":6,"totalPages":1,"first":true,"size":20,"number":0,"sort":{"sorted":false,"empty":true,"unsorted":true},"numberOfElements":6,"empty":false}
graphqlResponse: {"data":{"consultationHistory":{"totalElements":5,"items":[{"appointmentId":"fdf47833-7554-4c57-a97b-bc5882ee149c"},{"appointmentId":"47563505-335a-4979-b805-69797c8efba8"},{"appointmentId":"c8c2787b-17c4-446f-8e07-d7218d437590"},{"appointmentId":"944e3d00-4bb3-4a85-8573-07b6c2cd564f"},{"appointmentId":"1d36128f-ddd8-4169-a067-7e02f3585bce"}]}}}
```

## Maven Test
```text
mvn -q test: SUCCESS
```
