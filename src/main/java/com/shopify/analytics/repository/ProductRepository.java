package com.shopify.analytics.repository;

import com.shopify.analytics.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT COUNT(p) FROM Product p WHERE p.tenantId = :tenantId")
    Long countByTenantId(@Param("tenantId") UUID tenantId);

    @Query("SELECT p.category, COUNT(p) as productCount " +
            "FROM Product p WHERE p.tenantId = :tenantId AND p.category IS NOT NULL " +
            "GROUP BY p.category ORDER BY COUNT(p) DESC")
    List<Object[]> getProductCountByCategory(@Param("tenantId") UUID tenantId);

    List<Product> findByTenantIdAndCategory(UUID tenantId, String category);

    @Query("SELECT SUM(p.inventoryCount) FROM Product p WHERE p.tenantId = :tenantId")
    Long getTotalInventoryByTenantId(@Param("tenantId") UUID tenantId);

    @Modifying
    @Query("DELETE FROM Product p WHERE p.tenantId = :tenantId")
    void deleteByTenantId(@Param("tenantId") UUID tenantId);
}