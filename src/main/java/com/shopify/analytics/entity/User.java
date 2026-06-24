package com.shopify.analytics.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_email", columnList = "email", unique = true),
        @Index(name = "idx_user_api_key", columnList = "api_key")
})
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "api_key", unique = true)
    private String apiKey;

    @Column(name = "api_calls_today")
    private Integer apiCallsToday = 0;

    @Column(name = "api_calls_reset_date")
    private LocalDateTime apiCallsResetDate;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_demo")
    private Boolean isDemo = false;

    @Column(name = "plan", nullable = false)
    private String plan = "FREE";

    @Column(name = "max_stores")
    private Integer maxStores = 3;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
