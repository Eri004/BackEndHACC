package com.hacc.api.application.service;

import com.hacc.api.domain.model.Propietario;
import com.hacc.api.domain.repository.IPropietarioRepo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PropietarioService {
    @Inject
    private IPropietarioRepo propietarioRepo;
    
    public Propietario registrarPropietario(Propietario dto) {
        if (propietarioRepo.existePorEmail(dto.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        if (propietarioRepo.existePorCedula(dto.getCedula())) {
            throw new RuntimeException("La cédula ya está registrada");
        }
        
        propietarioRepo.crearPropietario(dto);
        return dto;
    }
    
    public void crear(Propietario propietario) {
        propietarioRepo.crearPropietario(propietario);
    }

    public Propietario obtener(Integer id_propietario) {
        Propietario propietario = propietarioRepo.obtenerPropietario(id_propietario);
        if (propietario == null) {
            throw new RuntimeException("Propietario no encontrado");
        }
        return propietario;
    }

    public void actualizar(Integer id_propietario, Propietario dto) {
        Propietario propietarioExistente = propietarioRepo.obtenerPropietario(id_propietario);
        if (propietarioExistente == null) {
            throw new RuntimeException("Propietario no encontrado");
        }
        
        if (!propietarioExistente.getEmail().equals(dto.getEmail()) 
                && propietarioRepo.existePorEmail(dto.getEmail())) {
            throw new RuntimeException("El email ya está registrado por otro propietario");
        }
        
        if (!propietarioExistente.getCedula().equals(dto.getCedula()) 
                && propietarioRepo.existePorCedula(dto.getCedula())) {
            throw new RuntimeException("La cédula ya está registrada por otro propietario");
        }
        
        propietarioExistente.setNombre(dto.getNombre());
        propietarioExistente.setApellido(dto.getApellido());
        propietarioExistente.setTelefono(dto.getTelefono());
        propietarioExistente.setEmail(dto.getEmail());
        propietarioExistente.setCedula(dto.getCedula());
        
        if (dto.getContrasena() != null && !dto.getContrasena().isEmpty()) {
            propietarioExistente.setContrasena(dto.getContrasena());
        }
        
        propietarioRepo.actualizarPropietario(propietarioExistente);
    }

    public void eliminar(Integer id_propietario) {
        Propietario propietario = propietarioRepo.obtenerPropietario(id_propietario);
        if (propietario == null) {
            throw new RuntimeException("Propietario no encontrado");
        }
        propietarioRepo.eliminarPropietario(id_propietario);
    }
    
    public java.util.List<Propietario> listar() {
        return propietarioRepo.listarPropietarios();
    }
}