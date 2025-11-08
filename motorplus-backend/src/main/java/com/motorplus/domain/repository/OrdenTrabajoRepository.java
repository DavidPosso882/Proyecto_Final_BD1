package com.motorplus.domain.repository;

import com.motorplus.domain.entity.OrdenTrabajo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdenTrabajoRepository extends JpaRepository<OrdenTrabajo, Integer> {

    @Query("SELECT ot FROM OrdenTrabajo ot " +
           "LEFT JOIN FETCH ot.vehiculo v " +
           "LEFT JOIN FETCH v.cliente c")
    List<OrdenTrabajo> findAllWithRelations();
}