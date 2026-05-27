package br.com.fiap.hospital.notification.service;

import br.com.fiap.hospital.notification.domain.NotificationLogEntity;
import br.com.fiap.hospital.notification.domain.NotificationStatus;
import br.com.fiap.hospital.notification.repository.NotificationLogRepository;
import br.com.fiap.hospital.sharedkernel.appointment.AppointmentEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.OffsetDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationLogRepository repository;
    private final ObjectMapper objectMapper;
    private final int maxRetryAttempts;

    public NotificationService(NotificationLogRepository repository, ObjectMapper objectMapper,
                               @Value("${notification.retry.max-attempts:3}") int maxRetryAttempts) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.maxRetryAttempts = maxRetryAttempts;
    }

    @Transactional
    public void registerEvent(AppointmentEvent event) {
        String payload = serialize(event);
        String fingerprint = fingerprint(payload);
        if (repository.findByEventFingerprint(fingerprint).isPresent()) {
            return;
        }

        try {
            NotificationLogEntity entity = new NotificationLogEntity();
            entity.setEventFingerprint(fingerprint);
            entity.setEventType(event.eventType());
            entity.setAppointmentId(event.appointmentId());
            entity.setPatientId(event.patientId());
            entity.setPayloadJson(payload);
            entity.setStatus(NotificationStatus.PENDING);
            repository.saveAndFlush(entity);
            deliver(entity);
        } catch (DataIntegrityViolationException exception) {
            log.info("notification event already registered fingerprint={}", fingerprint);
        }
    }

    @Transactional
    public void retryFailedNotifications() {
        repository.findTop20ByStatusAndRetryCountLessThanOrderByCreatedAtAsc(NotificationStatus.FAILED, maxRetryAttempts)
                .forEach(this::deliver);
    }

    @Transactional
    public void deliver(NotificationLogEntity entity) {
        try {
            entity.setStatus(NotificationStatus.PROCESSING);
            entity.setRetryCount(entity.getRetryCount() + 1);
            entity.setLastAttemptAt(OffsetDateTime.now());
            repository.save(entity);
            log.info("notification processed appointmentId={} patientId={} eventType={}", entity.getAppointmentId(), entity.getPatientId(), entity.getEventType());
            entity.setStatus(NotificationStatus.SENT);
            repository.save(entity);
        } catch (Exception exception) {
            entity.setStatus(entity.getRetryCount() >= maxRetryAttempts ? NotificationStatus.DEAD_LETTER : NotificationStatus.FAILED);
            repository.save(entity);
            log.warn("notification processing failed appointmentId={} fingerprint={}", entity.getAppointmentId(), entity.getEventFingerprint(), exception);
        }
    }

    private String serialize(AppointmentEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (Exception exception) {
            throw new IllegalStateException("Could not serialize event", exception);
        }
    }

    private String fingerprint(String payload) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte value : hash) {
                String hex = Integer.toHexString(0xff & value);
                if (hex.length() == 1) {
                    builder.append('0');
                }
                builder.append(hex);
            }
            return builder.toString();
        } catch (Exception exception) {
            throw new IllegalStateException("Could not create fingerprint", exception);
        }
    }
}
