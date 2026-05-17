package br.com.fiap.hospital.history.repository;

import br.com.fiap.hospital.history.domain.ConsultationHistoryEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConsultationHistoryRepository extends JpaRepository<ConsultationHistoryEntity, UUID>, JpaSpecificationExecutor<ConsultationHistoryEntity> {

    Optional<ConsultationHistoryEntity> findByAppointmentId(UUID appointmentId);
}
