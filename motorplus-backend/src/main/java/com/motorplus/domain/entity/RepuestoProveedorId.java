package com.motorplus.domain.entity;

import java.io.Serializable;
import java.util.Objects;

public class RepuestoProveedorId implements Serializable {
    private Integer repuestoCodigo;
    private Integer proveedorId;

    public RepuestoProveedorId() {}

    public RepuestoProveedorId(Integer repuestoCodigo, Integer proveedorId) {
        this.repuestoCodigo = repuestoCodigo;
        this.proveedorId = proveedorId;
    }

    // Getters and Setters
    public Integer getRepuestoCodigo() { return repuestoCodigo; }
    public void setRepuestoCodigo(Integer repuestoCodigo) { this.repuestoCodigo = repuestoCodigo; }

    public Integer getProveedorId() { return proveedorId; }
    public void setProveedorId(Integer proveedorId) { this.proveedorId = proveedorId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RepuestoProveedorId that = (RepuestoProveedorId) o;
        return Objects.equals(repuestoCodigo, that.repuestoCodigo) &&
               Objects.equals(proveedorId, that.proveedorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(repuestoCodigo, proveedorId);
    }
}