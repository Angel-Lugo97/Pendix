package com.mx.tecdesoftware.Pendix.web.dto.task;

import com.mx.tecdesoftware.Pendix.domain.Task;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(
        name = "TaskRequest",
        description = "Datos necesarios para registrar una tarea.",
        example = """
                {
                  "projectId": 1,
                  "assignedUserId": 1,
                  "title": "Grabar evidencia de autenticación",
                  "description": "Probar el login y los endpoints protegidos en Swagger",
                  "creationDate": "2026-07-30T10:00:00",
                  "dueDate": "2026-07-30T18:00:00",
                  "priority": "ALTA",
                  "state": "EN_PROGRESO"
                }
                """
)
public record TaskRequest(
        Integer projectId,
        Integer assignedUserId,
        String title,
        String description,
        LocalDateTime creationDate,
        LocalDateTime dueDate,
        String priority,
        String state
) {

    public Task toDomain() {
        Task task = new Task();

        task.setProjectId(projectId);
        task.setAssignedUserId(assignedUserId);
        task.setTitle(title);
        task.setDescription(description);
        task.setCreationDate(creationDate);
        task.setDueDate(dueDate);
        task.setPriority(priority);
        task.setState(state);

        return task;
    }
}
