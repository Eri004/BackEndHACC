package com.haccphoenix.api.domain.repository;

import com.haccphoenix.api.domain.model.TipoCargo;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TipoCargoRepository implements PanacheRepositoryBase<TipoCargo, Integer> {

    public List<TipoCargo> listarActivos() {
        return list("activo", true);
    }

    public Optional<TipoCargo> findByNombreOptional(String nombre) {
        return find("nombre", nombre).firstResultOptional();
    }
}
