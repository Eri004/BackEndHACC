package com.haccphoenix.api.domain.repository;

import com.haccphoenix.api.domain.model.Pago;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class PagoRepository implements PanacheRepositoryBase<Pago, Integer> {

    public List<Pago> listarPorDepartamento(Integer departamentoId) {
        return list("departamento.id = ?1 ORDER BY fecha DESC", departamentoId);
    }

    public List<Pago> listarPorUsuario(Integer usuarioId) {
        return list("usuario.id = ?1 ORDER BY fecha DESC", usuarioId);
    }

    public List<Pago> listarPorPeriodo(LocalDate inicio, LocalDate fin) {
        return list("fecha BETWEEN ?1 AND ?2 ORDER BY fecha DESC", inicio, fin);
    }
}
