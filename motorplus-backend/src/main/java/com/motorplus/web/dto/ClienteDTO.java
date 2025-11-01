package com.motorplus.web.dto;

import java.time.LocalDateTime;

public class ClienteDTO {
    private Integer idCliente;
    private String nombre;
    private String telefono;
    private String tipo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;

    public ClienteDTO() {}

    public ClienteDTO(Integer idCliente, String nombre, String telefono, String tipo) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.telefono = telefono;
        this.tipo = tipo;
    }

    // Getters and Setters
    public Integer getIdCliente() { return idCliente; }
    public void setIdCliente(Integer idCliente) { this.idCliente = idCliente; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaModificacion() { return fechaModificacion; }
    public void setFechaModificacion(LocalDateTime fechaModificacion) { this.fechaModificacion = fechaModificacion; }
}