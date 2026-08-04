package com.mx.tecdesoftware.Pendix.web.dto.project;

import com.mx.tecdesoftware.Pendix.domain.Task;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Schema(description = "Tarea que será persistida junto con el proyecto maestro")
public record ProjectTaskRequest(
        @Positive(message = "assignedUserId debe ser mayor que cero")
        Integer assignedUserId,

        @NotBlank(message = "El título de la tarea es obligatorio")
        @Size(max = 255, message = "El título no puede superar 255 caracteres")
        String title,

        @Size(max = 255, message = "La descripción no puede superar 255 caracteres")
        String description,

        @NotNull(message = "La fecha de creación es obligatoria")
        LocalDateTime creationDate,

        @NotNull(message = "La fecha límite es obligatoria")
        LocalDateTime dueDate,

        @NotBlank(message = "La prioridad es obligatoria")
        String priority,

        @NotBlank(message = "El estado es obligatorio")
        String state
) {

    public Task toDomain() {
        Task task = new Task();
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
