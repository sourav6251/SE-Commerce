package com.ecommerce.application.port.in.product;

import com.ecommerce.adapter.in.web.product.dto.ProductDTO;

import java.util.List;

public interface ProductUseCase {

    ProductDTO createProduct(ProductDTO productDTO);
    ProductDTO updateProduct(String id, ProductDTO productDTO);
    void deleteProduct(String id);
    ProductDTO getProduct(String id);
    List<ProductDTO> getAllProducts();
    List<ProductDTO> getProductsByCategoryId(String categoryId);
    List<ProductDTO> getProductsByCategoryName(String categoryName);
}

