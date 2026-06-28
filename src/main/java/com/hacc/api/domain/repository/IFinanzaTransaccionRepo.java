package com.hacc.api.domain.repository;

import com.hacc.api.domain.model.FinanzaTransaccion;
import com.hacc.api.domain.enums.TipoTransaccion;
import com.hacc.api.domain.enums.CategoriaTransaccion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IFinanzaTransaccionRepo {

    void crear(FinanzaTransaccion transaccion);
    void actualizar(FinanzaTransaccion transaccion);
    void eliminar(Long idTransaccion);
    Optional<FinanzaTransaccion> buscarPorId(Long idTransaccion);
    List<FinanzaTransaccion> listarTodos();

    List<FinanzaTransaccion> listarPorPropietario(Integer idPropietario);
    List<FinanzaTransaccion> listarPorPropietarioYTipo(Integer idPropietario, TipoTransaccion tipo);
    List<FinanzaTransaccion> listarPorPropietarioYCategoria(Integer idPropietario, CategoriaTransaccion categoria);

    List<FinanzaTransaccion> listarPorPeriodo(String periodo);
    List<FinanzaTransaccion> listarPorPropietarioYPeriodo(Integer idPropietario, String periodo);

    List<FinanzaTransaccion> listarPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin);
    List<FinanzaTransaccion> listarPorPropietarioYRangoFechas(Integer idPropietario, LocalDate fechaInicio, LocalDate fechaFin);

    BigDecimal sumarMontosPorPropietarioYTipo(Integer idPropietario, TipoTransaccion tipo);
    BigDecimal sumarMontosPorPropietarioYPeriodo(Integer idPropietario, String periodo, TipoTransaccion tipo);

    boolean existeTransaccionPorId(Long idTransaccion);
    boolean existeTransaccionPorPropietarioYPeriodo(Integer idPropietario, String periodo, String titulo);
}