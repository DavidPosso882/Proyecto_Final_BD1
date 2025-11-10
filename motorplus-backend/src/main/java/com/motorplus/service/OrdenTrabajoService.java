package com.motorplus.service;

import com.motorplus.domain.entity.OrdenTrabajo;
import com.motorplus.domain.repository.OrdenTrabajoRepository;
import com.motorplus.web.dto.OrdenServicioDTO;
import com.motorplus.web.dto.OrdenRepuestoDTO;
import com.motorplus.web.dto.OrdenMecanicoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OrdenTrabajoService {

    @Autowired
    private OrdenTrabajoRepository ordenTrabajoRepository;
    
    @Autowired
    private OrdenServicioService ordenServicioService;
    
    @Autowired
    private OrdenRepuestoService ordenRepuestoService;
    
    @Autowired
    private OrdenMecanicoService ordenMecanicoService;

    public List<OrdenTrabajo> findAll() {
        return ordenTrabajoRepository.findAll();
    }

    public List<OrdenTrabajo> findAllWithRelations() {
        return ordenTrabajoRepository.findAll();
    }

    public Optional<OrdenTrabajo> findById(Integer codigo) {
        return ordenTrabajoRepository.findById(codigo);
    }

    public OrdenTrabajo save(OrdenTrabajo ordenTrabajo) {
        return ordenTrabajoRepository.save(ordenTrabajo);
    }

    public void deleteById(Integer codigo) {
        ordenTrabajoRepository.deleteById(codigo);
    }
    
    @Transactional
    public void saveOrdenServicios(Integer ordenCodigo, List<OrdenServicioDTO> servicios) {
        for (OrdenServicioDTO servicioDTO : servicios) {
            servicioDTO.setOrdenCodigo(ordenCodigo);
            ordenServicioService.save(servicioDTO);
        }
    }
    
    @Transactional
    public void saveOrdenRepuestos(Integer ordenCodigo, List<OrdenRepuestoDTO> repuestos) {
        for (OrdenRepuestoDTO repuestoDTO : repuestos) {
            repuestoDTO.setOrdenCodigo(ordenCodigo);
            ordenRepuestoService.save(repuestoDTO);
        }
    }
    
    @Transactional
    public void saveOrdenMecanicos(Integer ordenCodigo, List<OrdenMecanicoDTO> mecanicos) {
        for (OrdenMecanicoDTO mecanicoDTO : mecanicos) {
            mecanicoDTO.setOrdenCodigo(ordenCodigo);
            ordenMecanicoService.save(mecanicoDTO);
        }
    }
    
    @Transactional
    public void deleteOrdenRelations(Integer ordenCodigo) {
        ordenServicioService.deleteByOrdenCodigo(ordenCodigo);
        ordenRepuestoService.deleteByOrdenCodigo(ordenCodigo);
        ordenMecanicoService.deleteByOrdenCodigo(ordenCodigo);
    }
}