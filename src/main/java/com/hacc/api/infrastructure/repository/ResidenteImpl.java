package com.hacc.api.infrastructure.repository;

import java.util.List;
import java.util.Optional;

import com.hacc.api.domain.model.Residente;
import com.hacc.api.domain.repository.IResidenteRepo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional
public class ResidenteImpl implements IResidenteRepo {

    @Inject
    EntityManager em;

    @Override
    public Residente crearResidente(Residente residente) {
        this.em.persist(residente);
        return residente;
    }

    @Override
    public Residente obtenerResidente(Integer id_residente) {
        return this.em.find(Residente.class, id_residente);
    }

    @Override
    public void actualizarResidente(Residente residente) {
        this.em.merge(residente);
    }

    @Override
    public void eliminarResidente(Integer id_residente) {
        Residente residente = this.em.find(Residente.class, id_residente);
        if (residente != null) {
            this.em.remove(residente);
        }
    }

    @Override
    public List<Residente> listarResidentes() {
        TypedQuery<Residente> query = this.em.createQuery("SELECT r FROM Residente r", Residente.class);
        return query.getResultList();
    }
    @Override
    public Optional<Residente> buscarPorId(Integer idResidente) {
        Residente residente = this.em.find(Residente.class, idResidente);
        return Optional.ofNullable(residente);
    }
    @Override
    public List<Residente> listarActivos() {
        TypedQuery<Residente> query = this.em.createQuery(
                "SELECT r FROM Residente r WHERE r.estado = :estado",
                Residente.class);
        query.setParameter("estado", "ACTIVO"); 
        return query.getResultList();
    }
    @Override
    public long contarActivos() {
        TypedQuery<Long> query = this.em.createQuery(
                "SELECT COUNT(r) FROM Residente r WHERE r.estado = :estado",
                Long.class);
        query.setParameter("estado", "ACTIVO");
        return query.getSingleResult();
    }
}
