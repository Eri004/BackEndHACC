package com.haccphoenix.api.domain.repository;

import com.haccphoenix.api.domain.model.Gasto;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class GastoRepository implements PanacheRepositoryBase<Gasto, Integer> {

    public List<Gasto> listarPorEdificio(Integer edificioId) {
        return list("edificio.id = ?1 ORDER BY fecha DESC", edificioId);
    }

    public List<Gasto> listarPorTipoGasto(Integer tipoGastoId) {
        return list("tipoGasto.id = ?1 ORDER BY fecha DESC", tipoGastoId);
    }

    public List<Gasto> listarPorPeriodo(LocalDate inicio, LocalDate fin) {
        return list("fecha BETWEEN ?1 AND ?2 ORDER BY fecha DESC", inicio, fin);
    }
}
