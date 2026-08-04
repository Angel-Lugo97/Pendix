package com.mx.tecdesoftware.Pendix.web.dto.reminder;

import com.mx.tecdesoftware.Pendix.domain.Reminder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Schema(name = "ReminderRequest", description = "Datos necesarios para registrar un recordatorio.")
public record ReminderRequest(
        @NotNull(message = "taskId es obligatorio")
        @Positive(message = "taskId debe ser mayor que cero")
        Integer taskId,

        @NotNull(message = "La fecha del recordatorio es obligatoria")
        LocalDateTime reminderDate,

        @NotBlank(message = "El mensaje es obligatorio")
        @Size(max = 255, message = "El mensaje no puede superar 255 caracteres")
        String message,

        Boolean sent
) {

    public Reminder toDomain() {
        Reminder reminder = new Reminder();
        reminder.setTaskId(taskId);
        reminder.setReminderDate(reminderDate);
        reminder.setMessage(message);
        reminder.setSent(sent == null ? false : sent);
        return reminder;
    }
}
