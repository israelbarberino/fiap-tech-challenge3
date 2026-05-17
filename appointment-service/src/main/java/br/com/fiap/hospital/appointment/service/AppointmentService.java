package br.com.fiap.hospital.appointment.service;

import br.com.fiap.hospital.appointment.config.RabbitConfiguration;
import br.com.fiap.hospital.appointment.domain.AppointmentEntity;
import br.com.fiap.hospital.appointment.domain.AppointmentStatus;
import br.com.fiap.hospital.appointment.dto.AppointmentRequest;
import br.com.fiap.hospital.appointment.dto.AppointmentResponse;
import br.com.fiap.hospital.appointment.dto.AppointmentUpdateRequest;
import br.com.fiap.hospital.appointment.exception.BusinessException;
import br.com.fiap.hospital.appointment.repository.AppointmentRepository;
import br.com.fiap.hospital.appointment.security.AuthenticatedUser;
import br.com.fiap.hospital.sharedkernel.appointment.AppointmentEvent;
import br.com.fiap.hospital.sharedkernel.appointment.AppointmentEventType;
import br.com.fiap.hospital.sharedkernel.security.UserRole;
import java.util.UUID;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final RabbitTemplate rabbitTemplate;

    public AppointmentService(AppointmentRepository appointmentRepository, RabbitTemplate rabbitTemplate) {
        this.appointmentRepository = appointmentRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional
    public AppointmentResponse create(AppointmentRequest request, AuthenticatedUser currentUser) {
        authorizeWrite(request.patientId(), currentUser);
        AppointmentEntity entity = new AppointmentEntity();
        entity.setPatientId(request.patientId());
        entity.setDoctorId(request.doctorId());
        entity.setScheduledAt(request.scheduledAt());
        entity.setNotes(request.notes());
        entity.setStatus(AppointmentStatus.SCHEDULED);
        AppointmentEntity saved = appointmentRepository.save(entity);
        publishEvent(AppointmentEventType.APPOINTMENT_CREATED, saved);
        return toResponse(saved);
    }

    @Transactional
    public AppointmentResponse update(UUID id, AppointmentUpdateRequest request, AuthenticatedUser currentUser) {
        AppointmentEntity entity = findOwnedAppointment(id, currentUser);
        if (request.scheduledAt() != null) {
            entity.setScheduledAt(request.scheduledAt());
        }
        if (request.notes() != null) {
            entity.setNotes(request.notes());
        }
        entity.setStatus(AppointmentStatus.CONFIRMED);
        AppointmentEntity saved = appointmentRepository.save(entity);
        publishEvent(AppointmentEventType.APPOINTMENT_UPDATED, saved);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public AppointmentResponse get(UUID id, AuthenticatedUser currentUser) {
        return toResponse(findOwnedAppointment(id, currentUser));
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponse> list(AppointmentSearchCriteria criteria, Pageable pageable, AuthenticatedUser currentUser) {
        Specification<AppointmentEntity> specification = AppointmentSpecifications.from(criteria, currentUser);
        return appointmentRepository.findAll(specification, pageable).map(this::toResponse);
    }

    @Transactional
    public void cancel(UUID id, AuthenticatedUser currentUser) {
        AppointmentEntity entity = findOwnedAppointment(id, currentUser);
        entity.setStatus(AppointmentStatus.CANCELED);
        AppointmentEntity saved = appointmentRepository.save(entity);
        publishEvent(AppointmentEventType.APPOINTMENT_UPDATED, saved);
    }

    private AppointmentEntity findOwnedAppointment(UUID id, AuthenticatedUser currentUser) {
        AppointmentEntity entity = appointmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Appointment not found"));
        if (currentUser.role() == UserRole.ROLE_PATIENT && !entity.getPatientId().equals(currentUser.userId())) {
            throw new BusinessException("Access denied to another patient's appointment");
        }
        return entity;
    }

    private void authorizeWrite(UUID patientId, AuthenticatedUser currentUser) {
        if (currentUser.role() == UserRole.ROLE_PATIENT && !currentUser.userId().equals(patientId)) {
            throw new BusinessException("Patient can only schedule appointments for themselves");
        }
    }

    private AppointmentResponse toResponse(AppointmentEntity entity) {
        return new AppointmentResponse(entity.getId(), entity.getPatientId(), entity.getDoctorId(), entity.getScheduledAt(), entity.getNotes(), entity.getStatus(), entity.getCreatedAt(), entity.getUpdatedAt());
    }

    private void publishEvent(AppointmentEventType eventType, AppointmentEntity entity) {
        AppointmentEvent event = new AppointmentEvent(
                eventType.name(),
                entity.getId(),
                entity.getPatientId(),
                entity.getDoctorId(),
                entity.getScheduledAt(),
                entity.getNotes(),
                entity.getStatus().name()
        );
        rabbitTemplate.convertAndSend(RabbitConfiguration.APPOINTMENT_EXCHANGE, eventType.routingKey(), event);
    }
}
