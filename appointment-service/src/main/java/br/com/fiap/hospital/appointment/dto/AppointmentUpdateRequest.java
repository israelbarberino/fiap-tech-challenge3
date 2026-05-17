package br.com.fiap.hospital.appointment.dto;

import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;

public record AppointmentUpdateRequest(
        OffsetDateTime scheduledAt,
        @Size(max = 4000) String notes
) {
}
