package com.ecommerce.application.port.out.persistence;

import com.ecommerce.adapter.out.persistence.jpa.entity.CartEntity;
import com.ecommerce.adapter.out.persistence.jpa.entity.CartItemEntity;
import com.ecommerce.adapter.out.persistence.jpa.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface CartPort {

    CartEntity findOrCreateCartForUser(UserEntity user);

    Optional<CartEntity> findCartByUserIdentifier(String userIdentifier);

    CartEntity saveCart(CartEntity cart);

    List<CartItemEntity> findCartItems(String cartId);

    Optional<CartItemEntity> findCartItemByProduct(String cartId, String productId);

    CartItemEntity saveCartItem(CartItemEntity item);

    void deleteCartItem(String cartItemId);

    void deleteCartItemByProduct(String cartId, String productId);

    void clearCart(String cartId);
}

