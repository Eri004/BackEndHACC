package com.haccphoenix.api.resource;

import java.util.List;
import java.util.Map;

import com.haccphoenix.api.application.service.UsuarioService;
import com.haccphoenix.api.domain.model.Usuario;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioResource {

    @Inject
    UsuarioService usuarioService;

    @POST
    public Response registrar(Usuario usuario) {
        try {
            Usuario nuevo = usuarioService.registrar(usuario);
            Map<String, Object> response = Map.of(
                "id", nuevo.getId(),
                "nombre", nuevo.getNombre(),
                "apellido", nuevo.getApellido() == null ? "" : nuevo.getApellido(),
                "email", nuevo.getEmail(),
                "rol", nuevo.getRol().name(),
                "activo", nuevo.getActivo()
            );
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error al registrar usuario: " + e.getMessage()))
                .build();
        }
    }

    @GET
    public Response listar() {
        try {
            List<Usuario> usuarios = usuarioService.listar();
            return Response.ok(usuarios).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response obtener(@PathParam("id") Integer id) {
        try {
            return Response.ok(usuarioService.obtener(id)).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response actualizar(@PathParam("id") Integer id, Usuario usuario) {
        try {
            Usuario actualizado = usuarioService.actualizar(id, usuario);
            return Response.ok(Map.of(
                "mensaje", "Usuario actualizado exitosamente",
                "id", actualizado.getId()
            )).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response eliminar(@PathParam("id") Integer id) {
        try {
            usuarioService.eliminar(id);
            return Response.ok(Map.of("mensaje", "Usuario eliminado exitosamente", "id", id)).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
}
