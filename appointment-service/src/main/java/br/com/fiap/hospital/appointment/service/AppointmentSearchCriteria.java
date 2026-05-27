package br.com.fiap.hospital.appointment.service;

import br.com.fiap.hospital.appointment.domain.AppointmentStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AppointmentSearchCriteria(
        UUID patientId,
        UUID doctorId,
        AppointmentStatus status,
        OffsetDateTime from,
        OffsetDateTime to
) {
}
