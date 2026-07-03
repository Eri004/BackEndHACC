package com.haccphoenix.api.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.haccphoenix.api.application.service.EdificioService;
import com.haccphoenix.api.domain.model.Edificio;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/edificios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EdificioResource {

    @Inject
    EdificioService edificioService;

    @POST
    public Response registrar(Edificio edificio) {
        try {
            if (edificio == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Los datos del edificio son obligatorios"))
                    .build();
            }
            Edificio nuevo = edificioService.registrar(edificio);
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Edificio registrado exitosamente");
            response.put("id", nuevo.getId());
            response.put("nombre", nuevo.getNombre());
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error al registrar edificio: " + e.getMessage()))
                .build();
        }
    }

    @GET
    public Response listar() {
        try {
            List<Edificio> edificios = edificioService.listar();
            Map<String, Object> response = new HashMap<>();
            response.put("total", edificios.size());
            response.put("edificios", edificios);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error al listar edificios: " + e.getMessage()))
                .build();
        }
    }

    @GET
    @Path("/activos")
    public Response listarActivos() {
        try {
            List<Edificio> edificios = edificioService.listarActivos();
            return Response.ok(edificios).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error al listar edificios activos: " + e.getMessage()))
                .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response obtener(@PathParam("id") Integer id) {
        try {
            Edificio edificio = edificioService.obtener(id);
            return Response.ok(edificio).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error al obtener edificio: " + e.getMessage()))
                .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response actualizar(@PathParam("id") Integer id, Edificio edificio) {
        try {
            Edificio actualizado = edificioService.actualizar(id, edificio);
            return Response.ok(Map.of(
                "mensaje", "Edificio actualizado exitosamente",
                "id", actualizado.getId()
            )).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error al actualizar edificio: " + e.getMessage()))
                .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response eliminar(@PathParam("id") Integer id) {
        try {
            edificioService.eliminar(id);
            return Response.ok(Map.of(
                "mensaje", "Edificio eliminado exitosamente",
                "id", id
            )).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error al eliminar edificio: " + e.getMessage()))
                .build();
        }
    }
}
