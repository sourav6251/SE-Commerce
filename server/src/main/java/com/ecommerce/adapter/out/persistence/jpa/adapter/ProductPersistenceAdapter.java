package com.ecommerce.adapter.out.persistence.jpa.adapter;

import com.ecommerce.adapter.out.persistence.jpa.entity.ProductEntity;
import com.ecommerce.adapter.out.persistence.jpa.repository.ProductRepository;
import com.ecommerce.application.port.out.persistence.ProductPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductPersistenceAdapter implements ProductPort {

    private final ProductRepository productRepository;

    public ProductPersistenceAdapter(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductEntity saveProduct(ProductEntity product) {
        return productRepository.save(product);
    }

    @Override
    public Optional<ProductEntity> findById(String id) {
        return productRepository.findById(id);
    }

    @Override
    public void deleteById(String id) {
        productRepository.deleteById(id);
    }

    @Override
    public List<ProductEntity> findAll() {
        return productRepository.findAll();
    }

    @Override
    public List<ProductEntity> findByCategoryId(String categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<ProductEntity> findByCategoryName(String categoryName) {
        return productRepository.findByCategoryName(categoryName);
    }
}

