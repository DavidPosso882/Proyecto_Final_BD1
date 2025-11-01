package com.motorplus.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "RegistroSup")
public class Supervision {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo")
    private Integer codigo;

    @Column(name = "fecha")
    private java.sql.Date fecha;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @ManyToOne
    @JoinColumn(name = "supervisor_id", nullable = false)
    private Mecanico supervisor;

    @ManyToOne
    @JoinColumn(name = "mecanico_id", nullable = false)
    private Mecanico supervisado;

    @Column(name = "fecha_creacion")
    private java.sql.Timestamp fechaCreacion;

    // Getters and Setters
    public Integer getCodigo() { return codigo; }
    public void setCodigo(Integer codigo) { this.codigo = codigo; }

    public java.sql.Date getFecha() { return fecha; }
    public void setFecha(java.sql.Date fecha) { this.fecha = fecha; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Mecanico getSupervisor() { return supervisor; }
    public void setSupervisor(Mecanico supervisor) { this.supervisor = supervisor; }

    public Mecanico getSupervisado() { return supervisado; }
    public void setSupervisado(Mecanico supervisado) { this.supervisado = supervisado; }

    public java.sql.Timestamp getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(java.sql.Timestamp fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}