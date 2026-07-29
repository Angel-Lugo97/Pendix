package com.mx.tecdesoftware.Pendix.domain.service;

import com.mx.tecdesoftware.Pendix.domain.Project;
import com.mx.tecdesoftware.Pendix.domain.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<Project> getAll() {
        return projectRepository.getAll();
    }

    public Optional<Project> getById(Integer projectId) {
        return projectRepository.getById(projectId);
    }

    public List<Project> getByOwnerId(Integer ownerId) {
        return projectRepository.getByOwnerId(ownerId);
    }

    public Project save(Project project) {
        return projectRepository.save(project);
    }

    public boolean delete(Integer projectId) {
        Optional<Project> project = projectRepository.getById(projectId);

        if (project.isEmpty()) {
            return false;
        }

        projectRepository.deleteById(projectId);
        return true;
    }
}
