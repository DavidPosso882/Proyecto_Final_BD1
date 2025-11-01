package com.motorplus.service;

import com.motorplus.domain.entity.RepuestoProveedor;
import com.motorplus.domain.repository.RepuestoProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RepuestoProveedorService {

    @Autowired
    private RepuestoProveedorRepository repuestoProveedorRepository;

    public List<RepuestoProveedor> findAll() {
        return repuestoProveedorRepository.findAll();
    }

    public RepuestoProveedor save(RepuestoProveedor repuestoProveedor) {
        return repuestoProveedorRepository.save(repuestoProveedor);
    }

    public void delete(RepuestoProveedor repuestoProveedor) {
        repuestoProveedorRepository.delete(repuestoProveedor);
    }
}