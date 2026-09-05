package com.ecommerce.application.port.in.order;

import com.ecommerce.adapter.in.web.order.dto.CreateOrderRequestDTO;
import com.ecommerce.adapter.in.web.order.dto.OrderDTO;

import java.util.List;

public interface OrderUseCase {

    OrderDTO createOrder(String userIdentifier, CreateOrderRequestDTO request);

    OrderDTO getOrderById(String userIdentifier, String orderId);

    List<OrderDTO> getUserOrders(String userIdentifier);

    OrderDTO cancelOrder(String userIdentifier, String orderId);

    OrderDTO updateOrderStatus(String orderId, String status);
}

