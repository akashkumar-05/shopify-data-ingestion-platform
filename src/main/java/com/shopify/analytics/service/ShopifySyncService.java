package com.shopify.analytics.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopify.analytics.entity.Customer;
import com.shopify.analytics.entity.Order;
import com.shopify.analytics.entity.Product;
import com.shopify.analytics.entity.Tenant;
import com.shopify.analytics.repository.CustomerRepository;
import com.shopify.analytics.repository.OrderRepository;
import com.shopify.analytics.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopifySyncService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private RestTemplate restTemplate;

    // Initialize RestTemplate with timeout
    private RestTemplate getRestTemplate() {
        if (restTemplate == null) {
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(5000);
            factory.setReadTimeout(10000);
            restTemplate = new RestTemplate(factory);
        }
        return restTemplate;
    }

    // ✅ FIXED DOMAIN HANDLING
    private String buildBaseUrl(String shopDomain) {
        if (shopDomain.contains("myshopify.com")) {
            return "https://" + shopDomain;
        } else {
            return "https://" + shopDomain + ".myshopify.com";
        }
    }

    private String buildUrl(Tenant tenant, String endpoint) {
        String baseUrl = buildBaseUrl(tenant.getShopDomain());
        return baseUrl + "/admin/api/" + tenant.getApiVersion() + endpoint;
    }

    private HttpHeaders createHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Shopify-Access-Token", accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    // ===================== CUSTOMERS =====================
    public void syncCustomers(Tenant tenant) {
        try {
            log.info("Syncing customers for tenant: {}", tenant.getStoreName());

            String url = buildUrl(tenant, "/customers.json?limit=250");

            HttpEntity<String> entity = new HttpEntity<>(createHeaders(tenant.getAccessToken()));
            ResponseEntity<String> response = getRestTemplate().exchange(url, HttpMethod.GET, entity, String.class);

            JsonNode customers = objectMapper.readTree(response.getBody()).get("customers");

            List<Customer> customerList = new ArrayList<>();

            for (JsonNode node : customers) {
                Customer customer = new Customer();
                customer.setId(node.get("id").asLong());
                customer.setTenantId(tenant.getTenantId());
                customer.setFirstName(getString(node, "first_name"));
                customer.setLastName(getString(node, "last_name"));
                customer.setEmail(getString(node, "email"));
                customer.setPhone(getString(node, "phone"));
                customer.setOrdersCount(node.get("orders_count").asInt(0));
                customer.setTotalSpent(new BigDecimal(node.get("total_spent").asText("0.00")));

                JsonNode address = node.get("addresses");
                if (address != null && address.isArray() && address.size() > 0) {
                    JsonNode a = address.get(0);
                    customer.setCity(getString(a, "city"));
                    customer.setProvince(getString(a, "province"));
                    customer.setCountry(getString(a, "country"));
                }

                customer.setShopifyCreatedAt(parseDate(node.get("created_at").asText()));
                customer.setShopifyUpdatedAt(parseDate(node.get("updated_at").asText()));

                customerList.add(customer);
            }

            customerRepository.saveAll(customerList);
            log.info("Synced {} customers", customerList.size());

        } catch (Exception e) {
            log.error("Customer sync failed for tenant: {}", tenant.getStoreName(), e);
        }
    }

    // ===================== ORDERS =====================
    public void syncOrders(Tenant tenant) {
        try {
            log.info("Syncing orders for tenant: {}", tenant.getStoreName());

            String url = buildUrl(tenant, "/orders.json?status=any&limit=250");

            HttpEntity<String> entity = new HttpEntity<>(createHeaders(tenant.getAccessToken()));
            ResponseEntity<String> response = getRestTemplate().exchange(url, HttpMethod.GET, entity, String.class);

            JsonNode orders = objectMapper.readTree(response.getBody()).get("orders");

            List<Order> orderList = new ArrayList<>();

            for (JsonNode node : orders) {
                Order order = new Order();
                order.setId(node.get("id").asLong());
                order.setTenantId(tenant.getTenantId());
                order.setOrderNumber(getString(node, "name"));
                order.setTotalAmount(new BigDecimal(node.get("total_price").asText("0.00")));
                order.setSubtotalAmount(new BigDecimal(node.get("subtotal_price").asText("0.00")));
                order.setTaxAmount(new BigDecimal(node.get("total_tax").asText("0.00")));
                order.setCurrency(getString(node, "currency"));
                order.setFinancialStatus(getString(node, "financial_status"));
                order.setFulfillmentStatus(getString(node, "fulfillment_status"));

                JsonNode customerNode = node.get("customer");
                if (customerNode != null) {
                    order.setCustomerId(customerNode.get("id").asLong());
                }

                JsonNode shipping = node.get("shipping_address");
                if (shipping != null) {
                    order.setCity(getString(shipping, "city"));
                    order.setProvince(getString(shipping, "province"));
                    order.setCountry(getString(shipping, "country"));
                }

                order.setOrderDate(parseDate(node.get("created_at").asText()));

                orderList.add(order);
            }

            orderRepository.saveAll(orderList);
            log.info("Synced {} orders", orderList.size());

        } catch (Exception e) {
            log.error("Order sync failed for tenant: {}", tenant.getStoreName(), e);
        }
    }

    // ===================== PRODUCTS =====================
    public void syncProducts(Tenant tenant) {
        try {
            log.info("Syncing products for tenant: {}", tenant.getStoreName());

            String url = buildUrl(tenant, "/products.json?limit=250");

            HttpEntity<String> entity = new HttpEntity<>(createHeaders(tenant.getAccessToken()));
            ResponseEntity<String> response = getRestTemplate().exchange(url, HttpMethod.GET, entity, String.class);

            JsonNode products = objectMapper.readTree(response.getBody()).get("products");

            List<Product> productList = new ArrayList<>();

            for (JsonNode node : products) {
                Product product = new Product();
                product.setId(node.get("id").asLong());
                product.setTenantId(tenant.getTenantId());
                product.setTitle(getString(node, "title"));
                product.setProductType(getString(node, "product_type"));
                product.setVendor(getString(node, "vendor"));
                product.setStatus(getString(node, "status"));

                JsonNode variants = node.get("variants");
                if (variants != null && variants.isArray() && variants.size() > 0) {
                    JsonNode v = variants.get(0);
                    product.setPrice(new BigDecimal(v.get("price").asText("0.00")));
                    product.setInventoryCount(v.get("inventory_quantity").asInt(0));
                }

                product.setShopifyCreatedAt(parseDate(node.get("created_at").asText()));
                product.setShopifyUpdatedAt(parseDate(node.get("updated_at").asText()));

                productList.add(product);
            }

            productRepository.saveAll(productList);
            log.info("Synced {} products", productList.size());

        } catch (Exception e) {
            log.error("Product sync failed for tenant: {}", tenant.getStoreName(), e);
        }
    }

    // ===================== HELPERS =====================
    private String getString(JsonNode node, String field) {
        JsonNode val = node.get(field);
        return (val != null && !val.isNull()) ? val.asText() : null;
    }

    private LocalDateTime parseDate(String date) {
        try {
            return LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception e) {
            log.warn("Date parse failed: {}", date);
            return LocalDateTime.now();
        }
    }
}