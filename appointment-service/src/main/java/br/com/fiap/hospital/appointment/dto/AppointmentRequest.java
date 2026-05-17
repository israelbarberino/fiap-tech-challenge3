package br.com.fiap.hospital.appointment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AppointmentRequest(
        @NotNull UUID patientId,
        @NotNull UUID doctorId,
        @NotNull OffsetDateTime scheduledAt,
        @Size(max = 4000) String notes
) {
}
