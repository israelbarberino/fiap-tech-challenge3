package br.com.fiap.hospital.history.graphql;

import static org.junit.jupiter.api.Assertions.assertThrows;

import br.com.fiap.hospital.history.domain.ConsultationHistoryEntity;
import br.com.fiap.hospital.history.security.AuthenticatedUser;
import br.com.fiap.hospital.sharedkernel.security.UserRole;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

class HistorySpecificationsTest {

    @Test
    void shouldRejectInvalidOffsetDateTimeFilter() {
        HistorySpecifications.HistoryFilterInput filter = new HistorySpecifications.HistoryFilterInput(
                null,
                null,
                "2025-99-99",
                null,
                null
        );
        AuthenticatedUser currentUser = new AuthenticatedUser(UUID.randomUUID(), "doctor1", UserRole.ROLE_DOCTOR);
        Specification<ConsultationHistoryEntity> specification = HistorySpecifications.from(filter, currentUser);

        assertThrows(IllegalArgumentException.class, () -> specification.toPredicate(
                org.mockito.Mockito.mock(jakarta.persistence.criteria.Root.class),
                org.mockito.Mockito.mock(jakarta.persistence.criteria.CriteriaQuery.class),
                org.mockito.Mockito.mock(jakarta.persistence.criteria.CriteriaBuilder.class)
        ));
    }
}