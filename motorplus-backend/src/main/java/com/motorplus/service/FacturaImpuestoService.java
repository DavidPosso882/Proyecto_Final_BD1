package com.motorplus.service;

import com.motorplus.domain.entity.FacturaImpuesto;
import com.motorplus.domain.repository.FacturaImpuestoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacturaImpuestoService {

    @Autowired
    private FacturaImpuestoRepository facturaImpuestoRepository;

    public List<FacturaImpuesto> findAll() {
        return facturaImpuestoRepository.findAll();
    }

    public FacturaImpuesto save(FacturaImpuesto facturaImpuesto) {
        return facturaImpuestoRepository.save(facturaImpuesto);
    }

    public void delete(FacturaImpuesto facturaImpuesto) {
        facturaImpuestoRepository.delete(facturaImpuesto);
    }
}