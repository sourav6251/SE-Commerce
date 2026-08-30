package com.ecommerce.adapter.out.persistence.jpa.adapter;


import com.ecommerce.adapter.out.persistence.jpa.entity.ProductEntity;
import com.ecommerce.adapter.out.persistence.jpa.repository.ProductRepository;
import com.ecommerce.application.port.out.persistence.ProductPort;
import org.springframework.stereotype.Service;

@Service
public class ProductPersistenceAdapter implements ProductPort {

    private  final ProductRepository productRepository;

    public ProductPersistenceAdapter(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductEntity saveProduct(ProductEntity product) {

        return productRepository.save(product);
    }
}
