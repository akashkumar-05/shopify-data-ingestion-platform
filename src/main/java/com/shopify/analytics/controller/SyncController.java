package com.shopify.analytics.controller;

import com.shopify.analytics.service.ShopifySyncService;
import com.shopify.analytics.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/sync")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SyncController {

    private final ShopifySyncService shopifySyncService;
    private final TenantService tenantService;

    @PostMapping("/{tenantId}/customers")
    public ResponseEntity<String> syncCustomers(@PathVariable UUID tenantId) {
        return tenantService.getTenantById(tenantId)
                .map(tenant -> {
                    shopifySyncService.syncCustomers(tenant);
                    return ResponseEntity.ok("Customers sync initiated for tenant: " + tenant.getStoreName());
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{tenantId}/orders")
    public ResponseEntity<String> syncOrders(@PathVariable UUID tenantId) {
        return tenantService.getTenantById(tenantId)
                .map(tenant -> {
                    shopifySyncService.syncOrders(tenant);
                    return ResponseEntity.ok("Orders sync initiated for tenant: " + tenant.getStoreName());
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{tenantId}/products")
    public ResponseEntity<String> syncProducts(@PathVariable UUID tenantId) {
        return tenantService.getTenantById(tenantId)
                .map(tenant -> {
                    shopifySyncService.syncProducts(tenant);
                    return ResponseEntity.ok("Products sync initiated for tenant: " + tenant.getStoreName());
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{tenantId}/all")
    public ResponseEntity<String> syncAll(@PathVariable UUID tenantId) {
        return tenantService.getTenantById(tenantId)
                .map(tenant -> {
                    shopifySyncService.syncCustomers(tenant);
                    shopifySyncService.syncOrders(tenant);
                    shopifySyncService.syncProducts(tenant);
                    return ResponseEntity.ok("Full sync initiated for tenant: " + tenant.getStoreName());
                })
                .orElse(ResponseEntity.notFound().build());
    }
}