package com.hacc.api.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.hacc.api.domain.enums.EstadoPago;
import com.hacc.api.domain.model.Pago;

public interface IPagoRepo {
    void crearPago(Pago pago);
    Pago obtenerPago(Integer id_pago);
    void actualizarPago(Pago pago);
    void eliminarPago(Integer id_pago);
    List<Pago> listarPagos();
    List<Pago> listarPagosPorResidente(Integer idResidente);
    List<Pago> findByPeriodo(LocalDate start, LocalDate end);
    List<Pago> listarProximosAVencer(LocalDate fechaInicio, LocalDate fechaFin);

    Optional<Pago> buscarPorId(Integer idPago);
    List<Pago> listarTodos();
    List<Pago> listarPorResidente(Integer idResidente);
    List<Pago> listarPorEstado(EstadoPago estado);
    List<Pago> listarPorEstadoYPeriodo(EstadoPago estado, String periodo);
    List<Pago> listarPorPeriodo(String periodo);
    List<Pago> listarPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin);
    List<Pago> listarPorEstadoYFechaVencimientoBefore(EstadoPago estado, LocalDate fecha);
    List<Pago> listarPorResidenteYEstadoYFechaVencimientoBefore(Integer idResidente, EstadoPago estado, LocalDate fecha);
    List<Pago> listarPorResidenteYEstadoYFechaVencimientoAfterOrEqual(Integer idResidente, EstadoPago estado, LocalDate fecha);
    List<Pago> listarPorResidenteYEstado(Integer idResidente, EstadoPago estado);
    boolean existePagoPorResidenteYPeriodo(Integer idResidente, String periodo);

    List<Pago> listarPorResidenteYEstadoConLimite(Integer idResidente, EstadoPago estado, int limite);
    Double obtenerTotalDeudaResidente(Integer idResidente);
    Double obtenerTotalPagadoPorResidenteYPeriodo(Integer idResidente, String periodo);
    List<Pago> listarVencidos();
    Long contarResidentesEnMora();
    List<Object[]> contarPagosPorEstado();
}