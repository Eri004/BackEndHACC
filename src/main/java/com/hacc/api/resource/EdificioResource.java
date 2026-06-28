package com.hacc.api.resource;

import java.util.List;
import java.util.Set;

import com.hacc.api.application.service.EdificioService;
import com.hacc.api.application.service.PropietarioService;
import com.hacc.api.domain.model.Edificio;
import com.hacc.api.domain.model.Propietario;
import com.hacc.api.domain.model.Unidad;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/edificios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EdificioResource {

    @Inject
    EdificioService edificioService;

    @Inject
    PropietarioService propietarioService;

    // ==================== ENDPOINTS PÚBLICOS (ADMIN) ====================

    /**
     * Listar todos los edificios (solo administradores)
     * GET /edificios
     */
    @GET
    public Response listarTodos() {
        return Response.ok(edificioService.listarTodos()).build();
    }

    /**
     * Listar edificios de un propietario específico (admin)
     * GET /edificios/propietario/{idPropietario}
     */
    @GET
    @Path("/propietario/{idPropietario}")
    public Response listarPorPropietario(@PathParam("idPropietario") Integer idPropietario) {
        return Response.ok(edificioService.listarPorPropietario(idPropietario)).build();
    }

    /**
     * Obtener un edificio por ID
     * GET /edificios/{idEdificio}
     */
    @GET
    @Path("/{idEdificio}")
    public Response obtenerPorId(
            @PathParam("idEdificio") Long idEdificio,
            @Context SecurityContext securityContext) {
        
        // Si es ADMIN, puede ver cualquier edificio
        if (securityContext.isUserInRole("ADMIN")) {
            return Response.ok(edificioService.obtener(idEdificio)).build();
        }
        
        // Si es PROPIETARIO, verificar que le pertenezca
        String email = securityContext.getUserPrincipal().getName();
        Propietario propietario = propietarioService.buscarPorEmail(email)
                .orElseThrow(() -> new NotFoundException("Propietario no encontrado"));
        
        Edificio edificio = edificioService.obtenerEdificioSeguro(idEdificio, propietario.getId_propietario());
        return Response.ok(edificio).build();
    }

    // ==================== ENDPOINTS PARA PROPIETARIOS (FRONTEND) ====================

    /**
     * Obtener todos los edificios del propietario autenticado
     * GET /edificios/mi-perfil/edificios
     */
    @GET
    @Path("/mi-perfil/edificios")
    public Response obtenerMisEdificios(@Context SecurityContext securityContext) {
        String email = securityContext.getUserPrincipal().getName();
        List<Edificio> edificios = edificioService.obtenerEdificiosPorEmailPropietario(email);
        return Response.ok(edificios).build();
    }

    /**
     * Obtener edificios activos del propietario autenticado
     * GET /edificios/mi-perfil/edificios/activos
     */
    @GET
    @Path("/mi-perfil/edificios/activos")
    @RolesAllowed({"PROPIETARIO", "ADMIN"})
    public Response obtenerMisEdificiosActivos(@Context SecurityContext securityContext) {
        String email = securityContext.getUserPrincipal().getName();
        Propietario propietario = propietarioService.buscarPorEmail(email)
                .orElseThrow(() -> new NotFoundException("Propietario no encontrado"));
        
        List<Edificio> edificios = edificioService.obtenerEdificiosActivos(propietario.getId_propietario());
        return Response.ok(edificios).build();
    }

    /**
     * Obtener unidades de un edificio del propietario autenticado
     * GET /edificios/mi-perfil/edificios/{idEdificio}/unidades
     */
    @GET
    @Path("/mi-perfil/edificios/{idEdificio}/unidades")
    @RolesAllowed({"PROPIETARIO", "ADMIN"})
    public Response obtenerMisUnidades(
            @PathParam("idEdificio") Long idEdificio,
            @Context SecurityContext securityContext) {
        
        String email = securityContext.getUserPrincipal().getName();
        Propietario propietario = propietarioService.buscarPorEmail(email)
                .orElseThrow(() -> new NotFoundException("Propietario no encontrado"));
        
        Set<Unidad> unidades = edificioService.obtenerUnidadesDeEdificio(idEdificio, propietario.getId_propietario());
        return Response.ok(unidades).build();
    }

    /**
     * Obtener resumen de edificios del propietario autenticado
     * GET /edificios/mi-perfil/resumen
     */
    @GET
    @Path("/mi-perfil/resumen")
    @RolesAllowed({"PROPIETARIO", "ADMIN"})
    public Response obtenerResumenMisEdificios(@Context SecurityContext securityContext) {
        String email = securityContext.getUserPrincipal().getName();
        Propietario propietario = propietarioService.buscarPorEmail(email)
                .orElseThrow(() -> new NotFoundException("Propietario no encontrado"));
        
        EdificioService.ResumenEdificiosDTO resumen = edificioService.obtenerResumenEdificios(
                propietario.getId_propietario()
        );
        return Response.ok(resumen).build();
    }

    /**
     * Obtener dashboard de edificios del propietario autenticado
     * GET /edificios/mi-perfil/dashboard
     */
    @GET
    @Path("/mi-perfil/dashboard")
    @RolesAllowed({"PROPIETARIO", "ADMIN"})
    public Response obtenerDashboardMisEdificios(@Context SecurityContext securityContext) {
        String email = securityContext.getUserPrincipal().getName();
        Propietario propietario = propietarioService.buscarPorEmail(email)
                .orElseThrow(() -> new NotFoundException("Propietario no encontrado"));
        
        EdificioService.DashboardEdificiosDTO dashboard = edificioService.obtenerDashboardEdificios(
                propietario.getId_propietario()
        );
        return Response.ok(dashboard).build();
    }

    // ==================== CRUD PARA PROPIETARIOS ====================

    /**
     * Crear un nuevo edificio
     * POST /edificios
     */
    @POST
    @RolesAllowed({"PROPIETARIO", "ADMIN"})
    public Response crearEdificio(
            @QueryParam("idPropietario") Integer idPropietario,
            Edificio edificio,
            @Context SecurityContext securityContext) {
        
        // Si es PROPIETARIO, usar su ID automáticamente
        if (!securityContext.isUserInRole("ADMIN")) {
            String email = securityContext.getUserPrincipal().getName();
            Propietario propietario = propietarioService.buscarPorEmail(email)
                    .orElseThrow(() -> new NotFoundException("Propietario no encontrado"));
            idPropietario = propietario.getId_propietario();
        }
        
        if (idPropietario == null) {
            throw new BadRequestException("idPropietario es obligatorio");
        }
        
        Edificio nuevo = edificioService.registrarEdificio(edificio, idPropietario);
        return Response.status(Response.Status.CREATED).entity(nuevo).build();
    }

    /**
     * Crear un edificio con unidades
     * POST /edificios/con-unidades
     */
   @POST
@Path("/con-unidades")
@RolesAllowed({"propietario", "ADMIN"})
public Response crearEdificioConUnidades(
        @QueryParam("idPropietario") Integer idPropietario,
        CrearEdificioConUnidadesRequest request) {

    // Si es PROPIETARIO, usar su ID automáticamente
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

    /**
     * Actualizar un edificio
     * PATCH /edificios/{idEdificio}
     */
    @PATCH
    @Path("/{idEdificio}")
    @RolesAllowed({"PROPIETARIO", "ADMIN"})
    public Response actualizarEdificio(
            @PathParam("idEdificio") Long idEdificio,
            Edificio edificio,
            @Context SecurityContext securityContext) {
        
        // Verificar que el propietario tenga acceso
        if (!securityContext.isUserInRole("ADMIN")) {
            String email = securityContext.getUserPrincipal().getName();
            Propietario propietario = propietarioService.buscarPorEmail(email)
                    .orElseThrow(() -> new NotFoundException("Propietario no encontrado"));
            
            // Verificar pertenencia
            if (!edificioService.verificarPertenencia(idEdificio, propietario.getId_propietario())) {
                throw new ForbiddenException("No tienes permiso para modificar este edificio");
            }
        }
        
        Edificio actualizado = edificioService.actualizar(idEdificio, edificio);
        return Response.ok(actualizado).build();
    }

    /**
     * Eliminar un edificio (solo si no tiene unidades)
     * DELETE /edificios/{idEdificio}
     */
    @DELETE
    @Path("/{idEdificio}")
    @RolesAllowed({"PROPIETARIO", "ADMIN"})
    public Response eliminarEdificio(
            @PathParam("idEdificio") Long idEdificio,
            @Context SecurityContext securityContext) {
        
        // Verificar que el propietario tenga acceso
        if (!securityContext.isUserInRole("ADMIN")) {
            String email = securityContext.getUserPrincipal().getName();
            Propietario propietario = propietarioService.buscarPorEmail(email)
                    .orElseThrow(() -> new NotFoundException("Propietario no encontrado"));
            
            // Verificar pertenencia
            if (!edificioService.verificarPertenencia(idEdificio, propietario.getId_propietario())) {
                throw new ForbiddenException("No tienes permiso para eliminar este edificio");
            }
        }
        
        edificioService.eliminar(idEdificio);
        return Response.noContent().build();
    }

    // ==================== DTO INTERNO ====================

    public static class CrearEdificioConUnidadesRequest {
        private Edificio edificio;
        private List<Unidad> unidades;

        public Edificio getEdificio() { return edificio; }
        public void setEdificio(Edificio edificio) { this.edificio = edificio; }
        public List<Unidad> getUnidades() { return unidades; }
        public void setUnidades(List<Unidad> unidades) { this.unidades = unidades; }
    }
}