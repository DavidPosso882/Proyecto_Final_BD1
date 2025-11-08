package com.motorplus.web.dto;

import java.math.BigDecimal;

public class OrdenServicioDTO {
    private Integer ordenCodigo;
    private Integer servicioCodigo;
    private Integer cantidad;
    private BigDecimal precioAplicado;
    private ServicioDTO servicio;

    public OrdenServicioDTO() {}

    public OrdenServicioDTO(Integer ordenCodigo, Integer servicioCodigo, Integer cantidad, BigDecimal precioAplicado) {
        this.ordenCodigo = ordenCodigo;
        this.servicioCodigo = servicioCodigo;
        this.cantidad = cantidad;
        this.precioAplicado = precioAplicado;
    }

    // Getters and Setters
    public Integer getOrdenCodigo() { return ordenCodigo; }
    public void setOrdenCodigo(Integer ordenCodigo) { this.ordenCodigo = ordenCodigo; }

    public Integer getServicioCodigo() { return servicioCodigo; }
    public void setServicioCodigo(Integer servicioCodigo) { this.servicioCodigo = servicioCodigo; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPrecioAplicado() { return precioAplicado; }
    public void setPrecioAplicado(BigDecimal precioAplicado) { this.precioAplicado = precioAplicado; }

    public ServicioDTO getServicio() { return servicio; }
    public void setServicio(ServicioDTO servicio) { this.servicio = servicio; }
}