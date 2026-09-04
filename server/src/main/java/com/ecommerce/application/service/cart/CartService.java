package com.ecommerce.application.service.cart;

import com.ecommerce.adapter.in.web.cart.dto.CartDTO;
import com.ecommerce.adapter.in.web.cart.dto.CartItemDTO;
import com.ecommerce.adapter.out.persistence.jpa.entity.CartEntity;
import com.ecommerce.adapter.out.persistence.jpa.entity.CartItemEntity;
import com.ecommerce.adapter.out.persistence.jpa.entity.ProductEntity;
import com.ecommerce.adapter.out.persistence.jpa.entity.ProductMediaEntity;
import com.ecommerce.adapter.out.persistence.jpa.entity.UserEntity;
import com.ecommerce.adapter.out.persistence.jpa.repository.ProductMediaRepository;
import com.ecommerce.application.port.in.cart.CartUseCase;
import com.ecommerce.application.port.out.persistence.CartPort;
import com.ecommerce.application.port.out.persistence.ProductPort;
import com.ecommerce.application.port.out.persistence.UserPort;
import com.ecommerce.domain.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService implements CartUseCase {

    private final CartPort cartPort;
    private final UserPort userPort;
    private final ProductPort productPort;
    private final ProductMediaRepository productMediaRepository;

    @Override
    @Transactional
    public CartDTO getCart(String userIdentifier) {
        UserEntity user = userPort.findUserEntityByIdOrEmail(userIdentifier);
        CartEntity cart = cartPort.findOrCreateCartForUser(user);
        return mapToCartDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO addToCart(String userIdentifier, String productId, Integer quantity) {
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("Product ID cannot be empty");
        }
        int qtyToAdd = (quantity == null || quantity <= 0) ? 1 : quantity;

        UserEntity user = userPort.findUserEntityByIdOrEmail(userIdentifier);
        CartEntity cart = cartPort.findOrCreateCartForUser(user);

        ProductEntity product = productPort.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));

        Optional<CartItemEntity> existingItemOpt = cartPort.findCartItemByProduct(cart.getId(), productId);

        int totalNewQuantity = qtyToAdd;
        if (existingItemOpt.isPresent()) {
            totalNewQuantity += existingItemOpt.get().getQuantity();
        }

        if (product.getStockQuantity() != null && totalNewQuantity > product.getStockQuantity()) {
            throw new IllegalArgumentException("Requested quantity (" + totalNewQuantity + ") exceeds available stock (" + product.getStockQuantity() + ")");
        }

        if (existingItemOpt.isPresent()) {
            CartItemEntity item = existingItemOpt.get();
            item.setQuantity(totalNewQuantity);
            cartPort.saveCartItem(item);
        } else {
            CartItemEntity newItem = CartItemEntity.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(qtyToAdd)
                    .build();
            cartPort.saveCartItem(newItem);
        }

        return mapToCartDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO updateItemQuantity(String userIdentifier, String productId, Integer quantity) {
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("Product ID cannot be empty");
        }

        UserEntity user = userPort.findUserEntityByIdOrEmail(userIdentifier);
        CartEntity cart = cartPort.findOrCreateCartForUser(user);

        if (quantity == null || quantity <= 0) {
            cartPort.deleteCartItemByProduct(cart.getId(), productId);
            return mapToCartDTO(cart);
        }

        ProductEntity product = productPort.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));

        if (product.getStockQuantity() != null && quantity > product.getStockQuantity()) {
            throw new IllegalArgumentException("Requested quantity (" + quantity + ") exceeds available stock (" + product.getStockQuantity() + ")");
        }

        CartItemEntity item = cartPort.findCartItemByProduct(cart.getId(), productId)
                .orElseThrow(() -> new IllegalArgumentException("Product is not in the cart"));

        item.setQuantity(quantity);
        cartPort.saveCartItem(item);

        return mapToCartDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO removeItem(String userIdentifier, String productId) {
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("Product ID cannot be empty");
        }

        UserEntity user = userPort.findUserEntityByIdOrEmail(userIdentifier);
        CartEntity cart = cartPort.findOrCreateCartForUser(user);

        cartPort.deleteCartItemByProduct(cart.getId(), productId);
        return mapToCartDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO clearCart(String userIdentifier) {
        UserEntity user = userPort.findUserEntityByIdOrEmail(userIdentifier);
        CartEntity cart = cartPort.findOrCreateCartForUser(user);

        cartPort.clearCart(cart.getId());
        return mapToCartDTO(cart);
    }

    private CartDTO mapToCartDTO(CartEntity cart) {
        List<CartItemEntity> items = cartPort.findCartItems(cart.getId());
        List<CartItemDTO> itemDTOs = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalItems = 0;

        for (CartItemEntity item : items) {
            ProductEntity product = item.getProduct();
            BigDecimal price = product.getPrice() != null ? product.getPrice() : BigDecimal.ZERO;
            BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(item.getQuantity()));

            totalAmount = totalAmount.add(itemTotal);
            totalItems += item.getQuantity();

            String primaryImageUrl = null;
            List<ProductMediaEntity> mediaList = productMediaRepository.findByProductIdOrderBySortOrderAsc(product.getId());
            if (mediaList != null && !mediaList.isEmpty()) {
                primaryImageUrl = mediaList.stream()
                        .filter(ProductMediaEntity::isPrimary)
                        .map(ProductMediaEntity::getMediaUrl)
                        .findFirst()
                        .orElse(mediaList.get(0).getMediaUrl());
            }

            CartItemDTO itemDTO = CartItemDTO.builder()
                    .id(item.getId())
                    .productId(product.getId())
                    .productName(product.getName())
                    .productSlug(product.getSlug())
                    .productImage(primaryImageUrl)
                    .price(price)
                    .quantity(item.getQuantity())
                    .totalPrice(itemTotal)
                    .stockQuantity(product.getStockQuantity())
                    .build();

            itemDTOs.add(itemDTO);
        }

        return CartDTO.builder()
                .id(cart.getId())
                .items(itemDTOs)
                .totalItems(totalItems)
                .totalAmount(totalAmount)
                .build();
    }
}

