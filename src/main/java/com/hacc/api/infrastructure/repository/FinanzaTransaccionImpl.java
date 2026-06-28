package com.hacc.api.infrastructure.repository;

import com.hacc.api.domain.model.FinanzaTransaccion;
import com.hacc.api.domain.repository.IFinanzaTransaccionRepo;
import com.hacc.api.domain.enums.TipoTransaccion;
import com.hacc.api.domain.enums.CategoriaTransaccion;

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
public class FinanzaTransaccionImpl implements IFinanzaTransaccionRepo {

    @Inject
    EntityManager em;

    // ==================== CRUD BÁSICO ====================

    @Override
    public void crear(FinanzaTransaccion transaccion) {
        this.em.persist(transaccion);
    }

    @Override
    public void actualizar(FinanzaTransaccion transaccion) {
        this.em.merge(transaccion);
    }

    @Override
    public void eliminar(Long idTransaccion) {
        FinanzaTransaccion transaccion = this.em.find(FinanzaTransaccion.class, idTransaccion);
        if (transaccion != null) {
            this.em.remove(transaccion);
        }
    }

    @Override
    public Optional<FinanzaTransaccion> buscarPorId(Long idTransaccion) {
        FinanzaTransaccion transaccion = this.em.find(FinanzaTransaccion.class, idTransaccion);
        return Optional.ofNullable(transaccion);
    }

    @Override
    public List<FinanzaTransaccion> listarTodos() {
        TypedQuery<FinanzaTransaccion> query = this.em.createQuery(
            "SELECT t FROM FinanzaTransaccion t ORDER BY t.fecha DESC",
            FinanzaTransaccion.class
        );
        return query.getResultList();
    }

    // ==================== FILTROS POR PROPIETARIO ====================

    @Override
    public List<FinanzaTransaccion> listarPorPropietario(Integer idPropietario) {
        TypedQuery<FinanzaTransaccion> query = this.em.createQuery(
            "SELECT t FROM FinanzaTransaccion t WHERE t.idPropietario = :idPropietario ORDER BY t.fecha DESC",
            FinanzaTransaccion.class
        );
        query.setParameter("idPropietario", idPropietario);
        return query.getResultList();
    }

    @Override
    public List<FinanzaTransaccion> listarPorPropietarioYTipo(Integer idPropietario, TipoTransaccion tipo) {
        TypedQuery<FinanzaTransaccion> query = this.em.createQuery(
            "SELECT t FROM FinanzaTransaccion t WHERE t.idPropietario = :idPropietario AND t.tipo = :tipo ORDER BY t.fecha DESC",
            FinanzaTransaccion.class
        );
        query.setParameter("idPropietario", idPropietario);
        query.setParameter("tipo", tipo);
        return query.getResultList();
    }

    @Override
    public List<FinanzaTransaccion> listarPorPropietarioYCategoria(Integer idPropietario, CategoriaTransaccion categoria) {
        TypedQuery<FinanzaTransaccion> query = this.em.createQuery(
            "SELECT t FROM FinanzaTransaccion t WHERE t.idPropietario = :idPropietario AND t.categoria = :categoria ORDER BY t.fecha DESC",
            FinanzaTransaccion.class
        );
        query.setParameter("idPropietario", idPropietario);
        query.setParameter("categoria", categoria);
        return query.getResultList();
    }

    @Override
    public List<FinanzaTransaccion> listarPorPeriodo(String periodo) {
        TypedQuery<FinanzaTransaccion> query = this.em.createQuery(
            "SELECT t FROM FinanzaTransaccion t WHERE t.periodo = :periodo ORDER BY t.fecha DESC",
            FinanzaTransaccion.class
        );
        query.setParameter("periodo", periodo);
        return query.getResultList();
    }

    @Override
    public List<FinanzaTransaccion> listarPorPropietarioYPeriodo(Integer idPropietario, String periodo) {
        TypedQuery<FinanzaTransaccion> query = this.em.createQuery(
            "SELECT t FROM FinanzaTransaccion t WHERE t.idPropietario = :idPropietario AND t.periodo = :periodo ORDER BY t.fecha DESC",
            FinanzaTransaccion.class
        );
        query.setParameter("idPropietario", idPropietario);
        query.setParameter("periodo", periodo);
        return query.getResultList();
    }

    @Override
    public List<FinanzaTransaccion> listarPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        TypedQuery<FinanzaTransaccion> query = this.em.createQuery(
            "SELECT t FROM FinanzaTransaccion t WHERE t.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY t.fecha DESC",
            FinanzaTransaccion.class
        );
        query.setParameter("fechaInicio", fechaInicio);
        query.setParameter("fechaFin", fechaFin);
        return query.getResultList();
    }

    @Override
    public List<FinanzaTransaccion> listarPorPropietarioYRangoFechas(Integer idPropietario, LocalDate fechaInicio, LocalDate fechaFin) {
        TypedQuery<FinanzaTransaccion> query = this.em.createQuery(
            "SELECT t FROM FinanzaTransaccion t WHERE t.idPropietario = :idPropietario AND t.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY t.fecha DESC",
            FinanzaTransaccion.class
        );
        query.setParameter("idPropietario", idPropietario);
        query.setParameter("fechaInicio", fechaInicio);
        query.setParameter("fechaFin", fechaFin);
        return query.getResultList();
    }

    @Override
    public BigDecimal sumarMontosPorPropietarioYTipo(Integer idPropietario, TipoTransaccion tipo) {
        TypedQuery<BigDecimal> query = this.em.createQuery(
            "SELECT COALESCE(SUM(t.monto), 0) FROM FinanzaTransaccion t WHERE t.idPropietario = :idPropietario AND t.tipo = :tipo",
            BigDecimal.class
        );
        query.setParameter("idPropietario", idPropietario);
        query.setParameter("tipo", tipo);
        return query.getSingleResult();
    }

    @Override
    public BigDecimal sumarMontosPorPropietarioYPeriodo(Integer idPropietario, String periodo, TipoTransaccion tipo) {
        TypedQuery<BigDecimal> query = this.em.createQuery(
            "SELECT COALESCE(SUM(t.monto), 0) FROM FinanzaTransaccion t WHERE t.idPropietario = :idPropietario AND t.periodo = :periodo AND t.tipo = :tipo",
            BigDecimal.class
        );
        query.setParameter("idPropietario", idPropietario);
        query.setParameter("periodo", periodo);
        query.setParameter("tipo", tipo);
        return query.getSingleResult();
    }

    @Override
    public boolean existeTransaccionPorId(Long idTransaccion) {
        TypedQuery<Long> query = this.em.createQuery(
            "SELECT COUNT(t) FROM FinanzaTransaccion t WHERE t.idTransaccion = :idTransaccion",
            Long.class
        );
        query.setParameter("idTransaccion", idTransaccion);
        return query.getSingleResult() > 0;
    }

    @Override
    public boolean existeTransaccionPorPropietarioYPeriodo(Integer idPropietario, String periodo, String titulo) {
        TypedQuery<Long> query = this.em.createQuery(
            "SELECT COUNT(t) FROM FinanzaTransaccion t WHERE t.idPropietario = :idPropietario AND t.periodo = :periodo AND t.titulo = :titulo",
            Long.class
        );
        query.setParameter("idPropietario", idPropietario);
        query.setParameter("periodo", periodo);
        query.setParameter("titulo", titulo);
        return query.getSingleResult() > 0;
    }
}