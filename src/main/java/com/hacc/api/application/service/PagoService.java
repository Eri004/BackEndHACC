package com.hacc.api.application.service;

import java.util.List;

import com.hacc.api.domain.model.Pago;
import com.hacc.api.domain.repository.IPagoRepo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PagoService {

    @Inject
    IPagoRepo pagoRepo;

    public void crear(Pago pago) {
        pagoRepo.crearPago(pago);
    }

    public Pago obtener(Integer id_pago) {
        return pagoRepo.obtenerPago(id_pago);
    }

    public void actualizar(Integer id_pago, Pago pago) {
        Pago pagoExistente = pagoRepo.obtenerPago(id_pago);
        if (pagoExistente != null) {
            pagoExistente.setIdResidente(pago.getIdResidente());
            pagoExistente.setTitulo(pago.getTitulo());
            pagoExistente.setMonto(pago.getMonto());
            pagoExistente.setFecha(pago.getFecha());
            pagoRepo.actualizarPago(pagoExistente);
        }
    }

    public void eliminar(Integer id_pago) {
        pagoRepo.eliminarPago(id_pago);
    }

    public List<Pago> listar() {
        return pagoRepo.listarPagos();
    }

    public List<Pago> listarPorResidente(Integer idResidente) {
        return pagoRepo.listarPagosPorResidente(idResidente);
    }
}
