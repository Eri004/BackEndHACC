package com.haccphoenix.api.domain.repository;

import com.haccphoenix.api.domain.model.Usuario;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class UsuarioRepository implements PanacheRepositoryBase<Usuario, Integer> {

    public Optional<Usuario> findByEmailOptional(String email) {
        return find("email", email).firstResultOptional();
    }

    public boolean existePorEmail(String email) {
        return count("email", email) > 0;
    }
}
