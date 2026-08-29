package com.ecommerce;

import com.ecommerce.application.port.out.notification.MailSender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
class EcommerceApplicationTests {

    @Autowired
    private MailSender mailSender;

    @Test
    void contextLoads() {
    }

    @Test
    void testSendMail() {
        mailSender.sendMail(
                "dassourav3738@gmail.com",
                "Test Email from E-Commerce App",
                "<h1>Hello Sourav!</h1><p>This is a test email sent from your Spring Boot E-Commerce application.</p>"
        );
    }
}
