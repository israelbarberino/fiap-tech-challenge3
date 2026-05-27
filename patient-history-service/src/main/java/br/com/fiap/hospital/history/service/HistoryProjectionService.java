package br.com.fiap.hospital.history.service;

import br.com.fiap.hospital.history.domain.ConsultationHistoryEntity;
import br.com.fiap.hospital.history.repository.ConsultationHistoryRepository;
import br.com.fiap.hospital.sharedkernel.appointment.AppointmentEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HistoryProjectionService {

    private final ConsultationHistoryRepository repository;

    public HistoryProjectionService(ConsultationHistoryRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void upsert(AppointmentEvent event) {
        ConsultationHistoryEntity entity = repository.findByAppointmentId(event.appointmentId())
                .orElseGet(ConsultationHistoryEntity::new);
        entity.setAppointmentId(event.appointmentId());
        entity.setPatientId(event.patientId());
        entity.setDoctorId(event.doctorId());
        entity.setScheduledAt(event.scheduledAt());
        entity.setNotes(event.notes());
        entity.setStatus(event.status());
        entity.setSourceEventType(event.eventType());
        repository.save(entity);
    }
}
