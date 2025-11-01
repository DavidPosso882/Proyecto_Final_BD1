package com.motorplus.service;

import com.motorplus.domain.entity.Impuesto;
import com.motorplus.domain.repository.ImpuestoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ImpuestoService {

    @Autowired
    private ImpuestoRepository impuestoRepository;

    public List<Impuesto> findAll() {
        return impuestoRepository.findAll();
    }

    public Optional<Impuesto> findById(Long id) {
        return impuestoRepository.findById(id);
    }

    public Impuesto save(Impuesto impuesto) {
        return impuestoRepository.save(impuesto);
    }

    public void deleteById(Long id) {
        impuestoRepository.deleteById(id);
    }
}