package com.shopify.analytics.controller;

import com.shopify.analytics.entity.Tenant;
import com.shopify.analytics.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @GetMapping("/{tenantId}")
    public ResponseEntity<Tenant> getTenant(@PathVariable UUID tenantId) {
        return tenantService.getTenantById(tenantId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Tenant> createTenant(@RequestBody Tenant tenant) {
        Tenant created = tenantService.createTenant(tenant);
        return ResponseEntity.ok(created);
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