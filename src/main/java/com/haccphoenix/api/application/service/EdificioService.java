package com.haccphoenix.api.application.service;

import com.haccphoenix.api.domain.model.Edificio;
import com.haccphoenix.api.domain.repository.EdificioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class EdificioService {

    @Inject
    EdificioRepository edificioRepository;

    @Transactional
    public Edificio registrar(Edificio edificio) {
        validar(edificio);
        if (edificioRepository.existePorNombre(edificio.getNombre())) {
            throw new RuntimeException("Ya existe un edificio con el nombre '" + edificio.getNombre() + "'");
        }
        edificioRepository.persist(edificio);
        return edificio;
    }

    public Edificio obtener(Integer id) {
        Edificio edificio = edificioRepository.findById(id);
        if (edificio == null) {
            throw new RuntimeException("Edificio no encontrado con ID: " + id);
        }
        return edificio;
    }

    public List<Edificio> listar() {
        return edificioRepository.listAll();
    }

    public List<Edificio> listarActivos() {
        return edificioRepository.listarActivos();
    }

    @Transactional
    public Edificio actualizar(Integer id, Edificio dto) {
        Edificio existente = obtener(id);

        if (!existente.getNombre().equals(dto.getNombre())
                && edificioRepository.existePorNombre(dto.getNombre())) {
            throw new RuntimeException("Ya existe otro edificio con el nombre '" + dto.getNombre() + "'");
        }

        existente.setNombre(dto.getNombre());
        existente.setDireccion(dto.getDireccion());
        existente.setNumeroPisos(dto.getNumeroPisos());
        existente.setDescripcion(dto.getDescripcion());
        existente.setEstado(dto.getEstado());

        edificioRepository.persist(existente);
        return existente;
    }

    @Transactional
    public void eliminar(Integer id) {
        Edificio edificio = obtener(id);
        edificioRepository.delete(edificio);
    }

    private void validar(Edificio edificio) {
        if (edificio == null) {
            throw new RuntimeException("Los datos del edificio son obligatorios");
        }
        if (edificio.getNombre() == null || edificio.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre del edificio es obligatorio");
        }
        if (edificio.getDireccion() == null || edificio.getDireccion().trim().isEmpty()) {
            throw new RuntimeException("La direccion del edificio es obligatoria");
        }
    }
}
