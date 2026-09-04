package com.ecommerce.adapter.out.persistence.jpa.repository;

import com.ecommerce.adapter.out.persistence.jpa.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity, String> {
    @Query("SELECT p FROM ProductEntity p JOIN p.categories c WHERE c.id = :categoryId")
    List<ProductEntity> findByCategoryId(@Param("categoryId") String categoryId);

    @Query("SELECT p FROM ProductEntity p JOIN p.categories c WHERE LOWER(c.name) = LOWER(:categoryName)")
    List<ProductEntity> findByCategoryName(@Param("categoryName") String categoryName);
}

