package com.haccphoenix.api.application.service;

import com.haccphoenix.api.domain.model.Propietario;
import com.haccphoenix.api.domain.model.Usuario;
import com.haccphoenix.api.domain.repository.PropietarioRepository;
import com.haccphoenix.api.domain.repository.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class PropietarioService {

    @Inject
    PropietarioRepository propietarioRepository;

    @Inject
    UsuarioRepository usuarioRepository;

    @Transactional
    public Propietario registrar(Propietario propietario) {
        validar(propietario);

        if (propietario.getUsuario() != null && propietario.getUsuario().getId() != null) {
            Usuario usuario = usuarioRepository.findById(propietario.getUsuario().getId());
            if (usuario == null) {
                throw new RuntimeException("Usuario no encontrado con ID: " + propietario.getUsuario().getId());
            }
            if (propietarioRepository.existePorUsuarioId(usuario.getId())) {
                throw new RuntimeException("El usuario ya tiene un perfil de propietario");
            }
            propietario.setUsuario(usuario);
        }

        if (propietarioRepository.existePorCedula(propietario.getCedula())) {
            throw new RuntimeException("La cedula ya esta registrada");
        }

        propietarioRepository.persist(propietario);
        return propietario;
    }

    public Propietario obtener(Integer id) {
        Propietario propietario = propietarioRepository.findById(id);
        if (propietario == null) {
            throw new RuntimeException("Propietario no encontrado con ID: " + id);
        }
        return propietario;
    }

    @Transactional
    public void actualizar(Integer id, Propietario dto) {
        Propietario existente = obtener(id);

        if (!existente.getCedula().equals(dto.getCedula())
                && propietarioRepository.existePorCedula(dto.getCedula())) {
            throw new RuntimeException("La cedula ya esta registrada por otro propietario");
        }

        existente.setCedula(dto.getCedula());
        existente.setTelefono(dto.getTelefono());
        existente.setDireccion(dto.getDireccion());

        if (dto.getUsuario() != null && dto.getUsuario().getId() != null) {
            Usuario usuario = usuarioRepository.findById(dto.getUsuario().getId());
            existente.setUsuario(usuario);
        }

        propietarioRepository.persist(existente);
    }

    @Transactional
    public void eliminar(Integer id) {
        Propietario propietario = obtener(id);
        propietarioRepository.delete(propietario);
    }

    public List<Propietario> listar() {
        return propietarioRepository.listAll();
    }

    private void validar(Propietario propietario) {
        if (propietario == null) {
            throw new RuntimeException("Los datos del propietario son obligatorios");
        }
        if (propietario.getCedula() == null || propietario.getCedula().trim().isEmpty()) {
            throw new RuntimeException("La cedula es obligatoria");
        }
    }
}
