package com.mx.tecdesoftware.Pendix.web.controller;

import com.mx.tecdesoftware.Pendix.domain.Task;
import com.mx.tecdesoftware.Pendix.domain.service.TaskService;
import com.mx.tecdesoftware.Pendix.web.config.OpenApiExamples;
import com.mx.tecdesoftware.Pendix.web.dto.auth.AuthErrorResponse;
import com.mx.tecdesoftware.Pendix.web.dto.task.TaskRequest;
import com.mx.tecdesoftware.Pendix.web.dto.task.TaskResponse;
import com.mx.tecdesoftware.Pendix.web.exception.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@Tag(name = "Tasks", description = "Administración de tareas pertenecientes a los proyectos.")
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
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Tareas consultadas correctamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TaskResponse.class)))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "JWT ausente o inválido",
                    content = @Content(schema = @Schema(implementation = AuthErrorResponse.class))
            )
    })
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
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Tarea encontrada",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Tarea no encontrada", content = @Content),
            @ApiResponse(
                    responseCode = "401",
                    description = "JWT ausente o inválido",
                    content = @Content(schema = @Schema(implementation = AuthErrorResponse.class))
            )
    })
    public ResponseEntity<TaskResponse> getById(
            @Parameter(description = "Identificador de la tarea", example = "1", required = true)
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
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Consulta realizada correctamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TaskResponse.class)))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "JWT ausente o inválido",
                    content = @Content(schema = @Schema(implementation = AuthErrorResponse.class))
            )
    })
    public ResponseEntity<List<TaskResponse>> getByProjectId(
            @Parameter(description = "Identificador del proyecto", example = "1", required = true)
            @RequestParam Integer projectId
    ) {
        List<TaskResponse> tasks = taskService.getByProjectId(projectId)
                .stream()
                .map(TaskResponse::from)
                .toList();
        return ResponseEntity.ok(tasks);
    }

    @PostMapping
    @Operation(
            summary = "Registrar una tarea",
            description = "Crea una tarea nueva dentro de un proyecto existente."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Tarea creada correctamente",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o fechas incorrectas",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Proyecto o usuario asignado no encontrado",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "JWT ausente o inválido",
                    content = @Content(schema = @Schema(implementation = AuthErrorResponse.class))
            )
    })
    public ResponseEntity<TaskResponse> save(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Datos de la tarea",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TaskRequest.class),
                            examples = @ExampleObject(
                                    name = "tareaValida",
                                    summary = "Tarea asociada con un proyecto",
                                    value = OpenApiExamples.TASK_CREATE
                            )
                    )
            )
            @Valid
            @org.springframework.web.bind.annotation.RequestBody
            TaskRequest request
    ) {
        Task savedTask = taskService.save(request.toDomain());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(TaskResponse.from(savedTask));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar una tarea",
            description = "Elimina una tarea y sus recordatorios relacionados."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tarea eliminada", content = @Content),
            @ApiResponse(responseCode = "404", description = "Tarea no encontrada", content = @Content),
            @ApiResponse(
                    responseCode = "401",
                    description = "JWT ausente o inválido",
                    content = @Content(schema = @Schema(implementation = AuthErrorResponse.class))
            )
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Identificador de la tarea", example = "1", required = true)
            @PathVariable Integer id
    ) {
        if (!taskService.delete(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
