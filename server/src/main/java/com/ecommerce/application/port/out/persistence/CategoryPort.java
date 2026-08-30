package com.ecommerce.application.port.out.persistence;

import com.ecommerce.adapter.out.persistence.jpa.entity.CategoryEntity;

import java.util.List;

public interface CategoryPort {

    CategoryEntity saveCategory(CategoryEntity category);
    void deleteCategory(String ID);
    Boolean existsByCategoryCode(String categoryCode);
    CategoryEntity findByID(String id);
    CategoryEntity findByCategoryCode(String categoryCode);
    Boolean isExistByCategoryCode(String categoryCode);
    List<CategoryEntity> findCategoryByParentCode(String parentCode);
    List<CategoryEntity> findRootCategories();
    List<CategoryEntity> findAllCategories();
}
