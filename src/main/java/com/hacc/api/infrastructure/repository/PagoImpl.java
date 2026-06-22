package com.hacc.api.infrastructure.repository;

import java.time.LocalDate;
import java.util.List;

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
        TypedQuery<Pago> query = this.em.createQuery("SELECT p FROM Pago p WHERE p.idResidente = :idResidente", Pago.class);
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
}
