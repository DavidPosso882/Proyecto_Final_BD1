package com.motorplus.web.controller;

import com.motorplus.domain.entity.Factura;
import com.motorplus.service.FacturaService;
import com.motorplus.web.dto.FacturaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/facturas")
@CrossOrigin(origins = "*")
public class FacturaController {

    @Autowired
    private FacturaService facturaService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Porcentaje de mano de obra (15%)
    private static final BigDecimal PORCENTAJE_MANO_OBRA = new BigDecimal("0.15");
    
    // Porcentaje de IVA (19%)
    private static final BigDecimal PORCENTAJE_IVA = new BigDecimal("0.19");

    @GetMapping
    public ResponseEntity<List<FacturaDTO>> getAllFacturas() {
        List<FacturaDTO> facturas = facturaService.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(facturas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacturaDTO> getFacturaById(@PathVariable Integer id) {
        Optional<Factura> factura = facturaService.findById(id);
        if (factura.isPresent()) {
            return ResponseEntity.ok(convertToDTO(factura.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<FacturaDTO> createFactura(@RequestBody FacturaDTO facturaDTO) {
        try {
            // Calcular el subtotal basado en servicios y repuestos de la orden
            Integer ordenCodigo = facturaDTO.getOrdenCodigo();
            
            // Calcular total de servicios
            String sqlServicios = """
                SELECT COALESCE(SUM(os.cantidad * os.precio_aplicado), 0) as total_servicios
                FROM OrdenServicio os
                WHERE os.orden_codigo = ?
                """;
            BigDecimal totalServicios = jdbcTemplate.queryForObject(sqlServicios, 
                (rs, rowNum) -> rs.getBigDecimal("total_servicios"), ordenCodigo);
            
            // Calcular total de repuestos
            String sqlRepuestos = """
                SELECT COALESCE(SUM(orep.cantidad_usada * orep.precio_aplicado), 0) as total_repuestos
                FROM OrdenRepuesto orep
                WHERE orep.orden_codigo = ?
                """;
            BigDecimal totalRepuestos = jdbcTemplate.queryForObject(sqlRepuestos, 
                (rs, rowNum) -> rs.getBigDecimal("total_repuestos"), ordenCodigo);
            
            // Calcular subtotal (servicios + repuestos)
            BigDecimal subtotal = (totalServicios != null ? totalServicios : BigDecimal.ZERO)
                .add(totalRepuestos != null ? totalRepuestos : BigDecimal.ZERO);
            
            // Agregar mano de obra (15% del subtotal)
            BigDecimal manoObra = subtotal.multiply(PORCENTAJE_MANO_OBRA)
                .setScale(2, RoundingMode.HALF_UP);
            
            // Subtotal con mano de obra
            BigDecimal subtotalConManoObra = subtotal.add(manoObra);
            
            // Calcular IVA (19% del subtotal con mano de obra)
            BigDecimal iva = subtotalConManoObra.multiply(PORCENTAJE_IVA)
                .setScale(2, RoundingMode.HALF_UP);
            
            // Calcular total (subtotal con mano de obra + IVA)
            BigDecimal total = subtotalConManoObra.add(iva);
            
            // Crear la factura con los valores calculados
            Factura factura = convertToEntity(facturaDTO);
            factura.setSubtotal(subtotalConManoObra); // Subtotal incluye mano de obra
            factura.setTotal(total); // Total incluye IVA
            factura.setFechaCreacion(LocalDateTime.now());
            factura.setFechaModificacion(LocalDateTime.now());
            
            Factura savedFactura = facturaService.save(factura);
            return ResponseEntity.ok(convertToDTO(savedFactura));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<FacturaDTO> updateFactura(@PathVariable Integer id, @RequestBody FacturaDTO facturaDTO) {
        if (!facturaService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Factura factura = convertToEntity(facturaDTO);
        factura.setIdFactura(id);
        factura.setFechaModificacion(LocalDateTime.now());
        Factura updatedFactura = facturaService.save(factura);
        return ResponseEntity.ok(convertToDTO(updatedFactura));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFactura(@PathVariable Integer id) {
        if (!facturaService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        facturaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private FacturaDTO convertToDTO(Factura factura) {
        FacturaDTO dto = new FacturaDTO();
        dto.setIdFactura(factura.getIdFactura());
        dto.setFechaEmision(factura.getFechaEmision());
        dto.setSubtotal(factura.getSubtotal());
        dto.setTotal(factura.getTotal());
        dto.setEstadoPago(factura.getEstadoPago());
        dto.setOrdenCodigo(factura.getOrdenCodigo());
        dto.setFechaCreacion(factura.getFechaCreacion());
        dto.setFechaModificacion(factura.getFechaModificacion());
        dto.setFechaPago(factura.getFechaPago());
        dto.setUsuarioCreacion(factura.getUsuarioCreacion());
        return dto;
    }

    private Factura convertToEntity(FacturaDTO dto) {
        Factura factura = new Factura();
        factura.setIdFactura(dto.getIdFactura());
        factura.setFechaEmision(dto.getFechaEmision());
        factura.setSubtotal(dto.getSubtotal());
        factura.setTotal(dto.getTotal());
        factura.setEstadoPago(dto.getEstadoPago());
        factura.setOrdenCodigo(dto.getOrdenCodigo());
        factura.setFechaCreacion(dto.getFechaCreacion());
        factura.setFechaModificacion(dto.getFechaModificacion());
        factura.setFechaPago(dto.getFechaPago());
        factura.setUsuarioCreacion(dto.getUsuarioCreacion());
        return factura;
    }
}