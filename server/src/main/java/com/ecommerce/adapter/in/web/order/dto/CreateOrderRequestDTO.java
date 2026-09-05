package com.ecommerce.adapter.in.web.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequestDTO {
    private String shippingAddressId;
    private List<OrderItemRequestDTO> items;
}

