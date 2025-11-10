package com.motorplus.domain.repository;

import com.motorplus.domain.entity.RepuestoProveedor;
import com.motorplus.domain.entity.RepuestoProveedorId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepuestoProveedorRepository extends JpaRepository<RepuestoProveedor, RepuestoProveedorId> {
    List<RepuestoProveedor> findByRepuestoCodigo(Integer repuestoCodigo);
    Optional<RepuestoProveedor> findFirstByRepuestoCodigo(Integer repuestoCodigo);
    
    @Modifying
    void deleteByRepuestoCodigo(Integer repuestoCodigo);
}