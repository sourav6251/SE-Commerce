package com.ecommerce.adapter.in.web.product.dto;


import com.ecommerce.adapter.out.persistence.jpa.entity.ProductEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private String id;
    @Builder.Default
    private List<String> category = new ArrayList<>();
    private String name;
    private String slug;
    private String description;
    private String sku;
    private BigDecimal price;
    private Integer stockQuantity;
    private String status;
    private Double weight;
    private Double height;
    private Double width;
    private List<MultipartFile> images;
    @Builder.Default
    private List<String> imagesList = new ArrayList<>();
    private Instant createdAt;
    private Instant updatedAt;

    public static ProductDTO fromEntity(ProductEntity entity) {
        if (entity == null) {
            return null;
        }
        return ProductDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .description(entity.getDescription())
                .sku(entity.getSku())
                .price(entity.getPrice())
                .stockQuantity(entity.getStockQuantity())
                .status(entity.getStatus())
                .weight(entity.getWeight())
                .height(entity.getHeight())
                .width(entity.getWidth())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }


}
