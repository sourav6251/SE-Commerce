package com.ecommerce.adapter.out.persistence.jpa.adapter;

import com.ecommerce.adapter.out.persistence.jpa.entity.CategoryEntity;
import com.ecommerce.adapter.out.persistence.jpa.repository.CategoryRepository;
import com.ecommerce.application.port.out.persistence.CategoryPort;
import com.ecommerce.domain.exception.CategotyExcaption;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CategoryPersistenceAdapter implements CategoryPort {

    private final CategoryRepository categoryRepository;

    public CategoryPersistenceAdapter(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryEntity saveCategory(CategoryEntity category) {
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(String ID) {
        categoryRepository.deleteById(ID);
    }

    @Override
    public Boolean existsByCategoryCode(String categoryCode) {
        return categoryRepository.existsByCategoryCode(categoryCode);
    }

    @Override
    public CategoryEntity findByID(String id) {
        return categoryRepository.findById(id).orElseThrow(()-> new CategotyExcaption("Category not found"));
    }

    @Override
    public CategoryEntity findByCategoryCode(String categoryCode) {
       return categoryRepository.findByCategoryCode(categoryCode).orElseThrow(() -> new CategotyExcaption("Category not found"));
    }

    @Override
    public Boolean isExistByCategoryCode(String categoryCode) {
        return categoryRepository.existsByCategoryCode(categoryCode);
    }

    @Override
    public List<CategoryEntity> findCategoryByParentCode(String parentId) {
       return categoryRepository.findByParentId(parentId);
    }

    @Override
    public List<CategoryEntity> findRootCategories() {
        return categoryRepository.findByParentIsNull();
    }

    @Override
    public List<CategoryEntity> findAllCategories() {
        return categoryRepository.findAll();
    }
}
