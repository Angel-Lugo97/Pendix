package com.mx.tecdesoftware.Pendix.persistence.repository;

import com.mx.tecdesoftware.Pendix.domain.Project;
import com.mx.tecdesoftware.Pendix.domain.repository.ProjectRepository;
import com.mx.tecdesoftware.Pendix.persistence.crud.ProyectoCrudRepository;
import com.mx.tecdesoftware.Pendix.persistence.entity.Proyecto;
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

        proyectoCrudRepository
                .findAll()
                .forEach(proyectos::add);

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
        List<Proyecto> proyectos =
                proyectoCrudRepository.findByIdUsuarioPropietario(ownerId);

        return projectMapper.toProjects(proyectos);
    }

    @Override
    public Project save(Project project) {
        Proyecto proyecto = projectMapper.toEntity(project);
        Proyecto proyectoGuardado =
                proyectoCrudRepository.save(proyecto);

        return projectMapper.toProject(proyectoGuardado);
    }

    @Override
    public void deleteById(Integer projectId) {
        proyectoCrudRepository.deleteById(projectId);
    }
}
