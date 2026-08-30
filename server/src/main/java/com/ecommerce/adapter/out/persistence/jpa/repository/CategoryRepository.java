package com.ecommerce.adapter.out.persistence.jpa.repository;

import com.ecommerce.adapter.out.persistence.jpa.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, String> {

    Boolean existsByCategoryCode(String categoryCode);

    Optional<CategoryEntity> findByCategoryCode(String categoryCode);

    Optional<CategoryEntity> findById(String id);

    List<CategoryEntity> findByParentId(String parentId);

    List<CategoryEntity> findByParentIsNull();
}
