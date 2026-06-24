package com.shopify.analytics.config;

import com.shopify.analytics.entity.User;
import com.shopify.analytics.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String apiKey = request.getHeader("X-API-Key");
        if (apiKey == null || apiKey.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\":false,\"message\":\"Missing X-API-Key header\"}");
            return false;
        }

        Optional<User> userOpt = authService.validateApiKey(apiKey);
        if (userOpt.isEmpty() || !userOpt.get().getIsActive()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\":false,\"message\":\"Invalid or inactive API Key\"}");
            return false;
        }

        User user = userOpt.get();

        // Check rate limit
        if (!authService.checkRateLimit(user)) {
            response.setStatus(429); // Too Many Requests
            response.setContentType("application/json");
            response.getWriter().write("{\"success\":false,\"message\":\"Daily API rate limit exceeded.\"}");
            return false;
        }

        request.setAttribute("user", user);
        return true;
    }
}
