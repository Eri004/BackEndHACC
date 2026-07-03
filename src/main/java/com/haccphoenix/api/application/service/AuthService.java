package com.haccphoenix.api.application.service;

import com.haccphoenix.api.domain.model.Usuario;
import com.haccphoenix.api.domain.repository.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AuthService {

    @Inject
    UsuarioRepository usuarioRepository;

    public static class AuthResponse {
        public Integer id;
        public String nombre;
        public String apellido;
        public String rol;
        public String email;

        public AuthResponse(Integer id, String nombre, String apellido, String rol, String email) {
            this.id = id;
            this.nombre = nombre;
            this.apellido = apellido;
            this.rol = rol;
            this.email = email;
        }
    }

    public AuthResponse autenticar(String email, String passwordPlain) {
        Usuario usuario = usuarioRepository.findByEmailOptional(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (Boolean.FALSE.equals(usuario.getActivo())) {
            throw new RuntimeException("Usuario inactivo");
        }

        if (!passwordPlain.equals(usuario.getPasswordHash())) {
            throw new RuntimeException("Credenciales invalidas");
        }

        return new AuthResponse(
            usuario.getId(),
            usuario.getNombre(),
            usuario.getApellido(),
            usuario.getRol().name(),
            usuario.getEmail()
        );
    }
}
