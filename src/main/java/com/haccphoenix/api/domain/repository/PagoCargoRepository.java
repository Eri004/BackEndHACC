package com.haccphoenix.api.domain.repository;

import com.haccphoenix.api.domain.model.PagoCargo;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class PagoCargoRepository implements PanacheRepositoryBase<PagoCargo, Integer> {

    public List<PagoCargo> listarPorPago(Integer pagoId) {
        return list("pago.id", pagoId);
    }

    public List<PagoCargo> listarPorCargo(Integer cargoId) {
        return list("cargo.id", cargoId);
    }
}
