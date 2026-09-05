package com.ecommerce.adapter.out.persistence.jpa.adapter;

import com.ecommerce.adapter.out.persistence.jpa.entity.OrderEntity;
import com.ecommerce.adapter.out.persistence.jpa.entity.OrderItemEntity;
import com.ecommerce.adapter.out.persistence.jpa.entity.PaymentEntity;
import com.ecommerce.adapter.out.persistence.jpa.repository.OrderItemRepository;
import com.ecommerce.adapter.out.persistence.jpa.repository.OrderRepository;
import com.ecommerce.adapter.out.persistence.jpa.repository.PaymentRepository;
import com.ecommerce.application.port.out.persistence.OrderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderPersistenceAdapter implements OrderPort {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public OrderEntity saveOrder(OrderEntity order) {
        return orderRepository.save(order);
    }

    @Override
    public Optional<OrderEntity> findOrderById(String id) {
        return orderRepository.findById(id);
    }

    @Override
    public Optional<OrderEntity> findOrderByIdAndUser(String orderId, String userIdentifier) {
        return orderRepository.findByIdAndUserIdentifier(orderId, userIdentifier);
    }

    @Override
    public List<OrderEntity> findOrdersByUser(String userIdentifier) {
        return orderRepository.findByUserIdentifierOrderByCreatedAtDesc(userIdentifier);
    }

    @Override
    public List<OrderEntity> findAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<OrderItemEntity> saveAllOrderItems(List<OrderItemEntity> items) {
        return orderItemRepository.saveAll(items);
    }

    @Override
    public List<OrderItemEntity> findOrderItemsByOrderId(String orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    @Override
    public PaymentEntity savePayment(PaymentEntity payment) {
        return paymentRepository.save(payment);
    }

    @Override
    public Optional<PaymentEntity> findPaymentByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId);
    }
}

