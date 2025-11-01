package com.motorplus.domain.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "OrdenServicio")
@IdClass(OrdenServicioId.class)
public class OrdenServicio {
    @Id
    @Column(name = "orden_codigo", nullable = false)
    private Integer ordenCodigo;

    @Id
    @Column(name = "servicio_codigo", nullable = false)
    private Integer servicioCodigo;

    @Column(name = "cantidad")
    private Integer cantidad;

    @Column(name = "precio_aplicado", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioAplicado;

    // Getters and Setters
    public Integer getOrdenCodigo() { return ordenCodigo; }
    public void setOrdenCodigo(Integer ordenCodigo) { this.ordenCodigo = ordenCodigo; }

    public Integer getServicioCodigo() { return servicioCodigo; }
    public void setServicioCodigo(Integer servicioCodigo) { this.servicioCodigo = servicioCodigo; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPrecioAplicado() { return precioAplicado; }
    public void setPrecioAplicado(BigDecimal precioAplicado) { this.precioAplicado = precioAplicado; }
}