package com.motorplus.domain.repository;

import com.motorplus.domain.entity.FacturaImpuesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacturaImpuestoRepository extends JpaRepository<FacturaImpuesto, Object> {
}