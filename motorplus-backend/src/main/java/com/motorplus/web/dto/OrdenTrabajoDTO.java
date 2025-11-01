package com.motorplus.web.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class OrdenTrabajoDTO {
    private Integer codigo;
    private LocalDate fechaIngreso;
    private String diagnosticoInicial;
    private String estado;
    private String placa;
    private Integer mecanicoId;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
    private String usuarioCreacion;

    public OrdenTrabajoDTO() {}

    public OrdenTrabajoDTO(Integer codigo, LocalDate fechaIngreso, String diagnosticoInicial, String estado, String placa, Integer mecanicoId) {
        this.codigo = codigo;
        this.fechaIngreso = fechaIngreso;
        this.diagnosticoInicial = diagnosticoInicial;
        this.estado = estado;
        this.placa = placa;
        this.mecanicoId = mecanicoId;
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

    public Integer getMecanicoId() { return mecanicoId; }
    public void setMecanicoId(Integer mecanicoId) { this.mecanicoId = mecanicoId; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaModificacion() { return fechaModificacion; }
    public void setFechaModificacion(LocalDateTime fechaModificacion) { this.fechaModificacion = fechaModificacion; }

    public String getUsuarioCreacion() { return usuarioCreacion; }
    public void setUsuarioCreacion(String usuarioCreacion) { this.usuarioCreacion = usuarioCreacion; }
}