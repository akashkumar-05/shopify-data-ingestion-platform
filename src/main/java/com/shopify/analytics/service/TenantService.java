package com.shopify.analytics.service;

import com.shopify.analytics.entity.Tenant;
import com.shopify.analytics.entity.User;
import com.shopify.analytics.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public List<Tenant> getAllActiveTenants() {
        return tenantRepository.findAllActiveTenants();
    }

    public List<Tenant> getTenantsByUserId(UUID userId) {
        return tenantRepository.findByUserId(userId);
    }

    public Optional<Tenant> getTenantById(UUID tenantId) {
        return tenantRepository.findById(tenantId);
    }

    public Optional<Tenant> getTenantByUserIdAndShopDomain(UUID userId, String shopDomain) {
        return tenantRepository.findByUserIdAndShopDomain(userId, shopDomain);
    }

    @Transactional
    public Tenant createTenant(Tenant tenant, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<Tenant> existingOpt = tenantRepository.findByUserIdAndShopDomain(userId, tenant.getShopDomain());
        if (existingOpt.isPresent()) {
            Tenant existing = existingOpt.get();
            if (existing.getIsActive()) {
                throw new RuntimeException("Store already connected. Please delete it first or use a different store.");
            } else {
                // Reactivate the existing soft-deleted store
                existing.setIsActive(true);
                existing.setUserId(userId);
                existing.setStoreName(tenant.getStoreName());
                existing.setAccessToken(tenant.getAccessToken());
                log.info("Reactivating existing soft-deleted store: {}", existing.getShopDomain());
                return tenantRepository.save(existing);
            }
        }

        Long count = tenantRepository.countByUserId(userId);
        if (user.getMaxStores() != null && count >= user.getMaxStores()) {
            throw new RuntimeException("Store limit reached for plan " + user.getPlan() + " (Max " + user.getMaxStores() + " stores). Please upgrade your subscription.");
        }

        tenant.setUserId(userId);
        log.info("Creating new tenant for shop: {} by user: {}", tenant.getShopDomain(), userId);
        return tenantRepository.save(tenant);
    }

    public Tenant createTenant(Tenant tenant) {
        log.info("Creating new tenant for shop: {}", tenant.getShopDomain());
        return tenantRepository.save(tenant);
    }

    public Tenant updateTenant(Tenant tenant) {
        log.info("Updating tenant: {}", tenant.getTenantId());
        return tenantRepository.save(tenant);
    }

    @Transactional
    public void deactivateTenant(UUID tenantId) {
        Optional<Tenant> tenant = tenantRepository.findById(tenantId);
        if (tenant.isPresent()) {
            log.info("Deleting all data for tenant: {}", tenantId);

            // Delete all related data first
            orderRepository.deleteByTenantId(tenantId);
            customerRepository.deleteByTenantId(tenantId);
            productRepository.deleteByTenantId(tenantId);

            // Delete the tenant record itself
            tenantRepository.delete(tenant.get());
            log.info("Tenant {} and all associated data deleted successfully", tenantId);
        }
    }

    public boolean isOwnedByUser(UUID tenantId, UUID userId) {
        Optional<Tenant> tenant = tenantRepository.findById(tenantId);
        return tenant.isPresent() && userId.equals(tenant.get().getUserId());
    }
}