package com.ecommerce.application.service.item;

import com.ecommerce.adapter.in.web.item.dto.CategoryDTO;
import com.ecommerce.adapter.out.persistence.jpa.entity.CategoryEntity;
import com.ecommerce.adapter.out.persistence.jpa.repository.CategoryRepository;
import com.ecommerce.application.port.in.Item.CategoryUseCase;
import com.ecommerce.domain.exception.CategotyExcaption;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService implements CategoryUseCase {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryEntity createCategory(CategoryDTO category) {
        if (category.getCategoryCode() == null || category.getCategoryCode().trim().isEmpty()) {
            throw new CategotyExcaption("Enter category code");
        }
        if (categoryRepository.existsByCategoryCode(category.getCategoryCode())) {
            throw new CategotyExcaption("Category code already exists");
        }

        CategoryEntity parentEntity = null;
        if (category.getParentId() != null && !category.getParentId().trim().isEmpty()) {
            parentEntity = categoryRepository.findById(category.getParentId())
                    .orElseThrow(() -> new CategotyExcaption("Parent category not found with ID: " + category.getParentId()));
        }

        try {
            CategoryEntity categoryEntity = CategoryEntity.builder()
                    .categoryCode(category.getCategoryCode())
                    .name(category.getName())
                    .description(category.getDescription())
                    .slug(category.getSlug())
                    .parent(parentEntity)
                    .build();

            return categoryRepository.save(categoryEntity);
        } catch (Exception e) {
            throw new CategotyExcaption("Error in creating category: " + e.getMessage());
        }
    }

    @Override
    public CategoryEntity createSubCategory(CategoryDTO subCategory) {
        if (subCategory.getParentId() == null || subCategory.getParentId().trim().isEmpty()) {
            throw new CategotyExcaption("Parent category ID is required to create a subcategory");
        }
        return createCategory(subCategory);
    }

    @Override
    public CategoryEntity updateCategory(CategoryDTO category) {
        if (category.getId() == null) {
            throw new CategotyExcaption("Category ID is required for update");
        }

        CategoryEntity oldCategoryEntity = categoryRepository.findById(category.getId())
                .orElseThrow(() -> new CategotyExcaption("Category not found"));

        if (category.getCategoryCode() != null) {
            CategoryEntity existingCategory = categoryRepository.findByCategoryCode(category.getCategoryCode());
            if (existingCategory != null && !existingCategory.getId().equals(oldCategoryEntity.getId())) {
                throw new CategotyExcaption("Category code already exists in another category");
            }
            oldCategoryEntity.setCategoryCode(category.getCategoryCode());
        }

        if (category.getName() != null) {
            oldCategoryEntity.setName(category.getName());
        }
        if (category.getDescription() != null) {
            oldCategoryEntity.setDescription(category.getDescription());
        }
        if (category.getSlug() != null) {
            oldCategoryEntity.setSlug(category.getSlug());
        }
        if (category.getParentId() != null) {
            CategoryEntity parent = categoryRepository.findById(category.getParentId())
                    .orElseThrow(() -> new CategotyExcaption("Parent category not found with ID: " + category.getParentId()));
            oldCategoryEntity.setParent(parent);
        }

        try {
            return categoryRepository.save(oldCategoryEntity);
        } catch (Exception e) {
            throw new CategotyExcaption("Error in updating category: " + e.getMessage());
        }
    }

    @Override
    public void deleteCategory(String id) {
        try {
            categoryRepository.deleteById(id);
        } catch (Exception e) {
            throw new CategotyExcaption("Error in deleting category: " + e.getMessage());
        }
    }

    @Override
    public CategoryEntity getCategoryByCode(String code) {
        try {
            return categoryRepository.findByCategoryCode(code);
        } catch (Exception e) {
            throw new CategotyExcaption("Error in getting category by code: " + e.getMessage());
        }
    }

    @Override
    public List<CategoryEntity> getAllCategories() {
        try {
            return categoryRepository.findAll();
        } catch (Exception e) {
            throw new CategotyExcaption("Error in getting categories");
        }
    }

    @Override
    public List<CategoryEntity> getAllRootCategories() {
        try {
            return categoryRepository.findByParentIsNull();
        } catch (Exception e) {
            throw new CategotyExcaption("Error in getting root categories");
        }
    }

    @Override
    public List<CategoryEntity> getSubCategoriesByParentId(String parentId) {
        try {
            return categoryRepository.findByParentId(parentId);
        } catch (Exception e) {
            throw new CategotyExcaption("Error in getting subcategories for parent ID: " + parentId);
        }
    }
}
