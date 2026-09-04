package com.ecommerce.application.port.out.notification;

public interface NotificationPort {
    void send(NotificationRequest request);
}