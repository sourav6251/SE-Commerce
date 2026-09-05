package com.ecommerce.adapter.out.persistence.jpa.repository;

import com.ecommerce.adapter.out.persistence.jpa.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, String> {

    @Query("SELECT o FROM OrderEntity o WHERE o.user.id = :identifier OR o.user.email = :identifier ORDER BY o.createdAt DESC")
    List<OrderEntity> findByUserIdentifierOrderByCreatedAtDesc(@Param("identifier") String identifier);

    @Query("SELECT o FROM OrderEntity o WHERE o.id = :orderId AND (o.user.id = :identifier OR o.user.email = :identifier)")
    Optional<OrderEntity> findByIdAndUserIdentifier(@Param("orderId") String orderId, @Param("identifier") String identifier);
}

