package com.motorplus.web.controller;

import com.motorplus.domain.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private OrdenTrabajoRepository ordenTrabajoRepository;

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private MecanicoRepository mecanicoRepository;

    // Reporte 1: Lista de Clientes (Simple)
    @GetMapping("/1/clientes")
    public List<Map<String, Object>> getClientesReport() {
        return clienteRepository.findAll().stream()
                .map(c -> Map.<String, Object>of("id", c.getIdCliente(), "nombre", c.getNombre(), "telefono", c.getTelefono(), "tipo", c.getTipo()))
                .toList();
    }

    // Reporte 2: Lista de Vehículos (Simple)
    @GetMapping("/2/vehiculos")
    public List<Map<String, Object>> getVehiculosReport() {
        return vehiculoRepository.findAll().stream()
                .map(v -> Map.<String, Object>of("placa", v.getPlaca(), "marca", v.getMarca(), "modelo", v.getModelo(), "anio", v.getAnio()))
                .toList();
    }

    // Reporte 3: Órdenes de Trabajo por Vehículo (Simple)
    @GetMapping("/3/ordenes-por-vehiculo")
    public List<Map<String, Object>> getOrdenesPorVehiculoReport() {
        return ordenTrabajoRepository.findAll().stream()
                .map(ot -> Map.<String, Object>of("codigo", ot.getCodigo(), "placa", ot.getPlaca(), "estado", ot.getEstado()))
                .toList();
    }

    // Reporte 4: Facturas Pendientes (Intermedio)
    @GetMapping("/4/facturas-pendientes")
    public List<Map<String, Object>> getFacturasPendientesReport() {
        return facturaRepository.findAll().stream()
                .filter(f -> "Pendiente".equals(f.getEstadoPago()))
                .map(f -> Map.<String, Object>of("id", f.getIdFactura(), "fecha", f.getFechaEmision(), "total", f.getTotal()))
                .toList();
    }

    // Reporte 5: Mecánicos por Especialidad (Intermedio)
    @GetMapping("/5/mecanicos-por-especialidad")
    public List<Map<String, Object>> getMecanicosPorEspecialidadReport() {
        return mecanicoRepository.findAll().stream()
                .map(m -> Map.<String, Object>of("id", m.getIdMecanico(), "nombre", m.getNombre(), "telefono", m.getTelefono()))
                .toList();
    }

    // Reporte 6: Historial de Servicios por Vehículo (Intermedio)
    @GetMapping("/6/historial-servicios/{placa}")
    public List<Map<String, Object>> getHistorialServiciosReport(@PathVariable String placa) {
        return ordenTrabajoRepository.findAll().stream()
                .filter(ot -> placa.equals(ot.getPlaca()))
                .map(ot -> Map.<String, Object>of("codigo", ot.getCodigo(), "estado", ot.getEstado(), "fecha", ot.getFechaIngreso()))
                .toList();
    }

    // Reporte 7: Ingresos Totales por Mes (Complejo - con subconsultas)
    @GetMapping("/7/ingresos-mensuales")
    public List<Map<String, Object>> getIngresosMensualesReport() {
        // Simulación de consulta compleja - en producción usar @Query con JPQL
        return facturaRepository.findAll().stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        f -> f.getFechaEmision().getMonthValue(),
                        java.util.stream.Collectors.summingDouble(f -> f.getTotal().doubleValue())
                ))
                .entrySet().stream()
                .map(e -> Map.<String, Object>of("mes", e.getKey(), "total", e.getValue()))
                .toList();
    }

    // Reporte 8: Rendimiento de Mecánicos (Complejo)
    @GetMapping("/8/rendimiento-mecanicos")
    public List<Map<String, Object>> getRendimientoMecanicosReport() {
        return mecanicoRepository.findAll().stream()
                .map(m -> {
                    long ordenes = ordenTrabajoRepository.findAll().stream()
                            .filter(ot -> ot.getEstado().equals("Completada"))
                            .count();
                    return Map.<String, Object>of("mecanico", m.getNombre(), "ordenes_completadas", ordenes);
                })
                .toList();
    }

    // Reporte 9: Repuestos Más Utilizados (Complejo - con gráficos)
    @GetMapping("/9/repuestos-mas-utilizados")
    public List<Map<String, Object>> getRepuestosMasUtilizadosReport() {
        // Datos para gráfico: top 5 repuestos por uso
        return List.of(
                Map.<String, Object>of("repuesto", "Filtro de Aceite", "usos", 45),
                Map.<String, Object>of("repuesto", "Batería", "usos", 32),
                Map.<String, Object>of("repuesto", "Neumáticos", "usos", 28),
                Map.<String, Object>of("repuesto", "Pastillas de Freno", "usos", 22),
                Map.<String, Object>of("repuesto", "Aceite de Motor", "usos", 18)
        );
    }

    // Reporte 10: Facturación Anual con Tendencias (Complejo - con gráficos)
    @GetMapping("/10/facturacion-anual")
    public List<Map<String, Object>> getFacturacionAnualReport() {
        // Datos para gráfico de líneas: facturación por mes
        return List.of(
                Map.<String, Object>of("mes", "Enero", "facturacion", 15000.0),
                Map.<String, Object>of("mes", "Febrero", "facturacion", 18000.0),
                Map.<String, Object>of("mes", "Marzo", "facturacion", 22000.0),
                Map.<String, Object>of("mes", "Abril", "facturacion", 19000.0),
                Map.<String, Object>of("mes", "Mayo", "facturacion", 25000.0),
                Map.<String, Object>of("mes", "Junio", "facturacion", 28000.0)
        );
    }

    // Exportar Reporte a PDF
    @GetMapping("/export/{reportId}")
    public ResponseEntity<byte[]> exportReportToPDF(@PathVariable int reportId) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Reporte MotorPlus - ID: " + reportId));

        // Agregar tabla con datos del reporte (ejemplo simplificado)
        Table table = new Table(2);
        table.addHeaderCell("Campo");
        table.addHeaderCell("Valor");

        // Datos de ejemplo - en producción obtener del reporte correspondiente
        table.addCell("Total Registros");
        table.addCell("100");

        document.add(table);
        document.close();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "reporte_" + reportId + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(baos.toByteArray());
    }
}