package com.mx.tecdesoftware.Pendix.persistence.repository;

import com.mx.tecdesoftware.Pendix.domain.Reminder;
import com.mx.tecdesoftware.Pendix.domain.repository.ReminderRepository;
import com.mx.tecdesoftware.Pendix.persistence.crud.RecordatorioCrudRepository;
import com.mx.tecdesoftware.Pendix.persistence.entity.Recordatorio;
import com.mx.tecdesoftware.Pendix.persistence.mapper.ReminderMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class ReminderRepositoryImpl implements ReminderRepository {

    private final RecordatorioCrudRepository recordatorioCrudRepository;
    private final ReminderMapper reminderMapper;

    public ReminderRepositoryImpl(
            RecordatorioCrudRepository recordatorioCrudRepository,
            ReminderMapper reminderMapper
    ) {
        this.recordatorioCrudRepository = recordatorioCrudRepository;
        this.reminderMapper = reminderMapper;
    }

    @Override
    public List<Reminder> getAll() {
        List<Recordatorio> recordatorios = new ArrayList<>();

        recordatorioCrudRepository
                .findAll()
                .forEach(recordatorios::add);

        return reminderMapper.toReminders(recordatorios);
    }

    @Override
    public Optional<Reminder> getById(Integer reminderId) {
        return recordatorioCrudRepository
                .findById(reminderId)
                .map(reminderMapper::toReminder);
    }

    @Override
    public List<Reminder> getByTaskId(Integer taskId) {
        List<Recordatorio> recordatorios =
                recordatorioCrudRepository.findByIdTarea(taskId);

        return reminderMapper.toReminders(recordatorios);
    }

    @Override
    public Reminder save(Reminder reminder) {
        Recordatorio recordatorio = reminderMapper.toEntity(reminder);
        Recordatorio recordatorioGuardado =
                recordatorioCrudRepository.save(recordatorio);

        return reminderMapper.toReminder(recordatorioGuardado);
    }

    @Override
    public void deleteById(Integer reminderId) {
        recordatorioCrudRepository.deleteById(reminderId);
    }
}
