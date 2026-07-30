package com.mx.tecdesoftware.Pendix.web.controller;

import com.mx.tecdesoftware.Pendix.domain.service.ReminderService;
import com.mx.tecdesoftware.Pendix.web.dto.reminder.ReminderRequest;
import com.mx.tecdesoftware.Pendix.web.dto.reminder.ReminderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reminders")
@Tag(
        name = "Reminders",
        description = "Administración de recordatorios asociados con tareas."
)
public class ReminderController {

    private final ReminderService reminderService;

    public ReminderController(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    @GetMapping
    @Operation(
            summary = "Consultar todos los recordatorios",
            description = "Devuelve todos los recordatorios registrados."
    )
    public ResponseEntity<List<ReminderResponse>> getAll() {
        List<ReminderResponse> reminders =
                reminderService.getAll()
                        .stream()
                        .map(ReminderResponse::from)
                        .toList();

        return ResponseEntity.ok(reminders);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Consultar un recordatorio por ID",
            description = "Busca un recordatorio utilizando su identificador."
    )
    public ResponseEntity<ReminderResponse> getById(
            @Parameter(
                    description = "Identificador del recordatorio",
                    example = "1"
            )
            @PathVariable Integer id
    ) {
        return reminderService.getById(id)
                .map(ReminderResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(params = "taskId")
    @Operation(
            summary = "Consultar recordatorios por tarea",
            description = "Devuelve los recordatorios pertenecientes a una tarea."
    )
    public ResponseEntity<List<ReminderResponse>> getByTaskId(
            @Parameter(
                    description = "Identificador de la tarea",
                    example = "1"
            )
            @RequestParam Integer taskId
    ) {
        List<ReminderResponse> reminders =
                reminderService.getByTaskId(taskId)
                        .stream()
                        .map(ReminderResponse::from)
                        .toList();

        return ResponseEntity.ok(reminders);
    }

    @PostMapping
    @Operation(
            summary = "Registrar un recordatorio",
            description = "Crea un recordatorio nuevo para una tarea existente."
    )
    public ResponseEntity<ReminderResponse> save(
            @RequestBody ReminderRequest request
    ) {
        return reminderService.save(request.toDomain())
                .map(ReminderResponse::from)
                .map(savedReminder -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(savedReminder))
                .orElseGet(() -> ResponseEntity
                        .badRequest()
                        .build());
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar un recordatorio",
            description = "Elimina un recordatorio utilizando su identificador."
    )
    public ResponseEntity<Void> delete(
            @Parameter(
                    description = "Identificador del recordatorio",
                    example = "1"
            )
            @PathVariable Integer id
    ) {
        boolean deleted = reminderService.delete(id);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
