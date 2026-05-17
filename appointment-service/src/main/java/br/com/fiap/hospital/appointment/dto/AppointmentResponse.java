package br.com.fiap.hospital.appointment.dto;

import br.com.fiap.hospital.appointment.domain.AppointmentStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AppointmentResponse(
        UUID id,
        UUID patientId,
        UUID doctorId,
        OffsetDateTime scheduledAt,
        String notes,
        AppointmentStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
