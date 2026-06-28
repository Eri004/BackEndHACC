package com.hacc.api.resource;

import java.time.LocalDate;

import com.hacc.api.application.service.PagoService;
import com.hacc.api.domain.model.Pago;

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

    // ==================== ENDPOINTS EXISTENTES (CORREGIDOS) ====================

    @GET
    public Response listarPagos(@QueryParam("idResidente") Integer idResidente) {
        if (idResidente != null) {
            return Response.ok(pagoService.listarPorResidente(idResidente)).build();
        }
        return Response.ok(pagoService.listarTodos()).build();
    }

    @GET
    @Path("/{idPago}")
    public Response obtenerPago(@PathParam("idPago") Integer idPago) {
        return pagoService.buscarPorId(idPago)
                .map(pago -> Response.ok(pago).build())
                .orElseThrow(() -> new NotFoundException("Pago no encontrado con ID: " + idPago));
    }

    @POST
    public Response crearPago(Pago pago) {
        Pago nuevo = pagoService.crear(pago);
        return Response.status(Response.Status.CREATED).entity(nuevo).build();
    }

    @DELETE
    @Path("/{idPago}")
    public Response eliminarPago(@PathParam("idPago") Integer idPago) {
        pagoService.eliminar(idPago);
        return Response.noContent().build();
    }

    @PATCH
    @Path("/{idPago}")
    public Response actualizarPago(@PathParam("idPago") Integer idPago, Pago pago) {
        // Obtener el pago existente
        Pago existente = pagoService.buscarPorId(idPago)
                .orElseThrow(() -> new NotFoundException("Pago no encontrado con ID: " + idPago));

        // Actualizar solo los campos permitidos
        if (pago.getTitulo() != null) existente.setTitulo(pago.getTitulo());
        if (pago.getMontoEsperado() != null) existente.setMontoEsperado(pago.getMontoEsperado());
        if (pago.getFechaVencimiento() != null) existente.setFechaVencimiento(pago.getFechaVencimiento());
        if (pago.getPeriodo() != null) existente.setPeriodo(pago.getPeriodo());
        if (pago.getObservacion() != null) existente.setObservacion(pago.getObservacion());

        Pago actualizado = pagoService.actualizar(existente);
        return Response.ok(actualizado).build();
    }

    // ==================== NUEVOS ENDPOINTS PARA FUNCIONALIDADES ====================

    /**
     * Genera cuotas mensuales para todos los residentes activos.
     * Debe ejecutarse mediante un cron job, pero se expone para pruebas.
     */
    @POST
    @Path("/generar-cuotas")
    public Response generarCuotasMensuales() {
        int cantidad = pagoService.generarCuotasMensuales();
        return Response.ok("Se generaron " + cantidad + " cuotas para el período actual").build();
    }

    /**
     * Registra el pago de una cuota.
     * Body: { "montoRecibido": 150.00 }
     */
    @POST
    @Path("/{idPago}/registrar")
    public Response registrarPago(@PathParam("idPago") Integer idPago, RegistrarPagoRequest request) {
        Pago pago = pagoService.registrarPago(idPago, request.getMontoRecibido());
        return Response.ok(pago).build();
    }

    /**
     * Anula un pago (solo si no está pagado).
     * Body: { "motivo": "Error en el registro" }
     */
    @POST
    @Path("/{idPago}/anular")
    public Response anularPago(@PathParam("idPago") Integer idPago, AnularPagoRequest request) {
        pagoService.anularPago(idPago, request.getMotivo());
        return Response.ok("Pago anulado exitosamente").build();
    }

    // ==================== FILTROS Y CONSULTAS ====================

    @GET
    @Path("/estado")
    public Response filtrarPorEstado(@QueryParam("estado") String estado) {
        // Convertir String a Enum (manejar error)
        try {
            com.hacc.api.domain.enums.EstadoPago estadoEnum = 
                com.hacc.api.domain.enums.EstadoPago.valueOf(estado.toUpperCase());
            return Response.ok(pagoService.filtrarPorEstado(estadoEnum)).build();
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Estado inválido. Valores permitidos: PENDIENTE, PAGADO, VENCIDO, ANULADO");
        }
    }

    @GET
    @Path("/periodo")
    public Response filtrarPorPeriodo(@QueryParam("periodo") String periodo) {
        return Response.ok(pagoService.filtrarPorPeriodo(periodo)).build();
    }

    @GET
    @Path("/rango")
    public Response filtrarPorRangoFechas(
            @QueryParam("inicio") String inicio,
            @QueryParam("fin") String fin) {
        LocalDate fechaInicio = LocalDate.parse(inicio);
        LocalDate fechaFin = LocalDate.parse(fin);
        return Response.ok(pagoService.filtrarPorRangoFechas(fechaInicio, fechaFin)).build();
    }

    @GET
    @Path("/estado-periodo")
    public Response filtrarPorEstadoYPeriodo(
            @QueryParam("estado") String estado,
            @QueryParam("periodo") String periodo) {
        try {
            com.hacc.api.domain.enums.EstadoPago estadoEnum = 
                com.hacc.api.domain.enums.EstadoPago.valueOf(estado.toUpperCase());
            return Response.ok(pagoService.filtrarPorEstadoYPeriodo(estadoEnum, periodo)).build();
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Estado inválido");
        }
    }

    // ==================== MORA ====================

    @GET
    @Path("/mora")
    public Response obtenerResidentesEnMora() {
        return Response.ok(pagoService.obtenerResidentesEnMora()).build();
    }

    @GET
    @Path("/mora/residente/{idResidente}")
    public Response residenteEnMora(@PathParam("idResidente") Integer idResidente) {
        boolean enMora = pagoService.residenteEnMora(idResidente);
        return Response.ok(new MoraResponse(idResidente, enMora)).build();
    }

    @GET
    @Path("/mora/detalle/{idResidente}")
    public Response obtenerDetalleMoraResidente(@PathParam("idResidente") Integer idResidente) {
        return Response.ok(pagoService.obtenerDetalleMoraResidente(idResidente)).build();
    }

    // ==================== REPORTES Y ESTADÍSTICAS ====================

    @GET
    @Path("/resumen")
    public Response obtenerResumenFinanciero(@QueryParam("periodo") String periodo) {
        if (periodo == null || periodo.isEmpty()) {
            periodo = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));
        }
        return Response.ok(pagoService.obtenerResumenFinanciero(periodo)).build();
    }

    @GET
    @Path("/historial/{idResidente}")
    public Response obtenerHistorialResidente(@PathParam("idResidente") Integer idResidente) {
        return Response.ok(pagoService.obtenerHistorialResidente(idResidente)).build();
    }

    @GET
    @Path("/dashboard")
    public Response obtenerDashboardFinanciero() {
        return Response.ok(pagoService.obtenerDashboardFinanciero()).build();
    }

    // ==================== CLASES DTO PARA REQUESTS (internas) ====================

    public static class RegistrarPagoRequest {
        private Double montoRecibido;

        public Double getMontoRecibido() { return montoRecibido; }
        public void setMontoRecibido(Double montoRecibido) { this.montoRecibido = montoRecibido; }
    }

    public static class AnularPagoRequest {
        private String motivo;

        public String getMotivo() { return motivo; }
        public void setMotivo(String motivo) { this.motivo = motivo; }
    }

    public static class MoraResponse {
        private Integer idResidente;
        private Boolean enMora;

        public MoraResponse(Integer idResidente, Boolean enMora) {
            this.idResidente = idResidente;
            this.enMora = enMora;
        }

        public Integer getIdResidente() { return idResidente; }
        public void setIdResidente(Integer idResidente) { this.idResidente = idResidente; }
        public Boolean getEnMora() { return enMora; }
        public void setEnMora(Boolean enMora) { this.enMora = enMora; }
    }
}