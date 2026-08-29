package com.ecommerce.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Value("${mail.host}")
    private String host;
    @Value("${mail.port}")
    private Integer port;
    @Value("${mail.username}")
    private String username;
    @Value("${mail.password}")
    private String password;
    @Value("${mail.protocol:smtp}")
    private String protocol;
    @Value("${mail.auth:true}")
    private String auth;
    @Value("${mail.isTlsEnable:true}")
    private Boolean isTlsEnable;
    @Value("${mail.isDebug:true}")
    private Boolean isDebug;

    @Bean
    public JavaMailSender javaMailSender() {

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(host);
        mailSender.setPort(port);

        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties properties = mailSender.getJavaMailProperties();

        properties.put("mail.transport.protocol", protocol);
        properties.put("mail.smtp.auth", auth);
        properties.put("mail.debug", isDebug);

        if (port != null && port == 465) {
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.socketFactory.port", "465");
            properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        } else {
            properties.put("mail.smtp.starttls.enable", isTlsEnable);
        }

        return mailSender;
    }

}
