package com.mx.tecdesoftware.Pendix.web.controller;

import com.mx.tecdesoftware.Pendix.domain.Project;
import com.mx.tecdesoftware.Pendix.domain.service.ProjectService;
import com.mx.tecdesoftware.Pendix.web.config.OpenApiExamples;
import com.mx.tecdesoftware.Pendix.web.dto.auth.AuthErrorResponse;
import com.mx.tecdesoftware.Pendix.web.dto.project.ProjectRequest;
import com.mx.tecdesoftware.Pendix.web.dto.project.ProjectResponse;
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
@RequestMapping("/projects")
@Tag(name = "Projects", description = "Administración de proyectos registrados en Pendix.")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    @Operation(
            summary = "Consultar todos los proyectos",
            description = "Devuelve los proyectos registrados junto con sus tareas."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Proyectos consultados correctamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ProjectResponse.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "JWT ausente o inválido",
                    content = @Content(schema = @Schema(implementation = AuthErrorResponse.class))
            )
    })
    public ResponseEntity<List<ProjectResponse>> getAll() {
        List<ProjectResponse> projects = projectService.getAll()
                .stream()
                .map(ProjectResponse::from)
                .toList();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Consultar un proyecto por ID",
            description = "Busca un proyecto específico utilizando su identificador."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Proyecto encontrado",
                    content = @Content(schema = @Schema(implementation = ProjectResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Proyecto no encontrado", content = @Content),
            @ApiResponse(
                    responseCode = "401",
                    description = "JWT ausente o inválido",
                    content = @Content(schema = @Schema(implementation = AuthErrorResponse.class))
            )
    })
    public ResponseEntity<ProjectResponse> getById(
            @Parameter(description = "Identificador del proyecto", example = "1", required = true)
            @PathVariable Integer id
    ) {
        return projectService.getById(id)
                .map(ProjectResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(params = "ownerId")
    @Operation(
            summary = "Consultar proyectos por propietario",
            description = "Devuelve los proyectos pertenecientes a un usuario."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Consulta realizada correctamente",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = ProjectResponse.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "JWT ausente o inválido",
                    content = @Content(schema = @Schema(implementation = AuthErrorResponse.class))
            )
    })
    public ResponseEntity<List<ProjectResponse>> getByOwnerId(
            @Parameter(description = "Identificador del usuario propietario", example = "1", required = true)
            @RequestParam Integer ownerId
    ) {
        List<ProjectResponse> projects = projectService.getByOwnerId(ownerId)
                .stream()
                .map(ProjectResponse::from)
                .toList();
        return ResponseEntity.ok(projects);
    }

    @PostMapping
    @Operation(
            summary = "Registrar un proyecto maestro con tareas detalle",
            description = "Crea un proyecto y persiste automáticamente las tareas incluidas mediante CascadeType.ALL."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Proyecto y tareas creados correctamente",
                    content = @Content(schema = @Schema(implementation = ProjectResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o regla de negocio incumplida",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario propietario o asignado no encontrado",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "JWT ausente o inválido",
                    content = @Content(schema = @Schema(implementation = AuthErrorResponse.class))
            )
    })
    public ResponseEntity<ProjectResponse> save(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Proyecto maestro y lista opcional de tareas detalle",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProjectRequest.class),
                            examples = @ExampleObject(
                                    name = "proyectoValido",
                                    summary = "Proyecto maestro con tareas detalle",
                                    value = OpenApiExamples.PROJECT_CREATE
                            )
                    )
            )
            @Valid
            @org.springframework.web.bind.annotation.RequestBody
            ProjectRequest request
    ) {
        Project savedProject = projectService.save(request.toDomain());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ProjectResponse.from(savedProject));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar un proyecto",
            description = "Elimina un proyecto y sus tareas detalle mediante cascada."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Proyecto eliminado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Proyecto no encontrado", content = @Content),
            @ApiResponse(
                    responseCode = "401",
                    description = "JWT ausente o inválido",
                    content = @Content(schema = @Schema(implementation = AuthErrorResponse.class))
            )
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Identificador del proyecto", example = "1", required = true)
            @PathVariable Integer id
    ) {
        if (!projectService.delete(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
