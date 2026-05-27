package br.com.fiap.hospital.notification.service;

import br.com.fiap.hospital.notification.domain.NotificationLogEntity;
import br.com.fiap.hospital.notification.domain.NotificationStatus;
import br.com.fiap.hospital.notification.repository.NotificationLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NotificationRetryPolicyTest {

    @Test
    void shouldOnlyRetryEventsBelowMaximumAttempts() {
        NotificationLogRepository repository = Mockito.mock(NotificationLogRepository.class);
        when(repository.findTop20ByStatusAndRetryCountLessThanOrderByCreatedAtAsc(NotificationStatus.FAILED, 3))
                .thenReturn(List.of());

        NotificationService service = new NotificationService(repository, new ObjectMapper().findAndRegisterModules(), 3);

        service.retryFailedNotifications();

        verify(repository).findTop20ByStatusAndRetryCountLessThanOrderByCreatedAtAsc(NotificationStatus.FAILED, 3);
    }

    @Test
    void shouldMarkNotificationAsDeadLetterWhenMaxAttemptsAreReached() {
        NotificationLogRepository repository = Mockito.mock(NotificationLogRepository.class);
        NotificationService service = new NotificationService(repository, new ObjectMapper().findAndRegisterModules(), 1);
        NotificationLogEntity entity = new NotificationLogEntity();
        entity.setAppointmentId(UUID.randomUUID());
        entity.setPatientId(UUID.randomUUID());
        entity.setEventFingerprint("fingerprint");
        entity.setEventType("APPOINTMENT_CREATED");
        entity.setRetryCount(1);
        AtomicInteger saveCalls = new AtomicInteger();

        doAnswer(invocation -> {
            if (saveCalls.getAndIncrement() == 0) {
                throw new RuntimeException("boom");
            }
            return invocation.getArgument(0);
        }).when(repository).save(Mockito.any());

        service.deliver(entity);

        assertThat(entity.getStatus()).isEqualTo(NotificationStatus.DEAD_LETTER);
    }
}
