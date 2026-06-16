CREATE TABLE categoria (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    estado VARCHAR(20) NOT NULL
);

INSERT INTO categoria (nombre, descripcion, estado)
VALUES
('Seguridad', 'Productos de seguridad industrial', 'ACTIVO'),
('Herramientas', 'Herramientas manuales y eléctricas', 'ACTIVO'),
('Repuestos', 'Piezas y repuestos industriales', 'ACTIVO');