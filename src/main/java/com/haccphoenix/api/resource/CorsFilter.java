package com.haccphoenix.api.resource;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Provider
@PreMatching
public class CorsFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final List<String> DEFAULT_ALLOWED_ORIGINS = List.of(
        "http://localhost:5173",
        "http://localhost:4173",
        "https://front-end-hacc.vercel.app"
    );

    @ConfigProperty(name = "app.cors.allowed-origins")
    Optional<String> extraAllowedOrigins;

    private volatile Set<String> allowedOrigins;

    private Set<String> allowedOrigins() {
        if (allowedOrigins == null) {
            Set<String> set = new HashSet<>(DEFAULT_ALLOWED_ORIGINS);
            extraAllowedOrigins.ifPresent(value -> {
                if (!value.isBlank()) {
                    Arrays.stream(value.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .forEach(set::add);
                }
            });
            allowedOrigins = Set.copyOf(set);
        }
        return allowedOrigins;
    }

    private boolean isAllowed(String origin) {
        if (origin == null) return false;
        if (allowedOrigins().contains(origin)) return true;
        for (String allowed : allowedOrigins()) {
            if (allowed.contains("*")) {
                String pattern = allowed.replace(".", "\\.").replace("*", ".*");
                if (origin.matches(pattern)) return true;
            }
        }
        return false;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(requestContext.getMethod())) {
            String origin = requestContext.getHeaderString("Origin");
            requestContext.abortWith(
                jakarta.ws.rs.core.Response.ok()
                    .header("Access-Control-Allow-Origin", isAllowed(origin) ? origin : "")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD")
                    .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
                    .header("Access-Control-Max-Age", "3600")
                    .build()
            );
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) {
        String origin = requestContext.getHeaderString("Origin");
        if (isAllowed(origin)) {
            responseContext.getHeaders().putSingle("Access-Control-Allow-Origin", origin);
            responseContext.getHeaders().putSingle("Access-Control-Allow-Credentials", "true");
            responseContext.getHeaders().putSingle(
                "Access-Control-Allow-Headers",
                "origin, content-type, accept, authorization, X-Requested-With"
            );
            responseContext.getHeaders().putSingle(
                "Access-Control-Allow-Methods",
                "GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD"
            );
        }
    }
}
