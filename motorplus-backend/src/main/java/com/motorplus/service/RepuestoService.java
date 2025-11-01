package com.motorplus.service;

import com.motorplus.domain.entity.Repuesto;
import com.motorplus.domain.repository.RepuestoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RepuestoService {

    @Autowired
    private RepuestoRepository repuestoRepository;

    public List<Repuesto> findAll() {
        return repuestoRepository.findAll();
    }

    public Optional<Repuesto> findById(Integer codigo) {
        return repuestoRepository.findById(codigo);
    }

    public Repuesto save(Repuesto repuesto) {
        return repuestoRepository.save(repuesto);
    }

    public void deleteById(Integer codigo) {
        repuestoRepository.deleteById(codigo);
    }
}