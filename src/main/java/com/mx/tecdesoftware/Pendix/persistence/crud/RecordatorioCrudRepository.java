package com.mx.tecdesoftware.Pendix.persistence.crud;

import com.mx.tecdesoftware.Pendix.persistence.entity.Recordatorio;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RecordatorioCrudRepository
        extends CrudRepository<Recordatorio, Integer> {

    List<Recordatorio> findByIdTarea(Integer idTarea);
}
