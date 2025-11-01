package com.motorplus.web.controller;

import com.motorplus.domain.entity.Servicio;
import com.motorplus.service.ServicioService;
import com.motorplus.web.dto.ServicioDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/servicios")
@CrossOrigin(origins = "*")
public class ServicioController {

    @Autowired
    private ServicioService servicioService;

    @GetMapping
    public ResponseEntity<List<ServicioDTO>> getAllServicios() {
        List<ServicioDTO> servicios = servicioService.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(servicios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicioDTO> getServicioById(@PathVariable Integer id) {
        Optional<Servicio> servicio = servicioService.findById(id);
        if (servicio.isPresent()) {
            return ResponseEntity.ok(convertToDTO(servicio.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<ServicioDTO> createServicio(@RequestBody ServicioDTO servicioDTO) {
        Servicio servicio = convertToEntity(servicioDTO);
        servicio.setFechaCreacion(LocalDateTime.now());
        servicio.setFechaModificacion(LocalDateTime.now());
        Servicio savedServicio = servicioService.save(servicio);
        return ResponseEntity.ok(convertToDTO(savedServicio));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicioDTO> updateServicio(@PathVariable Integer id, @RequestBody ServicioDTO servicioDTO) {
        if (!servicioService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Servicio servicio = convertToEntity(servicioDTO);
        servicio.setCodigo(id);
        servicio.setFechaModificacion(LocalDateTime.now());
        Servicio updatedServicio = servicioService.save(servicio);
        return ResponseEntity.ok(convertToDTO(updatedServicio));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServicio(@PathVariable Integer id) {
        if (!servicioService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        servicioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private ServicioDTO convertToDTO(Servicio servicio) {
        ServicioDTO dto = new ServicioDTO();
        dto.setCodigo(servicio.getCodigo());
        dto.setNombre(servicio.getNombre());
        dto.setDescripcion(servicio.getDescripcion());
        dto.setCategoria(servicio.getCategoria());
        dto.setPrecio(servicio.getPrecio());
        dto.setFechaCreacion(servicio.getFechaCreacion());
        dto.setFechaModificacion(servicio.getFechaModificacion());
        return dto;
    }

    private Servicio convertToEntity(ServicioDTO dto) {
        Servicio servicio = new Servicio();
        servicio.setCodigo(dto.getCodigo());
        servicio.setNombre(dto.getNombre());
        servicio.setDescripcion(dto.getDescripcion());
        servicio.setCategoria(dto.getCategoria());
        servicio.setPrecio(dto.getPrecio());
        servicio.setFechaCreacion(dto.getFechaCreacion());
        servicio.setFechaModificacion(dto.getFechaModificacion());
        return servicio;
    }
}