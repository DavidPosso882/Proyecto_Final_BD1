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

    // Getters and Setters
    public Integer getMecanicoId() { return mecanicoId; }
    public void setMecanicoId(Integer mecanicoId) { this.mecanicoId = mecanicoId; }

    public Integer getEspecialidadCodigo() { return especialidadCodigo; }
    public void setEspecialidadCodigo(Integer especialidadCodigo) { this.especialidadCodigo = especialidadCodigo; }

    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }

    public LocalDate getFechaCertificacion() { return fechaCertificacion; }
    public void setFechaCertificacion(LocalDate fechaCertificacion) { this.fechaCertificacion = fechaCertificacion; }
}