package com.shopify.analytics.repository;

import com.shopify.analytics.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {
    Optional<Tenant> findByUserIdAndShopDomain(UUID userId, String shopDomain);

    @Query("SELECT t FROM Tenant t WHERE t.isActive = true")
    List<Tenant> findAllActiveTenants();

    @Query("SELECT t FROM Tenant t WHERE t.userId = :userId AND t.isActive = true")
    List<Tenant> findByUserId(@Param("userId") UUID userId);

    @Query("SELECT COUNT(t) FROM Tenant t WHERE t.userId = :userId AND t.isActive = true")
    Long countByUserId(@Param("userId") UUID userId);
}
