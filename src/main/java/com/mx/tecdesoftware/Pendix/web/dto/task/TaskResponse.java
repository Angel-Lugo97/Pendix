package com.mx.tecdesoftware.Pendix.web.dto.task;

import com.mx.tecdesoftware.Pendix.domain.Task;
import com.mx.tecdesoftware.Pendix.web.dto.reminder.ReminderResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(
        name = "TaskResponse",
        description = "Información completa de una tarea.",
        example = """
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
                  "reminders": [
                    {
                      "reminderId": 1,
                      "taskId": 5,
                      "reminderDate": "2026-07-30T17:30:00",
                      "message": "Grabar la evidencia del funcionamiento de JWT",
                      "sent": false
                    }
                  ]
                }
                """
)
public record TaskResponse(
        Integer taskId,
        Integer projectId,
        Integer assignedUserId,
        String title,
        String description,
        LocalDateTime creationDate,
        LocalDateTime dueDate,
        String priority,
        String state,
        List<ReminderResponse> reminders
) {

    public static TaskResponse from(Task task) {
        List<ReminderResponse> reminderResponses =
                task.getReminders() == null
                        ? List.of()
                        : task.getReminders()
                                .stream()
                                .map(ReminderResponse::from)
                                .toList();

        return new TaskResponse(
                task.getTaskId(),
                task.getProjectId(),
                task.getAssignedUserId(),
                task.getTitle(),
                task.getDescription(),
                task.getCreationDate(),
                task.getDueDate(),
                task.getPriority(),
                task.getState(),
                reminderResponses
        );
    }
}
