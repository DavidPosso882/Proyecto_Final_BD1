package com.motorplus.service;

import com.motorplus.domain.entity.OrdenRepuesto;
import com.motorplus.domain.repository.OrdenRepuestoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrdenRepuestoService {

    @Autowired
    private OrdenRepuestoRepository ordenRepuestoRepository;

    public List<OrdenRepuesto> findAll() {
        return ordenRepuestoRepository.findAll();
    }

    public Optional<OrdenRepuesto> findById(Long id) {
        return ordenRepuestoRepository.findById(id);
    }

    public OrdenRepuesto save(OrdenRepuesto ordenRepuesto) {
        return ordenRepuestoRepository.save(ordenRepuesto);
    }

    public void deleteById(Long id) {
        ordenRepuestoRepository.deleteById(id);
    }
}