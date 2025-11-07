package com.motorplus.web.controller;

import com.motorplus.domain.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
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
    private JdbcTemplate jdbcTemplate;

    // Reporte 1: Lista de Clientes (Simple)
    // CAMBIO: id_cliente ahora es VARCHAR(20) en lugar de INT
    // CAMBIO: Agregado campo apellido
    @GetMapping("/1/clientes")
    public List<Map<String, Object>> getClientesReport() {
        String sql = """
            SELECT
                id_cliente,
                nombre,
                apellido,
                telefono,
                email,
                tipo,
                fecha_creacion
            FROM Cliente
            ORDER BY nombre
            """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            // Create a HashMap to avoid Map.of null restrictions
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            try {
                result.put("id", rs.getString("id_cliente"));
                result.put("nombre", rs.getString("nombre"));
                result.put("apellido", rs.getString("apellido"));
                result.put("telefono", rs.getString("telefono"));
                result.put("email", rs.getString("email"));
                result.put("tipo", rs.getString("tipo"));
            } catch (Exception e) {
                System.err.println("Error processing row " + rowNum + ": " + e.getMessage());
                // Return empty map for problematic rows
                result.put("id", "");
                result.put("nombre", "");
                result.put("apellido", "");
                result.put("telefono", "");
                result.put("email", "");
                result.put("tipo", "");
            }
            return result;
        });
    }

    // Reporte 2: Lista de Vehículos (Simple)
    // CAMBIO: id_cliente cambió a documento_cliente (VARCHAR)
    // CAMBIO: Agregado JOIN con Cliente para mostrar nombre del propietario
    @GetMapping("/2/vehiculos")
    public List<Map<String, Object>> getVehiculosReport() {
        String sql = """
            SELECT
                v.placa,
                v.tipo,
                v.marca,
                v.modelo,
                v.anio,
                v.documento_cliente,
                CONCAT(c.nombre, ' ', COALESCE(c.apellido, '')) AS cliente_nombre
            FROM Vehiculo v
            LEFT JOIN Cliente c ON v.documento_cliente = c.id_cliente
            ORDER BY v.marca, v.modelo
            """;
        return jdbcTemplate.query(sql, (rs, rowNum) ->
            Map.of("placa", rs.getString("placa"),
                   "marca", rs.getString("marca"),
                   "modelo", rs.getString("modelo"),
                   "anio", rs.getInt("anio"),
                   "cliente", rs.getString("cliente_nombre"))
        );
    }

    // Reporte 3: Órdenes de Trabajo por Vehículo (Simple)
    @GetMapping("/3/ordenes-por-vehiculo")
    public List<Map<String, Object>> getOrdenesPorVehiculoReport() {
        String sql = """
            SELECT
                codigo,
                fecha_ingreso,
                diagnostico_inicial,
                estado,
                placa
            FROM OrdenTrabajo
            WHERE estado = 'PENDIENTE'
            ORDER BY fecha_ingreso
            """;
        return jdbcTemplate.query(sql, (rs, rowNum) ->
            Map.of("codigo", rs.getInt("codigo"),
                   "placa", rs.getString("placa"),
                   "estado", rs.getString("estado"))
        );
    }

    // Reporte 4: Facturas Pendientes (Intermedio)
    @GetMapping("/4/facturas-pendientes")
    public List<Map<String, Object>> getFacturasPendientesReport() {
        String sql = """
            SELECT
                id_factura,
                fecha_emision,
                subtotal,
                total,
                estado_pago,
                orden_codigo
            FROM Factura
            WHERE estado_pago = 'PENDIENTE'
            ORDER BY fecha_emision DESC
            """;
        return jdbcTemplate.query(sql, (rs, rowNum) ->
            Map.of("id", rs.getInt("id_factura"),
                   "fecha", rs.getDate("fecha_emision"),
                   "total", rs.getBigDecimal("total"))
        );
    }

    // Reporte 5: Mecánicos por Especialidad (Intermedio)
    @GetMapping("/5/mecanicos-por-especialidad")
    public List<Map<String, Object>> getMecanicosPorEspecialidadReport() {
        String sql = """
            SELECT
                m.id_mecanico,
                m.nombre AS nombre_mecanico,
                m.telefono,
                e.nombre AS especialidad,
                me.nivel,
                me.fecha_certificacion
            FROM Mecanico m
            INNER JOIN MecanicoEspecialidad me ON m.id_mecanico = me.mecanico_id
            INNER JOIN Especialidad e ON me.especialidad_codigo = e.codigo
            WHERE m.activo = TRUE
            ORDER BY m.nombre, e.nombre
            """;
        return jdbcTemplate.query(sql, (rs, rowNum) ->
            Map.of("id", rs.getInt("id_mecanico"),
                   "nombre", rs.getString("nombre_mecanico"),
                   "telefono", rs.getString("telefono"))
        );
    }

    // Reporte 6: Historial de Servicios por Vehículo (Intermedio)
    // CAMBIO: documento_cliente en lugar de id_cliente para JOIN con Cliente
    @GetMapping("/6/historial-servicios/{placa}")
    public List<Map<String, Object>> getHistorialServiciosReport(@PathVariable String placa) {
        String sql = """
            SELECT
                ot.codigo AS codigo_orden,
                ot.fecha_ingreso,
                ot.estado,
                ot.diagnostico_inicial,
                GROUP_CONCAT(DISTINCT s.nombre SEPARATOR ', ') AS servicios,
                GROUP_CONCAT(DISTINCT r.nombre SEPARATOR ', ') AS repuestos,
                GROUP_CONCAT(DISTINCT m.nombre SEPARATOR ', ') AS mecanicos,
                COALESCE(f.total, 0) AS costo_total,
                f.estado_pago,
                CONCAT(c.nombre, ' ', COALESCE(c.apellido, '')) AS cliente
            FROM OrdenTrabajo ot
            LEFT JOIN Vehiculo v ON ot.placa = v.placa
            LEFT JOIN Cliente c ON v.documento_cliente = c.id_cliente
            LEFT JOIN OrdenServicio os ON ot.codigo = os.orden_codigo
            LEFT JOIN Servicio s ON os.servicio_codigo = s.codigo
            LEFT JOIN OrdenRepuesto orep ON ot.codigo = orep.orden_codigo
            LEFT JOIN Repuesto r ON orep.repuesto_codigo = r.codigo
            LEFT JOIN OrdenMecanico om ON ot.codigo = om.orden_codigo
            LEFT JOIN Mecanico m ON om.mecanico_id = m.id_mecanico
            LEFT JOIN Factura f ON ot.codigo = f.orden_codigo
            WHERE ot.placa = ?
            GROUP BY ot.codigo, ot.fecha_ingreso, ot.estado, ot.diagnostico_inicial, f.total, f.estado_pago, c.nombre, c.apellido
            ORDER BY ot.fecha_ingreso DESC
            """;
        return jdbcTemplate.query(sql, (rs, rowNum) ->
            Map.of("codigo", rs.getInt("codigo_orden"),
                   "estado", rs.getString("estado"),
                   "fecha", rs.getDate("fecha_ingreso"),
                   "cliente", rs.getString("cliente"),
                   "servicios", rs.getString("servicios"),
                   "costo_total", rs.getBigDecimal("costo_total"))
        , placa);
    }

    // Reporte 7: Ingresos Totales por Mes (Complejo - con subconsultas)
    @GetMapping("/7/ingresos-mensuales")
    public List<Map<String, Object>> getIngresosMensualesReport() {
        String sql = """
            SELECT
                YEAR(fecha_emision) AS anio,
                MONTH(fecha_emision) AS mes,
                MONTHNAME(fecha_emision) AS nombre_mes,
                COUNT(*) AS total_facturas,
                SUM(subtotal) AS total_subtotal,
                SUM(total) AS total_ingresos
            FROM Factura
            WHERE YEAR(fecha_emision) = YEAR(CURDATE())
            GROUP BY YEAR(fecha_emision), MONTH(fecha_emision), MONTHNAME(fecha_emision)
            ORDER BY anio, mes
            """;
        return jdbcTemplate.query(sql, (rs, rowNum) ->
            Map.of("mes", rs.getInt("mes"),
                   "total", rs.getBigDecimal("total_ingresos"))
        );
    }

    // Reporte 8: Rendimiento de Mecánicos (Complejo)
    // CAMBIO: Agregado JOIN con Vehiculo y Cliente para mostrar información completa
    // CAMBIO: documento_cliente en lugar de id_cliente
    @GetMapping("/8/rendimiento-mecanicos")
    public List<Map<String, Object>> getRendimientoMecanicosReport() {
        String sql = """
            SELECT
                m.id_mecanico,
                m.nombre AS mecanico,
                m.telefono,
                COUNT(DISTINCT om.orden_codigo) AS ordenes_trabajadas,
                SUM(om.horas_trabajadas) AS total_horas,
                ROUND(AVG(om.horas_trabajadas), 2) AS promedio_horas_por_orden,
                COUNT(DISTINCT me.especialidad_codigo) AS total_especialidades,
                COUNT(DISTINCT CASE WHEN ot.estado = 'COMPLETADA' THEN om.orden_codigo END) AS ordenes_completadas
            FROM Mecanico m
            LEFT JOIN OrdenMecanico om ON m.id_mecanico = om.mecanico_id
            LEFT JOIN OrdenTrabajo ot ON om.orden_codigo = ot.codigo
            LEFT JOIN MecanicoEspecialidad me ON m.id_mecanico = me.mecanico_id
            WHERE m.activo = TRUE
            GROUP BY m.id_mecanico, m.nombre, m.telefono
            ORDER BY ordenes_trabajadas DESC, total_horas DESC
            """;
        return jdbcTemplate.query(sql, (rs, rowNum) ->
            Map.of("mecanico", rs.getString("mecanico"),
                   "ordenes_completadas", rs.getInt("ordenes_completadas"),
                   "total_horas", rs.getBigDecimal("total_horas"),
                   "especialidades", rs.getInt("total_especialidades"))
        );
    }

    // Reporte 9: Repuestos Más Utilizados (Complejo - con gráficos)
    // CAMBIO: Agregado JOIN con Proveedor para mostrar información completa
    @GetMapping("/9/repuestos-mas-utilizados")
    public List<Map<String, Object>> getRepuestosMasUtilizadosReport() {
        String sql = """
            SELECT
                r.codigo,
                r.nombre AS repuesto,
                r.descripcion,
                COUNT(orep.orden_codigo) AS veces_usado,
                SUM(orep.cantidad_usada) AS cantidad_total_usada,
                SUM(orep.cantidad_usada * orep.precio_aplicado) AS ingresos_generados,
                r.stock AS stock_actual,
                r.stock_minimo,
                GROUP_CONCAT(DISTINCT p.nombre SEPARATOR ', ') AS proveedores
            FROM Repuesto r
            INNER JOIN OrdenRepuesto orep ON r.codigo = orep.repuesto_codigo
            LEFT JOIN RepuestoProveedor rp ON r.codigo = rp.repuesto_codigo
            LEFT JOIN Proveedor p ON rp.proveedor_id = p.id
            GROUP BY r.codigo, r.nombre, r.descripcion, r.stock, r.stock_minimo
            ORDER BY veces_usado DESC, cantidad_total_usada DESC
            LIMIT 10
            """;
        return jdbcTemplate.query(sql, (rs, rowNum) ->
            Map.of("repuesto", rs.getString("repuesto"),
                   "usos", rs.getInt("veces_usado"),
                   "stock_actual", rs.getInt("stock_actual"),
                   "ingresos", rs.getBigDecimal("ingresos_generados"))
        );
    }

    // Reporte 10: Facturación Anual con Tendencias (Complejo - con gráficos)
    // CAMBIO: Agregado JOIN con OrdenTrabajo y Vehiculo para información completa
    // CAMBIO: documento_cliente en lugar de id_cliente
    @GetMapping("/10/facturacion-anual")
    public List<Map<String, Object>> getFacturacionAnualReport() {
        String sql = """
            SELECT
                YEAR(f.fecha_emision) AS anio,
                MONTH(f.fecha_emision) AS mes,
                MONTHNAME(f.fecha_emision) AS nombre_mes,
                COUNT(*) AS total_facturas,
                SUM(f.subtotal) AS subtotal,
                SUM(f.total) AS total,
                COUNT(CASE WHEN f.estado_pago = 'PAGADA' THEN 1 END) AS facturas_pagadas,
                COUNT(CASE WHEN f.estado_pago = 'PENDIENTE' THEN 1 END) AS facturas_pendientes,
                COUNT(DISTINCT ot.placa) AS vehiculos_atendidos,
                COUNT(DISTINCT v.documento_cliente) AS clientes_unicos
            FROM Factura f
            LEFT JOIN OrdenTrabajo ot ON f.orden_codigo = ot.codigo
            LEFT JOIN Vehiculo v ON ot.placa = v.placa
            GROUP BY YEAR(f.fecha_emision), MONTH(f.fecha_emision), MONTHNAME(f.fecha_emision)
            ORDER BY anio DESC, mes DESC
            """;
        return jdbcTemplate.query(sql, (rs, rowNum) ->
            Map.of("mes", rs.getString("nombre_mes"),
                   "facturacion", rs.getBigDecimal("total"),
                   "facturas_pagadas", rs.getInt("facturas_pagadas"),
                   "vehiculos_atendidos", rs.getInt("vehiculos_atendidos"))
        );
    }

    // Exportar Reporte a PDF
    @GetMapping("/export/{reportId}")
    public ResponseEntity<byte[]> exportReportToPDF(@PathVariable int reportId) {
        try {
            // Obtener datos del reporte correspondiente
            List<Map<String, Object>> reportData = getReportData(reportId);
            String reportTitle = getReportTitle(reportId);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Título del reporte
            document.add(new Paragraph("MotorPlus - Sistema de Gestión"));
            document.add(new Paragraph("Reporte: " + reportTitle));
            document.add(new Paragraph("Fecha de generación: " + java.time.LocalDate.now()));
            document.add(new Paragraph("\n"));

            if (reportData.isEmpty()) {
                document.add(new Paragraph("No hay datos disponibles para este reporte."));
            } else {
                // Crear tabla con los datos del reporte
                int columnCount = reportData.get(0).size();
                Table table = new Table(columnCount);

                // Agregar encabezados
                for (String key : reportData.get(0).keySet()) {
                    String headerText = key.substring(0, 1).toUpperCase() + key.substring(1).replace("_", " ");
                    table.addHeaderCell(headerText);
                }

                // Agregar filas de datos
                for (Map<String, Object> row : reportData) {
                    for (String key : row.keySet()) {
                        Object value = row.get(key);
                        String cellValue = value != null ? value.toString() : "";
                        table.addCell(cellValue);
                    }
                }

                document.add(table);

                // Agregar resumen
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("Total de registros: " + reportData.size()));
            }

            document.close();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "reporte_" + reportId + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(baos.toByteArray());

        } catch (Exception e) {
            e.printStackTrace(); // Para debugging

            // En caso de error, devolver un PDF básico
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PdfWriter writer = new PdfWriter(baos);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);

                document.add(new Paragraph("MotorPlus - Error en Reporte"));
                document.add(new Paragraph("ID del reporte: " + reportId));
                document.add(new Paragraph("Título: " + getReportTitle(reportId)));
                document.add(new Paragraph("Fecha: " + java.time.LocalDate.now()));
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("Error al generar el reporte:"));
                document.add(new Paragraph(e.getMessage() != null ? e.getMessage() : "Error desconocido"));
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("Por favor, contacte al administrador del sistema."));

                document.close();

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDispositionFormData("attachment", "error_reporte_" + reportId + ".pdf");

                return ResponseEntity.ok()
                        .headers(headers)
                        .body(baos.toByteArray());
            } catch (Exception pdfError) {
                pdfError.printStackTrace();
                return ResponseEntity.internalServerError().build();
            }
        }
    }

    private List<Map<String, Object>> getReportData(int reportId) {
        return switch (reportId) {
            case 1 -> getClientesReport();
            case 2 -> getVehiculosReport();
            case 3 -> getOrdenesPorVehiculoReport();
            case 4 -> getFacturasPendientesReport();
            case 5 -> getMecanicosPorEspecialidadReport();
            case 7 -> getIngresosMensualesReport();
            case 8 -> getRendimientoMecanicosReport();
            case 9 -> getRepuestosMasUtilizadosReport();
            case 10 -> getFacturacionAnualReport();
            default -> List.of();
        };
    }

    private String getReportTitle(int reportId) {
        return switch (reportId) {
            case 1 -> "Lista de Clientes";
            case 2 -> "Lista de Vehículos";
            case 3 -> "Órdenes de Trabajo";
            case 4 -> "Facturas Pendientes";
            case 5 -> "Mecánicos por Especialidad";
            case 7 -> "Ingresos Mensuales";
            case 8 -> "Rendimiento de Mecánicos";
            case 9 -> "Repuestos Más Utilizados";
            case 10 -> "Facturación Anual";
            default -> "Reporte " + reportId;
        };
    }
}