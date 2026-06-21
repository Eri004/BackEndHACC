package com.hacc.api.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hacc.api.application.service.UnidadService;
import com.hacc.api.domain.model.Unidad;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/unidades")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UnidadResource {

    @Inject
    private UnidadService unidadService;

    @POST
    @Path("/registro/{edificioId}/{propietarioId}")
    public Response registrarUnidad(@PathParam("edificioId") Integer edificioId,
                                    @PathParam("propietarioId") Integer propietarioId,
                                    Unidad unidad) {
        try {
            if (edificioId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "El ID del edificio es obligatorio"))
                    .build();
            }

            if (propietarioId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "El ID del propietario es obligatorio"))
                    .build();
            }

            if (unidad == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Los datos de la unidad son obligatorios"))
                    .build();
            }

            Unidad nuevaUnidad = unidadService.registrarUnidad(unidad, edificioId, propietarioId);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Unidad registrada exitosamente");
            response.put("id", nuevaUnidad.getIdUnidad());
            response.put("numero", nuevaUnidad.getNumero());
            response.put("edificioId", edificioId);
            response.put("propietarioId", propietarioId);

            return Response.status(Response.Status.CREATED).entity(response).build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error al registrar unidad: " + e.getMessage()))
                .build();
        }
    }

    @POST
    @Path("/registro-multiple/{edificioId}/{propietarioId}")
    public Response registrarMultiplesUnidades(@PathParam("edificioId") Integer edificioId,
                                               @PathParam("propietarioId") Integer propietarioId,
                                               List<Unidad> unidades) {
        try {
            if (edificioId == null || propietarioId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Los IDs son obligatorios"))
                    .build();
            }

            if (unidades == null || unidades.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Debe enviar al menos una unidad"))
                    .build();
            }

            List<Unidad> unidadesRegistradas = unidadService.registrarMultiplesUnidades(
                unidades, edificioId, propietarioId
            );

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Unidades registradas exitosamente");
            response.put("total", unidadesRegistradas.size());
            response.put("edificioId", edificioId);
            response.put("propietarioId", propietarioId);

            return Response.status(Response.Status.CREATED).entity(response).build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error al registrar unidades: " + e.getMessage()))
                .build();
        }
    }

    @GET
    @Path("/edificio/{edificioId}")
    public Response listarUnidadesPorEdificio(@PathParam("edificioId") Integer edificioId) {
        try {
            if (edificioId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "El ID del edificio es obligatorio"))
                    .build();
            }

            List<Unidad> unidades = unidadService.listarPorEdificio(edificioId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("edificioId", edificioId);
            response.put("total", unidades.size());
            response.put("unidades", unidades);

            return Response.ok(response).build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error al listar unidades: " + e.getMessage()))
                .build();
        }
    }

    @GET
    @Path("/propietario/{propietarioId}")
    public Response listarUnidadesPorPropietario(@PathParam("propietarioId") Integer propietarioId) {
        try {
            if (propietarioId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "El ID del propietario es obligatorio"))
                    .build();
            }

            List<Unidad> unidades = unidadService.listarPorPropietario(propietarioId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("propietarioId", propietarioId);
            response.put("total", unidades.size());
            response.put("unidades", unidades);

            return Response.ok(response).build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error al listar unidades: " + e.getMessage()))
                .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response obtenerUnidad(@PathParam("id") Integer id) {
        try {
            if (id == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "El ID de la unidad es obligatorio"))
                    .build();
            }

            Unidad unidad = unidadService.obtener(id);
            return Response.ok(unidad).build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error al obtener unidad: " + e.getMessage()))
                .build();
        }
    }
    @PUT
    @Path("/{id}/cambiar-propietario/{nuevoPropietarioId}")
    public Response cambiarPropietario(@PathParam("id") Integer unidadId,
                                       @PathParam("nuevoPropietarioId") Integer nuevoPropietarioId) {
        try {
            if (unidadId == null || nuevoPropietarioId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Los IDs son obligatorios"))
                    .build();
            }

            Unidad unidadActualizada = unidadService.cambiarPropietario(unidadId, nuevoPropietarioId);

            return Response.ok(Map.of(
                "mensaje", "Propietario de la unidad cambiado exitosamente",
                "unidadId", unidadActualizada.getIdUnidad(),
                "nuevoPropietarioId", nuevoPropietarioId
            )).build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error al cambiar propietario: " + e.getMessage()))
                .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response eliminarUnidad(@PathParam("id") Integer id) {
        try {
            if (id == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "El ID de la unidad es obligatorio"))
                    .build();
            }

            unidadService.eliminar(id);

            return Response.ok(Map.of(
                "mensaje", "Unidad eliminada exitosamente",
                "id", id
            )).build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Error al eliminar unidad: " + e.getMessage()))
                .build();
        }
    }
}