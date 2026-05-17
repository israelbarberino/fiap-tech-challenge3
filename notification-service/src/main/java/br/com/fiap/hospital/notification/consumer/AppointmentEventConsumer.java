package br.com.fiap.hospital.notification.consumer;

import br.com.fiap.hospital.notification.config.NotificationRabbitConfiguration;
import br.com.fiap.hospital.notification.service.NotificationService;
import br.com.fiap.hospital.sharedkernel.appointment.AppointmentEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class AppointmentEventConsumer {

    private final NotificationService notificationService;

    public AppointmentEventConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = NotificationRabbitConfiguration.QUEUE)
    public void consume(AppointmentEvent event) {
        notificationService.registerEvent(event);
    }
}
