package com.mx.tecdesoftware.Pendix.domain.repository;

import com.mx.tecdesoftware.Pendix.domain.Reminder;

import java.util.List;
import java.util.Optional;

public interface ReminderRepository {

    List<Reminder> getAll();

    Optional<Reminder> getById(Integer reminderId);

    List<Reminder> getByTaskId(Integer taskId);

    Reminder save(Reminder reminder);

    void deleteById(Integer reminderId);
}
