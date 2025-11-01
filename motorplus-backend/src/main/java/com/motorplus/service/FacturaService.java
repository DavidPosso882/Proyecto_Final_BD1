package com.motorplus.service;

import com.motorplus.domain.entity.Factura;
import com.motorplus.domain.repository.FacturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FacturaService {

    @Autowired
    private FacturaRepository facturaRepository;

    public List<Factura> findAll() {
        return facturaRepository.findAll();
    }

    public Optional<Factura> findById(Integer idFactura) {
        return facturaRepository.findById(idFactura);
    }

    public Factura save(Factura factura) {
        return facturaRepository.save(factura);
    }

    public void deleteById(Integer idFactura) {
        facturaRepository.deleteById(idFactura);
    }
}