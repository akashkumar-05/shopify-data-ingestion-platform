package com.shopify.analytics.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "events", indexes = {
        @Index(name = "idx_event_tenant", columnList = "tenant_id"),
        @Index(name = "idx_event_type", columnList = "event_type"),
        @Index(name = "idx_event_date", columnList = "event_date")
})
@Data
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "event_type", nullable = false)
    private String eventType; // cart_abandoned, checkout_started, product_viewed, etc.

    @Column(name = "event_data", columnDefinition = "TEXT")
    private String eventData; // JSON data

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
