package com.haccphoenix.api.application.service;

import com.haccphoenix.api.domain.model.Cargo;
import com.haccphoenix.api.domain.model.Departamento;
import com.haccphoenix.api.domain.model.PagoCargo;
import com.haccphoenix.api.domain.model.TipoCargo;
import com.haccphoenix.api.domain.model.Usuario;
import com.haccphoenix.api.domain.repository.CargoRepository;
import com.haccphoenix.api.domain.repository.DepartamentoRepository;
import com.haccphoenix.api.domain.repository.PagoCargoRepository;
import com.haccphoenix.api.domain.repository.TipoCargoRepository;
import com.haccphoenix.api.domain.repository.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class CargoService {

    @Inject
    CargoRepository cargoRepository;

    @Inject
    DepartamentoRepository departamentoRepository;

    @Inject
    TipoCargoRepository tipoCargoRepository;

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    PagoCargoRepository pagoCargoRepository;

    @Transactional
    public Cargo registrar(Cargo cargo, Integer departamentoId, Integer tipoCargoId, Integer usuarioId) {
        Departamento departamento = departamentoRepository.findById(departamentoId);
        if (departamento == null) {
            throw new RuntimeException("Departamento no encontrado con ID: " + departamentoId);
        }
        TipoCargo tipoCargo = tipoCargoRepository.findById(tipoCargoId);
        if (tipoCargo == null) {
            throw new RuntimeException("Tipo de cargo no encontrado con ID: " + tipoCargoId);
        }
        validar(cargo);
        cargo.setDepartamento(departamento);
        cargo.setTipoCargo(tipoCargo);

        if (usuarioId != null) {
            Usuario usuario = usuarioRepository.findById(usuarioId);
            cargo.setUsuario(usuario);
        }

        cargoRepository.persist(cargo);
        return cargo;
    }

    public Cargo obtener(Integer id) {
        Cargo cargo = cargoRepository.findById(id);
        if (cargo == null) {
            throw new RuntimeException("Cargo no encontrado con ID: " + id);
        }
        return cargo;
    }

    public List<Cargo> listar() {
        return cargoRepository.listAll();
    }

    public List<Cargo> listarPorDepartamento(Integer departamentoId) {
        return cargoRepository.listarPorDepartamento(departamentoId);
    }

    public List<Cargo> listarPorEstado(Cargo.EstadoCargo estado) {
        return cargoRepository.listarPorEstado(estado);
    }

    public List<Cargo> listarPendientes() {
        return cargoRepository.listarPendientes();
    }

    public List<Cargo> listarPorPeriodo(LocalDate inicio, LocalDate fin) {
        return cargoRepository.listarPorPeriodo(inicio, fin);
    }

    @Transactional
    public Cargo actualizar(Integer id, Cargo dto) {
        Cargo existente = obtener(id);

        existente.setFechaGeneracion(dto.getFechaGeneracion());
        existente.setFechaVencimiento(dto.getFechaVencimiento());
        existente.setDescripcion(dto.getDescripcion());
        existente.setValor(dto.getValor());
        existente.setEstado(dto.getEstado());

        if (dto.getTipoCargo() != null && dto.getTipoCargo().getId() != null) {
            TipoCargo tipoCargo = tipoCargoRepository.findById(dto.getTipoCargo().getId());
            existente.setTipoCargo(tipoCargo);
        }

        cargoRepository.persist(existente);
        return existente;
    }

    @Transactional
    public void eliminar(Integer id) {
        Cargo cargo = obtener(id);
        List<PagoCargo> relaciones = pagoCargoRepository.listarPorCargo(id);
        for (PagoCargo pc : relaciones) {
            pagoCargoRepository.delete(pc);
        }
        pagoCargoRepository.flush();
        cargoRepository.delete(cargo);
    }

    private void validar(Cargo cargo) {
        if (cargo == null) {
            throw new RuntimeException("Los datos del cargo son obligatorios");
        }
        if (cargo.getFechaGeneracion() == null) {
            throw new RuntimeException("La fecha de generacion es obligatoria");
        }
        if (cargo.getValor() == null) {
            throw new RuntimeException("El valor es obligatorio");
        }
    }
}
