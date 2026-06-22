package com.hacc.api.domain.repository;

import com.hacc.api.domain.model.Edificio;
import java.util.List;

public interface IEdificioRepo {
    void crearEdificio(Edificio edificio);
    Edificio obtenerEdificio(Integer id);
    void actualizarEdificio(Edificio edificio);
    void eliminarEdificio(Integer id);
    List<Edificio> listarEdificios();
    List<Edificio> listarEdificiosPorPropietario(Integer propietarioId);
    boolean existePorNombre(String nombre, Integer propietarioId);
}