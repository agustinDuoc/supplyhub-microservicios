CREATE TABLE cliente (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rut_empresa VARCHAR(20) NOT NULL UNIQUE,
    razon_social VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    direccion VARCHAR(255),
    estado VARCHAR(20) NOT NULL
);

INSERT INTO cliente (rut_empresa, razon_social, email, telefono, direccion, estado)
VALUES
('76543210-1', 'Constructora Andes SpA', 'contacto@andes.cl', '912345678', 'Av. Providencia 1200', 'ACTIVO'),
('76111222-3', 'Metalúrgica Pacífico Ltda', 'ventas@pacifico.cl', '987654321', 'Camino Industrial 450', 'ACTIVO');