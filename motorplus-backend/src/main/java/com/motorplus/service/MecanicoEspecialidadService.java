package com.motorplus.service;

import com.motorplus.domain.entity.MecanicoEspecialidad;
import com.motorplus.domain.repository.MecanicoEspecialidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MecanicoEspecialidadService {

    @Autowired
    private MecanicoEspecialidadRepository mecanicoEspecialidadRepository;

    public List<MecanicoEspecialidad> findAll() {
        return mecanicoEspecialidadRepository.findAll();
    }

    public MecanicoEspecialidad save(MecanicoEspecialidad mecanicoEspecialidad) {
        return mecanicoEspecialidadRepository.save(mecanicoEspecialidad);
    }

    public void delete(MecanicoEspecialidad mecanicoEspecialidad) {
        mecanicoEspecialidadRepository.delete(mecanicoEspecialidad);
    }
}