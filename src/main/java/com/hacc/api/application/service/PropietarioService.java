package com.hacc.api.application.service;

import com.hacc.api.domain.model.Propietario;
import com.hacc.api.domain.repository.IPropietarioRepo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PropietarioService {
    @Inject
    private IPropietarioRepo propietarioRepo;
    
    public void crear(Propietario propietario) {
        propietarioRepo.crearPropietario(propietario);
    }

    public Propietario obtener(Integer id_propietario) {
        return propietarioRepo.obtenerPropietario(id_propietario);
    }

    public void actualizar(Integer id_propietario, Propietario propietario) {
        Propietario propietarioExistente = propietarioRepo.obtenerPropietario(id_propietario);
        propietarioExistente.setNombre(propietario.getNombre());
        propietarioExistente.setApellido(propietario.getApellido());
        propietarioExistente.setTelefono(propietario.getTelefono());
        propietarioExistente.setEmail(propietario.getEmail());
        propietarioExistente.setContrasena(propietario.getContrasena());
        propietarioRepo.actualizarPropietario(propietarioExistente);
    }

    public void eliminar(Integer id_propietario) {
        propietarioRepo.eliminarPropietario(id_propietario);
    }

    public java.util.List<Propietario> listar() {
        return propietarioRepo.listarPropietarios();
    }
}
