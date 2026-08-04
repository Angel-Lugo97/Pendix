package com.mx.tecdesoftware.Pendix.domain.service;

import com.mx.tecdesoftware.Pendix.domain.Project;
import com.mx.tecdesoftware.Pendix.domain.Task;
import com.mx.tecdesoftware.Pendix.domain.exception.BusinessRuleException;
import com.mx.tecdesoftware.Pendix.domain.exception.ResourceNotFoundException;
import com.mx.tecdesoftware.Pendix.domain.repository.ProjectRepository;
import com.mx.tecdesoftware.Pendix.domain.repository.UserAuthRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserAuthRepository userAuthRepository;

    public ProjectService(
            ProjectRepository projectRepository,
            UserAuthRepository userAuthRepository
    ) {
        this.projectRepository = projectRepository;
        this.userAuthRepository = userAuthRepository;
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
        if (!userAuthRepository.existsById(project.getOwnerId())) {
            throw new ResourceNotFoundException(
                    "No existe el usuario propietario con ID " + project.getOwnerId()
            );
        }

        validateDates(project.getStartDate(), project.getDueDate(), "proyecto");

        if (project.getTasks() != null) {
            for (Task task : project.getTasks()) {
                validateDates(task.getCreationDate(), task.getDueDate(), "tarea");

                if (task.getAssignedUserId() != null
                        && !userAuthRepository.existsById(task.getAssignedUserId())) {
                    throw new ResourceNotFoundException(
                            "No existe el usuario asignado con ID " + task.getAssignedUserId()
                    );
                }
            }
        }

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

    private void validateDates(
            java.time.LocalDateTime start,
            java.time.LocalDateTime end,
            String resourceName
    ) {
        if (start != null && end != null && end.isBefore(start)) {
            throw new BusinessRuleException(
                    "La fecha límite del " + resourceName
                            + " no puede ser anterior a la fecha inicial"
            );
        }
    }
}
