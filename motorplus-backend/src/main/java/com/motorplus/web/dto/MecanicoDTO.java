package com.motorplus.web.dto;

import java.time.LocalDateTime;

public class MecanicoDTO {
    private Integer idMecanico;
    private String nombre;
    private String telefono;
    private Boolean activo;
    private Integer supervisorId;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;

    public MecanicoDTO() {}

    public MecanicoDTO(Integer idMecanico, String nombre, String telefono, Boolean activo, Integer supervisorId) {
        this.idMecanico = idMecanico;
        this.nombre = nombre;
        this.telefono = telefono;
        this.activo = activo;
        this.supervisorId = supervisorId;
    }

    // Getters and Setters
    public Integer getIdMecanico() { return idMecanico; }
    public void setIdMecanico(Integer idMecanico) { this.idMecanico = idMecanico; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public Integer getSupervisorId() { return supervisorId; }
    public void setSupervisorId(Integer supervisorId) { this.supervisorId = supervisorId; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaModificacion() { return fechaModificacion; }
    public void setFechaModificacion(LocalDateTime fechaModificacion) { this.fechaModificacion = fechaModificacion; }
}