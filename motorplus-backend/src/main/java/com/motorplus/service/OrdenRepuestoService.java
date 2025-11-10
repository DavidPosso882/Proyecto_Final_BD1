package com.motorplus.service;

import com.motorplus.domain.entity.OrdenRepuesto;
import com.motorplus.domain.entity.OrdenRepuestoId;
import com.motorplus.domain.repository.OrdenRepuestoRepository;
import com.motorplus.web.dto.OrdenRepuestoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class OrdenRepuestoService {

    @Autowired
    private OrdenRepuestoRepository ordenRepuestoRepository;

    public List<OrdenRepuesto> findAll() {
        return ordenRepuestoRepository.findAll();
    }

    public Optional<OrdenRepuesto> findById(OrdenRepuestoId id) {
        return ordenRepuestoRepository.findById(id);
    }

    public OrdenRepuesto save(OrdenRepuesto ordenRepuesto) {
        return ordenRepuestoRepository.save(ordenRepuesto);
    }
    
    public void save(OrdenRepuestoDTO dto) {
        OrdenRepuesto ordenRepuesto = new OrdenRepuesto();
        
        ordenRepuesto.setOrdenCodigo(dto.getOrdenCodigo());
        ordenRepuesto.setRepuestoCodigo(dto.getRepuestoCodigo());
        ordenRepuesto.setCantidadUsada(dto.getCantidadUsada() != null ? dto.getCantidadUsada() : 1);
        ordenRepuesto.setPrecioAplicado(dto.getPrecioAplicado() != null ? dto.getPrecioAplicado() : BigDecimal.ZERO);
        
        ordenRepuestoRepository.save(ordenRepuesto);
    }

    public void deleteById(OrdenRepuestoId id) {
        ordenRepuestoRepository.deleteById(id);
    }

    @Transactional
    public void deleteByOrdenCodigo(Integer ordenCodigo) {
        ordenRepuestoRepository.deleteByOrdenCodigo(ordenCodigo);
    }
}