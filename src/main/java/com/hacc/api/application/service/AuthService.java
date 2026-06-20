package com.hacc.api.application.service;

import com.hacc.api.domain.model.Propietario;
import com.hacc.api.domain.model.Residente;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

@ApplicationScoped
public class AuthService {
    
    @Inject
    EntityManager em; 

    public static class AuthResponse {
        public Integer id;
        public String nombre;
        public String apellido;
        public String rol;
        public String email;
        public String telefono;
        public String cedula;
        
        public AuthResponse(Integer id, String nombre, String apellido, String rol, String email, String telefono, String cedula) {
            this.id = id;
            this.nombre = nombre;
            this.apellido = apellido;
            this.rol = rol;
            this.email = email;
            this.telefono=telefono;
            this.cedula=cedula;
        }
    }
    
    public AuthResponse autenticar(String email, String passwordPlain) {        
        try {
            Propietario propietario = em.createQuery(
                "SELECT p FROM Propietario p WHERE p.email = :email", 
                Propietario.class)
                .setParameter("email", email)
                .getSingleResult();
            if (!passwordPlain.equals(propietario.getContrasena())) {
                throw new RuntimeException("Credenciales inválidas");
            }
            
            return new AuthResponse(
                propietario.getId_propietario(),
                propietario.getNombre(),
                propietario.getApellido(),
                "propietario",
                propietario.getEmail(),
                propietario.getTelefono(),
                propietario.getCedula()
            );
            
        } catch (NoResultException e1) {
            // 2. Buscar en residentes
            try {
                Residente residente = em.createQuery(
                    "SELECT r FROM Residente r WHERE r.email = :email", 
                    Residente.class)
                    .setParameter("email", email)
                    .getSingleResult();
                
                if (!passwordPlain.equals(residente.getContrasena())) {
                    throw new RuntimeException("Credenciales inválidas");
                }
                
                return new AuthResponse(
                    residente.getId_residente(),
                    residente.getNombre(),
                    residente.getApellido(),
                    "residente",
                    residente.getEmail(),
                    residente.getTelefono(),
                    residente.getCedula()
                );
                
            } catch (NoResultException e2) {
                throw new RuntimeException("Usuario no encontrado");
            }
        }
    }
}