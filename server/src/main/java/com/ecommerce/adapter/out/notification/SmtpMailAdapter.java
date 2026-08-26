package com.ecommerce.adapter.out.notification;

import com.ecommerce.application.port.out.notification.MailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SmtpMailAdapter implements MailSender {

    private final JavaMailSender mailSender;

    @Override
    public void sendMail(String to,String subject,String content) {
        sendMail(List.of(to), subject, content);
    }

    @Override
    public void sendMail(List<String> recipients,String subject,String content) {

        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper =  new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(recipients.toArray(new String[0]));
            helper.setSubject(subject);

            // true = content is HTML
            helper.setText(content, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new MailSendException("Failed to send email");
        }
    }
}