package com.hacc.api.resource;

import java.util.Map;

import com.hacc.api.application.service.AuthService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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

            if (email == null || password == null) {
                return Response.status(400)
                        .entity(Map.of(
                                "success", false,
                                "mensaje", "Email y password son obligatorios"
                        ))
                        .build();
            }

            AuthService.AuthResponse user = authService.autenticar(email, password);
            if (user == null) {
    return Response.status(401)
            .entity(Map.of(
                    "success", false,
                    "mensaje", "Usuario no encontrado o credenciales inválidas"
            ))
            .build();
}           

System.out.println("id = " + user.id);
System.out.println("nombre = " + user.nombre);
System.out.println("apellido = " + user.apellido);
System.out.println("rol = " + user.rol);
System.out.println("email = " + user.email);
            return Response.ok(Map.of(
                    "success", true,
                    "id", user.id,
                    "nombre", user.nombre,
                    "apellido", user.apellido,
                    "rol", user.rol,
                    "email", user.email
            )).build();

        } catch (RuntimeException e) {
            e.printStackTrace(); 
            String msg = e.getMessage();

            if (msg != null && msg.contains("Usuario")) {
                return Response.status(404)
                        .entity(Map.of("success", false, "mensaje", msg))
                        .build();
            }

            if (msg != null && msg.contains("Contraseña")) {
                return Response.status(401)
                        .entity(Map.of("success", false, "mensaje", msg))
                        .build();
            }

            return Response.status(500)
                    .entity(Map.of(
                            "success", false,
                            "mensaje", "Error interno del servidor"
                    ))
                    .build();
        }
    }
}
