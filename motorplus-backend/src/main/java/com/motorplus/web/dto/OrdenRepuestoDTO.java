package com.motorplus.web.dto;

import java.math.BigDecimal;

public class OrdenRepuestoDTO {
    private Integer ordenCodigo;
    private Integer repuestoCodigo;
    private Integer cantidadUsada;
    private BigDecimal precioAplicado;
    private RepuestoDTO repuesto;

    public OrdenRepuestoDTO() {}

    public OrdenRepuestoDTO(Integer ordenCodigo, Integer repuestoCodigo, Integer cantidadUsada, BigDecimal precioAplicado) {
        this.ordenCodigo = ordenCodigo;
        this.repuestoCodigo = repuestoCodigo;
        this.cantidadUsada = cantidadUsada;
        this.precioAplicado = precioAplicado;
    }

    // Getters and Setters
    public Integer getOrdenCodigo() { return ordenCodigo; }
    public void setOrdenCodigo(Integer ordenCodigo) { this.ordenCodigo = ordenCodigo; }

    public Integer getRepuestoCodigo() { return repuestoCodigo; }
    public void setRepuestoCodigo(Integer repuestoCodigo) { this.repuestoCodigo = repuestoCodigo; }

    public Integer getCantidadUsada() { return cantidadUsada; }
    public void setCantidadUsada(Integer cantidadUsada) { this.cantidadUsada = cantidadUsada; }

    public BigDecimal getPrecioAplicado() { return precioAplicado; }
    public void setPrecioAplicado(BigDecimal precioAplicado) { this.precioAplicado = precioAplicado; }

    public RepuestoDTO getRepuesto() { return repuesto; }
    public void setRepuesto(RepuestoDTO repuesto) { this.repuesto = repuesto; }
}