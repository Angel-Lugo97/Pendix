package com.mx.tecdesoftware.Pendix.persistence.mapper;

import com.mx.tecdesoftware.Pendix.domain.Project;
import com.mx.tecdesoftware.Pendix.persistence.entity.Proyecto;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = TaskMapper.class
)
public interface ProjectMapper {

    @Mapping(source = "idProyecto", target = "projectId")
    @Mapping(
            source = "idUsuarioPropietario",
            target = "ownerId"
    )
    @Mapping(source = "nombre", target = "name")
    @Mapping(source = "descripcion", target = "description")
    @Mapping(source = "fechaInicio", target = "startDate")
    @Mapping(source = "fechaLimite", target = "dueDate")
    @Mapping(source = "estado", target = "state")
    @Mapping(source = "tareas", target = "tasks")
    Project toProject(Proyecto proyecto);

    List<Project> toProjects(List<Proyecto> proyectos);

    @InheritInverseConfiguration(name = "toProject")
    @Mapping(target = "usuarioPropietario", ignore = true)
    Proyecto toEntity(Project project);

    List<Proyecto> toEntities(List<Project> projects);
}
