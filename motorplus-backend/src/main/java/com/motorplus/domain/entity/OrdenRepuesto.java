package com.motorplus.domain.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "OrdenRepuesto")
@IdClass(OrdenRepuestoId.class)
public class OrdenRepuesto {
    @Id
    @Column(name = "orden_codigo", nullable = false)
    private Integer ordenCodigo;

    @Id
    @Column(name = "repuesto_codigo", nullable = false)
    private Integer repuestoCodigo;

    @Column(name = "cantidad_usada", nullable = false)
    private Integer cantidadUsada;

    @Column(name = "precio_aplicado", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioAplicado;

    // Getters and Setters
    public Integer getOrdenCodigo() { return ordenCodigo; }
    public void setOrdenCodigo(Integer ordenCodigo) { this.ordenCodigo = ordenCodigo; }

    public Integer getRepuestoCodigo() { return repuestoCodigo; }
    public void setRepuestoCodigo(Integer repuestoCodigo) { this.repuestoCodigo = repuestoCodigo; }

    public Integer getCantidadUsada() { return cantidadUsada; }
    public void setCantidadUsada(Integer cantidadUsada) { this.cantidadUsada = cantidadUsada; }

    public BigDecimal getPrecioAplicado() { return precioAplicado; }
    public void setPrecioAplicado(BigDecimal precioAplicado) { this.precioAplicado = precioAplicado; }
}