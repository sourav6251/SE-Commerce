package com.ecommerce.application.port.out.notification;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    private NotificationChannel channel;
    private String recipient;              // Email address, Phone number, or Device token
    private List<String> cc;               // Optional for email
    private String subject;                // Subject / Title
    private String content;                // Plain text or HTML body
    private String templateName;           // Optional: Template ID/name
    private Map<String, Object> templateParams; // Optional: dynamic data
}