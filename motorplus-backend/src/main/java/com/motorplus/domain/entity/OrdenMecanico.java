package com.motorplus.domain.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "OrdenMecanico")
@IdClass(OrdenMecanicoId.class)
public class OrdenMecanico {
    @Id
    @Column(name = "mecanico_id", nullable = false)
    private Integer mecanicoId;

    @Id
    @Column(name = "orden_codigo", nullable = false)
    private Integer ordenCodigo;

    @Column(name = "rol", length = 50)
    private String rol;

    @Column(name = "horas_trabajadas", precision = 5, scale = 2)
    private BigDecimal horasTrabajadas;

    // Getters and Setters
    public Integer getMecanicoId() { return mecanicoId; }
    public void setMecanicoId(Integer mecanicoId) { this.mecanicoId = mecanicoId; }

    public Integer getOrdenCodigo() { return ordenCodigo; }
    public void setOrdenCodigo(Integer ordenCodigo) { this.ordenCodigo = ordenCodigo; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public BigDecimal getHorasTrabajadas() { return horasTrabajadas; }
    public void setHorasTrabajadas(BigDecimal horasTrabajadas) { this.horasTrabajadas = horasTrabajadas; }
}