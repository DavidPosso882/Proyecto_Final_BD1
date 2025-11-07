CREATE TABLE Cliente (
  id_cliente VARCHAR(20) PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  apellido VARCHAR(100) NULL,
  telefono VARCHAR(20),
  email VARCHAR(50),
  tipo VARCHAR(50) NOT NULL,
 
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  INDEX idx_nombre (nombre),
  INDEX idx_tipo (tipo)
) ENGINE=InnoDB;


CREATE TABLE Vehiculo (
  placa VARCHAR(20) PRIMARY KEY,
  tipo VARCHAR(50) NOT NULL,
  marca VARCHAR(50) NOT NULL,
  modelo VARCHAR(50),
  anio INT,
  documento_cliente VARCHAR(20) NOT NULL,
 
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 
  INDEX idx_cliente (documento_cliente),
  INDEX idx_marca (marca)
) ENGINE=InnoDB;


CREATE TABLE Servicio (
  codigo INT AUTO_INCREMENT PRIMARY KEY,
  descripcion VARCHAR(255),
  nombre VARCHAR(100) NOT NULL,
  categoria VARCHAR(50),
  precio DECIMAL(10,2) DEFAULT 0.00,
  
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  
  INDEX idx_categoria (categoria),
  INDEX idx_nombre (nombre)
) ENGINE=InnoDB;


CREATE TABLE OrdenTrabajo (
  codigo INT AUTO_INCREMENT PRIMARY KEY,
  fecha_ingreso DATE NOT NULL,
  diagnostico_inicial TEXT,
  estado VARCHAR(50) NOT NULL DEFAULT 'PENDIENTE',
  placa VARCHAR(20) NOT NULL,
  
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  usuario_creacion VARCHAR(50),
  usuario_modificacion VARCHAR(50),
  
  INDEX idx_placa (placa),
  INDEX idx_fecha_ingreso (fecha_ingreso),
  INDEX idx_estado (estado)
) ENGINE=InnoDB;


CREATE TABLE Factura (
  id_factura INT AUTO_INCREMENT PRIMARY KEY,
  fecha_emision DATE NOT NULL,
  subtotal DECIMAL(10,2) DEFAULT 0.00,
  total DECIMAL(10,2) DEFAULT 0.00,
  estado_pago VARCHAR(50) NOT NULL DEFAULT 'PENDIENTE',
  orden_codigo INT UNIQUE NOT NULL,
  
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  fecha_pago TIMESTAMP NULL,
  usuario_creacion VARCHAR(50),
  
  INDEX idx_fecha_emision (fecha_emision),
  INDEX idx_estado_pago (estado_pago)
) ENGINE=InnoDB;


CREATE TABLE Impuesto (
  codigo INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  porcentaje DECIMAL(5,2) NOT NULL,
  activo BOOLEAN DEFAULT TRUE,
  
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;


CREATE TABLE Repuesto (
  codigo INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  descripcion TEXT,
  stock INT DEFAULT 0,
  stock_minimo INT DEFAULT 5,
  precio_unitario DECIMAL(10,2) NOT NULL,
  
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  
  INDEX idx_nombre (nombre),
  INDEX idx_stock (stock)
) ENGINE=InnoDB;


CREATE TABLE Proveedor (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  correo VARCHAR(100),
  telefono VARCHAR(20),
  activo BOOLEAN DEFAULT TRUE,
  
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 
  INDEX idx_nombre (nombre)
) ENGINE=InnoDB;


CREATE TABLE Mecanico (
  id_mecanico INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  telefono VARCHAR(20),
  supervisor_id INT NULL,
  activo BOOLEAN DEFAULT TRUE,
  
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  
  INDEX idx_nombre (nombre),
  INDEX idx_supervisor (supervisor_id)
) ENGINE=InnoDB;


CREATE TABLE Especialidad (
  codigo INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL UNIQUE,
  descripcion TEXT,
  
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;


CREATE TABLE RegistroSup (
  codigo INT AUTO_INCREMENT PRIMARY KEY,
  fecha DATE NOT NULL,
  observaciones TEXT,
  supervisor_id INT NOT NULL,
  mecanico_id INT NOT NULL,
  
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  
  INDEX idx_fecha (fecha),
  INDEX idx_supervisor (supervisor_id),
  INDEX idx_mecanico (mecanico_id)
) ENGINE=InnoDB;


CREATE TABLE FacturaImpuesto (
  factura_id INT NOT NULL,
  impuesto_codigo INT NOT NULL,
  monto_aplicado DECIMAL(10,2) DEFAULT 0.00,
  PRIMARY KEY (factura_id, impuesto_codigo),
  INDEX idx_factura (factura_id),
  INDEX idx_impuesto (impuesto_codigo)
) ENGINE=InnoDB;


CREATE TABLE OrdenServicio (
  orden_codigo INT NOT NULL,
  servicio_codigo INT NOT NULL,
  cantidad INT DEFAULT 1,
  precio_aplicado DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (orden_codigo, servicio_codigo),
  INDEX idx_orden (orden_codigo),
  INDEX idx_servicio (servicio_codigo)
) ENGINE=InnoDB;


CREATE TABLE OrdenRepuesto (
  orden_codigo INT NOT NULL,
  repuesto_codigo INT NOT NULL,
  cantidad_usada INT NOT NULL DEFAULT 1,
  precio_aplicado DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (orden_codigo, repuesto_codigo),
  INDEX idx_orden (orden_codigo),
  INDEX idx_repuesto (repuesto_codigo)
) ENGINE=InnoDB;


CREATE TABLE RepuestoProveedor (
  repuesto_codigo INT NOT NULL,
  proveedor_id INT NOT NULL,
  precio_compra DECIMAL(10,2) NOT NULL,
  tiempo_entrega_dias INT DEFAULT 7,
  PRIMARY KEY (repuesto_codigo, proveedor_id),
  INDEX idx_repuesto (repuesto_codigo),
  INDEX idx_proveedor (proveedor_id)
) ENGINE=InnoDB;


CREATE TABLE OrdenMecanico (
  mecanico_id INT NOT NULL,
  orden_codigo INT NOT NULL,
  rol VARCHAR(50) DEFAULT 'ASIGNADO',
  horas_trabajadas DECIMAL(5,2) DEFAULT 0.00,
  PRIMARY KEY (mecanico_id, orden_codigo),
  INDEX idx_mecanico (mecanico_id),
  INDEX idx_orden (orden_codigo)
) ENGINE=InnoDB;


CREATE TABLE MecanicoEspecialidad (
  mecanico_id INT NOT NULL,
  especialidad_codigo INT NOT NULL,
  nivel VARCHAR(50) DEFAULT 'BASICO',
  fecha_certificacion DATE,
  PRIMARY KEY (mecanico_id, especialidad_codigo),
  INDEX idx_mecanico (mecanico_id),
  INDEX idx_especialidad (especialidad_codigo)
) ENGINE=InnoDB;


ALTER TABLE Vehiculo 
ADD CONSTRAINT fk_vehiculo_cliente 
FOREIGN KEY (documento_cliente) 
REFERENCES Cliente(id_cliente) 
ON DELETE RESTRICT 
ON UPDATE CASCADE;


ALTER TABLE OrdenTrabajo 
ADD CONSTRAINT fk_orden_vehiculo 
FOREIGN KEY (placa) 
REFERENCES Vehiculo(placa) 
ON DELETE RESTRICT 
ON UPDATE CASCADE;


ALTER TABLE Factura 
ADD CONSTRAINT fk_factura_orden 
FOREIGN KEY (orden_codigo) 
REFERENCES OrdenTrabajo(codigo) 
ON DELETE RESTRICT 
ON UPDATE CASCADE;


ALTER TABLE Mecanico 
ADD CONSTRAINT fk_mecanico_supervisor 
FOREIGN KEY (supervisor_id) 
REFERENCES Mecanico(id_mecanico) 
ON DELETE SET NULL 
ON UPDATE CASCADE;


ALTER TABLE RegistroSup 
ADD CONSTRAINT fk_registro_supervisor 
FOREIGN KEY (supervisor_id) 
REFERENCES Mecanico(id_mecanico) 
ON DELETE RESTRICT 
ON UPDATE CASCADE;


ALTER TABLE RegistroSup 
ADD CONSTRAINT fk_registro_mecanico 
FOREIGN KEY (mecanico_id) 
REFERENCES Mecanico(id_mecanico) 
ON DELETE RESTRICT 
ON UPDATE CASCADE;


ALTER TABLE FacturaImpuesto 
ADD CONSTRAINT fk_factura_impuesto_factura 
FOREIGN KEY (factura_id) 
REFERENCES Factura(id_factura) 
ON DELETE CASCADE 
ON UPDATE CASCADE;


ALTER TABLE FacturaImpuesto 
ADD CONSTRAINT fk_factura_impuesto_impuesto 
FOREIGN KEY (impuesto_codigo) 
REFERENCES Impuesto(codigo) 
ON DELETE RESTRICT 
ON UPDATE CASCADE;


ALTER TABLE OrdenServicio 
ADD CONSTRAINT fk_orden_servicio_orden 
FOREIGN KEY (orden_codigo) 
REFERENCES OrdenTrabajo(codigo) 
ON DELETE CASCADE 
ON UPDATE CASCADE;

ALTER TABLE OrdenServicio 
ADD CONSTRAINT fk_orden_servicio_servicio 
FOREIGN KEY (servicio_codigo) 
REFERENCES Servicio(codigo) 
ON DELETE RESTRICT 
ON UPDATE CASCADE;


ALTER TABLE OrdenRepuesto 
ADD CONSTRAINT fk_orden_repuesto_orden 
FOREIGN KEY (orden_codigo) 
REFERENCES OrdenTrabajo(codigo) 
ON DELETE CASCADE 
ON UPDATE CASCADE;

ALTER TABLE OrdenRepuesto 
ADD CONSTRAINT fk_orden_repuesto_repuesto 
FOREIGN KEY (repuesto_codigo) 
REFERENCES Repuesto(codigo) 
ON DELETE RESTRICT 
ON UPDATE CASCADE;


ALTER TABLE RepuestoProveedor 
ADD CONSTRAINT fk_repuesto_proveedor_repuesto 
FOREIGN KEY (repuesto_codigo) 
REFERENCES Repuesto(codigo) 
ON DELETE CASCADE 
ON UPDATE CASCADE;

ALTER TABLE RepuestoProveedor 
ADD CONSTRAINT fk_repuesto_proveedor_proveedor 
FOREIGN KEY (proveedor_id) 
REFERENCES Proveedor(id) 
ON DELETE RESTRICT 
ON UPDATE CASCADE;


ALTER TABLE OrdenMecanico 
ADD CONSTRAINT fk_orden_mecanico_mecanico 
FOREIGN KEY (mecanico_id) 
REFERENCES Mecanico(id_mecanico) 
ON DELETE RESTRICT 
ON UPDATE CASCADE;

ALTER TABLE OrdenMecanico 
ADD CONSTRAINT fk_orden_mecanico_orden 
FOREIGN KEY (orden_codigo) 
REFERENCES OrdenTrabajo(codigo) 
ON DELETE CASCADE 
ON UPDATE CASCADE;


ALTER TABLE MecanicoEspecialidad 
ADD CONSTRAINT fk_mecanico_especialidad_mecanico 
FOREIGN KEY (mecanico_id) 
REFERENCES Mecanico(id_mecanico) 
ON DELETE CASCADE 
ON UPDATE CASCADE;

ALTER TABLE MecanicoEspecialidad 
ADD CONSTRAINT fk_mecanico_especialidad_especialidad 
FOREIGN KEY (especialidad_codigo) 
REFERENCES Especialidad(codigo) 
ON DELETE RESTRICT 
ON UPDATE CASCADE;


INSERT INTO Cliente (id_cliente, nombre, apellido, telefono, email, tipo) VALUES
('12345678', 'David', 'Posso', '3042999265', 'david.posso@email.com', 'INDIVIDUAL'),
('87654321', 'Daniela', 'Arboleda', '3001234567', 'daniela.arboleda@email.com', 'INDIVIDUAL'),
('900123456', 'Transportes UQ S.A.', NULL, '3011234567', 'contacto@transportesuq.com', 'CORPORATIVO');


INSERT INTO Vehiculo (placa, tipo, marca, modelo, anio, documento_cliente) VALUES
('ABC123', 'AUTOMOVIL', 'Toyota', 'Corolla', 2020, '12345678'),
('XYZ789', 'CAMIONETA', 'Ford', 'Ranger', 2019, '87654321'),
('DEF456', 'CAMION', 'Chevrolet', 'NPR', 2018, '900123456');


INSERT INTO Especialidad (nombre, descripcion) VALUES
('Motor', 'Reparación y mantenimiento de motores'),
('Transmisión', 'Sistemas de transmisión manual y automática'),
('Frenos', 'Sistemas de frenado'),
('Electricidad', 'Sistemas eléctricos y electrónicos'),
('Suspensión', 'Sistemas de suspensión y dirección');


INSERT INTO Mecanico (nombre, telefono, supervisor_id) VALUES
('Cristhian Osorio', '555-3001', NULL),
('Camilo Osorio', '555-3002', 1),
('Juan Dios', '555-3003', 1);


INSERT INTO Servicio (nombre, descripcion, categoria, precio) VALUES
('Cambio de aceite', 'Cambio de aceite y filtro', 'MANTENIMIENTO', 50.00),
('Revisión de frenos', 'Inspección completa del sistema de frenos', 'MANTENIMIENTO', 80.00),
('Alineación y balanceo', 'Alineación y balanceo de neumáticos', 'MANTENIMIENTO', 60.00),
('Diagnóstico computarizado', 'Escaneo completo del vehículo', 'DIAGNOSTICO', 40.00);


INSERT INTO Repuesto (nombre, descripcion, stock, precio_unitario) VALUES
('Filtro de aceite', 'Filtro de aceite estándar', 50, 8.50),
('Pastillas de freno delanteras', 'Juego de pastillas delanteras', 30, 45.00),
('Aceite sintético 5W-30', 'Aceite sintético por litro', 100, 12.00),
('Bujías', 'Bujías de encendido', 40, 15.00);


INSERT INTO Impuesto (nombre, porcentaje) VALUES
('IVA', 19.00),
('Impuesto al consumo', 8.00);


INSERT INTO Proveedor (nombre, correo, telefono) VALUES
('Repuestos Globales', 'ventas@repuestosglobales.com', '555-4001'),
('AutoPartes S.A.', 'info@autopartes.com', '555-4002');