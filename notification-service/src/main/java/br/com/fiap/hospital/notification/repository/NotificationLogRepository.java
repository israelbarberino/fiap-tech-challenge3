package br.com.fiap.hospital.notification.repository;

import br.com.fiap.hospital.notification.domain.NotificationLogEntity;
import br.com.fiap.hospital.notification.domain.NotificationStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationLogRepository extends JpaRepository<NotificationLogEntity, UUID> {

    Optional<NotificationLogEntity> findByEventFingerprint(String eventFingerprint);

    List<NotificationLogEntity> findTop20ByStatusOrderByCreatedAtAsc(NotificationStatus status);
}
