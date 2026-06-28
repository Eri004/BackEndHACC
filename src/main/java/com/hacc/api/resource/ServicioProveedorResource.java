package com.hacc.api.resource;

import com.hacc.api.application.service.ServicioProveedorService;
import com.hacc.api.domain.model.ServicioProveedor;
import com.hacc.api.domain.enums.NombreServicio;
import com.hacc.api.domain.enums.EstadoServicio;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Path("/finanzas/servicios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ServicioProveedorResource {

    @Inject
    ServicioProveedorService servicioService;

    // ==================== CRUD BÁSICO ====================

    /**
     * Listar todos los servicios
     * GET /finanzas/servicios
     */
    @GET
    public Response listarTodos() {
        return Response.ok(servicioService.listarTodos()).build();
    }

    /**
     * Listar servicios de un propietario específico
     * GET /finanzas/servicios/propietario/{idPropietario}
     */
    @GET
    @Path("/propietario/{idPropietario}")
    public Response listarPorPropietario(@PathParam("idPropietario") Integer idPropietario) {
        return Response.ok(servicioService.listarPorPropietario(idPropietario)).build();
    }

    /**
     * Obtener un servicio por ID
     * GET /finanzas/servicios/{idServicio}
     */
    @GET
    @Path("/{idServicio}")
    public Response obtenerPorId(@PathParam("idServicio") Long idServicio) {
        return servicioService.buscarPorId(idServicio)
                .map(servicio -> Response.ok(servicio).build())
                .orElseThrow(() -> new NotFoundException("Servicio no encontrado con ID: " + idServicio));
    }

    /**
     * Crear un nuevo servicio
     * POST /finanzas/servicios?idPropietario={idPropietario}
     * Body: { "nombre": "AGUA", "montoFacturado": 150.00, "fechaVencimiento": "2026-07-10", ... }
     */
    @POST
    public Response crearServicio(
            @QueryParam("idPropietario") Integer idPropietario,
            ServicioProveedor servicio) {
        if (idPropietario == null) {
            throw new BadRequestException("El parámetro idPropietario es obligatorio");
        }
        ServicioProveedor nuevo = servicioService.crear(servicio, idPropietario);
        return Response.status(Response.Status.CREATED).entity(nuevo).build();
    }

    /**
     * Actualizar un servicio existente
     * PATCH /finanzas/servicios/{idServicio}
     */
    @PATCH
    @Path("/{idServicio}")
    public Response actualizarServicio(
            @PathParam("idServicio") Long idServicio,
            ServicioProveedor servicio) {
        servicio.setIdServicio(idServicio);
        ServicioProveedor actualizado = servicioService.actualizar(servicio);
        return Response.ok(actualizado).build();
    }

    /**
     * Eliminar un servicio (solo si no está pagado)
     * DELETE /finanzas/servicios/{idServicio}
     */
    @DELETE
    @Path("/{idServicio}")
    public Response eliminarServicio(@PathParam("idServicio") Long idServicio) {
        servicioService.eliminar(idServicio);
        return Response.noContent().build();
    }

    // ==================== REGISTRO DE PAGO ====================

    /**
     * Registrar pago de un servicio
     * POST /finanzas/servicios/{idServicio}/pagar
     * Body: { "montoPagado": 150.00, "idTransaccion": 1 }
     */
    @POST
    @Path("/{idServicio}/pagar")
    public Response registrarPagoServicio(
            @PathParam("idServicio") Long idServicio,
            RegistrarPagoServicioRequest request) {
        if (request.getMontoPagado() == null || request.getMontoPagado().doubleValue() <= 0) {
            throw new BadRequestException("El monto pagado es obligatorio y debe ser mayor a 0");
        }
        ServicioProveedor servicio = servicioService.registrarPagoServicio(
                idServicio, 
                request.getMontoPagado(), 
                request.getIdTransaccion()
        );
        return Response.ok(servicio).build();
    }

    // ==================== FILTROS ====================

    /**
     * Filtrar servicios por estado
     * GET /finanzas/servicios/filtros/propietario/{idPropietario}/estado/{estado}
     */
    @GET
    @Path("/filtros/propietario/{idPropietario}/estado/{estado}")
    public Response filtrarPorEstado(
            @PathParam("idPropietario") Integer idPropietario,
            @PathParam("estado") String estado) {
        try {
            EstadoServicio estadoEnum = EstadoServicio.valueOf(estado.toUpperCase());
            return Response.ok(servicioService.filtrarPorPropietarioYEstado(idPropietario, estadoEnum)).build();
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Estado inválido. Valores permitidos: PENDIENTE, PAGADO, CORTADO, EN_DEUDA, ANULADO");
        }
    }

    /**
     * Filtrar servicios por nombre
     * GET /finanzas/servicios/filtros/propietario/{idPropietario}/nombre/{nombre}
     */
    @GET
    @Path("/filtros/propietario/{idPropietario}/nombre/{nombre}")
    public Response filtrarPorNombre(
            @PathParam("idPropietario") Integer idPropietario,
            @PathParam("nombre") String nombre) {
        try {
            NombreServicio nombreEnum = NombreServicio.valueOf(nombre.toUpperCase());
            return Response.ok(servicioService.filtrarPorPropietarioYNombre(idPropietario, nombreEnum)).build();
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Nombre inválido. Valores permitidos: AGUA, ELECTRICIDAD, GAS, INTERNET, SEGURIDAD, ASEO, OTROS");
        }
    }

    /**
     * Filtrar servicios por mes
     * GET /finanzas/servicios/filtros/mes?mes=2026-06
     */
    @GET
    @Path("/filtros/mes")
    public Response filtrarPorMes(@QueryParam("mes") String mes) {
        if (mes == null || mes.isEmpty()) {
            throw new BadRequestException("El parámetro mes es obligatorio (formato: yyyy-MM)");
        }
        return Response.ok(servicioService.filtrarPorMes(mes)).build();
    }

    /**
     * Filtrar servicios por propietario y mes
     * GET /finanzas/servicios/filtros/propietario/{idPropietario}/mes/{mes}
     */
    @GET
    @Path("/filtros/propietario/{idPropietario}/mes/{mes}")
    public Response filtrarPorPropietarioYMes(
            @PathParam("idPropietario") Integer idPropietario,
            @PathParam("mes") String mes) {
        return Response.ok(servicioService.filtrarPorPropietarioYMes(idPropietario, mes)).build();
    }

    /**
     * Filtrar servicios por propietario, mes y estado
     * GET /finanzas/servicios/filtros/propietario/{idPropietario}/mes/{mes}/estado/{estado}
     */
    @GET
    @Path("/filtros/propietario/{idPropietario}/mes/{mes}/estado/{estado}")
    public Response filtrarPorPropietarioMesYEstado(
            @PathParam("idPropietario") Integer idPropietario,
            @PathParam("mes") String mes,
            @PathParam("estado") String estado) {
        try {
            EstadoServicio estadoEnum = EstadoServicio.valueOf(estado.toUpperCase());
            return Response.ok(servicioService.filtrarPorPropietarioMesYEstado(idPropietario, mes, estadoEnum)).build();
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Estado inválido");
        }
    }

    /**
     * Filtrar servicios por propietario, mes y nombre
     * GET /finanzas/servicios/filtros/propietario/{idPropietario}/mes/{mes}/nombre/{nombre}
     */
    @GET
    @Path("/filtros/propietario/{idPropietario}/mes/{mes}/nombre/{nombre}")
    public Response filtrarPorPropietarioMesYNombre(
            @PathParam("idPropietario") Integer idPropietario,
            @PathParam("mes") String mes,
            @PathParam("nombre") String nombre) {
        try {
            NombreServicio nombreEnum = NombreServicio.valueOf(nombre.toUpperCase());
            return Response.ok(servicioService.filtrarPorPropietarioMesYNombre(idPropietario, mes, nombreEnum)).build();
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Nombre inválido");
        }
    }

    /**
     * Filtrar servicios por rango de fechas
     * GET /finanzas/servicios/filtros/rango?inicio=2026-01-01&fin=2026-06-30
     */
    @GET
    @Path("/filtros/rango")
    public Response filtrarPorRangoFechas(
            @QueryParam("inicio") String inicio,
            @QueryParam("fin") String fin) {
        if (inicio == null || fin == null) {
            throw new BadRequestException("Los parámetros inicio y fin son obligatorios (formato: yyyy-MM-dd)");
        }
        LocalDate fechaInicio = LocalDate.parse(inicio);
        LocalDate fechaFin = LocalDate.parse(fin);
        return Response.ok(servicioService.filtrarPorRangoFechas(fechaInicio, fechaFin)).build();
    }

    /**
     * Filtrar servicios por propietario y rango de fechas
     * GET /finanzas/servicios/filtros/propietario/{idPropietario}/rango?inicio=2026-01-01&fin=2026-06-30
     */
    @GET
    @Path("/filtros/propietario/{idPropietario}/rango")
    public Response filtrarPorPropietarioYRangoFechas(
            @PathParam("idPropietario") Integer idPropietario,
            @QueryParam("inicio") String inicio,
            @QueryParam("fin") String fin) {
        if (inicio == null || fin == null) {
            throw new BadRequestException("Los parámetros inicio y fin son obligatorios (formato: yyyy-MM-dd)");
        }
        LocalDate fechaInicio = LocalDate.parse(inicio);
        LocalDate fechaFin = LocalDate.parse(fin);
        return Response.ok(servicioService.filtrarPorPropietarioYRangoFechas(idPropietario, fechaInicio, fechaFin)).build();
    }

    // ==================== REPORTES Y ESTADÍSTICAS ====================

    /**
     * Obtener resumen de servicios por mes
     * GET /finanzas/servicios/resumen/propietario/{idPropietario}/mes/{mes}
     */
    @GET
    @Path("/resumen/propietario/{idPropietario}/mes/{mes}")
    public Response obtenerResumenPorMes(
            @PathParam("idPropietario") Integer idPropietario,
            @PathParam("mes") String mes) {
        return Response.ok(servicioService.obtenerResumenServiciosPorMes(idPropietario, mes)).build();
    }

    /**
     * Obtener resumen del mes actual
     * GET /finanzas/servicios/resumen/mes-actual/{idPropietario}
     */
    @GET
    @Path("/resumen/mes-actual/{idPropietario}")
    public Response obtenerResumenMesActual(@PathParam("idPropietario") Integer idPropietario) {
        String mesActual = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));
        return Response.ok(servicioService.obtenerResumenServiciosPorMes(idPropietario, mesActual)).build();
    }

    /**
     * Obtener servicios vencidos de un propietario
     * GET /finanzas/servicios/vencidos/{idPropietario}
     */
    @GET
    @Path("/vencidos/{idPropietario}")
    public Response obtenerServiciosVencidos(@PathParam("idPropietario") Integer idPropietario) {
        return Response.ok(servicioService.obtenerServiciosVencidos(idPropietario)).build();
    }

    /**
     * Obtener dashboard de servicios
     * GET /finanzas/servicios/dashboard/{idPropietario}
     */
    @GET
    @Path("/dashboard/{idPropietario}")
    public Response obtenerDashboard(@PathParam("idPropietario") Integer idPropietario) {
        return Response.ok(servicioService.obtenerDashboardServicios(idPropietario)).build();
    }

    // ==================== DTOs INTERNOS ====================

    public static class RegistrarPagoServicioRequest {
        private BigDecimal montoPagado;
        private Long idTransaccion;

        public BigDecimal getMontoPagado() { return montoPagado; }
        public void setMontoPagado(BigDecimal montoPagado) { this.montoPagado = montoPagado; }
        public Long getIdTransaccion() { return idTransaccion; }
        public void setIdTransaccion(Long idTransaccion) { this.idTransaccion = idTransaccion; }
    }
}