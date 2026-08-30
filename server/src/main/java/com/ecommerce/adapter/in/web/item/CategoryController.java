package com.ecommerce.adapter.in.web.item;

import com.ecommerce.adapter.in.web.item.dto.CategoryDTO;
import com.ecommerce.adapter.out.persistence.jpa.entity.CategoryEntity;
import com.ecommerce.application.port.in.Item.CategoryUseCase;
import com.ecommerce.domain.exception.CategotyExcaption;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryUseCase categoryUseCase;

    public CategoryController(CategoryUseCase categoryUseCase) {
        this.categoryUseCase = categoryUseCase;
    }

    @PostMapping()
    public ResponseEntity<?> createCategory(@RequestBody CategoryDTO category) {
        try {
            CategoryDTO categoryDTO = categoryUseCase.createCategory(category);
            return ResponseEntity.ok().body(categoryDTO);
        } catch (CategotyExcaption e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Something went wrong");
        }
    }

    @PostMapping("/subcategory")
    public ResponseEntity<?> createSubCategory(@RequestBody CategoryDTO subCategory) {
        try {
            CategoryDTO categoryDTO = categoryUseCase.createSubCategory(subCategory);
            return ResponseEntity.ok().body(categoryDTO);
        } catch (CategotyExcaption e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Something went wrong");
        }
    }

    @GetMapping()
    public ResponseEntity<?> getAllCategories() {
        try {
            List<CategoryDTO> categoryDTOList = categoryUseCase.getAllCategories();
            return ResponseEntity.ok(categoryDTOList);
        } catch (CategotyExcaption e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Something went wrong");
        }
    }

    @GetMapping("/root")
    public ResponseEntity<?> getAllRootCategories() {
        try {
            List<CategoryDTO> categoryDTOList = categoryUseCase.getAllRootCategories();
            return ResponseEntity.ok(categoryDTOList);
        } catch (CategotyExcaption e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Something went wrong");
        }
    }

    @GetMapping("/{parentId}/subcategories")
    public ResponseEntity<?> getSubCategories(@PathVariable("parentId") String parentId) {
        try {
            List<CategoryDTO> categoryDTOList = categoryUseCase.getSubCategoriesByParentId(parentId);
            return ResponseEntity.ok(categoryDTOList);
        } catch (CategotyExcaption e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Something went wrong");
        }
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteCategory(@RequestBody CategoryDTO category) {
        try {
            categoryUseCase.deleteCategory(category.getId());
            return ResponseEntity.noContent().build();
        } catch (CategotyExcaption e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Something went wrong");
        }
    }

    @PutMapping()
    public ResponseEntity<?> updateCategory(@RequestBody CategoryDTO category) {
        try {
            CategoryDTO categoryDTO = categoryUseCase.updateCategory(category);
            return ResponseEntity.ok().body(categoryDTO);
        } catch (CategotyExcaption e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Something went wrong");
        }
    }
}
