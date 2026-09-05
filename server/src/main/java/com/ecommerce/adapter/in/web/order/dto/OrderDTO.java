package com.ecommerce.adapter.in.web.order.dto;

import com.ecommerce.adapter.in.web.address.dto.AddressDTO;
import com.ecommerce.adapter.in.web.payment.dto.PaymentDTO;
import com.ecommerce.adapter.out.persistence.enums.OrderStatus;
import com.ecommerce.adapter.out.persistence.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private String id;
    private String userId;
    private AddressDTO shippingAddress;
    @Builder.Default
    private List<OrderItemDTO> items = new ArrayList<>();
    private PaymentDTO payment;
    private BigDecimal totalAmount;
    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
    private Instant createdAt;
    private Instant updatedAt;
}
