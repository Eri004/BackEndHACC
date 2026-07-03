package com.haccphoenix.api.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.haccphoenix.api.application.service.InquilinoService;
import com.haccphoenix.api.domain.model.Inquilino;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/inquilinos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InquilinoResource {

    @Inject
    InquilinoService inquilinoService;

    @POST
    @Path("/registro/{departamentoId}")
    public Response registrar(@PathParam("departamentoId") Integer departamentoId, Inquilino inquilino) {
        try {
            Inquilino nuevo = inquilinoService.registrar(inquilino, departamentoId);
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Inquilino registrado exitosamente");
            response.put("id", nuevo.getId());
            response.put("nombre", nuevo.getNombre() + " " + nuevo.getApellido());
            response.put("departamentoId", departamentoId);
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error al registrar inquilino: " + e.getMessage()))
                .build();
        }
    }

    @GET
    public Response listar() {
        try {
            List<Inquilino> inquilinos = inquilinoService.listar();
            return Response.ok(inquilinos).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error al listar inquilinos: " + e.getMessage()))
                .build();
        }
    }

    @GET
    @Path("/activos")
    public Response listarActivos() {
        try {
            return Response.ok(inquilinoService.listarActivos()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error al listar inquilinos activos: " + e.getMessage()))
                .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response obtener(@PathParam("id") Integer id) {
        try {
            return Response.ok(inquilinoService.obtener(id)).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error al obtener inquilino: " + e.getMessage()))
                .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response actualizar(@PathParam("id") Integer id, Inquilino inquilino) {
        try {
            Inquilino actualizado = inquilinoService.actualizar(id, inquilino);
            return Response.ok(Map.of(
                "mensaje", "Inquilino actualizado exitosamente",
                "id", actualizado.getId()
            )).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error al actualizar inquilino: " + e.getMessage()))
                .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response eliminar(@PathParam("id") Integer id) {
        try {
            inquilinoService.eliminar(id);
            return Response.ok(Map.of(
                "mensaje", "Inquilino eliminado exitosamente",
                "id", id
            )).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error al eliminar inquilino: " + e.getMessage()))
                .build();
        }
    }
}
