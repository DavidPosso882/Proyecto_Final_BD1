package com.motorplus.domain.repository;

import com.motorplus.domain.entity.OrdenTrabajo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdenTrabajoRepository extends JpaRepository<OrdenTrabajo, Integer> {

    @Query("SELECT DISTINCT ot FROM OrdenTrabajo ot " +
           "LEFT JOIN FETCH ot.ordenServicios os " +
           "LEFT JOIN FETCH os.servicio " +
           "LEFT JOIN FETCH ot.ordenRepuestos orp " +
           "LEFT JOIN FETCH orp.repuesto " +
           "LEFT JOIN FETCH ot.ordenMecanicos om " +
           "LEFT JOIN FETCH om.mecanico " +
           "LEFT JOIN FETCH ot.vehiculo v " +
           "LEFT JOIN FETCH v.cliente")
    List<OrdenTrabajo> findAllWithRelations();
}