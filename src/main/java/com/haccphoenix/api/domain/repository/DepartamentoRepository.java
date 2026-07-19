package com.haccphoenix.api.domain.repository;

import com.haccphoenix.api.domain.model.Departamento;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class DepartamentoRepository implements PanacheRepositoryBase<Departamento, Integer> {

    public List<Departamento> listarPorEdificio(Integer edificioId) {
        return list("edificio.id = ?1 ORDER BY numero", edificioId);
    }

    public List<Departamento> listarPorEdificios(java.util.List<Integer> edificioIds) {
        return list("edificio.id IN ?1 ORDER BY edificio.nombre, numero", edificioIds);
    }

    public List<Departamento> listarPorPropietario(Integer propietarioId) {
        return list("propietario.id = ?1 ORDER BY edificio.nombre, numero", propietarioId);
    }

    public boolean existeNumeroEnEdificio(String numero, Integer edificioId) {
        return count("numero = ?1 AND edificio.id = ?2", numero, edificioId) > 0;
    }
}
