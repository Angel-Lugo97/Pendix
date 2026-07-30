package com.mx.tecdesoftware.Pendix.web.dto.project;

import com.mx.tecdesoftware.Pendix.domain.Project;
import com.mx.tecdesoftware.Pendix.web.dto.task.TaskResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(
        name = "ProjectResponse",
        description = "Información completa de un proyecto y sus tareas.",
        example = """
                {
                  "projectId": 1,
                  "ownerId": 1,
                  "name": "Desarrollo de Pendix",
                  "description": "Aplicación para administrar proyectos, tareas y recordatorios",
                  "startDate": "2026-07-01T08:00:00",
                  "dueDate": "2026-09-30T23:59:00",
                  "state": "EN_PROGRESO",
                  "tasks": [
                    {
                      "taskId": 5,
                      "projectId": 1,
                      "assignedUserId": 1,
                      "title": "Grabar evidencia de autenticación",
                      "description": "Probar el login y los endpoints protegidos",
                      "creationDate": "2026-07-30T10:00:00",
                      "dueDate": "2026-07-30T18:00:00",
                      "priority": "ALTA",
                      "state": "EN_PROGRESO",
                      "reminders": []
                    }
                  ]
                }
                """
)
public record ProjectResponse(
        Integer projectId,
        Integer ownerId,
        String name,
        String description,
        LocalDateTime startDate,
        LocalDateTime dueDate,
        String state,
        List<TaskResponse> tasks
) {

    public static ProjectResponse from(Project project) {
        List<TaskResponse> taskResponses =
                project.getTasks() == null
                        ? List.of()
                        : project.getTasks()
                                .stream()
                                .map(TaskResponse::from)
                                .toList();

        return new ProjectResponse(
                project.getProjectId(),
                project.getOwnerId(),
                project.getName(),
                project.getDescription(),
                project.getStartDate(),
                project.getDueDate(),
                project.getState(),
                taskResponses
        );
    }
}
