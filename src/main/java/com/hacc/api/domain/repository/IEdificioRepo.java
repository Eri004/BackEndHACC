package com.hacc.api.domain.repository;

import com.hacc.api.domain.model.Edificio;

import java.util.List;
import java.util.Optional;

public interface IEdificioRepo {

    // ==================== CRUD BÁSICO ====================
    void crearEdificio(Edificio edificio);
    void actualizarEdificio(Edificio edificio);
    void eliminarEdificio(Long id);
    Optional<Edificio> buscarPorId(Long id);
    List<Edificio> listarTodos();

    // ==================== FILTROS POR PROPIETARIO ====================
    List<Edificio> listarPorPropietario(Integer propietarioId);
    boolean existePorNombre(String nombre, Integer propietarioId);

    // ==================== FILTROS ADICIONALES ====================
    List<Edificio> listarActivosPorPropietario(Integer propietarioId);
    List<Edificio> listarInactivosPorPropietario(Integer propietarioId);
    Long contarPorPropietario(Integer propietarioId);
    Integer contarUnidadesPorEdificio(Long idEdificio);
    List<Edificio> buscarPorNombre(String nombre, Integer propietarioId);
    Optional<Edificio> buscarPorIdConUnidades(Long id);
    List<Edificio> listarPorPropietarioConUnidades(Integer propietarioId);
}