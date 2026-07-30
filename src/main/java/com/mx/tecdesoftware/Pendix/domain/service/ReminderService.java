package com.mx.tecdesoftware.Pendix.domain.service;

import com.mx.tecdesoftware.Pendix.domain.Reminder;
import com.mx.tecdesoftware.Pendix.domain.repository.ReminderRepository;
import com.mx.tecdesoftware.Pendix.domain.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReminderService {

    private final ReminderRepository reminderRepository;
    private final TaskRepository taskRepository;

    public ReminderService(
            ReminderRepository reminderRepository,
            TaskRepository taskRepository
    ) {
        this.reminderRepository = reminderRepository;
        this.taskRepository = taskRepository;
    }

    public List<Reminder> getAll() {
        return reminderRepository.getAll();
    }

    public Optional<Reminder> getById(Integer reminderId) {
        return reminderRepository.getById(reminderId);
    }

    public List<Reminder> getByTaskId(Integer taskId) {
        return reminderRepository.getByTaskId(taskId);
    }

    public Optional<Reminder> save(Reminder reminder) {
        if (reminder == null
                || reminder.getTaskId() == null
                || reminder.getReminderDate() == null
                || reminder.getMessage() == null
                || reminder.getMessage().isBlank()
                || taskRepository.getById(reminder.getTaskId()).isEmpty()) {
            return Optional.empty();
        }

        if (reminder.getSent() == null) {
            reminder.setSent(false);
        }

        return Optional.of(reminderRepository.save(reminder));
    }

    public boolean delete(Integer reminderId) {
        Optional<Reminder> reminder =
                reminderRepository.getById(reminderId);

        if (reminder.isEmpty()) {
            return false;
        }

        reminderRepository.deleteById(reminderId);
        return true;
    }
}
