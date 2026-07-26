package com.mx.tecdesoftware.Pendix.persistence.crud;

import com.mx.tecdesoftware.Pendix.persistence.entity.Proyecto;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProyectoCrudRepository
        extends CrudRepository<Proyecto, Integer> {

    List<Proyecto> findByIdUsuarioPropietario(
            Integer idUsuarioPropietario
    );
}
