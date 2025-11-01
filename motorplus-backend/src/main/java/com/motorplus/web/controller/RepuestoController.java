package com.motorplus.web.controller;

import com.motorplus.domain.entity.Repuesto;
import com.motorplus.service.RepuestoService;
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
        Repuesto repuesto = convertToEntity(repuestoDTO);
        repuesto.setFechaCreacion(LocalDateTime.now());
        repuesto.setFechaModificacion(LocalDateTime.now());
        Repuesto savedRepuesto = repuestoService.save(repuesto);
        return ResponseEntity.ok(convertToDTO(savedRepuesto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RepuestoDTO> updateRepuesto(@PathVariable Integer id, @RequestBody RepuestoDTO repuestoDTO) {
        if (!repuestoService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Repuesto repuesto = convertToEntity(repuestoDTO);
        repuesto.setCodigo(id);
        repuesto.setFechaModificacion(LocalDateTime.now());
        Repuesto updatedRepuesto = repuestoService.save(repuesto);
        return ResponseEntity.ok(convertToDTO(updatedRepuesto));
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
        dto.setFechaCreacion(repuesto.getFechaCreacion());
        dto.setFechaModificacion(repuesto.getFechaModificacion());
        return dto;
    }

    private Repuesto convertToEntity(RepuestoDTO dto) {
        Repuesto repuesto = new Repuesto();
        repuesto.setCodigo(dto.getCodigo());
        repuesto.setNombre(dto.getNombre());
        repuesto.setDescripcion(dto.getDescripcion());
        repuesto.setPrecioUnitario(dto.getPrecioUnitario());
        repuesto.setStock(dto.getStock());
        repuesto.setFechaCreacion(dto.getFechaCreacion());
        repuesto.setFechaModificacion(dto.getFechaModificacion());
        return repuesto;
    }
}