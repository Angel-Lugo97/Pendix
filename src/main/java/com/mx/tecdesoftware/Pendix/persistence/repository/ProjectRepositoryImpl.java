package com.mx.tecdesoftware.Pendix.persistence.repository;

import com.mx.tecdesoftware.Pendix.domain.Project;
import com.mx.tecdesoftware.Pendix.domain.repository.ProjectRepository;
import com.mx.tecdesoftware.Pendix.persistence.crud.ProyectoCrudRepository;
import com.mx.tecdesoftware.Pendix.persistence.entity.Proyecto;
import com.mx.tecdesoftware.Pendix.persistence.entity.Tarea;
import com.mx.tecdesoftware.Pendix.persistence.mapper.ProjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class ProjectRepositoryImpl implements ProjectRepository {

    private final ProyectoCrudRepository proyectoCrudRepository;
    private final ProjectMapper projectMapper;

    public ProjectRepositoryImpl(
            ProyectoCrudRepository proyectoCrudRepository,
            ProjectMapper projectMapper
    ) {
        this.proyectoCrudRepository = proyectoCrudRepository;
        this.projectMapper = projectMapper;
    }

    @Override
    public List<Project> getAll() {
        List<Proyecto> proyectos = new ArrayList<>();
        proyectoCrudRepository.findAll().forEach(proyectos::add);
        return projectMapper.toProjects(proyectos);
    }

    @Override
    public Optional<Project> getById(Integer projectId) {
        return proyectoCrudRepository
                .findById(projectId)
                .map(projectMapper::toProject);
    }

    @Override
    public List<Project> getByOwnerId(Integer ownerId) {
        return projectMapper.toProjects(
                proyectoCrudRepository.findByIdUsuarioPropietario(ownerId)
        );
    }

    @Override
    public Project save(Project project) {
        Proyecto proyecto = projectMapper.toEntity(project);

        if (proyecto.getTareas() != null) {
            for (Tarea tarea : proyecto.getTareas()) {
                tarea.setProyecto(proyecto);
            }
        }

        Proyecto savedEntity = proyectoCrudRepository.save(proyecto);
        Project savedProject = projectMapper.toProject(savedEntity);

        if (savedProject.getTasks() != null) {
            savedProject.getTasks().forEach(
                    task -> task.setProjectId(savedProject.getProjectId())
            );
        }

        return savedProject;
    }

    @Override
    public void deleteById(Integer projectId) {
        proyectoCrudRepository.deleteById(projectId);
    }
}
