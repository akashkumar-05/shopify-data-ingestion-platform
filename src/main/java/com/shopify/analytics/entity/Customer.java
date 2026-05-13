package com.shopify.analytics.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "customers", indexes = {
        @Index(name = "idx_customer_tenant", columnList = "tenant_id"),
        @Index(name = "idx_customer_email", columnList = "email")
})
@Data
public class Customer {
    @Id
    private Long id; // Shopify customer ID

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "city")
    private String city;

    @Column(name = "province")
    private String province;

    @Column(name = "country")
    private String country;

    @Column(name = "total_spent", precision = 10, scale = 2)
    private java.math.BigDecimal totalSpent;

    @Column(name = "orders_count")
    private Integer ordersCount = 0;

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