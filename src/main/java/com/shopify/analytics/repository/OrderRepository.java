package com.shopify.analytics.repository;

import com.shopify.analytics.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT COUNT(o) FROM Order o WHERE o.tenantId = :tenantId")
    Long countByTenantId(@Param("tenantId") UUID tenantId);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.tenantId = :tenantId")
    BigDecimal getTotalRevenueByTenantId(@Param("tenantId") UUID tenantId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.tenantId = :tenantId AND o.orderDate BETWEEN :startDate AND :endDate")
    Long countByTenantIdAndDateRange(@Param("tenantId") UUID tenantId,
                                     @Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.tenantId = :tenantId AND o.orderDate BETWEEN :startDate AND :endDate")
    BigDecimal getRevenueByTenantIdAndDateRange(@Param("tenantId") UUID tenantId,
                                                @Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate);

    @Query("SELECT DATE(o.orderDate) as orderDate, COUNT(o) as orderCount, SUM(o.totalAmount) as revenue " +
            "FROM Order o WHERE o.tenantId = :tenantId AND o.orderDate BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(o.orderDate) ORDER BY DATE(o.orderDate)")
    List<Object[]> getDailyOrderTrendsByTenantId(@Param("tenantId") UUID tenantId,
                                                 @Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate);

    @Query("SELECT o.city, COUNT(o) as orderCount, SUM(o.totalAmount) as revenue " +
            "FROM Order o WHERE o.tenantId = :tenantId AND o.city IS NOT NULL " +
            "GROUP BY o.city ORDER BY SUM(o.totalAmount) DESC")
    List<Object[]> getSalesByCity(@Param("tenantId") UUID tenantId);

    @Modifying
    @Query("DELETE FROM Order o WHERE o.tenantId = :tenantId")
    void deleteByTenantId(@Param("tenantId") UUID tenantId);
}