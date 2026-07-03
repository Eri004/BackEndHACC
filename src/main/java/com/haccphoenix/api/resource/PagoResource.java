package com.haccphoenix.api.resource;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.haccphoenix.api.application.service.PagoService;
import com.haccphoenix.api.domain.model.Pago;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/pagos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PagoResource {

    @Inject
    PagoService pagoService;

    @POST
    @Path("/registro/{departamentoId}")
    public Response registrar(@PathParam("departamentoId") Integer departamentoId,
                              @QueryParam("usuarioId") Integer usuarioId,
                              PagoPagoRequest body) {
        try {
            List<Integer> cargosIds = body != null ? body.cargosIds : null;
            Pago nuevo = pagoService.registrar(body.pago, departamentoId, usuarioId, cargosIds);
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Pago registrado exitosamente");
            response.put("id", nuevo.getId());
            response.put("montoTotal", nuevo.getMontoTotal());
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error al registrar pago: " + e.getMessage()))
                .build();
        }
    }

    @GET
    public Response listar() {
        try {
            return Response.ok(pagoService.listar()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }

    @GET
    @Path("/departamento/{departamentoId}")
    public Response listarPorDepartamento(@PathParam("departamentoId") Integer departamentoId) {
        try {
            return Response.ok(pagoService.listarPorDepartamento(departamentoId)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }

    @GET
    @Path("/usuario/{usuarioId}")
    public Response listarPorUsuario(@PathParam("usuarioId") Integer usuarioId) {
        try {
            return Response.ok(pagoService.listarPorUsuario(usuarioId)).build();
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
            return Response.ok(pagoService.listarPorPeriodo(LocalDate.parse(inicio), LocalDate.parse(fin))).build();
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
            return Response.ok(pagoService.obtener(id)).build();
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
            pagoService.eliminar(id);
            return Response.ok(Map.of("mensaje", "Pago eliminado exitosamente", "id", id)).build();
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

    public static class PagoPagoRequest {
        public Pago pago;
        public List<Integer> cargosIds;
    }
}
