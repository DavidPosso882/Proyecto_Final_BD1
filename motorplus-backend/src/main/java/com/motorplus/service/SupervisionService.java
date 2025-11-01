package com.motorplus.service;

import com.motorplus.domain.entity.Supervision;
import com.motorplus.domain.repository.SupervisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SupervisionService {

    @Autowired
    private SupervisionRepository supervisionRepository;

    public List<Supervision> findAll() {
        return supervisionRepository.findAll();
    }

    public Optional<Supervision> findById(Long id) {
        return supervisionRepository.findById(id);
    }

    public Supervision save(Supervision supervision) {
        return supervisionRepository.save(supervision);
    }

    public void deleteById(Long id) {
        supervisionRepository.deleteById(id);
    }
}