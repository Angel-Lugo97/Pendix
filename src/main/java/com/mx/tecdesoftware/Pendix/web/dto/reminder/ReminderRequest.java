package com.mx.tecdesoftware.Pendix.web.dto.reminder;

import com.mx.tecdesoftware.Pendix.domain.Reminder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(
        name = "ReminderRequest",
        description = "Datos necesarios para registrar un recordatorio.",
        example = """
                {
                  "taskId": 5,
                  "reminderDate": "2026-07-30T17:30:00",
                  "message": "Grabar la evidencia del funcionamiento de JWT",
                  "sent": false
                }
                """
)
public record ReminderRequest(
        Integer taskId,
        LocalDateTime reminderDate,
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
