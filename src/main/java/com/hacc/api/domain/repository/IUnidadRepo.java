package com.hacc.api.domain.repository;

import com.hacc.api.domain.model.Unidad;
import java.util.List;

public interface IUnidadRepo {
    void crearUnidad(Unidad unidad);
    Unidad obtenerUnidad(Integer id);
    void actualizarUnidad(Unidad unidad);
    void eliminarUnidad(Integer id);
    List<Unidad> listarUnidades();
    List<Unidad> listarUnidadesPorEdificio(Integer edificioId);
    List<Unidad> listarUnidadesPorPropietario(Integer propietarioId);
    boolean existeNumeroEnEdificio(String numero, Integer edificioId);
}