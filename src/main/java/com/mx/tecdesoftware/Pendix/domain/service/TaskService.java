package com.mx.tecdesoftware.Pendix.domain.service;

import com.mx.tecdesoftware.Pendix.domain.Task;
import com.mx.tecdesoftware.Pendix.domain.exception.BusinessRuleException;
import com.mx.tecdesoftware.Pendix.domain.exception.ResourceNotFoundException;
import com.mx.tecdesoftware.Pendix.domain.repository.ProjectRepository;
import com.mx.tecdesoftware.Pendix.domain.repository.TaskRepository;
import com.mx.tecdesoftware.Pendix.domain.repository.UserAuthRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserAuthRepository userAuthRepository;

    public TaskService(
            TaskRepository taskRepository,
            ProjectRepository projectRepository,
            UserAuthRepository userAuthRepository
    ) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userAuthRepository = userAuthRepository;
    }

    public List<Task> getAll() {
        return taskRepository.getAll();
    }

    public Optional<Task> getById(Integer taskId) {
        return taskRepository.getById(taskId);
    }

    public List<Task> getByProjectId(Integer projectId) {
        return taskRepository.getByProjectId(projectId);
    }

    public Task save(Task task) {
        if (projectRepository.getById(task.getProjectId()).isEmpty()) {
            throw new ResourceNotFoundException(
                    "No existe el proyecto con ID " + task.getProjectId()
            );
        }

        if (task.getAssignedUserId() != null
                && !userAuthRepository.existsById(task.getAssignedUserId())) {
            throw new ResourceNotFoundException(
                    "No existe el usuario asignado con ID " + task.getAssignedUserId()
            );
        }

        if (task.getCreationDate() != null
                && task.getDueDate() != null
                && task.getDueDate().isBefore(task.getCreationDate())) {
            throw new BusinessRuleException(
                    "La fecha límite de la tarea no puede ser anterior a la fecha de creación"
            );
        }

        return taskRepository.save(task);
    }

    public boolean delete(Integer taskId) {
        Optional<Task> task = taskRepository.getById(taskId);

        if (task.isEmpty()) {
            return false;
        }

        taskRepository.deleteById(taskId);
        return true;
    }
}
