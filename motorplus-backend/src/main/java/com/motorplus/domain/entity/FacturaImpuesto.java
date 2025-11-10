package com.motorplus.domain.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "FacturaImpuesto")
@IdClass(FacturaImpuestoId.class)
public class FacturaImpuesto {
    @Id
    @Column(name = "factura_id", nullable = false)
    private Integer facturaId;

    @Id
    @Column(name = "impuesto_codigo", nullable = false)
    private Integer impuestoCodigo;

    @Column(name = "monto_aplicado", precision = 10, scale = 2)
    private BigDecimal montoAplicado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factura_id", insertable = false, updatable = false)
    private Factura factura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "impuesto_codigo", insertable = false, updatable = false)
    private Impuesto impuesto;

    // Getters and Setters
    public Integer getFacturaId() { return facturaId; }
    public void setFacturaId(Integer facturaId) { this.facturaId = facturaId; }

    public Integer getImpuestoCodigo() { return impuestoCodigo; }
    public void setImpuestoCodigo(Integer impuestoCodigo) { this.impuestoCodigo = impuestoCodigo; }

    public BigDecimal getMontoAplicado() { return montoAplicado; }
    public void setMontoAplicado(BigDecimal montoAplicado) { this.montoAplicado = montoAplicado; }

    public Factura getFactura() { return factura; }
    public void setFactura(Factura factura) { this.factura = factura; }

    public Impuesto getImpuesto() { return impuesto; }
    public void setImpuesto(Impuesto impuesto) { this.impuesto = impuesto; }
}