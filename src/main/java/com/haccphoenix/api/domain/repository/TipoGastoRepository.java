package com.haccphoenix.api.domain.repository;

import com.haccphoenix.api.domain.model.TipoGasto;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TipoGastoRepository implements PanacheRepositoryBase<TipoGasto, Integer> {

    public List<TipoGasto> listarActivos() {
        return list("activo", true);
    }

    public Optional<TipoGasto> findByNombreOptional(String nombre) {
        return find("nombre", nombre).firstResultOptional();
    }
}
