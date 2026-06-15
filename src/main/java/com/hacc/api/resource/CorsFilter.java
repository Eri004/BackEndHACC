package com.hacc.api.resource;

import java.io.IOException;
import java.util.Set;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.ext.Provider;

@Provider
@PreMatching
public class CorsFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Set<String> ALLOWED_ORIGINS = Set.of(
        "http://localhost:5173",
        "https://front-end-hacc.vercel.app"
    );

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        if ("OPTIONS".equalsIgnoreCase(requestContext.getMethod())) {
            requestContext.abortWith(
                jakarta.ws.rs.core.Response.ok()
                    .header("Access-Control-Allow-Origin",
                            requestContext.getHeaderString("Origin"))
                    .header("Access-Control-Allow-Methods",
                            "GET, POST, PUT, DELETE, OPTIONS")
                    .header("Access-Control-Allow-Headers",
                            "Content-Type, Authorization")
                    .build()
            );
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) {

        String origin = requestContext.getHeaderString("Origin");

        if (origin != null && ALLOWED_ORIGINS.contains(origin)) {

            responseContext.getHeaders().putSingle(
                "Access-Control-Allow-Origin",
                origin
            );

            responseContext.getHeaders().putSingle(
                "Access-Control-Allow-Credentials",
                "true"
            );

            responseContext.getHeaders().putSingle(
                "Access-Control-Allow-Headers",
                "origin, content-type, accept, authorization, X-Requested-With"
            );

            responseContext.getHeaders().putSingle(
                "Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS, HEAD"
            );
        }
    }
}
