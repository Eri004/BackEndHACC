package com.haccphoenix.api.application.service;

import com.haccphoenix.api.domain.model.Usuario;
import com.haccphoenix.api.domain.repository.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class UsuarioService {

    @Inject
    UsuarioRepository usuarioRepository;

    @Transactional
    public Usuario registrar(Usuario usuario) {
        validar(usuario);
        if (usuarioRepository.existePorEmail(usuario.getEmail())) {
            throw new RuntimeException("El email ya esta registrado");
        }
        if (usuario.getActivo() == null) usuario.setActivo(true);
        usuarioRepository.persist(usuario);
        return usuario;
    }

    public Usuario obtener(Integer id) {
        Usuario usuario = usuarioRepository.findById(id);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        return usuario;
    }

    public List<Usuario> listar() {
        return usuarioRepository.listAll();
    }

    @Transactional
    public Usuario actualizar(Integer id, Usuario dto) {
        Usuario existente = obtener(id);

        if (!existente.getEmail().equals(dto.getEmail())
                && usuarioRepository.existePorEmail(dto.getEmail())) {
            throw new RuntimeException("El email ya esta registrado por otro usuario");
        }

        existente.setNombre(dto.getNombre());
        existente.setApellido(dto.getApellido());
        existente.setEmail(dto.getEmail());
        existente.setRol(dto.getRol());
        existente.setActivo(dto.getActivo());

        if (dto.getPasswordHash() != null && !dto.getPasswordHash().isEmpty()) {
            existente.setPasswordHash(dto.getPasswordHash());
        }

        usuarioRepository.persist(existente);
        return existente;
    }

    @Transactional
    public void eliminar(Integer id) {
        Usuario usuario = obtener(id);
        usuarioRepository.delete(usuario);
    }

    private void validar(Usuario usuario) {
        if (usuario == null) {
            throw new RuntimeException("Los datos del usuario son obligatorios");
        }
        if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre es obligatorio");
        }
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new RuntimeException("El email es obligatorio");
        }
        if (usuario.getPasswordHash() == null || usuario.getPasswordHash().isEmpty()) {
            throw new RuntimeException("La contrasena es obligatoria");
        }
        if (usuario.getRol() == null) {
            throw new RuntimeException("El rol es obligatorio");
        }
    }
}
