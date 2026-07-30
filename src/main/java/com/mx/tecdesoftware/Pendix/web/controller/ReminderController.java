package com.mx.tecdesoftware.Pendix.web.controller;

import com.mx.tecdesoftware.Pendix.domain.Reminder;
import com.mx.tecdesoftware.Pendix.domain.service.ReminderService;
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
public class ReminderController {

    private final ReminderService reminderService;

    public ReminderController(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    @GetMapping
    public ResponseEntity<List<Reminder>> getAll() {
        return ResponseEntity.ok(reminderService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reminder> getById(
            @PathVariable Integer id
    ) {
        return reminderService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(
                        () -> ResponseEntity.notFound().build()
                );
    }

    @GetMapping(params = "taskId")
    public ResponseEntity<List<Reminder>> getByTaskId(
            @RequestParam Integer taskId
    ) {
        return ResponseEntity.ok(
                reminderService.getByTaskId(taskId)
        );
    }

    @PostMapping
    public ResponseEntity<Reminder> save(
            @RequestBody Reminder reminder
    ) {
        return reminderService.save(reminder)
                .map(savedReminder -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(savedReminder))
                .orElseGet(() -> ResponseEntity
                        .badRequest()
                        .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Integer id
    ) {
        boolean deleted = reminderService.delete(id);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
