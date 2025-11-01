package com.motorplus.domain.entity;

import java.io.Serializable;
import java.util.Objects;

public class OrdenRepuestoId implements Serializable {
    private Integer ordenCodigo;
    private Integer repuestoCodigo;

    public OrdenRepuestoId() {}

    public OrdenRepuestoId(Integer ordenCodigo, Integer repuestoCodigo) {
        this.ordenCodigo = ordenCodigo;
        this.repuestoCodigo = repuestoCodigo;
    }

    // Getters and Setters
    public Integer getOrdenCodigo() { return ordenCodigo; }
    public void setOrdenCodigo(Integer ordenCodigo) { this.ordenCodigo = ordenCodigo; }

    public Integer getRepuestoCodigo() { return repuestoCodigo; }
    public void setRepuestoCodigo(Integer repuestoCodigo) { this.repuestoCodigo = repuestoCodigo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrdenRepuestoId that = (OrdenRepuestoId) o;
        return Objects.equals(ordenCodigo, that.ordenCodigo) &&
               Objects.equals(repuestoCodigo, that.repuestoCodigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ordenCodigo, repuestoCodigo);
    }
}