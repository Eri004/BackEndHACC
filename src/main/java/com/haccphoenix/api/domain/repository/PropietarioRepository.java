package com.haccphoenix.api.domain.repository;

import com.haccphoenix.api.domain.model.Propietario;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class PropietarioRepository implements PanacheRepositoryBase<Propietario, Integer> {

    public Optional<Propietario> findByCedulaOptional(String cedula) {
        return find("cedula", cedula).firstResultOptional();
    }

    public Optional<Propietario> findByUsuarioIdOptional(Integer usuarioId) {
        return find("usuario.id", usuarioId).firstResultOptional();
    }

    public boolean existePorCedula(String cedula) {
        return count("cedula", cedula) > 0;
    }

    public boolean existePorUsuarioId(Integer usuarioId) {
        return count("usuario.id", usuarioId) > 0;
    }
}
