-- ==========================================
-- CONSULTAS SQL COMPLETAS - MOTORPLUS
-- Sistema de Gestión de Taller Mecánico
-- Proyecto Final BD1
-- ==========================================

-- ==========================================
-- SECCIÓN 1: CONSULTAS SIMPLES (SELECT BÁSICOS)
-- ==========================================

-- 1. Listar todos los clientes
SELECT 
    id_cliente,
    nombre,
    telefono,
    tipo,
    fecha_creacion
FROM Cliente
ORDER BY nombre;

-- 2. Listar todos los vehículos con sus datos básicos
SELECT 
    placa,
    tipo,
    marca,
    modelo,
    anio,
    id_cliente
FROM Vehiculo
ORDER BY marca, modelo;

-- 3. Listar todos los servicios disponibles
SELECT 
    codigo,
    nombre,
    descripcion,
    categoria,
    precio
FROM Servicio
ORDER BY categoria, nombre;

-- 4. Listar todos los mecánicos activos
SELECT 
    id_mecanico,
    nombre,
    telefono,
    supervisor_id,
    activo
FROM Mecanico
WHERE activo = TRUE
ORDER BY nombre;

-- 5. Listar todos los repuestos con stock bajo
SELECT 
    codigo,
    nombre,
    stock,
    stock_minimo,
    precio_unitario
FROM Repuesto
WHERE stock < stock_minimo
ORDER BY stock ASC;

-- 6. Listar todas las órdenes de trabajo pendientes
SELECT 
    codigo,
    fecha_ingreso,
    diagnostico_inicial,
    estado,
    placa
FROM OrdenTrabajo
WHERE estado = 'PENDIENTE'
ORDER BY fecha_ingreso;

-- 7. Listar todas las facturas pendientes de pago
SELECT 
    id_factura,
    fecha_emision,
    subtotal,
    total,
    estado_pago,
    orden_codigo
FROM Factura
WHERE estado_pago = 'PENDIENTE'
ORDER BY fecha_emision DESC;

-- 8. Listar todos los proveedores activos
SELECT 
    id,
    nombre,
    correo,
    telefono,
    activo
FROM Proveedor
WHERE activo = TRUE
ORDER BY nombre;

-- 9. Listar todas las especialidades disponibles
SELECT 
    codigo,
    nombre,
    descripcion
FROM Especialidad
ORDER BY nombre;

-- 10. Listar todos los impuestos activos
SELECT 
    codigo,
    nombre,
    porcentaje,
    activo
FROM Impuesto
WHERE activo = TRUE
ORDER BY porcentaje DESC;


-- ==========================================
-- SECCIÓN 2: CONSULTAS CON JOINS (INTERMEDIAS)
-- ==========================================

-- 11. Listar vehículos con información de sus dueños
SELECT 
    v.placa,
    v.tipo AS tipo_vehiculo,
    v.marca,
    v.modelo,
    v.anio,
    c.nombre AS nombre_cliente,
    c.telefono AS telefono_cliente,
    c.tipo AS tipo_cliente
FROM Vehiculo v
INNER JOIN Cliente c ON v.id_cliente = c.id_cliente
ORDER BY c.nombre, v.marca;

-- 12. Listar órdenes de trabajo con información del vehículo y cliente
SELECT 
    ot.codigo AS codigo_orden,
    ot.fecha_ingreso,
    ot.estado,
    ot.diagnostico_inicial,
    v.placa,
    v.marca,
    v.modelo,
    c.nombre AS cliente,
    c.telefono AS telefono_cliente
FROM OrdenTrabajo ot
INNER JOIN Vehiculo v ON ot.placa = v.placa
INNER JOIN Cliente c ON v.id_cliente = c.id_cliente
ORDER BY ot.fecha_ingreso DESC;

-- 13. Listar facturas con detalles de la orden de trabajo
SELECT 
    f.id_factura,
    f.fecha_emision,
    f.total,
    f.estado_pago,
    ot.codigo AS codigo_orden,
    ot.fecha_ingreso,
    v.placa,
    c.nombre AS cliente
FROM Factura f
INNER JOIN OrdenTrabajo ot ON f.orden_codigo = ot.codigo
INNER JOIN Vehiculo v ON ot.placa = v.placa
INNER JOIN Cliente c ON v.id_cliente = c.id_cliente
ORDER BY f.fecha_emision DESC;

-- 14. Listar mecánicos con sus especialidades
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
ORDER BY m.nombre, e.nombre;

-- 15. Listar mecánicos con sus supervisores
SELECT 
    m1.id_mecanico AS id_mecanico,
    m1.nombre AS nombre_mecanico,
    m1.telefono,
    m2.id_mecanico AS id_supervisor,
    m2.nombre AS nombre_supervisor
FROM Mecanico m1
LEFT JOIN Mecanico m2 ON m1.supervisor_id = m2.id_mecanico
WHERE m1.activo = TRUE
ORDER BY m2.nombre, m1.nombre;

-- 16. Listar servicios aplicados en órdenes de trabajo
SELECT 
    ot.codigo AS codigo_orden,
    ot.fecha_ingreso,
    s.nombre AS servicio,
    s.categoria,
    os.cantidad,
    os.precio_aplicado,
    (os.cantidad * os.precio_aplicado) AS subtotal
FROM OrdenServicio os
INNER JOIN OrdenTrabajo ot ON os.orden_codigo = ot.codigo
INNER JOIN Servicio s ON os.servicio_codigo = s.codigo
ORDER BY ot.codigo, s.nombre;

-- 17. Listar repuestos utilizados en órdenes de trabajo
SELECT 
    ot.codigo AS codigo_orden,
    ot.fecha_ingreso,
    r.nombre AS repuesto,
    orep.cantidad_usada,
    orep.precio_aplicado,
    (orep.cantidad_usada * orep.precio_aplicado) AS subtotal
FROM OrdenRepuesto orep
INNER JOIN OrdenTrabajo ot ON orep.orden_codigo = ot.codigo
INNER JOIN Repuesto r ON orep.repuesto_codigo = r.codigo
ORDER BY ot.codigo, r.nombre;

-- 18. Listar repuestos con sus proveedores y precios
SELECT 
    r.codigo AS codigo_repuesto,
    r.nombre AS repuesto,
    r.precio_unitario AS precio_venta,
    p.nombre AS proveedor,
    rp.precio_compra,
    rp.tiempo_entrega_dias,
    (r.precio_unitario - rp.precio_compra) AS margen_ganancia
FROM RepuestoProveedor rp
INNER JOIN Repuesto r ON rp.repuesto_codigo = r.codigo
INNER JOIN Proveedor p ON rp.proveedor_id = p.id
WHERE p.activo = TRUE
ORDER BY r.nombre, p.nombre;

-- 19. Listar mecánicos asignados a órdenes de trabajo
SELECT 
    ot.codigo AS codigo_orden,
    ot.fecha_ingreso,
    ot.estado,
    m.nombre AS mecanico,
    om.rol,
    om.horas_trabajadas
FROM OrdenMecanico om
INNER JOIN OrdenTrabajo ot ON om.orden_codigo = ot.codigo
INNER JOIN Mecanico m ON om.mecanico_id = m.id_mecanico
ORDER BY ot.codigo, m.nombre;

-- 20. Listar impuestos aplicados a facturas
SELECT 
    f.id_factura,
    f.fecha_emision,
    f.subtotal,
    i.nombre AS impuesto,
    i.porcentaje,
    fi.monto_aplicado
FROM FacturaImpuesto fi
INNER JOIN Factura f ON fi.factura_id = f.id_factura
INNER JOIN Impuesto i ON fi.impuesto_codigo = i.codigo
ORDER BY f.id_factura, i.nombre;


-- ==========================================
-- SECCIÓN 3: CONSULTAS CON AGREGACIONES
-- ==========================================

-- 21. Total de vehículos por cliente
SELECT 
    c.id_cliente,
    c.nombre AS cliente,
    c.tipo,
    COUNT(v.placa) AS total_vehiculos
FROM Cliente c
LEFT JOIN Vehiculo v ON c.id_cliente = v.id_cliente
GROUP BY c.id_cliente, c.nombre, c.tipo
ORDER BY total_vehiculos DESC, c.nombre;

-- 22. Total de órdenes de trabajo por estado
SELECT 
    estado,
    COUNT(*) AS total_ordenes,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM OrdenTrabajo), 2) AS porcentaje
FROM OrdenTrabajo
GROUP BY estado
ORDER BY total_ordenes DESC;

-- 23. Total de ingresos por mes (año actual)
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
ORDER BY anio, mes;

-- 24. Promedio de servicios por orden de trabajo
SELECT 
    AVG(servicios_por_orden) AS promedio_servicios
FROM (
    SELECT 
        orden_codigo,
        COUNT(*) AS servicios_por_orden
    FROM OrdenServicio
    GROUP BY orden_codigo
) AS subconsulta;

-- 25. Top 10 servicios más solicitados
SELECT 
    s.codigo,
    s.nombre AS servicio,
    s.categoria,
    COUNT(os.orden_codigo) AS veces_solicitado,
    SUM(os.cantidad) AS cantidad_total,
    SUM(os.cantidad * os.precio_aplicado) AS ingresos_generados
FROM Servicio s
INNER JOIN OrdenServicio os ON s.codigo = os.servicio_codigo
GROUP BY s.codigo, s.nombre, s.categoria
ORDER BY veces_solicitado DESC, ingresos_generados DESC
LIMIT 10;

-- 26. Top 10 repuestos más utilizados
SELECT 
    r.codigo,
    r.nombre AS repuesto,
    COUNT(orep.orden_codigo) AS veces_usado,
    SUM(orep.cantidad_usada) AS cantidad_total_usada,
    SUM(orep.cantidad_usada * orep.precio_aplicado) AS ingresos_generados,
    r.stock AS stock_actual
FROM Repuesto r
INNER JOIN OrdenRepuesto orep ON r.codigo = orep.repuesto_codigo
GROUP BY r.codigo, r.nombre, r.stock
ORDER BY veces_usado DESC, cantidad_total_usada DESC
LIMIT 10;

-- 27. Rendimiento de mecánicos (órdenes completadas)
SELECT 
    m.id_mecanico,
    m.nombre AS mecanico,
    COUNT(DISTINCT om.orden_codigo) AS ordenes_trabajadas,
    SUM(om.horas_trabajadas) AS total_horas,
    ROUND(AVG(om.horas_trabajadas), 2) AS promedio_horas_por_orden,
    COUNT(DISTINCT me.especialidad_codigo) AS total_especialidades
FROM Mecanico m
LEFT JOIN OrdenMecanico om ON m.id_mecanico = om.mecanico_id
LEFT JOIN MecanicoEspecialidad me ON m.id_mecanico = me.mecanico_id
WHERE m.activo = TRUE
GROUP BY m.id_mecanico, m.nombre
ORDER BY ordenes_trabajadas DESC, total_horas DESC;

-- 28. Ingresos por categoría de servicio
SELECT 
    s.categoria,
    COUNT(DISTINCT os.orden_codigo) AS total_ordenes,
    SUM(os.cantidad) AS total_servicios,
    SUM(os.cantidad * os.precio_aplicado) AS ingresos_totales,
    ROUND(AVG(os.precio_aplicado), 2) AS precio_promedio
FROM Servicio s
INNER JOIN OrdenServicio os ON s.codigo = os.servicio_codigo
GROUP BY s.categoria
ORDER BY ingresos_totales DESC;

-- 29. Análisis de facturas por estado de pago
SELECT 
    estado_pago,
    COUNT(*) AS total_facturas,
    SUM(subtotal) AS total_subtotal,
    SUM(total) AS total_con_impuestos,
    ROUND(AVG(total), 2) AS promedio_factura,
    MIN(total) AS factura_minima,
    MAX(total) AS factura_maxima
FROM Factura
GROUP BY estado_pago
ORDER BY total_con_impuestos DESC;

-- 30. Clientes con mayor gasto total
SELECT 
    c.id_cliente,
    c.nombre AS cliente,
    c.tipo,
    COUNT(DISTINCT v.placa) AS total_vehiculos,
    COUNT(DISTINCT ot.codigo) AS total_ordenes,
    COUNT(DISTINCT f.id_factura) AS total_facturas,
    COALESCE(SUM(f.total), 0) AS gasto_total,
    ROUND(COALESCE(AVG(f.total), 0), 2) AS gasto_promedio_por_factura
FROM Cliente c
LEFT JOIN Vehiculo v ON c.id_cliente = v.id_cliente
LEFT JOIN OrdenTrabajo ot ON v.placa = ot.placa
LEFT JOIN Factura f ON ot.codigo = f.orden_codigo
GROUP BY c.id_cliente, c.nombre, c.tipo
ORDER BY gasto_total DESC
LIMIT 20;


-- ==========================================
-- SECCIÓN 4: CONSULTAS CON SUBCONSULTAS
-- ==========================================

-- 31. Clientes que nunca han traído un vehículo al taller
SELECT 
    c.id_cliente,
    c.nombre,
    c.telefono,
    c.tipo
FROM Cliente c
WHERE c.id_cliente NOT IN (
    SELECT DISTINCT v.id_cliente
    FROM Vehiculo v
    INNER JOIN OrdenTrabajo ot ON v.placa = ot.placa
);

-- 32. Vehículos sin órdenes de trabajo
SELECT 
    v.placa,
    v.marca,
    v.modelo,
    v.anio,
    c.nombre AS cliente
FROM Vehiculo v
INNER JOIN Cliente c ON v.id_cliente = c.id_cliente
WHERE v.placa NOT IN (
    SELECT DISTINCT placa
    FROM OrdenTrabajo
);

-- 33. Mecánicos sin órdenes asignadas en el último mes
SELECT 
    m.id_mecanico,
    m.nombre,
    m.telefono
FROM Mecanico m
WHERE m.activo = TRUE
AND m.id_mecanico NOT IN (
    SELECT DISTINCT om.mecanico_id
    FROM OrdenMecanico om
    INNER JOIN OrdenTrabajo ot ON om.orden_codigo = ot.codigo
    WHERE ot.fecha_ingreso >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH)
);

-- 34. Servicios que nunca se han solicitado
SELECT 
    s.codigo,
    s.nombre,
    s.categoria,
    s.precio
FROM Servicio s
WHERE s.codigo NOT IN (
    SELECT DISTINCT servicio_codigo
    FROM OrdenServicio
);

-- 35. Repuestos que nunca se han utilizado
SELECT 
    r.codigo,
    r.nombre,
    r.stock,
    r.precio_unitario
FROM Repuesto r
WHERE r.codigo NOT IN (
    SELECT DISTINCT repuesto_codigo
    FROM OrdenRepuesto
);

-- 36. Facturas con monto superior al promedio
SELECT 
    f.id_factura,
    f.fecha_emision,
    f.total,
    f.estado_pago,
    ot.codigo AS codigo_orden
FROM Factura f
INNER JOIN OrdenTrabajo ot ON f.orden_codigo = ot.codigo
WHERE f.total > (SELECT AVG(total) FROM Factura)
ORDER BY f.total DESC;

-- 37. Órdenes de trabajo con más servicios que el promedio
SELECT 
    ot.codigo,
    ot.fecha_ingreso,
    ot.estado,
    v.placa,
    (SELECT COUNT(*) 
     FROM OrdenServicio os2 
     WHERE os2.orden_codigo = ot.codigo) AS total_servicios
FROM OrdenTrabajo ot
INNER JOIN Vehiculo v ON ot.placa = v.placa
WHERE (SELECT COUNT(*) 
       FROM OrdenServicio os 
       WHERE os.orden_codigo = ot.codigo) > 
      (SELECT AVG(servicios_count)
       FROM (SELECT COUNT(*) AS servicios_count
             FROM OrdenServicio
             GROUP BY orden_codigo) AS subconsulta)
ORDER BY total_servicios DESC;

-- 38. Clientes con vehículos de las marcas más comunes
SELECT 
    c.id_cliente,
    c.nombre,
    v.placa,
    v.marca,
    v.modelo
FROM Cliente c
INNER JOIN Vehiculo v ON c.id_cliente = v.id_cliente
WHERE v.marca IN (
    SELECT marca
    FROM (
        SELECT marca, COUNT(*) AS total
        FROM Vehiculo
        GROUP BY marca
        ORDER BY total DESC
        LIMIT 3
    ) AS top_marcas
)
ORDER BY v.marca, c.nombre;

-- 39. Mecánicos con especialidades en categorías de servicios más demandadas
SELECT DISTINCT
    m.id_mecanico,
    m.nombre AS mecanico,
    e.nombre AS especialidad
FROM Mecanico m
INNER JOIN MecanicoEspecialidad me ON m.id_mecanico = me.mecanico_id
INNER JOIN Especialidad e ON me.especialidad_codigo = e.codigo
WHERE e.nombre IN (
    SELECT s.categoria
    FROM Servicio s
    INNER JOIN OrdenServicio os ON s.codigo = os.servicio_codigo
    GROUP BY s.categoria
    ORDER BY COUNT(*) DESC
    LIMIT 5
)
ORDER BY m.nombre, e.nombre;

-- 40. Repuestos con precio de compra más bajo que el promedio de su categoría
SELECT 
    r.codigo,
    r.nombre,
    rp.precio_compra,
    p.nombre AS proveedor,
    (SELECT AVG(rp2.precio_compra)
     FROM RepuestoProveedor rp2
     WHERE rp2.repuesto_codigo = r.codigo) AS precio_promedio
FROM Repuesto r
INNER JOIN RepuestoProveedor rp ON r.codigo = rp.repuesto_codigo
INNER JOIN Proveedor p ON rp.proveedor_id = p.id
WHERE rp.precio_compra < (
    SELECT AVG(rp2.precio_compra)
    FROM RepuestoProveedor rp2
    WHERE rp2.repuesto_codigo = r.codigo
)
ORDER BY r.nombre, rp.precio_compra;


-- ==========================================
-- SECCIÓN 5: CONSULTAS COMPLEJAS CON MÚLTIPLES JOINS Y AGREGACIONES
-- ==========================================

-- 41. Reporte completo de orden de trabajo con todos los detalles
SELECT 
    ot.codigo AS codigo_orden,
    ot.fecha_ingreso,
    ot.estado,
    ot.diagnostico_inicial,
    v.placa,
    v.marca,
    v.modelo,
    v.anio,
    c.nombre AS cliente,
    c.telefono AS telefono_cliente,
    c.tipo AS tipo_cliente,
    COUNT(DISTINCT os.servicio_codigo) AS total_servicios,
    COUNT(DISTINCT orep.repuesto_codigo) AS total_repuestos,
    COUNT(DISTINCT om.mecanico_id) AS total_mecanicos,
    COALESCE(SUM(DISTINCT os.cantidad * os.precio_aplicado), 0) AS costo_servicios,
    COALESCE(SUM(DISTINCT orep.cantidad_usada * orep.precio_aplicado), 0) AS costo_repuestos,
    COALESCE(SUM(DISTINCT os.cantidad * os.precio_aplicado), 0) + 
    COALESCE(SUM(DISTINCT orep.cantidad_usada * orep.precio_aplicado), 0) AS costo_total
FROM OrdenTrabajo ot
INNER JOIN Vehiculo v ON ot.placa = v.placa
INNER JOIN Cliente c ON v.id_cliente = c.id_cliente
LEFT JOIN OrdenServicio os ON ot.codigo = os.orden_codigo
LEFT JOIN OrdenRepuesto orep ON ot.codigo = orep.orden_codigo
LEFT JOIN OrdenMecanico om ON ot.codigo = om.orden_codigo
GROUP BY ot.codigo, ot.fecha_ingreso, ot.estado, ot.diagnostico_inicial,
         v.placa, v.marca, v.modelo, v.anio,
         c.nombre, c.telefono, c.tipo
ORDER BY ot.fecha_ingreso DESC;

-- 42. Dashboard principal - Estadísticas generales del taller
SELECT 
    (SELECT COUNT(*) FROM Cliente) AS total_clientes,
    (SELECT COUNT(*) FROM Vehiculo) AS total_vehiculos,
    (SELECT COUNT(*) FROM OrdenTrabajo) AS total_ordenes,
    (SELECT COUNT(*) FROM OrdenTrabajo WHERE estado = 'PENDIENTE') AS ordenes_pendientes,
    (SELECT COUNT(*) FROM OrdenTrabajo WHERE estado = 'EN PROCESO') AS ordenes_en_proceso,
    (SELECT COUNT(*) FROM OrdenTrabajo WHERE estado = 'COMPLETADA') AS ordenes_completadas,
    (SELECT COUNT(*) FROM Factura WHERE estado_pago = 'PENDIENTE') AS facturas_pendientes,
    (SELECT COUNT(*) FROM Factura WHERE estado_pago = 'PAGADA') AS facturas_pagadas,
    (SELECT COALESCE(SUM(total), 0) FROM Factura WHERE estado_pago = 'PAGADA') AS ingresos_totales,
    (SELECT COALESCE(SUM(total), 0) FROM Factura WHERE estado_pago = 'PENDIENTE') AS cuentas_por_cobrar,
    (SELECT COUNT(*) FROM Mecanico WHERE activo = TRUE) AS mecanicos_activos,
    (SELECT COUNT(*) FROM Repuesto WHERE stock < stock_minimo) AS repuestos_stock_bajo;

-- 43. Historial completo de servicios por vehículo
SELECT 
    v.placa,
    v.marca,
    v.modelo,
    c.nombre AS cliente,
    ot.codigo AS codigo_orden,
    ot.fecha_ingreso,
    ot.estado,
    GROUP_CONCAT(DISTINCT s.nombre SEPARATOR ', ') AS servicios_realizados,
    GROUP_CONCAT(DISTINCT r.nombre SEPARATOR ', ') AS repuestos_usados,
    GROUP_CONCAT(DISTINCT m.nombre SEPARATOR ', ') AS mecanicos_asignados,
    COALESCE(f.total, 0) AS costo_total
FROM Vehiculo v
INNER JOIN Cliente c ON v.id_cliente = c.id_cliente
INNER JOIN OrdenTrabajo ot ON v.placa = ot.placa
LEFT JOIN OrdenServicio os ON ot.codigo = os.orden_codigo
LEFT JOIN Servicio s ON os.servicio_codigo = s.codigo
LEFT JOIN OrdenRepuesto orep ON ot.codigo = orep.orden_codigo
LEFT JOIN Repuesto r ON orep.repuesto_codigo = r.codigo
LEFT JOIN OrdenMecanico om ON ot.codigo = om.orden_codigo
LEFT JOIN Mecanico m ON om.mecanico_id = m.id_mecanico
LEFT JOIN Factura f ON ot.codigo = f.orden_codigo
GROUP BY v.placa, v.marca, v.modelo, c.nombre, ot.codigo, 
         ot.fecha_ingreso, ot.estado, f.total
ORDER BY ot.fecha_ingreso DESC;

-- 44. Análisis de rentabilidad por servicio
SELECT 
    s.codigo,
    s.nombre AS servicio,
    s.categoria,
    s.precio AS precio_base,
    COUNT(os.orden_codigo) AS veces_vendido,
    SUM(os.cantidad) AS cantidad_total,
    ROUND(AVG(os.precio_aplicado), 2) AS precio_promedio_aplicado,
    SUM(os.cantidad * os.precio_aplicado) AS ingreso_total,
    ROUND(SUM(os.cantidad * os.precio_aplicado) / COUNT(os.orden_codigo), 2) AS ingreso_promedio_por_orden
FROM Servicio s
LEFT JOIN OrdenServicio os ON s.codigo = os.servicio_codigo
GROUP BY s.codigo, s.nombre, s.categoria, s.precio
ORDER BY ingreso_total DESC;

-- 45. Análisis de rotación de repuestos
SELECT 
    r.codigo,
    r.nombre,
    r.stock AS stock_actual,
    r.stock_minimo,
    COALESCE(SUM(orep.cantidad_usada), 0) AS total_usado,
    COUNT(DISTINCT orep.orden_codigo) AS ordenes_utilizadas,
    COALESCE(SUM(orep.cantidad_usada * orep.precio_aplicado), 0) AS ingresos_generados,
    ROUND(COALESCE(SUM(orep.cantidad_usada), 0) / 
          NULLIF(DATEDIFF(CURDATE(), r.fecha_creacion) / 30, 0), 2) AS uso_mensual_promedio,
    CASE 
        WHEN r.stock < r.stock_minimo THEN 'CRÍTICO'
        WHEN r.stock < (r.stock_minimo * 1.5) THEN 'BAJO'
        ELSE 'NORMAL'
    END AS estado_stock
FROM Repuesto r
LEFT JOIN OrdenRepuesto orep ON r.codigo = orep.repuesto_codigo
GROUP BY r.codigo, r.nombre, r.stock, r.stock_minimo, r.fecha_creacion
ORDER BY uso_mensual_promedio DESC, ingresos_generados DESC;

-- 46. Rendimiento comparativo de mecánicos
SELECT 
    m.id_mecanico,
    m.nombre AS mecanico,
    supervisor.nombre AS supervisor,
    COUNT(DISTINCT om.orden_codigo) AS ordenes_trabajadas,
    SUM(om.horas_trabajadas) AS total_horas,
    ROUND(AVG(om.horas_trabajadas), 2) AS horas_promedio_por_orden,
    COUNT(DISTINCT me.especialidad_codigo) AS especialidades,
    GROUP_CONCAT(DISTINCT e.nombre SEPARATOR ', ') AS lista_especialidades,
    COUNT(DISTINCT CASE WHEN ot.estado = 'COMPLETADA' THEN om.orden_codigo END) AS ordenes_completadas,
    ROUND(COUNT(DISTINCT CASE WHEN ot.estado = 'COMPLETADA' THEN om.orden_codigo END) * 100.0 / 
          NULLIF(COUNT(DISTINCT om.orden_codigo), 0), 2) AS tasa_completado
FROM Mecanico m
LEFT JOIN Mecanico supervisor ON m.supervisor_id = supervisor.id_mecanico
LEFT JOIN OrdenMecanico om ON m.id_mecanico = om.mecanico_id
LEFT JOIN OrdenTrabajo ot ON om.orden_codigo = ot.codigo
LEFT JOIN MecanicoEspecialidad me ON m.id_mecanico = me.mecanico_id
LEFT JOIN Especialidad e ON me.especialidad_codigo = e.codigo
WHERE m.activo = TRUE
GROUP BY m.id_mecanico, m.nombre, supervisor.nombre
ORDER BY ordenes_completadas DESC, total_horas DESC;

-- 47. Análisis de tiempo de servicio por tipo de vehículo
SELECT 
    v.tipo AS tipo_vehiculo,
    v.marca,
    COUNT(DISTINCT ot.codigo) AS total_ordenes,
    ROUND(AVG(DATEDIFF(COALESCE(f.fecha_pago, CURDATE()), ot.fecha_ingreso)), 2) AS dias_promedio_servicio,
    ROUND(AVG(f.total), 2) AS costo_promedio,
    SUM(f.total) AS ingreso_total
FROM Vehiculo v
INNER JOIN OrdenTrabajo ot ON v.placa = ot.placa
LEFT JOIN Factura f ON ot.codigo = f.orden_codigo
GROUP BY v.tipo, v.marca
HAVING total_ordenes >= 1
ORDER BY total_ordenes DESC, ingreso_total DESC;

-- 48. Análisis de clientes corporativos vs individuales
SELECT 
    c.tipo AS tipo_cliente,
    COUNT(DISTINCT c.id_cliente) AS total_clientes,
    COUNT(DISTINCT v.placa) AS total_vehiculos,
    ROUND(COUNT(DISTINCT v.placa) / COUNT(DISTINCT c.id_cliente), 2) AS vehiculos_por_cliente,
    COUNT(DISTINCT ot.codigo) AS total_ordenes,
    ROUND(COUNT(DISTINCT ot.codigo) / COUNT(DISTINCT c.id_cliente), 2) AS ordenes_por_cliente,
    COALESCE(SUM(f.total), 0) AS ingresos_totales,
    ROUND(COALESCE(AVG(f.total), 0), 2) AS promedio_por_factura,
    ROUND(COALESCE(SUM(f.total) / COUNT(DISTINCT c.id_cliente), 0), 2) AS ingreso_por_cliente
FROM Cliente c
LEFT JOIN Vehiculo v ON c.id_cliente = v.id_cliente
LEFT JOIN OrdenTrabajo ot ON v.placa = ot.placa
LEFT JOIN Factura f ON ot.codigo = f.orden_codigo
GROUP BY c.tipo
ORDER BY ingresos_totales DESC;

-- 49. Proveedores más utilizados y rentables
SELECT 
    p.id,
    p.nombre AS proveedor,
    p.correo,
    p.telefono,
    COUNT(DISTINCT rp.repuesto_codigo) AS repuestos_suministrados,
    ROUND(AVG(rp.precio_compra), 2) AS precio_compra_promedio,
    ROUND(AVG(rp.tiempo_entrega_dias), 2) AS tiempo_entrega_promedio,
    COUNT(DISTINCT orep.orden_codigo) AS ordenes_con_repuestos,
    COALESCE(SUM(orep.cantidad_usada), 0) AS unidades_vendidas,
    COALESCE(SUM(orep.cantidad_usada * orep.precio_aplicado), 0) AS ingresos_generados,
    COALESCE(SUM(orep.cantidad_usada * (orep.precio_aplicado - rp.precio_compra)), 0) AS margen_ganancia
FROM Proveedor p
INNER JOIN RepuestoProveedor rp ON p.id = rp.proveedor_id
LEFT JOIN OrdenRepuesto orep ON rp.repuesto_codigo = orep.repuesto_codigo
WHERE p.activo = TRUE
GROUP BY p.id, p.nombre, p.correo, p.telefono
ORDER BY margen_ganancia DESC, unidades_vendidas DESC;

-- 50. Análisis de supervisión de mecánicos
SELECT 
    supervisor.id_mecanico AS id_supervisor,
    supervisor.nombre AS supervisor,
    COUNT(DISTINCT mecanico.id_mecanico) AS mecanicos_supervisados,
    COUNT(DISTINCT rs.codigo) AS registros_supervision,
    ROUND(AVG(om.horas_trabajadas), 2) AS horas_promedio_equipo,
    COUNT(DISTINCT om.orden_codigo) AS ordenes_equipo,
    GROUP_CONCAT(DISTINCT mecanico.nombre SEPARATOR ', ') AS equipo
FROM Mecanico supervisor
INNER JOIN Mecanico mecanico ON supervisor.id_mecanico = mecanico.supervisor_id
LEFT JOIN RegistroSup rs ON supervisor.id_mecanico = rs.supervisor_id
LEFT JOIN OrdenMecanico om ON mecanico.id_mecanico = om.mecanico_id
WHERE supervisor.activo = TRUE
GROUP BY supervisor.id_mecanico, supervisor.nombre
ORDER BY mecanicos_supervisados DESC, ordenes_equipo DESC;


-- ==========================================
-- SECCIÓN 6: VISTAS ÚTILES
-- ==========================================

-- Vista 1: Vista de órdenes de trabajo con información básica
CREATE OR REPLACE VIEW vista_ordenes_trabajo AS
SELECT 
    ot.codigo,
    ot.fecha_ingreso,
    ot.estado,
    v.placa,
    v.marca,
    v.modelo,
    c.nombre AS cliente,
    c.telefono AS telefono_cliente,
    COALESCE(f.total, 0) AS total_factura,
    f.estado_pago
FROM OrdenTrabajo ot
INNER JOIN Vehiculo v ON ot.placa = v.placa
INNER JOIN Cliente c ON v.id_cliente = c.id_cliente
LEFT JOIN Factura f ON ot.codigo = f.orden_codigo;

-- Vista 2: Vista de inventario de repuestos
CREATE OR REPLACE VIEW vista_inventario_repuestos AS
SELECT 
    r.codigo,
    r.nombre,
    r.stock,
    r.stock_minimo,
    r.precio_unitario,
    CASE 
        WHEN r.stock < r.stock_minimo THEN 'CRÍTICO'
        WHEN r.stock < (r.stock_minimo * 1.5) THEN 'BAJO'
        ELSE 'NORMAL'
    END AS estado_stock,
    COALESCE(COUNT(DISTINCT orep.orden_codigo), 0) AS veces_usado
FROM Repuesto r
LEFT JOIN OrdenRepuesto orep ON r.codigo = orep.repuesto_codigo
GROUP BY r.codigo, r.nombre, r.stock, r.stock_minimo, r.precio_unitario;

-- Vista 3: Vista de facturación mensual
CREATE OR REPLACE VIEW vista_facturacion_mensual AS
SELECT 
    YEAR(f.fecha_emision) AS anio,
    MONTH(f.fecha_emision) AS mes,
    MONTHNAME(f.fecha_emision) AS nombre_mes,
    COUNT(*) AS total_facturas,
    SUM(f.subtotal) AS subtotal,
    SUM(f.total) AS total,
    COUNT(CASE WHEN f.estado_pago = 'PAGADA' THEN 1 END) AS facturas_pagadas,
    COUNT(CASE WHEN f.estado_pago = 'PENDIENTE' THEN 1 END) AS facturas_pendientes
FROM Factura f
GROUP BY YEAR(f.fecha_emision), MONTH(f.fecha_emision), MONTHNAME(f.fecha_emision);

-- Vista 4: Vista de mecánicos con sus especialidades
CREATE OR REPLACE VIEW vista_mecanicos_especialidades AS
SELECT 
    m.id_mecanico,
    m.nombre AS mecanico,
    m.telefono,
    supervisor.nombre AS supervisor,
    GROUP_CONCAT(e.nombre SEPARATOR ', ') AS especialidades,
    COUNT(DISTINCT me.especialidad_codigo) AS total_especialidades
FROM Mecanico m
LEFT JOIN Mecanico supervisor ON m.supervisor_id = supervisor.id_mecanico
LEFT JOIN MecanicoEspecialidad me ON m.id_mecanico = me.mecanico_id
LEFT JOIN Especialidad e ON me.especialidad_codigo = e.codigo
WHERE m.activo = TRUE
GROUP BY m.id_mecanico, m.nombre, m.telefono, supervisor.nombre;

-- Vista 5: Vista de clientes con resumen de actividad
CREATE OR REPLACE VIEW vista_clientes_resumen AS
SELECT 
    c.id_cliente,
    c.nombre,
    c.telefono,
    c.tipo,
    COUNT(DISTINCT v.placa) AS total_vehiculos,
    COUNT(DISTINCT ot.codigo) AS total_ordenes,
    COALESCE(SUM(f.total), 0) AS gasto_total,
    MAX(ot.fecha_ingreso) AS ultima_visita
FROM Cliente c
LEFT JOIN Vehiculo v ON c.id_cliente = v.id_cliente
LEFT JOIN OrdenTrabajo ot ON v.placa = ot.placa
LEFT JOIN Factura f ON ot.codigo = f.orden_codigo
GROUP BY c.id_cliente, c.nombre, c.telefono, c.tipo;


-- ==========================================
-- SECCIÓN 7: PROCEDIMIENTOS ALMACENADOS
-- ==========================================

-- Procedimiento 1: Crear una orden de trabajo completa
DELIMITER //
CREATE PROCEDURE sp_crear_orden_trabajo(
    IN p_placa VARCHAR(20),
    IN p_diagnostico TEXT,
    IN p_mecanico_id INT
)
BEGIN
    DECLARE v_orden_codigo INT;
    
    -- Insertar orden de trabajo
    INSERT INTO OrdenTrabajo (fecha_ingreso, diagnostico_inicial, estado, placa)
    VALUES (CURDATE(), p_diagnostico, 'PENDIENTE', p_placa);
    
    SET v_orden_codigo = LAST_INSERT_ID();
    
    -- Asignar mecánico
    INSERT INTO OrdenMecanico (mecanico_id, orden_codigo, rol)
    VALUES (p_mecanico_id, v_orden_codigo, 'PRINCIPAL');
    
    SELECT v_orden_codigo AS codigo_orden;
END //
DELIMITER ;

-- Procedimiento 2: Agregar servicio a orden de trabajo
DELIMITER //
CREATE PROCEDURE sp_agregar_servicio_orden(
    IN p_orden_codigo INT,
    IN p_servicio_codigo INT,
    IN p_cantidad INT
)
BEGIN
    DECLARE v_precio DECIMAL(10,2);
    
    -- Obtener precio del servicio
    SELECT precio INTO v_precio
    FROM Servicio
    WHERE codigo = p_servicio_codigo;
    
    -- Insertar el servicio en la orden
    INSERT INTO OrdenServicio (orden_codigo, servicio_codigo, cantidad, precio_aplicado)
    VALUES (p_orden_codigo, p_servicio_codigo, p_cantidad, v_precio);
    
    SELECT 'Servicio agregado exitosamente' AS resultado;
END //
DELIMITER ;

-- Procedimiento 3: Agregar repuesto a orden de trabajo
DELIMITER //
CREATE PROCEDURE sp_agregar_repuesto_orden(
    IN p_orden_codigo INT,
    IN p_repuesto_codigo INT,
    IN p_cantidad INT
)
BEGIN
    DECLARE v_precio DECIMAL(10,2);
    DECLARE v_stock_actual INT;
    
    -- Obtener precio y stock del repuesto
    SELECT precio_unitario, stock INTO v_precio, v_stock_actual
    FROM Repuesto
    WHERE codigo = p_repuesto_codigo;
    
    -- Verificar stock disponible
    IF v_stock_actual < p_cantidad THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Stock insuficiente';
    END IF;
    
    -- Insertar el repuesto en la orden
    INSERT INTO OrdenRepuesto (orden_codigo, repuesto_codigo, cantidad_usada, precio_aplicado)
    VALUES (p_orden_codigo, p_repuesto_codigo, p_cantidad, v_precio);
    
    -- Actualizar stock
    UPDATE Repuesto
    SET stock = stock - p_cantidad
    WHERE codigo = p_repuesto_codigo;
    
    SELECT 'Repuesto agregado exitosamente' AS resultado;
END //
DELIMITER ;

-- Procedimiento 4: Generar factura para orden de trabajo
DELIMITER //
CREATE PROCEDURE sp_generar_factura(
    IN p_orden_codigo INT
)
BEGIN
    DECLARE v_subtotal DECIMAL(10,2);
    DECLARE v_iva DECIMAL(10,2);
    DECLARE v_total DECIMAL(10,2);
    DECLARE v_factura_id INT;
    
    -- Calcular subtotal (servicios + repuestos)
    SELECT 
        COALESCE(SUM(os.cantidad * os.precio_aplicado), 0) +
        COALESCE(SUM(orep.cantidad_usada * orep.precio_aplicado), 0)
    INTO v_subtotal
    FROM OrdenTrabajo ot
    LEFT JOIN OrdenServicio os ON ot.codigo = os.orden_codigo
    LEFT JOIN OrdenRepuesto orep ON ot.codigo = orep.orden_codigo
    WHERE ot.codigo = p_orden_codigo;
    
    -- Calcular IVA (19%)
    SET v_iva = v_subtotal * 0.19;
    SET v_total = v_subtotal + v_iva;
    
    -- Crear factura
    INSERT INTO Factura (fecha_emision, subtotal, total, estado_pago, orden_codigo)
    VALUES (CURDATE(), v_subtotal, v_total, 'PENDIENTE', p_orden_codigo);
    
    SET v_factura_id = LAST_INSERT_ID();
    
    -- Agregar impuesto IVA a la factura
    INSERT INTO FacturaImpuesto (factura_id, impuesto_codigo, monto_aplicado)
    SELECT v_factura_id, codigo, v_iva
    FROM Impuesto
    WHERE nombre = 'IVA' AND activo = TRUE;
    
    -- Actualizar estado de la orden
    UPDATE OrdenTrabajo
    SET estado = 'COMPLETADA'
    WHERE codigo = p_orden_codigo;
    
    SELECT v_factura_id AS factura_id, v_subtotal AS subtotal, v_total AS total;
END //
DELIMITER ;

-- Procedimiento 5: Registrar pago de factura
DELIMITER //
CREATE PROCEDURE sp_registrar_pago_factura(
    IN p_factura_id INT
)
BEGIN
    UPDATE Factura
    SET estado_pago = 'PAGADA',
        fecha_pago = NOW()
    WHERE id_factura = p_factura_id;
    
    SELECT 'Pago registrado exitosamente' AS resultado;
END //
DELIMITER ;

-- Procedimiento 6: Obtener estadísticas del taller por rango de fechas
DELIMITER //
CREATE PROCEDURE sp_estadisticas_taller(
    IN p_fecha_inicio DATE,
    IN p_fecha_fin DATE
)
BEGIN
    SELECT 
        COUNT(DISTINCT ot.codigo) AS total_ordenes,
        COUNT(DISTINCT CASE WHEN ot.estado = 'COMPLETADA' THEN ot.codigo END) AS ordenes_completadas,
        COUNT(DISTINCT f.id_factura) AS total_facturas,
        COALESCE(SUM(f.total), 0) AS ingresos_totales,
        COUNT(DISTINCT c.id_cliente) AS clientes_atendidos,
        COUNT(DISTINCT m.id_mecanico) AS mecanicos_trabajaron,
        ROUND(AVG(f.total), 2) AS ticket_promedio
    FROM OrdenTrabajo ot
    LEFT JOIN Factura f ON ot.codigo = f.orden_codigo
    LEFT JOIN Vehiculo v ON ot.placa = v.placa
    LEFT JOIN Cliente c ON v.id_cliente = c.id_cliente
    LEFT JOIN OrdenMecanico om ON ot.codigo = om.orden_codigo
    LEFT JOIN Mecanico m ON om.mecanico_id = m.id_mecanico
    WHERE ot.fecha_ingreso BETWEEN p_fecha_inicio AND p_fecha_fin;
END //
DELIMITER ;

-- Procedimiento 7: Obtener repuestos con stock bajo
DELIMITER //
CREATE PROCEDURE sp_repuestos_stock_bajo()
BEGIN
    SELECT 
        r.codigo,
        r.nombre,
        r.stock AS stock_actual,
        r.stock_minimo,
        (r.stock_minimo - r.stock) AS cantidad_faltante,
        GROUP_CONCAT(DISTINCT p.nombre SEPARATOR ', ') AS proveedores
    FROM Repuesto r
    LEFT JOIN RepuestoProveedor rp ON r.codigo = rp.repuesto_codigo
    LEFT JOIN Proveedor p ON rp.proveedor_id = p.id
    WHERE r.stock < r.stock_minimo
    GROUP BY r.codigo, r.nombre, r.stock, r.stock_minimo
    ORDER BY cantidad_faltante DESC;
END //
DELIMITER ;

-- Procedimiento 8: Obtener historial de servicios de un vehículo
DELIMITER //
CREATE PROCEDURE sp_historial_vehiculo(
    IN p_placa VARCHAR(20)
)
BEGIN
    SELECT 
        ot.codigo AS codigo_orden,
        ot.fecha_ingreso,
        ot.estado,
        ot.diagnostico_inicial,
        GROUP_CONCAT(DISTINCT s.nombre SEPARATOR ', ') AS servicios,
        GROUP_CONCAT(DISTINCT r.nombre SEPARATOR ', ') AS repuestos,
        GROUP_CONCAT(DISTINCT m.nombre SEPARATOR ', ') AS mecanicos,
        COALESCE(f.total, 0) AS costo_total,
        f.estado_pago
    FROM OrdenTrabajo ot
    LEFT JOIN OrdenServicio os ON ot.codigo = os.orden_codigo
    LEFT JOIN Servicio s ON os.servicio_codigo = s.codigo
    LEFT JOIN OrdenRepuesto orep ON ot.codigo = orep.orden_codigo
    LEFT JOIN Repuesto r ON orep.repuesto_codigo = r.codigo
    LEFT JOIN OrdenMecanico om ON ot.codigo = om.orden_codigo
    LEFT JOIN Mecanico m ON om.mecanico_id = m.id_mecanico
    LEFT JOIN Factura f ON ot.codigo = f.orden_codigo
    WHERE ot.placa = p_placa
    GROUP BY ot.codigo, ot.fecha_ingreso, ot.estado, ot.diagnostico_inicial, f.total, f.estado_pago
    ORDER BY ot.fecha_ingreso DESC;
END //
DELIMITER ;


-- ==========================================
-- SECCIÓN 8: FUNCIONES ÚTILES
-- ==========================================

-- Función 1: Calcular el total de una orden de trabajo
DELIMITER //
CREATE FUNCTION fn_calcular_total_orden(p_orden_codigo INT)
RETURNS DECIMAL(10,2)
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_total DECIMAL(10,2);
    
    SELECT 
        COALESCE(SUM(os.cantidad * os.precio_aplicado), 0) +
        COALESCE(SUM(orep.cantidad_usada * orep.precio_aplicado), 0)
    INTO v_total
    FROM OrdenTrabajo ot
    LEFT JOIN OrdenServicio os ON ot.codigo = os.orden_codigo
    LEFT JOIN OrdenRepuesto orep ON ot.codigo = orep.orden_codigo
    WHERE ot.codigo = p_orden_codigo;
    
    RETURN COALESCE(v_total, 0);
END //
DELIMITER ;

-- Función 2: Obtener total de órdenes de un cliente
DELIMITER //
CREATE FUNCTION fn_total_ordenes_cliente(p_cliente_id INT)
RETURNS INT
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_total INT;
    
    SELECT COUNT(DISTINCT ot.codigo)
    INTO v_total
    FROM Cliente c
    INNER JOIN Vehiculo v ON c.id_cliente = v.id_cliente
    INNER JOIN OrdenTrabajo ot ON v.placa = ot.placa
    WHERE c.id_cliente = p_cliente_id;
    
    RETURN COALESCE(v_total, 0);
END //
DELIMITER ;

-- Función 3: Calcular días promedio de servicio
DELIMITER //
CREATE FUNCTION fn_dias_promedio_servicio()
RETURNS DECIMAL(10,2)
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_promedio DECIMAL(10,2);
    
    SELECT AVG(DATEDIFF(COALESCE(f.fecha_pago, CURDATE()), ot.fecha_ingreso))
    INTO v_promedio
    FROM OrdenTrabajo ot
    LEFT JOIN Factura f ON ot.codigo = f.orden_codigo
    WHERE ot.estado = 'COMPLETADA';
    
    RETURN COALESCE(v_promedio, 0);
END //
DELIMITER ;


-- ==========================================
-- SECCIÓN 9: TRIGGERS
-- ==========================================

-- Trigger 1: Auditoría de cambios en órdenes de trabajo
DELIMITER //
CREATE TRIGGER trg_orden_trabajo_auditoria
AFTER UPDATE ON OrdenTrabajo
FOR EACH ROW
BEGIN
    IF OLD.estado != NEW.estado THEN
        INSERT INTO auditoria_ordenes (orden_codigo, estado_anterior, estado_nuevo, fecha_cambio)
        VALUES (NEW.codigo, OLD.estado, NEW.estado, NOW());
    END IF;
END //
DELIMITER ;

-- Trigger 2: Actualizar stock al agregar repuesto a orden (alternativo)
DELIMITER //
CREATE TRIGGER trg_orden_repuesto_stock
AFTER INSERT ON OrdenRepuesto
FOR EACH ROW
BEGIN
    UPDATE Repuesto
    SET stock = stock - NEW.cantidad_usada
    WHERE codigo = NEW.repuesto_codigo;
END //
DELIMITER ;

-- Trigger 3: Validar stock antes de agregar repuesto
DELIMITER //
CREATE TRIGGER trg_validar_stock_repuesto
BEFORE INSERT ON OrdenRepuesto
FOR EACH ROW
BEGIN
    DECLARE v_stock_actual INT;
    
    SELECT stock INTO v_stock_actual
    FROM Repuesto
    WHERE codigo = NEW.repuesto_codigo;
    
    IF v_stock_actual < NEW.cantidad_usada THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Stock insuficiente para el repuesto solicitado';
    END IF;
END //
DELIMITER ;


-- ==========================================
-- SECCIÓN 10: CONSULTAS ADICIONALES ESPECÍFICAS
-- ==========================================

-- 51. Análisis de servicios por mes
SELECT 
    YEAR(ot.fecha_ingreso) AS anio,
    MONTH(ot.fecha_ingreso) AS mes,
    MONTHNAME(ot.fecha_ingreso) AS nombre_mes,
    COUNT(DISTINCT ot.codigo) AS total_ordenes,
    COUNT(DISTINCT os.servicio_codigo) AS servicios_distintos,
    SUM(os.cantidad) AS total_servicios,
    SUM(os.cantidad * os.precio_aplicado) AS ingresos_servicios
FROM OrdenTrabajo ot
INNER JOIN OrdenServicio os ON ot.codigo = os.orden_codigo
GROUP BY YEAR(ot.fecha_ingreso), MONTH(ot.fecha_ingreso), MONTHNAME(ot.fecha_ingreso)
ORDER BY anio DESC, mes DESC;

-- 52. Análisis de tendencia de vehículos por año
SELECT 
    v.anio,
    COUNT(*) AS total_vehiculos,
    COUNT(DISTINCT v.marca) AS marcas_distintas,
    COUNT(DISTINCT ot.codigo) AS ordenes_asociadas,
    ROUND(AVG(f.total), 2) AS gasto_promedio
FROM Vehiculo v
LEFT JOIN OrdenTrabajo ot ON v.placa = ot.placa
LEFT JOIN Factura f ON ot.codigo = f.orden_codigo
GROUP BY v.anio
ORDER BY v.anio DESC;

-- 53. Clientes VIP (mayor gasto en el último año)
SELECT 
    c.id_cliente,
    c.nombre,
    c.tipo,
    c.telefono,
    COUNT(DISTINCT v.placa) AS vehiculos,
    COUNT(DISTINCT ot.codigo) AS ordenes_ultimo_año,
    COALESCE(SUM(f.total), 0) AS gasto_ultimo_año
FROM Cliente c
INNER JOIN Vehiculo v ON c.id_cliente = v.id_cliente
INNER JOIN OrdenTrabajo ot ON v.placa = ot.placa
INNER JOIN Factura f ON ot.codigo = f.orden_codigo
WHERE ot.fecha_ingreso >= DATE_SUB(CURDATE(), INTERVAL 1 YEAR)
GROUP BY c.id_cliente, c.nombre, c.tipo, c.telefono
HAVING gasto_ultimo_año > 0
ORDER BY gasto_ultimo_año DESC
LIMIT 10;

-- 54. Mecánicos más eficientes (menos horas por orden completada)
SELECT 
    m.id_mecanico,
    m.nombre,
    COUNT(DISTINCT om.orden_codigo) AS ordenes_completadas,
    SUM(om.horas_trabajadas) AS total_horas,
    ROUND(SUM(om.horas_trabajadas) / COUNT(DISTINCT om.orden_codigo), 2) AS horas_promedio_por_orden
FROM Mecanico m
INNER JOIN OrdenMecanico om ON m.id_mecanico = om.mecanico_id
INNER JOIN OrdenTrabajo ot ON om.orden_codigo = ot.codigo
WHERE ot.estado = 'COMPLETADA'
AND m.activo = TRUE
GROUP BY m.id_mecanico, m.nombre
HAVING ordenes_completadas >= 5
ORDER BY horas_promedio_por_orden ASC;

-- 55. Servicios complementarios (servicios que se solicitan juntos frecuentemente)
SELECT 
    s1.nombre AS servicio_1,
    s2.nombre AS servicio_2,
    COUNT(*) AS veces_juntos
FROM OrdenServicio os1
INNER JOIN OrdenServicio os2 ON os1.orden_codigo = os2.orden_codigo
INNER JOIN Servicio s1 ON os1.servicio_codigo = s1.codigo
INNER JOIN Servicio s2 ON os2.servicio_codigo = s2.codigo
WHERE os1.servicio_codigo < os2.servicio_codigo
GROUP BY s1.nombre, s2.nombre
HAVING veces_juntos >= 3
ORDER BY veces_juntos DESC
LIMIT 10;

-- 56. Análisis de morosidad
SELECT 
    c.id_cliente,
    c.nombre AS cliente,
    c.tipo,
    COUNT(f.id_factura) AS facturas_pendientes,
    SUM(f.total) AS deuda_total,
    MIN(f.fecha_emision) AS factura_mas_antigua,
    DATEDIFF(CURDATE(), MIN(f.fecha_emision)) AS dias_mora_maxima
FROM Cliente c
INNER JOIN Vehiculo v ON c.id_cliente = v.id_cliente
INNER JOIN OrdenTrabajo ot ON v.placa = ot.placa
INNER JOIN Factura f ON ot.codigo = f.orden_codigo
WHERE f.estado_pago = 'PENDIENTE'
GROUP BY c.id_cliente, c.nombre, c.tipo
ORDER BY dias_mora_maxima DESC, deuda_total DESC;

-- 57. Proyección de necesidad de repuestos
SELECT 
    r.codigo,
    r.nombre,
    r.stock AS stock_actual,
    r.stock_minimo,
    COALESCE(AVG(uso_mensual.uso_mes), 0) AS uso_promedio_mensual,
    ROUND(r.stock / NULLIF(AVG(uso_mensual.uso_mes), 0), 2) AS meses_stock_restante,
    CASE 
        WHEN ROUND(r.stock / NULLIF(AVG(uso_mensual.uso_mes), 0), 2) < 1 THEN 'URGENTE'
        WHEN ROUND(r.stock / NULLIF(AVG(uso_mensual.uso_mes), 0), 2) < 2 THEN 'PRONTO'
        ELSE 'NORMAL'
    END AS prioridad_compra
FROM Repuesto r
LEFT JOIN (
    SELECT 
        repuesto_codigo,
        YEAR(ot.fecha_ingreso) AS anio,
        MONTH(ot.fecha_ingreso) AS mes,
        SUM(cantidad_usada) AS uso_mes
    FROM OrdenRepuesto orep
    INNER JOIN OrdenTrabajo ot ON orep.orden_codigo = ot.codigo
    WHERE ot.fecha_ingreso >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH)
    GROUP BY repuesto_codigo, YEAR(ot.fecha_ingreso), MONTH(ot.fecha_ingreso)
) AS uso_mensual ON r.codigo = uso_mensual.repuesto_codigo
GROUP BY r.codigo, r.nombre, r.stock, r.stock_minimo
HAVING uso_promedio_mensual > 0
ORDER BY prioridad_compra, meses_stock_restante ASC;

-- 58. Análisis de especialidades más demandadas
SELECT 
    e.codigo,
    e.nombre AS especialidad,
    COUNT(DISTINCT me.mecanico_id) AS mecanicos_con_especialidad,
    COUNT(DISTINCT om.orden_codigo) AS ordenes_relacionadas,
    ROUND(COUNT(DISTINCT om.orden_codigo) / 
          NULLIF(COUNT(DISTINCT me.mecanico_id), 0), 2) AS ordenes_por_mecanico
FROM Especialidad e
LEFT JOIN MecanicoEspecialidad me ON e.codigo = me.especialidad_codigo
LEFT JOIN OrdenMecanico om ON me.mecanico_id = om.mecanico_id
GROUP BY e.codigo, e.nombre
ORDER BY ordenes_relacionadas DESC;

-- 59. Facturación por día de la semana
SELECT 
    DAYNAME(f.fecha_emision) AS dia_semana,
    DAYOFWEEK(f.fecha_emision) AS numero_dia,
    COUNT(*) AS total_facturas,
    SUM(f.total) AS ingresos_totales,
    ROUND(AVG(f.total), 2) AS ticket_promedio
FROM Factura f
WHERE f.fecha_emision >= DATE_SUB(CURDATE(), INTERVAL 3 MONTH)
GROUP BY DAYNAME(f.fecha_emision), DAYOFWEEK(f.fecha_emision)
ORDER BY numero_dia;

-- 60. Reporte ejecutivo completo
SELECT 
    'Resumen Ejecutivo' AS seccion,
    (SELECT COUNT(*) FROM Cliente) AS total_clientes,
    (SELECT COUNT(*) FROM Vehiculo) AS total_vehiculos,
    (SELECT COUNT(*) FROM OrdenTrabajo WHERE MONTH(fecha_ingreso) = MONTH(CURDATE())) AS ordenes_mes_actual,
    (SELECT COALESCE(SUM(total), 0) FROM Factura WHERE estado_pago = 'PAGADA' AND MONTH(fecha_emision) = MONTH(CURDATE())) AS ingresos_mes_actual,
    (SELECT COUNT(*) FROM Mecanico WHERE activo = TRUE) AS mecanicos_activos,
    (SELECT COUNT(*) FROM Repuesto WHERE stock < stock_minimo) AS repuestos_criticos,
    (SELECT COUNT(*) FROM Factura WHERE estado_pago = 'PENDIENTE') AS facturas_pendientes,
    (SELECT COALESCE(SUM(total), 0) FROM Factura WHERE estado_pago = 'PENDIENTE') AS cuentas_por_cobrar;


-- ==========================================
-- FIN DEL ARCHIVO DE CONSULTAS SQL
-- ==========================================

-- Nota: Este archivo contiene 60 consultas SQL organizadas en 10 secciones:
-- 1. Consultas simples (1-10)
-- 2. Consultas con JOINs (11-20)
-- 3. Consultas con agregaciones (21-30)
-- 4. Consultas con subconsultas (31-40)
-- 5. Consultas complejas (41-50)
-- 6. Vistas útiles (5 vistas)
-- 7. Procedimientos almacenados (8 procedimientos)
-- 8. Funciones útiles (3 funciones)
-- 9. Triggers (3 triggers)
-- 10. Consultas adicionales específicas (51-60)

-- Total: 60 consultas + 5 vistas + 8 procedimientos + 3 funciones + 3 triggers
-- Todas las consultas son SQL puro, sin usar JPA o frameworks ORM

