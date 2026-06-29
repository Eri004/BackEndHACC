package com.hacc.api.infrastructure.repository;

import com.hacc.api.domain.model.Unidad;
import com.hacc.api.domain.repository.IUnidadRepo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class UnidadRepoImpl implements IUnidadRepo {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void crearUnidad(Unidad unidad) {
        entityManager.persist(unidad);
    }

    @Override
    public Unidad obtenerUnidad(Integer id) {
        return entityManager.find(Unidad.class, id);
    }

    @Override
    @Transactional
    public void actualizarUnidad(Unidad unidad) {
        entityManager.merge(unidad);
    }

    @Override
    @Transactional
    public void eliminarUnidad(Integer id) {
        Unidad unidad = entityManager.find(Unidad.class, id);
        if (unidad != null) {
            entityManager.remove(unidad);
        }
    }

    @Override
    public List<Unidad> listarUnidades() {
        TypedQuery<Unidad> query = entityManager.createQuery(
                "SELECT u FROM Unidad u ORDER BY u.edificio.nombre, u.numero",
                Unidad.class);
        return query.getResultList();
    }

    @Override
    public List<Unidad> listarUnidadesPorEdificio(Integer edificioId) {
        TypedQuery<Unidad> query = entityManager.createQuery(
                "SELECT u FROM Unidad u WHERE u.edificio.idEdificio = :edificioId ORDER BY u.numero",
                Unidad.class);
        query.setParameter("edificioId", edificioId);
        return query.getResultList();
    }

    @Override
    public List<Unidad> listarUnidadesPorPropietario(Integer propietarioId) {
        TypedQuery<Unidad> query = entityManager.createQuery(
                "SELECT u FROM Unidad u WHERE u.propietario.idPropietario = :propietarioId ORDER BY u.edificio.nombre, u.numero",
                Unidad.class);
        query.setParameter("propietarioId", propietarioId);
        return query.getResultList();
    }

    @Override
    public boolean existeNumeroEnEdificio(String numero, Integer edificioId) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(u) FROM Unidad u WHERE u.numero = :numero AND u.edificio.idEdificio = :edificioId",
                Long.class);
        query.setParameter("numero", numero);
        query.setParameter("edificioId", edificioId);
        return query.getSingleResult() > 0;
    }

    @Override
    public List<Unidad> listarPorEdificio(Long idEdificio) {
        TypedQuery<Unidad> query = this.entityManager.createQuery(
                "SELECT u FROM Unidad u WHERE u.edificio.idEdificio = :idEdificio",
                Unidad.class);
        query.setParameter("idEdificio", idEdificio);
        return query.getResultList();
    }
}