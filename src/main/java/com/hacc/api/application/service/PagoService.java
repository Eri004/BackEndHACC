package com.hacc.api.application.service;

import com.hacc.api.domain.enums.EstadoPago;
import com.hacc.api.domain.model.Pago;
import com.hacc.api.domain.model.Residente;
import com.hacc.api.domain.repository.IPagoRepo;
import com.hacc.api.domain.repository.IResidenteRepo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class PagoService {

    private static final Logger LOG = LoggerFactory.getLogger(PagoService.class);
    private static final int DIA_VENCIMIENTO = 10;
    private static final double TASA_INTERES_MORATORIO = 0.05; 
    private static final DateTimeFormatter PERIODO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Inject
    IPagoRepo pagoRepo;
    
    @Inject
    IResidenteRepo residenteRepo;

    // ==================== CRUD BÁSICO ====================

    @Transactional
    public Pago crear(Pago pago) {
        LOG.info("Creando nuevo pago para residente: {}", pago.getIdResidente());
        
        // Validaciones
        if (pago.getIdResidente() == null) {
            throw new IllegalArgumentException("El residente es obligatorio");
        }
        if (pago.getMontoEsperado() == null || pago.getMontoEsperado() <= 0) {
            throw new IllegalArgumentException("El monto esperado debe ser mayor a 0");
        }
        if (pago.getPeriodo() == null || pago.getPeriodo().isEmpty()) {
            throw new IllegalArgumentException("El período es obligatorio");
        }

        Residente residente = residenteRepo.buscarPorId(pago.getIdResidente())
                .orElseThrow(() -> new NotFoundException("Residente no encontrado con ID: " + pago.getIdResidente()));
        
        // Asignar valores por defecto
        if (pago.getEstado() == null) {
            pago.setEstado(EstadoPago.PENDIENTE);
        }
        if (pago.getIntereses() == null) {
            pago.setIntereses(0.0);
        }
        if (pago.getFecha() == null) {
            pago.setFecha(LocalDate.now());
        }
        if (pago.getFechaVencimiento() == null) {
            pago.setFechaVencimiento(calcularFechaVencimiento(pago.getPeriodo()));
        }
        if (pago.getTitulo() == null || pago.getTitulo().isEmpty()) {
            pago.setTitulo("Cuota condominio - " + pago.getPeriodo());
        }
        
        pago.setFechaCreacion(LocalDateTime.now());
        pago.setFechaActualizacion(LocalDateTime.now());
        
        pagoRepo.crearPago(pago);
        LOG.info("Pago creado exitosamente con ID: {}", pago.getId_pago());
        return pago;
    }

    @Transactional
    public Pago actualizar(Pago pago) {
        LOG.info("Actualizando pago ID: {}", pago.getId_pago());
        
        Pago pagoExistente = pagoRepo.buscarPorId(pago.getId_pago())
                .orElseThrow(() -> new NotFoundException("Pago no encontrado con ID: " + pago.getId_pago()));
        
        pagoExistente.setTitulo(pago.getTitulo());
        pagoExistente.setMontoEsperado(pago.getMontoEsperado());
        pagoExistente.setFechaVencimiento(pago.getFechaVencimiento());
        pagoExistente.setPeriodo(pago.getPeriodo());
        pagoExistente.setObservacion(pago.getObservacion());
        pagoExistente.setFechaActualizacion(LocalDateTime.now());
        
        pagoRepo.actualizarPago(pagoExistente);
        LOG.info("Pago actualizado exitosamente");
        return pagoExistente;
    }

    @Transactional
    public void eliminar(Integer idPago) {
        LOG.info("Eliminando pago ID: {}", idPago);
        
        Pago pago = pagoRepo.buscarPorId(idPago)
                .orElseThrow(() -> new NotFoundException("Pago no encontrado con ID: " + idPago));
        
        if (pago.getEstado() == EstadoPago.PAGADO) {
            throw new IllegalStateException("No se puede eliminar un pago ya pagado");
        }
        
        pagoRepo.eliminarPago(idPago);
        LOG.info("Pago eliminado exitosamente");
    }

    public Optional<Pago> buscarPorId(Integer idPago) {
        return pagoRepo.buscarPorId(idPago);
    }

    public List<Pago> listarTodos() {
        return pagoRepo.listarTodos();
    }

    public List<Pago> listarPorResidente(Integer idResidente) {
        return pagoRepo.listarPorResidente(idResidente);
    }

    // ==================== GENERACIÓN AUTOMÁTICA DE CUOTAS ====================

    /**
     * Genera cuotas mensuales para todos los residentes activos
     * Este método debe ser llamado por un cron job el día 1 de cada mes
     */
    @Transactional
    public int generarCuotasMensuales() {
        LOG.info("Generando cuotas mensuales para el período: {}", getPeriodoActual());
        
        String periodo = getPeriodoActual();
        LocalDate fechaVencimiento = calcularFechaVencimiento(periodo);
        
        // Obtener residentes activos (estado = "ACTIVO" o similar)
        List<Residente> residentesActivos = residenteRepo.listarActivos();
        
        if (residentesActivos.isEmpty()) {
            LOG.warn("No hay residentes activos para generar cuotas");
            return 0;
        }
        
        int cuotasGeneradas = 0;
        
        for (Residente residente : residentesActivos) {
            boolean yaExiste = pagoRepo.existePagoPorResidenteYPeriodo(residente.getId_residente(), periodo);
            
            if (!yaExiste) {
                // Obtener el monto de la cuota (puedes obtenerlo de la unidad o de un valor fijo)
                Double cuotaMensual = obtenerCuotaMensual(residente);
                
                Pago nuevoPago = new Pago();
                nuevoPago.setIdResidente(residente.getId_residente());
                nuevoPago.setTitulo("Cuota condominio - " + periodo);
                nuevoPago.setMontoEsperado(cuotaMensual);
                nuevoPago.setMonto(cuotaMensual);
                nuevoPago.setFecha(LocalDate.now());
                nuevoPago.setFechaVencimiento(fechaVencimiento);
                nuevoPago.setPeriodo(periodo);
                nuevoPago.setEstado(EstadoPago.PENDIENTE);
                nuevoPago.setIntereses(0.0);
                nuevoPago.setFechaCreacion(LocalDateTime.now());
                nuevoPago.setFechaActualizacion(LocalDateTime.now());
                
                pagoRepo.crearPago(nuevoPago);
                cuotasGeneradas++;
            }
        }
        
        LOG.info("Generadas {} cuotas para el período {}", cuotasGeneradas, periodo);
        return cuotasGeneradas;
    }

    // ==================== REGISTRO DE PAGOS ====================

    @Transactional
    public Pago registrarPago(Integer idPago, Double montoRecibido) {
        LOG.info("Registrando pago ID: {} con monto: {}", idPago, montoRecibido);
        
        Pago pago = pagoRepo.buscarPorId(idPago)
                .orElseThrow(() -> new NotFoundException("Pago no encontrado con ID: " + idPago));
        
        if (pago.getEstado() == EstadoPago.PAGADO) {
            throw new IllegalStateException("Este pago ya está registrado como pagado");
        }
        if (montoRecibido == null || montoRecibido <= 0) {
            throw new IllegalArgumentException("El monto recibido debe ser mayor a 0");
        }
        
        LocalDate fechaPago = LocalDate.now();
        pago.setFechaPago(fechaPago);
        pago.setMontoPagado(montoRecibido);
        pago.setFechaActualizacion(LocalDateTime.now());
        
        // Calcular intereses si pagó después del vencimiento
        if (fechaPago.isAfter(pago.getFechaVencimiento())) {
            double interes = calcularInteres(pago.getMontoEsperado(), pago.getFechaVencimiento(), fechaPago);
            pago.setIntereses(interes);
            
            // Verificar si pagó el monto completo (incluyendo intereses)
            double montoTotalEsperado = pago.getMontoEsperado() + interes;
            if (montoRecibido >= montoTotalEsperado) {
                pago.setEstado(EstadoPago.PAGADO);
            } else {
                pago.setEstado(EstadoPago.PAGADO); // Pago parcial
            }
        } else {
            pago.setIntereses(0.0);
            
            if (montoRecibido >= pago.getMontoEsperado()) {
                pago.setEstado(EstadoPago.PAGADO);
            } else {
                pago.setEstado(EstadoPago.PAGADO); // Pago parcial
            }
        }
        
        // Actualizar el último pago del residente
        actualizarUltimoPagoResidente(pago.getIdResidente(), fechaPago);
        
        pagoRepo.actualizarPago(pago);
        LOG.info("Pago registrado exitosamente. Estado: {}", pago.getEstado());
        return pago;
    }

    @Transactional
    public void anularPago(Integer idPago, String motivo) {
        LOG.info("Anulando pago ID: {} - Motivo: {}", idPago, motivo);
        
        Pago pago = pagoRepo.buscarPorId(idPago)
                .orElseThrow(() -> new NotFoundException("Pago no encontrado con ID: " + idPago));
        
        if (pago.getEstado() == EstadoPago.ANULADO) {
            throw new IllegalStateException("Este pago ya está anulado");
        }
        
        pago.setEstado(EstadoPago.ANULADO);
        pago.setObservacion("ANULADO: " + motivo);
        pago.setFechaActualizacion(LocalDateTime.now());
        
        pagoRepo.actualizarPago(pago);
        LOG.info("Pago anulado exitosamente");
    }

    // ==================== FILTROS Y CONSULTAS ====================

    public List<Pago> filtrarPorEstado(EstadoPago estado) {
        LOG.info("Filtrando pagos por estado: {}", estado);
        return pagoRepo.listarPorEstado(estado);
    }

    public List<Pago> filtrarPorEstadoYPeriodo(EstadoPago estado, String periodo) {
        LOG.info("Filtrando pagos por estado: {} y período: {}", estado, periodo);
        return pagoRepo.listarPorEstadoYPeriodo(estado, periodo);
    }

    public List<Pago> filtrarPorPeriodo(String periodo) {
        LOG.info("Filtrando pagos por período: {}", periodo);
        return pagoRepo.listarPorPeriodo(periodo);
    }

    public List<Pago> filtrarPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        LOG.info("Filtrando pagos entre {} y {}", fechaInicio, fechaFin);
        return pagoRepo.listarPorRangoFechas(fechaInicio, fechaFin);
    }

    // ==================== RESIDENTES EN MORA ====================

    /**
     * Obtiene todos los residentes que están en mora (con pagos vencidos)
     */
    public List<ResidenteEnMoraDTO> obtenerResidentesEnMora() {
        LOG.info("Obteniendo residentes en mora");
        
        LocalDate hoy = LocalDate.now();
        
        // Obtener todos los pagos pendientes con fecha de vencimiento anterior a hoy
        List<Pago> pagosVencidos = pagoRepo.listarPorEstadoYFechaVencimientoBefore(
                EstadoPago.PENDIENTE, hoy);
        
        // Agrupar por residente
        Map<Integer, List<Pago>> moraPorResidente = pagosVencidos.stream()
                .collect(Collectors.groupingBy(Pago::getIdResidente));
        
        List<ResidenteEnMoraDTO> resultado = new ArrayList<>();
        
        for (Map.Entry<Integer, List<Pago>> entry : moraPorResidente.entrySet()) {
            Integer idResidente = entry.getKey();
            List<Pago> pagos = entry.getValue();
            
            // Obtener información del residente
            Residente residente = residenteRepo.buscarPorId(idResidente)
                    .orElse(null);
            
            if (residente != null) {
                double totalDeuda = pagos.stream()
                        .mapToDouble(Pago::getMontoEsperado)
                        .sum();
                
                long mesesEnMora = pagos.stream()
                        .map(Pago::getPeriodo)
                        .distinct()
                        .count();
                
                LocalDate fechaPagoMasAntiguo = pagos.stream()
                        .map(Pago::getFechaVencimiento)
                        .min(LocalDate::compareTo)
                        .orElse(null);
                
                ResidenteEnMoraDTO dto = new ResidenteEnMoraDTO();
                dto.setIdResidente(idResidente);
                dto.setNombreCompleto(residente.getNombre() + " " + residente.getApellido());
                dto.setDepartamento(residente.getDepartamento());
                dto.setTotalDeuda(totalDeuda);
                dto.setMesesEnMora(mesesEnMora);
                dto.setFechaPagoMasAntiguo(fechaPagoMasAntiguo);
                dto.setPagos(pagos);
                
                resultado.add(dto);
            }
        }
        
        // Ordenar por deuda mayor a menor
        resultado.sort((a, b) -> Double.compare(b.getTotalDeuda(), a.getTotalDeuda()));
        
        LOG.info("Encontrados {} residentes en mora", resultado.size());
        return resultado;
    }

    /**
     * Verifica si un residente específico está en mora
     */
    public boolean residenteEnMora(Integer idResidente) {
        LOG.info("Verificando si residente {} está en mora", idResidente);
        
        LocalDate hoy = LocalDate.now();
        List<Pago> pagosVencidos = pagoRepo.listarPorResidenteYEstadoYFechaVencimientoBefore(
                idResidente, EstadoPago.PENDIENTE, hoy);
        
        return !pagosVencidos.isEmpty();
    }

    /**
     * Obtiene el detalle de mora de un residente específico
     */
    public DetalleMoraDTO obtenerDetalleMoraResidente(Integer idResidente) {
        LOG.info("Obteniendo detalle de mora para residente: {}", idResidente);
        
        LocalDate hoy = LocalDate.now();
        
        List<Pago> pagosVencidos = pagoRepo.listarPorResidenteYEstadoYFechaVencimientoBefore(
                idResidente, EstadoPago.PENDIENTE, hoy);
        
        List<Pago> pagosAlDia = pagoRepo.listarPorResidenteYEstadoYFechaVencimientoAfterOrEqual(
                idResidente, EstadoPago.PENDIENTE, hoy);
        
        List<Pago> pagosPagados = pagoRepo.listarPorResidenteYEstado(idResidente, EstadoPago.PAGADO);
        
        Residente residente = residenteRepo.buscarPorId(idResidente)
                .orElseThrow(() -> new NotFoundException("Residente no encontrado"));
        
        double totalDeuda = pagosVencidos.stream()
                .mapToDouble(Pago::getMontoEsperado)
                .sum();
        
        DetalleMoraDTO detalle = new DetalleMoraDTO();
        detalle.setIdResidente(idResidente);
        detalle.setNombreCompleto(residente.getNombre() + " " + residente.getApellido());
        detalle.setDepartamento(residente.getDepartamento());
        detalle.setTotalDeuda(totalDeuda);
        detalle.setCantidadCuotasVencidas(pagosVencidos.size());
        detalle.setPagosVencidos(pagosVencidos);
        detalle.setPagosPendientes(pagosAlDia);
        detalle.setPagosPagados(pagosPagados);
        detalle.setEstaEnMora(!pagosVencidos.isEmpty());
        
        return detalle;
    }

    // ==================== REPORTES Y ESTADÍSTICAS ====================

    /**
     * Obtiene el resumen financiero de un período
     */
    public ResumenFinancieroDTO obtenerResumenFinanciero(String periodo) {
        LOG.info("Obteniendo resumen financiero para período: {}", periodo);
        
        List<Pago> pagosPeriodo = pagoRepo.listarPorPeriodo(periodo);
        
        double totalEsperado = pagosPeriodo.stream()
                .mapToDouble(Pago::getMontoEsperado)
                .sum();
        
        double totalPagado = pagosPeriodo.stream()
                .filter(p -> p.getEstado() == EstadoPago.PAGADO)
                .mapToDouble(p -> p.getMontoPagado() != null ? p.getMontoPagado() : 0)
                .sum();
        
        double totalIntereses = pagosPeriodo.stream()
                .filter(p -> p.getEstado() == EstadoPago.PAGADO)
                .mapToDouble(p -> p.getIntereses() != null ? p.getIntereses() : 0)
                .sum();
        
        long totalPagos = pagosPeriodo.size();
        long pagosPagados = pagosPeriodo.stream()
                .filter(p -> p.getEstado() == EstadoPago.PAGADO)
                .count();
        long pagosPendientes = pagosPeriodo.stream()
                .filter(p -> p.getEstado() == EstadoPago.PENDIENTE)
                .count();
        long pagosVencidos = pagosPeriodo.stream()
                .filter(p -> p.getEstado() == EstadoPago.VENCIDO)
                .count();
        
        double porcentajeCobrado = totalEsperado > 0 ? (totalPagado / totalEsperado) * 100 : 0;
        
        ResumenFinancieroDTO resumen = new ResumenFinancieroDTO();
        resumen.setPeriodo(periodo);
        resumen.setTotalEsperado(totalEsperado);
        resumen.setTotalPagado(totalPagado);
        resumen.setTotalIntereses(totalIntereses);
        resumen.setTotalPendiente(totalEsperado - totalPagado);
        resumen.setTotalPagos(totalPagos);
        resumen.setPagosPagados(pagosPagados);
        resumen.setPagosPendientes(pagosPendientes);
        resumen.setPagosVencidos(pagosVencidos);
        resumen.setPorcentajeCobrado(porcentajeCobrado);
        
        return resumen;
    }

    /**
     * Obtiene el histórico de pagos de un residente
     */
    public HistorialPagosDTO obtenerHistorialResidente(Integer idResidente) {
        LOG.info("Obteniendo historial de pagos para residente: {}", idResidente);
        
        List<Pago> todosPagos = pagoRepo.listarPorResidente(idResidente);
        
        // Ordenar por fecha (más reciente primero)
        todosPagos.sort((a, b) -> b.getFecha().compareTo(a.getFecha()));
        
        Residente residente = residenteRepo.buscarPorId(idResidente)
                .orElseThrow(() -> new NotFoundException("Residente no encontrado"));
        
        HistorialPagosDTO historial = new HistorialPagosDTO();
        historial.setIdResidente(idResidente);
        historial.setNombreCompleto(residente.getNombre() + " " + residente.getApellido());
        historial.setDepartamento(residente.getDepartamento());
        historial.setTotalPagado(todosPagos.stream()
                .filter(p -> p.getEstado() == EstadoPago.PAGADO)
                .mapToDouble(p -> p.getMontoPagado() != null ? p.getMontoPagado() : 0)
                .sum());
        historial.setTotalInteresesPagados(todosPagos.stream()
                .filter(p -> p.getEstado() == EstadoPago.PAGADO)
                .mapToDouble(p -> p.getIntereses() != null ? p.getIntereses() : 0)
                .sum());
        historial.setPagos(todosPagos);
        
        return historial;
    }

    /**
     * Obtiene el dashboard financiero completo
     */
    public DashboardFinancieroDTO obtenerDashboardFinanciero() {
        LOG.info("Obteniendo dashboard financiero");
        
        String periodoActual = getPeriodoActual();
        LocalDate hoy = LocalDate.now();
        
        // Resumen del mes actual
        ResumenFinancieroDTO resumenActual = obtenerResumenFinanciero(periodoActual);
        List<ResidenteEnMoraDTO> residentesEnMora = obtenerResidentesEnMora();
        List<Pago> todosPagos = pagoRepo.listarTodos();
        double totalGeneralPagado = todosPagos.stream()
                .filter(p -> p.getEstado() == EstadoPago.PAGADO)
                .mapToDouble(p -> p.getMontoPagado() != null ? p.getMontoPagado() : 0)
                .sum();
        
        double totalGeneralIntereses = todosPagos.stream()
                .filter(p -> p.getEstado() == EstadoPago.PAGADO)
                .mapToDouble(p -> p.getIntereses() != null ? p.getIntereses() : 0)
                .sum();
        
        long totalResidentes = residenteRepo.contarActivos();
        long totalResidentesEnMora = residentesEnMora.size();
        double porcentajeMora = totalResidentes > 0 ? ((double) totalResidentesEnMora / totalResidentes) * 100 : 0;
        
        DashboardFinancieroDTO dashboard = new DashboardFinancieroDTO();
        dashboard.setPeriodoActual(periodoActual);
        dashboard.setResumenMesActual(resumenActual);
        dashboard.setTotalGeneralPagado(totalGeneralPagado);
        dashboard.setTotalGeneralIntereses(totalGeneralIntereses);
        dashboard.setTotalResidentes(totalResidentes);
        dashboard.setTotalResidentesEnMora(totalResidentesEnMora);
        dashboard.setPorcentajeMora(porcentajeMora);
        dashboard.setResidentesEnMora(residentesEnMora);
        
        return dashboard;
    }

    // ==================== MÉTODOS PRIVADOS DE UTILIDAD ====================

    private String getPeriodoActual() {
        return LocalDate.now().format(PERIODO_FORMATTER);
    }

    private LocalDate calcularFechaVencimiento(String periodo) {
        String[] partes = periodo.split("-");
        int anio = Integer.parseInt(partes[0]);
        int mes = Integer.parseInt(partes[1]);
        
        // Crear fecha con el día de vencimiento configurado
        return LocalDate.of(anio, mes, DIA_VENCIMIENTO);
    }

    private double calcularInteres(Double monto, LocalDate fechaVencimiento, LocalDate fechaPago) {
        long diasRetraso = java.time.temporal.ChronoUnit.DAYS.between(fechaVencimiento, fechaPago);
        
        // Interés simple: monto * tasa * (díasRetraso / 30)
        double interes = monto * TASA_INTERES_MORATORIO * (diasRetraso / 30.0);
        
        // Redondear a 2 decimales
        return Math.round(interes * 100.0) / 100.0;
    }

    /**
     * Obtiene la cuota mensual de un residente
     * Puedes modificar esta lógica según tu negocio
     */
    private Double obtenerCuotaMensual(Residente residente) {
        return residente.getCuotaMensual();
    }

    /**
     * Actualiza el campo ultimoPago del residente
     */
    private void actualizarUltimoPagoResidente(Integer idResidente, LocalDate fechaPago) {
        try {
            Residente residente = residenteRepo.buscarPorId(idResidente)
                    .orElse(null);
            if (residente != null) {
                // Actualizar solo si la fecha es más reciente
                if (residente.getUltimoPago() == null || fechaPago.isAfter(residente.getUltimoPago())) {
                    residente.setUltimoPago(fechaPago);
                    residenteRepo.actualizarResidente(residente);
                }
            }
        } catch (Exception e) {
            LOG.error("Error al actualizar último pago del residente: {}", e.getMessage());
        }
    }

    // ==================== DTOs INTERNOS ====================

    public static class ResidenteEnMoraDTO {
        private Integer idResidente;
        private String nombreCompleto;
        private String departamento;
        private Double totalDeuda;
        private Long mesesEnMora;
        private LocalDate fechaPagoMasAntiguo;
        private List<Pago> pagos;

        // Getters y setters
        public Integer getIdResidente() { return idResidente; }
        public void setIdResidente(Integer idResidente) { this.idResidente = idResidente; }
        public String getNombreCompleto() { return nombreCompleto; }
        public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
        public String getDepartamento() { return departamento; }
        public void setDepartamento(String departamento) { this.departamento = departamento; }
        public Double getTotalDeuda() { return totalDeuda; }
        public void setTotalDeuda(Double totalDeuda) { this.totalDeuda = totalDeuda; }
        public Long getMesesEnMora() { return mesesEnMora; }
        public void setMesesEnMora(Long mesesEnMora) { this.mesesEnMora = mesesEnMora; }
        public LocalDate getFechaPagoMasAntiguo() { return fechaPagoMasAntiguo; }
        public void setFechaPagoMasAntiguo(LocalDate fechaPagoMasAntiguo) { this.fechaPagoMasAntiguo = fechaPagoMasAntiguo; }
        public List<Pago> getPagos() { return pagos; }
        public void setPagos(List<Pago> pagos) { this.pagos = pagos; }
    }

    public static class DetalleMoraDTO {
        private Integer idResidente;
        private String nombreCompleto;
        private String departamento;
        private Double totalDeuda;
        private Integer cantidadCuotasVencidas;
        private Boolean estaEnMora;
        private List<Pago> pagosVencidos;
        private List<Pago> pagosPendientes;
        private List<Pago> pagosPagados;

        // Getters y setters
        public Integer getIdResidente() { return idResidente; }
        public void setIdResidente(Integer idResidente) { this.idResidente = idResidente; }
        public String getNombreCompleto() { return nombreCompleto; }
        public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
        public String getDepartamento() { return departamento; }
        public void setDepartamento(String departamento) { this.departamento = departamento; }
        public Double getTotalDeuda() { return totalDeuda; }
        public void setTotalDeuda(Double totalDeuda) { this.totalDeuda = totalDeuda; }
        public Integer getCantidadCuotasVencidas() { return cantidadCuotasVencidas; }
        public void setCantidadCuotasVencidas(Integer cantidadCuotasVencidas) { this.cantidadCuotasVencidas = cantidadCuotasVencidas; }
        public Boolean getEstaEnMora() { return estaEnMora; }
        public void setEstaEnMora(Boolean estaEnMora) { this.estaEnMora = estaEnMora; }
        public List<Pago> getPagosVencidos() { return pagosVencidos; }
        public void setPagosVencidos(List<Pago> pagosVencidos) { this.pagosVencidos = pagosVencidos; }
        public List<Pago> getPagosPendientes() { return pagosPendientes; }
        public void setPagosPendientes(List<Pago> pagosPendientes) { this.pagosPendientes = pagosPendientes; }
        public List<Pago> getPagosPagados() { return pagosPagados; }
        public void setPagosPagados(List<Pago> pagosPagados) { this.pagosPagados = pagosPagados; }
    }

    public static class ResumenFinancieroDTO {
        private String periodo;
        private Double totalEsperado;
        private Double totalPagado;
        private Double totalIntereses;
        private Double totalPendiente;
        private Long totalPagos;
        private Long pagosPagados;
        private Long pagosPendientes;
        private Long pagosVencidos;
        private Double porcentajeCobrado;

        // Getters y setters
        public String getPeriodo() { return periodo; }
        public void setPeriodo(String periodo) { this.periodo = periodo; }
        public Double getTotalEsperado() { return totalEsperado; }
        public void setTotalEsperado(Double totalEsperado) { this.totalEsperado = totalEsperado; }
        public Double getTotalPagado() { return totalPagado; }
        public void setTotalPagado(Double totalPagado) { this.totalPagado = totalPagado; }
        public Double getTotalIntereses() { return totalIntereses; }
        public void setTotalIntereses(Double totalIntereses) { this.totalIntereses = totalIntereses; }
        public Double getTotalPendiente() { return totalPendiente; }
        public void setTotalPendiente(Double totalPendiente) { this.totalPendiente = totalPendiente; }
        public Long getTotalPagos() { return totalPagos; }
        public void setTotalPagos(Long totalPagos) { this.totalPagos = totalPagos; }
        public Long getPagosPagados() { return pagosPagados; }
        public void setPagosPagados(Long pagosPagados) { this.pagosPagados = pagosPagados; }
        public Long getPagosPendientes() { return pagosPendientes; }
        public void setPagosPendientes(Long pagosPendientes) { this.pagosPendientes = pagosPendientes; }
        public Long getPagosVencidos() { return pagosVencidos; }
        public void setPagosVencidos(Long pagosVencidos) { this.pagosVencidos = pagosVencidos; }
        public Double getPorcentajeCobrado() { return porcentajeCobrado; }
        public void setPorcentajeCobrado(Double porcentajeCobrado) { this.porcentajeCobrado = porcentajeCobrado; }
    }

    public static class HistorialPagosDTO {
        private Integer idResidente;
        private String nombreCompleto;
        private String departamento;
        private Double totalPagado;
        private Double totalInteresesPagados;
        private List<Pago> pagos;

        // Getters y setters
        public Integer getIdResidente() { return idResidente; }
        public void setIdResidente(Integer idResidente) { this.idResidente = idResidente; }
        public String getNombreCompleto() { return nombreCompleto; }
        public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
        public String getDepartamento() { return departamento; }
        public void setDepartamento(String departamento) { this.departamento = departamento; }
        public Double getTotalPagado() { return totalPagado; }
        public void setTotalPagado(Double totalPagado) { this.totalPagado = totalPagado; }
        public Double getTotalInteresesPagados() { return totalInteresesPagados; }
        public void setTotalInteresesPagados(Double totalInteresesPagados) { this.totalInteresesPagados = totalInteresesPagados; }
        public List<Pago> getPagos() { return pagos; }
        public void setPagos(List<Pago> pagos) { this.pagos = pagos; }
    }

    public static class DashboardFinancieroDTO {
        private String periodoActual;
        private ResumenFinancieroDTO resumenMesActual;
        private Double totalGeneralPagado;
        private Double totalGeneralIntereses;
        private Long totalResidentes;
        private Long totalResidentesEnMora;
        private Double porcentajeMora;
        private List<ResidenteEnMoraDTO> residentesEnMora;

        // Getters y setters
        public String getPeriodoActual() { return periodoActual; }
        public void setPeriodoActual(String periodoActual) { this.periodoActual = periodoActual; }
        public ResumenFinancieroDTO getResumenMesActual() { return resumenMesActual; }
        public void setResumenMesActual(ResumenFinancieroDTO resumenMesActual) { this.resumenMesActual = resumenMesActual; }
        public Double getTotalGeneralPagado() { return totalGeneralPagado; }
        public void setTotalGeneralPagado(Double totalGeneralPagado) { this.totalGeneralPagado = totalGeneralPagado; }
        public Double getTotalGeneralIntereses() { return totalGeneralIntereses; }
        public void setTotalGeneralIntereses(Double totalGeneralIntereses) { this.totalGeneralIntereses = totalGeneralIntereses; }
        public Long getTotalResidentes() { return totalResidentes; }
        public void setTotalResidentes(Long totalResidentes) { this.totalResidentes = totalResidentes; }
        public Long getTotalResidentesEnMora() { return totalResidentesEnMora; }
        public void setTotalResidentesEnMora(Long totalResidentesEnMora) { this.totalResidentesEnMora = totalResidentesEnMora; }
        public Double getPorcentajeMora() { return porcentajeMora; }
        public void setPorcentajeMora(Double porcentajeMora) { this.porcentajeMora = porcentajeMora; }
        public List<ResidenteEnMoraDTO> getResidentesEnMora() { return residentesEnMora; }
        public void setResidentesEnMora(List<ResidenteEnMoraDTO> residentesEnMora) { this.residentesEnMora = residentesEnMora; }
    }
}