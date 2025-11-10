package com.motorplus.web.controller;

import com.motorplus.domain.entity.OrdenTrabajo;
import com.motorplus.domain.entity.OrdenServicio;
import com.motorplus.domain.entity.Servicio;
import com.motorplus.service.OrdenTrabajoService;
import com.motorplus.service.OrdenServicioService;
import com.motorplus.service.ServicioService;
import com.motorplus.web.dto.OrdenTrabajoDTO;
import com.motorplus.web.dto.OrdenServicioDTO;
import com.motorplus.web.dto.ServicioDTO;
import com.motorplus.web.dto.VehiculoDTO;
import com.motorplus.web.dto.ClienteDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/ordenes-trabajo")
@CrossOrigin(origins = "*")
public class OrdenTrabajoController {

    @Autowired
    private OrdenTrabajoService ordenTrabajoService;

    @Autowired
    private OrdenServicioService ordenServicioService;

    @Autowired
    private ServicioService servicioService;

    @GetMapping
    public ResponseEntity<List<OrdenTrabajoDTO>> getAllOrdenesTrabajo() {
        List<OrdenTrabajo> ordenesEntities = ordenTrabajoService.findAll();
        List<OrdenTrabajoDTO> ordenes = ordenesEntities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ordenes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenTrabajoDTO> getOrdenTrabajoById(@PathVariable Integer id) {
        Optional<OrdenTrabajo> orden = ordenTrabajoService.findById(id);
        if (orden.isPresent()) {
            return ResponseEntity.ok(convertToDTO(orden.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<OrdenTrabajoDTO> createOrdenTrabajo(@RequestBody OrdenTrabajoDTO ordenTrabajoDTO) {
        OrdenTrabajo orden = convertToEntity(ordenTrabajoDTO);
        orden.setFechaCreacion(LocalDateTime.now());
        orden.setFechaModificacion(LocalDateTime.now());
        
        // Guardar la orden primero
        OrdenTrabajo savedOrden = ordenTrabajoService.save(orden);
        
        // Procesar y guardar los servicios asociados
        if (ordenTrabajoDTO.getServicios() != null && !ordenTrabajoDTO.getServicios().isEmpty()) {
            for (OrdenServicioDTO servicioDTO : ordenTrabajoDTO.getServicios()) {
                OrdenServicio ordenServicio = new OrdenServicio();
                ordenServicio.setOrdenCodigo(savedOrden.getCodigo());
                ordenServicio.setServicioCodigo(servicioDTO.getServicioCodigo());
                ordenServicio.setCantidad(servicioDTO.getCantidad() != null ? servicioDTO.getCantidad() : 1);
                
                // Obtener el precio del servicio si no viene en el DTO
                if (servicioDTO.getPrecioAplicado() != null) {
                    ordenServicio.setPrecioAplicado(servicioDTO.getPrecioAplicado());
                } else {
                    Optional<Servicio> servicio = servicioService.findById(servicioDTO.getServicioCodigo());
                    if (servicio.isPresent()) {
                        ordenServicio.setPrecioAplicado(servicio.get().getPrecio());
                    }
                }
                
                ordenServicioService.save(ordenServicio);
            }
        }
        
        // Recargar la orden con las relaciones
        Optional<OrdenTrabajo> ordenConRelaciones = ordenTrabajoService.findById(savedOrden.getCodigo());
        return ResponseEntity.ok(convertToDTO(ordenConRelaciones.orElse(savedOrden)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrdenTrabajoDTO> updateOrdenTrabajo(@PathVariable Integer id, @RequestBody OrdenTrabajoDTO ordenTrabajoDTO) {
        if (!ordenTrabajoService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        OrdenTrabajo orden = convertToEntity(ordenTrabajoDTO);
        orden.setCodigo(id);
        orden.setFechaModificacion(LocalDateTime.now());
        
        // Actualizar la orden
        OrdenTrabajo updatedOrden = ordenTrabajoService.save(orden);
        
        // Procesar servicios actualizados si vienen en el DTO
        if (ordenTrabajoDTO.getServicios() != null) {
            // Nota: En una implementación completa, deberías eliminar los servicios antiguos
            // y agregar los nuevos, o hacer una comparación más sofisticada
            for (OrdenServicioDTO servicioDTO : ordenTrabajoDTO.getServicios()) {
                OrdenServicio ordenServicio = new OrdenServicio();
                ordenServicio.setOrdenCodigo(updatedOrden.getCodigo());
                ordenServicio.setServicioCodigo(servicioDTO.getServicioCodigo());
                ordenServicio.setCantidad(servicioDTO.getCantidad() != null ? servicioDTO.getCantidad() : 1);
                
                // Obtener el precio del servicio si no viene en el DTO
                if (servicioDTO.getPrecioAplicado() != null) {
                    ordenServicio.setPrecioAplicado(servicioDTO.getPrecioAplicado());
                } else {
                    Optional<Servicio> servicio = servicioService.findById(servicioDTO.getServicioCodigo());
                    if (servicio.isPresent()) {
                        ordenServicio.setPrecioAplicado(servicio.get().getPrecio());
                    }
                }
                
                ordenServicioService.save(ordenServicio);
            }
        }
        
        // Recargar la orden con las relaciones
        Optional<OrdenTrabajo> ordenConRelaciones = ordenTrabajoService.findById(updatedOrden.getCodigo());
        return ResponseEntity.ok(convertToDTO(ordenConRelaciones.orElse(updatedOrden)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrdenTrabajo(@PathVariable Integer id) {
        if (!ordenTrabajoService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        ordenTrabajoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private OrdenTrabajoDTO convertToDTO(OrdenTrabajo orden) {
        OrdenTrabajoDTO dto = new OrdenTrabajoDTO();
        dto.setCodigo(orden.getCodigo());
        dto.setFechaIngreso(orden.getFechaIngreso());
        dto.setDiagnosticoInicial(orden.getDiagnosticoInicial());
        dto.setEstado(orden.getEstado());
        dto.setPlaca(orden.getPlaca());
        dto.setFechaCreacion(orden.getFechaCreacion());
        dto.setFechaModificacion(orden.getFechaModificacion());
        dto.setUsuarioCreacion(orden.getUsuarioCreacion());

        // Cargar datos relacionados si están disponibles
        if (orden.getVehiculo() != null) {
            dto.setVehiculo(new VehiculoDTO(
                orden.getVehiculo().getPlaca(),
                orden.getVehiculo().getTipo(),
                orden.getVehiculo().getMarca(),
                orden.getVehiculo().getModelo(),
                orden.getVehiculo().getAnio(),
                orden.getVehiculo().getDocumentoCliente()
            ));
        }

        if (orden.getVehiculo() != null && orden.getVehiculo().getCliente() != null) {
            dto.setCliente(new ClienteDTO(
                orden.getVehiculo().getCliente().getIdCliente(),
                orden.getVehiculo().getCliente().getNombre(),
                orden.getVehiculo().getCliente().getApellido(),
                orden.getVehiculo().getCliente().getTelefono(),
                orden.getVehiculo().getCliente().getEmail(),
                orden.getVehiculo().getCliente().getTipo()
            ));
        }

        // Cargar servicios asociados
        if (orden.getOrdenServicios() != null && !orden.getOrdenServicios().isEmpty()) {
            List<OrdenServicioDTO> serviciosDTO = new ArrayList<>();
            for (OrdenServicio os : orden.getOrdenServicios()) {
                OrdenServicioDTO osDTO = new OrdenServicioDTO();
                osDTO.setOrdenCodigo(os.getOrdenCodigo());
                osDTO.setServicioCodigo(os.getServicioCodigo());
                osDTO.setCantidad(os.getCantidad());
                osDTO.setPrecioAplicado(os.getPrecioAplicado());
                
                // Cargar información del servicio
                if (os.getServicio() != null) {
                    ServicioDTO servicioDTO = new ServicioDTO(
                        os.getServicio().getCodigo(),
                        os.getServicio().getNombre(),
                        os.getServicio().getDescripcion(),
                        os.getServicio().getCategoria(),
                        os.getServicio().getPrecio()
                    );
                    osDTO.setServicio(servicioDTO);
                }
                
                serviciosDTO.add(osDTO);
            }
            dto.setServicios(serviciosDTO);
        }

        return dto;
    }

    private OrdenTrabajo convertToEntity(OrdenTrabajoDTO dto) {
        OrdenTrabajo orden = new OrdenTrabajo();
        orden.setCodigo(dto.getCodigo());
        orden.setFechaIngreso(dto.getFechaIngreso());
        orden.setDiagnosticoInicial(dto.getDiagnosticoInicial());
        orden.setEstado(dto.getEstado());
        orden.setPlaca(dto.getPlaca());
        orden.setFechaCreacion(dto.getFechaCreacion());
        orden.setFechaModificacion(dto.getFechaModificacion());
        orden.setUsuarioCreacion(dto.getUsuarioCreacion());
        return orden;
    }
}