package com.ecommerce.adapter.out.storage.cloudinary.dto;

public record FileUploadResponseDTO(
        String url,
        String publicId
) {}