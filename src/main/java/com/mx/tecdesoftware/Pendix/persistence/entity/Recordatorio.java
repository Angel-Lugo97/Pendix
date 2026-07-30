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
