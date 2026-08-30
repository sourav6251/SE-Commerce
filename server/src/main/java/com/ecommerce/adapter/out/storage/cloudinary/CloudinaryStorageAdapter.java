package com.ecommerce.adapter.out.storage.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ecommerce.adapter.out.storage.cloudinary.dto.FileUploadResponseDTO;
import com.ecommerce.application.port.out.storage.FileStoragePort;
import com.ecommerce.domain.exception.ImagePixelLimitException;
import com.ecommerce.domain.validation.ImagePixelValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("cloudinaryAdapter")
public class CloudinaryStorageAdapter implements FileStoragePort {

    private final Cloudinary cloudinary;

    public CloudinaryStorageAdapter(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public FileUploadResponseDTO upload(MultipartFile file) {
        try {
            ImagePixelValidator.validate(file);
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.emptyMap()
            );
            log.info("Image uploaded successfully: {}", result);
            return new FileUploadResponseDTO ((String) result.get("secure_url"),(String) result.get("public_id"));
        } catch (ImagePixelLimitException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to upload image to Cloudinary: {}", e.getMessage(), e);
            throw new RuntimeException("Cloudinary upload failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String fileId) {
        try {
            Map params = cloudinary.uploader().destroy(fileId, ObjectUtils.emptyMap());
            log.info("File delete successfully: {}", params);
            log.info("Deleted image from Cloudinary with ID: {}", fileId);
        } catch (Exception e) {
            log.error("Failed to delete image from Cloudinary: {}", e.getMessage(), e);
            throw new RuntimeException("Cloudinary delete failed: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> list(List<MultipartFile> file) {
        return List.of();
    }
}
