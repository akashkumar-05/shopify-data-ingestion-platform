package com.shopify.analytics.service;

import com.shopify.analytics.repository.CustomerRepository;
import com.shopify.analytics.repository.OrderRepository;
import com.shopify.analytics.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public Map<String, Object> getOverviewMetrics(UUID tenantId) {
        Map<String, Object> metrics = new HashMap<>();

        // Basic counts
        Long totalCustomers = customerRepository.countByTenantId(tenantId);
        Long totalOrders = orderRepository.countByTenantId(tenantId);
        Long totalProducts = productRepository.countByTenantId(tenantId);
        BigDecimal totalRevenue = orderRepository.getTotalRevenueByTenantId(tenantId);

        metrics.put("totalCustomers", totalCustomers);
        metrics.put("totalOrders", totalOrders);
        metrics.put("totalProducts", totalProducts);
        metrics.put("totalRevenue", totalRevenue);

        // Average order value
        if (totalOrders > 0) {
            BigDecimal avgOrderValue = totalRevenue.divide(new BigDecimal(totalOrders), 2, BigDecimal.ROUND_HALF_UP);
            metrics.put("avgOrderValue", avgOrderValue);
        } else {
            metrics.put("avgOrderValue", BigDecimal.ZERO);
        }

        return metrics;
    }

    public Map<String, Object> getDateRangeMetrics(UUID tenantId, LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> metrics = new HashMap<>();

        Long ordersInPeriod = orderRepository.countByTenantIdAndDateRange(tenantId, startDate, endDate);
        BigDecimal revenueInPeriod = orderRepository.getRevenueByTenantIdAndDateRange(tenantId, startDate, endDate);
        Long customersInPeriod = customerRepository.countByTenantIdAndDateRange(tenantId, startDate, endDate);

        metrics.put("ordersInPeriod", ordersInPeriod);
        metrics.put("revenueInPeriod", revenueInPeriod);
        metrics.put("customersInPeriod", customersInPeriod);

        return metrics;
    }

    public List<Object[]> getDailyOrderTrends(UUID tenantId, LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.getDailyOrderTrendsByTenantId(tenantId, startDate, endDate);
    }

    public List<Object[]> getTopCustomersBySpending(UUID tenantId, int limit) {
        return customerRepository.findTopCustomersBySpending(tenantId, PageRequest.of(0, limit))
                .stream()
                .map(customer -> new Object[]{
                        customer.getId(),
                        customer.getFirstName() + " " + customer.getLastName(),
                        customer.getEmail(),
                        customer.getTotalSpent(),
                        customer.getOrdersCount()
                })
                .toList();
    }

    public List<Object[]> getSalesByCity(UUID tenantId) {
        return orderRepository.getSalesByCity(tenantId);
    }

    public List<Object[]> getProductCountByCategory(UUID tenantId) {
        return productRepository.getProductCountByCategory(tenantId);
    }

    public Map<String, Object> getInventoryMetrics(UUID tenantId) {
        Map<String, Object> metrics = new HashMap<>();

        Long totalInventory = productRepository.getTotalInventoryByTenantId(tenantId);
        metrics.put("totalInventory", totalInventory);

        return metrics;
    }
}