package com.hacc.api.infrastructure.repository;

import com.hacc.api.domain.model.Edificio;
import com.hacc.api.domain.repository.IEdificioRepo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class EdificioRepoImpl implements IEdificioRepo {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void crearEdificio(Edificio edificio) {
        entityManager.persist(edificio);
    }

    @Override
    public Edificio obtenerEdificio(Integer id) {
        return entityManager.find(Edificio.class, id);
    }

    @Override
    @Transactional
    public void actualizarEdificio(Edificio edificio) {
        entityManager.merge(edificio);
    }

    @Override
    @Transactional
    public void eliminarEdificio(Integer id) {
        Edificio edificio = entityManager.find(Edificio.class, id);
        if (edificio != null) {
            entityManager.remove(edificio);
        }
    }

    @Override
    public List<Edificio> listarEdificios() {
        TypedQuery<Edificio> query = entityManager.createQuery(
            "SELECT e FROM Edificio e ORDER BY e.nombre", Edificio.class
        );
        return query.getResultList();
    }

    @Override
    public List<Edificio> listarEdificiosPorPropietario(Integer propietarioId) {
        TypedQuery<Edificio> query = entityManager.createQuery(
            "SELECT e FROM Edificio e WHERE e.propietario.id_propietario = :propietarioId ORDER BY e.nombre",
            Edificio.class
        );
        query.setParameter("propietarioId", propietarioId);
        return query.getResultList();
    }

    @Override
    public boolean existePorNombre(String nombre, Integer propietarioId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(e) FROM Edificio e WHERE e.nombre = :nombre AND e.propietario.id_propietario = :propietarioId",
            Long.class
        );
        query.setParameter("nombre", nombre);
        query.setParameter("propietarioId", propietarioId);
        return query.getSingleResult() > 0;
    }
}