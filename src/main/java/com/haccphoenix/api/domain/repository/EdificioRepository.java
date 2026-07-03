package com.haccphoenix.api.domain.repository;

import com.haccphoenix.api.domain.model.Edificio;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class EdificioRepository implements PanacheRepositoryBase<Edificio, Integer> {

    public List<Edificio> listarActivos() {
        return list("estado", "ACTIVO");
    }

    public boolean existePorNombre(String nombre) {
        return count("nombre", nombre) > 0;
    }
}
