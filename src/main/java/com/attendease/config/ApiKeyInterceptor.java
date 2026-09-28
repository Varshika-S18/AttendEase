package com.attendease.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/** Protects all faculty endpoints (/api/** except /api/me/**) with the X-API-Key header. */
@Component
public class ApiKeyInterceptor implements HandlerInterceptor {

    @Value("${attendease.faculty-api-key}")
    private String facultyApiKey;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String key = request.getHeader("X-API-Key");
        if (key == null || !key.equals(facultyApiKey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Missing or invalid X-API-Key header\"}");
            return false;
        }
        return true;
    }
}
