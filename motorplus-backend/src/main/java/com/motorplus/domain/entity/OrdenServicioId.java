package com.motorplus.domain.entity;

import java.io.Serializable;
import java.util.Objects;

public class OrdenServicioId implements Serializable {
    private Integer ordenCodigo;
    private Integer servicioCodigo;

    public OrdenServicioId() {}

    public OrdenServicioId(Integer ordenCodigo, Integer servicioCodigo) {
        this.ordenCodigo = ordenCodigo;
        this.servicioCodigo = servicioCodigo;
    }

    // Getters and Setters
    public Integer getOrdenCodigo() { return ordenCodigo; }
    public void setOrdenCodigo(Integer ordenCodigo) { this.ordenCodigo = ordenCodigo; }

    public Integer getServicioCodigo() { return servicioCodigo; }
    public void setServicioCodigo(Integer servicioCodigo) { this.servicioCodigo = servicioCodigo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrdenServicioId that = (OrdenServicioId) o;
        return Objects.equals(ordenCodigo, that.ordenCodigo) &&
               Objects.equals(servicioCodigo, that.servicioCodigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ordenCodigo, servicioCodigo);
    }
}