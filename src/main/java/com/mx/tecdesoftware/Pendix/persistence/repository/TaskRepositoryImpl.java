package com.mx.tecdesoftware.Pendix.persistence.repository;

import com.mx.tecdesoftware.Pendix.domain.Task;
import com.mx.tecdesoftware.Pendix.domain.exception.ResourceNotFoundException;
import com.mx.tecdesoftware.Pendix.domain.repository.TaskRepository;
import com.mx.tecdesoftware.Pendix.persistence.crud.ProyectoCrudRepository;
import com.mx.tecdesoftware.Pendix.persistence.crud.TareaCrudRepository;
import com.mx.tecdesoftware.Pendix.persistence.entity.Proyecto;
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
    private final ProyectoCrudRepository proyectoCrudRepository;
    private final TaskMapper taskMapper;

    public TaskRepositoryImpl(
            TareaCrudRepository tareaCrudRepository,
            ProyectoCrudRepository proyectoCrudRepository,
            TaskMapper taskMapper
    ) {
        this.tareaCrudRepository = tareaCrudRepository;
        this.proyectoCrudRepository = proyectoCrudRepository;
        this.taskMapper = taskMapper;
    }

    @Override
    public List<Task> getAll() {
        List<Tarea> tareas = new ArrayList<>();
        tareaCrudRepository.findAll().forEach(tareas::add);
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
        return taskMapper.toTasks(
                tareaCrudRepository.findByIdProyecto(projectId)
        );
    }

    @Override
    public Task save(Task task) {
        Proyecto proyecto = proyectoCrudRepository
                .findById(task.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe el proyecto con ID " + task.getProjectId()
                ));

        Tarea tarea = taskMapper.toEntity(task);
        tarea.setProyecto(proyecto);

        Tarea savedEntity = tareaCrudRepository.save(tarea);
        Task savedTask = taskMapper.toTask(savedEntity);
        savedTask.setProjectId(task.getProjectId());
        return savedTask;
    }

    @Override
    public void deleteById(Integer taskId) {
        tareaCrudRepository.deleteById(taskId);
    }
}
