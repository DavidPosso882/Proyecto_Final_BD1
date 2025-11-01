package com.motorplus.domain.entity;

import java.io.Serializable;
import java.util.Objects;

public class MecanicoEspecialidadId implements Serializable {
    private Integer mecanicoId;
    private Integer especialidadCodigo;

    public MecanicoEspecialidadId() {}

    public MecanicoEspecialidadId(Integer mecanicoId, Integer especialidadCodigo) {
        this.mecanicoId = mecanicoId;
        this.especialidadCodigo = especialidadCodigo;
    }

    // Getters and Setters
    public Integer getMecanicoId() { return mecanicoId; }
    public void setMecanicoId(Integer mecanicoId) { this.mecanicoId = mecanicoId; }

    public Integer getEspecialidadCodigo() { return especialidadCodigo; }
    public void setEspecialidadCodigo(Integer especialidadCodigo) { this.especialidadCodigo = especialidadCodigo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MecanicoEspecialidadId that = (MecanicoEspecialidadId) o;
        return Objects.equals(mecanicoId, that.mecanicoId) &&
               Objects.equals(especialidadCodigo, that.especialidadCodigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mecanicoId, especialidadCodigo);
    }
}