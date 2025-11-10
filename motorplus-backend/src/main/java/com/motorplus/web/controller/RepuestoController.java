package com.motorplus.web.controller;

import com.motorplus.domain.entity.Repuesto;
import com.motorplus.domain.entity.RepuestoProveedor;
import com.motorplus.service.RepuestoService;
import com.motorplus.service.RepuestoProveedorService;
import com.motorplus.web.dto.RepuestoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/repuestos")
@CrossOrigin(origins = "*")
public class RepuestoController {

    @Autowired
    private RepuestoService repuestoService;

    @Autowired
    private RepuestoProveedorService repuestoProveedorService;

    @GetMapping
    public ResponseEntity<List<RepuestoDTO>> getAllRepuestos() {
        List<RepuestoDTO> repuestos = repuestoService.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(repuestos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RepuestoDTO> getRepuestoById(@PathVariable Integer id) {
        Optional<Repuesto> repuesto = repuestoService.findById(id);
        if (repuesto.isPresent()) {
            return ResponseEntity.ok(convertToDTO(repuesto.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<RepuestoDTO> createRepuesto(@RequestBody RepuestoDTO repuestoDTO) {
        // Validar que precio no sea null
        if (repuestoDTO.getPrecioUnitario() == null) {
            throw new IllegalArgumentException("El precio unitario es obligatorio");
        }

        Repuesto repuesto = convertToEntity(repuestoDTO);
        repuesto.setFechaCreacion(LocalDateTime.now());
        repuesto.setFechaModificacion(LocalDateTime.now());
        Repuesto savedRepuesto = repuestoService.save(repuesto);
        
        // Guardar la relación con el proveedor si se proporcionó
        if (repuestoDTO.getIdProveedor() != null) {
            RepuestoProveedor repuestoProveedor = new RepuestoProveedor();
            repuestoProveedor.setRepuestoCodigo(savedRepuesto.getCodigo());
            repuestoProveedor.setProveedorId(repuestoDTO.getIdProveedor());
            repuestoProveedor.setPrecioCompra(repuestoDTO.getPrecioUnitario());
            repuestoProveedor.setTiempoEntregaDias(7); // Valor por defecto
            repuestoProveedorService.save(repuestoProveedor);
        }
        
        return ResponseEntity.ok(convertToDTO(savedRepuesto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RepuestoDTO> updateRepuesto(@PathVariable Integer id, @RequestBody RepuestoDTO repuestoDTO) {
        try {
            Optional<Repuesto> repuestoExistente = repuestoService.findById(id);
            if (!repuestoExistente.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            // Mantener la fecha de creación original
            Repuesto repuesto = convertToEntity(repuestoDTO);
            repuesto.setCodigo(id);
            repuesto.setFechaCreacion(repuestoExistente.get().getFechaCreacion());
            repuesto.setFechaModificacion(LocalDateTime.now());
            Repuesto updatedRepuesto = repuestoService.save(repuesto);
            
            // Actualizar la relación con el proveedor
            try {
                // Buscar si existe una relación previa
                List<RepuestoProveedor> relacionesExistentes = repuestoProveedorService.findByRepuestoCodigo(id);
                
                if (repuestoDTO.getIdProveedor() != null) {
                    // Eliminar relaciones anteriores si existen
                    for (RepuestoProveedor relacionExistente : relacionesExistentes) {
                        repuestoProveedorService.delete(relacionExistente);
                    }
                    
                    // Crear la nueva relación
                    RepuestoProveedor repuestoProveedor = new RepuestoProveedor();
                    repuestoProveedor.setRepuestoCodigo(id);
                    repuestoProveedor.setProveedorId(repuestoDTO.getIdProveedor());
                    repuestoProveedor.setPrecioCompra(repuestoDTO.getPrecioUnitario());
                    repuestoProveedor.setTiempoEntregaDias(7); // Valor por defecto
                    repuestoProveedorService.save(repuestoProveedor);
                } else {
                    // Si no se proporciona proveedor, eliminar relaciones existentes
                    for (RepuestoProveedor relacionExistente : relacionesExistentes) {
                        repuestoProveedorService.delete(relacionExistente);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error al actualizar relación con proveedor: " + e.getMessage());
                // Continuar aunque falle la actualización del proveedor
            }
            
            return ResponseEntity.ok(convertToDTO(updatedRepuesto));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRepuesto(@PathVariable Integer id) {
        if (!repuestoService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        repuestoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private RepuestoDTO convertToDTO(Repuesto repuesto) {
        RepuestoDTO dto = new RepuestoDTO();
        dto.setCodigo(repuesto.getCodigo());
        dto.setNombre(repuesto.getNombre());
        dto.setDescripcion(repuesto.getDescripcion());
        dto.setPrecioUnitario(repuesto.getPrecioUnitario());
        dto.setStock(repuesto.getStock());
        dto.setStockMinimo(repuesto.getStockMinimo());
        dto.setFechaCreacion(repuesto.getFechaCreacion());
        dto.setFechaModificacion(repuesto.getFechaModificacion());
        
        // Cargar el proveedor desde RepuestoProveedor
        Optional<RepuestoProveedor> repuestoProveedor = repuestoProveedorService.findFirstByRepuestoCodigo(repuesto.getCodigo());
        if (repuestoProveedor.isPresent()) {
            dto.setIdProveedor(repuestoProveedor.get().getProveedorId());
        }
        
        return dto;
    }

    private Repuesto convertToEntity(RepuestoDTO dto) {
        Repuesto repuesto = new Repuesto();
        repuesto.setCodigo(dto.getCodigo());
        repuesto.setNombre(dto.getNombre());
        repuesto.setDescripcion(dto.getDescripcion());
        repuesto.setPrecioUnitario(dto.getPrecioUnitario());
        repuesto.setStock(dto.getStock());
        repuesto.setStockMinimo(dto.getStockMinimo() != null ? dto.getStockMinimo() : 5);
        repuesto.setFechaCreacion(dto.getFechaCreacion());
        repuesto.setFechaModificacion(dto.getFechaModificacion());
        return repuesto;
    }
}