package br.com.fiap.hospital.sharedkernel.appointment;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AppointmentEvent(
        String eventType,
        UUID appointmentId,
        UUID patientId,
        UUID doctorId,
        OffsetDateTime scheduledAt,
        String notes,
        String status
) {
}
