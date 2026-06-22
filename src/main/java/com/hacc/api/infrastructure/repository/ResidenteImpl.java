package com.hacc.api.infrastructure.repository;

import java.util.List;

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
}
