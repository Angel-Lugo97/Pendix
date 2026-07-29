package com.mx.tecdesoftware.Pendix.domain.service;

import com.mx.tecdesoftware.Pendix.domain.Task;
import com.mx.tecdesoftware.Pendix.domain.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
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
