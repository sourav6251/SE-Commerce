package com.ecommerce.application.port.out.persistence;

import com.ecommerce.adapter.out.persistence.jpa.entity.ProductEntity;

import java.util.List;
import java.util.Optional;

public interface ProductPort {

    ProductEntity saveProduct(ProductEntity product);
    Optional<ProductEntity> findById(String id);
    void deleteById(String id);
    List<ProductEntity> findAll();
    List<ProductEntity> findByCategoryId(String categoryId);
    List<ProductEntity> findByCategoryName(String categoryName);
}

