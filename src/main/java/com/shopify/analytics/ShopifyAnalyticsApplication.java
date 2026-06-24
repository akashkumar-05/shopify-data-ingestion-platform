package com.shopify.analytics;

import com.shopify.analytics.entity.Tenant;
import com.shopify.analytics.entity.User;
import com.shopify.analytics.repository.UserRepository;
import com.shopify.analytics.service.AuthService;
import com.shopify.analytics.service.TenantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties
@Slf4j
public class ShopifyAnalyticsApplication {
	public static void main(String[] args) {
		SpringApplication.run(ShopifyAnalyticsApplication.class, args);
	}

	@Value("${app.demo-mode:false}")
	private boolean demoMode;

	@Value("${app.demo-email:demo@shopifypulse.com}")
	private String demoEmail;

	@Value("${app.demo-password:demo123}")
	private String demoPassword;

	@Value("${shopify.store.url:}")
	private String shopifyStoreUrl;

	@Value("${shopify.access.token:}")
	private String shopifyAccessToken;

	@Value("${shopify.api.version:2025-01}")
	private String shopifyApiVersion;

	@Bean
	CommandLineRunner initDemoData(AuthService authService, TenantService tenantService, UserRepository userRepository) {
		return args -> {
			if (demoMode) {
				log.info("=== DEMO MODE ENABLED ===");

				// Create demo user if not exists
				if (!userRepository.existsByEmail(demoEmail)) {
					var result = authService.register(demoEmail, demoPassword, "Demo User", "ShopifyPulse Demo");
					if ((boolean) result.get("success")) {
						log.info("Demo user created: {}", demoEmail);

						// Create demo tenant if shopify credentials are provided
						if (shopifyStoreUrl != null && !shopifyStoreUrl.isEmpty() && shopifyAccessToken != null && !shopifyAccessToken.isEmpty()) {
							String domain = shopifyStoreUrl.replace("https://", "").replace("http://", "");
							Tenant demoTenant = new Tenant();
							demoTenant.setStoreName("Demo Store");
							demoTenant.setShopDomain(domain);
							demoTenant.setAccessToken(shopifyAccessToken);
							demoTenant.setApiVersion(shopifyApiVersion);

							try {
								tenantService.createTenant(demoTenant, (java.util.UUID) result.get("userId"));
								log.info("Demo tenant created: {}", domain);
							} catch (Exception e) {
								log.warn("Demo tenant may already exist: {}", e.getMessage());
							}
						}
					}
				} else {
					log.info("Demo user already exists: {}", demoEmail);
				}
				log.info("=== Demo login: {} / {} ===", demoEmail, demoPassword);
			}
		};
	}
}
