package com.haccphoenix.api.resource;

import com.haccphoenix.api.application.service.AuthService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    AuthService authService;

    @POST
    @Path("/login")
    public Response login(Map<String, String> credentials) {
        try {
            String email = credentials.get("email");
            String password = credentials.get("password");

            AuthService.AuthResponse user = authService.autenticar(email, password);

            return Response.ok(Map.of(
                "success", true,
                "id", user.id,
                "nombre", user.nombre,
                "apellido", user.apellido,
                "rol", user.rol,
                "email", user.email
            )).build();

        } catch (RuntimeException e) {
            return Response.status(401).entity(Map.of(
                "success", false,
                "mensaje", e.getMessage()
            )).build();
        }
    }
}
