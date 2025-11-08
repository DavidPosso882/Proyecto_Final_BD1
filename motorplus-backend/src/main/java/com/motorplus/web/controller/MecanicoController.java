package com.motorplus.web.controller;

import com.motorplus.domain.entity.Mecanico;
import com.motorplus.service.MecanicoService;
import com.motorplus.web.dto.MecanicoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mecanicos")
@CrossOrigin(origins = "*")
public class MecanicoController {

    @Autowired
    private MecanicoService mecanicoService;

    @GetMapping
    public ResponseEntity<List<MecanicoDTO>> getAllMecanicos() {
        List<MecanicoDTO> mecanicos = mecanicoService.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(mecanicos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MecanicoDTO> getMecanicoById(@PathVariable Integer id) {
        Optional<Mecanico> mecanico = mecanicoService.findById(id);
        if (mecanico.isPresent()) {
            return ResponseEntity.ok(convertToDTO(mecanico.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<MecanicoDTO> createMecanico(@RequestBody MecanicoDTO mecanicoDTO) {
        Mecanico mecanico = convertToEntity(mecanicoDTO);
        mecanico.setFechaCreacion(LocalDateTime.now());
        mecanico.setFechaModificacion(LocalDateTime.now());
        Mecanico savedMecanico = mecanicoService.save(mecanico);
        return ResponseEntity.ok(convertToDTO(savedMecanico));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MecanicoDTO> updateMecanico(@PathVariable Integer id, @RequestBody MecanicoDTO mecanicoDTO) {
        if (!mecanicoService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Mecanico mecanico = convertToEntity(mecanicoDTO);
        mecanico.setIdMecanico(id);
        mecanico.setFechaModificacion(LocalDateTime.now());
        Mecanico updatedMecanico = mecanicoService.save(mecanico);
        return ResponseEntity.ok(convertToDTO(updatedMecanico));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMecanico(@PathVariable Integer id) {
        if (!mecanicoService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        mecanicoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private MecanicoDTO convertToDTO(Mecanico mecanico) {
        MecanicoDTO dto = new MecanicoDTO();
        dto.setIdMecanico(mecanico.getIdMecanico());
        dto.setNombre(mecanico.getNombre());
        dto.setTelefono(mecanico.getTelefono());
        dto.setEmail(mecanico.getEmail());
        dto.setActivo(mecanico.getActivo());
        dto.setSupervisorId(mecanico.getSupervisorId());
        dto.setFechaCreacion(mecanico.getFechaCreacion());
        dto.setFechaModificacion(mecanico.getFechaModificacion());
        return dto;
    }

    private Mecanico convertToEntity(MecanicoDTO dto) {
        Mecanico mecanico = new Mecanico();
        mecanico.setIdMecanico(dto.getIdMecanico());
        mecanico.setNombre(dto.getNombre());
        mecanico.setTelefono(dto.getTelefono());
        mecanico.setEmail(dto.getEmail());
        mecanico.setActivo(dto.getActivo());
        mecanico.setSupervisorId(dto.getSupervisorId());
        mecanico.setFechaCreacion(dto.getFechaCreacion());
        mecanico.setFechaModificacion(dto.getFechaModificacion());
        return mecanico;
    }
}