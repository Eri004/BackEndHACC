package com.hacc.api.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hacc.api.application.service.PropietarioService;
import com.hacc.api.domain.model.Propietario;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/propietarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PropietarioResource {

    @Inject
    private PropietarioService propietarioService;

    @POST
    @Path("/registro")
    public Response registrarPropietario(Propietario propietario) {
        try {
            if (propietario == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Los datos del propietario son obligatorios"))
                    .build();
            }
            
            Propietario nuevoPropietario = propietarioService.registrarPropietario(propietario);
            
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Propietario registrado exitosamente");
            response.put("id", nuevoPropietario.getId_propietario());
            response.put("nombreCompleto", nuevoPropietario.getNombre() + " " + nuevoPropietario.getApellido());
            response.put("email", nuevoPropietario.getEmail());
            
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
    public Response listarPropietarios() {
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
    public Response obtenerPropietario(@PathParam("id") Integer id) {
        try {
            if (id == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "El ID es obligatorio"))
                    .build();
            }
            
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
    public Response actualizarPropietario(@PathParam("id") Integer id, Propietario propietario) {
        try {
            if (id == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "El ID es obligatorio"))
                    .build();
            }
            
            if (propietario == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Los datos del propietario son obligatorios"))
                    .build();
            }
            
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
    public Response eliminarPropietario(@PathParam("id") Integer id) {
        try {
            if (id == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "El ID es obligatorio"))
                    .build();
            }
            
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