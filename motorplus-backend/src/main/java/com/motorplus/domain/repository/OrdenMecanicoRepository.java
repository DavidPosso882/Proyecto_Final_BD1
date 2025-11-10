package com.motorplus.domain.repository;

import com.motorplus.domain.entity.OrdenMecanico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdenMecanicoRepository extends JpaRepository<OrdenMecanico, Object> {
    @Modifying
    void deleteByOrdenCodigo(Integer ordenCodigo);
}