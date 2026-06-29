package com.hacc.api.resource;

import java.util.List;
import java.util.Set;

import com.hacc.api.application.service.EdificioService;
import com.hacc.api.application.service.PropietarioService;
import com.hacc.api.domain.model.Edificio;
import com.hacc.api.domain.model.Propietario;
import com.hacc.api.domain.model.Unidad;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/edificios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EdificioResource {

    @Inject
    EdificioService edificioService;

    @Inject
    PropietarioService propietarioService;

    // ==================== GET ====================

    @GET
    public Response listarTodos() {
        return Response.ok(edificioService.listarTodos()).build();
    }

    @GET
    @Path("/propietario/{idPropietario}")
    public Response listarPorPropietario(@PathParam("idPropietario") Integer idPropietario) {
        return Response.ok(edificioService.listarPorPropietario(idPropietario)).build();
    }

    @GET
    @Path("/{idEdificio}")
    public Response obtenerPorId(@PathParam("idEdificio") Long idEdificio) {
        return Response.ok(edificioService.obtener(idEdificio)).build();
    }

    // ==================== CREATE ====================

    @POST
    public Response crearEdificio(
            @QueryParam("idPropietario") Integer idPropietario,
            Edificio edificio) {

        if (idPropietario == null) {
            throw new BadRequestException("idPropietario es obligatorio");
        }

        Edificio nuevo = edificioService.registrarEdificio(edificio, idPropietario);

        return Response.status(Response.Status.CREATED).entity(nuevo).build();
    }

    @POST
    @Path("/con-unidades")
    public Response crearEdificioConUnidades(
            @QueryParam("idPropietario") Integer idPropietario,
            CrearEdificioConUnidadesRequest request) {

        if (idPropietario == null) {
            throw new BadRequestException("idPropietario es obligatorio");
        }

        Edificio nuevo = edificioService.registrarEdificioConUnidades(
                request.getEdificio(),
                idPropietario,
                request.getUnidades()
        );

        return Response.status(Response.Status.CREATED).entity(nuevo).build();
    }

    // ==================== UPDATE ====================

    @PATCH
    @Path("/{idEdificio}")
    public Response actualizarEdificio(
            @PathParam("idEdificio") Long idEdificio,
            Edificio edificio) {

        Edificio actualizado = edificioService.actualizar(idEdificio, edificio);
        return Response.ok(actualizado).build();
    }

    // ==================== DELETE ====================

    @DELETE
    @Path("/{idEdificio}")
    public Response eliminarEdificio(@PathParam("idEdificio") Long idEdificio) {

        edificioService.eliminar(idEdificio);
        return Response.noContent().build();
    }

    // ==================== DTO ====================

    public static class CrearEdificioConUnidadesRequest {
        private Edificio edificio;
        private List<Unidad> unidades;

        public Edificio getEdificio() { return edificio; }
        public void setEdificio(Edificio edificio) { this.edificio = edificio; }

        public List<Unidad> getUnidades() { return unidades; }
        public void setUnidades(List<Unidad> unidades) { this.unidades = unidades; }
    }
}