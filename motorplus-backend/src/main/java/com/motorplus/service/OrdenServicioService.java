package com.motorplus.service;

import com.motorplus.domain.entity.OrdenServicio;
import com.motorplus.domain.repository.OrdenServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrdenServicioService {

    @Autowired
    private OrdenServicioRepository ordenServicioRepository;

    public List<OrdenServicio> findAll() {
        return ordenServicioRepository.findAll();
    }

    public OrdenServicio save(OrdenServicio ordenServicio) {
        return ordenServicioRepository.save(ordenServicio);
    }

    public void delete(OrdenServicio ordenServicio) {
        ordenServicioRepository.delete(ordenServicio);
    }

    @Transactional
    public void deleteByOrdenCodigo(Integer ordenCodigo) {
        ordenServicioRepository.deleteByOrdenCodigo(ordenCodigo);
    }
}