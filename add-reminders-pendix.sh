#!/usr/bin/env bash
set -euo pipefail

if [[ ! -f build.gradle || ! -d src/main/java/com/mx/tecdesoftware/Pendix ]]; then
  echo 'ERROR: ejecuta este script desde la raíz del proyecto Pendix.' >&2
  exit 1
fi

echo 'Creando la entidad Recordatorio y su flujo completo...'

mkdir -p "$(dirname 'src/main/java/com/mx/tecdesoftware/Pendix/domain/Reminder.java')"
cat > 'src/main/java/com/mx/tecdesoftware/Pendix/domain/Reminder.java' <<'PENDIX_EOF'
package com.mx.tecdesoftware.Pendix.domain;

import java.time.LocalDateTime;

public class Reminder {

    private Integer reminderId;
    private Integer taskId;
    private LocalDateTime reminderDate;
    private String message;
    private Boolean sent;

    public Integer getReminderId() {
        return reminderId;
    }

    public void setReminderId(Integer reminderId) {
        this.reminderId = reminderId;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public LocalDateTime getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(LocalDateTime reminderDate) {
        this.reminderDate = reminderDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSent() {
        return sent;
    }

    public void setSent(Boolean sent) {
        this.sent = sent;
    }
}
PENDIX_EOF

mkdir -p "$(dirname 'src/main/java/com/mx/tecdesoftware/Pendix/domain/Task.java')"
cat > 'src/main/java/com/mx/tecdesoftware/Pendix/domain/Task.java' <<'PENDIX_EOF'
package com.mx.tecdesoftware.Pendix.domain;

import java.time.LocalDateTime;
import java.util.List;

public class Task {

    private Integer taskId;
    private Integer projectId;
    private Integer assignedUserId;
    private String title;
    private String description;
    private LocalDateTime creationDate;
    private LocalDateTime dueDate;
    private String priority;
    private String state;
    private List<Reminder> reminders;

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getAssignedUserId() {
        return assignedUserId;
    }

    public void setAssignedUserId(Integer assignedUserId) {
        this.assignedUserId = assignedUserId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<Reminder> getReminders() {
        return reminders;
    }

    public void setReminders(List<Reminder> reminders) {
        this.reminders = reminders;
    }
}
PENDIX_EOF

mkdir -p "$(dirname 'src/main/java/com/mx/tecdesoftware/Pendix/domain/repository/ReminderRepository.java')"
cat > 'src/main/java/com/mx/tecdesoftware/Pendix/domain/repository/ReminderRepository.java' <<'PENDIX_EOF'
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
PENDIX_EOF

mkdir -p "$(dirname 'src/main/java/com/mx/tecdesoftware/Pendix/domain/service/ReminderService.java')"
cat > 'src/main/java/com/mx/tecdesoftware/Pendix/domain/service/ReminderService.java' <<'PENDIX_EOF'
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
PENDIX_EOF

mkdir -p "$(dirname 'src/main/java/com/mx/tecdesoftware/Pendix/persistence/entity/Recordatorio.java')"
cat > 'src/main/java/com/mx/tecdesoftware/Pendix/persistence/entity/Recordatorio.java' <<'PENDIX_EOF'
package com.mx.tecdesoftware.Pendix.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "recordatorios")
public class Recordatorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recordatorio")
    private Integer idRecordatorio;

    @Column(name = "id_tarea", nullable = false)
    private Integer idTarea;

    @Column(name = "fecha_recordatorio", nullable = false)
    private LocalDateTime fechaRecordatorio;

    @Column(nullable = false)
    private String mensaje;

    @Column(nullable = false)
    private Boolean enviado = false;

    @ManyToOne
    @JoinColumn(
            name = "id_tarea",
            insertable = false,
            updatable = false
    )
    private Tarea tarea;

    @PrePersist
    public void applyDefaults() {
        if (enviado == null) {
            enviado = false;
        }
    }

    public Integer getIdRecordatorio() {
        return idRecordatorio;
    }

    public void setIdRecordatorio(Integer idRecordatorio) {
        this.idRecordatorio = idRecordatorio;
    }

    public Integer getIdTarea() {
        return idTarea;
    }

    public void setIdTarea(Integer idTarea) {
        this.idTarea = idTarea;
    }

    public LocalDateTime getFechaRecordatorio() {
        return fechaRecordatorio;
    }

    public void setFechaRecordatorio(LocalDateTime fechaRecordatorio) {
        this.fechaRecordatorio = fechaRecordatorio;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Boolean getEnviado() {
        return enviado;
    }

    public void setEnviado(Boolean enviado) {
        this.enviado = enviado;
    }

    public Tarea getTarea() {
        return tarea;
    }

    public void setTarea(Tarea tarea) {
        this.tarea = tarea;
    }
}
PENDIX_EOF

mkdir -p "$(dirname 'src/main/java/com/mx/tecdesoftware/Pendix/persistence/entity/Tarea.java')"
cat > 'src/main/java/com/mx/tecdesoftware/Pendix/persistence/entity/Tarea.java' <<'PENDIX_EOF'
package com.mx.tecdesoftware.Pendix.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tareas")
public class Tarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tarea")
    private Integer idTarea;

    @Column(name = "id_proyecto", nullable = false)
    private Integer idProyecto;

    @Column(name = "id_usuario_asignado")
    private Integer idUsuarioAsignado;

    @Column(nullable = false)
    private String titulo;

    private String descripcion;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_limite")
    private LocalDateTime fechaLimite;

    private String prioridad;

    private String estado;

    @ManyToOne
    @JoinColumn(
            name = "id_proyecto",
            insertable = false,
            updatable = false
    )
    private Proyecto proyecto;

    @ManyToOne
    @JoinColumn(
            name = "id_usuario_asignado",
            insertable = false,
            updatable = false
    )
    private Usuario usuarioAsignado;

    @OneToMany(
            mappedBy = "tarea",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Recordatorio> recordatorios;

    public Integer getIdTarea() {
        return idTarea;
    }

    public void setIdTarea(Integer idTarea) {
        this.idTarea = idTarea;
    }

    public Integer getIdProyecto() {
        return idProyecto;
    }

    public void setIdProyecto(Integer idProyecto) {
        this.idProyecto = idProyecto;
    }

    public Integer getIdUsuarioAsignado() {
        return idUsuarioAsignado;
    }

    public void setIdUsuarioAsignado(Integer idUsuarioAsignado) {
        this.idUsuarioAsignado = idUsuarioAsignado;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(LocalDateTime fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Proyecto getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
    }

    public Usuario getUsuarioAsignado() {
        return usuarioAsignado;
    }

    public void setUsuarioAsignado(Usuario usuarioAsignado) {
        this.usuarioAsignado = usuarioAsignado;
    }

    public List<Recordatorio> getRecordatorios() {
        return recordatorios;
    }

    public void setRecordatorios(List<Recordatorio> recordatorios) {
        this.recordatorios = recordatorios;
    }
}
PENDIX_EOF

mkdir -p "$(dirname 'src/main/java/com/mx/tecdesoftware/Pendix/persistence/crud/RecordatorioCrudRepository.java')"
cat > 'src/main/java/com/mx/tecdesoftware/Pendix/persistence/crud/RecordatorioCrudRepository.java' <<'PENDIX_EOF'
package com.mx.tecdesoftware.Pendix.persistence.crud;

import com.mx.tecdesoftware.Pendix.persistence.entity.Recordatorio;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RecordatorioCrudRepository
        extends CrudRepository<Recordatorio, Integer> {

    List<Recordatorio> findByIdTarea(Integer idTarea);
}
PENDIX_EOF

mkdir -p "$(dirname 'src/main/java/com/mx/tecdesoftware/Pendix/persistence/mapper/ReminderMapper.java')"
cat > 'src/main/java/com/mx/tecdesoftware/Pendix/persistence/mapper/ReminderMapper.java' <<'PENDIX_EOF'
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
PENDIX_EOF

mkdir -p "$(dirname 'src/main/java/com/mx/tecdesoftware/Pendix/persistence/mapper/TaskMapper.java')"
cat > 'src/main/java/com/mx/tecdesoftware/Pendix/persistence/mapper/TaskMapper.java' <<'PENDIX_EOF'
package com.mx.tecdesoftware.Pendix.persistence.mapper;

import com.mx.tecdesoftware.Pendix.domain.Task;
import com.mx.tecdesoftware.Pendix.persistence.entity.Tarea;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = ReminderMapper.class
)
public interface TaskMapper {

    @Mapping(source = "idTarea", target = "taskId")
    @Mapping(source = "idProyecto", target = "projectId")
    @Mapping(
            source = "idUsuarioAsignado",
            target = "assignedUserId"
    )
    @Mapping(source = "titulo", target = "title")
    @Mapping(source = "descripcion", target = "description")
    @Mapping(source = "fechaCreacion", target = "creationDate")
    @Mapping(source = "fechaLimite", target = "dueDate")
    @Mapping(source = "prioridad", target = "priority")
    @Mapping(source = "estado", target = "state")
    @Mapping(source = "recordatorios", target = "reminders")
    Task toTask(Tarea tarea);

    List<Task> toTasks(List<Tarea> tareas);

    @InheritInverseConfiguration(name = "toTask")
    @Mapping(target = "proyecto", ignore = true)
    @Mapping(target = "usuarioAsignado", ignore = true)
    Tarea toEntity(Task task);

    List<Tarea> toEntities(List<Task> tasks);
}
PENDIX_EOF

mkdir -p "$(dirname 'src/main/java/com/mx/tecdesoftware/Pendix/persistence/repository/ReminderRepositoryImpl.java')"
cat > 'src/main/java/com/mx/tecdesoftware/Pendix/persistence/repository/ReminderRepositoryImpl.java' <<'PENDIX_EOF'
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
PENDIX_EOF

mkdir -p "$(dirname 'src/main/java/com/mx/tecdesoftware/Pendix/web/controller/ReminderController.java')"
cat > 'src/main/java/com/mx/tecdesoftware/Pendix/web/controller/ReminderController.java' <<'PENDIX_EOF'
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
PENDIX_EOF

mkdir -p "$(dirname 'src/test/java/com/mx/tecdesoftware/Pendix/persistence/mapper/MapperContextTest.java')"
cat > 'src/test/java/com/mx/tecdesoftware/Pendix/persistence/mapper/MapperContextTest.java' <<'PENDIX_EOF'
package com.mx.tecdesoftware.Pendix.persistence.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringJUnitConfig(
        MapperContextTest.MapperTestConfiguration.class
)
class MapperContextTest {

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private ReminderMapper reminderMapper;

    @Test
    void shouldLoadMappersAsSpringBeans() {
        assertNotNull(projectMapper);
        assertNotNull(taskMapper);
        assertNotNull(reminderMapper);
    }

    @Configuration
    @ComponentScan(basePackageClasses = ProjectMapper.class)
    static class MapperTestConfiguration {
    }
}
PENDIX_EOF

mkdir -p "$(dirname 'migration-add-reminders.sql')"
cat > 'migration-add-reminders.sql' <<'PENDIX_EOF'
BEGIN;

CREATE TABLE IF NOT EXISTS recordatorios (
    id_recordatorio INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    id_tarea INTEGER NOT NULL,
    fecha_recordatorio TIMESTAMP NOT NULL,
    mensaje VARCHAR(255) NOT NULL,
    enviado BOOLEAN NOT NULL DEFAULT FALSE
);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_recordatorios_tareas'
    ) THEN
        ALTER TABLE recordatorios
            ADD CONSTRAINT fk_recordatorios_tareas
            FOREIGN KEY (id_tarea)
            REFERENCES tareas (id_tarea)
            ON DELETE CASCADE;
    END IF;
END
$$;

CREATE INDEX IF NOT EXISTS idx_recordatorios_id_tarea
    ON recordatorios (id_tarea);

COMMIT;
PENDIX_EOF

echo
echo 'Archivos creados correctamente.'
echo 'Siguiente paso: aplica migration-add-reminders.sql y ejecuta ./gradlew clean compileJava.'
