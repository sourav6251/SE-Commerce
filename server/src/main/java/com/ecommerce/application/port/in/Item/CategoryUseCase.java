package com.ecommerce.application.port.in.Item;

import com.ecommerce.adapter.in.web.item.dto.CategoryDTO;
import com.ecommerce.adapter.out.persistence.jpa.entity.CategoryEntity;

import java.util.List;

public interface CategoryUseCase {

    CategoryDTO createCategory(CategoryDTO category);

    CategoryDTO createSubCategory(CategoryDTO subCategory);

    CategoryDTO updateCategory(CategoryDTO category);

    void deleteCategory(String id);

    CategoryDTO getCategoryByCode(String code);

    CategoryDTO getCategoryById(String id);

    List<CategoryDTO> getAllCategories();

    List<CategoryDTO> getAllRootCategories();

    List<CategoryDTO> getSubCategoriesByParentId(String parentId);
}
