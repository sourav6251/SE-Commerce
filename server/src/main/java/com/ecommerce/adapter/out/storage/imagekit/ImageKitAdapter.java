package com.ecommerce.adapter.out.storage.imagekit;

import com.ecommerce.adapter.out.storage.cloudinary.dto.FileUploadResponseDTO;
import com.ecommerce.application.port.out.storage.FileStoragePort;
import com.ecommerce.domain.exception.ImagePixelLimitException;
import com.ecommerce.domain.validation.ImagePixelValidator;
import io.imagekit.client.ImageKitClient;
import io.imagekit.models.files.FileUploadParams;
import io.imagekit.models.files.FileUploadResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@Slf4j
@Service("imageKitAdapter")
public class ImageKitAdapter implements FileStoragePort {

    private final ImageKitClient imageKitClient;

    public ImageKitAdapter(ImageKitClient imageKitClient) {
        this.imageKitClient = imageKitClient;
    }

    @Override
    public FileUploadResponseDTO upload(MultipartFile file) {
        try {
            // 1. Validate image pixel flooding & dimensions
            ImagePixelValidator.validate(file);
            if ( file.getOriginalFilename() == null){
                throw new ImagePixelLimitException("File is corrupted");
            }

            String originalFilename = file.getOriginalFilename();

            try (InputStream inputStream = file.getInputStream()) {
                FileUploadParams params = FileUploadParams.builder()
                        .file(inputStream)
                        .fileName(originalFilename)
                        .useUniqueFileName(true)
                        .build();

                FileUploadResponse response = imageKitClient.files().upload(params);
                String url = response.url().orElse("");
                log.info("Image uploaded successfully to ImageKit: url={}, fileId={} , response ={}", url, response.fileId().orElse(""),response);
                return new FileUploadResponseDTO(url,response.fileId().orElse(""));
            }

        } catch (ImagePixelLimitException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to upload image to ImageKit: {}", e.getMessage(), e);
            throw new RuntimeException("ImageKit upload failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String fileId) {
        try {
            imageKitClient.files().delete(fileId);
            log.info("Deleted image from ImageKit with fileId: {}", fileId);
        } catch (Exception e) {
            log.error("Failed to delete image from ImageKit: {}", e.getMessage(), e);
            throw new RuntimeException("ImageKit delete failed: " + e.getMessage(), e);
        }
    }


    @Override
    public List<String> list(List<MultipartFile> file) {
        return List.of();
    }
}
