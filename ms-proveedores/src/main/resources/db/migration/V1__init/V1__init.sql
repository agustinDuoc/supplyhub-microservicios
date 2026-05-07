CREATE TABLE proveedor (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rut_proveedor VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    direccion VARCHAR(255),
    estado VARCHAR(20) NOT NULL
);

INSERT INTO proveedor (rut_proveedor, nombre, email, telefono, direccion, estado)
VALUES
('76123456-7', 'Industrial Norte SpA', 'contacto@industrialnorte.cl', '912345678', 'Av. Industrial 123', 'ACTIVO'),
('76987654-3', 'Seguridad Total Ltda', 'ventas@seguridadtotal.cl', '987654321', 'Calle Seguridad 456', 'ACTIVO');