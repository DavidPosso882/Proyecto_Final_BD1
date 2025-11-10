package com.motorplus.service;

import com.motorplus.domain.entity.OrdenMecanico;
import com.motorplus.domain.repository.OrdenMecanicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrdenMecanicoService {

    @Autowired
    private OrdenMecanicoRepository ordenMecanicoRepository;

    public List<OrdenMecanico> findAll() {
        return ordenMecanicoRepository.findAll();
    }

    public OrdenMecanico save(OrdenMecanico ordenMecanico) {
        return ordenMecanicoRepository.save(ordenMecanico);
    }

    public void delete(OrdenMecanico ordenMecanico) {
        ordenMecanicoRepository.delete(ordenMecanico);
    }

    @Transactional
    public void deleteByOrdenCodigo(Integer ordenCodigo) {
        ordenMecanicoRepository.deleteByOrdenCodigo(ordenCodigo);
    }
}