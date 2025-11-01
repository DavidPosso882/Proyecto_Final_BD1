package com.motorplus.web.controller;

import com.motorplus.domain.entity.Proveedor;
import com.motorplus.service.ProveedorService;
import com.motorplus.web.dto.ProveedorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/proveedores")
@CrossOrigin(origins = "*")
public class ProveedorController {

    @Autowired
    private ProveedorService proveedorService;

    @GetMapping
    public ResponseEntity<List<ProveedorDTO>> getAllProveedores() {
        List<ProveedorDTO> proveedores = proveedorService.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(proveedores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorDTO> getProveedorById(@PathVariable Integer id) {
        Optional<Proveedor> proveedor = proveedorService.findById(id);
        if (proveedor.isPresent()) {
            return ResponseEntity.ok(convertToDTO(proveedor.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<ProveedorDTO> createProveedor(@RequestBody ProveedorDTO proveedorDTO) {
        Proveedor proveedor = convertToEntity(proveedorDTO);
        proveedor.setFechaCreacion(LocalDateTime.now());
        proveedor.setFechaModificacion(LocalDateTime.now());
        Proveedor savedProveedor = proveedorService.save(proveedor);
        return ResponseEntity.ok(convertToDTO(savedProveedor));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProveedorDTO> updateProveedor(@PathVariable Integer id, @RequestBody ProveedorDTO proveedorDTO) {
        if (!proveedorService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Proveedor proveedor = convertToEntity(proveedorDTO);
        proveedor.setId(id);
        proveedor.setFechaModificacion(LocalDateTime.now());
        Proveedor updatedProveedor = proveedorService.save(proveedor);
        return ResponseEntity.ok(convertToDTO(updatedProveedor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProveedor(@PathVariable Integer id) {
        if (!proveedorService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        proveedorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private ProveedorDTO convertToDTO(Proveedor proveedor) {
        ProveedorDTO dto = new ProveedorDTO();
        dto.setId(proveedor.getId());
        dto.setNombre(proveedor.getNombre());
        dto.setCorreo(proveedor.getCorreo());
        dto.setTelefono(proveedor.getTelefono());
        dto.setActivo(proveedor.getActivo());
        dto.setFechaCreacion(proveedor.getFechaCreacion());
        dto.setFechaModificacion(proveedor.getFechaModificacion());
        return dto;
    }

    private Proveedor convertToEntity(ProveedorDTO dto) {
        Proveedor proveedor = new Proveedor();
        proveedor.setId(dto.getId());
        proveedor.setNombre(dto.getNombre());
        proveedor.setCorreo(dto.getCorreo());
        proveedor.setTelefono(dto.getTelefono());
        proveedor.setActivo(dto.getActivo());
        proveedor.setFechaCreacion(dto.getFechaCreacion());
        proveedor.setFechaModificacion(dto.getFechaModificacion());
        return proveedor;
    }
}