package com.hacc.api.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hacc.api.application.service.EdificioService;
import com.hacc.api.domain.model.Edificio;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/edificios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EdificioResource {

    @Inject
    private EdificioService edificioService;

    @POST
    @Path("/registro/{propietarioId}")
    public Response registrarEdificio(@PathParam("propietarioId") Integer propietarioId, Edificio edificio) {
        try {
            if (propietarioId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "El ID del propietario es obligatorio"))
                    .build();
            }

            if (edificio == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Los datos del edificio son obligatorios"))
                    .build();
            }

            Edificio nuevoEdificio = edificioService.registrarEdificio(edificio, propietarioId);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Edificio registrado exitosamente");
            response.put("id", nuevoEdificio.getIdEdificio());
            response.put("nombre", nuevoEdificio.getNombre());
            response.put("propietarioId", propietarioId);

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

    @POST
    @Path("/registro-con-unidades/{propietarioId}")
    public Response registrarEdificioConUnidades(@PathParam("propietarioId") Integer propietarioId, Edificio edificio) {
        try {
            if (propietarioId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "El ID del propietario es obligatorio"))
                    .build();
            }

            if (edificio == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Los datos del edificio son obligatorios"))
                    .build();
            }

            Edificio nuevoEdificio = edificioService.registrarEdificioConUnidades(edificio, propietarioId, null);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Edificio registrado exitosamente");
            response.put("id", nuevoEdificio.getIdEdificio());
            response.put("nombre", nuevoEdificio.getNombre());
            response.put("totalUnidades", nuevoEdificio.getUnidades().size());
            response.put("propietarioId", propietarioId);

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
    @Path("/propietario/{propietarioId}")
    public Response listarEdificiosPorPropietario(@PathParam("propietarioId") Integer propietarioId) {
        try {
            if (propietarioId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "El ID del propietario es obligatorio"))
                    .build();
            }

            List<Edificio> edificios = edificioService.listarPorPropietario(propietarioId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("propietarioId", propietarioId);
            response.put("total", edificios.size());
            response.put("edificios", edificios);

            return Response.ok(response).build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error al listar edificios: " + e.getMessage()))
                .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response obtenerEdificio(@PathParam("id") Integer id) {
        try {
            if (id == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "El ID del edificio es obligatorio"))
                    .build();
            }

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

    @GET
    public Response listarEdificios() {
        try {
            List<Edificio> edificios = edificioService.listarTodos();
            
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

    @PUT
    @Path("/{id}")
    public Response actualizarEdificio(@PathParam("id") Integer id, Edificio edificio) {
        try {
            if (id == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "El ID del edificio es obligatorio"))
                    .build();
            }

            if (edificio == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Los datos del edificio son obligatorios"))
                    .build();
            }

            Edificio edificioActualizado = edificioService.actualizar(id, edificio);

            return Response.ok(Map.of(
                "mensaje", "Edificio actualizado exitosamente",
                "id", edificioActualizado.getIdEdificio()
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
    public Response eliminarEdificio(@PathParam("id") Integer id) {
        try {
            if (id == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "El ID del edificio es obligatorio"))
                    .build();
            }

            edificioService.eliminar(id);

            return Response.ok(Map.of(
                "mensaje", "Edificio eliminado exitosamente",
                "id", id
            )).build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error al eliminar edificio: " + e.getMessage()))
                .build();
        }
    }
}