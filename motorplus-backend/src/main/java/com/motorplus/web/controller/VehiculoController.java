package com.motorplus.web.controller;

import com.motorplus.domain.entity.Vehiculo;
import com.motorplus.service.VehiculoService;
import com.motorplus.web.dto.VehiculoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vehiculos")
@CrossOrigin(origins = "*")
public class VehiculoController {

    @Autowired
    private VehiculoService vehiculoService;

    @GetMapping
    public ResponseEntity<List<VehiculoDTO>> getAllVehiculos() {
        List<VehiculoDTO> vehiculos = vehiculoService.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(vehiculos);
    }

    @GetMapping("/{placa}")
    public ResponseEntity<VehiculoDTO> getVehiculoById(@PathVariable String placa) {
        Optional<Vehiculo> vehiculo = vehiculoService.findById(placa);
        if (vehiculo.isPresent()) {
            return ResponseEntity.ok(convertToDTO(vehiculo.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<VehiculoDTO> createVehiculo(@RequestBody VehiculoDTO vehiculoDTO) {
        Vehiculo vehiculo = convertToEntity(vehiculoDTO);
        vehiculo.setFechaCreacion(LocalDateTime.now());
        vehiculo.setFechaModificacion(LocalDateTime.now());
        Vehiculo savedVehiculo = vehiculoService.save(vehiculo);
        return ResponseEntity.ok(convertToDTO(savedVehiculo));
    }

    @PutMapping("/{placa}")
    public ResponseEntity<VehiculoDTO> updateVehiculo(@PathVariable String placa, @RequestBody VehiculoDTO vehiculoDTO) {
        if (!vehiculoService.findById(placa).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Vehiculo vehiculo = convertToEntity(vehiculoDTO);
        vehiculo.setPlaca(placa);
        vehiculo.setFechaModificacion(LocalDateTime.now());
        Vehiculo updatedVehiculo = vehiculoService.save(vehiculo);
        return ResponseEntity.ok(convertToDTO(updatedVehiculo));
    }

    @DeleteMapping("/{placa}")
    public ResponseEntity<Void> deleteVehiculo(@PathVariable String placa) {
        if (!vehiculoService.findById(placa).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        vehiculoService.deleteById(placa);
        return ResponseEntity.noContent().build();
    }

    private VehiculoDTO convertToDTO(Vehiculo vehiculo) {
        VehiculoDTO dto = new VehiculoDTO();
        dto.setPlaca(vehiculo.getPlaca());
        dto.setTipo(vehiculo.getTipo());
        dto.setMarca(vehiculo.getMarca());
        dto.setModelo(vehiculo.getModelo());
        dto.setAnio(vehiculo.getAnio());
        dto.setDocumentoCliente(vehiculo.getDocumentoCliente());
        dto.setFechaCreacion(vehiculo.getFechaCreacion());
        dto.setFechaModificacion(vehiculo.getFechaModificacion());
        return dto;
    }

    private Vehiculo convertToEntity(VehiculoDTO dto) {
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setPlaca(dto.getPlaca());
        vehiculo.setTipo(dto.getTipo());
        vehiculo.setMarca(dto.getMarca());
        vehiculo.setModelo(dto.getModelo());
        vehiculo.setAnio(dto.getAnio());
        vehiculo.setDocumentoCliente(dto.getDocumentoCliente());
        vehiculo.setFechaCreacion(dto.getFechaCreacion());
        vehiculo.setFechaModificacion(dto.getFechaModificacion());
        return vehiculo;
    }
}