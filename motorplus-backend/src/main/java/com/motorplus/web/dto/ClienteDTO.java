package com.motorplus.web.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ClienteDTO {
     private String idCliente;
     private String nombre;
     private String apellido;
     private String telefono;
     private String email;
     private String tipo;
     private LocalDateTime fechaCreacion;
     private LocalDateTime fechaModificacion;
     private List<VehiculoDTO> vehiculos;

     public ClienteDTO() {}

     public ClienteDTO(String idCliente, String nombre, String apellido, String telefono, String email, String tipo) {
         this.idCliente = idCliente;
         this.nombre = nombre;
         this.apellido = apellido;
         this.telefono = telefono;
         this.email = email;
         this.tipo = tipo;
     }

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

     public List<VehiculoDTO> getVehiculos() { return vehiculos; }
     public void setVehiculos(List<VehiculoDTO> vehiculos) { this.vehiculos = vehiculos; }
}