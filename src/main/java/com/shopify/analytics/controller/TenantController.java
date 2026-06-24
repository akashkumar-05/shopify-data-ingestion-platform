package com.shopify.analytics.controller;

import com.shopify.analytics.entity.Tenant;
import com.shopify.analytics.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TenantController {

    private final TenantService tenantService;

    @GetMapping
    public ResponseEntity<List<Tenant>> getAllTenants() {
        return ResponseEntity.ok(tenantService.getAllActiveTenants());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Tenant>> getTenantsByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(tenantService.getTenantsByUserId(userId));
    }

    @GetMapping("/{tenantId}")
    public ResponseEntity<Tenant> getTenant(@PathVariable UUID tenantId) {
        return tenantService.getTenantById(tenantId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createTenant(@RequestBody Tenant tenant, @RequestParam(required = false) UUID userId) {
        try {
            Tenant created;
            if (userId != null) {
                created = tenantService.createTenant(tenant, userId);
            } else {
                created = tenantService.createTenant(tenant);
            }
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @PutMapping("/{tenantId}")
    public ResponseEntity<Tenant> updateTenant(@PathVariable UUID tenantId, @RequestBody Tenant tenant) {
        tenant.setTenantId(tenantId);
        Tenant updated = tenantService.updateTenant(tenant);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{tenantId}")
    public ResponseEntity<Void> deactivateTenant(@PathVariable UUID tenantId) {
        tenantService.deactivateTenant(tenantId);
        return ResponseEntity.ok().build();
    }
}