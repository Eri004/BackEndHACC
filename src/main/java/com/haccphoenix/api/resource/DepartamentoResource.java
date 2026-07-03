package com.haccphoenix.api.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.haccphoenix.api.application.service.DepartamentoService;
import com.haccphoenix.api.domain.model.Departamento;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/departamentos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DepartamentoResource {

    @Inject
    DepartamentoService departamentoService;

    @POST
    @Path("/registro/{edificioId}/{propietarioId}")
    public Response registrar(@PathParam("edificioId") Integer edificioId,
                              @PathParam("propietarioId") Integer propietarioId,
                              Departamento departamento) {
        try {
            Departamento nuevo = departamentoService.registrar(departamento, edificioId, propietarioId);
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Departamento registrado exitosamente");
            response.put("id", nuevo.getId());
            response.put("numero", nuevo.getNumero());
            response.put("edificioId", edificioId);
            response.put("propietarioId", propietarioId);
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error al registrar departamento: " + e.getMessage()))
                .build();
        }
    }

    @GET
    public Response listar() {
        try {
            List<Departamento> departamentos = departamentoService.listar();
            return Response.ok(departamentos).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error al listar departamentos: " + e.getMessage()))
                .build();
        }
    }

    @GET
    @Path("/edificio/{edificioId}")
    public Response listarPorEdificio(@PathParam("edificioId") Integer edificioId) {
        try {
            List<Departamento> departamentos = departamentoService.listarPorEdificio(edificioId);
            Map<String, Object> response = new HashMap<>();
            response.put("edificioId", edificioId);
            response.put("total", departamentos.size());
            response.put("departamentos", departamentos);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error al listar departamentos: " + e.getMessage()))
                .build();
        }
    }

    @GET
    @Path("/propietario/{propietarioId}")
    public Response listarPorPropietario(@PathParam("propietarioId") Integer propietarioId) {
        try {
            List<Departamento> departamentos = departamentoService.listarPorPropietario(propietarioId);
            Map<String, Object> response = new HashMap<>();
            response.put("propietarioId", propietarioId);
            response.put("total", departamentos.size());
            response.put("departamentos", departamentos);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error al listar departamentos: " + e.getMessage()))
                .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response obtener(@PathParam("id") Integer id) {
        try {
            Departamento departamento = departamentoService.obtener(id);
            return Response.ok(departamento).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error al obtener departamento: " + e.getMessage()))
                .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response actualizar(@PathParam("id") Integer id, Departamento departamento) {
        try {
            Departamento actualizado = departamentoService.actualizar(id, departamento);
            return Response.ok(Map.of(
                "mensaje", "Departamento actualizado exitosamente",
                "id", actualizado.getId()
            )).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error al actualizar departamento: " + e.getMessage()))
                .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response eliminar(@PathParam("id") Integer id) {
        try {
            departamentoService.eliminar(id);
            return Response.ok(Map.of(
                "mensaje", "Departamento eliminado exitosamente",
                "id", id
            )).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error al eliminar departamento: " + e.getMessage()))
                .build();
        }
    }
}
