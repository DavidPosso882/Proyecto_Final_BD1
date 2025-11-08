package com.motorplus.web.dto;

import java.math.BigDecimal;

public class OrdenMecanicoDTO {
    private Integer mecanicoId;
    private Integer ordenCodigo;
    private String rol;
    private BigDecimal horasTrabajadas;
    private MecanicoDTO mecanico;

    public OrdenMecanicoDTO() {}

    public OrdenMecanicoDTO(Integer mecanicoId, Integer ordenCodigo, String rol, BigDecimal horasTrabajadas) {
        this.mecanicoId = mecanicoId;
        this.ordenCodigo = ordenCodigo;
        this.rol = rol;
        this.horasTrabajadas = horasTrabajadas;
    }

    // Getters and Setters
    public Integer getMecanicoId() { return mecanicoId; }
    public void setMecanicoId(Integer mecanicoId) { this.mecanicoId = mecanicoId; }

    public Integer getOrdenCodigo() { return ordenCodigo; }
    public void setOrdenCodigo(Integer ordenCodigo) { this.ordenCodigo = ordenCodigo; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public BigDecimal getHorasTrabajadas() { return horasTrabajadas; }
    public void setHorasTrabajadas(BigDecimal horasTrabajadas) { this.horasTrabajadas = horasTrabajadas; }

    public MecanicoDTO getMecanico() { return mecanico; }
    public void setMecanico(MecanicoDTO mecanico) { this.mecanico = mecanico; }
}