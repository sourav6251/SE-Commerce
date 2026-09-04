package com.ecommerce.adapter.out.notification.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component 
@ConfigurationProperties(prefix = "notification")
public class NotificationProperties {
    private Mail mail = new Mail();
    @Data
    public static class Mail {
        private String host;
        private Integer port;
        private String username;
        private String password;
        private String protocol;
        private String auth;
        private Boolean isTlsEnable;
        private Boolean isDebug;
        private String from;
    }
}
