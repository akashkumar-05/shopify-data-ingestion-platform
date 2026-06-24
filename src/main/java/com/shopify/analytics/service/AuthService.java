package com.shopify.analytics.service;

import com.shopify.analytics.entity.User;
import com.shopify.analytics.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;

    // Simple BCrypt-like hashing using SHA-256 + salt
    private String hashPassword(String password) {
        try {
            String salt = "shopify-pulse-salt-2024";
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((salt + password).getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }

    public Map<String, Object> register(String email, String password, String fullName, String companyName) {
        Map<String, Object> result = new HashMap<>();

        if (userRepository.existsByEmail(email)) {
            result.put("success", false);
            result.put("message", "Email already registered");
            return result;
        }

        User user = new User();
        user.setEmail(email.toLowerCase().trim());
        user.setPasswordHash(hashPassword(password));
        user.setFullName(fullName);
        user.setCompanyName(companyName);
        user.setApiKey(generateApiKey());
        user.setApiCallsToday(0);
        user.setApiCallsResetDate(LocalDateTime.now());
        user.setPlan("FREE");
        user.setMaxStores(3);

        userRepository.save(user);
        log.info("New user registered: {}", email);

        result.put("success", true);
        result.put("userId", user.getUserId());
        result.put("apiKey", user.getApiKey());
        result.put("fullName", user.getFullName());
        result.put("email", user.getEmail());
        result.put("plan", user.getPlan());
        result.put("maxStores", user.getMaxStores());
        return result;
    }

    public Map<String, Object> login(String email, String password) {
        Map<String, Object> result = new HashMap<>();

        Optional<User> userOpt = userRepository.findByEmail(email.toLowerCase().trim());

        if (userOpt.isEmpty()) {
            result.put("success", false);
            result.put("message", "Invalid email or password");
            return result;
        }

        User user = userOpt.get();

        if (!user.getPasswordHash().equals(hashPassword(password))) {
            result.put("success", false);
            result.put("message", "Invalid email or password");
            return result;
        }

        if (!user.getIsActive()) {
            result.put("success", false);
            result.put("message", "Account is deactivated");
            return result;
        }

        result.put("success", true);
        result.put("userId", user.getUserId());
        result.put("apiKey", user.getApiKey());
        result.put("fullName", user.getFullName());
        result.put("email", user.getEmail());
        result.put("plan", user.getPlan());
        result.put("maxStores", user.getMaxStores());
        return result;
    }

    public Optional<User> validateApiKey(String apiKey) {
        return userRepository.findByApiKey(apiKey);
    }

    public Optional<User> getUserById(UUID userId) {
        return userRepository.findById(userId);
    }



    public boolean checkRateLimit(User user) {
        LocalDateTime now = LocalDateTime.now();

        // Reset daily counter if needed
        if (user.getApiCallsResetDate() == null || user.getApiCallsResetDate().toLocalDate().isBefore(now.toLocalDate())) {
            user.setApiCallsToday(0);
            user.setApiCallsResetDate(now);
            userRepository.save(user);
        }

        int limit = 50000;

        if (user.getApiCallsToday() >= limit) {
            return false;
        }

        user.setApiCallsToday(user.getApiCallsToday() + 1);
        userRepository.save(user);
        return true;
    }

    private String generateApiKey() {
        return "sp_" + UUID.randomUUID().toString().replace("-", "");
    }
}
