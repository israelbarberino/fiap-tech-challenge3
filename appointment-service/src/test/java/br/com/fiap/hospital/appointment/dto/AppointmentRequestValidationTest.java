package br.com.fiap.hospital.appointment.dto;

import jakarta.validation.Validation;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AppointmentRequestValidationTest {

    @Test
    void shouldRejectPastScheduledAt() {
        var validator = Validation.buildDefaultValidatorFactory().getValidator();
        var request = new AppointmentRequest(UUID.randomUUID(), UUID.randomUUID(), OffsetDateTime.now().minusDays(1), "notes");

        assertThat(validator.validate(request))
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("scheduledAt"));
    }
}
