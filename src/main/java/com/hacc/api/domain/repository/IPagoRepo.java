package com.hacc.api.domain.repository;

import java.util.List;

import com.hacc.api.domain.model.Pago;

public interface IPagoRepo {
    public void crearPago(Pago pago);
    public Pago obtenerPago(Integer id_pago);   
    public void actualizarPago(Pago pago);
    public void eliminarPago(Integer id_pago);
    public List<Pago> listarPagos();
    public List<Pago> listarPagosPorResidente(Integer idResidente);
}
