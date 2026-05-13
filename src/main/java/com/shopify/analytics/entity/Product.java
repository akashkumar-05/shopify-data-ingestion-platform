package com.shopify.analytics.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_product_tenant", columnList = "tenant_id"),
        @Index(name = "idx_product_category", columnList = "category")
})
@Data
public class Product {
    @Id
    private Long id; // Shopify product ID

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "category")
    private String category;

    @Column(name = "product_type")
    private String productType;

    @Column(name = "vendor")
    private String vendor;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "inventory_count")
    private Integer inventoryCount = 0;

    @Column(name = "status")
    private String status;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "shopify_created_at")
    private LocalDateTime shopifyCreatedAt;

    @Column(name = "shopify_updated_at")
    private LocalDateTime shopifyUpdatedAt;
}