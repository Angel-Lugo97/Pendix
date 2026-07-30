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
