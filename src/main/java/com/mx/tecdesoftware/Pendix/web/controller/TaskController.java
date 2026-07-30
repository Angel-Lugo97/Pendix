package com.mx.tecdesoftware.Pendix.web.controller;

import com.mx.tecdesoftware.Pendix.domain.Task;
import com.mx.tecdesoftware.Pendix.domain.service.TaskService;
import com.mx.tecdesoftware.Pendix.web.dto.task.TaskRequest;
import com.mx.tecdesoftware.Pendix.web.dto.task.TaskResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@Tag(
        name = "Tasks",
        description = "Administración de tareas pertenecientes a los proyectos."
)
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @Operation(
            summary = "Consultar todas las tareas",
            description = "Devuelve todas las tareas junto con sus recordatorios."
    )
    public ResponseEntity<List<TaskResponse>> getAll() {
        List<TaskResponse> tasks = taskService.getAll()
                .stream()
                .map(TaskResponse::from)
                .toList();

        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Consultar una tarea por ID",
            description = "Busca una tarea específica utilizando su identificador."
    )
    public ResponseEntity<TaskResponse> getById(
            @Parameter(
                    description = "Identificador de la tarea",
                    example = "1"
            )
            @PathVariable Integer id
    ) {
        return taskService.getById(id)
                .map(TaskResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(params = "projectId")
    @Operation(
            summary = "Consultar tareas por proyecto",
            description = "Devuelve todas las tareas pertenecientes a un proyecto."
    )
    public ResponseEntity<List<TaskResponse>> getByProjectId(
            @Parameter(
                    description = "Identificador del proyecto",
                    example = "1"
            )
            @RequestParam Integer projectId
    ) {
        List<TaskResponse> tasks =
                taskService.getByProjectId(projectId)
                        .stream()
                        .map(TaskResponse::from)
                        .toList();

        return ResponseEntity.ok(tasks);
    }

    @PostMapping
    @Operation(
            summary = "Registrar una tarea",
            description = "Crea una tarea nueva dentro de un proyecto."
    )
    public ResponseEntity<TaskResponse> save(
            @RequestBody TaskRequest request
    ) {
        Task savedTask = taskService.save(
                request.toDomain()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(TaskResponse.from(savedTask));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar una tarea",
            description = "Elimina una tarea y sus recordatorios relacionados."
    )
    public ResponseEntity<Void> delete(
            @Parameter(
                    description = "Identificador de la tarea",
                    example = "1"
            )
            @PathVariable Integer id
    ) {
        boolean deleted = taskService.delete(id);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
