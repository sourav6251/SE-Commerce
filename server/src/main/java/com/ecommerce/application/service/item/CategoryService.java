package com.ecommerce.application.service.item;

import com.ecommerce.adapter.in.web.item.dto.CategoryDTO;
import com.ecommerce.adapter.out.persistence.jpa.entity.CategoryEntity;
import com.ecommerce.application.port.in.Item.CategoryUseCase;
import com.ecommerce.application.port.out.persistence.CategoryPort;
import com.ecommerce.domain.exception.CategotyExcaption;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService implements CategoryUseCase {

    private final CategoryPort categoryPort;

    public CategoryService( CategoryPort categoryPort) {
        this.categoryPort = categoryPort;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO category) {
        try {
        if (category.getCategoryCode() == null || category.getCategoryCode().trim().isEmpty()) {
            throw new CategotyExcaption("Enter category code");
        }
        if (categoryPort.isExistByCategoryCode(category.getCategoryCode())) {
            throw new CategotyExcaption("Category code already exists");
        }

        CategoryEntity parentEntity = null;
        if (category.getParentId() != null && !category.getParentId().trim().isEmpty()) {
            parentEntity = categoryPort.findByID(category.getParentId());
        }

            CategoryEntity categoryEntity = CategoryEntity.builder()
                    .categoryCode(category.getCategoryCode())
                    .name(category.getName())
                    .description(category.getDescription())
                    .slug(category.getSlug())
                    .parent(parentEntity)
                    .build();

            return CategoryDTO.fromEntity(categoryPort.saveCategory(categoryEntity));
        } catch (CategotyExcaption e){
            throw e;
        } catch (Exception e) {
            throw new CategotyExcaption("Error in creating category: " + e.getMessage());
        }
    }

    @Override
    public CategoryDTO createSubCategory(CategoryDTO subCategory) {
        if (subCategory.getParentId() == null || subCategory.getParentId().trim().isEmpty()) {
            throw new CategotyExcaption("Parent category ID is required to create a subcategory");
        }
        return createCategory(subCategory);
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO category) {
        try {
            if (category.getId() == null) {
                throw new CategotyExcaption("Category ID is required for update");
            }

            CategoryEntity oldCategoryEntity = categoryPort.findByID(category.getId());

            if (category.getCategoryCode() != null) {
                CategoryEntity existingCategory = categoryPort.findByCategoryCode(category.getCategoryCode());
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
                CategoryEntity parent = categoryPort.findByID(category.getParentId());
                oldCategoryEntity.setParent(parent);
            }

            return CategoryDTO.fromEntity(categoryPort.saveCategory(oldCategoryEntity));
        }catch (CategotyExcaption e){
            throw e;
        }
        catch (Exception e) {
            throw new CategotyExcaption("Error in updating category: " + e.getMessage());
        }
    }

    @Override
    public void deleteCategory(String id) {
        try {
            categoryPort.deleteCategory(id);
        } catch (Exception e) {
            throw new CategotyExcaption("Error in deleting category: " + e.getMessage());
        }
    }

    @Override
    public CategoryDTO getCategoryByCode(String code) {
        try {
            return CategoryDTO.fromEntity(categoryPort.findByCategoryCode(code));
        } catch (Exception e) {
            throw new CategotyExcaption("Error in getting category by code: " + e.getMessage());
        }
    }

    @Override
    public CategoryDTO getCategoryById(String id) {
        try {
            return CategoryDTO.fromEntity(categoryPort.findByID(id));
        } catch (Exception e) {
            throw new CategotyExcaption("Error in getting category by id: " + e.getMessage());
        }
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        try {
            return categoryPort.findAllCategories().stream().map(CategoryDTO::fromEntity).toList();
        } catch (Exception e) {
            throw new CategotyExcaption("Error in getting categories");
        }
    }

    @Override
    public List<CategoryDTO> getAllRootCategories() {
        try {
            return categoryPort.findRootCategories().stream().map(CategoryDTO::fromEntity).toList();
        } catch (Exception e) {
            throw new CategotyExcaption("Error in getting root categories");
        }
    }

    @Override
    public List<CategoryDTO> getSubCategoriesByParentId(String parentId) {
        try {
            return categoryPort.findCategoryByParentCode(parentId).stream().map(CategoryDTO::fromEntity).toList();
        } catch (Exception e) {
            throw new CategotyExcaption("Error in getting subcategories for parent ID: " + parentId);
        }
    }
}
