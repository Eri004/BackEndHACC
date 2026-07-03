package com.haccphoenix.api.application.service;

import com.haccphoenix.api.domain.model.TipoCargo;
import com.haccphoenix.api.domain.repository.TipoCargoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class TipoCargoService {

    @Inject
    TipoCargoRepository tipoCargoRepository;

    @Transactional
    public TipoCargo registrar(TipoCargo tipoCargo) {
        validar(tipoCargo);
        if (tipoCargoRepository.findByNombreOptional(tipoCargo.getNombre()).isPresent()) {
            throw new RuntimeException("Ya existe un tipo de cargo con el nombre '" + tipoCargo.getNombre() + "'");
        }
        if (tipoCargo.getActivo() == null) tipoCargo.setActivo(true);
        tipoCargoRepository.persist(tipoCargo);
        return tipoCargo;
    }

    public TipoCargo obtener(Integer id) {
        TipoCargo tipoCargo = tipoCargoRepository.findById(id);
        if (tipoCargo == null) {
            throw new RuntimeException("Tipo de cargo no encontrado con ID: " + id);
        }
        return tipoCargo;
    }

    public List<TipoCargo> listar() {
        return tipoCargoRepository.listAll();
    }

    public List<TipoCargo> listarActivos() {
        return tipoCargoRepository.listarActivos();
    }

    @Transactional
    public TipoCargo actualizar(Integer id, TipoCargo dto) {
        TipoCargo existente = obtener(id);

        if (!existente.getNombre().equals(dto.getNombre())
                && tipoCargoRepository.findByNombreOptional(dto.getNombre()).isPresent()) {
            throw new RuntimeException("Ya existe otro tipo de cargo con el nombre '" + dto.getNombre() + "'");
        }

        existente.setNombre(dto.getNombre());
        existente.setDescripcion(dto.getDescripcion());
        existente.setActivo(dto.getActivo());

        tipoCargoRepository.persist(existente);
        return existente;
    }

    @Transactional
    public void eliminar(Integer id) {
        TipoCargo tipoCargo = obtener(id);
        tipoCargoRepository.delete(tipoCargo);
    }

    private void validar(TipoCargo tipoCargo) {
        if (tipoCargo == null) {
            throw new RuntimeException("Los datos del tipo de cargo son obligatorios");
        }
        if (tipoCargo.getNombre() == null || tipoCargo.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre del tipo de cargo es obligatorio");
        }
    }
}
