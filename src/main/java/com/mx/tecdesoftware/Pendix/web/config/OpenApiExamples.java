package com.mx.tecdesoftware.Pendix.web.config;

public final class OpenApiExamples {

    public static final String LOGIN = """
            {
              "email": "angel.lugo@pendix.com",
              "password": "1234"
            }
            """;

    public static final String PROJECT_CREATE = """
            {
              "ownerId": 1,
              "name": "Proyecto final Pendix",
              "description": "Proyecto creado junto con sus tareas para demostrar la cascada",
              "startDate": "2026-08-03T09:00:00",
              "dueDate": "2026-08-31T23:59:00",
              "state": "EN_PROGRESO",
              "tasks": [
                {
                  "assignedUserId": 1,
                  "title": "Validar persistencia en cascada",
                  "description": "Comprobar que la tarea se guarda junto con el proyecto",
                  "creationDate": "2026-08-03T09:30:00",
                  "dueDate": "2026-08-10T18:00:00",
                  "priority": "ALTA",
                  "state": "PENDIENTE"
                }
              ]
            }
            """;

    public static final String TASK_CREATE = """
            {
              "projectId": 1,
              "assignedUserId": 1,
              "title": "Grabar evidencia de Swagger",
              "description": "Probar todos los endpoints documentados",
              "creationDate": "2026-08-03T10:00:00",
              "dueDate": "2026-08-10T18:00:00",
              "priority": "ALTA",
              "state": "EN_PROGRESO"
            }
            """;

    public static final String REMINDER_CREATE = """
            {
              "taskId": 1,
              "reminderDate": "2026-08-09T17:30:00",
              "message": "Preparar la evidencia de la tarea",
              "sent": false
            }
            """;

    private OpenApiExamples() {
    }
}
