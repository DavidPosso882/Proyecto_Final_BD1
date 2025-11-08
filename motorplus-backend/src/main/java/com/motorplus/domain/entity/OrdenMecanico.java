package com.motorplus.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity
@Table(name = "OrdenMecanico")
@IdClass(OrdenMecanicoId.class)
public class OrdenMecanico {
     @Id
     @Column(name = "mecanico_id", nullable = false)
     @NotNull(message = "El ID del mecánico es obligatorio")
     private Integer mecanicoId;

     @Id
     @Column(name = "orden_codigo", nullable = false)
     @NotNull(message = "El código de orden es obligatorio")
     private Integer ordenCodigo;

     @Column(name = "rol", length = 50)
     @Pattern(regexp = "^(ASIGNADO|SUPERVISOR|LIDER)$", message = "Rol inválido")
     private String rol = "ASIGNADO";

     @Column(name = "horas_trabajadas", precision = 5, scale = 2)
     @DecimalMin(value = "0.0", inclusive = true, message = "Las horas trabajadas no pueden ser negativas")
     @Digits(integer = 3, fraction = 2, message = "Formato de horas inválido")
     private BigDecimal horasTrabajadas = BigDecimal.ZERO;

     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "mecanico_id", insertable = false, updatable = false)
     private Mecanico mecanico;

     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "orden_codigo", insertable = false, updatable = false)
     private OrdenTrabajo ordenTrabajo;

     // Getters and Setters
     public Integer getMecanicoId() { return mecanicoId; }
     public void setMecanicoId(Integer mecanicoId) { this.mecanicoId = mecanicoId; }

     public Integer getOrdenCodigo() { return ordenCodigo; }
     public void setOrdenCodigo(Integer ordenCodigo) { this.ordenCodigo = ordenCodigo; }

     public String getRol() { return rol; }
     public void setRol(String rol) { this.rol = rol; }

     public BigDecimal getHorasTrabajadas() { return horasTrabajadas; }
     public void setHorasTrabajadas(BigDecimal horasTrabajadas) { this.horasTrabajadas = horasTrabajadas; }

     public Mecanico getMecanico() { return mecanico; }
     public void setMecanico(Mecanico mecanico) { this.mecanico = mecanico; }

     public OrdenTrabajo getOrdenTrabajo() { return ordenTrabajo; }
     public void setOrdenTrabajo(OrdenTrabajo ordenTrabajo) { this.ordenTrabajo = ordenTrabajo; }
}