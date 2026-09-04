package com.ecommerce.adapter.out.persistence.jpa.repository;

import com.ecommerce.adapter.out.persistence.jpa.entity.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItemEntity, String> {

    List<CartItemEntity> findByCartIdOrderByCreatedAtDesc(String cartId);

    Optional<CartItemEntity> findByCartIdAndProductId(String cartId, String productId);

    @Modifying
    @Query("DELETE FROM CartItemEntity ci WHERE ci.cart.id = :cartId AND ci.product.id = :productId")
    void deleteByCartIdAndProductId(@Param("cartId") String cartId, @Param("productId") String productId);

    @Modifying
    @Query("DELETE FROM CartItemEntity ci WHERE ci.cart.id = :cartId")
    void deleteByCartId(@Param("cartId") String cartId);
}

