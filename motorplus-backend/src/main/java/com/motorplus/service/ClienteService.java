package com.motorplus.service;

import com.motorplus.domain.entity.Cliente;
import com.motorplus.domain.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> findById(String id) {
        return clienteRepository.findById(id);
    }

    public Cliente save(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public void deleteById(String id) {
        clienteRepository.deleteById(id);
    }
}