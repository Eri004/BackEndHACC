package com.hacc.api.domain.repository;

import com.hacc.api.domain.model.PlantillaServicio;
import com.hacc.api.domain.enums.NombreServicio;

import java.util.List;
import java.util.Optional;

public interface IPlantillaServicioRepo {

    // CRUD
    void crear(PlantillaServicio plantilla);
    void actualizar(PlantillaServicio plantilla);
    void eliminar(Long idPlantilla);
    Optional<PlantillaServicio> buscarPorId(Long idPlantilla);
    List<PlantillaServicio> listarTodos();

    // Filtros por propietario
    List<PlantillaServicio> listarPorPropietario(Integer idPropietario);
    List<PlantillaServicio> listarPorPropietarioYActivo(Integer idPropietario, Boolean activo);

    // Filtros por nombre
    List<PlantillaServicio> listarPorPropietarioYNombre(Integer idPropietario, NombreServicio nombre);

    // Obtener plantillas activas que deben generar servicios
    List<PlantillaServicio> listarActivasParaGenerar();
}