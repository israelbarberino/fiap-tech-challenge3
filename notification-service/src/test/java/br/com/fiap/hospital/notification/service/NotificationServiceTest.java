package br.com.fiap.hospital.notification.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.fiap.hospital.notification.repository.NotificationLogRepository;
import br.com.fiap.hospital.sharedkernel.appointment.AppointmentEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

class NotificationServiceTest {

    @Test
    void shouldIgnoreDuplicateEventWhenUniqueConstraintWinsTheRace() {
        NotificationLogRepository repository = org.mockito.Mockito.mock(NotificationLogRepository.class);
        when(repository.findByEventFingerprint(any())).thenReturn(Optional.empty());
        when(repository.saveAndFlush(any())).thenThrow(new DataIntegrityViolationException("duplicate fingerprint"));

        NotificationService service = new NotificationService(repository, new ObjectMapper().findAndRegisterModules());

        service.registerEvent(new AppointmentEvent(
                "APPOINTMENT_CREATED",
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                OffsetDateTime.parse("2025-05-18T10:15:30Z"),
                "Initial consultation",
                "SCHEDULED"
        ));

        verify(repository).saveAndFlush(any());
        verify(repository, never()).save(any());
    }
}