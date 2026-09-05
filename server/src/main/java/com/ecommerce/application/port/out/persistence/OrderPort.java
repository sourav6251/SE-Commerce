package com.ecommerce.application.port.out.persistence;

import com.ecommerce.adapter.out.persistence.jpa.entity.OrderEntity;
import com.ecommerce.adapter.out.persistence.jpa.entity.OrderItemEntity;
import com.ecommerce.adapter.out.persistence.jpa.entity.PaymentEntity;

import java.util.List;
import java.util.Optional;

public interface OrderPort {

    OrderEntity saveOrder(OrderEntity order);

    Optional<OrderEntity> findOrderById(String id);

    Optional<OrderEntity> findOrderByIdAndUser(String orderId, String userIdentifier);

    List<OrderEntity> findOrdersByUser(String userIdentifier);

    List<OrderEntity> findAllOrders();

    List<OrderItemEntity> saveAllOrderItems(List<OrderItemEntity> items);

    List<OrderItemEntity> findOrderItemsByOrderId(String orderId);

    PaymentEntity savePayment(PaymentEntity payment);

    Optional<PaymentEntity> findPaymentByOrderId(String orderId);
}

