package br.com.fiap.hospital.history.integration;

import br.com.fiap.hospital.history.domain.ConsultationHistoryEntity;
import br.com.fiap.hospital.history.repository.ConsultationHistoryRepository;
import br.com.fiap.hospital.sharedkernel.security.UserRole;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
@AutoConfigureHttpGraphQlTester
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class HistoryGraphqlIntegrationTest {

    private static final String TEST_SECRET = "0123456789012345678901234567890123456789012345678901234567890123";

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Container
    static RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3.13-management-alpine");

    @DynamicPropertySource
    static void register(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> true);
        registry.add("spring.rabbitmq.host", rabbit::getHost);
        registry.add("spring.rabbitmq.port", rabbit::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbit::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbit::getAdminPassword);
        registry.add("jwt.secret", () -> TEST_SECRET);
    }

    @Autowired
    private ConsultationHistoryRepository repository;

    @Autowired
    private HttpGraphQlTester graphQlTester;

    @Test
    void shouldReturnOnlyPatientOwnHistory() {
        UUID patientId = UUID.randomUUID();
        UUID otherPatientId = UUID.randomUUID();

        ConsultationHistoryEntity ownItem = new ConsultationHistoryEntity();
        ownItem.setId(UUID.randomUUID());
        ownItem.setAppointmentId(UUID.randomUUID());
        ownItem.setPatientId(patientId);
        ownItem.setDoctorId(UUID.randomUUID());
        ownItem.setScheduledAt(OffsetDateTime.now().minusDays(1));
        ownItem.setNotes("Histórico próprio");
        ownItem.setStatus("COMPLETED");
        ownItem.setSourceEventType("APPOINTMENT_UPDATED");

        ConsultationHistoryEntity foreignItem = new ConsultationHistoryEntity();
        foreignItem.setId(UUID.randomUUID());
        foreignItem.setAppointmentId(UUID.randomUUID());
        foreignItem.setPatientId(otherPatientId);
        foreignItem.setDoctorId(UUID.randomUUID());
        foreignItem.setScheduledAt(OffsetDateTime.now().minusDays(2));
        foreignItem.setNotes("Histórico de outro paciente");
        foreignItem.setStatus("COMPLETED");
        foreignItem.setSourceEventType("APPOINTMENT_UPDATED");

        repository.save(ownItem);
        repository.save(foreignItem);

        String token = Jwts.builder()
                .subject(patientId.toString())
                .claim("username", "patient1")
                .claim("role", UserRole.ROLE_PATIENT.name())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(3600)))
                .signWith(Keys.hmacShaKeyFor(TEST_SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();

        graphQlTester.mutate()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build()
                .document("""
                        query {
                          consultationHistory(page: 0, size: 10) {
                            totalElements
                            items {
                              patientId
                              notes
                            }
                          }
                        }
                        """)
                .execute()
                .path("consultationHistory.totalElements").entity(Integer.class).isEqualTo(1)
                .path("consultationHistory.items[0].patientId").entity(String.class).isEqualTo(patientId.toString())
                .path("consultationHistory.items[0].notes").entity(String.class).isEqualTo("Histórico próprio");
    }
}
