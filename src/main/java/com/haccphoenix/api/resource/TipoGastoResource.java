package com.haccphoenix.api.resource;

import java.util.List;
import java.util.Map;

import com.haccphoenix.api.application.service.TipoGastoService;
import com.haccphoenix.api.domain.model.TipoGasto;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/tipos-gastos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TipoGastoResource {

    @Inject
    TipoGastoService tipoGastoService;

    @POST
    public Response registrar(TipoGasto tipoGasto) {
        try {
            TipoGasto nuevo = tipoGastoService.registrar(tipoGasto);
            return Response.status(Response.Status.CREATED).entity(nuevo).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error al registrar tipo de gasto: " + e.getMessage()))
                .build();
        }
    }

    @GET
    public Response listar() {
        try {
            return Response.ok(tipoGastoService.listar()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }

    @GET
    @Path("/activos")
    public Response listarActivos() {
        try {
            return Response.ok(tipoGastoService.listarActivos()).build();
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
            return Response.ok(tipoGastoService.obtener(id)).build();
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
    public Response actualizar(@PathParam("id") Integer id, TipoGasto tipoGasto) {
        try {
            TipoGasto actualizado = tipoGastoService.actualizar(id, tipoGasto);
            return Response.ok(actualizado).build();
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
            tipoGastoService.eliminar(id);
            return Response.ok(Map.of("mensaje", "Tipo de gasto eliminado exitosamente", "id", id)).build();
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
