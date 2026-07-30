package com.mx.tecdesoftware.Pendix.persistence.repository;

import com.mx.tecdesoftware.Pendix.domain.auth.UserCredentials;
import com.mx.tecdesoftware.Pendix.domain.repository.UserAuthRepository;
import com.mx.tecdesoftware.Pendix.persistence.crud.UsuarioCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public class UserAuthRepositoryImpl implements UserAuthRepository {

    private final UsuarioCrudRepository usuarioCrudRepository;

    public UserAuthRepositoryImpl(
            UsuarioCrudRepository usuarioCrudRepository
    ) {
        this.usuarioCrudRepository = usuarioCrudRepository;
    }

    @Override
    public Optional<UserCredentials> findByEmail(String email) {
        return usuarioCrudRepository
                .findByCorreoIgnoreCase(email)
                .map(usuario -> new UserCredentials(
                        usuario.getCorreo(),
                        usuario.getContrasena(),
                        Boolean.TRUE.equals(usuario.getEstado())
                ));
    }
}
