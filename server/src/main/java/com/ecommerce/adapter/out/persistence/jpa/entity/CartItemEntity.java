package com.ecommerce.adapter.out.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(
    name = "cart_items",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_cart_product", columnNames = {"cart_id", "product_id"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private CartEntity cart;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ProductEntity product;

    @Column(nullable = false)
    private Integer quantity;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}
