package com.motorplus.web.controller;

import com.motorplus.domain.entity.OrdenTrabajo;
import com.motorplus.service.OrdenTrabajoService;
import com.motorplus.web.dto.OrdenTrabajoDTO;
import com.motorplus.web.dto.VehiculoDTO;
import com.motorplus.web.dto.ClienteDTO;
import com.motorplus.web.dto.OrdenServicioDTO;
import com.motorplus.web.dto.OrdenRepuestoDTO;
import com.motorplus.web.dto.OrdenMecanicoDTO;
import com.motorplus.web.dto.ServicioDTO;
import com.motorplus.web.dto.RepuestoDTO;
import com.motorplus.web.dto.MecanicoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ordenes-trabajo")
@CrossOrigin(origins = "*")
public class OrdenTrabajoController {

    @Autowired
    private OrdenTrabajoService ordenTrabajoService;

    @GetMapping
    public ResponseEntity<List<OrdenTrabajoDTO>> getAllOrdenesTrabajo() {
        List<OrdenTrabajo> ordenesEntities = ordenTrabajoService.findAllWithRelations();
        List<OrdenTrabajoDTO> ordenes = ordenesEntities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ordenes);
    }

    private OrdenTrabajoDTO convertToDTOWithoutCollections(OrdenTrabajo orden) {
        OrdenTrabajoDTO dto = new OrdenTrabajoDTO();
        dto.setCodigo(orden.getCodigo());
        dto.setFechaIngreso(orden.getFechaIngreso());
        dto.setDiagnosticoInicial(orden.getDiagnosticoInicial());
        dto.setEstado(orden.getEstado());
        dto.setPlaca(orden.getPlaca());
        dto.setFechaCreacion(orden.getFechaCreacion());
        dto.setFechaModificacion(orden.getFechaModificacion());
        dto.setUsuarioCreacion(orden.getUsuarioCreacion());
        return dto;
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenTrabajoDTO> getOrdenTrabajoById(@PathVariable String id) {
        try {
            Integer codigo = Integer.parseInt(id);
            Optional<OrdenTrabajo> orden = ordenTrabajoService.findById(codigo);
            if (orden.isPresent()) {
                return ResponseEntity.ok(convertToDTO(orden.get()));
            }
            return ResponseEntity.notFound().build();
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<OrdenTrabajoDTO> createOrdenTrabajo(@RequestBody OrdenTrabajoDTO ordenTrabajoDTO) {
        try {
            OrdenTrabajo orden = convertToEntity(ordenTrabajoDTO);
            orden.setFechaCreacion(LocalDateTime.now());
            orden.setFechaModificacion(LocalDateTime.now());
            orden.setUsuarioCreacion("admin");
            
            // Guardar la orden primero para obtener el código generado
            OrdenTrabajo savedOrden = ordenTrabajoService.save(orden);
            
            // Procesar relaciones si existen
            if (ordenTrabajoDTO.getServicios() != null && !ordenTrabajoDTO.getServicios().isEmpty()) {
                ordenTrabajoService.saveOrdenServicios(savedOrden.getCodigo(), ordenTrabajoDTO.getServicios());
            }
            
            if (ordenTrabajoDTO.getRepuestos() != null && !ordenTrabajoDTO.getRepuestos().isEmpty()) {
                ordenTrabajoService.saveOrdenRepuestos(savedOrden.getCodigo(), ordenTrabajoDTO.getRepuestos());
            }
            
            if (ordenTrabajoDTO.getMecanicos() != null && !ordenTrabajoDTO.getMecanicos().isEmpty()) {
                ordenTrabajoService.saveOrdenMecanicos(savedOrden.getCodigo(), ordenTrabajoDTO.getMecanicos());
            }
            
            // Recargar la orden con todas las relaciones
            OrdenTrabajo finalOrden = ordenTrabajoService.findById(savedOrden.getCodigo()).orElse(savedOrden);
            return ResponseEntity.ok(convertToDTO(finalOrden));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrdenTrabajoDTO> updateOrdenTrabajo(@PathVariable String id, @RequestBody OrdenTrabajoDTO ordenTrabajoDTO) {
        try {
            Integer codigo = Integer.parseInt(id);
            Optional<OrdenTrabajo> ordenExistente = ordenTrabajoService.findById(codigo);
            if (!ordenExistente.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            OrdenTrabajo orden = convertToEntity(ordenTrabajoDTO);
            orden.setCodigo(codigo);
            orden.setFechaModificacion(LocalDateTime.now());
            orden.setFechaCreacion(ordenExistente.get().getFechaCreacion());
            orden.setUsuarioCreacion(ordenExistente.get().getUsuarioCreacion());
            orden.setUsuarioModificacion("admin");
            
            // Actualizar la orden
            OrdenTrabajo updatedOrden = ordenTrabajoService.save(orden);
            
            // Eliminar relaciones antiguas
            ordenTrabajoService.deleteOrdenRelations(codigo);
            
            // Procesar nuevas relaciones si existen
            if (ordenTrabajoDTO.getServicios() != null && !ordenTrabajoDTO.getServicios().isEmpty()) {
                ordenTrabajoService.saveOrdenServicios(codigo, ordenTrabajoDTO.getServicios());
            }
            
            if (ordenTrabajoDTO.getRepuestos() != null && !ordenTrabajoDTO.getRepuestos().isEmpty()) {
                ordenTrabajoService.saveOrdenRepuestos(codigo, ordenTrabajoDTO.getRepuestos());
            }
            
            if (ordenTrabajoDTO.getMecanicos() != null && !ordenTrabajoDTO.getMecanicos().isEmpty()) {
                ordenTrabajoService.saveOrdenMecanicos(codigo, ordenTrabajoDTO.getMecanicos());
            }
            
            // Recargar la orden con todas las relaciones
            OrdenTrabajo finalOrden = ordenTrabajoService.findById(codigo).orElse(updatedOrden);
            return ResponseEntity.ok(convertToDTO(finalOrden));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrdenTrabajo(@PathVariable String id) {
        try {
            Integer codigo = Integer.parseInt(id);
            if (!ordenTrabajoService.findById(codigo).isPresent()) {
                return ResponseEntity.notFound().build();
            }
            ordenTrabajoService.deleteById(codigo);
            return ResponseEntity.noContent().build();
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }
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

        // ✅ FIXED: Convertir relaciones de servicios, repuestos y mecánicos
        if (orden.getOrdenServicios() != null && !orden.getOrdenServicios().isEmpty()) {
            dto.setServicios(orden.getOrdenServicios().stream()
                .map(os -> {
                    OrdenServicioDTO servicioDTO = new OrdenServicioDTO();
                    servicioDTO.setOrdenCodigo(os.getOrdenCodigo());
                    servicioDTO.setServicioCodigo(os.getServicioCodigo());
                    servicioDTO.setCantidad(os.getCantidad());
                    servicioDTO.setPrecioAplicado(os.getPrecioAplicado());
                    if (os.getServicio() != null) {
                        ServicioDTO servicioInfo = new ServicioDTO();
                        servicioInfo.setCodigo(os.getServicio().getCodigo());
                        servicioInfo.setNombre(os.getServicio().getNombre());
                        servicioInfo.setCategoria(os.getServicio().getCategoria());
                        servicioDTO.setServicio(servicioInfo);
                    }
                    return servicioDTO;
                })
                .collect(Collectors.toList()));
        }

        if (orden.getOrdenRepuestos() != null && !orden.getOrdenRepuestos().isEmpty()) {
            dto.setRepuestos(orden.getOrdenRepuestos().stream()
                .map(or -> {
                    OrdenRepuestoDTO repuestoDTO = new OrdenRepuestoDTO();
                    repuestoDTO.setOrdenCodigo(or.getOrdenCodigo());
                    repuestoDTO.setRepuestoCodigo(or.getRepuestoCodigo());
                    repuestoDTO.setCantidadUsada(or.getCantidadUsada());
                    repuestoDTO.setPrecioAplicado(or.getPrecioAplicado());
                    if (or.getRepuesto() != null) {
                        RepuestoDTO repuestoInfo = new RepuestoDTO();
                        repuestoInfo.setCodigo(or.getRepuesto().getCodigo());
                        repuestoInfo.setNombre(or.getRepuesto().getNombre());
                        repuestoInfo.setStock(or.getRepuesto().getStock());
                        repuestoDTO.setRepuesto(repuestoInfo);
                    }
                    return repuestoDTO;
                })
                .collect(Collectors.toList()));
        }

        if (orden.getOrdenMecanicos() != null && !orden.getOrdenMecanicos().isEmpty()) {
            dto.setMecanicos(orden.getOrdenMecanicos().stream()
                .map(om -> {
                    OrdenMecanicoDTO mecanicoDTO = new OrdenMecanicoDTO();
                    mecanicoDTO.setOrdenCodigo(om.getOrdenCodigo());
                    mecanicoDTO.setMecanicoId(om.getMecanicoId());
                    mecanicoDTO.setRol(om.getRol());
                    mecanicoDTO.setHorasTrabajadas(om.getHorasTrabajadas());
                    if (om.getMecanico() != null) {
                        MecanicoDTO mecanicoInfo = new MecanicoDTO();
                        mecanicoInfo.setIdMecanico(om.getMecanico().getIdMecanico());
                        mecanicoInfo.setNombre(om.getMecanico().getNombre());
                        mecanicoInfo.setTelefono(om.getMecanico().getTelefono());
                        mecanicoDTO.setMecanico(mecanicoInfo);
                    }
                    return mecanicoDTO;
                })
                .collect(Collectors.toList()));
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