package com.ecommerce.adapter.out.notification.email;

import com.ecommerce.adapter.out.notification.NotificationSender;
import com.ecommerce.adapter.out.notification.properties.NotificationProperties;
import com.ecommerce.application.port.out.notification.NotificationChannel;
import com.ecommerce.application.port.out.notification.NotificationRequest;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SmtpEmailSender implements NotificationSender {

    private final JavaMailSender mailSender;
    private final NotificationProperties notificationProperties;

    @Override
    public boolean supports(NotificationChannel channel) {
        return NotificationChannel.EMAIL.equals(channel);
    }

    @Override
    public void send(NotificationRequest request) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String from = notificationProperties.getMail().getFrom() != null && !notificationProperties.getMail().getFrom().isBlank()
                    ? notificationProperties.getMail().getFrom()
                    : notificationProperties.getMail().getUsername();

            helper.setFrom(from);
            helper.setTo(request.getRecipient());
            helper.setSubject(request.getSubject());
            helper.setText(request.getContent(), true);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email notification", e);
        }
    }
}