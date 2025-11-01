package com.motorplus.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RepuestoDTO {
    private Integer codigo;
    private String nombre;
    private String descripcion;
    private Integer stock;
    private Integer stockMinimo;
    private BigDecimal precioUnitario;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;

    public RepuestoDTO() {}

    public RepuestoDTO(Integer codigo, String nombre, String descripcion, Integer stock, Integer stockMinimo, BigDecimal precioUnitario) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
        this.precioUnitario = precioUnitario;
    }

    // Getters and Setters
    public Integer getCodigo() { return codigo; }
    public void setCodigo(Integer codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaModificacion() { return fechaModificacion; }
    public void setFechaModificacion(LocalDateTime fechaModificacion) { this.fechaModificacion = fechaModificacion; }
}