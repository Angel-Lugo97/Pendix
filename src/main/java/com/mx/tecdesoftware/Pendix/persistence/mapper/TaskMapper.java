package com.mx.tecdesoftware.Pendix.persistence.mapper;

import com.mx.tecdesoftware.Pendix.domain.Task;
import com.mx.tecdesoftware.Pendix.persistence.entity.Tarea;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(source = "idTarea", target = "taskId")
    @Mapping(source = "idProyecto", target = "projectId")
    @Mapping(
            source = "idUsuarioAsignado",
            target = "assignedUserId"
    )
    @Mapping(source = "titulo", target = "title")
    @Mapping(source = "descripcion", target = "description")
    @Mapping(source = "fechaCreacion", target = "creationDate")
    @Mapping(source = "fechaLimite", target = "dueDate")
    @Mapping(source = "prioridad", target = "priority")
    @Mapping(source = "estado", target = "state")
    Task toTask(Tarea tarea);

    List<Task> toTasks(List<Tarea> tareas);

    @InheritInverseConfiguration(name = "toTask")
    @Mapping(target = "proyecto", ignore = true)
    @Mapping(target = "usuarioAsignado", ignore = true)
    Tarea toEntity(Task task);

    List<Tarea> toEntities(List<Task> tasks);
}
