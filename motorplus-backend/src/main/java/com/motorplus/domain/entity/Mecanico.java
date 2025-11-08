package com.motorplus.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Mecanico")
public class Mecanico {
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     @Column(name = "id_mecanico")
     private Integer idMecanico;

     @Column(name = "nombre", nullable = false, length = 100)
     @NotBlank(message = "El nombre es obligatorio")
     @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
     private String nombre;

     @Column(name = "telefono", length = 20)
     @Pattern(regexp = "^[0-9+\\-\\s()]*$", message = "El teléfono contiene caracteres inválidos")
     @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
     private String telefono;

     @Transient
     private String email;

     @Column(name = "supervisor_id")
     private Integer supervisorId;

     @Column(name = "activo")
     private Boolean activo;

     @Column(name = "fecha_creacion")
     private LocalDateTime fechaCreacion;

     @Column(name = "fecha_modificacion")
     private LocalDateTime fechaModificacion;

     @ManyToOne
     @JoinColumn(name = "supervisor_id", insertable = false, updatable = false)
     private Mecanico supervisor;

    // Getters and Setters
    public Integer getIdMecanico() { return idMecanico; }
    public void setIdMecanico(Integer idMecanico) { this.idMecanico = idMecanico; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public Integer getSupervisorId() { return supervisorId; }
    public void setSupervisorId(Integer supervisorId) { this.supervisorId = supervisorId; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaModificacion() { return fechaModificacion; }
    public void setFechaModificacion(LocalDateTime fechaModificacion) { this.fechaModificacion = fechaModificacion; }

    public Mecanico getSupervisor() { return supervisor; }
    public void setSupervisor(Mecanico supervisor) { this.supervisor = supervisor; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}