package com.motorplus.web.controller;

import com.motorplus.domain.entity.Cliente;
import com.motorplus.service.ClienteService;
import com.motorplus.web.dto.ClienteDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<ClienteDTO>> getAllClientes() {
        List<ClienteDTO> clientes = clienteService.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> getClienteById(@PathVariable String id) {
        Optional<Cliente> cliente = clienteService.findById(id);
        if (cliente.isPresent()) {
            return ResponseEntity.ok(convertToDTO(cliente.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<ClienteDTO> createCliente(@RequestBody ClienteDTO clienteDTO) {
        Cliente cliente = convertToEntity(clienteDTO);
        cliente.setFechaCreacion(LocalDateTime.now());
        cliente.setFechaModificacion(LocalDateTime.now());
        Cliente savedCliente = clienteService.save(cliente);
        return ResponseEntity.ok(convertToDTO(savedCliente));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> updateCliente(@PathVariable String id, @RequestBody ClienteDTO clienteDTO) {
        if (!clienteService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Cliente cliente = convertToEntity(clienteDTO);
        cliente.setIdCliente(id);
        cliente.setFechaModificacion(LocalDateTime.now());
        Cliente updatedCliente = clienteService.save(cliente);
        return ResponseEntity.ok(convertToDTO(updatedCliente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable String id) {
        if (!clienteService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        clienteService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private ClienteDTO convertToDTO(Cliente cliente) {
        ClienteDTO dto = new ClienteDTO();
        dto.setIdCliente(cliente.getIdCliente());
        dto.setNombre(cliente.getNombre());
        dto.setApellido(cliente.getApellido());
        dto.setTelefono(cliente.getTelefono());
        dto.setEmail(cliente.getEmail());
        dto.setTipo(cliente.getTipo());
        dto.setFechaCreacion(cliente.getFechaCreacion());
        dto.setFechaModificacion(cliente.getFechaModificacion());
        return dto;
    }

    private Cliente convertToEntity(ClienteDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(dto.getIdCliente());
        cliente.setNombre(dto.getNombre());
        cliente.setApellido(dto.getApellido());
        cliente.setTelefono(dto.getTelefono());
        cliente.setEmail(dto.getEmail());
        cliente.setTipo(dto.getTipo());
        cliente.setFechaCreacion(dto.getFechaCreacion());
        cliente.setFechaModificacion(dto.getFechaModificacion());
        return cliente;
    }
}