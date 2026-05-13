package com.shopify.analytics.controller;

import com.shopify.analytics.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/{tenantId}/overview")
    public ResponseEntity<Map<String, Object>> getOverviewMetrics(@PathVariable UUID tenantId) {
        Map<String, Object> metrics = analyticsService.getOverviewMetrics(tenantId);
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/{tenantId}/metrics")
    public ResponseEntity<Map<String, Object>> getDateRangeMetrics(
            @PathVariable UUID tenantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        Map<String, Object> metrics = analyticsService.getDateRangeMetrics(tenantId, startDate, endDate);
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/{tenantId}/trends/daily")
    public ResponseEntity<List<Object[]>> getDailyTrends(
            @PathVariable UUID tenantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Object[]> trends = analyticsService.getDailyOrderTrends(tenantId, startDate, endDate);
        return ResponseEntity.ok(trends);
    }

    @GetMapping("/{tenantId}/customers/top")
    public ResponseEntity<List<Object[]>> getTopCustomers(
            @PathVariable UUID tenantId,
            @RequestParam(defaultValue = "5") int limit) {
        List<Object[]> topCustomers = analyticsService.getTopCustomersBySpending(tenantId, limit);
        return ResponseEntity.ok(topCustomers);
    }

    @GetMapping("/{tenantId}/sales/by-city")
    public ResponseEntity<List<Object[]>> getSalesByCity(@PathVariable UUID tenantId) {
        List<Object[]> salesByCity = analyticsService.getSalesByCity(tenantId);
        return ResponseEntity.ok(salesByCity);
    }

    @GetMapping("/{tenantId}/products/by-category")
    public ResponseEntity<List<Object[]>> getProductsByCategory(@PathVariable UUID tenantId) {
        List<Object[]> productsByCategory = analyticsService.getProductCountByCategory(tenantId);
        return ResponseEntity.ok(productsByCategory);
    }

    @GetMapping("/{tenantId}/inventory")
    public ResponseEntity<Map<String, Object>> getInventoryMetrics(@PathVariable UUID tenantId) {
        Map<String, Object> metrics = analyticsService.getInventoryMetrics(tenantId);
        return ResponseEntity.ok(metrics);
    }
}