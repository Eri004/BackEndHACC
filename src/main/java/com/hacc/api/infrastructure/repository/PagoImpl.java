package com.hacc.api.infrastructure.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.hacc.api.domain.enums.EstadoPago;
import com.hacc.api.domain.model.Pago;
import com.hacc.api.domain.repository.IPagoRepo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional
public class PagoImpl implements IPagoRepo {

    @Inject
    EntityManager em;

    // ==================== MÉTODOS EXISTENTES (MANTENIDOS) ====================

    @Override
    public void crearPago(Pago pago) {
        this.em.persist(pago);
    }

    @Override
    public Pago obtenerPago(Integer id_pago) {
        return this.em.find(Pago.class, id_pago);
    }

    @Override
    public void actualizarPago(Pago pago) {
        this.em.merge(pago);
    }

    @Override
    public void eliminarPago(Integer id_pago) {
        Pago pago = this.em.find(Pago.class, id_pago);
        if (pago != null) {
            this.em.remove(pago);
        }
    }

    @Override
    public List<Pago> listarPagos() {
        TypedQuery<Pago> query = this.em.createQuery("SELECT p FROM Pago p", Pago.class);
        return query.getResultList();
    }

    @Override
    public List<Pago> listarPagosPorResidente(Integer idResidente) {
        TypedQuery<Pago> query = this.em.createQuery(
            "SELECT p FROM Pago p WHERE p.idResidente = :idResidente", 
            Pago.class
        );
        query.setParameter("idResidente", idResidente);
        return query.getResultList();
    }

    @Override
    public List<Pago> findByPeriodo(LocalDate start, LocalDate end) {
        TypedQuery<Pago> query = em.createQuery(
            "SELECT p FROM Pago p WHERE p.fecha BETWEEN :start AND :end",
            Pago.class
        );
        query.setParameter("start", start);
        query.setParameter("end", end);
        return query.getResultList();
    }

    // ==================== NUEVOS MÉTODOS PARA EL SERVICE ====================

    @Override
    public Optional<Pago> buscarPorId(Integer idPago) {
        Pago pago = this.em.find(Pago.class, idPago);
        return Optional.ofNullable(pago);
    }

    @Override
    public List<Pago> listarTodos() {
        TypedQuery<Pago> query = this.em.createQuery(
            "SELECT p FROM Pago p ORDER BY p.fecha DESC", 
            Pago.class
        );
        return query.getResultList();
    }

    @Override
    public List<Pago> listarPorResidente(Integer idResidente) {
        TypedQuery<Pago> query = this.em.createQuery(
            "SELECT p FROM Pago p WHERE p.idResidente = :idResidente ORDER BY p.fecha DESC",
            Pago.class
        );
        query.setParameter("idResidente", idResidente);
        return query.getResultList();
    }

    @Override
    public List<Pago> listarPorEstado(EstadoPago estado) {
        TypedQuery<Pago> query = this.em.createQuery(
            "SELECT p FROM Pago p WHERE p.estado = :estado ORDER BY p.fecha DESC",
            Pago.class
        );
        query.setParameter("estado", estado);
        return query.getResultList();
    }

    @Override
    public List<Pago> listarPorEstadoYPeriodo(EstadoPago estado, String periodo) {
        TypedQuery<Pago> query = this.em.createQuery(
            "SELECT p FROM Pago p WHERE p.estado = :estado AND p.periodo = :periodo ORDER BY p.fecha DESC",
            Pago.class
        );
        query.setParameter("estado", estado);
        query.setParameter("periodo", periodo);
        return query.getResultList();
    }

    @Override
    public List<Pago> listarPorPeriodo(String periodo) {
        TypedQuery<Pago> query = this.em.createQuery(
            "SELECT p FROM Pago p WHERE p.periodo = :periodo ORDER BY p.fecha DESC",
            Pago.class
        );
        query.setParameter("periodo", periodo);
        return query.getResultList();
    }

    @Override
    public List<Pago> listarPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        TypedQuery<Pago> query = this.em.createQuery(
            "SELECT p FROM Pago p WHERE p.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY p.fecha DESC",
            Pago.class
        );
        query.setParameter("fechaInicio", fechaInicio);
        query.setParameter("fechaFin", fechaFin);
        return query.getResultList();
    }

    @Override
    public List<Pago> listarPorEstadoYFechaVencimientoBefore(EstadoPago estado, LocalDate fecha) {
        TypedQuery<Pago> query = this.em.createQuery(
            "SELECT p FROM Pago p WHERE p.estado = :estado AND p.fechaVencimiento < :fecha ORDER BY p.fechaVencimiento ASC",
            Pago.class
        );
        query.setParameter("estado", estado);
        query.setParameter("fecha", fecha);
        return query.getResultList();
    }

    @Override
    public List<Pago> listarPorResidenteYEstadoYFechaVencimientoBefore(Integer idResidente, EstadoPago estado, LocalDate fecha) {
        TypedQuery<Pago> query = this.em.createQuery(
            "SELECT p FROM Pago p WHERE p.idResidente = :idResidente AND p.estado = :estado AND p.fechaVencimiento < :fecha ORDER BY p.fechaVencimiento ASC",
            Pago.class
        );
        query.setParameter("idResidente", idResidente);
        query.setParameter("estado", estado);
        query.setParameter("fecha", fecha);
        return query.getResultList();
    }

    @Override
    public List<Pago> listarPorResidenteYEstadoYFechaVencimientoAfterOrEqual(Integer idResidente, EstadoPago estado, LocalDate fecha) {
        TypedQuery<Pago> query = this.em.createQuery(
            "SELECT p FROM Pago p WHERE p.idResidente = :idResidente AND p.estado = :estado AND p.fechaVencimiento >= :fecha ORDER BY p.fechaVencimiento ASC",
            Pago.class
        );
        query.setParameter("idResidente", idResidente);
        query.setParameter("estado", estado);
        query.setParameter("fecha", fecha);
        return query.getResultList();
    }

    @Override
    public List<Pago> listarPorResidenteYEstado(Integer idResidente, EstadoPago estado) {
        TypedQuery<Pago> query = this.em.createQuery(
            "SELECT p FROM Pago p WHERE p.idResidente = :idResidente AND p.estado = :estado ORDER BY p.fecha DESC",
            Pago.class
        );
        query.setParameter("idResidente", idResidente);
        query.setParameter("estado", estado);
        return query.getResultList();
    }

    @Override
    public boolean existePagoPorResidenteYPeriodo(Integer idResidente, String periodo) {
        TypedQuery<Long> query = this.em.createQuery(
            "SELECT COUNT(p) FROM Pago p WHERE p.idResidente = :idResidente AND p.periodo = :periodo",
            Long.class
        );
        query.setParameter("idResidente", idResidente);
        query.setParameter("periodo", periodo);
        Long count = query.getSingleResult();
        return count > 0;
    }

    // ==================== MÉTODOS ADICIONALES ÚTILES ====================

    /**
     * Obtiene pagos por estado y residente (con paginación opcional)
     */
    @Override
    public List<Pago> listarPorResidenteYEstadoConLimite(Integer idResidente, EstadoPago estado, int limite) {
        TypedQuery<Pago> query = this.em.createQuery(
            "SELECT p FROM Pago p WHERE p.idResidente = :idResidente AND p.estado = :estado ORDER BY p.fecha DESC",
            Pago.class
        );
        query.setParameter("idResidente", idResidente);
        query.setParameter("estado", estado);
        query.setMaxResults(limite);
        return query.getResultList();
    }

    /**
     * Obtiene el total de deuda de un residente (suma de todos los pagos pendientes y vencidos)
     */
    @Override
    public Double obtenerTotalDeudaResidente(Integer idResidente) {
        TypedQuery<Double> query = this.em.createQuery(
            "SELECT COALESCE(SUM(p.montoEsperado), 0) FROM Pago p " +
            "WHERE p.idResidente = :idResidente AND p.estado IN :estados",
            Double.class
        );
        query.setParameter("idResidente", idResidente);
        query.setParameter("estados", List.of(EstadoPago.PENDIENTE, EstadoPago.VENCIDO));
        return query.getSingleResult();
    }

    /**
     * Obtiene el total pagado por un residente en un período específico
     */
    @Override
    public Double obtenerTotalPagadoPorResidenteYPeriodo(Integer idResidente, String periodo) {
        TypedQuery<Double> query = this.em.createQuery(
            "SELECT COALESCE(SUM(p.montoPagado), 0) FROM Pago p " +
            "WHERE p.idResidente = :idResidente AND p.periodo = :periodo AND p.estado = :estado",
            Double.class
        );
        query.setParameter("idResidente", idResidente);
        query.setParameter("periodo", periodo);
        query.setParameter("estado", EstadoPago.PAGADO);
        return query.getSingleResult();
    }

    /**
     * Obtiene todos los pagos vencidos sin importar el residente
     */
    @Override
    public List<Pago> listarVencidos() {
        return listarPorEstadoYFechaVencimientoBefore(EstadoPago.PENDIENTE, LocalDate.now());
    }

    /**
     * Cuenta cuántos residentes están en mora (tienen al menos un pago vencido)
     */
    @Override
    public Long contarResidentesEnMora() {
        TypedQuery<Long> query = this.em.createQuery(
            "SELECT COUNT(DISTINCT p.idResidente) FROM Pago p " +
            "WHERE p.estado = :estado AND p.fechaVencimiento < :fecha",
            Long.class
        );
        query.setParameter("estado", EstadoPago.PENDIENTE);
        query.setParameter("fecha", LocalDate.now());
        return query.getSingleResult();
    }

    /**
     * Obtiene el resumen de pagos por estado
     */
    @Override
    public List<Object[]> contarPagosPorEstado() {
        TypedQuery<Object[]> query = this.em.createQuery(
            "SELECT p.estado, COUNT(p), COALESCE(SUM(p.montoEsperado), 0) " +
            "FROM Pago p GROUP BY p.estado",
            Object[].class
        );
        return query.getResultList();
    }
}