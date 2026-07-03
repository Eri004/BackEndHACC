package com.haccphoenix.api.domain.repository;

import com.haccphoenix.api.domain.model.Cargo;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class CargoRepository implements PanacheRepositoryBase<Cargo, Integer> {

    public List<Cargo> listarPorDepartamento(Integer departamentoId) {
        return list("departamento.id = ?1 ORDER BY fechaGeneracion DESC", departamentoId);
    }

    public List<Cargo> listarPorEstado(Cargo.EstadoCargo estado) {
        return list("estado", estado);
    }

    public List<Cargo> listarPendientes() {
        return list("estado IN (?1, ?2) ORDER BY fechaVencimiento",
            Cargo.EstadoCargo.PENDIENTE, Cargo.EstadoCargo.PARCIAL);
    }

    public List<Cargo> listarPorPeriodo(LocalDate inicio, LocalDate fin) {
        return list("fechaGeneracion BETWEEN ?1 AND ?2 ORDER BY fechaGeneracion DESC", inicio, fin);
    }
}
