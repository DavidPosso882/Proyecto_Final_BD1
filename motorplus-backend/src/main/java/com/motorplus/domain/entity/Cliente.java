package com.motorplus.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Cliente")
public class Cliente {
     @Id
     @Column(name = "id_cliente", length = 20)
     @NotBlank(message = "El ID del cliente es obligatorio")
     @Size(max = 20, message = "El ID del cliente no puede exceder 20 caracteres")
     private String idCliente;

     @Column(name = "nombre", nullable = false, length = 100)
     @NotBlank(message = "El nombre es obligatorio")
     @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
     private String nombre;

     @Column(name = "apellido", length = 100)
     @Size(max = 100, message = "El apellido no puede exceder 100 caracteres")
     private String apellido;

     @Column(name = "telefono", length = 20)
     @Pattern(regexp = "^[0-9+\\-\\s()]*$", message = "El teléfono contiene caracteres inválidos")
     @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
     private String telefono;

     @Column(name = "email", length = 50)
     @Email(message = "El email debe tener un formato válido")
     @Size(max = 50, message = "El email no puede exceder 50 caracteres")
     private String email;

     @Column(name = "tipo", nullable = false, length = 50)
     @NotBlank(message = "El tipo es obligatorio")
     @Pattern(regexp = "^(INDIVIDUAL|CORPORATIVO)$", message = "El tipo debe ser INDIVIDUAL o CORPORATIVO")
     private String tipo;

     @Column(name = "fecha_creacion")
     private LocalDateTime fechaCreacion;

     @Column(name = "fecha_modificacion")
     private LocalDateTime fechaModificacion;

     @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
     private List<Vehiculo> vehiculos;

     // Getters and Setters
     public String getIdCliente() { return idCliente; }
     public void setIdCliente(String idCliente) { this.idCliente = idCliente; }

     public String getNombre() { return nombre; }
     public void setNombre(String nombre) { this.nombre = nombre; }

     public String getApellido() { return apellido; }
     public void setApellido(String apellido) { this.apellido = apellido; }

     public String getTelefono() { return telefono; }
     public void setTelefono(String telefono) { this.telefono = telefono; }

     public String getEmail() { return email; }
     public void setEmail(String email) { this.email = email; }

     public String getTipo() { return tipo; }
     public void setTipo(String tipo) { this.tipo = tipo; }

     public LocalDateTime getFechaCreacion() { return fechaCreacion; }
     public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

     public LocalDateTime getFechaModificacion() { return fechaModificacion; }
     public void setFechaModificacion(LocalDateTime fechaModificacion) { this.fechaModificacion = fechaModificacion; }

     public List<Vehiculo> getVehiculos() { return vehiculos; }
     public void setVehiculos(List<Vehiculo> vehiculos) { this.vehiculos = vehiculos; }
}