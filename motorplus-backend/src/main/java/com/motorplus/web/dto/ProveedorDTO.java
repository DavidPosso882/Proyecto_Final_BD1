package com.motorplus.web.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.time.LocalDateTime;

public class ProveedorDTO {
    private Integer id;
    private String nombre;
    private String correo;
    private String telefono;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;

    public ProveedorDTO() {}

    public ProveedorDTO(Integer id, String nombre, String correo, String telefono, Boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.activo = activo;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    // Alias para compatibilidad con frontend
    @JsonGetter("idProveedor")
    public Integer getIdProveedor() { return id; }
    
    @JsonSetter("idProveedor")
    public void setIdProveedor(Integer idProveedor) { this.id = idProveedor; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    
    // Alias para compatibilidad con frontend
    @JsonGetter("email")
    public String getEmail() { return correo; }
    
    @JsonSetter("email")
    public void setEmail(String email) { this.correo = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaModificacion() { return fechaModificacion; }
    public void setFechaModificacion(LocalDateTime fechaModificacion) { this.fechaModificacion = fechaModificacion; }
}