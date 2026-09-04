package com.ecommerce.adapter.in.web.item;

import com.ecommerce.adapter.in.web.item.dto.CategoryDTO;
import com.ecommerce.application.port.in.Item.CategoryUseCase;
import com.ecommerce.domain.auth.ApiResponse;
import com.ecommerce.domain.exception.CategotyExcaption;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryUseCase categoryUseCase;

    @PostMapping()
    public ResponseEntity<?> createCategory(@RequestBody CategoryDTO category) {
        try {
            CategoryDTO categoryDTO = categoryUseCase.createCategory(category);
            ApiResponse response = ApiResponse.success()
                    .add("message", "Category created successfully.")
                    .add("category", categoryDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (CategotyExcaption e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Something went wrong"));
        }
    }

    @PostMapping("/subcategory")
    public ResponseEntity<?> createSubCategory(@RequestBody CategoryDTO subCategory) {
        try {
            CategoryDTO categoryDTO = categoryUseCase.createSubCategory(subCategory);
            ApiResponse response = ApiResponse.success()
                    .add("message", "Subcategory created successfully.")
                    .add("category", categoryDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (CategotyExcaption e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Something went wrong"));
        }
    }

    @GetMapping()
    public ResponseEntity<?> getAllCategories() {
        try {
            List<CategoryDTO> categoryDTOList = categoryUseCase.getAllCategories();
            ApiResponse response = ApiResponse.success()
                    .add("categories", categoryDTOList);
            return ResponseEntity.ok(response);
        } catch (CategotyExcaption e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Something went wrong"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable("id") String id) {
        try {
            CategoryDTO categoryDTO = categoryUseCase.getCategoryById(id);
            ApiResponse response = ApiResponse.success()
                    .add("category", categoryDTO);
            return ResponseEntity.ok(response);
        } catch (CategotyExcaption e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Something went wrong"));
        }
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<?> getCategoryByCode(@PathVariable("code") String code) {
        try {
            CategoryDTO categoryDTO = categoryUseCase.getCategoryByCode(code);
            ApiResponse response = ApiResponse.success()
                    .add("category", categoryDTO);
            return ResponseEntity.ok(response);
        } catch (CategotyExcaption e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Something went wrong"));
        }
    }

    @GetMapping("/root")
    public ResponseEntity<?> getAllRootCategories() {
        try {
            List<CategoryDTO> categoryDTOList = categoryUseCase.getAllRootCategories();
            ApiResponse response = ApiResponse.success()
                    .add("categories", categoryDTOList);
            return ResponseEntity.ok(response);
        } catch (CategotyExcaption e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Something went wrong"));
        }
    }

    @GetMapping("/{parentId}/subcategories")
    public ResponseEntity<?> getSubCategories(@PathVariable("parentId") String parentId) {
        try {
            List<CategoryDTO> categoryDTOList = categoryUseCase.getSubCategoriesByParentId(parentId);
            ApiResponse response = ApiResponse.success()
                    .add("categories", categoryDTOList);
            return ResponseEntity.ok(response);
        } catch (CategotyExcaption e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Something went wrong"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable("id") String id) {
        try {
            categoryUseCase.deleteCategory(id);
            ApiResponse response = ApiResponse.success()
                    .add("message", "Category deleted successfully.");
            return ResponseEntity.ok(response);
        } catch (CategotyExcaption e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Something went wrong"));
        }
    }

    @PutMapping()
    public ResponseEntity<?> updateCategory(@RequestBody CategoryDTO category) {
        try {
            CategoryDTO categoryDTO = categoryUseCase.updateCategory(category);
            ApiResponse response = ApiResponse.success()
                    .add("message", "Category updated successfully.")
                    .add("category", categoryDTO);
            return ResponseEntity.ok(response);
        } catch (CategotyExcaption e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Something went wrong"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategoryById(@PathVariable("id") String id, @RequestBody CategoryDTO category) {
        try {
            category.setId(id);
            CategoryDTO categoryDTO = categoryUseCase.updateCategory(category);
            ApiResponse response = ApiResponse.success()
                    .add("message", "Category updated successfully.")
                    .add("category", categoryDTO);
            return ResponseEntity.ok(response);
        } catch (CategotyExcaption e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Something went wrong"));
        }
    }
}
