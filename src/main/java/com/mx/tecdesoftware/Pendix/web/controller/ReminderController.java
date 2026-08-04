package com.mx.tecdesoftware.Pendix.web.controller;

import com.mx.tecdesoftware.Pendix.domain.Reminder;
import com.mx.tecdesoftware.Pendix.domain.service.ReminderService;
import com.mx.tecdesoftware.Pendix.web.config.OpenApiExamples;
import com.mx.tecdesoftware.Pendix.web.dto.auth.AuthErrorResponse;
import com.mx.tecdesoftware.Pendix.web.dto.reminder.ReminderRequest;
import com.mx.tecdesoftware.Pendix.web.dto.reminder.ReminderResponse;
import com.mx.tecdesoftware.Pendix.web.exception.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reminders")
@Tag(name = "Reminders", description = "Administración de recordatorios asociados con tareas.")
public class ReminderController {

    private final ReminderService reminderService;

    public ReminderController(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    @GetMapping
    @Operation(summary = "Consultar todos los recordatorios", description = "Devuelve todos los recordatorios registrados.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Recordatorios consultados correctamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReminderResponse.class)))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "JWT ausente o inválido",
                    content = @Content(schema = @Schema(implementation = AuthErrorResponse.class))
            )
    })
    public ResponseEntity<List<ReminderResponse>> getAll() {
        List<ReminderResponse> reminders = reminderService.getAll()
                .stream()
                .map(ReminderResponse::from)
                .toList();
        return ResponseEntity.ok(reminders);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar un recordatorio por ID", description = "Busca un recordatorio utilizando su identificador.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Recordatorio encontrado",
                    content = @Content(schema = @Schema(implementation = ReminderResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Recordatorio no encontrado", content = @Content),
            @ApiResponse(
                    responseCode = "401",
                    description = "JWT ausente o inválido",
                    content = @Content(schema = @Schema(implementation = AuthErrorResponse.class))
            )
    })
    public ResponseEntity<ReminderResponse> getById(
            @Parameter(description = "Identificador del recordatorio", example = "1", required = true)
            @PathVariable Integer id
    ) {
        return reminderService.getById(id)
                .map(ReminderResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(params = "taskId")
    @Operation(summary = "Consultar recordatorios por tarea", description = "Devuelve los recordatorios pertenecientes a una tarea.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Consulta realizada correctamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReminderResponse.class)))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "JWT ausente o inválido",
                    content = @Content(schema = @Schema(implementation = AuthErrorResponse.class))
            )
    })
    public ResponseEntity<List<ReminderResponse>> getByTaskId(
            @Parameter(description = "Identificador de la tarea", example = "1", required = true)
            @RequestParam Integer taskId
    ) {
        List<ReminderResponse> reminders = reminderService.getByTaskId(taskId)
                .stream()
                .map(ReminderResponse::from)
                .toList();
        return ResponseEntity.ok(reminders);
    }

    @PostMapping
    @Operation(summary = "Registrar un recordatorio", description = "Crea un recordatorio nuevo para una tarea existente.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Recordatorio creado correctamente",
                    content = @Content(schema = @Schema(implementation = ReminderResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Tarea no encontrada",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "JWT ausente o inválido",
                    content = @Content(schema = @Schema(implementation = AuthErrorResponse.class))
            )
    })
    public ResponseEntity<ReminderResponse> save(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Datos del recordatorio",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ReminderRequest.class),
                            examples = @ExampleObject(
                                    name = "recordatorioValido",
                                    summary = "Recordatorio asociado con una tarea",
                                    value = OpenApiExamples.REMINDER_CREATE
                            )
                    )
            )
            @Valid
            @org.springframework.web.bind.annotation.RequestBody
            ReminderRequest request
    ) {
        Reminder savedReminder = reminderService.save(request.toDomain());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ReminderResponse.from(savedReminder));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un recordatorio", description = "Elimina un recordatorio utilizando su identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Recordatorio eliminado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recordatorio no encontrado", content = @Content),
            @ApiResponse(
                    responseCode = "401",
                    description = "JWT ausente o inválido",
                    content = @Content(schema = @Schema(implementation = AuthErrorResponse.class))
            )
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Identificador del recordatorio", example = "1", required = true)
            @PathVariable Integer id
    ) {
        if (!reminderService.delete(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
