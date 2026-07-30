package com.mx.tecdesoftware.Pendix.persistence.mapper;

import com.mx.tecdesoftware.Pendix.domain.Reminder;
import com.mx.tecdesoftware.Pendix.persistence.entity.Recordatorio;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReminderMapper {

    @Mapping(source = "idRecordatorio", target = "reminderId")
    @Mapping(source = "idTarea", target = "taskId")
    @Mapping(source = "fechaRecordatorio", target = "reminderDate")
    @Mapping(source = "mensaje", target = "message")
    @Mapping(source = "enviado", target = "sent")
    Reminder toReminder(Recordatorio recordatorio);

    List<Reminder> toReminders(List<Recordatorio> recordatorios);

    @InheritInverseConfiguration(name = "toReminder")
    @Mapping(target = "tarea", ignore = true)
    Recordatorio toEntity(Reminder reminder);

    List<Recordatorio> toEntities(List<Reminder> reminders);
}
