package com.motorplus.domain.entity;

import java.io.Serializable;
import java.util.Objects;

public class FacturaImpuestoId implements Serializable {
    private Integer facturaId;
    private Integer impuestoCodigo;

    public FacturaImpuestoId() {}

    public FacturaImpuestoId(Integer facturaId, Integer impuestoCodigo) {
        this.facturaId = facturaId;
        this.impuestoCodigo = impuestoCodigo;
    }

    // Getters and Setters
    public Integer getFacturaId() { return facturaId; }
    public void setFacturaId(Integer facturaId) { this.facturaId = facturaId; }

    public Integer getImpuestoCodigo() { return impuestoCodigo; }
    public void setImpuestoCodigo(Integer impuestoCodigo) { this.impuestoCodigo = impuestoCodigo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FacturaImpuestoId that = (FacturaImpuestoId) o;
        return Objects.equals(facturaId, that.facturaId) &&
               Objects.equals(impuestoCodigo, that.impuestoCodigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(facturaId, impuestoCodigo);
    }
}