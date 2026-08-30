package com.ecommerce.application.port.out.persistence;

import com.ecommerce.adapter.out.persistence.jpa.entity.ProductEntity;

public interface ProductPort {

    ProductEntity saveProduct(ProductEntity product);


}
