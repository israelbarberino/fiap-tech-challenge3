package br.com.fiap.hospital.appointment.repository;

import br.com.fiap.hospital.appointment.domain.AppointmentEntity;
import br.com.fiap.hospital.appointment.domain.AppointmentStatus;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers(disabledWithoutDocker = true)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AppointmentRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void configureDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> true);
    }

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Test
    void shouldPersistAndLoadAppointment() {
        AppointmentEntity appointment = new AppointmentEntity();
        appointment.setPatientId(UUID.randomUUID());
        appointment.setDoctorId(UUID.randomUUID());
        appointment.setScheduledAt(OffsetDateTime.now().plusDays(1));
        appointment.setNotes("Retorno clínico");
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        AppointmentEntity saved = appointmentRepository.saveAndFlush(appointment);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(appointmentRepository.findById(saved.getId())).isPresent();
    }
}
