package com.motorplus.domain.repository;

import com.motorplus.domain.entity.MecanicoEspecialidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MecanicoEspecialidadRepository extends JpaRepository<MecanicoEspecialidad, Object> {
}