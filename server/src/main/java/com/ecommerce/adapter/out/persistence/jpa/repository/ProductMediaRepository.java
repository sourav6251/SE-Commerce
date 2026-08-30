package com.ecommerce.adapter.out.persistence.jpa.repository;

import com.ecommerce.adapter.out.persistence.jpa.entity.ProductMediaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductMediaRepository extends JpaRepository<ProductMediaEntity, String> {
    List<ProductMediaEntity> findByProductIdOrderBySortOrderAsc(String productId);
}

