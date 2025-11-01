package com.motorplus.service;

import com.motorplus.domain.entity.Mecanico;
import com.motorplus.domain.repository.MecanicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MecanicoService {

    @Autowired
    private MecanicoRepository mecanicoRepository;

    public List<Mecanico> findAll() {
        return mecanicoRepository.findAll();
    }

    public Optional<Mecanico> findById(Integer id) {
        return mecanicoRepository.findById(id);
    }

    public Mecanico save(Mecanico mecanico) {
        return mecanicoRepository.save(mecanico);
    }

    public void deleteById(Integer id) {
        mecanicoRepository.deleteById(id);
    }
}