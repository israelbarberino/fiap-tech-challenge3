package br.com.fiap.hospital.appointment.service;

import br.com.fiap.hospital.appointment.domain.AppointmentEntity;
import br.com.fiap.hospital.appointment.domain.AppointmentStatus;
import br.com.fiap.hospital.appointment.exception.BusinessException;
import br.com.fiap.hospital.appointment.repository.AppointmentRepository;
import br.com.fiap.hospital.appointment.security.AuthenticatedUser;
import br.com.fiap.hospital.sharedkernel.security.UserRole;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class AppointmentServiceAuthorizationTest {

    @Test
    void shouldRejectPatientAccessingAnotherPatientAppointment() {
        AppointmentRepository repository = Mockito.mock(AppointmentRepository.class);
        RabbitTemplate rabbitTemplate = Mockito.mock(RabbitTemplate.class);
        AppointmentService service = new AppointmentService(repository, rabbitTemplate);

        UUID appointmentId = UUID.randomUUID();
        UUID ownerPatientId = UUID.randomUUID();
        UUID otherPatientId = UUID.randomUUID();

        AppointmentEntity entity = new AppointmentEntity();
        entity.setPatientId(ownerPatientId);
        entity.setDoctorId(UUID.randomUUID());
        entity.setScheduledAt(OffsetDateTime.now().plusDays(1));
        entity.setStatus(AppointmentStatus.SCHEDULED);

        when(repository.findById(appointmentId)).thenReturn(Optional.of(entity));

        AuthenticatedUser patient = new AuthenticatedUser(otherPatientId, "patient1", UserRole.ROLE_PATIENT);

        assertThatThrownBy(() -> service.get(appointmentId, patient))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Access denied to another patient's appointment");
    }
}
