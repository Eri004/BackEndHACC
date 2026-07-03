package com.haccphoenix.api.application.service;

import com.haccphoenix.api.domain.model.TipoGasto;
import com.haccphoenix.api.domain.repository.TipoGastoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class TipoGastoService {

    @Inject
    TipoGastoRepository tipoGastoRepository;

    @Transactional
    public TipoGasto registrar(TipoGasto tipoGasto) {
        validar(tipoGasto);
        if (tipoGastoRepository.findByNombreOptional(tipoGasto.getNombre()).isPresent()) {
            throw new RuntimeException("Ya existe un tipo de gasto con el nombre '" + tipoGasto.getNombre() + "'");
        }
        if (tipoGasto.getActivo() == null) tipoGasto.setActivo(true);
        tipoGastoRepository.persist(tipoGasto);
        return tipoGasto;
    }

    public TipoGasto obtener(Integer id) {
        TipoGasto tipoGasto = tipoGastoRepository.findById(id);
        if (tipoGasto == null) {
            throw new RuntimeException("Tipo de gasto no encontrado con ID: " + id);
        }
        return tipoGasto;
    }

    public List<TipoGasto> listar() {
        return tipoGastoRepository.listAll();
    }

    public List<TipoGasto> listarActivos() {
        return tipoGastoRepository.listarActivos();
    }

    @Transactional
    public TipoGasto actualizar(Integer id, TipoGasto dto) {
        TipoGasto existente = obtener(id);

        if (!existente.getNombre().equals(dto.getNombre())
                && tipoGastoRepository.findByNombreOptional(dto.getNombre()).isPresent()) {
            throw new RuntimeException("Ya existe otro tipo de gasto con el nombre '" + dto.getNombre() + "'");
        }

        existente.setNombre(dto.getNombre());
        existente.setDescripcion(dto.getDescripcion());
        existente.setActivo(dto.getActivo());

        tipoGastoRepository.persist(existente);
        return existente;
    }

    @Transactional
    public void eliminar(Integer id) {
        TipoGasto tipoGasto = obtener(id);
        tipoGastoRepository.delete(tipoGasto);
    }

    private void validar(TipoGasto tipoGasto) {
        if (tipoGasto == null) {
            throw new RuntimeException("Los datos del tipo de gasto son obligatorios");
        }
        if (tipoGasto.getNombre() == null || tipoGasto.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre del tipo de gasto es obligatorio");
        }
    }
}
