package com.mx.tecdesoftware.Pendix.web.dto.reminder;

import com.mx.tecdesoftware.Pendix.domain.Reminder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(
        name = "ReminderResponse",
        description = "Información de un recordatorio registrado.",
        example = """
                {
                  "reminderId": 1,
                  "taskId": 5,
                  "reminderDate": "2026-07-30T17:30:00",
                  "message": "Grabar la evidencia del funcionamiento de JWT",
                  "sent": false
                }
                """
)
public record ReminderResponse(
        Integer reminderId,
        Integer taskId,
        LocalDateTime reminderDate,
        String message,
        Boolean sent
) {

    public static ReminderResponse from(Reminder reminder) {
        return new ReminderResponse(
                reminder.getReminderId(),
                reminder.getTaskId(),
                reminder.getReminderDate(),
                reminder.getMessage(),
                reminder.getSent()
        );
    }
}
