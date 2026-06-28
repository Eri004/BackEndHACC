package com.hacc.api.resource;

import com.hacc.api.application.service.PlantillaServicioService;
import com.hacc.api.domain.model.PlantillaServicio;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/finanzas/plantillas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PlantillaServicioResource {

    @Inject
    PlantillaServicioService plantillaService;

    // ==================== CRUD ====================

    @GET
    @Path("/propietario/{idPropietario}")
    public Response listarPorPropietario(@PathParam("idPropietario") Integer idPropietario) {
        return Response.ok(plantillaService.listarPorPropietario(idPropietario)).build();
    }

    @GET
    @Path("/{idPlantilla}")
    public Response obtenerPorId(@PathParam("idPlantilla") Long idPlantilla) {
        return plantillaService.buscarPorId(idPlantilla)
                .map(plantilla -> Response.ok(plantilla).build())
                .orElseThrow(() -> new NotFoundException("Plantilla no encontrada"));
    }

    @POST
    public Response crearPlantilla(
            @QueryParam("idPropietario") Integer idPropietario,
            PlantillaServicio plantilla) {
        if (idPropietario == null) {
            throw new BadRequestException("idPropietario es obligatorio");
        }
        PlantillaServicio nueva = plantillaService.crear(plantilla, idPropietario);
        return Response.status(Response.Status.CREATED).entity(nueva).build();
    }

    @PATCH
    @Path("/{idPlantilla}")
    public Response actualizarPlantilla(
            @PathParam("idPlantilla") Long idPlantilla,
            PlantillaServicio plantilla) {
        plantilla.setIdPlantilla(idPlantilla);
        return Response.ok(plantillaService.actualizar(plantilla)).build();
    }

    @DELETE
    @Path("/{idPlantilla}")
    public Response eliminarPlantilla(@PathParam("idPlantilla") Long idPlantilla) {
        plantillaService.eliminar(idPlantilla);
        return Response.noContent().build();
    }

    // ==================== GENERACIÓN AUTOMÁTICA ====================

    @POST
    @Path("/generar")
    public Response generarServicios() {
        int generados = plantillaService.generarServiciosDesdePlantillas();
        return Response.ok("Se generaron " + generados + " servicios automáticamente").build();
    }

    @GET
    @Path("/propietario/{idPropietario}/activos")
    public Response listarActivos(@PathParam("idPropietario") Integer idPropietario) {
        return Response.ok(plantillaService.listarPlantillasActivas(idPropietario)).build();
    }
}