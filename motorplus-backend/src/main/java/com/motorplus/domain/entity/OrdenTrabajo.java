package com.motorplus.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "OrdenTrabajo")
public class OrdenTrabajo {
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     @Column(name = "codigo")
     private Integer codigo;

     @Column(name = "fecha_ingreso", nullable = false)
     @NotNull(message = "La fecha de ingreso es obligatoria")
     @PastOrPresent(message = "La fecha de ingreso no puede ser futura")
     private LocalDate fechaIngreso;

     @Column(name = "diagnostico_inicial", columnDefinition = "TEXT")
     @Size(max = 1000, message = "El diagnóstico inicial no puede exceder 1000 caracteres")
     private String diagnosticoInicial;

     @Column(name = "estado", nullable = false, length = 50)
     @NotBlank(message = "El estado es obligatorio")
     @Pattern(regexp = "^(PENDIENTE|EN_PROCESO|COMPLETADA|CANCELADA)$", message = "Estado inválido")
     private String estado;

     @Column(name = "placa", nullable = false, length = 20)
     @NotBlank(message = "La placa es obligatoria")
     private String placa;

     @Column(name = "fecha_creacion")
     private LocalDateTime fechaCreacion;

     @Column(name = "fecha_modificacion")
     private LocalDateTime fechaModificacion;

     @Column(name = "usuario_creacion", length = 50)
     @Size(max = 50, message = "El usuario de creación no puede exceder 50 caracteres")
     private String usuarioCreacion;

     @Column(name = "usuario_modificacion", length = 50)
     @Size(max = 50, message = "El usuario de modificación no puede exceder 50 caracteres")
     private String usuarioModificacion;

     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "placa", insertable = false, updatable = false)
     private Vehiculo vehiculo;

     @OneToMany(mappedBy = "ordenTrabajo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
     private List<OrdenServicio> ordenServicios;

     @OneToMany(mappedBy = "ordenTrabajo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
     private List<OrdenRepuesto> ordenRepuestos;

     @OneToMany(mappedBy = "ordenTrabajo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
     private List<OrdenMecanico> ordenMecanicos;

     // Getters and Setters
     public Integer getCodigo() { return codigo; }
     public void setCodigo(Integer codigo) { this.codigo = codigo; }

     public LocalDate getFechaIngreso() { return fechaIngreso; }
     public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }

     public String getDiagnosticoInicial() { return diagnosticoInicial; }
     public void setDiagnosticoInicial(String diagnosticoInicial) { this.diagnosticoInicial = diagnosticoInicial; }

     public String getEstado() { return estado; }
     public void setEstado(String estado) { this.estado = estado; }

     public String getPlaca() { return placa; }
     public void setPlaca(String placa) { this.placa = placa; }

     public LocalDateTime getFechaCreacion() { return fechaCreacion; }
     public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

     public LocalDateTime getFechaModificacion() { return fechaModificacion; }
     public void setFechaModificacion(LocalDateTime fechaModificacion) { this.fechaModificacion = fechaModificacion; }

     public String getUsuarioCreacion() { return usuarioCreacion; }
     public void setUsuarioCreacion(String usuarioCreacion) { this.usuarioCreacion = usuarioCreacion; }

     public String getUsuarioModificacion() { return usuarioModificacion; }
     public void setUsuarioModificacion(String usuarioModificacion) { this.usuarioModificacion = usuarioModificacion; }

     public Vehiculo getVehiculo() { return vehiculo; }
     public void setVehiculo(Vehiculo vehiculo) { this.vehiculo = vehiculo; }

     public List<OrdenServicio> getOrdenServicios() { return ordenServicios; }
     public void setOrdenServicios(List<OrdenServicio> ordenServicios) { this.ordenServicios = ordenServicios; }

     public List<OrdenRepuesto> getOrdenRepuestos() { return ordenRepuestos; }
     public void setOrdenRepuestos(List<OrdenRepuesto> ordenRepuestos) { this.ordenRepuestos = ordenRepuestos; }

     public List<OrdenMecanico> getOrdenMecanicos() { return ordenMecanicos; }
     public void setOrdenMecanicos(List<OrdenMecanico> ordenMecanicos) { this.ordenMecanicos = ordenMecanicos; }
}