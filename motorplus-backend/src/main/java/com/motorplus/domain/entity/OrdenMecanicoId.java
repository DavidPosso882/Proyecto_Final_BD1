package com.motorplus.domain.entity;

import java.io.Serializable;
import java.util.Objects;

public class OrdenMecanicoId implements Serializable {
    private Integer mecanicoId;
    private Integer ordenCodigo;

    public OrdenMecanicoId() {}

    public OrdenMecanicoId(Integer mecanicoId, Integer ordenCodigo) {
        this.mecanicoId = mecanicoId;
        this.ordenCodigo = ordenCodigo;
    }

    // Getters and Setters
    public Integer getMecanicoId() { return mecanicoId; }
    public void setMecanicoId(Integer mecanicoId) { this.mecanicoId = mecanicoId; }

    public Integer getOrdenCodigo() { return ordenCodigo; }
    public void setOrdenCodigo(Integer ordenCodigo) { this.ordenCodigo = ordenCodigo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrdenMecanicoId that = (OrdenMecanicoId) o;
        return Objects.equals(mecanicoId, that.mecanicoId) &&
               Objects.equals(ordenCodigo, that.ordenCodigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mecanicoId, ordenCodigo);
    }
}