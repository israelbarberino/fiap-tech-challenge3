package br.com.fiap.hospital.appointment.service;

import br.com.fiap.hospital.appointment.domain.AppointmentEntity;
import br.com.fiap.hospital.appointment.security.AuthenticatedUser;
import br.com.fiap.hospital.sharedkernel.security.UserRole;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public final class AppointmentSpecifications {

    private AppointmentSpecifications() {
    }

    public static Specification<AppointmentEntity> from(AppointmentSearchCriteria criteria, AuthenticatedUser currentUser) {
        return (root, query, builder) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            if (currentUser.role() == UserRole.ROLE_PATIENT) {
                predicates.add(builder.equal(root.get("patientId"), currentUser.userId()));
            } else if (criteria.patientId() != null) {
                predicates.add(builder.equal(root.get("patientId"), criteria.patientId()));
            }

            if (criteria.doctorId() != null) {
                predicates.add(builder.equal(root.get("doctorId"), criteria.doctorId()));
            }
            if (criteria.status() != null) {
                predicates.add(builder.equal(root.get("status"), criteria.status()));
            }
            if (criteria.from() != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("scheduledAt"), criteria.from()));
            }
            if (criteria.to() != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("scheduledAt"), criteria.to()));
            }

            return builder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
}
