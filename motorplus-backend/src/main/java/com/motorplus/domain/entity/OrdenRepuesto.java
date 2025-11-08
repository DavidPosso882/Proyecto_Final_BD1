package com.motorplus.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity
@Table(name = "OrdenRepuesto")
@IdClass(OrdenRepuestoId.class)
public class OrdenRepuesto {
     @Id
     @Column(name = "orden_codigo", nullable = false)
     @NotNull(message = "El código de orden es obligatorio")
     private Integer ordenCodigo;

     @Id
     @Column(name = "repuesto_codigo", nullable = false)
     @NotNull(message = "El código de repuesto es obligatorio")
     private Integer repuestoCodigo;

     @Column(name = "cantidad_usada", nullable = false)
     @NotNull(message = "La cantidad usada es obligatoria")
     @Min(value = 1, message = "La cantidad usada debe ser mayor a 0")
     private Integer cantidadUsada = 1;

     @Column(name = "precio_aplicado", nullable = false, precision = 10, scale = 2)
     @NotNull(message = "El precio aplicado es obligatorio")
     @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
     @Digits(integer = 8, fraction = 2, message = "Formato de precio inválido")
     private BigDecimal precioAplicado;

     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "orden_codigo", insertable = false, updatable = false)
     private OrdenTrabajo ordenTrabajo;

     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "repuesto_codigo", insertable = false, updatable = false)
     private Repuesto repuesto;

     // Getters and Setters
     public Integer getOrdenCodigo() { return ordenCodigo; }
     public void setOrdenCodigo(Integer ordenCodigo) { this.ordenCodigo = ordenCodigo; }

     public Integer getRepuestoCodigo() { return repuestoCodigo; }
     public void setRepuestoCodigo(Integer repuestoCodigo) { this.repuestoCodigo = repuestoCodigo; }

     public Integer getCantidadUsada() { return cantidadUsada; }
     public void setCantidadUsada(Integer cantidadUsada) { this.cantidadUsada = cantidadUsada; }

     public BigDecimal getPrecioAplicado() { return precioAplicado; }
     public void setPrecioAplicado(BigDecimal precioAplicado) { this.precioAplicado = precioAplicado; }

     public OrdenTrabajo getOrdenTrabajo() { return ordenTrabajo; }
     public void setOrdenTrabajo(OrdenTrabajo ordenTrabajo) { this.ordenTrabajo = ordenTrabajo; }

     public Repuesto getRepuesto() { return repuesto; }
     public void setRepuesto(Repuesto repuesto) { this.repuesto = repuesto; }
}