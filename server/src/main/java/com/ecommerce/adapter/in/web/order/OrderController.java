package com.ecommerce.adapter.in.web.order;

import com.ecommerce.adapter.in.web.order.dto.CreateOrderRequestDTO;
import com.ecommerce.adapter.in.web.order.dto.OrderDTO;
import com.ecommerce.adapter.in.web.order.dto.OrderStatusUpdateRequestDTO;
import com.ecommerce.annotation.CurrentUserId;
import com.ecommerce.application.port.in.order.OrderUseCase;
import com.ecommerce.domain.auth.ApiResponse;
import com.ecommerce.domain.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderUseCase orderUseCase;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody(required = true) CreateOrderRequestDTO request,
                                         @CurrentUserId String userID) {
        try {
            OrderDTO order = orderUseCase.createOrder(userID, request );
            ApiResponse response = ApiResponse.success()
                    .add("message", "Order placed successfully with Cash on Delivery (COD).")
                    .add("order", order);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException | ProductNotFoundException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Something went wrong"));
        }
    }

    @GetMapping
    public ResponseEntity<?> getUserOrders(@CurrentUserId String userID) {
        try {
            List<OrderDTO> orders = orderUseCase.getUserOrders(userID);
            ApiResponse response = ApiResponse.success()
                    .add("orders", orders);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Something went wrong"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable("id") String id, @CurrentUserId String userID) {
        try {
            OrderDTO order = orderUseCase.getOrderById(userID, id);
            ApiResponse response = ApiResponse.success()
                    .add("order", order);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Something went wrong"));
        }
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable("id") String id, @CurrentUserId String userID) {
        try {
            OrderDTO order = orderUseCase.cancelOrder(userID, id);
            ApiResponse response = ApiResponse.success()
                    .add("message", "Order cancelled successfully.")
                    .add("order", order);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Something went wrong"));
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable("id") String id,
                                               @RequestBody OrderStatusUpdateRequestDTO request) {
        try {
            OrderDTO order = orderUseCase.updateOrderStatus(id, request.getStatus());
            ApiResponse response = ApiResponse.success()
                    .add("message", "Order status updated successfully.")
                    .add("order", order);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Something went wrong"));
        }
    }
}

