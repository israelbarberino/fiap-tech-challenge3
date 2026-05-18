package br.com.fiap.hospital.history.consumer;

import br.com.fiap.hospital.history.config.HistoryRabbitConfiguration;
import br.com.fiap.hospital.history.service.HistoryProjectionService;
import br.com.fiap.hospital.sharedkernel.appointment.AppointmentEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class AppointmentHistoryConsumer {

    private final HistoryProjectionService projectionService;

    public AppointmentHistoryConsumer(HistoryProjectionService projectionService) {
        this.projectionService = projectionService;
    }

    @RabbitListener(queues = HistoryRabbitConfiguration.QUEUE)
    public void consume(AppointmentEvent event) {
        projectionService.upsert(event);
    }
}
