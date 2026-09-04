package com.ecommerce.adapter.in.web.cart;

import com.ecommerce.adapter.in.web.cart.dto.AddToCartRequestDTO;
import com.ecommerce.adapter.in.web.cart.dto.CartDTO;
import com.ecommerce.annotation.CurrentUserId;
import com.ecommerce.application.port.in.cart.CartUseCase;
import com.ecommerce.domain.auth.ApiResponse;
import com.ecommerce.domain.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartUseCase cartUseCase;

    @GetMapping
    public ResponseEntity<?> getCart(@CurrentUserId String userID) {
        try {
            CartDTO cart = cartUseCase.getCart(userID);
            ApiResponse response = ApiResponse.success()
                    .add("cart", cart);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | ProductNotFoundException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Something went wrong"));
        }
    }

    @PostMapping("/items")
    public ResponseEntity<?> addToCart(@RequestBody AddToCartRequestDTO request, @CurrentUserId String userID) {
        try {
            CartDTO cart = cartUseCase.addToCart(userID, request.getProductId(), request.getQuantity());
            ApiResponse response = ApiResponse.success()
                    .add("message", "Item added to cart successfully.")
                    .add("cart", cart);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | ProductNotFoundException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Something went wrong"));
        }
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<?> updateItemQuantity(@PathVariable("productId") String productId,
                                                @RequestBody(required = false) AddToCartRequestDTO request,
                                                @RequestParam(value = "quantity", required = false) Integer queryQuantity,
                                                @CurrentUserId String userID) {
        try {
            Integer quantity = (request != null && request.getQuantity() != null) ? request.getQuantity() : queryQuantity;
            CartDTO cart = cartUseCase.updateItemQuantity(userID, productId, quantity);
            ApiResponse response = ApiResponse.success()
                    .add("message", "Cart item updated successfully.")
                    .add("cart", cart);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | ProductNotFoundException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Something went wrong"));
        }
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<?> removeItem(@PathVariable("productId") String productId, @CurrentUserId String userID) {
        try {
            CartDTO cart = cartUseCase.removeItem(userID, productId);
            ApiResponse response = ApiResponse.success()
                    .add("message", "Item removed from cart successfully.")
                    .add("cart", cart);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | ProductNotFoundException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Something went wrong"));
        }
    }

    @DeleteMapping
    public ResponseEntity<?> clearCart(@CurrentUserId String userID) {
        try {
            CartDTO cart = cartUseCase.clearCart(userID);
            ApiResponse response = ApiResponse.success()
                    .add("message", "Cart cleared successfully.")
                    .add("cart", cart);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | ProductNotFoundException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Something went wrong"));
        }
    }
}

