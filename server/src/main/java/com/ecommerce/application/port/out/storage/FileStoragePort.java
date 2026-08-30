package com.ecommerce.application.port.out.storage;

import com.ecommerce.adapter.out.storage.cloudinary.dto.FileUploadResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileStoragePort {
    FileUploadResponseDTO upload(MultipartFile file);
    void delete(String fileId);
    List<String> list(List<MultipartFile> file);

}
