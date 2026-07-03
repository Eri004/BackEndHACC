package com.haccphoenix.api.resource;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.haccphoenix.api.application.service.CargoService;
import com.haccphoenix.api.domain.model.Cargo;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/cargos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CargoResource {

    @Inject
    CargoService cargoService;

    @POST
    @Path("/registro/{departamentoId}/{tipoCargoId}")
    public Response registrar(@PathParam("departamentoId") Integer departamentoId,
                              @PathParam("tipoCargoId") Integer tipoCargoId,
                              @QueryParam("usuarioId") Integer usuarioId,
                              Cargo cargo) {
        try {
            Cargo nuevo = cargoService.registrar(cargo, departamentoId, tipoCargoId, usuarioId);
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Cargo registrado exitosamente");
            response.put("id", nuevo.getId());
            response.put("valor", nuevo.getValor());
            response.put("estado", nuevo.getEstado());
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error al registrar cargo: " + e.getMessage()))
                .build();
        }
    }

    @GET
    public Response listar() {
        try {
            return Response.ok(cargoService.listar()).build();
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
            return Response.ok(cargoService.listarPorDepartamento(departamentoId)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }

    @GET
    @Path("/estado/{estado}")
    public Response listarPorEstado(@PathParam("estado") String estado) {
        try {
            Cargo.EstadoCargo estadoEnum = Cargo.EstadoCargo.valueOf(estado.toUpperCase());
            return Response.ok(cargoService.listarPorEstado(estadoEnum)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", "Estado invalido. Valores permitidos: PENDIENTE, PARCIAL, PAGADO, ANULADO"))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }

    @GET
    @Path("/pendientes")
    public Response listarPendientes() {
        try {
            return Response.ok(cargoService.listarPendientes()).build();
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
            return Response.ok(cargoService.listarPorPeriodo(LocalDate.parse(inicio), LocalDate.parse(fin))).build();
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
            return Response.ok(cargoService.obtener(id)).build();
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
    public Response actualizar(@PathParam("id") Integer id, Cargo cargo) {
        try {
            Cargo actualizado = cargoService.actualizar(id, cargo);
            return Response.ok(Map.of("mensaje", "Cargo actualizado exitosamente", "id", actualizado.getId())).build();
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
            cargoService.eliminar(id);
            return Response.ok(Map.of("mensaje", "Cargo eliminado exitosamente", "id", id)).build();
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
