package com.motorplus.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity
@Table(name = "OrdenServicio")
@IdClass(OrdenServicioId.class)
public class OrdenServicio {
     @Id
     @Column(name = "orden_codigo", nullable = false)
     @NotNull(message = "El código de orden es obligatorio")
     private Integer ordenCodigo;

     @Id
     @Column(name = "servicio_codigo", nullable = false)
     @NotNull(message = "El código de servicio es obligatorio")
     private Integer servicioCodigo;

     @Column(name = "cantidad")
     @Min(value = 1, message = "La cantidad debe ser mayor a 0")
     private Integer cantidad = 1;

     @Column(name = "precio_aplicado", nullable = false, precision = 10, scale = 2)
     @NotNull(message = "El precio aplicado es obligatorio")
     @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
     @Digits(integer = 8, fraction = 2, message = "Formato de precio inválido")
     private BigDecimal precioAplicado;

     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "orden_codigo", insertable = false, updatable = false)
     private OrdenTrabajo ordenTrabajo;

     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "servicio_codigo", insertable = false, updatable = false)
     private Servicio servicio;

     // Getters and Setters
     public Integer getOrdenCodigo() { return ordenCodigo; }
     public void setOrdenCodigo(Integer ordenCodigo) { this.ordenCodigo = ordenCodigo; }

     public Integer getServicioCodigo() { return servicioCodigo; }
     public void setServicioCodigo(Integer servicioCodigo) { this.servicioCodigo = servicioCodigo; }

     public Integer getCantidad() { return cantidad; }
     public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

     public BigDecimal getPrecioAplicado() { return precioAplicado; }
     public void setPrecioAplicado(BigDecimal precioAplicado) { this.precioAplicado = precioAplicado; }

     public OrdenTrabajo getOrdenTrabajo() { return ordenTrabajo; }
     public void setOrdenTrabajo(OrdenTrabajo ordenTrabajo) { this.ordenTrabajo = ordenTrabajo; }

     public Servicio getServicio() { return servicio; }
     public void setServicio(Servicio servicio) { this.servicio = servicio; }
}