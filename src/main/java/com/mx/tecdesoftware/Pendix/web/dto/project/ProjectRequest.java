package com.mx.tecdesoftware.Pendix.web.dto.project;

import com.mx.tecdesoftware.Pendix.domain.Project;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(
        name = "ProjectRequest",
        description = "Datos necesarios para registrar un proyecto.",
        example = """
                {
                  "ownerId": 1,
                  "name": "Implementación de seguridad JWT",
                  "description": "Agregar autenticación y proteger los endpoints de Pendix",
                  "startDate": "2026-07-30T09:00:00",
                  "dueDate": "2026-07-31T23:59:00",
                  "state": "EN_PROGRESO"
                }
                """
)
public record ProjectRequest(
        Integer ownerId,
        String name,
        String description,
        LocalDateTime startDate,
        LocalDateTime dueDate,
        String state
) {

    public Project toDomain() {
        Project project = new Project();

        project.setOwnerId(ownerId);
        project.setName(name);
        project.setDescription(description);
        project.setStartDate(startDate);
        project.setDueDate(dueDate);
        project.setState(state);

        return project;
    }
}
