package com.ecommerce.adapter.out.notification;

import com.ecommerce.application.port.out.notification.NotificationChannel;
import com.ecommerce.application.port.out.notification.NotificationRequest;
public interface NotificationSender {
    boolean supports(NotificationChannel channel);
    void send(NotificationRequest request);
}