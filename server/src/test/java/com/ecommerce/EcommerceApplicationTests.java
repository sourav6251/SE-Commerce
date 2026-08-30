package com.ecommerce;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ecommerce.adapter.out.storage.cloudinary.CloudinaryStorageAdapter;
import com.ecommerce.adapter.out.storage.cloudinary.dto.FileUploadResponseDTO;
import com.ecommerce.application.port.out.notification.MailSender;
import com.ecommerce.application.port.out.storage.FileStoragePort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@SpringBootTest
@ActiveProfiles("dev")
class EcommerceApplicationTests {

//    @Autowired
    private MailSender mailSender;
//     @Autowired
    private final FileStoragePort fileStoragePort;

    public EcommerceApplicationTests(FileStoragePort fileStoragePort, MailSender mailSender) {
        this.fileStoragePort = fileStoragePort;
        this.mailSender = mailSender;
    }

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

    @Test
    void testFileUpload() throws IOException {
        // 1. Instantiate Cloudinary directly
        // Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
        //         "cloud_name", "dkxei4b5s",
        //         "api_key", "676427825871499",
        //         "api_secret", "N1IkkDZN72MT8imtldClpoNCUfQ",
        //         "secure", true));

        // FileStoragePort storage = new CloudinaryStorageAdapter(cloudinary);

        // 2. Load test image
        ClassPathResource resource = new ClassPathResource("images/jwt-hero.png");
        MultipartFile file = new MockMultipartFile(
                "file",
                "jwt-hero.png",
                "image/png",
                resource.getInputStream()
        );

        // 3. Upload
        FileUploadResponseDTO url = fileStoragePort.upload(file);
        System.out.println("Uploaded URL: " + url);
    }
}
