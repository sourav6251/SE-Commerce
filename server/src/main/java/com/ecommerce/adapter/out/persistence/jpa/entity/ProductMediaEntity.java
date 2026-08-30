package com.ecommerce.adapter.out.persistence.jpa.entity;

import com.ecommerce.adapter.out.persistence.enums.MediaType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "product_media")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductMediaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mediaUrl;

    @Column(name = "public_id")
    private String publicId; // Storage ID for deleting from Cloudinary/S3

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private MediaType mediaType = MediaType.IMAGE; // IMAGE or VIDEO

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0; // Sequence control: 0, 1, 2, 3...

    @Column(nullable = false)
    @Builder.Default
    private boolean isPrimary = false; // Flag for cover/thumbnail

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}