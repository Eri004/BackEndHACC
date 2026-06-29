package com.hacc.api.application.service;

import com.hacc.api.domain.enums.EstadoUnidad;
import com.hacc.api.domain.model.Edificio;
import com.hacc.api.domain.model.Propietario;
import com.hacc.api.domain.model.Unidad;
import com.hacc.api.domain.repository.IEdificioRepo;
import com.hacc.api.domain.repository.IPropietarioRepo;
import com.hacc.api.domain.repository.IUnidadRepo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ForbiddenException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class EdificioService {

    private static final Logger LOG = LoggerFactory.getLogger(EdificioService.class);

    @Inject
    private IEdificioRepo edificioRepo;

    @Inject
    private IPropietarioRepo propietarioRepo;

    @Inject
    private IUnidadRepo unidadRepo;

    // ==================== CRUD BÁSICO ====================

    @Transactional
    public Edificio registrarEdificio(Edificio edificio, Integer propietarioId) {
        LOG.info("Registrando nuevo edificio para propietario ID: {}", propietarioId);

        Propietario propietario = propietarioRepo.buscarPorId(propietarioId)
                .orElseThrow(() -> new NotFoundException("Propietario no encontrado con ID: " + propietarioId));

        validarCamposObligatorios(edificio);

        if (edificioRepo.existePorNombre(edificio.getNombre(), propietarioId)) {
            throw new IllegalStateException("Ya existe un edificio con el nombre '" + edificio.getNombre() +
                    "' para este propietario");
        }

        edificio.setPropietario(propietario);
        edificio.setActivo(true);

        edificioRepo.crearEdificio(edificio);
        LOG.info("Edificio creado exitosamente con ID: {}", edificio.getIdEdificio());
        return edificio;
    }

    @Transactional
    public Edificio registrarEdificioConUnidades(Edificio edificio, Integer propietarioId,
            List<Unidad> unidades) {
        LOG.info("Registrando edificio con {} unidades para propietario ID: {}",
                unidades != null ? unidades.size() : 0, propietarioId);

        Edificio edificioGuardado = registrarEdificio(edificio, propietarioId);

        if (unidades != null && !unidades.isEmpty()) {
            for (Unidad unidad : unidades) {
                unidad.setEdificio(edificioGuardado);
                unidad.setPropietario(edificioGuardado.getPropietario());
                edificioGuardado.agregarUnidad(unidad);
            }
        }
        edificioGuardado
                .setTotalUnidades(edificioGuardado.getUnidades() != null ? edificioGuardado.getUnidades().size() : 0);
        edificioRepo.actualizarEdificio(edificioGuardado);

        LOG.info("Edificio creado con {} unidades", edificioGuardado.getTotalUnidades());
        return edificioGuardado;
    }

    public Optional<Edificio> buscarPorId(Long id) {
        LOG.debug("Buscando edificio por ID: {}", id);
        return edificioRepo.buscarPorId(id);
    }

    public Edificio obtener(Long id) {
        LOG.debug("Obteniendo edificio por ID: {}", id);
        return edificioRepo.buscarPorId(id)
                .orElseThrow(() -> new NotFoundException("Edificio no encontrado con ID: " + id));
    }

    public List<Edificio> listarPorPropietario(Integer propietarioId) {
        LOG.info("Listando edificios para propietario ID: {}", propietarioId);

        propietarioRepo.buscarPorId(propietarioId)
                .orElseThrow(() -> new NotFoundException("Propietario no encontrado con ID: " + propietarioId));

        return edificioRepo.listarPorPropietario(propietarioId);
    }

    public List<Edificio> listarTodos() {
        LOG.debug("Listando todos los edificios");
        return edificioRepo.listarTodos();
    }

    @Transactional
    public Edificio actualizar(Long id, Edificio edificioActualizado) {
        LOG.info("Actualizando edificio ID: {}", id);

        Edificio edificioExistente = edificioRepo.buscarPorId(id)
                .orElseThrow(() -> new NotFoundException("Edificio no encontrado con ID: " + id));

        // Validar nombre único
        if (edificioActualizado.getNombre() != null &&
                !edificioExistente.getNombre().equals(edificioActualizado.getNombre()) &&
                edificioRepo.existePorNombre(edificioActualizado.getNombre(),
                        edificioExistente.getPropietario().getId_propietario())) {
            throw new IllegalStateException("Ya existe otro edificio con el nombre '" +
                    edificioActualizado.getNombre() + "' para este propietario");
        }

        // Actualizar campos permitidos
        if (edificioActualizado.getNombre() != null && !edificioActualizado.getNombre().isEmpty()) {
            edificioExistente.setNombre(edificioActualizado.getNombre());
        }
        if (edificioActualizado.getDireccion() != null) {
            edificioExistente.setDireccion(edificioActualizado.getDireccion());
        }
        if (edificioActualizado.getActivo() != null) {
            edificioExistente.setActivo(edificioActualizado.getActivo());
        }

        edificioRepo.actualizarEdificio(edificioExistente);
        LOG.info("Edificio actualizado exitosamente");
        return edificioExistente;
    }

    @Transactional
    public void eliminar(Long id) {
        LOG.info("Eliminando edificio ID: {}", id);

        Edificio edificio = edificioRepo.buscarPorId(id)
                .orElseThrow(() -> new NotFoundException("Edificio no encontrado con ID: " + id));

        if (edificio.getUnidades() != null && !edificio.getUnidades().isEmpty()) {
            throw new IllegalStateException("No se puede eliminar el edificio porque tiene " +
                    edificio.getUnidades().size() + " unidades asociadas");
        }

        edificioRepo.eliminarEdificio(id);
        LOG.info("Edificio eliminado exitosamente");
    }

    // ==================== MÉTODOS PARA FRONTEND ====================

    /**
     * Obtiene edificios de un propietario por email
     * Útil para el frontend cuando el usuario está autenticado
     */
    public List<Edificio> obtenerEdificiosPorEmailPropietario(String email) {
        LOG.info("Obteniendo edificios para propietario con email: {}", email);

        Propietario propietario = propietarioRepo.buscarPorEmail(email).get();

        return edificioRepo.listarPorPropietario(propietario.getId_propietario());
    }

    /**
     * Obtiene un edificio verificando que pertenezca al propietario
     */
    public Edificio obtenerEdificioSeguro(Long idEdificio, Integer idPropietario) {
        LOG.debug("Verificando edificio {} para propietario {}", idEdificio, idPropietario);

        Edificio edificio = edificioRepo.buscarPorId(idEdificio)
                .orElseThrow(() -> new NotFoundException("Edificio no encontrado con ID: " + idEdificio));

        if (!edificio.getPropietario().getId_propietario().equals(idPropietario)) {
            throw new ForbiddenException("No tienes permiso para acceder a este edificio");
        }

        return edificio;
    }

    /**
     * Obtiene todas las unidades de un edificio verificando pertenencia
     */
    public Set<Unidad> obtenerUnidadesDeEdificio(Long idEdificio, Integer idPropietario) {
        LOG.info("Obteniendo unidades del edificio {} para propietario {}", idEdificio, idPropietario);

        // Verificar que el edificio pertenece al propietario
        Edificio edificio = obtenerEdificioSeguro(idEdificio, idPropietario);

        return edificio.getUnidades();
    }

    /**
     * Obtiene el resumen de edificios de un propietario
     */
    public ResumenEdificiosDTO obtenerResumenEdificios(Integer idPropietario) {
        LOG.info("Obteniendo resumen de edificios para propietario: {}", idPropietario);

        propietarioRepo.buscarPorId(idPropietario)
                .orElseThrow(() -> new NotFoundException("Propietario no encontrado con ID: " + idPropietario));

        List<Edificio> edificios = edificioRepo.listarPorPropietario(idPropietario);

        long totalEdificios = edificios.size();
        long totalUnidades = edificios.stream()
                .mapToLong(e -> e.getUnidades() != null ? e.getUnidades().size() : 0)
                .sum();

        // Contar unidades ocupadas vs disponibles (asumiendo que "OCUPADO" es el
        // estado)
        long unidadesOcupadas = edificios.stream()
                .flatMap(e -> e.getUnidades().stream())
                .filter(u -> u.getEstado() == EstadoUnidad.OCUPADO)
                .count();

        long unidadesDisponibles = totalUnidades - unidadesOcupadas;

        ResumenEdificiosDTO resumen = new ResumenEdificiosDTO();
        resumen.setIdPropietario(idPropietario);
        resumen.setTotalEdificios(totalEdificios);
        resumen.setTotalUnidades(totalUnidades);
        resumen.setUnidadesOcupadas(unidadesOcupadas);
        resumen.setUnidadesDisponibles(unidadesDisponibles);
        resumen.setEdificios(edificios);

        return resumen;
    }

    /**
     * Obtiene resumen para el dashboard del propietario
     */
    public DashboardEdificiosDTO obtenerDashboardEdificios(Integer idPropietario) {
        LOG.info("Obteniendo dashboard de edificios para propietario: {}", idPropietario);

        propietarioRepo.buscarPorId(idPropietario)
                .orElseThrow(() -> new NotFoundException("Propietario no encontrado con ID: " + idPropietario));

        List<Edificio> edificios = edificioRepo.listarPorPropietario(idPropietario);

        DashboardEdificiosDTO dashboard = new DashboardEdificiosDTO();

        // Edificios más recientes (últimos 5 - usando ID como proxy de fecha)
        List<Edificio> ultimosEdificios = edificios.stream()
                .sorted((e1, e2) -> e2.getIdEdificio().compareTo(e1.getIdEdificio()))
                .limit(5)
                .collect(Collectors.toList());

        // Unidades por edificio
        List<UnidadesPorEdificioDTO> unidadesPorEdificio = edificios.stream()
                .map(e -> {
                    UnidadesPorEdificioDTO dto = new UnidadesPorEdificioDTO();
                    dto.setIdEdificio(e.getIdEdificio());
                    dto.setNombreEdificio(e.getNombre());
                    dto.setTotalUnidades(e.getUnidades() != null ? e.getUnidades().size() : 0);
                    dto.setUnidadesOcupadas(e.getUnidades() != null
                            ? e.getUnidades().stream().filter(u -> u.getEstado() == EstadoUnidad.OCUPADO).count()
                            : 0);
                    return dto;
                })
                .collect(Collectors.toList());

        dashboard.setTotalEdificios((long) edificios.size());
        dashboard.setTotalUnidades(edificios.stream()
                .mapToLong(e -> e.getUnidades() != null ? e.getUnidades().size() : 0)
                .sum());
        dashboard.setUltimosEdificios(ultimosEdificios);
        dashboard.setUnidadesPorEdificio(unidadesPorEdificio);

        return dashboard;
    }

    /**
     * Obtiene edificios activos de un propietario
     */
    public List<Edificio> obtenerEdificiosActivos(Integer idPropietario) {
        LOG.info("Obteniendo edificios activos para propietario: {}", idPropietario);

        propietarioRepo.buscarPorId(idPropietario)
                .orElseThrow(() -> new NotFoundException("Propietario no encontrado con ID: " + idPropietario));

        return edificioRepo.listarPorPropietario(idPropietario).stream()
                .filter(e -> e.getActivo() != null && e.getActivo())
                .collect(Collectors.toList());
    }

    // ==================== MÉTODOS DE VALIDACIÓN ====================

    private void validarCamposObligatorios(Edificio edificio) {
        if (edificio.getNombre() == null || edificio.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del edificio es obligatorio");
        }
        if (edificio.getDireccion() == null || edificio.getDireccion().trim().isEmpty()) {
            throw new IllegalArgumentException("La dirección del edificio es obligatoria");
        }
    }

    /**
     * Verifica si un edificio pertenece a un propietario
     */
    public boolean verificarPertenencia(Long idEdificio, Integer idPropietario) {
        return edificioRepo.buscarPorId(idEdificio)
                .map(Edificio::getPropietario)
                .map(Propietario::getId_propietario)
                .map(id -> id.longValue() == idPropietario.longValue())
                .orElse(false);
    }
    // ==================== DTOS ====================

    public static class ResumenEdificiosDTO {
        private Integer idPropietario;
        private Long totalEdificios;
        private Long totalUnidades;
        private Long unidadesOcupadas;
        private Long unidadesDisponibles;
        private List<Edificio> edificios;

        // Getters y Setters
        public Integer getIdPropietario() {
            return idPropietario;
        }

        public void setIdPropietario(Integer idPropietario) {
            this.idPropietario = idPropietario;
        }

        public Long getTotalEdificios() {
            return totalEdificios;
        }

        public void setTotalEdificios(Long totalEdificios) {
            this.totalEdificios = totalEdificios;
        }

        public Long getTotalUnidades() {
            return totalUnidades;
        }

        public void setTotalUnidades(Long totalUnidades) {
            this.totalUnidades = totalUnidades;
        }

        public Long getUnidadesOcupadas() {
            return unidadesOcupadas;
        }

        public void setUnidadesOcupadas(Long unidadesOcupadas) {
            this.unidadesOcupadas = unidadesOcupadas;
        }

        public Long getUnidadesDisponibles() {
            return unidadesDisponibles;
        }

        public void setUnidadesDisponibles(Long unidadesDisponibles) {
            this.unidadesDisponibles = unidadesDisponibles;
        }

        public List<Edificio> getEdificios() {
            return edificios;
        }

        public void setEdificios(List<Edificio> edificios) {
            this.edificios = edificios;
        }
    }

    public static class DashboardEdificiosDTO {
        private Long totalEdificios;
        private Long totalUnidades;
        private List<Edificio> ultimosEdificios;
        private List<UnidadesPorEdificioDTO> unidadesPorEdificio;

        // Getters y Setters
        public Long getTotalEdificios() {
            return totalEdificios;
        }

        public void setTotalEdificios(Long totalEdificios) {
            this.totalEdificios = totalEdificios;
        }

        public Long getTotalUnidades() {
            return totalUnidades;
        }

        public void setTotalUnidades(Long totalUnidades) {
            this.totalUnidades = totalUnidades;
        }

        public List<Edificio> getUltimosEdificios() {
            return ultimosEdificios;
        }

        public void setUltimosEdificios(List<Edificio> ultimosEdificios) {
            this.ultimosEdificios = ultimosEdificios;
        }

        public List<UnidadesPorEdificioDTO> getUnidadesPorEdificio() {
            return unidadesPorEdificio;
        }

        public void setUnidadesPorEdificio(List<UnidadesPorEdificioDTO> unidadesPorEdificio) {
            this.unidadesPorEdificio = unidadesPorEdificio;
        }
    }

    public static class UnidadesPorEdificioDTO {
        private Long idEdificio;
        private String nombreEdificio;
        private Integer totalUnidades;
        private Long unidadesOcupadas;

        // Getters y Setters
        public Long getIdEdificio() {
            return idEdificio;
        }

        public void setIdEdificio(Long idEdificio) {
            this.idEdificio = idEdificio;
        }

        public String getNombreEdificio() {
            return nombreEdificio;
        }

        public void setNombreEdificio(String nombreEdificio) {
            this.nombreEdificio = nombreEdificio;
        }

        public Integer getTotalUnidades() {
            return totalUnidades;
        }

        public void setTotalUnidades(Integer totalUnidades) {
            this.totalUnidades = totalUnidades;
        }

        public Long getUnidadesOcupadas() {
            return unidadesOcupadas;
        }

        public void setUnidadesOcupadas(Long unidadesOcupadas) {
            this.unidadesOcupadas = unidadesOcupadas;
        }
    }
}