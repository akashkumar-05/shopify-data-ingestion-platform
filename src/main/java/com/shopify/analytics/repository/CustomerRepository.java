package com.shopify.analytics.repository;
import com.shopify.analytics.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("SELECT COUNT(c) FROM Customer c WHERE c.tenantId = :tenantId")
    Long countByTenantId(@Param("tenantId") UUID tenantId);

    @Query("SELECT c FROM Customer c WHERE c.tenantId = :tenantId ORDER BY c.totalSpent DESC")
    List<Customer> findTopCustomersBySpending(@Param("tenantId") UUID tenantId,
                                              org.springframework.data.domain.Pageable pageable);

    @Query("SELECT COUNT(c) FROM Customer c WHERE c.tenantId = :tenantId AND c.createdAt BETWEEN :startDate AND :endDate")
    Long countByTenantIdAndDateRange(@Param("tenantId") UUID tenantId,
                                     @Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);

    List<Customer> findByTenantIdAndCity(UUID tenantId, String city);
}