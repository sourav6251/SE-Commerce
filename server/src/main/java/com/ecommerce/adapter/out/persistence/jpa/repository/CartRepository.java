package com.ecommerce.adapter.out.persistence.jpa.repository;

import com.ecommerce.adapter.out.persistence.jpa.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends JpaRepository<CartEntity, String> {

    @Query("SELECT c FROM CartEntity c WHERE c.user.id = :identifier OR c.user.email = :identifier")
    Optional<CartEntity> findByUserIdentifier(@Param("identifier") String identifier);

    Optional<CartEntity> findByUserId(String userId);

    Optional<CartEntity> findByUserEmail(String email);
}

