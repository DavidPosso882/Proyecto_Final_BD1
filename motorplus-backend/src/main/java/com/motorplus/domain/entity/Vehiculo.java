package com.motorplus.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Vehiculo")
public class Vehiculo {
     @Id
     @Column(name = "placa", nullable = false, length = 20)
     @NotBlank(message = "La placa es obligatoria")
     @Size(max = 20, message = "La placa no puede exceder 20 caracteres")
     @Pattern(regexp = "^[A-Z0-9\\-]*$", message = "La placa solo puede contener letras mayúsculas, números y guiones")
     private String placa;

     @Column(name = "tipo", nullable = false, length = 50)
     @NotBlank(message = "El tipo es obligatorio")
     @Pattern(regexp = "^(AUTOMOVIL|MOTOCICLETA|CAMIONETA|CAMION)$", message = "Tipo de vehículo inválido")
     private String tipo;

     @Column(name = "marca", nullable = false, length = 50)
     @NotBlank(message = "La marca es obligatoria")
     @Size(max = 50, message = "La marca no puede exceder 50 caracteres")
     private String marca;

     @Column(name = "modelo", length = 50)
     @Size(max = 50, message = "El modelo no puede exceder 50 caracteres")
     private String modelo;

     @Column(name = "anio")
     @Min(value = 1900, message = "El año debe ser mayor o igual a 1900")
     @Max(value = 2100, message = "El año debe ser menor o igual a 2100")
     private Integer anio;

     @Column(name = "documento_cliente", nullable = false, length = 20)
     @NotBlank(message = "El documento del cliente es obligatorio")
     private String documentoCliente;

     @Column(name = "fecha_creacion")
     private LocalDateTime fechaCreacion;

     @Column(name = "fecha_modificacion")
     private LocalDateTime fechaModificacion;

     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "documento_cliente", insertable = false, updatable = false)
     private Cliente cliente;

     @OneToMany(mappedBy = "vehiculo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
     private List<OrdenTrabajo> ordenesTrabajo;

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

     public Cliente getCliente() { return cliente; }
     public void setCliente(Cliente cliente) { this.cliente = cliente; }

     public List<OrdenTrabajo> getOrdenesTrabajo() { return ordenesTrabajo; }
     public void setOrdenesTrabajo(List<OrdenTrabajo> ordenesTrabajo) { this.ordenesTrabajo = ordenesTrabajo; }
}