package com.motorplus.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "RegistroSup")
public class RegistroSup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo")
    private Integer codigo;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "supervisor_id", nullable = false)
    private Integer supervisorId;

    @Column(name = "mecanico_id", nullable = false)
    private Integer mecanicoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id", insertable = false, updatable = false)
    private Mecanico supervisor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mecanico_id", insertable = false, updatable = false)
    private Mecanico mecanico;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    // Getters and Setters
    public Integer getCodigo() { return codigo; }
    public void setCodigo(Integer codigo) { this.codigo = codigo; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Integer getSupervisorId() { return supervisorId; }
    public void setSupervisorId(Integer supervisorId) { this.supervisorId = supervisorId; }

    public Integer getMecanicoId() { return mecanicoId; }
    public void setMecanicoId(Integer mecanicoId) { this.mecanicoId = mecanicoId; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public Mecanico getSupervisor() { return supervisor; }
    public void setSupervisor(Mecanico supervisor) { this.supervisor = supervisor; }

    public Mecanico getMecanico() { return mecanico; }
    public void setMecanico(Mecanico mecanico) { this.mecanico = mecanico; }
}