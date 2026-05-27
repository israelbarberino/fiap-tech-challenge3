package br.com.fiap.hospital.notification.scheduler;

import br.com.fiap.hospital.notification.service.NotificationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotificationRetryScheduler {

    private final NotificationService notificationService;

    public NotificationRetryScheduler(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Scheduled(fixedDelayString = "${notification.retry.delay-ms:60000}")
    public void retryFailedNotifications() {
        notificationService.retryFailedNotifications();
    }
}
