package com.hacc.api.application.service;

import com.hacc.api.domain.model.Edificio;
import com.hacc.api.domain.model.Propietario;
import com.hacc.api.domain.model.Unidad;
import com.hacc.api.domain.repository.IEdificioRepo;
import com.hacc.api.domain.repository.IPropietarioRepo;
import com.hacc.api.domain.repository.IUnidadRepo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UnidadService {

    @Inject
    private IUnidadRepo unidadRepo;

    @Inject
    private IEdificioRepo edificioRepo;

    @Inject
    private IPropietarioRepo propietarioRepo;

    @Transactional
    public Unidad registrarUnidad(Unidad unidad, Integer edificioId, Integer propietarioId) {
        Edificio edificio = edificioRepo.buscarPorId(edificioId.longValue())
                .orElseThrow(() -> new RuntimeException("Edificio no encontrado con ID: " + edificioId));

        Propietario propietario = propietarioRepo.obtenerPropietario(propietarioId);
        if (propietario == null) {
            throw new RuntimeException("Propietario no encontrado con ID: " + propietarioId);
        }

        if (unidad.getNumero() == null || unidad.getNumero().trim().isEmpty()) {
            throw new RuntimeException("El número de la unidad es obligatorio");
        }

        if (unidadRepo.existeNumeroEnEdificio(unidad.getNumero(), edificioId)) {
            throw new RuntimeException("Ya existe una unidad con el número '" + unidad.getNumero() +
                    "' en este edificio");
        }
        unidad.setEdificio(edificio);
        unidad.setPropietario(propietario);
        unidadRepo.crearUnidad(unidad);
        edificio.setTotalUnidades(edificio.getUnidades().size());
        edificioRepo.actualizarEdificio(edificio);

        return unidad;
    }

    @Transactional
    public List<Unidad> registrarMultiplesUnidades(List<Unidad> unidades, Integer edificioId, Integer propietarioId) {
           Edificio edificio = edificioRepo.buscarPorId(edificioId.longValue())
                .orElseThrow(() -> new RuntimeException("Edificio no encontrado con ID: " + edificioId));
        if (edificio == null) {
            throw new RuntimeException("Edificio no encontrado con ID: " + edificioId);
        }
        Propietario propietario = propietarioRepo.obtenerPropietario(propietarioId);
        if (propietario == null) {
            throw new RuntimeException("Propietario no encontrado con ID: " + propietarioId);
        }
        for (Unidad unidad : unidades) {
            if (unidad.getNumero() == null || unidad.getNumero().trim().isEmpty()) {
                throw new RuntimeException("El número de la unidad es obligatorio para todas las unidades");
            }
            if (unidadRepo.existeNumeroEnEdificio(unidad.getNumero(), edificioId)) {
                throw new RuntimeException("Ya existe una unidad con el número '" + unidad.getNumero() +
                        "' en este edificio");
            }
            unidad.setEdificio(edificio);
            unidad.setPropietario(propietario);
            unidadRepo.crearUnidad(unidad);
        }
        edificio.setTotalUnidades(edificio.getUnidades().size());
        edificioRepo.actualizarEdificio(edificio);

        return unidades;
    }

    public Unidad obtener(Integer id) {
        Unidad unidad = unidadRepo.obtenerUnidad(id);
        if (unidad == null) {
            throw new RuntimeException("Unidad no encontrada con ID: " + id);
        }
        return unidad;
    }

    public List<Unidad> listarPorEdificio(Integer edificioId) {
        Edificio edificio = edificioRepo.buscarPorId(edificioId.longValue())
                .orElseThrow(() -> new NotFoundException("Edificio no encontrado con ID: " + edificioId));
        return unidadRepo.listarUnidadesPorEdificio(edificioId);
    }

    public List<Unidad> listarPorPropietario(Integer propietarioId) {
        Propietario propietario = propietarioRepo.obtenerPropietario(propietarioId);
        if (propietario == null) {
            throw new RuntimeException("Propietario no encontrado con ID: " + propietarioId);
        }
        return unidadRepo.listarUnidadesPorPropietario(propietarioId);
    }

    @Transactional
    public void eliminar(Integer id) {
        Unidad unidad = unidadRepo.obtenerUnidad(id);
        if (unidad == null) {
            throw new RuntimeException("Unidad no encontrada con ID: " + id);
        }
        if (unidad.getResidentes() != null && !unidad.getResidentes().isEmpty()) {
            throw new RuntimeException("No se puede eliminar la unidad porque tiene " +
                    unidad.getResidentes().size() + " residentes asociados");
        }
        Edificio edificio = unidad.getEdificio();

        unidadRepo.eliminarUnidad(id);
        if (edificio != null) {
            edificio.setTotalUnidades(edificio.getUnidades().size());
            edificioRepo.actualizarEdificio(edificio);
        }
    }

    @Transactional
    public Unidad cambiarPropietario(Integer unidadId, Integer nuevoPropietarioId) {
        Unidad unidad = unidadRepo.obtenerUnidad(unidadId);
        if (unidad == null) {
            throw new RuntimeException("Unidad no encontrada con ID: " + unidadId);
        }

        Propietario nuevoPropietario = propietarioRepo.obtenerPropietario(nuevoPropietarioId);
        if (nuevoPropietario == null) {
            throw new RuntimeException("Propietario no encontrado con ID: " + nuevoPropietarioId);
        }

        unidad.setPropietario(nuevoPropietario);
        unidadRepo.actualizarUnidad(unidad);
        return unidad;
    }
}