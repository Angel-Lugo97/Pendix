package com.mx.tecdesoftware.Pendix.web.dto.project;

import com.mx.tecdesoftware.Pendix.domain.Project;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

@Schema(
        name = "ProjectRequest",
        description = "Datos para registrar un proyecto y, opcionalmente, sus tareas en cascada."
)
public record ProjectRequest(
        @NotNull(message = "ownerId es obligatorio")
        @Positive(message = "ownerId debe ser mayor que cero")
        Integer ownerId,

        @NotBlank(message = "El nombre del proyecto es obligatorio")
        @Size(max = 255, message = "El nombre no puede superar 255 caracteres")
        String name,

        @Size(max = 255, message = "La descripción no puede superar 255 caracteres")
        String description,

        @NotNull(message = "La fecha de inicio es obligatoria")
        LocalDateTime startDate,

        @NotNull(message = "La fecha límite es obligatoria")
        LocalDateTime dueDate,

        @NotBlank(message = "El estado es obligatorio")
        String state,

        List<@Valid ProjectTaskRequest> tasks
) {

    public Project toDomain() {
        Project project = new Project();
        project.setOwnerId(ownerId);
        project.setName(name);
        project.setDescription(description);
        project.setStartDate(startDate);
        project.setDueDate(dueDate);
        project.setState(state);
        project.setTasks(
                tasks == null
                        ? List.of()
                        : tasks.stream()
                                .map(ProjectTaskRequest::toDomain)
                                .toList()
        );
        return project;
    }
}
