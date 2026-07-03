package com.haccphoenix.api.application.service;

import com.haccphoenix.api.domain.model.Cargo;
import com.haccphoenix.api.domain.model.Departamento;
import com.haccphoenix.api.domain.model.Pago;
import com.haccphoenix.api.domain.model.PagoCargo;
import com.haccphoenix.api.domain.model.Usuario;
import com.haccphoenix.api.domain.repository.CargoRepository;
import com.haccphoenix.api.domain.repository.DepartamentoRepository;
import com.haccphoenix.api.domain.repository.PagoRepository;
import com.haccphoenix.api.domain.repository.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class PagoService {

    @Inject
    PagoRepository pagoRepository;

    @Inject
    DepartamentoRepository departamentoRepository;

    @Inject
    CargoRepository cargoRepository;

    @Inject
    UsuarioRepository usuarioRepository;

    @Transactional
    public Pago registrar(Pago pago, Integer departamentoId, Integer usuarioId, List<Integer> cargosIds) {
        Departamento departamento = departamentoRepository.findById(departamentoId);
        if (departamento == null) {
            throw new RuntimeException("Departamento no encontrado con ID: " + departamentoId);
        }
        validar(pago);
        pago.setDepartamento(departamento);

        if (usuarioId != null) {
            Usuario usuario = usuarioRepository.findById(usuarioId);
            pago.setUsuario(usuario);
        }

        if (cargosIds != null && !cargosIds.isEmpty()) {
            for (Integer cargoId : cargosIds) {
                Cargo cargo = cargoRepository.findById(cargoId);
                if (cargo == null) {
                    throw new RuntimeException("Cargo no encontrado con ID: " + cargoId);
                }
                PagoCargo pagoCargo = new PagoCargo();
                pagoCargo.setCargo(cargo);
                pagoCargo.setMonto(cargo.getValor());
                pago.agregarPagoCargo(pagoCargo);
                actualizarEstadoCargo(cargo, cargo.getValor());
            }
        }

        pagoRepository.persist(pago);
        return pago;
    }

    public Pago obtener(Integer id) {
        Pago pago = pagoRepository.findById(id);
        if (pago == null) {
            throw new RuntimeException("Pago no encontrado con ID: " + id);
        }
        return pago;
    }

    public List<Pago> listar() {
        return pagoRepository.listAll();
    }

    public List<Pago> listarPorDepartamento(Integer departamentoId) {
        return pagoRepository.listarPorDepartamento(departamentoId);
    }

    public List<Pago> listarPorUsuario(Integer usuarioId) {
        return pagoRepository.listarPorUsuario(usuarioId);
    }

    public List<Pago> listarPorPeriodo(LocalDate inicio, LocalDate fin) {
        return pagoRepository.listarPorPeriodo(inicio, fin);
    }

    @Transactional
    public void eliminar(Integer id) {
        Pago pago = obtener(id);
        pagoRepository.delete(pago);
    }

    private void actualizarEstadoCargo(Cargo cargo, BigDecimal montoPagado) {
        if (montoPagado == null || cargo.getValor() == null) return;
        int cmp = montoPagado.compareTo(cargo.getValor());
        if (cmp >= 0) {
            cargo.setEstado(Cargo.EstadoCargo.PAGADO);
        } else if (cmp > 0) {
            cargo.setEstado(Cargo.EstadoCargo.PARCIAL);
        }
    }

    private void validar(Pago pago) {
        if (pago == null) {
            throw new RuntimeException("Los datos del pago son obligatorios");
        }
        if (pago.getFecha() == null) {
            throw new RuntimeException("La fecha es obligatoria");
        }
        if (pago.getMontoTotal() == null) {
            throw new RuntimeException("El monto total es obligatorio");
        }
    }
}
