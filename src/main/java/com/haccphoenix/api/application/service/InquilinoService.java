package com.haccphoenix.api.application.service;

import com.haccphoenix.api.domain.model.Departamento;
import com.haccphoenix.api.domain.model.Inquilino;
import com.haccphoenix.api.domain.repository.DepartamentoRepository;
import com.haccphoenix.api.domain.repository.InquilinoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class InquilinoService {

    @Inject
    InquilinoRepository inquilinoRepository;

    @Inject
    DepartamentoRepository departamentoRepository;

    @Transactional
    public Inquilino registrar(Inquilino inquilino, Integer departamentoId) {
        Departamento departamento = departamentoRepository.findById(departamentoId);
        if (departamento == null) {
            throw new RuntimeException("Departamento no encontrado con ID: " + departamentoId);
        }
        validar(inquilino);
        if (inquilinoRepository.findByDepartamentoOptional(departamentoId).isPresent()) {
            throw new RuntimeException("El departamento ya tiene un inquilino asignado");
        }
        inquilino.setDepartamento(departamento);
        inquilinoRepository.persist(inquilino);
        return inquilino;
    }

    public Inquilino obtener(Integer id) {
        Inquilino inquilino = inquilinoRepository.findById(id);
        if (inquilino == null) {
            throw new RuntimeException("Inquilino no encontrado con ID: " + id);
        }
        return inquilino;
    }

    public List<Inquilino> listar() {
        return inquilinoRepository.listAll();
    }

    public List<Inquilino> listarActivos() {
        return inquilinoRepository.listarActivos();
    }

    @Transactional
    public Inquilino actualizar(Integer id, Inquilino dto) {
        Inquilino existente = obtener(id);

        existente.setCedula(dto.getCedula());
        existente.setNombre(dto.getNombre());
        existente.setApellido(dto.getApellido());
        existente.setTelefono(dto.getTelefono());
        existente.setCorreo(dto.getCorreo());
        existente.setFechaIngreso(dto.getFechaIngreso());
        existente.setFechaSalida(dto.getFechaSalida());
        existente.setEstado(dto.getEstado());

        if (dto.getDepartamento() != null && dto.getDepartamento().getId() != null) {
            Departamento departamento = departamentoRepository.findById(dto.getDepartamento().getId());
            existente.setDepartamento(departamento);
        }

        inquilinoRepository.persist(existente);
        return existente;
    }

    @Transactional
    public void eliminar(Integer id) {
        Inquilino inquilino = obtener(id);
        inquilinoRepository.delete(inquilino);
    }

    private void validar(Inquilino inquilino) {
        if (inquilino == null) {
            throw new RuntimeException("Los datos del inquilino son obligatorios");
        }
        if (inquilino.getCedula() == null || inquilino.getCedula().trim().isEmpty()) {
            throw new RuntimeException("La cedula es obligatoria");
        }
        if (inquilino.getNombre() == null || inquilino.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre es obligatorio");
        }
    }
}
