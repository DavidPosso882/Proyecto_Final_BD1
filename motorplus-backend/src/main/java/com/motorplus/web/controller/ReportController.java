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

    // Reporte 11: Clientes con Gasto Superior al Promedio (SUBCONSULTA)
    @GetMapping("/11/clientes-vip")
    public List<Map<String, Object>> getClientesVIPReport() {
        String sql = """
            SELECT
                c.id_cliente,
                c.nombre,
                c.apellido,
                c.tipo,
                COUNT(DISTINCT v.placa) AS total_vehiculos,
                COUNT(DISTINCT ot.codigo) AS total_ordenes,
                COALESCE(SUM(f.total), 0) AS gasto_total
            FROM Cliente c
            LEFT JOIN Vehiculo v ON c.id_cliente = v.documento_cliente
            LEFT JOIN OrdenTrabajo ot ON v.placa = ot.placa
            LEFT JOIN Factura f ON ot.codigo = f.orden_codigo
            GROUP BY c.id_cliente, c.nombre, c.apellido, c.tipo
            HAVING COALESCE(SUM(f.total), 0) > (
                SELECT AVG(gasto_cliente) FROM (
                    SELECT COALESCE(SUM(f2.total), 0) AS gasto_cliente
                    FROM Cliente c2
                    LEFT JOIN Vehiculo v2 ON c2.id_cliente = v2.documento_cliente
                    LEFT JOIN OrdenTrabajo ot2 ON v2.placa = ot2.placa
                    LEFT JOIN Factura f2 ON ot2.codigo = f2.orden_codigo
                    GROUP BY c2.id_cliente
                ) AS gastos_promedio
            )
            ORDER BY gasto_total DESC
            """;
        return jdbcTemplate.query(sql, (rs, rowNum) ->
            Map.of("id", rs.getString("id_cliente"),
                   "nombre", rs.getString("nombre") + " " + rs.getString("apellido"),
                   "tipo", rs.getString("tipo"),
                   "gasto_total", rs.getBigDecimal("gasto_total"),
                   "total_vehiculos", rs.getInt("total_vehiculos"))
        );
    }

    // Reporte 12: Vehículos con Más Órdenes que el Promedio (SUBCONSULTA)
    @GetMapping("/12/vehiculos-mas-servicios")
    public List<Map<String, Object>> getVehiculosMasServiciosReport() {
        String sql = """
            SELECT
                v.placa,
                v.marca,
                v.modelo,
                v.anio,
                c.nombre AS cliente,
                COUNT(ot.codigo) AS total_ordenes,
                COALESCE(SUM(f.total), 0) AS gasto_total
            FROM Vehiculo v
            INNER JOIN Cliente c ON v.documento_cliente = c.id_cliente
            LEFT JOIN OrdenTrabajo ot ON v.placa = ot.placa
            LEFT JOIN Factura f ON ot.codigo = f.orden_codigo
            GROUP BY v.placa, v.marca, v.modelo, v.anio, c.nombre
            HAVING COUNT(ot.codigo) > (
                SELECT AVG(ordenes_por_vehiculo) FROM (
                    SELECT COUNT(ot2.codigo) AS ordenes_por_vehiculo
                    FROM Vehiculo v2
                    LEFT JOIN OrdenTrabajo ot2 ON v2.placa = ot2.placa
                    GROUP BY v2.placa
                ) AS ordenes_promedio
            )
            ORDER BY total_ordenes DESC
            """;
        return jdbcTemplate.query(sql, (rs, rowNum) ->
            Map.of("placa", rs.getString("placa"),
                   "vehiculo", rs.getString("marca") + " " + rs.getString("modelo"),
                   "cliente", rs.getString("cliente"),
                   "total_ordenes", rs.getInt("total_ordenes"),
                   "gasto_total", rs.getBigDecimal("gasto_total"))
        );
    }

    // Reporte 13: Facturas con Monto Superior al Promedio (SUBCONSULTA)
    @GetMapping("/13/facturas-superiores-promedio")
    public List<Map<String, Object>> getFacturasSuperioresPromedioReport() {
        String sql = """
            SELECT
                f.id_factura,
                f.fecha_emision,
                f.total,
                f.estado_pago,
                ot.placa,
                c.nombre AS cliente,
                (f.total - (SELECT AVG(total) FROM Factura)) AS diferencia_promedio
            FROM Factura f
            INNER JOIN OrdenTrabajo ot ON f.orden_codigo = ot.codigo
            INNER JOIN Vehiculo v ON ot.placa = v.placa
            INNER JOIN Cliente c ON v.documento_cliente = c.id_cliente
            WHERE f.total > (SELECT AVG(total) FROM Factura)
            ORDER BY f.total DESC
            """;
        return jdbcTemplate.query(sql, (rs, rowNum) ->
            Map.of("id", rs.getInt("id_factura"),
                   "fecha", rs.getDate("fecha_emision"),
                   "total", rs.getBigDecimal("total"),
                   "cliente", rs.getString("cliente"),
                   "placa", rs.getString("placa"))
        );
    }

    // Reporte 15: Detalle Completo de Orden de Trabajo
    @GetMapping("/15/orden-trabajo/{codigoOrden}")
    public Map<String, Object> getOrdenTrabajoDetalleReport(@PathVariable Integer codigoOrden) {
        try {
            // Información general de la orden
            String sqlOrden = """
                SELECT
                    ot.codigo,
                    ot.fecha_ingreso,
                    ot.diagnostico_inicial,
                    ot.estado,
                    ot.placa,
                    ot.fecha_creacion,
                    COALESCE(v.tipo, 'N/A') AS vehiculo_tipo,
                    COALESCE(v.marca, 'N/A') AS vehiculo_marca,
                    COALESCE(v.modelo, 'N/A') AS vehiculo_modelo,
                    COALESCE(v.anio, 0) AS vehiculo_anio,
                    COALESCE(CONCAT(c.nombre, ' ', COALESCE(c.apellido, '')), 'N/A') AS cliente_nombre,
                    c.telefono AS cliente_telefono,
                    c.email AS cliente_email,
                    c.tipo AS cliente_tipo,
                    c.id_cliente AS cliente_documento
                FROM OrdenTrabajo ot
                LEFT JOIN Vehiculo v ON ot.placa = v.placa
                LEFT JOIN Cliente c ON v.documento_cliente = c.id_cliente
                WHERE ot.codigo = ?
                """;
            
            List<Map<String, Object>> ordenList = jdbcTemplate.queryForList(sqlOrden, codigoOrden);
            
            if (ordenList.isEmpty()) {
                throw new RuntimeException("Orden de trabajo no encontrada con código: " + codigoOrden);
            }
            
            Map<String, Object> ordenInfo = ordenList.get(0);
            
            // Servicios de la orden
            String sqlServicios = """
                SELECT
                    s.codigo,
                    s.nombre,
                    s.descripcion,
                    s.categoria,
                    os.cantidad,
                    os.precio_aplicado,
                    (os.cantidad * os.precio_aplicado) AS subtotal
                FROM OrdenServicio os
                INNER JOIN Servicio s ON os.servicio_codigo = s.codigo
                WHERE os.orden_codigo = ?
                ORDER BY s.nombre
                """;
            
            List<Map<String, Object>> servicios = jdbcTemplate.queryForList(sqlServicios, codigoOrden);
            
            // Repuestos de la orden
            String sqlRepuestos = """
                SELECT
                    r.codigo,
                    r.nombre,
                    r.descripcion,
                    orep.cantidad_usada,
                    orep.precio_aplicado,
                    (orep.cantidad_usada * orep.precio_aplicado) AS subtotal
                FROM OrdenRepuesto orep
                INNER JOIN Repuesto r ON orep.repuesto_codigo = r.codigo
                WHERE orep.orden_codigo = ?
                ORDER BY r.nombre
                """;
            
            List<Map<String, Object>> repuestos = jdbcTemplate.queryForList(sqlRepuestos, codigoOrden);
            
            // Mecánicos de la orden
            String sqlMecanicos = """
                SELECT
                    m.id_mecanico,
                    m.nombre,
                    m.telefono,
                    om.horas_trabajadas,
                    om.rol,
                    0.00 AS tarifa_hora,
                    0.00 AS costo_mano_obra
                FROM OrdenMecanico om
                INNER JOIN Mecanico m ON om.mecanico_id = m.id_mecanico
                WHERE om.orden_codigo = ?
                ORDER BY m.nombre
                """;
            
            List<Map<String, Object>> mecanicos = jdbcTemplate.queryForList(sqlMecanicos, codigoOrden);
            
            // Factura asociada
            String sqlFactura = """
                SELECT
                    f.id_factura,
                    f.fecha_emision,
                    f.subtotal,
                    f.total,
                    f.estado_pago,
                    f.fecha_pago
                FROM Factura f
                WHERE f.orden_codigo = ?
                """;
            
            List<Map<String, Object>> facturaList = jdbcTemplate.queryForList(sqlFactura, codigoOrden);
            Map<String, Object> factura = facturaList.isEmpty() ? null : facturaList.get(0);
            
            // Construir respuesta completa
            Map<String, Object> resultado = new java.util.HashMap<>();
            resultado.put("orden", ordenInfo);
            resultado.put("servicios", servicios);
            resultado.put("repuestos", repuestos);
            resultado.put("mecanicos", mecanicos);
            resultado.put("factura", factura);
            
            return resultado;
        } catch (Exception e) {
            System.err.println("Error al obtener detalle de orden " + codigoOrden + ": " + e.getMessage());
            e.printStackTrace();
            
            // Retornar respuesta de error
            Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("mensaje", "Error al cargar el detalle de la orden: " + e.getMessage());
            errorResponse.put("codigoOrden", codigoOrden);
            return errorResponse;
        }
    }

    // Reporte 14: Historial Completo de Cliente (por documento)
    @GetMapping("/14/historial-cliente/{documentoCliente}")
    public List<Map<String, Object>> getHistorialClienteReport(@PathVariable String documentoCliente) {
        String sql = """
            SELECT
                c.nombre AS cliente,
                c.apellido,
                c.telefono,
                c.email,
                v.placa,
                v.marca,
                v.modelo,
                v.anio,
                ot.codigo AS codigo_orden,
                ot.fecha_ingreso,
                ot.diagnostico_inicial,
                ot.estado,
                GROUP_CONCAT(DISTINCT s.nombre SEPARATOR ', ') AS servicios,
                GROUP_CONCAT(DISTINCT r.nombre SEPARATOR ', ') AS repuestos,
                GROUP_CONCAT(DISTINCT m.nombre SEPARATOR ', ') AS mecanicos,
                COALESCE(f.total, 
                    (SELECT 
                        COALESCE(SUM(os2.cantidad * os2.precio_aplicado), 0) +
                        COALESCE(SUM(orep2.cantidad_usada * orep2.precio_aplicado), 0)
                     FROM OrdenTrabajo ot2
                     LEFT JOIN OrdenServicio os2 ON ot2.codigo = os2.orden_codigo
                     LEFT JOIN OrdenRepuesto orep2 ON ot2.codigo = orep2.orden_codigo
                     WHERE ot2.codigo = ot.codigo
                     GROUP BY ot2.codigo
                    ), 0
                ) AS costo_total,
                (SELECT 
                    COALESCE(SUM(os3.cantidad * os3.precio_aplicado), 0)
                 FROM OrdenServicio os3
                 WHERE os3.orden_codigo = ot.codigo
                ) AS total_servicios,
                (SELECT 
                    COALESCE(SUM(orep3.cantidad_usada * orep3.precio_aplicado), 0)
                 FROM OrdenRepuesto orep3
                 WHERE orep3.orden_codigo = ot.codigo
                ) AS total_repuestos,
                f.estado_pago,
                f.fecha_emision AS fecha_factura
            FROM Cliente c
            INNER JOIN Vehiculo v ON c.id_cliente = v.documento_cliente
            INNER JOIN OrdenTrabajo ot ON v.placa = ot.placa
            LEFT JOIN OrdenServicio os ON ot.codigo = os.orden_codigo
            LEFT JOIN Servicio s ON os.servicio_codigo = s.codigo
            LEFT JOIN OrdenRepuesto orep ON ot.codigo = orep.orden_codigo
            LEFT JOIN Repuesto r ON orep.repuesto_codigo = r.codigo
            LEFT JOIN OrdenMecanico om ON ot.codigo = om.orden_codigo
            LEFT JOIN Mecanico m ON om.mecanico_id = m.id_mecanico
            LEFT JOIN Factura f ON ot.codigo = f.orden_codigo
            WHERE c.id_cliente = ?
            GROUP BY c.nombre, c.apellido, c.telefono, c.email, v.placa, v.marca, v.modelo, v.anio,
                     ot.codigo, ot.fecha_ingreso, ot.diagnostico_inicial, ot.estado,
                     f.total, f.estado_pago, f.fecha_emision
            ORDER BY ot.fecha_ingreso DESC
            """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            // Create a HashMap to avoid Map.of null restrictions
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            try {
                result.put("cliente", rs.getString("cliente") + " " + rs.getString("apellido"));
                result.put("telefono", rs.getString("telefono"));
                result.put("email", rs.getString("email"));
                result.put("placa", rs.getString("placa"));
                result.put("vehiculo", rs.getString("marca") + " " + rs.getString("modelo") + " " + rs.getInt("anio"));
                result.put("codigo_orden", rs.getInt("codigo_orden"));
                result.put("fecha_ingreso", rs.getDate("fecha_ingreso"));
                result.put("diagnostico", rs.getString("diagnostico_inicial"));
                result.put("estado", rs.getString("estado"));
                result.put("servicios", rs.getString("servicios"));
                result.put("repuestos", rs.getString("repuestos"));
                result.put("mecanicos", rs.getString("mecanicos"));
                result.put("costo_total", rs.getBigDecimal("costo_total"));
                result.put("total_servicios", rs.getBigDecimal("total_servicios"));
                result.put("total_repuestos", rs.getBigDecimal("total_repuestos"));
                result.put("estado_pago", rs.getString("estado_pago"));
                result.put("fecha_factura", rs.getDate("fecha_factura"));
            } catch (Exception e) {
                System.err.println("Error processing row " + rowNum + ": " + e.getMessage());
                result.put("cliente", "");
                result.put("telefono", "");
                result.put("email", "");
                result.put("placa", "");
                result.put("vehiculo", "");
                result.put("codigo_orden", 0);
                result.put("fecha_ingreso", null);
                result.put("diagnostico", "");
                result.put("estado", "");
                result.put("servicios", "");
                result.put("repuestos", "");
                result.put("mecanicos", "");
                result.put("costo_total", 0);
                result.put("total_servicios", 0);
                result.put("total_repuestos", 0);
                result.put("estado_pago", "");
                result.put("fecha_factura", null);
            }
            return result;
        }, documentoCliente);
    }

    // Exportar Historial de Cliente a PDF
    @GetMapping("/export-historial/{documentoCliente}")
    public ResponseEntity<byte[]> exportHistorialClientePDF(@PathVariable String documentoCliente) {
        try {
            List<Map<String, Object>> historial = getHistorialClienteReport(documentoCliente);
            
            if (historial.isEmpty()) {
                throw new RuntimeException("No se encontró historial para el cliente: " + documentoCliente);
            }
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Encabezado
            document.add(new Paragraph("MOTORPLUS - Sistema de Gestión de Taller")
                .setFontSize(18)
                .setBold());
            document.add(new Paragraph("HISTORIAL COMPLETO DE CLIENTE")
                .setFontSize(14)
                .setBold());
            document.add(new Paragraph("Fecha de generación: " + java.time.LocalDate.now()));
            document.add(new Paragraph("\n"));

            // Información del cliente (tomada del primer registro)
            Map<String, Object> primerRegistro = historial.get(0);
            document.add(new Paragraph("INFORMACIÓN DEL CLIENTE").setBold().setFontSize(12));
            document.add(new Paragraph("Cliente: " + primerRegistro.get("cliente")));
            document.add(new Paragraph("Documento: " + documentoCliente));
            document.add(new Paragraph("Teléfono: " + (primerRegistro.get("telefono") != null ? primerRegistro.get("telefono") : "N/A")));
            document.add(new Paragraph("Email: " + (primerRegistro.get("email") != null ? primerRegistro.get("email") : "N/A")));
            document.add(new Paragraph("\n"));

            // Tabla de historial
            document.add(new Paragraph("HISTORIAL DE ÓRDENES").setBold().setFontSize(12));
            Table historialTable = new Table(8);
            historialTable.addHeaderCell("Orden");
            historialTable.addHeaderCell("Fecha");
            historialTable.addHeaderCell("Vehículo");
            historialTable.addHeaderCell("Estado");
            historialTable.addHeaderCell("Servicios");
            historialTable.addHeaderCell("Total Servicios");
            historialTable.addHeaderCell("Total Repuestos");
            historialTable.addHeaderCell("Total");
            
            java.math.BigDecimal totalGeneral = java.math.BigDecimal.ZERO;
            java.math.BigDecimal totalServiciosGeneral = java.math.BigDecimal.ZERO;
            java.math.BigDecimal totalRepuestosGeneral = java.math.BigDecimal.ZERO;
            
            for (Map<String, Object> registro : historial) {
                historialTable.addCell(registro.get("codigo_orden").toString());
                historialTable.addCell(registro.get("fecha_ingreso").toString());
                historialTable.addCell(registro.get("vehiculo") != null ? registro.get("vehiculo").toString() : "N/A");
                historialTable.addCell(registro.get("estado").toString());
                historialTable.addCell(registro.get("servicios") != null ? registro.get("servicios").toString() : "N/A");
                
                java.math.BigDecimal totalServicios = (java.math.BigDecimal) registro.get("total_servicios");
                java.math.BigDecimal totalRepuestos = (java.math.BigDecimal) registro.get("total_repuestos");
                java.math.BigDecimal costoTotal = (java.math.BigDecimal) registro.get("costo_total");
                
                historialTable.addCell("$" + totalServicios.toString());
                historialTable.addCell("$" + totalRepuestos.toString());
                historialTable.addCell("$" + costoTotal.toString());
                
                totalServiciosGeneral = totalServiciosGeneral.add(totalServicios);
                totalRepuestosGeneral = totalRepuestosGeneral.add(totalRepuestos);
                totalGeneral = totalGeneral.add(costoTotal);
            }
            document.add(historialTable);
            document.add(new Paragraph("\n"));
            
            // Resumen
            document.add(new Paragraph("RESUMEN").setBold().setFontSize(12));
            document.add(new Paragraph("Total de órdenes: " + historial.size()));
            document.add(new Paragraph("Total servicios: $" + totalServiciosGeneral.toString()));
            document.add(new Paragraph("Total repuestos: $" + totalRepuestosGeneral.toString()));
            document.add(new Paragraph("TOTAL GENERAL: $" + totalGeneral.toString()).setBold());

            document.close();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "historial_cliente_" + documentoCliente + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(baos.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // Exportar Orden de Trabajo a PDF
    @GetMapping("/export/orden-trabajo/{codigoOrden}")
    public ResponseEntity<byte[]> exportOrdenTrabajoPDF(@PathVariable Integer codigoOrden) {
        try {
            Map<String, Object> ordenDetalle = getOrdenTrabajoDetalleReport(codigoOrden);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Encabezado
            document.add(new Paragraph("MOTORPLUS - Sistema de Gestión de Taller")
                .setFontSize(18)
                .setBold());
            document.add(new Paragraph("ORDEN DE TRABAJO - Detalle Completo")
                .setFontSize(14)
                .setBold());
            document.add(new Paragraph("Fecha de generación: " + java.time.LocalDate.now()));
            document.add(new Paragraph("\n"));

            // Información de la orden
            @SuppressWarnings("unchecked")
            Map<String, Object> orden = (Map<String, Object>) ordenDetalle.get("orden");
            document.add(new Paragraph("INFORMACIÓN DE LA ORDEN").setBold().setFontSize(12));
            document.add(new Paragraph("Código de Orden: " + orden.get("codigo")));
            document.add(new Paragraph("Fecha de Ingreso: " + orden.get("fecha_ingreso")));
            document.add(new Paragraph("Estado: " + orden.get("estado")));
            document.add(new Paragraph("Diagnóstico Inicial: " + (orden.get("diagnostico_inicial") != null ? orden.get("diagnostico_inicial") : "N/A")));
            document.add(new Paragraph("\n"));

            // Información del cliente
            document.add(new Paragraph("INFORMACIÓN DEL CLIENTE").setBold().setFontSize(12));
            document.add(new Paragraph("Nombre: " + orden.get("cliente_nombre")));
            document.add(new Paragraph("Documento: " + orden.get("cliente_documento")));
            document.add(new Paragraph("Teléfono: " + (orden.get("cliente_telefono") != null ? orden.get("cliente_telefono") : "N/A")));
            document.add(new Paragraph("Email: " + (orden.get("cliente_email") != null ? orden.get("cliente_email") : "N/A")));
            document.add(new Paragraph("\n"));

            // Información del vehículo
            document.add(new Paragraph("INFORMACIÓN DEL VEHÍCULO").setBold().setFontSize(12));
            document.add(new Paragraph("Placa: " + orden.get("placa")));
            document.add(new Paragraph("Vehículo: " + orden.get("vehiculo_marca") + " " + orden.get("vehiculo_modelo") + " (" + orden.get("vehiculo_anio") + ")"));
            document.add(new Paragraph("Tipo: " + orden.get("vehiculo_tipo")));
            document.add(new Paragraph("\n"));

            // Servicios
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> servicios = (List<Map<String, Object>>) ordenDetalle.get("servicios");
            if (servicios != null && !servicios.isEmpty()) {
                document.add(new Paragraph("SERVICIOS REALIZADOS").setBold().setFontSize(12));
                Table serviciosTable = new Table(4);
                serviciosTable.addHeaderCell("Servicio");
                serviciosTable.addHeaderCell("Cantidad");
                serviciosTable.addHeaderCell("Precio Unit.");
                serviciosTable.addHeaderCell("Subtotal");
                
                for (Map<String, Object> servicio : servicios) {
                    serviciosTable.addCell(servicio.get("nombre").toString());
                    serviciosTable.addCell(servicio.get("cantidad").toString());
                    serviciosTable.addCell("$" + servicio.get("precio_aplicado").toString());
                    serviciosTable.addCell("$" + servicio.get("subtotal").toString());
                }
                document.add(serviciosTable);
                document.add(new Paragraph("\n"));
            }

            // Repuestos
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> repuestos = (List<Map<String, Object>>) ordenDetalle.get("repuestos");
            if (repuestos != null && !repuestos.isEmpty()) {
                document.add(new Paragraph("REPUESTOS UTILIZADOS").setBold().setFontSize(12));
                Table repuestosTable = new Table(4);
                repuestosTable.addHeaderCell("Repuesto");
                repuestosTable.addHeaderCell("Cantidad");
                repuestosTable.addHeaderCell("Precio Unit.");
                repuestosTable.addHeaderCell("Subtotal");
                
                for (Map<String, Object> repuesto : repuestos) {
                    repuestosTable.addCell(repuesto.get("nombre").toString());
                    repuestosTable.addCell(repuesto.get("cantidad_usada").toString());
                    repuestosTable.addCell("$" + repuesto.get("precio_aplicado").toString());
                    repuestosTable.addCell("$" + repuesto.get("subtotal").toString());
                }
                document.add(repuestosTable);
                document.add(new Paragraph("\n"));
            }

            // Mecánicos
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> mecanicos = (List<Map<String, Object>>) ordenDetalle.get("mecanicos");
            if (mecanicos != null && !mecanicos.isEmpty()) {
                document.add(new Paragraph("MECÁNICOS ASIGNADOS").setBold().setFontSize(12));
                Table mecanicosTable = new Table(3);
                mecanicosTable.addHeaderCell("Mecánico");
                mecanicosTable.addHeaderCell("Rol");
                mecanicosTable.addHeaderCell("Horas Trabajadas");
                
                for (Map<String, Object> mecanico : mecanicos) {
                    mecanicosTable.addCell(mecanico.get("nombre").toString());
                    mecanicosTable.addCell(mecanico.get("rol") != null ? mecanico.get("rol").toString() : "N/A");
                    mecanicosTable.addCell(mecanico.get("horas_trabajadas").toString());
                }
                document.add(mecanicosTable);
                document.add(new Paragraph("\n"));
            }

            // Factura
            @SuppressWarnings("unchecked")
            Map<String, Object> factura = (Map<String, Object>) ordenDetalle.get("factura");
            if (factura != null) {
                document.add(new Paragraph("INFORMACIÓN DE FACTURA").setBold().setFontSize(12));
                document.add(new Paragraph("ID Factura: " + factura.get("id_factura")));
                document.add(new Paragraph("Fecha Emisión: " + factura.get("fecha_emision")));
                document.add(new Paragraph("Subtotal: $" + factura.get("subtotal")));
                document.add(new Paragraph("Total: $" + factura.get("total")));
                document.add(new Paragraph("Estado de Pago: " + factura.get("estado_pago")));
            }

            document.close();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "orden_trabajo_" + codigoOrden + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(baos.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
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
            case 11 -> getClientesVIPReport();
            case 12 -> getVehiculosMasServiciosReport();
            case 13 -> getFacturasSuperioresPromedioReport();
            default -> List.of();
        };
    }

    private List<Map<String, Object>> getReportData(int reportId, String documentoCliente) {
        return switch (reportId) {
            case 14 -> getHistorialClienteReport(documentoCliente);
            default -> List.of();
        };
    }

    // Obtener detalle de factura para PDF
    @GetMapping("/factura/{idFactura}")
    public Map<String, Object> getFacturaDetalle(@PathVariable Integer idFactura) {
        try {
            // Obtener información de la factura
            String sqlFactura = """
                SELECT 
                    f.id_factura,
                    f.fecha_emision,
                    f.subtotal,
                    f.total,
                    f.estado_pago,
                    f.orden_codigo,
                    ot.diagnostico_inicial,
                    ot.estado as orden_estado,
                    c.id_cliente,
                    COALESCE(c.nombre, '') as cliente_nombre,
                    COALESCE(c.apellido, '') as cliente_apellido,
                    COALESCE(c.telefono, '') as cliente_telefono,
                    COALESCE(c.email, '') as cliente_email,
                    v.placa,
                    COALESCE(v.marca, '') as vehiculo_marca,
                    COALESCE(v.modelo, '') as vehiculo_modelo,
                    COALESCE(v.anio, 0) as vehiculo_anio
                FROM Factura f
                INNER JOIN OrdenTrabajo ot ON f.orden_codigo = ot.codigo
                INNER JOIN Vehiculo v ON ot.placa = v.placa
                INNER JOIN Cliente c ON v.documento_cliente = c.id_cliente
                WHERE f.id_factura = ?
                """;
            
            List<Map<String, Object>> facturaList = jdbcTemplate.queryForList(sqlFactura, idFactura);
            if (facturaList.isEmpty()) {
                throw new RuntimeException("Factura no encontrada con ID: " + idFactura);
            }
            
            Map<String, Object> factura = facturaList.get(0);
            
            // Obtener servicios de la orden
            String sqlServicios = """
                SELECT 
                    s.nombre,
                    os.cantidad,
                    os.precio_aplicado,
                    (os.cantidad * os.precio_aplicado) as subtotal
                FROM OrdenServicio os
                INNER JOIN Servicio s ON os.servicio_codigo = s.codigo
                WHERE os.orden_codigo = ?
                """;
            
            List<Map<String, Object>> servicios = jdbcTemplate.queryForList(
                sqlServicios, 
                factura.get("orden_codigo")
            );
            
            // Obtener repuestos de la orden
            String sqlRepuestos = """
                SELECT 
                    r.nombre,
                    orep.cantidad_usada as cantidad,
                    orep.precio_aplicado,
                    (orep.cantidad_usada * orep.precio_aplicado) as subtotal
                FROM OrdenRepuesto orep
                INNER JOIN Repuesto r ON orep.repuesto_codigo = r.codigo
                WHERE orep.orden_codigo = ?
                """;
            
            List<Map<String, Object>> repuestos = jdbcTemplate.queryForList(
                sqlRepuestos, 
                factura.get("orden_codigo")
            );
            
            Map<String, Object> resultado = new java.util.HashMap<>();
            resultado.put("factura", factura);
            resultado.put("servicios", servicios);
            resultado.put("repuestos", repuestos);
            
            return resultado;
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("mensaje", "Error al obtener detalle de factura: " + e.getMessage());
            errorResponse.put("idFactura", idFactura);
            return errorResponse;
        }
    }

    // Exportar Factura a PDF
    @GetMapping("/export/factura/{idFactura}")
    public ResponseEntity<byte[]> exportFacturaPDF(@PathVariable Integer idFactura) {
        try {
            Map<String, Object> facturaDetalle = getFacturaDetalle(idFactura);
            
            if (facturaDetalle.containsKey("error")) {
                return ResponseEntity.notFound().build();
            }
            
            @SuppressWarnings("unchecked")
            Map<String, Object> factura = (Map<String, Object>) facturaDetalle.get("factura");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> servicios = (List<Map<String, Object>>) facturaDetalle.get("servicios");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> repuestos = (List<Map<String, Object>>) facturaDetalle.get("repuestos");
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Encabezado de la factura
            document.add(new Paragraph("MOTORPLUS")
                .setFontSize(24)
                .setBold()
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            document.add(new Paragraph("Sistema de Gestión de Taller Automotriz")
                .setFontSize(10)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            document.add(new Paragraph("NIT: 900.123.456-7")
                .setFontSize(10)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            document.add(new Paragraph("\n"));
            
            // Título FACTURA
            document.add(new Paragraph("FACTURA DE VENTA")
                .setFontSize(18)
                .setBold()
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            document.add(new Paragraph("No. " + factura.get("id_factura"))
                .setFontSize(14)
                .setBold()
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            document.add(new Paragraph("\n"));
            
            // Información de factura y cliente en dos columnas
            Table infoTable = new Table(2);
            infoTable.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));
            
            // Columna izquierda - Datos del cliente
            String clienteInfo = "CLIENTE:\n" +
                factura.get("cliente_nombre") + " " + factura.get("cliente_apellido") + "\n" +
                "Documento: " + factura.get("id_cliente") + "\n" +
                "Teléfono: " + factura.get("cliente_telefono") + "\n" +
                "Email: " + factura.get("cliente_email");
            infoTable.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(clienteInfo).setFontSize(10))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));
            
            // Columna derecha - Datos de la factura
            String facturaInfo = "FECHA: " + factura.get("fecha_emision") + "\n" +
                "ORDEN DE TRABAJO: " + factura.get("orden_codigo") + "\n" +
                "ESTADO: " + factura.get("estado_pago") + "\n\n" +
                "VEHÍCULO:\n" +
                factura.get("vehiculo_marca") + " " + factura.get("vehiculo_modelo") + " " + factura.get("vehiculo_anio") + "\n" +
                "Placa: " + factura.get("placa");
            infoTable.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(facturaInfo).setFontSize(10))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            
            document.add(infoTable);
            document.add(new Paragraph("\n"));
            
            // Tabla de servicios y repuestos
            document.add(new Paragraph("DETALLE DE LA FACTURA").setBold().setFontSize(12));
            Table detalleTable = new Table(new float[]{3, 1, 2, 2});
            detalleTable.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));
            
            // Encabezados
            detalleTable.addHeaderCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph("Descripción").setBold())
                .setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY));
            detalleTable.addHeaderCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph("Cant.").setBold())
                .setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            detalleTable.addHeaderCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph("Precio Unit.").setBold())
                .setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            detalleTable.addHeaderCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph("Subtotal").setBold())
                .setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            
            // Agregar servicios
            if (servicios != null && !servicios.isEmpty()) {
                for (Map<String, Object> servicio : servicios) {
                    detalleTable.addCell(servicio.get("nombre").toString());
                    detalleTable.addCell(new com.itextpdf.layout.element.Cell()
                        .add(new Paragraph(servicio.get("cantidad").toString()))
                        .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
                    
                    // Convertir precio_aplicado a número
                    Object precioObj = servicio.get("precio_aplicado");
                    double precio = precioObj instanceof Number ? ((Number) precioObj).doubleValue() : 0.0;
                    detalleTable.addCell(new com.itextpdf.layout.element.Cell()
                        .add(new Paragraph("$" + String.format("%,.2f", precio)))
                        .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
                    
                    // Convertir subtotal a número
                    Object subtotalObj = servicio.get("subtotal");
                    double subtotal = subtotalObj instanceof Number ? ((Number) subtotalObj).doubleValue() : 0.0;
                    detalleTable.addCell(new com.itextpdf.layout.element.Cell()
                        .add(new Paragraph("$" + String.format("%,.2f", subtotal)))
                        .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
                }
            }
            
            // Agregar repuestos
            if (repuestos != null && !repuestos.isEmpty()) {
                for (Map<String, Object> repuesto : repuestos) {
                    detalleTable.addCell(repuesto.get("nombre").toString());
                    detalleTable.addCell(new com.itextpdf.layout.element.Cell()
                        .add(new Paragraph(repuesto.get("cantidad").toString()))
                        .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
                    
                    // Convertir precio_aplicado a número
                    Object precioObj = repuesto.get("precio_aplicado");
                    double precio = precioObj instanceof Number ? ((Number) precioObj).doubleValue() : 0.0;
                    detalleTable.addCell(new com.itextpdf.layout.element.Cell()
                        .add(new Paragraph("$" + String.format("%,.2f", precio)))
                        .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
                    
                    // Convertir subtotal a número
                    Object subtotalObj = repuesto.get("subtotal");
                    double subtotal = subtotalObj instanceof Number ? ((Number) subtotalObj).doubleValue() : 0.0;
                    detalleTable.addCell(new com.itextpdf.layout.element.Cell()
                        .add(new Paragraph("$" + String.format("%,.2f", subtotal)))
                        .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
                }
            }
            
            document.add(detalleTable);
            document.add(new Paragraph("\n"));
            
            // Totales con desglose de mano de obra
            Table totalesTable = new Table(new float[]{3, 2});
            totalesTable.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));
            
            // Convertir totales a BigDecimal de forma segura
            Object subtotalObj = factura.get("subtotal");
            java.math.BigDecimal subtotalConManoObra = subtotalObj instanceof Number 
                ? new java.math.BigDecimal(((Number) subtotalObj).doubleValue()) 
                : java.math.BigDecimal.ZERO;
            
            Object totalObj = factura.get("total");
            java.math.BigDecimal total = totalObj instanceof Number 
                ? new java.math.BigDecimal(((Number) totalObj).doubleValue()) 
                : java.math.BigDecimal.ZERO;
            
            // Calcular el subtotal base (sin mano de obra)
            // El subtotal guardado incluye la mano de obra (15%), así que: subtotalConManoObra = subtotalBase * 1.15
            // Por lo tanto: subtotalBase = subtotalConManoObra / 1.15
            java.math.BigDecimal subtotalBase = subtotalConManoObra.divide(
                new java.math.BigDecimal("1.15"), 
                2, 
                java.math.RoundingMode.HALF_UP
            );
            
            // Calcular mano de obra (15% del subtotal base)
            java.math.BigDecimal manoObra = subtotalBase.multiply(new java.math.BigDecimal("0.15"))
                .setScale(2, java.math.RoundingMode.HALF_UP);
            
            // Calcular IVA (19% del subtotal con mano de obra)
            java.math.BigDecimal iva = total.subtract(subtotalConManoObra);
            
            // Espaciador
            totalesTable.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(""))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));
            totalesTable.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(""))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));
            
            // Subtotal Base (Servicios + Repuestos)
            totalesTable.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph("SUBTOTAL BASE:").setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));
            totalesTable.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph("$" + String.format("%,.2f", subtotalBase)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));
            
            // Mano de Obra (15%)
            totalesTable.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph("MANO DE OBRA (15%):").setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));
            totalesTable.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph("$" + String.format("%,.2f", manoObra)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));
            
            // Subtotal (Base + Mano de Obra)
            totalesTable.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph("SUBTOTAL:").setBold().setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));
            totalesTable.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph("$" + String.format("%,.2f", subtotalConManoObra)).setBold().setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));
            
            // IVA (19%)
            totalesTable.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph("IVA (19%):").setBold().setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));
            totalesTable.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph("$" + String.format("%,.2f", iva)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));
            
            // Total a Pagar
            totalesTable.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph("TOTAL A PAGAR:").setBold().setFontSize(12).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));
            totalesTable.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph("$" + String.format("%,.2f", total)).setBold().setFontSize(12).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT))
                .setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY)
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));
            
            document.add(totalesTable);
            document.add(new Paragraph("\n\n"));
            
            // Pie de página
            document.add(new Paragraph("Gracias por su preferencia")
                .setFontSize(10)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                .setItalic());
            document.add(new Paragraph("Para cualquier consulta contacte con nosotros")
                .setFontSize(8)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));

            document.close();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "factura_" + idFactura + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(baos.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
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
            case 11 -> "Clientes VIP (Gasto > Promedio)";
            case 12 -> "Vehículos Más Atendidos";
            case 13 -> "Facturas Superiores al Promedio";
            case 14 -> "Historial Completo de Cliente";
            default -> "Reporte " + reportId;
        };
    }
}