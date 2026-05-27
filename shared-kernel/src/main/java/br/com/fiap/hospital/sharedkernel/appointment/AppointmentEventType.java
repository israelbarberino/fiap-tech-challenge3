package br.com.fiap.hospital.sharedkernel.appointment;

public enum AppointmentEventType {
    APPOINTMENT_CREATED("v1.appointment.created"),
    APPOINTMENT_UPDATED("v1.appointment.updated");

    private final String routingKey;

    AppointmentEventType(String routingKey) {
        this.routingKey = routingKey;
    }

    public String routingKey() {
        return routingKey;
    }
}
