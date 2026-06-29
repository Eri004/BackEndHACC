package com.hacc.api.infrastructure.repository;

import com.hacc.api.domain.model.Edificio;
import com.hacc.api.domain.repository.IEdificioRepo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class EdificioRepoImpl implements IEdificioRepo {

    @Inject
    EntityManager em;

    // ==================== CRUD BÁSICO ====================

    @Override
    public void crearEdificio(Edificio edificio) {
        this.em.persist(edificio);
    }

    @Override
    public void actualizarEdificio(Edificio edificio) {
        this.em.merge(edificio);
    }

    @Override
    public void eliminarEdificio(Long id) {
        Edificio edificio = this.em.find(Edificio.class, id);
        if (edificio != null) {
            this.em.remove(edificio);
        }
    }

    @Override
    public Optional<Edificio> buscarPorId(Long id) {
        Edificio edificio = this.em.find(Edificio.class, id);
        return Optional.ofNullable(edificio);
    }

    @Override
    public List<Edificio> listarTodos() {
        TypedQuery<Edificio> query = this.em.createQuery(
            "SELECT e FROM Edificio e ORDER BY e.idEdificio DESC",
            Edificio.class
        );
        return query.getResultList();
    }

    // ==================== FILTROS POR PROPIETARIO ====================

    @Override
    public List<Edificio> listarPorPropietario(Integer propietarioId) {
        TypedQuery<Edificio> query = this.em.createQuery(
            "SELECT e FROM Edificio e WHERE e.propietario.id_propietario = :propietarioId ORDER BY e.idEdificio DESC",
            Edificio.class
        );
        query.setParameter("propietarioId", propietarioId);
        return query.getResultList();
    }

    @Override
    public boolean existePorNombre(String nombre, Integer propietarioId) {
        TypedQuery<Long> query = this.em.createQuery(
            "SELECT COUNT(e) FROM Edificio e WHERE e.nombre = :nombre AND e.propietario.id_propietario = :propietarioId",
            Long.class
        );
        query.setParameter("nombre", nombre);
        query.setParameter("propietarioId", propietarioId);
        return query.getSingleResult() > 0;
    }

    // ==================== FILTROS ADICIONALES ====================

    /**
     * Lista edificios activos de un propietario
     */
    public List<Edificio> listarActivosPorPropietario(Integer propietarioId) {
        TypedQuery<Edificio> query = this.em.createQuery(
            "SELECT e FROM Edificio e WHERE e.propietario.id_propietario = :propietarioId AND e.activo = true ORDER BY e.idEdificio DESC",
            Edificio.class
        );
        query.setParameter("propietarioId", propietarioId);
        return query.getResultList();
    }

    /**
     * Lista edificios inactivos de un propietario
     */
    public List<Edificio> listarInactivosPorPropietario(Integer propietarioId) {
        TypedQuery<Edificio> query = this.em.createQuery(
            "SELECT e FROM Edificio e WHERE e.propietario.id_propietario = :propietarioId AND e.activo = false ORDER BY e.idEdificio DESC",
            Edificio.class
        );
        query.setParameter("propietarioId", propietarioId);
        return query.getResultList();
    }

    /**
     * Cuenta cuántos edificios tiene un propietario
     */
    public Long contarPorPropietario(Integer propietarioId) {
        TypedQuery<Long> query = this.em.createQuery(
            "SELECT COUNT(e) FROM Edificio e WHERE e.propietario.id_propietario = :propietarioId",
            Long.class
        );
        query.setParameter("propietarioId", propietarioId);
        return query.getSingleResult();
    }

    /**
     * Cuenta cuántas unidades tiene un edificio
     */
    public Integer contarUnidadesPorEdificio(Long idEdificio) {
        TypedQuery<Long> query = this.em.createQuery(
            "SELECT COUNT(u) FROM Unidad u WHERE u.edificio.idEdificio = :idEdificio",
            Long.class
        );
        query.setParameter("idEdificio", idEdificio);
        return query.getSingleResult().intValue();
    }

    /**
     * Busca edificios por nombre (búsqueda parcial)
     */
    public List<Edificio> buscarPorNombre(String nombre, Integer propietarioId) {
        TypedQuery<Edificio> query = this.em.createQuery(
            "SELECT e FROM Edificio e WHERE e.propietario.id_propietario = :propietarioId AND LOWER(e.nombre) LIKE LOWER(:nombre) ORDER BY e.idEdificio DESC",
            Edificio.class
        );
        query.setParameter("propietarioId", propietarioId);
        query.setParameter("nombre", "%" + nombre + "%");
        return query.getResultList();
    }

    /**
     * Obtiene edificios con sus unidades cargadas (EAGER)
     */
    public Optional<Edificio> buscarPorIdConUnidades(Long id) {
        TypedQuery<Edificio> query = this.em.createQuery(
            "SELECT DISTINCT e FROM Edificio e LEFT JOIN FETCH e.unidades WHERE e.idEdificio = :id",
            Edificio.class
        );
        query.setParameter("id", id);
        try {
            return Optional.of(query.getSingleResult());
        } catch (jakarta.persistence.NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Obtiene edificios de un propietario con sus unidades cargadas (EAGER)
     */
    public List<Edificio> listarPorPropietarioConUnidades(Integer propietarioId) {
        TypedQuery<Edificio> query = this.em.createQuery(
            "SELECT DISTINCT e FROM Edificio e LEFT JOIN FETCH e.unidades WHERE e.propietario.id_propietario = :propietarioId ORDER BY e.idEdificio DESC",
            Edificio.class
        );
        query.setParameter("propietarioId", propietarioId);
        return query.getResultList();
    }
}