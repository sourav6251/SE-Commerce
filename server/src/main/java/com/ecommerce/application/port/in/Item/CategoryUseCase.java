package com.ecommerce.application.port.in.Item;

import com.ecommerce.adapter.in.web.item.dto.CategoryDTO;
import com.ecommerce.adapter.out.persistence.jpa.entity.CategoryEntity;

import java.util.List;

public interface CategoryUseCase {

    CategoryEntity createCategory(CategoryDTO category);

    CategoryEntity createSubCategory(CategoryDTO subCategory);

    CategoryEntity updateCategory(CategoryDTO category);

    void deleteCategory(String id);

    CategoryEntity getCategoryByCode(String code);

    List<CategoryEntity> getAllCategories();

    List<CategoryEntity> getAllRootCategories();

    List<CategoryEntity> getSubCategoriesByParentId(String parentId);
}
