package com.ecommerce.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import io.imagekit.client.ImageKitClient;
import io.imagekit.client.okhttp.ImageKitOkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileUploadConfig {

    @Value("${storage.url:}")
    private String storageUrl;
    @Value("${storage.cloudName:}")
    private String cloudName;
    @Value("${storage.api.key:}")
    private String apiKey;
    @Value("${storage.api.secret:}")
    private String apiSecret;

    @Value("${storage.imagekit.privateKey}")
    private String imagekitPrivateKey;

    @Value("${storage.imagekit.webhookSecret}")
    private String imagekitWebhookSecret;

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(
                ObjectUtils.asMap(
                        "cloud_name", cloudName,
                        "api_key", apiKey,
                        "api_secret", apiSecret,
                        "secure", true));
    }

    @Bean
    public ImageKitClient imageKitClient() {

        return ImageKitOkHttpClient.builder()
                .privateKey(imagekitPrivateKey)
//                .webhookSecret(webhookSecret)
                .build();
    }
}
