package com.ecommerce.application.service.order;

import com.ecommerce.adapter.in.web.address.dto.AddressDTO;
import com.ecommerce.adapter.in.web.order.dto.CreateOrderRequestDTO;
import com.ecommerce.adapter.in.web.order.dto.OrderDTO;
import com.ecommerce.adapter.in.web.order.dto.OrderItemDTO;
import com.ecommerce.adapter.in.web.order.dto.OrderItemRequestDTO;
import com.ecommerce.adapter.in.web.payment.dto.PaymentDTO;
import com.ecommerce.adapter.out.persistence.enums.OrderStatus;
import com.ecommerce.adapter.out.persistence.enums.PaymentMethod;
import com.ecommerce.adapter.out.persistence.enums.PaymentStatus;

import com.ecommerce.adapter.out.persistence.jpa.entity.*;
import com.ecommerce.adapter.out.persistence.jpa.repository.ProductMediaRepository;
import com.ecommerce.application.port.in.order.OrderUseCase;
import com.ecommerce.application.port.out.persistence.*;
import com.ecommerce.domain.exception.ProductNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService implements OrderUseCase {

    private final OrderPort orderPort;
    private final UserPort userPort;
    private final AddressPort addressPort;
    private final ProductPort productPort;
    private final CartPort cartPort;
    private final ProductMediaRepository productMediaRepository;

    @Override
    @Transactional
    public OrderDTO createOrder(String userIdentifier, CreateOrderRequestDTO request) {
        UserEntity user = userPort.findUserEntityByIdOrEmail(userIdentifier);

        // 1. Resolve Shipping Address
        AddressEntity shippingAddress = resolveShippingAddress(userIdentifier, request != null ? request.getShippingAddressId() : null);

        // 2. Resolve Items (from Request items or fallback to User Cart)
        List<OrderItemRequestDTO> itemsToOrder = new ArrayList<>();
        boolean fromCart = false;
        String cartIdToClear = null;

        if (request != null && request.getItems() != null && !request.getItems().isEmpty()) {
            itemsToOrder.addAll(request.getItems());
        } else {
            CartEntity cart = cartPort.findOrCreateCartForUser(user);
            List<CartItemEntity> cartItems = cartPort.findCartItems(cart.getId());
            if (cartItems == null || cartItems.isEmpty()) {
                throw new IllegalArgumentException("Cannot place order: Cart is empty and no items were provided");
            }
            for (CartItemEntity cartItem : cartItems) {
                itemsToOrder.add(OrderItemRequestDTO.builder()
                        .productId(cartItem.getProduct().getId())
                        .quantity(cartItem.getQuantity())
                        .build());
            }
            fromCart = true;
            cartIdToClear = cart.getId();
        }

        // 3. Validate stock, deduct inventory, and compute totals
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItemEntity> orderItems = new ArrayList<>();

        for (OrderItemRequestDTO itemReq : itemsToOrder) {
            if (itemReq.getProductId() == null || itemReq.getProductId().isBlank()) {
                throw new IllegalArgumentException("Product ID cannot be empty");
            }
            int quantity = (itemReq.getQuantity() == null || itemReq.getQuantity() <= 0) ? 1 : itemReq.getQuantity();

            ProductEntity product = productPort.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + itemReq.getProductId()));

            if (product.getStockQuantity() != null && product.getStockQuantity() < quantity) {
                throw new IllegalArgumentException("Insufficient stock for product '" + product.getName() + "'. Available: " + product.getStockQuantity());
            }

            // Deduct stock
            if (product.getStockQuantity() != null) {
                product.setStockQuantity(product.getStockQuantity() - quantity);
                productPort.saveProduct(product);
            }

            BigDecimal unitPrice = product.getPrice() != null ? product.getPrice() : BigDecimal.ZERO;
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
            totalAmount = totalAmount.add(lineTotal);

            OrderItemEntity orderItem = OrderItemEntity.builder()
                    .product(product)
                    .productName(product.getName())
                    .unitPrice(unitPrice)
                    .quantity(quantity)
                    .totalPrice(lineTotal)
                    .build();

            orderItems.add(orderItem);
        }

        // 4. Create and persist Order (Always COD)
        OrderEntity order = OrderEntity.builder()
                .user(user)
                .shippingAddress(shippingAddress)
                .totalAmount(totalAmount)
                .orderStatus(OrderStatus.PLACED)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        order = orderPort.saveOrder(order);


        // 5. Link Order Items to Order and save
        for (OrderItemEntity orderItem : orderItems) {
            orderItem.setOrder(order);
        }
        orderItems = orderPort.saveAllOrderItems(orderItems);

        // 6. Create and persist Payment (Always COD)
        PaymentEntity payment = PaymentEntity.builder()
                .order(order)
                .paymentMethod(PaymentMethod.COD)
                .gatewayTransactionId(null)
                .amount(totalAmount)
                .status(PaymentStatus.PENDING)
                .build();

        payment = orderPort.savePayment(payment);


        // 7. Clear Cart if checked out from cart
        if (fromCart && cartIdToClear != null) {
            cartPort.clearCart(cartIdToClear);
        }

        return mapToOrderDTO(order, orderItems, payment, shippingAddress);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(String userIdentifier, String orderId) {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("Order ID cannot be empty");
        }

        OrderEntity order = orderPort.findOrderByIdAndUser(orderId, userIdentifier)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));

        List<OrderItemEntity> orderItems = orderPort.findOrderItemsByOrderId(order.getId());
        PaymentEntity payment = orderPort.findPaymentByOrderId(order.getId()).orElse(null);

        return mapToOrderDTO(order, orderItems, payment, order.getShippingAddress());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getUserOrders(String userIdentifier) {
        List<OrderEntity> orders = orderPort.findOrdersByUser(userIdentifier);
        List<OrderDTO> orderDTOs = new ArrayList<>();

        for (OrderEntity order : orders) {
            List<OrderItemEntity> orderItems = orderPort.findOrderItemsByOrderId(order.getId());
            PaymentEntity payment = orderPort.findPaymentByOrderId(order.getId()).orElse(null);
            orderDTOs.add(mapToOrderDTO(order, orderItems, payment, order.getShippingAddress()));
        }

        return orderDTOs;
    }

    @Override
    @Transactional
    public OrderDTO cancelOrder(String userIdentifier, String orderId) {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("Order ID cannot be empty");
        }

        OrderEntity order = orderPort.findOrderByIdAndUser(orderId, userIdentifier)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));

        if (order.getOrderStatus() != OrderStatus.PLACED && order.getOrderStatus() != OrderStatus.PENDING) {
            throw new IllegalArgumentException("Cannot cancel an order that is already " + order.getOrderStatus());
        }

        // Restore stock
        List<OrderItemEntity> orderItems = orderPort.findOrderItemsByOrderId(order.getId());
        for (OrderItemEntity item : orderItems) {
            ProductEntity product = item.getProduct();
            if (product != null && product.getStockQuantity() != null) {
                product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                productPort.saveProduct(product);
            }
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        order = orderPort.saveOrder(order);

        PaymentEntity payment = orderPort.findPaymentByOrderId(order.getId()).orElse(null);
        if (payment != null && payment.getStatus() == PaymentStatus.PENDING) {
            payment.setStatus(PaymentStatus.CANCELLED);
            payment = orderPort.savePayment(payment);
        }

        return mapToOrderDTO(order, orderItems, payment, order.getShippingAddress());
    }

    @Override
    @Transactional
    public OrderDTO updateOrderStatus(String orderId, String status) {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("Order ID cannot be empty");
        }
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Status cannot be empty");
        }

        OrderEntity order = orderPort.findOrderById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));

        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid order status: " + status);
        }

        order.setOrderStatus(newStatus);

        PaymentEntity payment = orderPort.findPaymentByOrderId(order.getId()).orElse(null);

        // When order is delivered for COD, mark payment as completed
        if (newStatus == OrderStatus.DELIVERED) {
            order.setPaymentStatus(PaymentStatus.COMPLETED);
            if (payment != null) {
                payment.setStatus(PaymentStatus.COMPLETED);
                payment = orderPort.savePayment(payment);
            }
        } else if (newStatus == OrderStatus.CANCELLED) {
            // Restore inventory if cancelled via status update
            List<OrderItemEntity> items = orderPort.findOrderItemsByOrderId(order.getId());
            for (OrderItemEntity item : items) {
                ProductEntity product = item.getProduct();
                if (product != null && product.getStockQuantity() != null) {
                    product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                    productPort.saveProduct(product);
                }
            }
            if (payment != null && payment.getStatus() == PaymentStatus.PENDING) {
                payment.setStatus(PaymentStatus.CANCELLED);
                payment = orderPort.savePayment(payment);
            }
        }


        order = orderPort.saveOrder(order);
        List<OrderItemEntity> orderItems = orderPort.findOrderItemsByOrderId(order.getId());

        return mapToOrderDTO(order, orderItems, payment, order.getShippingAddress());

    }

    private AddressEntity resolveShippingAddress(String userIdentifier, String shippingAddressId) {
        if (shippingAddressId != null && !shippingAddressId.isBlank()) {
            return addressPort.findByIdAndUserIdentifier(shippingAddressId.trim(), userIdentifier)
                    .orElseThrow(() -> new IllegalArgumentException("Shipping address not found with ID: " + shippingAddressId));
        }

        // Fallback to default address
        List<AddressEntity> defaults = addressPort.findDefaultsByUserIdentifier(userIdentifier);
        if (!defaults.isEmpty()) {
            return defaults.get(0);
        }

        // Fallback to any existing user address
        List<AddressEntity> allAddresses = addressPort.findByUserIdentifier(userIdentifier);
        if (!allAddresses.isEmpty()) {
            return allAddresses.get(0);
        }

        throw new IllegalArgumentException("Please provide or add a shipping address before placing an order");
    }

    private OrderDTO mapToOrderDTO(OrderEntity order, List<OrderItemEntity> items, PaymentEntity payment, AddressEntity shippingAddress) {
        List<OrderItemDTO> itemDTOs = new ArrayList<>();
        if (items != null) {
            for (OrderItemEntity item : items) {
                String primaryImageUrl = null;
                if (item.getProduct() != null) {
                    List<ProductMediaEntity> mediaList = productMediaRepository.findByProductIdOrderBySortOrderAsc(item.getProduct().getId());
                    if (mediaList != null && !mediaList.isEmpty()) {
                        primaryImageUrl = mediaList.stream()
                                .filter(ProductMediaEntity::isPrimary)
                                .map(ProductMediaEntity::getMediaUrl)
                                .findFirst()
                                .orElse(mediaList.get(0).getMediaUrl());
                    }
                }

                itemDTOs.add(OrderItemDTO.builder()
                        .id(item.getId())
                        .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                        .productName(item.getProductName())
                        .productImage(primaryImageUrl)
                        .unitPrice(item.getUnitPrice())
                        .quantity(item.getQuantity())
                        .totalPrice(item.getTotalPrice())
                        .build());
            }
        }

        PaymentDTO paymentDTO = null;
        if (payment != null) {
            paymentDTO = PaymentDTO.builder()
                    .id(payment.getId())
                    .paymentMethod(payment.getPaymentMethod())
                    .gatewayTransactionId(payment.getGatewayTransactionId())
                    .amount(payment.getAmount())
                    .status(payment.getStatus())
                    .createdAt(payment.getCreatedAt())
                    .build();
        }

        AddressDTO addressDTO = shippingAddress != null ? AddressDTO.fromEntity(shippingAddress) : null;

        return OrderDTO.builder()
                .id(order.getId())
                .userId(order.getUser() != null ? order.getUser().getId() : null)
                .shippingAddress(addressDTO)
                .items(itemDTOs)
                .payment(paymentDTO)
                .totalAmount(order.getTotalAmount())
                .orderStatus(order.getOrderStatus())
                .paymentStatus(order.getPaymentStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}

