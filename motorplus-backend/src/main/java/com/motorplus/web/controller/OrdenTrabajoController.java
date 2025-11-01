package com.motorplus.web.controller;

import com.motorplus.domain.entity.OrdenTrabajo;
import com.motorplus.service.OrdenTrabajoService;
import com.motorplus.web.dto.OrdenTrabajoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ordenes-trabajo")
@CrossOrigin(origins = "*")
public class OrdenTrabajoController {

    @Autowired
    private OrdenTrabajoService ordenTrabajoService;

    @GetMapping
    public ResponseEntity<List<OrdenTrabajoDTO>> getAllOrdenesTrabajo() {
        List<OrdenTrabajoDTO> ordenes = ordenTrabajoService.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ordenes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenTrabajoDTO> getOrdenTrabajoById(@PathVariable Integer id) {
        Optional<OrdenTrabajo> orden = ordenTrabajoService.findById(id);
        if (orden.isPresent()) {
            return ResponseEntity.ok(convertToDTO(orden.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<OrdenTrabajoDTO> createOrdenTrabajo(@RequestBody OrdenTrabajoDTO ordenTrabajoDTO) {
        OrdenTrabajo orden = convertToEntity(ordenTrabajoDTO);
        orden.setFechaCreacion(LocalDateTime.now());
        orden.setFechaModificacion(LocalDateTime.now());
        OrdenTrabajo savedOrden = ordenTrabajoService.save(orden);
        return ResponseEntity.ok(convertToDTO(savedOrden));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrdenTrabajoDTO> updateOrdenTrabajo(@PathVariable Integer id, @RequestBody OrdenTrabajoDTO ordenTrabajoDTO) {
        if (!ordenTrabajoService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        OrdenTrabajo orden = convertToEntity(ordenTrabajoDTO);
        orden.setCodigo(id);
        orden.setFechaModificacion(LocalDateTime.now());
        OrdenTrabajo updatedOrden = ordenTrabajoService.save(orden);
        return ResponseEntity.ok(convertToDTO(updatedOrden));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrdenTrabajo(@PathVariable Integer id) {
        if (!ordenTrabajoService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        ordenTrabajoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private OrdenTrabajoDTO convertToDTO(OrdenTrabajo orden) {
        OrdenTrabajoDTO dto = new OrdenTrabajoDTO();
        dto.setCodigo(orden.getCodigo());
        dto.setFechaIngreso(orden.getFechaIngreso());
        dto.setDiagnosticoInicial(orden.getDiagnosticoInicial());
        dto.setEstado(orden.getEstado());
        dto.setPlaca(orden.getPlaca());
        dto.setFechaCreacion(orden.getFechaCreacion());
        dto.setFechaModificacion(orden.getFechaModificacion());
        dto.setUsuarioCreacion(orden.getUsuarioCreacion());
        return dto;
    }

    private OrdenTrabajo convertToEntity(OrdenTrabajoDTO dto) {
        OrdenTrabajo orden = new OrdenTrabajo();
        orden.setCodigo(dto.getCodigo());
        orden.setFechaIngreso(dto.getFechaIngreso());
        orden.setDiagnosticoInicial(dto.getDiagnosticoInicial());
        orden.setEstado(dto.getEstado());
        orden.setPlaca(dto.getPlaca());
        orden.setFechaCreacion(dto.getFechaCreacion());
        orden.setFechaModificacion(dto.getFechaModificacion());
        orden.setUsuarioCreacion(dto.getUsuarioCreacion());
        return orden;
    }
}