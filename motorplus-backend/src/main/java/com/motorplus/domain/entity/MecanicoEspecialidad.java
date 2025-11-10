package com.motorplus.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "MecanicoEspecialidad")
@IdClass(MecanicoEspecialidadId.class)
public class MecanicoEspecialidad {
    @Id
    @Column(name = "mecanico_id", nullable = false)
    private Integer mecanicoId;

    @Id
    @Column(name = "especialidad_codigo", nullable = false)
    private Integer especialidadCodigo;

    @Column(name = "nivel", length = 50)
    private String nivel;

    @Column(name = "fecha_certificacion")
    private LocalDate fechaCertificacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mecanico_id", insertable = false, updatable = false)
    private Mecanico mecanico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "especialidad_codigo", insertable = false, updatable = false)
    private Especialidad especialidad;

    // Getters and Setters
    public Integer getMecanicoId() { return mecanicoId; }
    public void setMecanicoId(Integer mecanicoId) { this.mecanicoId = mecanicoId; }

    public Integer getEspecialidadCodigo() { return especialidadCodigo; }
    public void setEspecialidadCodigo(Integer especialidadCodigo) { this.especialidadCodigo = especialidadCodigo; }

    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }

    public LocalDate getFechaCertificacion() { return fechaCertificacion; }
    public void setFechaCertificacion(LocalDate fechaCertificacion) { this.fechaCertificacion = fechaCertificacion; }

    public Mecanico getMecanico() { return mecanico; }
    public void setMecanico(Mecanico mecanico) { this.mecanico = mecanico; }

    public Especialidad getEspecialidad() { return especialidad; }
    public void setEspecialidad(Especialidad especialidad) { this.especialidad = especialidad; }
}