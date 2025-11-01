package com.motorplus.web.controller;

import com.motorplus.domain.entity.Factura;
import com.motorplus.service.FacturaService;
import com.motorplus.web.dto.FacturaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        Factura factura = convertToEntity(facturaDTO);
        factura.setFechaCreacion(LocalDateTime.now());
        factura.setFechaModificacion(LocalDateTime.now());
        Factura savedFactura = facturaService.save(factura);
        return ResponseEntity.ok(convertToDTO(savedFactura));
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