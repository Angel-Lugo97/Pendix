package com.mx.tecdesoftware.Pendix.persistence.crud;

import com.mx.tecdesoftware.Pendix.persistence.entity.Usuario;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UsuarioCrudRepository
        extends CrudRepository<Usuario, Integer> {

    Optional<Usuario> findByCorreoIgnoreCase(String correo);
}
