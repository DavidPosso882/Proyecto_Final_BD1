package com.motorplus.domain.repository;

import com.motorplus.domain.entity.OrdenServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdenServicioRepository extends JpaRepository<OrdenServicio, Object> {
    @Modifying
    void deleteByOrdenCodigo(Integer ordenCodigo);
}