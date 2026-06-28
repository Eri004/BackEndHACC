package com.hacc.api.infrastructure.repository;

import com.hacc.api.domain.model.ServicioProveedor;
import com.hacc.api.domain.repository.IServicioProveedorRepo;
import com.hacc.api.domain.enums.NombreServicio;
import com.hacc.api.domain.enums.EstadoServicio;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class ServicioProveedorImpl implements IServicioProveedorRepo {

    @Inject
    EntityManager em;

    // ==================== CRUD BÁSICO ====================

    @Override
    public void crear(ServicioProveedor servicio) {
        this.em.persist(servicio);
    }

    @Override
    public void actualizar(ServicioProveedor servicio) {
        this.em.merge(servicio);
    }

    @Override
    public void eliminar(Long idServicio) {
        ServicioProveedor servicio = this.em.find(ServicioProveedor.class, idServicio);
        if (servicio != null) {
            this.em.remove(servicio);
        }
    }

    @Override
    public Optional<ServicioProveedor> buscarPorId(Long idServicio) {
        ServicioProveedor servicio = this.em.find(ServicioProveedor.class, idServicio);
        return Optional.ofNullable(servicio);
    }

    @Override
    public List<ServicioProveedor> listarTodos() {
        TypedQuery<ServicioProveedor> query = this.em.createQuery(
            "SELECT s FROM ServicioProveedor s ORDER BY s.fechaVencimiento DESC",
            ServicioProveedor.class
        );
        return query.getResultList();
    }

    // ==================== FILTROS POR PROPIETARIO ====================

    @Override
    public List<ServicioProveedor> listarPorPropietario(Integer idPropietario) {
        TypedQuery<ServicioProveedor> query = this.em.createQuery(
            "SELECT s FROM ServicioProveedor s WHERE s.idPropietario = :idPropietario ORDER BY s.fechaVencimiento DESC",
            ServicioProveedor.class
        );
        query.setParameter("idPropietario", idPropietario);
        return query.getResultList();
    }

    @Override
    public List<ServicioProveedor> listarPorPropietarioYEstado(Integer idPropietario, EstadoServicio estado) {
        TypedQuery<ServicioProveedor> query = this.em.createQuery(
            "SELECT s FROM ServicioProveedor s WHERE s.idPropietario = :idPropietario AND s.estado = :estado ORDER BY s.fechaVencimiento DESC",
            ServicioProveedor.class
        );
        query.setParameter("idPropietario", idPropietario);
        query.setParameter("estado", estado);
        return query.getResultList();
    }

    @Override
    public List<ServicioProveedor> listarPorPropietarioYNombre(Integer idPropietario, NombreServicio nombre) {
        TypedQuery<ServicioProveedor> query = this.em.createQuery(
            "SELECT s FROM ServicioProveedor s WHERE s.idPropietario = :idPropietario AND s.nombre = :nombre ORDER BY s.fechaVencimiento DESC",
            ServicioProveedor.class
        );
        query.setParameter("idPropietario", idPropietario);
        query.setParameter("nombre", nombre);
        return query.getResultList();
    }

    // ==================== FILTROS POR MES ====================

    @Override
    public List<ServicioProveedor> listarPorMes(String mes) {
        TypedQuery<ServicioProveedor> query = this.em.createQuery(
            "SELECT s FROM ServicioProveedor s WHERE s.mes = :mes ORDER BY s.fechaVencimiento DESC",
            ServicioProveedor.class
        );
        query.setParameter("mes", mes);
        return query.getResultList();
    }

    @Override
    public List<ServicioProveedor> listarPorPropietarioYMes(Integer idPropietario, String mes) {
        TypedQuery<ServicioProveedor> query = this.em.createQuery(
            "SELECT s FROM ServicioProveedor s WHERE s.idPropietario = :idPropietario AND s.mes = :mes ORDER BY s.fechaVencimiento DESC",
            ServicioProveedor.class
        );
        query.setParameter("idPropietario", idPropietario);
        query.setParameter("mes", mes);
        return query.getResultList();
    }

    // ==================== FILTROS COMBINADOS ====================

    @Override
    public List<ServicioProveedor> listarPorPropietarioMesYEstado(Integer idPropietario, String mes, EstadoServicio estado) {
        TypedQuery<ServicioProveedor> query = this.em.createQuery(
            "SELECT s FROM ServicioProveedor s WHERE s.idPropietario = :idPropietario AND s.mes = :mes AND s.estado = :estado ORDER BY s.fechaVencimiento DESC",
            ServicioProveedor.class
        );
        query.setParameter("idPropietario", idPropietario);
        query.setParameter("mes", mes);
        query.setParameter("estado", estado);
        return query.getResultList();
    }

    @Override
    public List<ServicioProveedor> listarPorPropietarioMesYNombre(Integer idPropietario, String mes, NombreServicio nombre) {
        TypedQuery<ServicioProveedor> query = this.em.createQuery(
            "SELECT s FROM ServicioProveedor s WHERE s.idPropietario = :idPropietario AND s.mes = :mes AND s.nombre = :nombre ORDER BY s.fechaVencimiento DESC",
            ServicioProveedor.class
        );
        query.setParameter("idPropietario", idPropietario);
        query.setParameter("mes", mes);
        query.setParameter("nombre", nombre);
        return query.getResultList();
    }

    // ==================== FILTROS POR RANGO DE FECHAS ====================

    @Override
    public List<ServicioProveedor> listarPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        TypedQuery<ServicioProveedor> query = this.em.createQuery(
            "SELECT s FROM ServicioProveedor s WHERE s.fechaVencimiento BETWEEN :fechaInicio AND :fechaFin ORDER BY s.fechaVencimiento DESC",
            ServicioProveedor.class
        );
        query.setParameter("fechaInicio", fechaInicio);
        query.setParameter("fechaFin", fechaFin);
        return query.getResultList();
    }

    @Override
    public List<ServicioProveedor> listarPorPropietarioYRangoFechas(Integer idPropietario, LocalDate fechaInicio, LocalDate fechaFin) {
        TypedQuery<ServicioProveedor> query = this.em.createQuery(
            "SELECT s FROM ServicioProveedor s WHERE s.idPropietario = :idPropietario AND s.fechaVencimiento BETWEEN :fechaInicio AND :fechaFin ORDER BY s.fechaVencimiento DESC",
            ServicioProveedor.class
        );
        query.setParameter("idPropietario", idPropietario);
        query.setParameter("fechaInicio", fechaInicio);
        query.setParameter("fechaFin", fechaFin);
        return query.getResultList();
    }

    // ==================== CONSULTAS DE TOTALES ====================

    @Override
    public BigDecimal sumarMontoFacturadoPorPropietarioYMes(Integer idPropietario, String mes) {
        TypedQuery<BigDecimal> query = this.em.createQuery(
            "SELECT COALESCE(SUM(s.montoFacturado), 0) FROM ServicioProveedor s WHERE s.idPropietario = :idPropietario AND s.mes = :mes",
            BigDecimal.class
        );
        query.setParameter("idPropietario", idPropietario);
        query.setParameter("mes", mes);
        return query.getSingleResult();
    }

    @Override
    public BigDecimal sumarMontoPagadoPorPropietarioYMes(Integer idPropietario, String mes) {
        TypedQuery<BigDecimal> query = this.em.createQuery(
            "SELECT COALESCE(SUM(s.montoPagado), 0) FROM ServicioProveedor s WHERE s.idPropietario = :idPropietario AND s.mes = :mes AND s.estado = :estado",
            BigDecimal.class
        );
        query.setParameter("idPropietario", idPropietario);
        query.setParameter("mes", mes);
        query.setParameter("estado", EstadoServicio.PAGADO);
        return query.getSingleResult();
    }

    @Override
    public BigDecimal sumarMontoFacturadoPorPropietarioYEstado(Integer idPropietario, EstadoServicio estado) {
        TypedQuery<BigDecimal> query = this.em.createQuery(
            "SELECT COALESCE(SUM(s.montoFacturado), 0) FROM ServicioProveedor s WHERE s.idPropietario = :idPropietario AND s.estado = :estado",
            BigDecimal.class
        );
        query.setParameter("idPropietario", idPropietario);
        query.setParameter("estado", estado);
        return query.getSingleResult();
    }

    // ==================== VALIDACIONES ====================

    @Override
    public boolean existeServicioPorId(Long idServicio) {
        TypedQuery<Long> query = this.em.createQuery(
            "SELECT COUNT(s) FROM ServicioProveedor s WHERE s.idServicio = :idServicio",
            Long.class
        );
        query.setParameter("idServicio", idServicio);
        return query.getSingleResult() > 0;
    }

    @Override
    public boolean existeServicioPorPropietarioMesYNombre(Integer idPropietario, String mes, NombreServicio nombre) {
        TypedQuery<Long> query = this.em.createQuery(
            "SELECT COUNT(s) FROM ServicioProveedor s WHERE s.idPropietario = :idPropietario AND s.mes = :mes AND s.nombre = :nombre",
            Long.class
        );
        query.setParameter("idPropietario", idPropietario);
        query.setParameter("mes", mes);
        query.setParameter("nombre", nombre);
        return query.getSingleResult() > 0;
    }

    // ==================== CONSULTAS PARA DASHBOARD ====================

    @Override
    public Long contarPorPropietarioYEstado(Integer idPropietario, EstadoServicio estado) {
        TypedQuery<Long> query = this.em.createQuery(
            "SELECT COUNT(s) FROM ServicioProveedor s WHERE s.idPropietario = :idPropietario AND s.estado = :estado",
            Long.class
        );
        query.setParameter("idPropietario", idPropietario);
        query.setParameter("estado", estado);
        return query.getSingleResult();
    }

    @Override
    public List<Object[]> agruparPorEstado(Integer idPropietario) {
        TypedQuery<Object[]> query = this.em.createQuery(
            "SELECT s.estado, COUNT(s), COALESCE(SUM(s.montoFacturado), 0) FROM ServicioProveedor s WHERE s.idPropietario = :idPropietario GROUP BY s.estado",
            Object[].class
        );
        query.setParameter("idPropietario", idPropietario);
        return query.getResultList();
    }

    @Override
    public List<ServicioProveedor> listarVencidosPorPropietario(Integer idPropietario) {
        TypedQuery<ServicioProveedor> query = this.em.createQuery(
            "SELECT s FROM ServicioProveedor s WHERE s.idPropietario = :idPropietario AND s.estado IN (:estados) AND s.fechaVencimiento < :fecha ORDER BY s.fechaVencimiento ASC",
            ServicioProveedor.class
        );
        query.setParameter("idPropietario", idPropietario);
        query.setParameter("estados", List.of(EstadoServicio.PENDIENTE, EstadoServicio.EN_DEUDA));
        query.setParameter("fecha", LocalDate.now());
        return query.getResultList();
    }
}