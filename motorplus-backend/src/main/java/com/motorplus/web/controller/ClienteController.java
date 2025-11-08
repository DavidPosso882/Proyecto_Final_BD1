package com.motorplus.web.controller;

import com.motorplus.service.ClienteService;
import com.motorplus.web.dto.ClienteDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
@Validated
public class ClienteController {

     @Autowired
     private ClienteService clienteService;

     @GetMapping
     public ResponseEntity<List<ClienteDTO>> getAllClientes() {
         List<ClienteDTO> clientes = clienteService.findAllDTO();
         return ResponseEntity.ok(clientes);
     }

     @GetMapping("/{id}")
     public ResponseEntity<ClienteDTO> getClienteById(@PathVariable String id) {
         Optional<ClienteDTO> cliente = clienteService.findByIdDTO(id);
         return cliente.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
     }

     @PostMapping
     public ResponseEntity<?> createCliente(@Valid @RequestBody ClienteDTO clienteDTO, BindingResult result) {
         if (result.hasErrors()) {
             return ResponseEntity.badRequest().body(buildValidationErrorResponse(result));
         }

         try {
             ClienteDTO savedCliente = clienteService.saveDTO(clienteDTO);
             return ResponseEntity.status(HttpStatus.CREATED).body(savedCliente);
         } catch (Exception e) {
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Map.of("error", "Error al crear cliente: " + e.getMessage()));
         }
     }

     @PutMapping("/{id}")
     public ResponseEntity<?> updateCliente(@PathVariable String id, @Valid @RequestBody ClienteDTO clienteDTO, BindingResult result) {
         if (result.hasErrors()) {
             return ResponseEntity.badRequest().body(buildValidationErrorResponse(result));
         }

         if (!clienteService.findById(id).isPresent()) {
             return ResponseEntity.notFound().build();
         }

         try {
             clienteDTO.setIdCliente(id);
             ClienteDTO updatedCliente = clienteService.saveDTO(clienteDTO);
             return ResponseEntity.ok(updatedCliente);
         } catch (Exception e) {
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Map.of("error", "Error al actualizar cliente: " + e.getMessage()));
         }
     }

     @DeleteMapping("/{id}")
     public ResponseEntity<?> deleteCliente(@PathVariable String id) {
         if (!clienteService.findById(id).isPresent()) {
             return ResponseEntity.notFound().build();
         }

         try {
             clienteService.deleteById(id);
             return ResponseEntity.noContent().build();
         } catch (Exception e) {
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Map.of("error", "Error al eliminar cliente: " + e.getMessage()));
         }
     }

     private Map<String, String> buildValidationErrorResponse(BindingResult result) {
         Map<String, String> errors = new HashMap<>();
         result.getFieldErrors().forEach(error ->
             errors.put(error.getField(), error.getDefaultMessage())
         );
         return errors;
     }
}