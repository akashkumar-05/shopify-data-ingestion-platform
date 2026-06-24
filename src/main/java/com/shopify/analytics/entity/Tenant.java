package com.shopify.analytics.entity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tenants", indexes = {
        @Index(name = "idx_tenant_user", columnList = "user_id"),
        @Index(name = "idx_tenant_domain", columnList = "shop_domain")
}, uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "shop_domain"})
})
@Data
public class Tenant {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "tenant_id")
    private UUID tenantId;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "store_name", nullable = false)
    private String storeName;

    @Column(name = "shop_domain", nullable = false)
    private String shopDomain;

    @Column(name = "access_token", nullable = false)
    private String accessToken;

    @Column(name = "api_version")
    private String apiVersion = "2025-01";

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "last_sync_at")
    private LocalDateTime lastSyncAt;

    @Column(name = "sync_status")
    private String syncStatus = "PENDING"; // PENDING, SYNCING, COMPLETED, FAILED

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}