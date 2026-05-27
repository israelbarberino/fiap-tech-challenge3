package br.com.fiap.hospital.history.graphql;

import br.com.fiap.hospital.history.domain.ConsultationHistoryEntity;
import br.com.fiap.hospital.history.repository.ConsultationHistoryRepository;
import br.com.fiap.hospital.history.security.AuthenticatedUser;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

@Controller
public class HistoryGraphqlController {

    private static final int MAX_PAGE_SIZE = 100;

    private final ConsultationHistoryRepository repository;

    public HistoryGraphqlController(ConsultationHistoryRepository repository) {
        this.repository = repository;
    }

    @QueryMapping
    public ConsultationHistoryPage consultationHistory(@Argument HistorySpecifications.HistoryFilterInput filter,
                                                        @Argument Integer page,
                                                        @Argument Integer size) {
        AuthenticatedUser currentUser = (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int pageNumber = page == null ? 0 : Math.max(page, 0);
        int pageSize = size == null ? 20 : Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        Specification<ConsultationHistoryEntity> specification = HistorySpecifications.from(filter, currentUser);
        Page<ConsultationHistoryEntity> result = repository.findAll(specification, PageRequest.of(pageNumber, pageSize));
        List<ConsultationHistoryItem> items = result.getContent().stream().map(HistoryGraphqlController::toItem).toList();
        long totalElements = result.getTotalElements();
        int totalElementsInt = totalElements > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) totalElements;
        return new ConsultationHistoryPage(items, pageNumber, pageSize, totalElementsInt);
    }

    private static ConsultationHistoryItem toItem(ConsultationHistoryEntity entity) {
        return new ConsultationHistoryItem(
                entity.getAppointmentId(),
                entity.getPatientId(),
                entity.getDoctorId(),
                entity.getScheduledAt().toString(),
                entity.getNotes(),
                entity.getStatus(),
                entity.getSourceEventType()
        );
    }

    public record ConsultationHistoryItem(UUID appointmentId, UUID patientId, UUID doctorId, String scheduledAt, String notes, String status, String sourceEventType) {
    }

    public record ConsultationHistoryPage(List<ConsultationHistoryItem> items, int page, int size, int totalElements) {
    }
}
