package com.mx.tecdesoftware.Pendix.persistence.crud;

import com.mx.tecdesoftware.Pendix.persistence.entity.Tarea;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TareaCrudRepository
        extends CrudRepository<Tarea, Integer> {

    List<Tarea> findByIdProyecto(Integer idProyecto);
}
