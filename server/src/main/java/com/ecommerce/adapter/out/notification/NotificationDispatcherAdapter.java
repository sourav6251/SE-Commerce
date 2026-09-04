package com.ecommerce.adapter.out.notification;

import com.ecommerce.application.port.out.notification.NotificationChannel;
import com.ecommerce.application.port.out.notification.NotificationPort;
import com.ecommerce.application.port.out.notification.NotificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
@Component
@RequiredArgsConstructor
public class NotificationDispatcherAdapter implements NotificationPort {
    private final List<NotificationSender> senders;
    @Override
    public void send(NotificationRequest request) {
        NotificationSender targetSender = senders.stream()
                .filter(sender -> sender.supports(request.getChannel()))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException(
                        "No sender configured for notification channel: " + request.getChannel()
                ));
        targetSender.send(request);
    }
}
