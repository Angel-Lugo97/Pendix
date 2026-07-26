package com.mx.tecdesoftware.Pendix.domain.repository;

import com.mx.tecdesoftware.Pendix.domain.Task;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {

    List<Task> getAll();

    Optional<Task> getById(Integer taskId);

    List<Task> getByProjectId(Integer projectId);

    Task save(Task task);

    void deleteById(Integer taskId);
}
