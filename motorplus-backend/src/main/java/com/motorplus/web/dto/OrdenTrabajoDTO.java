package com.motorplus.web.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class OrdenTrabajoDTO {
     private Integer codigo;
     private LocalDate fechaIngreso;
     private String diagnosticoInicial;
     private String estado;
     private String placa;
     private LocalDateTime fechaCreacion;
     private LocalDateTime fechaModificacion;
     private String usuarioCreacion;
     private String usuarioModificacion;

     // Relaciones
     private VehiculoDTO vehiculo;
     private ClienteDTO cliente;
     private List<OrdenServicioDTO> servicios;
     private List<OrdenRepuestoDTO> repuestos;
     private List<OrdenMecanicoDTO> mecanicos;

     public OrdenTrabajoDTO() {}

     public OrdenTrabajoDTO(Integer codigo, LocalDate fechaIngreso, String diagnosticoInicial, String estado, String placa) {
         this.codigo = codigo;
         this.fechaIngreso = fechaIngreso;
         this.diagnosticoInicial = diagnosticoInicial;
         this.estado = estado;
         this.placa = placa;
     }

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

     public VehiculoDTO getVehiculo() { return vehiculo; }
     public void setVehiculo(VehiculoDTO vehiculo) { this.vehiculo = vehiculo; }

     public ClienteDTO getCliente() { return cliente; }
     public void setCliente(ClienteDTO cliente) { this.cliente = cliente; }

     public List<OrdenServicioDTO> getServicios() { return servicios; }
     public void setServicios(List<OrdenServicioDTO> servicios) { this.servicios = servicios; }

     public List<OrdenRepuestoDTO> getRepuestos() { return repuestos; }
     public void setRepuestos(List<OrdenRepuestoDTO> repuestos) { this.repuestos = repuestos; }

     public List<OrdenMecanicoDTO> getMecanicos() { return mecanicos; }
     public void setMecanicos(List<OrdenMecanicoDTO> mecanicos) { this.mecanicos = mecanicos; }
}