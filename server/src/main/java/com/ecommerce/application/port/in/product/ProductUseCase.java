package com.ecommerce.application.port.in.product;

import com.ecommerce.adapter.in.web.product.dto.ProductDTO;

import java.util.List;

public interface ProductUseCase {

    ProductDTO createProduct(ProductDTO productDTO);
    ProductDTO updateProduct(ProductDTO productDTO);
    void deleteProduct(Long id);
    ProductDTO getProduct(Long id);
    List<ProductDTO> getAllProducts();
    List<ProductDTO> getProductsByCategoryId(Long categoryId);
    List<ProductDTO> getProductsByCategoryName(String categoryName);
}
