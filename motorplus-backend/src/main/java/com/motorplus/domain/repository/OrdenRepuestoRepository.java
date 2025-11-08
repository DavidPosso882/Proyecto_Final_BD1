package com.motorplus.domain.repository;

import com.motorplus.domain.entity.OrdenRepuesto;
import com.motorplus.domain.entity.OrdenRepuestoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdenRepuestoRepository extends JpaRepository<OrdenRepuesto, OrdenRepuestoId> {
}