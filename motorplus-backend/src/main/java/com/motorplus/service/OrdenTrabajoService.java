package com.motorplus.service;

import com.motorplus.domain.entity.OrdenTrabajo;
import com.motorplus.domain.repository.OrdenTrabajoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrdenTrabajoService {

    @Autowired
    private OrdenTrabajoRepository ordenTrabajoRepository;

    public List<OrdenTrabajo> findAll() {
        return ordenTrabajoRepository.findAll();
    }

    public List<OrdenTrabajo> findAllWithRelations() {
        return ordenTrabajoRepository.findAll();
    }

    public Optional<OrdenTrabajo> findById(Integer codigo) {
        return ordenTrabajoRepository.findById(codigo);
    }

    public OrdenTrabajo save(OrdenTrabajo ordenTrabajo) {
        return ordenTrabajoRepository.save(ordenTrabajo);
    }

    public void deleteById(Integer codigo) {
        ordenTrabajoRepository.deleteById(codigo);
    }
}