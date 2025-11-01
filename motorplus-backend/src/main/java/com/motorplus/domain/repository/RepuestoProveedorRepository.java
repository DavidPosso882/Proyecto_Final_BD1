package com.motorplus.domain.repository;

import com.motorplus.domain.entity.RepuestoProveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepuestoProveedorRepository extends JpaRepository<RepuestoProveedor, Object> {
}