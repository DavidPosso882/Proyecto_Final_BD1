package com.motorplus.service;

import com.motorplus.domain.entity.Servicio;
import com.motorplus.domain.repository.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServicioService {

    @Autowired
    private ServicioRepository servicioRepository;

    public List<Servicio> findAll() {
        return servicioRepository.findAll();
    }

    public Optional<Servicio> findById(Integer codigo) {
        return servicioRepository.findById(codigo);
    }

    public Servicio save(Servicio servicio) {
        return servicioRepository.save(servicio);
    }

    public void deleteById(Integer codigo) {
        servicioRepository.deleteById(codigo);
    }
}