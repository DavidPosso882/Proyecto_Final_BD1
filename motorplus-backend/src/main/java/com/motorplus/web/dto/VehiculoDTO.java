package com.motorplus.web.dto;

import java.time.LocalDateTime;

public class VehiculoDTO {
    private String placa;
    private String tipo;
    private String marca;
    private String modelo;
    private Integer anio;
    private String documentoCliente;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;

    public VehiculoDTO() {}

    public VehiculoDTO(String placa, String tipo, String marca, String modelo, Integer anio, String documentoCliente) {
        this.placa = placa;
        this.tipo = tipo;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.documentoCliente = documentoCliente;
    }

    // Getters and Setters
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }

    public String getDocumentoCliente() { return documentoCliente; }
    public void setDocumentoCliente(String documentoCliente) { this.documentoCliente = documentoCliente; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaModificacion() { return fechaModificacion; }
    public void setFechaModificacion(LocalDateTime fechaModificacion) { this.fechaModificacion = fechaModificacion; }
}