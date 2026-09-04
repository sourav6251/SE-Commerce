package com.ecommerce.adapter.out.persistence.jpa.adapter;

import com.ecommerce.adapter.out.persistence.jpa.entity.CartEntity;
import com.ecommerce.adapter.out.persistence.jpa.entity.CartItemEntity;
import com.ecommerce.adapter.out.persistence.jpa.entity.UserEntity;
import com.ecommerce.adapter.out.persistence.jpa.repository.CartItemRepository;
import com.ecommerce.adapter.out.persistence.jpa.repository.CartRepository;
import com.ecommerce.application.port.out.persistence.CartPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartPersistenceAdapter implements CartPort {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    @Transactional
    public CartEntity findOrCreateCartForUser(UserEntity user) {
        return cartRepository.findByUserIdentifier(user.getId())
                .or(() -> cartRepository.findByUserIdentifier(user.getEmail()))
                .orElseGet(() -> {
                    CartEntity newCart = CartEntity.builder()
                            .user(user)
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    @Override
    public Optional<CartEntity> findCartByUserIdentifier(String userIdentifier) {
        return cartRepository.findByUserIdentifier(userIdentifier);
    }

    @Override
    public CartEntity saveCart(CartEntity cart) {
        return cartRepository.save(cart);
    }

    @Override
    public List<CartItemEntity> findCartItems(String cartId) {
        return cartItemRepository.findByCartIdOrderByCreatedAtDesc(cartId);
    }

    @Override
    public Optional<CartItemEntity> findCartItemByProduct(String cartId, String productId) {
        return cartItemRepository.findByCartIdAndProductId(cartId, productId);
    }

    @Override
    public CartItemEntity saveCartItem(CartItemEntity item) {
        return cartItemRepository.save(item);
    }

    @Override
    public void deleteCartItem(String cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    @Override
    public void deleteCartItemByProduct(String cartId, String productId) {
        cartItemRepository.deleteByCartIdAndProductId(cartId, productId);
    }

    @Override
    public void clearCart(String cartId) {
        cartItemRepository.deleteByCartId(cartId);
    }
}

