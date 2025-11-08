package com.motorplus.web.controller;

import com.motorplus.domain.entity.Especialidad;
import com.motorplus.service.EspecialidadService;
import com.motorplus.web.dto.EspecialidadDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/especialidades")
@CrossOrigin(origins = "*")
public class EspecialidadController {

    @Autowired
    private EspecialidadService especialidadService;

    @GetMapping
    public ResponseEntity<List<EspecialidadDTO>> getAllEspecialidades() {
        List<EspecialidadDTO> especialidades = especialidadService.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(especialidades);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EspecialidadDTO> getEspecialidadById(@PathVariable Integer id) {
        Optional<Especialidad> especialidad = especialidadService.findById(Long.valueOf(id));
        if (especialidad.isPresent()) {
            return ResponseEntity.ok(convertToDTO(especialidad.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<EspecialidadDTO> createEspecialidad(@RequestBody EspecialidadDTO especialidadDTO) {
        Especialidad especialidad = convertToEntity(especialidadDTO);
        especialidad.setFechaCreacion(LocalDateTime.now());
        especialidad.setFechaModificacion(LocalDateTime.now());
        Especialidad savedEspecialidad = especialidadService.save(especialidad);
        return ResponseEntity.ok(convertToDTO(savedEspecialidad));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EspecialidadDTO> updateEspecialidad(@PathVariable Integer id, @RequestBody EspecialidadDTO especialidadDTO) {
        if (!especialidadService.findById(Long.valueOf(id)).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Especialidad especialidad = convertToEntity(especialidadDTO);
        especialidad.setCodigo(id);
        especialidad.setFechaModificacion(LocalDateTime.now());
        Especialidad updatedEspecialidad = especialidadService.save(especialidad);
        return ResponseEntity.ok(convertToDTO(updatedEspecialidad));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEspecialidad(@PathVariable Integer id) {
        if (!especialidadService.findById(Long.valueOf(id)).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        especialidadService.deleteById(Long.valueOf(id));
        return ResponseEntity.noContent().build();
    }

    private EspecialidadDTO convertToDTO(Especialidad especialidad) {
        EspecialidadDTO dto = new EspecialidadDTO();
        dto.setCodigo(especialidad.getCodigo());
        dto.setNombre(especialidad.getNombre());
        dto.setDescripcion(especialidad.getDescripcion());
        dto.setFechaCreacion(especialidad.getFechaCreacion());
        dto.setFechaModificacion(especialidad.getFechaModificacion());
        return dto;
    }

    private Especialidad convertToEntity(EspecialidadDTO dto) {
        Especialidad especialidad = new Especialidad();
        especialidad.setCodigo(dto.getCodigo());
        especialidad.setNombre(dto.getNombre());
        especialidad.setDescripcion(dto.getDescripcion());
        especialidad.setFechaCreacion(dto.getFechaCreacion());
        especialidad.setFechaModificacion(dto.getFechaModificacion());
        return especialidad;
    }
}