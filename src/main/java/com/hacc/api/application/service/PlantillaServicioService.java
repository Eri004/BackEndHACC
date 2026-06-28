package com.hacc.api.application.service;

import com.hacc.api.domain.model.PlantillaServicio;
import com.hacc.api.domain.model.Propietario;
import com.hacc.api.domain.model.ServicioProveedor;
import com.hacc.api.domain.repository.IPlantillaServicioRepo;
import com.hacc.api.domain.repository.IPropietarioRepo;
import com.hacc.api.domain.repository.IServicioProveedorRepo;
import com.hacc.api.domain.enums.NombreServicio;
import com.hacc.api.domain.enums.EstadoServicio;
import com.hacc.api.domain.enums.FrecuenciaServicio;

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
public class PlantillaServicioService {

    private static final Logger LOG = LoggerFactory.getLogger(PlantillaServicioService.class);
    private static final DateTimeFormatter PERIODO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Inject
    IPlantillaServicioRepo plantillaRepo;

    @Inject
    IPropietarioRepo propietarioRepo;

    @Inject
    IServicioProveedorRepo servicioRepo;

    // ==================== CRUD BÁSICO ====================

    @Transactional
    public PlantillaServicio crear(PlantillaServicio plantilla, Integer idPropietario) {
        LOG.info("Creando nueva plantilla para propietario: {}", idPropietario);

        Propietario propietario = propietarioRepo.buscarPorId(idPropietario)
                .orElseThrow(() -> new NotFoundException("Propietario no encontrado con ID: " + idPropietario));

        // Validaciones
        if (plantilla.getNombre() == null) {
            throw new IllegalArgumentException("El nombre del servicio es obligatorio");
        }
        if (plantilla.getMontoBase() == null || plantilla.getMontoBase().doubleValue() <= 0) {
            throw new IllegalArgumentException("El monto base debe ser mayor a 0");
        }
        if (plantilla.getFrecuencia() == null) {
            throw new IllegalArgumentException("La frecuencia es obligatoria");
        }
        if (plantilla.getDiaVencimiento() == null || plantilla.getDiaVencimiento() < 1 || plantilla.getDiaVencimiento() > 31) {
            throw new IllegalArgumentException("El día de vencimiento debe estar entre 1 y 31");
        }

        // Validar que no exista otra plantilla con el mismo nombre para el mismo propietario
        List<PlantillaServicio> existentes = plantillaRepo.listarPorPropietarioYNombre(
                idPropietario, plantilla.getNombre());
        if (!existentes.isEmpty()) {
            throw new IllegalArgumentException("Ya existe una plantilla para el servicio " + 
                    plantilla.getNombre() + " en este propietario");
        }

        // Asignar valores por defecto
        if (plantilla.getActivo() == null) {
            plantilla.setActivo(true);
        }

        // Redondear monto
        if (plantilla.getMontoBase() != null) {
            plantilla.setMontoBase(plantilla.getMontoBase().setScale(2, RoundingMode.HALF_UP));
        }

        // Asignar propietario
        plantilla.setPropietario(propietario);
        plantilla.setCreadoEn(LocalDateTime.now());
        plantilla.setActualizadoEn(LocalDateTime.now());

        plantillaRepo.crear(plantilla);
        LOG.info("Plantilla creada exitosamente con ID: {}", plantilla.getIdPlantilla());
        return plantilla;
    }

    @Transactional
    public PlantillaServicio actualizar(PlantillaServicio plantilla) {
        LOG.info("Actualizando plantilla ID: {}", plantilla.getIdPlantilla());

        PlantillaServicio existente = plantillaRepo.buscarPorId(plantilla.getIdPlantilla())
                .orElseThrow(() -> new NotFoundException("Plantilla no encontrada con ID: " + plantilla.getIdPlantilla()));

        // Actualizar solo campos permitidos
        if (plantilla.getNombre() != null) existente.setNombre(plantilla.getNombre());
        if (plantilla.getDescripcion() != null) existente.setDescripcion(plantilla.getDescripcion());
        if (plantilla.getMontoBase() != null) {
            existente.setMontoBase(plantilla.getMontoBase().setScale(2, RoundingMode.HALF_UP));
        }
        if (plantilla.getFrecuencia() != null) existente.setFrecuencia(plantilla.getFrecuencia());
        if (plantilla.getDiaVencimiento() != null) existente.setDiaVencimiento(plantilla.getDiaVencimiento());
        if (plantilla.getMesInicio() != null) existente.setMesInicio(plantilla.getMesInicio());
        if (plantilla.getFechaFin() != null) existente.setFechaFin(plantilla.getFechaFin());
        if (plantilla.getActivo() != null) existente.setActivo(plantilla.getActivo());

        existente.setActualizadoEn(LocalDateTime.now());

        plantillaRepo.actualizar(existente);
        LOG.info("Plantilla actualizada exitosamente");
        return existente;
    }

    @Transactional
    public void eliminar(Long idPlantilla) {
        LOG.info("Eliminando plantilla ID: {}", idPlantilla);

        PlantillaServicio plantilla = plantillaRepo.buscarPorId(idPlantilla)
                .orElseThrow(() -> new NotFoundException("Plantilla no encontrada con ID: " + idPlantilla));

        plantillaRepo.eliminar(idPlantilla);
        LOG.info("Plantilla eliminada exitosamente");
    }

    public Optional<PlantillaServicio> buscarPorId(Long idPlantilla) {
        return plantillaRepo.buscarPorId(idPlantilla);
    }

    public List<PlantillaServicio> listarTodos() {
        return plantillaRepo.listarTodos();
    }

    public List<PlantillaServicio> listarPorPropietario(Integer idPropietario) {
        propietarioRepo.buscarPorId(idPropietario)
                .orElseThrow(() -> new NotFoundException("Propietario no encontrado con ID: " + idPropietario));
        return plantillaRepo.listarPorPropietario(idPropietario);
    }

    public List<PlantillaServicio> listarPlantillasActivas(Integer idPropietario) {
        propietarioRepo.buscarPorId(idPropietario)
                .orElseThrow(() -> new NotFoundException("Propietario no encontrado con ID: " + idPropietario));
        return plantillaRepo.listarPorPropietarioYActivo(idPropietario, true);
    }

    // ==================== GENERACIÓN AUTOMÁTICA DE SERVICIOS ====================

    /**
     * Genera servicios automáticamente a partir de las plantillas activas
     * Este método debe ejecutarse mediante un cron job (ej. diariamente a las 00:00)
     */
    @Transactional
    public int generarServiciosDesdePlantillas() {
        LOG.info("Generando servicios automáticos desde plantillas...");

        List<PlantillaServicio> plantillas = plantillaRepo.listarActivasParaGenerar();
        int serviciosGenerados = 0;
        String mesActual = getPeriodoActual();

        if (plantillas.isEmpty()) {
            LOG.info("No hay plantillas activas para generar servicios");
            return 0;
        }

        LOG.info("Encontradas {} plantillas activas para procesar", plantillas.size());

        for (PlantillaServicio plantilla : plantillas) {
            try {
                // Verificar si ya existe un servicio para este mes y plantilla
                boolean yaExiste = servicioRepo.existeServicioPorPropietarioMesYNombre(
                        plantilla.getIdPropietario(),
                        mesActual,
                        plantilla.getNombre()
                );

                if (!yaExiste) {
                    // Verificar si corresponde generar según la frecuencia
                    if (correspondeGenerar(plantilla, mesActual)) {
                        ServicioProveedor nuevoServicio = new ServicioProveedor();
                        nuevoServicio.setNombre(plantilla.getNombre());
                        nuevoServicio.setMes(mesActual);
                        nuevoServicio.setMontoFacturado(plantilla.getMontoBase());
                        nuevoServicio.setMontoPagado(BigDecimal.ZERO);
                        nuevoServicio.setEstado(EstadoServicio.PENDIENTE);
                        nuevoServicio.setFechaVencimiento(
                                calcularFechaVencimiento(mesActual, plantilla.getDiaVencimiento())
                        );
                        nuevoServicio.setPropietario(plantilla.getPropietario());
                        nuevoServicio.setCreadoEn(LocalDateTime.now());
                        nuevoServicio.setActualizadoEn(LocalDateTime.now());

                        servicioRepo.crear(nuevoServicio);
                        serviciosGenerados++;
                        LOG.info("Servicio generado: {} - {} para propietario {}",
                                plantilla.getNombre(), mesActual, plantilla.getIdPropietario());
                    }
                } else {
                    LOG.debug("Ya existe servicio para {} en mes {} para propietario {}",
                            plantilla.getNombre(), mesActual, plantilla.getIdPropietario());
                }
            } catch (Exception e) {
                LOG.error("Error al generar servicio para plantilla {}: {}",
                        plantilla.getIdPlantilla(), e.getMessage());
            }
        }

        LOG.info("Generados {} servicios automáticos", serviciosGenerados);
        return serviciosGenerados;
    }

    /**
     * Genera servicios para un mes específico (útil para probar o generar retroactivamente)
     */
    @Transactional
    public int generarServiciosParaMes(Integer idPropietario, String mes) {
        LOG.info("Generando servicios para propietario: {} mes: {}", idPropietario, mes);

        propietarioRepo.buscarPorId(idPropietario)
                .orElseThrow(() -> new NotFoundException("Propietario no encontrado con ID: " + idPropietario));

        List<PlantillaServicio> plantillas = plantillaRepo.listarPorPropietarioYActivo(idPropietario, true);
        int serviciosGenerados = 0;

        for (PlantillaServicio plantilla : plantillas) {
            boolean yaExiste = servicioRepo.existeServicioPorPropietarioMesYNombre(
                    idPropietario, mes, plantilla.getNombre()
            );

            if (!yaExiste && correspondeGenerar(plantilla, mes)) {
                ServicioProveedor nuevoServicio = new ServicioProveedor();
                nuevoServicio.setNombre(plantilla.getNombre());
                nuevoServicio.setMes(mes);
                nuevoServicio.setMontoFacturado(plantilla.getMontoBase());
                nuevoServicio.setMontoPagado(BigDecimal.ZERO);
                nuevoServicio.setEstado(EstadoServicio.PENDIENTE);
                nuevoServicio.setFechaVencimiento(
                        calcularFechaVencimiento(mes, plantilla.getDiaVencimiento())
                );
                nuevoServicio.setPropietario(plantilla.getPropietario());
                nuevoServicio.setCreadoEn(LocalDateTime.now());
                nuevoServicio.setActualizadoEn(LocalDateTime.now());

                servicioRepo.crear(nuevoServicio);
                serviciosGenerados++;
                LOG.info("Servicio generado: {} - {} para propietario {}",
                        plantilla.getNombre(), mes, idPropietario);
            }
        }

        LOG.info("Generados {} servicios para el mes {}", serviciosGenerados, mes);
        return serviciosGenerados;
    }

    /**
     * Verifica si corresponde generar un servicio según la frecuencia
     */
    private boolean correspondeGenerar(PlantillaServicio plantilla, String mesActual) {
        // Si tiene fecha de fin y ya pasó, no generar
        if (plantilla.getFechaFin() != null) {
            LocalDate fechaFin = plantilla.getFechaFin();
            LocalDate fechaActual = LocalDate.parse(mesActual + "-01");
            if (fechaActual.isAfter(fechaFin)) {
                return false;
            }
        }

        // Si no tiene mes de inicio, asumir que empieza desde ahora
        if (plantilla.getMesInicio() == null) {
            return true;
        }

        LocalDate fechaInicio = LocalDate.parse(plantilla.getMesInicio() + "-01");
        LocalDate fechaActual = LocalDate.parse(mesActual + "-01");

        // Si la fecha actual es anterior a la fecha de inicio, no generar
        if (fechaActual.isBefore(fechaInicio)) {
            return false;
        }

        // Calcular meses de diferencia
        int mesesDiferencia = (fechaActual.getYear() - fechaInicio.getYear()) * 12
                + (fechaActual.getMonthValue() - fechaInicio.getMonthValue());

        switch (plantilla.getFrecuencia()) {
            case MENSUAL:
                return true; // Cada mes
            case BIMENSUAL:
                return mesesDiferencia % 2 == 0;
            case TRIMESTRAL:
                return mesesDiferencia % 3 == 0;
            case SEMESTRAL:
                return mesesDiferencia % 6 == 0;
            case ANUAL:
                return mesesDiferencia % 12 == 0;
            default:
                return true;
        }
    }

    /**
     * Calcula la fecha de vencimiento para un mes y día específico
     */
    private LocalDate calcularFechaVencimiento(String mes, Integer dia) {
        String[] partes = mes.split("-");
        int anio = Integer.parseInt(partes[0]);
        int mesNum = Integer.parseInt(partes[1]);

        // Ajustar día si es mayor al máximo del mes
        int maxDia = LocalDate.of(anio, mesNum, 1).lengthOfMonth();
        int diaVenc = Math.min(dia, maxDia);

        return LocalDate.of(anio, mesNum, diaVenc);
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private String getPeriodoActual() {
        return LocalDate.now().format(PERIODO_FORMATTER);
    }

    // ==================== REPORTES ====================

    /**
     * Obtiene un resumen de las plantillas de un propietario
     */
    public ResumenPlantillasDTO obtenerResumenPlantillas(Integer idPropietario) {
        LOG.info("Obteniendo resumen de plantillas para propietario: {}", idPropietario);

        propietarioRepo.buscarPorId(idPropietario)
                .orElseThrow(() -> new NotFoundException("Propietario no encontrado con ID: " + idPropietario));

        List<PlantillaServicio> plantillas = plantillaRepo.listarPorPropietario(idPropietario);

        long totalPlantillas = plantillas.size();
        long activas = plantillas.stream().filter(PlantillaServicio::getActivo).count();
        long inactivas = totalPlantillas - activas;

        // Agrupar por frecuencia
        long mensuales = plantillas.stream()
                .filter(p -> p.getFrecuencia() == FrecuenciaServicio.MENSUAL)
                .count();
        long trimestrales = plantillas.stream()
                .filter(p -> p.getFrecuencia() == FrecuenciaServicio.TRIMESTRAL)
                .count();
        long semestrales = plantillas.stream()
                .filter(p -> p.getFrecuencia() == FrecuenciaServicio.SEMESTRAL)
                .count();
        long anuales = plantillas.stream()
                .filter(p -> p.getFrecuencia() == FrecuenciaServicio.ANUAL)
                .count();

        // Suma de montos mensuales estimados
        BigDecimal totalMensualEstimado = plantillas.stream()
                .filter(PlantillaServicio::getActivo)
                .filter(p -> p.getFrecuencia() == FrecuenciaServicio.MENSUAL)
                .map(PlantillaServicio::getMontoBase)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        ResumenPlantillasDTO resumen = new ResumenPlantillasDTO();
        resumen.setIdPropietario(idPropietario);
        resumen.setTotalPlantillas(totalPlantillas);
        resumen.setActivas(activas);
        resumen.setInactivas(inactivas);
        resumen.setMensuales(mensuales);
        resumen.setTrimestrales(trimestrales);
        resumen.setSemestrales(semestrales);
        resumen.setAnuales(anuales);
        resumen.setTotalMensualEstimado(totalMensualEstimado.doubleValue());
        resumen.setPlantillas(plantillas);

        return resumen;
    }

    // ==================== DTOS ====================

    public static class ResumenPlantillasDTO {
        private Integer idPropietario;
        private Long totalPlantillas;
        private Long activas;
        private Long inactivas;
        private Long mensuales;
        private Long trimestrales;
        private Long semestrales;
        private Long anuales;
        private Double totalMensualEstimado;
        private List<PlantillaServicio> plantillas;

        // Getters y Setters
        public Integer getIdPropietario() { return idPropietario; }
        public void setIdPropietario(Integer idPropietario) { this.idPropietario = idPropietario; }
        public Long getTotalPlantillas() { return totalPlantillas; }
        public void setTotalPlantillas(Long totalPlantillas) { this.totalPlantillas = totalPlantillas; }
        public Long getActivas() { return activas; }
        public void setActivas(Long activas) { this.activas = activas; }
        public Long getInactivas() { return inactivas; }
        public void setInactivas(Long inactivas) { this.inactivas = inactivas; }
        public Long getMensuales() { return mensuales; }
        public void setMensuales(Long mensuales) { this.mensuales = mensuales; }
        public Long getTrimestrales() { return trimestrales; }
        public void setTrimestrales(Long trimestrales) { this.trimestrales = trimestrales; }
        public Long getSemestrales() { return semestrales; }
        public void setSemestrales(Long semestrales) { this.semestrales = semestrales; }
        public Long getAnuales() { return anuales; }
        public void setAnuales(Long anuales) { this.anuales = anuales; }
        public Double getTotalMensualEstimado() { return totalMensualEstimado; }
        public void setTotalMensualEstimado(Double totalMensualEstimado) { this.totalMensualEstimado = totalMensualEstimado; }
        public List<PlantillaServicio> getPlantillas() { return plantillas; }
        public void setPlantillas(List<PlantillaServicio> plantillas) { this.plantillas = plantillas; }
    }
}