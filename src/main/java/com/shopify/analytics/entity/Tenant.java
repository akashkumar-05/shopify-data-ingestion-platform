package com.shopify.analytics.entity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tenants")
@Data
public class Tenant {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "tenant_id")
    private UUID tenantId;

    @Column(name = "store_name", nullable = false)
    private String storeName;

    @Column(name = "shop_domain", nullable = false, unique = true)
    private String shopDomain;

    @Column(name = "access_token", nullable = false)
    private String accessToken;

    @Column(name = "api_version")
    private String apiVersion = "2023-10";

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}