package com.mx.tecdesoftware.Pendix.web.controller;

import com.mx.tecdesoftware.Pendix.domain.Project;
import com.mx.tecdesoftware.Pendix.domain.service.ProjectService;
import com.mx.tecdesoftware.Pendix.web.dto.project.ProjectRequest;
import com.mx.tecdesoftware.Pendix.web.dto.project.ProjectResponse;
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
@RequestMapping("/projects")
@Tag(
        name = "Projects",
        description = "Administración de proyectos registrados en Pendix."
)
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
    public ResponseEntity<ProjectResponse> getById(
            @Parameter(
                    description = "Identificador del proyecto",
                    example = "1"
            )
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
    public ResponseEntity<List<ProjectResponse>> getByOwnerId(
            @Parameter(
                    description = "Identificador del usuario propietario",
                    example = "1"
            )
            @RequestParam Integer ownerId
    ) {
        List<ProjectResponse> projects =
                projectService.getByOwnerId(ownerId)
                        .stream()
                        .map(ProjectResponse::from)
                        .toList();

        return ResponseEntity.ok(projects);
    }

    @PostMapping
    @Operation(
            summary = "Registrar un proyecto",
            description = "Crea un proyecto nuevo. El ID y la lista de tareas son generados por el sistema."
    )
    public ResponseEntity<ProjectResponse> save(
            @RequestBody ProjectRequest request
    ) {
        Project savedProject = projectService.save(
                request.toDomain()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ProjectResponse.from(savedProject));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar un proyecto",
            description = "Elimina un proyecto utilizando su identificador."
    )
    public ResponseEntity<Void> delete(
            @Parameter(
                    description = "Identificador del proyecto",
                    example = "1"
            )
            @PathVariable Integer id
    ) {
        boolean deleted = projectService.delete(id);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
