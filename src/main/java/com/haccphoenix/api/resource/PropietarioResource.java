package com.haccphoenix.api.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.haccphoenix.api.application.service.PropietarioService;
import com.haccphoenix.api.domain.model.Propietario;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/propietarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PropietarioResource {

    @Inject
    PropietarioService propietarioService;

    @POST
    public Response registrar(Propietario propietario) {
        try {
            if (propietario == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Los datos del propietario son obligatorios"))
                    .build();
            }
            Propietario nuevo = propietarioService.registrar(propietario);
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Propietario registrado exitosamente");
            response.put("id", nuevo.getId());
            response.put("cedula", nuevo.getCedula());
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error al registrar propietario: " + e.getMessage()))
                .build();
        }
    }

    @GET
    public Response listar() {
        try {
            List<Propietario> propietarios = propietarioService.listar();
            return Response.ok(propietarios).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error al listar propietarios: " + e.getMessage()))
                .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response obtener(@PathParam("id") Integer id) {
        try {
            Propietario propietario = propietarioService.obtener(id);
            return Response.ok(propietario).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error al obtener propietario: " + e.getMessage()))
                .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response actualizar(@PathParam("id") Integer id, Propietario propietario) {
        try {
            propietarioService.actualizar(id, propietario);
            return Response.ok(Map.of(
                "mensaje", "Propietario actualizado exitosamente",
                "id", id
            )).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error al actualizar: " + e.getMessage()))
                .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response eliminar(@PathParam("id") Integer id) {
        try {
            propietarioService.eliminar(id);
            return Response.ok(Map.of(
                "mensaje", "Propietario eliminado exitosamente",
                "id", id
            )).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error al eliminar: " + e.getMessage()))
                .build();
        }
    }
}
