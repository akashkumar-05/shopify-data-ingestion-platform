package com.shopify.analytics.scheduler;

import com.shopify.analytics.entity.Tenant;
import com.shopify.analytics.service.ShopifySyncService;
import com.shopify.analytics.service.TenantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShopifyScheduler {

    private final TenantService tenantService;
    private final ShopifySyncService shopifySyncService;

    // Sync every hour (3600000 ms = 1 hour)
    @Scheduled(fixedRate =300000)
    public void syncAllStores() {
        log.info("Starting scheduled sync for all active tenants");

        List<Tenant> tenants = tenantService.getAllActiveTenants();

        for (Tenant tenant : tenants) {
            try {
                log.info("Syncing data for tenant: {}", tenant.getStoreName());

                shopifySyncService.syncCustomers(tenant);
                shopifySyncService.syncOrders(tenant);
                shopifySyncService.syncProducts(tenant);

                log.info("Completed sync for tenant: {}", tenant.getStoreName());

            } catch (Exception e) {
                log.error("Failed to sync data for tenant: {}", tenant.getStoreName(), e);
            }
        }

        log.info("Completed scheduled sync for {} tenants", tenants.size());
    }

    // Sync customers every 30 minutes
    @Scheduled(fixedRate = 1800000)
    public void syncCustomersOnly() {
        log.info("Starting scheduled customer sync for all active tenants");

        List<Tenant> tenants = tenantService.getAllActiveTenants();

        for (Tenant tenant : tenants) {
            try {
                shopifySyncService.syncCustomers(tenant);
            } catch (Exception e) {
                log.error("Failed to sync customers for tenant: {}", tenant.getStoreName(), e);
            }
        }
    }

    // Sync orders every 15 minutes (most critical data)
    @Scheduled(fixedRate = 900000)
    public void syncOrdersOnly() {
        log.info("Starting scheduled order sync for all active tenants");

        List<Tenant> tenants = tenantService.getAllActiveTenants();

        for (Tenant tenant : tenants) {
            try {
                shopifySyncService.syncOrders(tenant);
            } catch (Exception e) {
                log.error("Failed to sync orders for tenant: {}", tenant.getStoreName(), e);
            }
        }
    }
}