package br.com.fiap.hospital.history.graphql;

import br.com.fiap.hospital.history.domain.ConsultationHistoryEntity;
import br.com.fiap.hospital.history.security.AuthenticatedUser;
import br.com.fiap.hospital.sharedkernel.security.UserRole;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public final class HistorySpecifications {

    private HistorySpecifications() {
    }

    public static Specification<ConsultationHistoryEntity> from(HistoryFilterInput filter, AuthenticatedUser currentUser) {
        return (root, query, builder) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
            OffsetDateTime from = parseDateTime(filter == null ? null : filter.from(), "from");
            OffsetDateTime to = parseDateTime(filter == null ? null : filter.to(), "to");

            if (currentUser.role() == UserRole.ROLE_PATIENT) {
                predicates.add(builder.equal(root.get("patientId"), currentUser.userId()));
            } else if (filter != null && filter.patientId() != null) {
                predicates.add(builder.equal(root.get("patientId"), filter.patientId()));
            }

            if (filter != null && filter.doctorId() != null) {
                predicates.add(builder.equal(root.get("doctorId"), filter.doctorId()));
            }
            if (from != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("scheduledAt"), from));
            }
            if (to != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("scheduledAt"), to));
            }
            if (filter != null && filter.status() != null && !filter.status().isBlank()) {
                predicates.add(builder.equal(root.get("status"), filter.status()));
            }

            return builder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }

    private static OffsetDateTime parseDateTime(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return OffsetDateTime.parse(value);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Invalid date-time for filter '" + fieldName + "': " + value, exception);
        }
    }

    public record HistoryFilterInput(UUID patientId, UUID doctorId, String from, String to, String status) {
    }
}
