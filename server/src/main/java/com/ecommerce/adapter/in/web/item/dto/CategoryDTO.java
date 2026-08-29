package com.ecommerce.adapter.in.web.item.dto;

import com.ecommerce.adapter.out.persistence.jpa.entity.CategoryEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    private String id;
    private String categoryCode;
    private String name;
    private String slug;
    private String description;
    private String parentId;
    @Builder.Default
    private List<CategoryDTO> subCategories = new ArrayList<>();
    private Instant createdAt;

    public static CategoryDTO fromEntity(CategoryEntity categoryEntity) {
        if (categoryEntity == null) {
            return null;
        }

        List<CategoryDTO> subDTOs = Collections.emptyList();
        if (categoryEntity.getSubCategories() != null && !categoryEntity.getSubCategories().isEmpty()) {
            subDTOs = categoryEntity.getSubCategories().stream()
                    .map(CategoryDTO::fromEntitySummary)
                    .toList();
        }

        return CategoryDTO.builder()
                .id(categoryEntity.getId())
                .categoryCode(categoryEntity.getCategoryCode())
                .name(categoryEntity.getName())
                .slug(categoryEntity.getSlug())
                .description(categoryEntity.getDescription())
                .parentId(categoryEntity.getParent() != null ? categoryEntity.getParent().getId() : null)
                .subCategories(subDTOs)
                .createdAt(categoryEntity.getCreatedAt())
                .build();
    }

    public static CategoryDTO fromEntitySummary(CategoryEntity categoryEntity) {
        if (categoryEntity == null) {
            return null;
        }

        return CategoryDTO.builder()
                .id(categoryEntity.getId())
                .categoryCode(categoryEntity.getCategoryCode())
                .name(categoryEntity.getName())
                .slug(categoryEntity.getSlug())
                .description(categoryEntity.getDescription())
                .parentId(categoryEntity.getParent() != null ? categoryEntity.getParent().getId() : null)
                .createdAt(categoryEntity.getCreatedAt())
                .build();
    }
}
