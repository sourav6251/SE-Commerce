package com.ecommerce.application.port.out.notification;

import java.util.List;

public interface MailSender {
    void sendMail(String to, String subject, String content);
    void sendMail( List<String> recipients, String subject, String content);
}
