package com.haccphoenix.api.resource;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.haccphoenix.api.application.service.GastoService;
import com.haccphoenix.api.domain.model.Gasto;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/gastos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GastoResource {

    @Inject
    GastoService gastoService;

    @POST
    @Path("/registro/{edificioId}/{tipoGastoId}")
    public Response registrar(@PathParam("edificioId") Integer edificioId,
                              @PathParam("tipoGastoId") Integer tipoGastoId,
                              @QueryParam("usuarioId") Integer usuarioId,
                              Gasto gasto) {
        try {
            Gasto nuevo = gastoService.registrar(gasto, edificioId, tipoGastoId, usuarioId);
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Gasto registrado exitosamente");
            response.put("id", nuevo.getId());
            response.put("valor", nuevo.getValor());
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error al registrar gasto: " + e.getMessage()))
                .build();
        }
    }

    @GET
    public Response listar() {
        try {
            return Response.ok(gastoService.listar()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }

    @GET
    @Path("/edificio/{edificioId}")
    public Response listarPorEdificio(@PathParam("edificioId") Integer edificioId) {
        try {
            List<Gasto> gastos = gastoService.listarPorEdificio(edificioId);
            return Response.ok(gastos).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }

    @GET
    @Path("/tipo-gasto/{tipoGastoId}")
    public Response listarPorTipoGasto(@PathParam("tipoGastoId") Integer tipoGastoId) {
        try {
            return Response.ok(gastoService.listarPorTipoGasto(tipoGastoId)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }

    @GET
    @Path("/periodo")
    public Response listarPorPeriodo(@QueryParam("inicio") String inicio, @QueryParam("fin") String fin) {
        try {
            LocalDate fechaInicio = LocalDate.parse(inicio);
            LocalDate fechaFin = LocalDate.parse(fin);
            return Response.ok(gastoService.listarPorPeriodo(fechaInicio, fechaFin)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response obtener(@PathParam("id") Integer id) {
        try {
            return Response.ok(gastoService.obtener(id)).build();
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
    public Response actualizar(@PathParam("id") Integer id, Gasto gasto) {
        try {
            Gasto actualizado = gastoService.actualizar(id, gasto);
            return Response.ok(Map.of("mensaje", "Gasto actualizado exitosamente", "id", actualizado.getId())).build();
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
            gastoService.eliminar(id);
            return Response.ok(Map.of("mensaje", "Gasto eliminado exitosamente", "id", id)).build();
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
