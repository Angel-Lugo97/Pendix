package com.mx.tecdesoftware.Pendix.persistence.repository;

import com.mx.tecdesoftware.Pendix.domain.Task;
import com.mx.tecdesoftware.Pendix.domain.repository.TaskRepository;
import com.mx.tecdesoftware.Pendix.persistence.crud.TareaCrudRepository;
import com.mx.tecdesoftware.Pendix.persistence.entity.Tarea;
import com.mx.tecdesoftware.Pendix.persistence.mapper.TaskMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class TaskRepositoryImpl implements TaskRepository {

    private final TareaCrudRepository tareaCrudRepository;
    private final TaskMapper taskMapper;

    public TaskRepositoryImpl(
            TareaCrudRepository tareaCrudRepository,
            TaskMapper taskMapper
    ) {
        this.tareaCrudRepository = tareaCrudRepository;
        this.taskMapper = taskMapper;
    }

    @Override
    public List<Task> getAll() {
        List<Tarea> tareas = new ArrayList<>();

        tareaCrudRepository
                .findAll()
                .forEach(tareas::add);

        return taskMapper.toTasks(tareas);
    }

    @Override
    public Optional<Task> getById(Integer taskId) {
        return tareaCrudRepository
                .findById(taskId)
                .map(taskMapper::toTask);
    }

    @Override
    public List<Task> getByProjectId(Integer projectId) {
        List<Tarea> tareas =
                tareaCrudRepository.findByIdProyecto(projectId);

        return taskMapper.toTasks(tareas);
    }

    @Override
    public Task save(Task task) {
        Tarea tarea = taskMapper.toEntity(task);
        Tarea tareaGuardada =
                tareaCrudRepository.save(tarea);

        return taskMapper.toTask(tareaGuardada);
    }

    @Override
    public void deleteById(Integer taskId) {
        tareaCrudRepository.deleteById(taskId);
    }
}
