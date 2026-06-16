CREATE TABLE producto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    precio INT NOT NULL,
    id_categoria BIGINT NOT NULL,
    id_proveedor BIGINT NOT NULL,
    estado VARCHAR(20) NOT NULL
);

INSERT INTO producto (nombre, descripcion, precio, id_categoria, id_proveedor, estado)
VALUES
('Casco industrial', 'Casco de seguridad certificado', 12990, 1, 1, 'ACTIVO'),
('Guantes anticorte', 'Guantes de protección nivel industrial', 8990, 1, 1, 'ACTIVO');