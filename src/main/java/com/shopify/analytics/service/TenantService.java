package com.shopify.analytics.service;

import com.shopify.analytics.entity.Tenant;
import com.shopify.analytics.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantService {

    private final TenantRepository tenantRepository;

    public List<Tenant> getAllActiveTenants() {
        return tenantRepository.findAllActiveTenants();
    }

    public Optional<Tenant> getTenantById(UUID tenantId) {
        return tenantRepository.findById(tenantId);
    }

    public Optional<Tenant> getTenantByShopDomain(String shopDomain) {
        return tenantRepository.findByShopDomain(shopDomain);
    }

    public Tenant createTenant(Tenant tenant) {
        log.info("Creating new tenant for shop: {}", tenant.getShopDomain());
        return tenantRepository.save(tenant);
    }

    public Tenant updateTenant(Tenant tenant) {
        log.info("Updating tenant: {}", tenant.getTenantId());
        return tenantRepository.save(tenant);
    }

    public void deactivateTenant(UUID tenantId) {
        Optional<Tenant> tenant = tenantRepository.findById(tenantId);
        if (tenant.isPresent()) {
            tenant.get().setIsActive(false);
            tenantRepository.save(tenant.get());
            log.info("Deactivated tenant: {}", tenantId);
        }
    }
}