package com.haccphoenix.api.application.service;

import com.haccphoenix.api.domain.model.Departamento;
import com.haccphoenix.api.domain.model.Edificio;
import com.haccphoenix.api.domain.model.Propietario;
import com.haccphoenix.api.domain.repository.DepartamentoRepository;
import com.haccphoenix.api.domain.repository.EdificioRepository;
import com.haccphoenix.api.domain.repository.PropietarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class DepartamentoService {

    @Inject
    DepartamentoRepository departamentoRepository;

    @Inject
    EdificioRepository edificioRepository;

    @Inject
    PropietarioRepository propietarioRepository;

    @Transactional
    public Departamento registrar(Departamento departamento, Integer edificioId, Integer propietarioId) {
        Edificio edificio = edificioRepository.findById(edificioId);
        if (edificio == null) {
            throw new RuntimeException("Edificio no encontrado con ID: " + edificioId);
        }
        Propietario propietario = propietarioRepository.findById(propietarioId);
        if (propietario == null) {
            throw new RuntimeException("Propietario no encontrado con ID: " + propietarioId);
        }
        validar(departamento);
        if (departamentoRepository.existeNumeroEnEdificio(departamento.getNumero(), edificioId)) {
            throw new RuntimeException("Ya existe un departamento con el numero '" + departamento.getNumero()
                + "' en este edificio");
        }
        departamento.setEdificio(edificio);
        departamento.setPropietario(propietario);
        departamentoRepository.persist(departamento);
        return departamento;
    }

    public Departamento obtener(Integer id) {
        Departamento departamento = departamentoRepository.findById(id);
        if (departamento == null) {
            throw new RuntimeException("Departamento no encontrado con ID: " + id);
        }
        return departamento;
    }

    public List<Departamento> listar() {
        return departamentoRepository.listAll();
    }

    public List<Departamento> listarPorEdificio(Integer edificioId) {
        return departamentoRepository.listarPorEdificio(edificioId);
    }

    public List<Departamento> listarPorPropietario(Integer propietarioId) {
        return departamentoRepository.listarPorPropietario(propietarioId);
    }

    @Transactional
    public Departamento actualizar(Integer id, Departamento dto) {
        Departamento existente = obtener(id);

        if (!existente.getNumero().equals(dto.getNumero())
                && departamentoRepository.existeNumeroEnEdificio(dto.getNumero(), existente.getEdificio().getId())) {
            throw new RuntimeException("Ya existe otro departamento con el numero '" + dto.getNumero() + "'");
        }

        existente.setNumero(dto.getNumero());
        existente.setPiso(dto.getPiso());
        existente.setArea(dto.getArea());
        existente.setAlicuota(dto.getAlicuota());
        existente.setObservaciones(dto.getObservaciones());

        if (dto.getPropietario() != null && dto.getPropietario().getId() != null) {
            Propietario propietario = propietarioRepository.findById(dto.getPropietario().getId());
            existente.setPropietario(propietario);
        }

        departamentoRepository.persist(existente);
        return existente;
    }

    @Transactional
    public void eliminar(Integer id) {
        Departamento departamento = obtener(id);
        departamentoRepository.delete(departamento);
    }

    private void validar(Departamento departamento) {
        if (departamento == null) {
            throw new RuntimeException("Los datos del departamento son obligatorios");
        }
        if (departamento.getNumero() == null || departamento.getNumero().trim().isEmpty()) {
            throw new RuntimeException("El numero del departamento es obligatorio");
        }
        if (departamento.getAlicuota() == null) {
            throw new RuntimeException("La alicuota es obligatoria");
        }
    }
}
