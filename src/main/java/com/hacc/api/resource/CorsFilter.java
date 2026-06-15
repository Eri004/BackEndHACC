package com.hacc.api.resource;

import java.io.IOException;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
@Provider
@PreMatching
public class CorsFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(requestContext.getMethod())) {
            requestContext.abortWith(
                jakarta.ws.rs.core.Response.ok()
                    .header("Access-Control-Allow-Origin", "https://front-end-hacc.vercel.app")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
                    .build()
            );
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) {

        responseContext.getHeaders().putSingle(
            "Access-Control-Allow-Origin", 
            "https://front-end-hacc.vercel.app"
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
