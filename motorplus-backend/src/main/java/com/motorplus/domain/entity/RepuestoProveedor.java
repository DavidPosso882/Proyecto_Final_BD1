package com.motorplus.domain.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "RepuestoProveedor")
@IdClass(RepuestoProveedorId.class)
public class RepuestoProveedor {
    @Id
    @Column(name = "repuesto_codigo", nullable = false)
    private Integer repuestoCodigo;

    @Id
    @Column(name = "proveedor_id", nullable = false)
    private Integer proveedorId;

    @Column(name = "precio_compra", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioCompra;

    @Column(name = "tiempo_entrega_dias")
    private Integer tiempoEntregaDias;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repuesto_codigo", insertable = false, updatable = false)
    private Repuesto repuesto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id", insertable = false, updatable = false)
    private Proveedor proveedor;

    // Getters and Setters
    public Integer getRepuestoCodigo() { return repuestoCodigo; }
    public void setRepuestoCodigo(Integer repuestoCodigo) { this.repuestoCodigo = repuestoCodigo; }

    public Integer getProveedorId() { return proveedorId; }
    public void setProveedorId(Integer proveedorId) { this.proveedorId = proveedorId; }

    public BigDecimal getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(BigDecimal precioCompra) { this.precioCompra = precioCompra; }

    public Integer getTiempoEntregaDias() { return tiempoEntregaDias; }
    public void setTiempoEntregaDias(Integer tiempoEntregaDias) { this.tiempoEntregaDias = tiempoEntregaDias; }

    public Repuesto getRepuesto() { return repuesto; }
    public void setRepuesto(Repuesto repuesto) { this.repuesto = repuesto; }

    public Proveedor getProveedor() { return proveedor; }
    public void setProveedor(Proveedor proveedor) { this.proveedor = proveedor; }
}