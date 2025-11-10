package com.motorplus.service;

import com.motorplus.domain.entity.RepuestoProveedor;
import com.motorplus.domain.repository.RepuestoProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RepuestoProveedorService {

    @Autowired
    private RepuestoProveedorRepository repuestoProveedorRepository;

    public List<RepuestoProveedor> findAll() {
        return repuestoProveedorRepository.findAll();
    }

    public List<RepuestoProveedor> findByRepuestoCodigo(Integer repuestoCodigo) {
        return repuestoProveedorRepository.findByRepuestoCodigo(repuestoCodigo);
    }

    public Optional<RepuestoProveedor> findFirstByRepuestoCodigo(Integer repuestoCodigo) {
        return repuestoProveedorRepository.findFirstByRepuestoCodigo(repuestoCodigo);
    }

    public RepuestoProveedor save(RepuestoProveedor repuestoProveedor) {
        return repuestoProveedorRepository.save(repuestoProveedor);
    }

    public void delete(RepuestoProveedor repuestoProveedor) {
        repuestoProveedorRepository.delete(repuestoProveedor);
    }

    @Transactional
    public void deleteByRepuestoCodigo(Integer repuestoCodigo) {
        repuestoProveedorRepository.deleteByRepuestoCodigo(repuestoCodigo);
    }
}