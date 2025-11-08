package com.motorplus.service;

import com.motorplus.domain.entity.Cliente;
import com.motorplus.domain.repository.ClienteRepository;
import com.motorplus.web.dto.ClienteDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClienteService {

     @Autowired
     private ClienteRepository clienteRepository;

     public List<Cliente> findAll() {
         return clienteRepository.findAll();
     }

     public List<ClienteDTO> findAllDTO() {
         return clienteRepository.findAll().stream()
                 .map(this::convertToDTO)
                 .collect(Collectors.toList());
     }

     public Optional<Cliente> findById(String id) {
         return clienteRepository.findById(id);
     }

     public Optional<ClienteDTO> findByIdDTO(String id) {
         return clienteRepository.findById(id)
                 .map(this::convertToDTO);
     }

     public Cliente save(Cliente cliente) {
         return clienteRepository.save(cliente);
     }

     public ClienteDTO saveDTO(ClienteDTO clienteDTO) {
         Cliente cliente = convertToEntity(clienteDTO);
         Cliente savedCliente = clienteRepository.save(cliente);
         return convertToDTO(savedCliente);
     }

     public void deleteById(String id) {
         clienteRepository.deleteById(id);
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