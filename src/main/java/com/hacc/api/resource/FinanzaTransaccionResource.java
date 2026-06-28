package com.hacc.api.resource;

import com.hacc.api.application.service.FinanzaTransaccionService;
import com.hacc.api.domain.model.FinanzaTransaccion;
import com.hacc.api.domain.enums.TipoTransaccion;
import com.hacc.api.domain.enums.CategoriaTransaccion;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.util.List;

@Path("/finanzas/transacciones")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FinanzaTransaccionResource {

    @Inject
    FinanzaTransaccionService transaccionService;

    // ==================== CRUD BÁSICO ====================

    /**
     * Listar todas las transacciones
     * GET /finanzas/transacciones
     */
    @GET
    public Response listarTodas() {
        return Response.ok(transaccionService.listarTodos()).build();
    }

    /**
     * Listar transacciones de un propietario específico
     * GET /finanzas/transacciones/propietario/{idPropietario}
     */
    @GET
    @Path("/propietario/{idPropietario}")
    public Response listarPorPropietario(@PathParam("idPropietario") Integer idPropietario) {
        return Response.ok(transaccionService.listarPorPropietario(idPropietario)).build();
    }

    /**
     * Obtener una transacción por ID
     * GET /finanzas/transacciones/{idTransaccion}
     */
    @GET
    @Path("/{idTransaccion}")
    public Response obtenerPorId(@PathParam("idTransaccion") Long idTransaccion) {
        return transaccionService.buscarPorId(idTransaccion)
                .map(transaccion -> Response.ok(transaccion).build())
                .orElseThrow(() -> new NotFoundException("Transacción no encontrada con ID: " + idTransaccion));
    }

    /**
     * Crear una nueva transacción
     * POST /finanzas/transacciones
     * Body: { "titulo": "...", "monto": 100.00, "tipo": "INGRESO", "categoria": "MANTENIMIENTO", ... }
     */
    @POST
    public Response crearTransaccion(
            @QueryParam("idPropietario") Integer idPropietario,
            FinanzaTransaccion transaccion) {
        if (idPropietario == null) {
            throw new BadRequestException("El parámetro idPropietario es obligatorio");
        }
        FinanzaTransaccion nueva = transaccionService.crear(transaccion, idPropietario);
        return Response.status(Response.Status.CREATED).entity(nueva).build();
    }

    /**
     * Actualizar una transacción existente
     * PATCH /finanzas/transacciones/{idTransaccion}
     */
    @PATCH
    @Path("/{idTransaccion}")
    public Response actualizarTransaccion(
            @PathParam("idTransaccion") Long idTransaccion,
            FinanzaTransaccion transaccion) {
        transaccion.setIdTransaccion(idTransaccion);
        FinanzaTransaccion actualizada = transaccionService.actualizar(transaccion);
        return Response.ok(actualizada).build();
    }

    /**
     * Eliminar una transacción
     * DELETE /finanzas/transacciones/{idTransaccion}
     */
    @DELETE
    @Path("/{idTransaccion}")
    public Response eliminarTransaccion(@PathParam("idTransaccion") Long idTransaccion) {
        transaccionService.eliminar(idTransaccion);
        return Response.noContent().build();
    }

    // ==================== FILTROS ====================

    /**
     * Filtrar transacciones por tipo (INGRESO/EGRESO)
     * GET /finanzas/transacciones/filtros/tipo?tipo=INGRESO
     */
    @GET
    @Path("/filtros/tipo")
    public Response filtrarPorTipo(@QueryParam("tipo") String tipo) {
        if (tipo == null || tipo.isEmpty()) {
            throw new BadRequestException("El parámetro tipo es obligatorio");
        }
        try {
            TipoTransaccion tipoEnum = TipoTransaccion.valueOf(tipo.toUpperCase());
            // Nota: Este método no está en el service, necesitas agregarlo o usar filtro con propietario
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Use el endpoint con propietario: /filtros/propietario/{id}/tipo/{tipo}")
                    .build();
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Tipo inválido. Valores permitidos: INGRESO, EGRESO");
        }
    }

    /**
     * Filtrar transacciones por propietario y tipo
     * GET /finanzas/transacciones/filtros/propietario/{idPropietario}/tipo/{tipo}
     */
    @GET
    @Path("/filtros/propietario/{idPropietario}/tipo/{tipo}")
    public Response filtrarPorPropietarioYTipo(
            @PathParam("idPropietario") Integer idPropietario,
            @PathParam("tipo") String tipo) {
        try {
            TipoTransaccion tipoEnum = TipoTransaccion.valueOf(tipo.toUpperCase());
            return Response.ok(transaccionService.filtrarPorPropietarioYTipo(idPropietario, tipoEnum)).build();
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Tipo inválido. Valores permitidos: INGRESO, EGRESO");
        }
    }

    /**
     * Filtrar transacciones por propietario y categoría
     * GET /finanzas/transacciones/filtros/propietario/{idPropietario}/categoria/{categoria}
     */
    @GET
    @Path("/filtros/propietario/{idPropietario}/categoria/{categoria}")
    public Response filtrarPorPropietarioYCategoria(
            @PathParam("idPropietario") Integer idPropietario,
            @PathParam("categoria") String categoria) {
        try {
            CategoriaTransaccion categoriaEnum = CategoriaTransaccion.valueOf(categoria.toUpperCase());
            return Response.ok(transaccionService.filtrarPorPropietarioYCategoria(idPropietario, categoriaEnum)).build();
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Categoría inválida. Valores permitidos: " +
                    "MANTENIMIENTO, SERVICIOS_BASICOS, OPERACIONES, SEGURIDAD, INGRESOS_EXTRA, OTROS");
        }
    }

    /**
     * Filtrar transacciones por período
     * GET /finanzas/transacciones/filtros/periodo?periodo=2026-06
     */
    @GET
    @Path("/filtros/periodo")
    public Response filtrarPorPeriodo(@QueryParam("periodo") String periodo) {
        if (periodo == null || periodo.isEmpty()) {
            throw new BadRequestException("El parámetro periodo es obligatorio (formato: yyyy-MM)");
        }
        return Response.ok(transaccionService.filtrarPorPeriodo(periodo)).build();
    }

    /**
     * Filtrar transacciones por propietario y período
     * GET /finanzas/transacciones/filtros/propietario/{idPropietario}/periodo/{periodo}
     */
    @GET
    @Path("/filtros/propietario/{idPropietario}/periodo/{periodo}")
    public Response filtrarPorPropietarioYPeriodo(
            @PathParam("idPropietario") Integer idPropietario,
            @PathParam("periodo") String periodo) {
        return Response.ok(transaccionService.filtrarPorPropietarioYPeriodo(idPropietario, periodo)).build();
    }

    /**
     * Filtrar transacciones por rango de fechas
     * GET /finanzas/transacciones/filtros/rango?inicio=2026-01-01&fin=2026-06-30
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
        return Response.ok(transaccionService.filtrarPorRangoFechas(fechaInicio, fechaFin)).build();
    }

    /**
     * Filtrar transacciones por propietario y rango de fechas
     * GET /finanzas/transacciones/filtros/propietario/{idPropietario}/rango?inicio=2026-01-01&fin=2026-06-30
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
        return Response.ok(transaccionService.filtrarPorPropietarioYRangoFechas(idPropietario, fechaInicio, fechaFin)).build();
    }

    // ==================== REPORTES Y ESTADÍSTICAS ====================

    /**
     * Obtener balance financiero de un propietario
     * GET /finanzas/transacciones/balance/{idPropietario}
     */
    @GET
    @Path("/balance/{idPropietario}")
    public Response obtenerBalance(@PathParam("idPropietario") Integer idPropietario) {
        return Response.ok(transaccionService.obtenerBalancePropietario(idPropietario)).build();
    }

    /**
     * Obtener resumen financiero por período
     * GET /finanzas/transacciones/resumen/propietario/{idPropietario}/periodo/{periodo}
     */
    @GET
    @Path("/resumen/propietario/{idPropietario}/periodo/{periodo}")
    public Response obtenerResumenPorPeriodo(
            @PathParam("idPropietario") Integer idPropietario,
            @PathParam("periodo") String periodo) {
        return Response.ok(transaccionService.obtenerResumenPorPeriodo(idPropietario, periodo)).build();
    }

    /**
     * Obtener resumen por categoría para un propietario en un período
     * GET /finanzas/transacciones/resumen/propietario/{idPropietario}/periodo/{periodo}/categorias
     */
    @GET
    @Path("/resumen/propietario/{idPropietario}/periodo/{periodo}/categorias")
    public Response obtenerResumenPorCategoria(
            @PathParam("idPropietario") Integer idPropietario,
            @PathParam("periodo") String periodo) {
        return Response.ok(transaccionService.obtenerResumenPorCategoria(idPropietario, periodo)).build();
    }

    /**
     * Obtener resumen del mes actual
     * GET /finanzas/transacciones/resumen/mes-actual/{idPropietario}
     */
    @GET
    @Path("/resumen/mes-actual/{idPropietario}")
    public Response obtenerResumenMesActual(@PathParam("idPropietario") Integer idPropietario) {
        String periodoActual = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));
        return Response.ok(transaccionService.obtenerResumenPorPeriodo(idPropietario, periodoActual)).build();
    }

    /**
     * Obtener dashboard financiero completo de un propietario
     * GET /finanzas/transacciones/dashboard/{idPropietario}
     */
    @GET
    @Path("/dashboard/{idPropietario}")
    public Response obtenerDashboard(@PathParam("idPropietario") Integer idPropietario) {
        FinanzaTransaccionService.BalanceFinancieroDTO balance = transaccionService.obtenerBalancePropietario(idPropietario);
        String periodoActual = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));
        FinanzaTransaccionService.ResumenFinancieroTransaccionDTO resumen = 
                transaccionService.obtenerResumenPorPeriodo(idPropietario, periodoActual);
        List<FinanzaTransaccionService.ResumenPorCategoriaDTO> categorias = 
                transaccionService.obtenerResumenPorCategoria(idPropietario, periodoActual);

        DashboardFinancieroDTO dashboard = new DashboardFinancieroDTO();
        dashboard.setBalance(balance);
        dashboard.setResumenMesActual(resumen);
        dashboard.setResumenPorCategoria(categorias);

        return Response.ok(dashboard).build();
    }

    // ==================== DTO PARA DASHBOARD ====================

    public static class DashboardFinancieroDTO {
        private FinanzaTransaccionService.BalanceFinancieroDTO balance;
        private FinanzaTransaccionService.ResumenFinancieroTransaccionDTO resumenMesActual;
        private List<FinanzaTransaccionService.ResumenPorCategoriaDTO> resumenPorCategoria;

        // Getters y Setters
        public FinanzaTransaccionService.BalanceFinancieroDTO getBalance() { return balance; }
        public void setBalance(FinanzaTransaccionService.BalanceFinancieroDTO balance) { this.balance = balance; }
        public FinanzaTransaccionService.ResumenFinancieroTransaccionDTO getResumenMesActual() { return resumenMesActual; }
        public void setResumenMesActual(FinanzaTransaccionService.ResumenFinancieroTransaccionDTO resumenMesActual) { 
            this.resumenMesActual = resumenMesActual; 
        }
        public List<FinanzaTransaccionService.ResumenPorCategoriaDTO> getResumenPorCategoria() { return resumenPorCategoria; }
        public void setResumenPorCategoria(List<FinanzaTransaccionService.ResumenPorCategoriaDTO> resumenPorCategoria) { 
            this.resumenPorCategoria = resumenPorCategoria; 
        }
    }
}