package com.ecommerce.adapter.in.web.product;

import com.ecommerce.adapter.in.web.product.dto.ProductDTO;
import com.ecommerce.application.port.in.product.ProductUseCase;
import com.ecommerce.domain.auth.ApiResponse;
import com.ecommerce.domain.exception.CategotyExcaption;
import com.ecommerce.domain.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductUseCase productUseCase;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> createProduct(@ModelAttribute ProductDTO productDTO) {
        try {
            ProductDTO created = productUseCase.createProduct(productDTO);
            ApiResponse response = ApiResponse.success()
                    .add("message", "Product created successfully.")
                    .add("product", created);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ProductNotFoundException | CategotyExcaption e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Something went wrong"));
        }
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> updateProduct(@PathVariable("id") String id, @ModelAttribute ProductDTO productDTO) {
        try {
            ProductDTO updated = productUseCase.updateProduct(id, productDTO);
            ApiResponse response = ApiResponse.success()
                    .add("message", "Product updated successfully.")
                    .add("product", updated);
            return ResponseEntity.ok(response);
        } catch (ProductNotFoundException | CategotyExcaption e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Something went wrong"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") String id) {
        try {
            productUseCase.deleteProduct(id);
            ApiResponse response = ApiResponse.success()
                    .add("message", "Product deleted successfully.");
            return ResponseEntity.ok(response);
        } catch (ProductNotFoundException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Something went wrong"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") String id) {
        try {
            ProductDTO product = productUseCase.getProduct(id);
            ApiResponse response = ApiResponse.success()
                    .add("product", product);
            return ResponseEntity.ok(response);
        } catch (ProductNotFoundException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Something went wrong"));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        try {
            List<ProductDTO> products = productUseCase.getAllProducts();
            ApiResponse response = ApiResponse.success()
                    .add("products", products);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Something went wrong"));
        }
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getProductsByCategoryId(@PathVariable("categoryId") String categoryId) {
        try {
            List<ProductDTO> products = productUseCase.getProductsByCategoryId(categoryId);
            ApiResponse response = ApiResponse.success()
                    .add("products", products);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Something went wrong"));
        }
    }
}
