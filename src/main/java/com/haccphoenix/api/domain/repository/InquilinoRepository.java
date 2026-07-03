package com.haccphoenix.api.domain.repository;

import com.haccphoenix.api.domain.model.Inquilino;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class InquilinoRepository implements PanacheRepositoryBase<Inquilino, Integer> {

    public Optional<Inquilino> findByCedulaOptional(String cedula) {
        return find("cedula", cedula).firstResultOptional();
    }

    public Optional<Inquilino> findByDepartamentoOptional(Integer departamentoId) {
        return find("departamento.id", departamentoId).firstResultOptional();
    }

    public List<Inquilino> listarActivos() {
        return list("estado", "ACTIVO");
    }
}
