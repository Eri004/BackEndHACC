package com.hacc.api.application.service;

import com.hacc.api.domain.model.ServicioProveedor;
import com.hacc.api.domain.model.Propietario;
import com.hacc.api.domain.model.FinanzaTransaccion;
import com.hacc.api.domain.repository.IServicioProveedorRepo;
import com.hacc.api.domain.repository.IPropietarioRepo;
import com.hacc.api.domain.repository.IFinanzaTransaccionRepo;
import com.hacc.api.domain.enums.NombreServicio;
import com.hacc.api.domain.enums.EstadoServicio;
import com.hacc.api.domain.enums.TipoTransaccion;
import com.hacc.api.domain.enums.CategoriaTransaccion;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ServicioProveedorService {

    private static final Logger LOG = LoggerFactory.getLogger(ServicioProveedorService.class);
    private static final DateTimeFormatter PERIODO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Inject
    IServicioProveedorRepo servicioRepo;

    @Inject
    IPropietarioRepo propietarioRepo;

    @Inject
    IFinanzaTransaccionRepo transaccionRepo;

    // ==================== CRUD BÁSICO ====================

    @Transactional
    public ServicioProveedor crear(ServicioProveedor servicio, Integer idPropietario) {
        LOG.info("Creando nuevo servicio para propietario: {}", idPropietario);

        Propietario propietario = propietarioRepo.buscarPorId(idPropietario)
                .orElseThrow(() -> new NotFoundException("Propietario no encontrado con ID: " + idPropietario));

        // Validaciones
        if (servicio.getNombre() == null) {
            throw new IllegalArgumentException("El nombre del servicio es obligatorio");
        }
        if (servicio.getMontoFacturado() == null || servicio.getMontoFacturado().doubleValue() <= 0) {
            throw new IllegalArgumentException("El monto facturado debe ser mayor a 0");
        }
        if (servicio.getFechaVencimiento() == null) {
            throw new IllegalArgumentException("La fecha de vencimiento es obligatoria");
        }
        if (servicio.getMes() == null || servicio.getMes().isEmpty()) {
            servicio.setMes(getPeriodoActual());
        }

        // Asignar valores por defecto
        if (servicio.getEstado() == null) {
            servicio.setEstado(EstadoServicio.PENDIENTE);
        }
        if (servicio.getMontoPagado() == null) {
            servicio.setMontoPagado(BigDecimal.ZERO);
        }

        // Redondear montos
        if (servicio.getMontoFacturado() != null) {
            servicio.setMontoFacturado(servicio.getMontoFacturado().setScale(2, RoundingMode.HALF_UP));
        }
        if (servicio.getMontoPagado() != null) {
            servicio.setMontoPagado(servicio.getMontoPagado().setScale(2, RoundingMode.HALF_UP));
        }

        // Asignar propietario
        servicio.setPropietario(propietario);
        servicio.setCreadoEn(LocalDateTime.now());
        servicio.setActualizadoEn(LocalDateTime.now());

        servicioRepo.crear(servicio);
        LOG.info("Servicio creado exitosamente con ID: {}", servicio.getIdServicio());
        return servicio;
    }

    @Transactional
    public ServicioProveedor actualizar(ServicioProveedor servicio) {
        LOG.info("Actualizando servicio ID: {}", servicio.getIdServicio());

        ServicioProveedor existente = servicioRepo.buscarPorId(servicio.getIdServicio())
                .orElseThrow(() -> new NotFoundException("Servicio no encontrado con ID: " + servicio.getIdServicio()));

        // Actualizar solo campos permitidos
        if (servicio.getNombre() != null) existente.setNombre(servicio.getNombre());
        if (servicio.getMontoFacturado() != null) {
            existente.setMontoFacturado(servicio.getMontoFacturado().setScale(2, RoundingMode.HALF_UP));
        }
        if (servicio.getMontoPagado() != null) {
            existente.setMontoPagado(servicio.getMontoPagado().setScale(2, RoundingMode.HALF_UP));
        }
        if (servicio.getEstado() != null) existente.setEstado(servicio.getEstado());
        if (servicio.getFechaVencimiento() != null) existente.setFechaVencimiento(servicio.getFechaVencimiento());
        if (servicio.getMes() != null) existente.setMes(servicio.getMes());

        existente.setActualizadoEn(LocalDateTime.now());

        servicioRepo.actualizar(existente);
        LOG.info("Servicio actualizado exitosamente");
        return existente;
    }

    @Transactional
    public void eliminar(Long idServicio) {
        LOG.info("Eliminando servicio ID: {}", idServicio);

        ServicioProveedor servicio = servicioRepo.buscarPorId(idServicio)
                .orElseThrow(() -> new NotFoundException("Servicio no encontrado con ID: " + idServicio));

        // No permitir eliminar si está pagado
        if (servicio.getEstado() == EstadoServicio.PAGADO) {
            throw new IllegalStateException("No se puede eliminar un servicio ya pagado");
        }

        servicioRepo.eliminar(idServicio);
        LOG.info("Servicio eliminado exitosamente");
    }

    public Optional<ServicioProveedor> buscarPorId(Long idServicio) {
        return servicioRepo.buscarPorId(idServicio);
    }

    public List<ServicioProveedor> listarTodos() {
        return servicioRepo.listarTodos();
    }

    public List<ServicioProveedor> listarPorPropietario(Integer idPropietario) {
        propietarioRepo.buscarPorId(idPropietario)
                .orElseThrow(() -> new NotFoundException("Propietario no encontrado con ID: " + idPropietario));
        return servicioRepo.listarPorPropietario(idPropietario);
    }

    // ==================== REGISTRO DE PAGO DE SERVICIO ====================

    @Transactional
    public ServicioProveedor registrarPagoServicio(Long idServicio, BigDecimal montoPagado, Long idTransaccion) {
        LOG.info("Registrando pago para servicio ID: {}, monto: {}", idServicio, montoPagado);

        ServicioProveedor servicio = servicioRepo.buscarPorId(idServicio)
                .orElseThrow(() -> new NotFoundException("Servicio no encontrado con ID: " + idServicio));

        if (servicio.getEstado() == EstadoServicio.PAGADO) {
            throw new IllegalStateException("Este servicio ya está pagado");
        }

        if (montoPagado == null || montoPagado.doubleValue() <= 0) {
            throw new IllegalArgumentException("El monto pagado debe ser mayor a 0");
        }

        FinanzaTransaccion transaccion = null;
        if (idTransaccion != null) {
            transaccion = transaccionRepo.buscarPorId(idTransaccion)
                    .orElseThrow(() -> new NotFoundException("Transacción no encontrada con ID: " + idTransaccion));
            servicio.setTransaccion(transaccion);
        }

        servicio.setMontoPagado(montoPagado.setScale(2, RoundingMode.HALF_UP));

        // Determinar estado: si pagó completo, PAGADO; si no, PARCIAL
        if (montoPagado.doubleValue() >= servicio.getMontoFacturado().doubleValue()) {
            servicio.setEstado(EstadoServicio.PAGADO);
        } else {
            servicio.setEstado(EstadoServicio.EN_DEUDA);
        }

        servicio.setActualizadoEn(LocalDateTime.now());

        servicioRepo.actualizar(servicio);
        LOG.info("Pago de servicio registrado exitosamente. Estado: {}", servicio.getEstado());
        return servicio;
    }

    // ==================== FILTROS ====================

    public List<ServicioProveedor> filtrarPorPropietarioYEstado(Integer idPropietario, EstadoServicio estado) {
        propietarioRepo.buscarPorId(idPropietario)
                .orElseThrow(() -> new NotFoundException("Propietario no encontrado con ID: " + idPropietario));
        return servicioRepo.listarPorPropietarioYEstado(idPropietario, estado);
    }

    public List<ServicioProveedor> filtrarPorPropietarioYNombre(Integer idPropietario, NombreServicio nombre) {
        propietarioRepo.buscarPorId(idPropietario)
                .orElseThrow(() -> new NotFoundException("Propietario no encontrado con ID: " + idPropietario));
        return servicioRepo.listarPorPropietarioYNombre(idPropietario, nombre);
    }

    public List<ServicioProveedor> filtrarPorMes(String mes) {
        return servicioRepo.listarPorMes(mes);
    }

    public List<ServicioProveedor> filtrarPorPropietarioYMes(Integer idPropietario, String mes) {
        propietarioRepo.buscarPorId(idPropietario)
                .orElseThrow(() -> new NotFoundException("Propietario no encontrado con ID: " + idPropietario));
        return servicioRepo.listarPorPropietarioYMes(idPropietario, mes);
    }

    public List<ServicioProveedor> filtrarPorPropietarioMesYEstado(Integer idPropietario, String mes, EstadoServicio estado) {
        propietarioRepo.buscarPorId(idPropietario)
                .orElseThrow(() -> new NotFoundException("Propietario no encontrado con ID: " + idPropietario));
        return servicioRepo.listarPorPropietarioMesYEstado(idPropietario, mes, estado);
    }

    public List<ServicioProveedor> filtrarPorPropietarioMesYNombre(Integer idPropietario, String mes, NombreServicio nombre) {
        propietarioRepo.buscarPorId(idPropietario)
                .orElseThrow(() -> new NotFoundException("Propietario no encontrado con ID: " + idPropietario));
        return servicioRepo.listarPorPropietarioMesYNombre(idPropietario, mes, nombre);
    }

    public List<ServicioProveedor> filtrarPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return servicioRepo.listarPorRangoFechas(fechaInicio, fechaFin);
    }

    public List<ServicioProveedor> filtrarPorPropietarioYRangoFechas(Integer idPropietario, LocalDate fechaInicio, LocalDate fechaFin) {
        propietarioRepo.buscarPorId(idPropietario)
                .orElseThrow(() -> new NotFoundException("Propietario no encontrado con ID: " + idPropietario));
        return servicioRepo.listarPorPropietarioYRangoFechas(idPropietario, fechaInicio, fechaFin);
    }

    // ==================== REPORTES Y ESTADÍSTICAS ====================

    /**
     * Obtiene el resumen de servicios de un propietario por mes
     */
    public ResumenServiciosDTO obtenerResumenServiciosPorMes(Integer idPropietario, String mes) {
        LOG.info("Obteniendo resumen de servicios para propietario: {} mes: {}", idPropietario, mes);

        propietarioRepo.buscarPorId(idPropietario)
                .orElseThrow(() -> new NotFoundException("Propietario no encontrado con ID: " + idPropietario));

        List<ServicioProveedor> servicios = servicioRepo.listarPorPropietarioYMes(idPropietario, mes);

        Double totalFacturado = servicios.stream()
                .mapToDouble(s -> s.getMontoFacturado().doubleValue())
                .sum();

        Double totalPagado = servicios.stream()
                .filter(s -> s.getEstado() == EstadoServicio.PAGADO)
                .mapToDouble(s -> s.getMontoPagado() != null ? s.getMontoPagado().doubleValue() : 0)
                .sum();

        Long totalPendientes = servicios.stream()
                .filter(s -> s.getEstado() == EstadoServicio.PENDIENTE)
                .count();

        Long totalPagados = servicios.stream()
                .filter(s -> s.getEstado() == EstadoServicio.PAGADO)
                .count();

        Long totalEnDeuda = servicios.stream()
                .filter(s -> s.getEstado() == EstadoServicio.EN_DEUDA)
                .count();

        ResumenServiciosDTO resumen = new ResumenServiciosDTO();
        resumen.setMes(mes);
        resumen.setTotalServicios((long) servicios.size());
        resumen.setTotalFacturado(totalFacturado);
        resumen.setTotalPagado(totalPagado);
        resumen.setTotalPendientes(totalPendientes);
        resumen.setTotalPagados(totalPagados);
        resumen.setTotalEnDeuda(totalEnDeuda);
        resumen.setServicios(servicios);

        return resumen;
    }

    /**
     * Obtiene los servicios vencidos de un propietario
     */
    public List<ServicioProveedor> obtenerServiciosVencidos(Integer idPropietario) {
        LOG.info("Obteniendo servicios vencidos para propietario: {}", idPropietario);

        propietarioRepo.buscarPorId(idPropietario)
                .orElseThrow(() -> new NotFoundException("Propietario no encontrado con ID: " + idPropietario));

        return servicioRepo.listarVencidosPorPropietario(idPropietario);
    }

    /**
     * Obtiene el dashboard de servicios
     */
    public DashboardServiciosDTO obtenerDashboardServicios(Integer idPropietario) {
        LOG.info("Obteniendo dashboard de servicios para propietario: {}", idPropietario);

        propietarioRepo.buscarPorId(idPropietario)
                .orElseThrow(() -> new NotFoundException("Propietario no encontrado con ID: " + idPropietario));

        String mesActual = getPeriodoActual();

        // Contar por estado
        Long totalPendientes = servicioRepo.contarPorPropietarioYEstado(idPropietario, EstadoServicio.PENDIENTE);
        Long totalPagados = servicioRepo.contarPorPropietarioYEstado(idPropietario, EstadoServicio.PAGADO);
        Long totalEnDeuda = servicioRepo.contarPorPropietarioYEstado(idPropietario, EstadoServicio.EN_DEUDA);
        Long totalCortados = servicioRepo.contarPorPropietarioYEstado(idPropietario, EstadoServicio.CORTADO);

        // Totales del mes actual
        BigDecimal totalFacturadoMesBD = servicioRepo.sumarMontoFacturadoPorPropietarioYMes(idPropietario, mesActual);
        BigDecimal totalPagadoMesBD = servicioRepo.sumarMontoPagadoPorPropietarioYMes(idPropietario, mesActual);

        Double totalFacturadoMes = totalFacturadoMesBD != null ? totalFacturadoMesBD.doubleValue() : 0.0;
        Double totalPagadoMes = totalPagadoMesBD != null ? totalPagadoMesBD.doubleValue() : 0.0;
        // Servicios vencidos
        List<ServicioProveedor> vencidos = servicioRepo.listarVencidosPorPropietario(idPropietario);

        // Agrupar por estado
        List<Object[]> agrupados = servicioRepo.agruparPorEstado(idPropietario);

        DashboardServiciosDTO dashboard = new DashboardServiciosDTO();
        dashboard.setMesActual(mesActual);
        dashboard.setTotalFacturadoMes(totalFacturadoMes != null ? totalFacturadoMes : 0.0);
        dashboard.setTotalPagadoMes(totalPagadoMes != null ? totalPagadoMes : 0.0);
        dashboard.setTotalPendientes(totalPendientes);
        dashboard.setTotalPagados(totalPagados);
        dashboard.setTotalEnDeuda(totalEnDeuda);
        dashboard.setTotalCortados(totalCortados);
        dashboard.setServiciosVencidos(vencidos);
        dashboard.setAgrupadosPorEstado(agrupados);

        return dashboard;
    }

    // ==================== MÉTODOS PRIVADOS ====================

    private String getPeriodoActual() {
        return LocalDate.now().format(PERIODO_FORMATTER);
    }

    // ==================== DTOS ====================

    public static class ResumenServiciosDTO {
        private String mes;
        private Long totalServicios;
        private Double totalFacturado;
        private Double totalPagado;
        private Long totalPendientes;
        private Long totalPagados;
        private Long totalEnDeuda;
        private List<ServicioProveedor> servicios;

        // Getters y Setters
        public String getMes() { return mes; }
        public void setMes(String mes) { this.mes = mes; }
        public Long getTotalServicios() { return totalServicios; }
        public void setTotalServicios(Long totalServicios) { this.totalServicios = totalServicios; }
        public Double getTotalFacturado() { return totalFacturado; }
        public void setTotalFacturado(Double totalFacturado) { this.totalFacturado = totalFacturado; }
        public Double getTotalPagado() { return totalPagado; }
        public void setTotalPagado(Double totalPagado) { this.totalPagado = totalPagado; }
        public Long getTotalPendientes() { return totalPendientes; }
        public void setTotalPendientes(Long totalPendientes) { this.totalPendientes = totalPendientes; }
        public Long getTotalPagados() { return totalPagados; }
        public void setTotalPagados(Long totalPagados) { this.totalPagados = totalPagados; }
        public Long getTotalEnDeuda() { return totalEnDeuda; }
        public void setTotalEnDeuda(Long totalEnDeuda) { this.totalEnDeuda = totalEnDeuda; }
        public List<ServicioProveedor> getServicios() { return servicios; }
        public void setServicios(List<ServicioProveedor> servicios) { this.servicios = servicios; }
    }

    public static class DashboardServiciosDTO {
        private String mesActual;
        private Double totalFacturadoMes;
        private Double totalPagadoMes;
        private Long totalPendientes;
        private Long totalPagados;
        private Long totalEnDeuda;
        private Long totalCortados;
        private List<ServicioProveedor> serviciosVencidos;
        private List<Object[]> agrupadosPorEstado;

        // Getters y Setters
        public String getMesActual() { return mesActual; }
        public void setMesActual(String mesActual) { this.mesActual = mesActual; }
        public Double getTotalFacturadoMes() { return totalFacturadoMes; }
        public void setTotalFacturadoMes(Double totalFacturadoMes) { this.totalFacturadoMes = totalFacturadoMes; }
        public Double getTotalPagadoMes() { return totalPagadoMes; }
        public void setTotalPagadoMes(Double totalPagadoMes) { this.totalPagadoMes = totalPagadoMes; }
        public Long getTotalPendientes() { return totalPendientes; }
        public void setTotalPendientes(Long totalPendientes) { this.totalPendientes = totalPendientes; }
        public Long getTotalPagados() { return totalPagados; }
        public void setTotalPagados(Long totalPagados) { this.totalPagados = totalPagados; }
        public Long getTotalEnDeuda() { return totalEnDeuda; }
        public void setTotalEnDeuda(Long totalEnDeuda) { this.totalEnDeuda = totalEnDeuda; }
        public Long getTotalCortados() { return totalCortados; }
        public void setTotalCortados(Long totalCortados) { this.totalCortados = totalCortados; }
        public List<ServicioProveedor> getServiciosVencidos() { return serviciosVencidos; }
        public void setServiciosVencidos(List<ServicioProveedor> serviciosVencidos) { this.serviciosVencidos = serviciosVencidos; }
        public List<Object[]> getAgrupadosPorEstado() { return agrupadosPorEstado; }
        public void setAgrupadosPorEstado(List<Object[]> agrupadosPorEstado) { this.agrupadosPorEstado = agrupadosPorEstado; }
    }
}