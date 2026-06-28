package com.hacc.api.infrastructure.repository;

import com.hacc.api.domain.model.PlantillaServicio;
import com.hacc.api.domain.repository.IPlantillaServicioRepo;
import com.hacc.api.domain.enums.NombreServicio;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class PlantillaServicioImpl implements IPlantillaServicioRepo {

    @Inject
    EntityManager em;

    @Override
    public void crear(PlantillaServicio plantilla) {
        this.em.persist(plantilla);
    }

    @Override
    public void actualizar(PlantillaServicio plantilla) {
        this.em.merge(plantilla);
    }

    @Override
    public void eliminar(Long idPlantilla) {
        PlantillaServicio plantilla = this.em.find(PlantillaServicio.class, idPlantilla);
        if (plantilla != null) {
            this.em.remove(plantilla);
        }
    }

    @Override
    public Optional<PlantillaServicio> buscarPorId(Long idPlantilla) {
        PlantillaServicio plantilla = this.em.find(PlantillaServicio.class, idPlantilla);
        return Optional.ofNullable(plantilla);
    }

    @Override
    public List<PlantillaServicio> listarTodos() {
        TypedQuery<PlantillaServicio> query = this.em.createQuery(
            "SELECT p FROM PlantillaServicio p ORDER BY p.nombre",
            PlantillaServicio.class
        );
        return query.getResultList();
    }

    @Override
    public List<PlantillaServicio> listarPorPropietario(Integer idPropietario) {
        TypedQuery<PlantillaServicio> query = this.em.createQuery(
            "SELECT p FROM PlantillaServicio p WHERE p.idPropietario = :idPropietario ORDER BY p.nombre",
            PlantillaServicio.class
        );
        query.setParameter("idPropietario", idPropietario);
        return query.getResultList();
    }

    @Override
    public List<PlantillaServicio> listarPorPropietarioYActivo(Integer idPropietario, Boolean activo) {
        TypedQuery<PlantillaServicio> query = this.em.createQuery(
            "SELECT p FROM PlantillaServicio p WHERE p.idPropietario = :idPropietario AND p.activo = :activo ORDER BY p.nombre",
            PlantillaServicio.class
        );
        query.setParameter("idPropietario", idPropietario);
        query.setParameter("activo", activo);
        return query.getResultList();
    }

    @Override
    public List<PlantillaServicio> listarPorPropietarioYNombre(Integer idPropietario, NombreServicio nombre) {
        TypedQuery<PlantillaServicio> query = this.em.createQuery(
            "SELECT p FROM PlantillaServicio p WHERE p.idPropietario = :idPropietario AND p.nombre = :nombre ORDER BY p.nombre",
            PlantillaServicio.class
        );
        query.setParameter("idPropietario", idPropietario);
        query.setParameter("nombre", nombre);
        return query.getResultList();
    }

    @Override
    public List<PlantillaServicio> listarActivasParaGenerar() {
        TypedQuery<PlantillaServicio> query = this.em.createQuery(
            "SELECT p FROM PlantillaServicio p WHERE p.activo = true AND (p.fechaFin IS NULL OR p.fechaFin >= CURRENT_DATE)",
            PlantillaServicio.class
        );
        return query.getResultList();
    }
}