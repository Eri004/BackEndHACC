package com.hacc.api.infrastructure.repository;

import java.util.List;

import com.hacc.api.domain.model.Propietario;
import com.hacc.api.domain.repository.IPropietarioRepo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional
public class PropietarioImpl implements IPropietarioRepo{
    @Inject
    private EntityManager em;

    @Override
    public void actualizarPropietario(Propietario propietario) {
        this.em.merge(propietario);
    }

    @Override
    public void crearPropietario(Propietario propietario) {
        this.em.persist(propietario);
    }

    @Override
    public void eliminarPropietario(Integer id_propietario) {
        Propietario propietario = this.em.find(Propietario.class, id_propietario);
        if (propietario != null) {
            this.em.remove(propietario);
        }
    }

    @Override
    public List<Propietario> listarPropietarios() {
        return this.em.createQuery("SELECT p FROM Propietario p", Propietario.class).getResultList();
    }

    @Override
    public Propietario obtenerPropietario(Integer id_propietario) {
        return this.em.find(Propietario.class, id_propietario);
    }

}
