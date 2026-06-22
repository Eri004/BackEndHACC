package com.hacc.api.application.service;

import com.hacc.api.domain.model.Edificio;
import com.hacc.api.domain.model.Propietario;
import com.hacc.api.domain.model.Unidad;
import com.hacc.api.domain.repository.IEdificioRepo;
import com.hacc.api.domain.repository.IPropietarioRepo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class EdificioService {

    @Inject
    private IEdificioRepo edificioRepo;

    @Inject
    private IPropietarioRepo propietarioRepo;

    @Transactional
    public Edificio registrarEdificio(Edificio edificio, Integer propietarioId) {
        Propietario propietario = propietarioRepo.obtenerPropietario(propietarioId);
        if (propietario == null) {
            throw new RuntimeException("Propietario no encontrado con ID: " + propietarioId);
        }
        validarCamposObligatorios(edificio);
        if (edificioRepo.existePorNombre(edificio.getNombre(), propietarioId)) {
            throw new RuntimeException("Ya existe un edificio con el nombre '" + edificio.getNombre() + 
                                     "' para este propietario");
        }
        edificio.setPropietario(propietario);
        edificioRepo.crearEdificio(edificio);
        return edificio;
    }

    @Transactional
    public Edificio registrarEdificioConUnidades(Edificio edificio, Integer propietarioId, 
                                                 List<Unidad> unidades) {
        Edificio edificioGuardado = registrarEdificio(edificio, propietarioId);

        if (unidades != null && !unidades.isEmpty()) {
            for (Unidad unidad : unidades) {
                unidad.setEdificio(edificioGuardado);
                // El propietario de la unidad puede ser el mismo del edificio
                unidad.setPropietario(edificioGuardado.getPropietario());
                edificioGuardado.agregarUnidad(unidad);
            }
        }
        edificioGuardado.setTotalUnidades(edificioGuardado.getUnidades().size());
        edificioRepo.actualizarEdificio(edificioGuardado);

        return edificioGuardado;
    }

    public Edificio obtener(Integer id) {
        Edificio edificio = edificioRepo.obtenerEdificio(id);
        if (edificio == null) {
            throw new RuntimeException("Edificio no encontrado con ID: " + id);
        }
        return edificio;
    }

    public List<Edificio> listarPorPropietario(Integer propietarioId) {
        Propietario propietario = propietarioRepo.obtenerPropietario(propietarioId);
        if (propietario == null) {
            throw new RuntimeException("Propietario no encontrado con ID: " + propietarioId);
        }
        return edificioRepo.listarEdificiosPorPropietario(propietarioId);
    }

    public List<Edificio> listarTodos() {
        return edificioRepo.listarEdificios();
    }

    @Transactional
    public Edificio actualizar(Integer id, Edificio edificioActualizado) {
        Edificio edificioExistente = edificioRepo.obtenerEdificio(id);
        if (edificioExistente == null) {
            throw new RuntimeException("Edificio no encontrado con ID: " + id);
        }

        if (!edificioExistente.getNombre().equals(edificioActualizado.getNombre()) &&
            edificioRepo.existePorNombre(edificioActualizado.getNombre(), 
                                        edificioExistente.getPropietario().getId_propietario())) {
            throw new RuntimeException("Ya existe otro edificio con el nombre '" + 
                                     edificioActualizado.getNombre() + "'");
        }
        edificioExistente.setNombre(edificioActualizado.getNombre());
        edificioExistente.setDireccion(edificioActualizado.getDireccion());

        edificioRepo.actualizarEdificio(edificioExistente);
        return edificioExistente;
    }

    @Transactional
    public void eliminar(Integer id) {
        Edificio edificio = edificioRepo.obtenerEdificio(id);
        if (edificio == null) {
            throw new RuntimeException("Edificio no encontrado con ID: " + id);
        }

        if (edificio.getUnidades() != null && !edificio.getUnidades().isEmpty()) {
            throw new RuntimeException("No se puede eliminar el edificio porque tiene " + 
                                     edificio.getUnidades().size() + " unidades asociadas");
        }

        edificioRepo.eliminarEdificio(id);
    }

    private void validarCamposObligatorios(Edificio edificio) {
        if (edificio.getNombre() == null || edificio.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre del edificio es obligatorio");
        }
        if (edificio.getDireccion() == null || edificio.getDireccion().trim().isEmpty()) {
            throw new RuntimeException("La dirección del edificio es obligatoria");
        }
    }
}