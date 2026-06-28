package com.hacc.api.application.service;

import com.hacc.api.domain.model.FinanzaTransaccion;
import com.hacc.api.domain.model.Propietario;
import com.hacc.api.domain.repository.IFinanzaTransaccionRepo;
import com.hacc.api.domain.repository.IPropietarioRepo;
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
public class FinanzaTransaccionService {

    private static final Logger LOG = LoggerFactory.getLogger(FinanzaTransaccionService.class);
    private static final DateTimeFormatter PERIODO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Inject
    IFinanzaTransaccionRepo transaccionRepo;

    @Inject
    IPropietarioRepo propietarioRepo;

    // ==================== CRUD BÁSICO ====================

    @Transactional
    public FinanzaTransaccion crear(FinanzaTransaccion transaccion, Integer idPropietario) {
        LOG.info("Creando nueva transacción para propietario: {}", idPropietario);

        Propietario propietario = propietarioRepo.buscarPorId(idPropietario)
                .orElseThrow(() -> new NotFoundException("Propietario no encontrado con ID: " + idPropietario));

        if (transaccion.getTitulo() == null || transaccion.getTitulo().isEmpty()) {
            throw new IllegalArgumentException("El título es obligatorio");
        }
        if (transaccion.getMonto() == null || transaccion.getMonto().doubleValue() <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a 0");
        }
        if (transaccion.getTipo() == null) {
            throw new IllegalArgumentException("El tipo de transacción es obligatorio (INGRESO/EGRESO)");
        }
        if (transaccion.getCategoria() == null) {
            throw new IllegalArgumentException("La categoría es obligatoria");
        }

        if (transaccion.getFecha() == null) {
            transaccion.setFecha(LocalDate.now());
        }
        if (transaccion.getPeriodo() == null || transaccion.getPeriodo().isEmpty()) {
            transaccion.setPeriodo(getPeriodoActual());
        }

        if (transaccion.getMonto() != null) {
            transaccion.setMonto(transaccion.getMonto().setScale(2, RoundingMode.HALF_UP));
        }

        transaccion.setPropietario(propietario);
        transaccion.setCreadoEn(LocalDateTime.now());
        transaccion.setActualizadoEn(LocalDateTime.now());

        transaccionRepo.crear(transaccion);
        LOG.info("Transacción creada exitosamente con ID: {}", transaccion.getIdTransaccion());
        return transaccion;
    }

    @Transactional
    public FinanzaTransaccion actualizar(FinanzaTransaccion transaccion) {
        LOG.info("Actualizando transacción ID: {}", transaccion.getIdTransaccion());

        FinanzaTransaccion existente = transaccionRepo.buscarPorId(transaccion.getIdTransaccion())
                .orElseThrow(() -> new NotFoundException("Transacción no encontrada con ID: " + transaccion.getIdTransaccion()));

        if (transaccion.getTitulo() != null) existente.setTitulo(transaccion.getTitulo());
        if (transaccion.getMonto() != null) {
            existente.setMonto(transaccion.getMonto().setScale(2, RoundingMode.HALF_UP));
        }
        if (transaccion.getFecha() != null) existente.setFecha(transaccion.getFecha());
        if (transaccion.getTipo() != null) existente.setTipo(transaccion.getTipo());
        if (transaccion.getCategoria() != null) existente.setCategoria(transaccion.getCategoria());
        if (transaccion.getSubcategoria() != null) existente.setSubcategoria(transaccion.getSubcategoria());
        if (transaccion.getComprobante() != null) existente.setComprobante(transaccion.getComprobante());
        if (transaccion.getPeriodo() != null) existente.setPeriodo(transaccion.getPeriodo());

        existente.setActualizadoEn(LocalDateTime.now());

        transaccionRepo.actualizar(existente);
        LOG.info("Transacción actualizada exitosamente");
        return existente;
    }

    @Transactional
    public void eliminar(Long idTransaccion) {
        LOG.info("Eliminando transacción ID: {}", idTransaccion);

        FinanzaTransaccion transaccion = transaccionRepo.buscarPorId(idTransaccion)
                .orElseThrow(() -> new NotFoundException("Transacción no encontrada con ID: " + idTransaccion));

        transaccionRepo.eliminar(idTransaccion);
        LOG.info("Transacción eliminada exitosamente");
    }

    public Optional<FinanzaTransaccion> buscarPorId(Long idTransaccion) {
        return transaccionRepo.buscarPorId(idTransaccion);
    }

    public List<FinanzaTransaccion> listarTodos() {
        return transaccionRepo.listarTodos();
    }

    public List<FinanzaTransaccion> listarPorPropietario(Integer idPropietario) {
        propietarioRepo.buscarPorId(idPropietario)
                .orElseThrow(() -> new NotFoundException("Propietario no encontrado con ID: " + idPropietario));
        return transaccionRepo.listarPorPropietario(idPropietario);
    }

    // ==================== FILTROS ====================

    public List<FinanzaTransaccion> filtrarPorPropietarioYTipo(Integer idPropietario, TipoTransaccion tipo) {
        propietarioRepo.buscarPorId(idPropietario)
                .orElseThrow(() -> new NotFoundException("Propietario no encontrado con ID: " + idPropietario));
        return transaccionRepo.listarPorPropietarioYTipo(idPropietario, tipo);
    }

    public List<FinanzaTransaccion> filtrarPorPropietarioYCategoria(Integer idPropietario, CategoriaTransaccion categoria) {
        propietarioRepo.buscarPorId(idPropietario)
                .orElseThrow(() -> new NotFoundException("Propietario no encontrado con ID: " + idPropietario));
        return transaccionRepo.listarPorPropietarioYCategoria(idPropietario, categoria);
    }

    public List<FinanzaTransaccion> filtrarPorPeriodo(String periodo) {
        return transaccionRepo.listarPorPeriodo(periodo);
    }

    public List<FinanzaTransaccion> filtrarPorPropietarioYPeriodo(Integer idPropietario, String periodo) {
        propietarioRepo.buscarPorId(idPropietario)
                .orElseThrow(() -> new NotFoundException("Propietario no encontrado con ID: " + idPropietario));
        return transaccionRepo.listarPorPropietarioYPeriodo(idPropietario, periodo);
    }

    public List<FinanzaTransaccion> filtrarPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return transaccionRepo.listarPorRangoFechas(fechaInicio, fechaFin);
    }

    public List<FinanzaTransaccion> filtrarPorPropietarioYRangoFechas(Integer idPropietario, LocalDate fechaInicio, LocalDate fechaFin) {
        propietarioRepo.buscarPorId(idPropietario)
                .orElseThrow(() -> new NotFoundException("Propietario no encontrado con ID: " + idPropietario));
        return transaccionRepo.listarPorPropietarioYRangoFechas(idPropietario, fechaInicio, fechaFin);
    }

    // ==================== REPORTES Y ESTADÍSTICAS (CORREGIDOS) ====================

    /**
     * Obtiene el balance financiero de un propietario
     */
    public BalanceFinancieroDTO obtenerBalancePropietario(Integer idPropietario) {
        LOG.info("Obteniendo balance financiero para propietario: {}", idPropietario);

        propietarioRepo.buscarPorId(idPropietario)
                .orElseThrow(() -> new NotFoundException("Propietario no encontrado con ID: " + idPropietario));

        BigDecimal totalIngresos = transaccionRepo.sumarMontosPorPropietarioYTipo(idPropietario, TipoTransaccion.INGRESO);
        BigDecimal totalEgresos = transaccionRepo.sumarMontosPorPropietarioYTipo(idPropietario, TipoTransaccion.EGRESO);

        BalanceFinancieroDTO balance = new BalanceFinancieroDTO();
        balance.setIdPropietario(idPropietario);
        balance.setTotalIngresos(totalIngresos != null ? totalIngresos.doubleValue() : 0.0);
        balance.setTotalEgresos(totalEgresos != null ? totalEgresos.doubleValue() : 0.0);
        balance.setBalance(balance.getTotalIngresos() - balance.getTotalEgresos());

        return balance;
    }

    /**
     * Obtiene el resumen financiero por período
     */
    public ResumenFinancieroTransaccionDTO obtenerResumenPorPeriodo(Integer idPropietario, String periodo) {
        LOG.info("Obteniendo resumen para propietario: {} período: {}", idPropietario, periodo);

        propietarioRepo.buscarPorId(idPropietario)
                .orElseThrow(() -> new NotFoundException("Propietario no encontrado con ID: " + idPropietario));

        List<FinanzaTransaccion> transacciones = transaccionRepo.listarPorPropietarioYPeriodo(idPropietario, periodo);

        Double totalIngresos = transacciones.stream()
                .filter(t -> t.getTipo() == TipoTransaccion.INGRESO)
                .mapToDouble(t -> t.getMonto().doubleValue())
                .sum();

        Double totalEgresos = transacciones.stream()
                .filter(t -> t.getTipo() == TipoTransaccion.EGRESO)
                .mapToDouble(t -> t.getMonto().doubleValue())
                .sum();

        ResumenFinancieroTransaccionDTO resumen = new ResumenFinancieroTransaccionDTO();
        resumen.setPeriodo(periodo);
        resumen.setTotalIngresos(totalIngresos);
        resumen.setTotalEgresos(totalEgresos);
        resumen.setBalance(totalIngresos - totalEgresos);
        resumen.setCantidadTransacciones((long) transacciones.size());
        resumen.setTransacciones(transacciones);

        return resumen;
    }

    /**
     * Obtiene el resumen por categoría
     */
    public List<ResumenPorCategoriaDTO> obtenerResumenPorCategoria(Integer idPropietario, String periodo) {
        LOG.info("Obteniendo resumen por categoría para propietario: {} período: {}", idPropietario, periodo);

        propietarioRepo.buscarPorId(idPropietario)
                .orElseThrow(() -> new NotFoundException("Propietario no encontrado con ID: " + idPropietario));

        List<FinanzaTransaccion> transacciones = transaccionRepo.listarPorPropietarioYPeriodo(idPropietario, periodo);

        return transacciones.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        FinanzaTransaccion::getCategoria,
                        java.util.stream.Collectors.summingDouble(t -> t.getMonto().doubleValue())
                ))
                .entrySet().stream()
                .map(entry -> {
                    ResumenPorCategoriaDTO dto = new ResumenPorCategoriaDTO();
                    dto.setCategoria(entry.getKey());
                    dto.setTotal(entry.getValue());
                    return dto;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    // ==================== MÉTODOS PRIVADOS ====================

    private String getPeriodoActual() {
        return LocalDate.now().format(PERIODO_FORMATTER);
    }

    // ==================== DTOS ====================

    public static class BalanceFinancieroDTO {
        private Integer idPropietario;
        private Double totalIngresos;
        private Double totalEgresos;
        private Double balance;

        // Getters y Setters
        public Integer getIdPropietario() { return idPropietario; }
        public void setIdPropietario(Integer idPropietario) { this.idPropietario = idPropietario; }
        public Double getTotalIngresos() { return totalIngresos; }
        public void setTotalIngresos(Double totalIngresos) { this.totalIngresos = totalIngresos; }
        public Double getTotalEgresos() { return totalEgresos; }
        public void setTotalEgresos(Double totalEgresos) { this.totalEgresos = totalEgresos; }
        public Double getBalance() { return balance; }
        public void setBalance(Double balance) { this.balance = balance; }
    }

    public static class ResumenFinancieroTransaccionDTO {
        private String periodo;
        private Double totalIngresos;
        private Double totalEgresos;
        private Double balance;
        private Long cantidadTransacciones;
        private List<FinanzaTransaccion> transacciones;

        // Getters y Setters
        public String getPeriodo() { return periodo; }
        public void setPeriodo(String periodo) { this.periodo = periodo; }
        public Double getTotalIngresos() { return totalIngresos; }
        public void setTotalIngresos(Double totalIngresos) { this.totalIngresos = totalIngresos; }
        public Double getTotalEgresos() { return totalEgresos; }
        public void setTotalEgresos(Double totalEgresos) { this.totalEgresos = totalEgresos; }
        public Double getBalance() { return balance; }
        public void setBalance(Double balance) { this.balance = balance; }
        public Long getCantidadTransacciones() { return cantidadTransacciones; }
        public void setCantidadTransacciones(Long cantidadTransacciones) { this.cantidadTransacciones = cantidadTransacciones; }
        public List<FinanzaTransaccion> getTransacciones() { return transacciones; }
        public void setTransacciones(List<FinanzaTransaccion> transacciones) { this.transacciones = transacciones; }
    }

    public static class ResumenPorCategoriaDTO {
        private CategoriaTransaccion categoria;
        private Double total;

        public CategoriaTransaccion getCategoria() { return categoria; }
        public void setCategoria(CategoriaTransaccion categoria) { this.categoria = categoria; }
        public Double getTotal() { return total; }
        public void setTotal(Double total) { this.total = total; }
    }
}