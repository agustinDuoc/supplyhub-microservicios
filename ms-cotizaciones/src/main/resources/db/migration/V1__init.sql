CREATE TABLE cotizacion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_producto BIGINT NOT NULL,
    cantidad INT NOT NULL,
    total INT NOT NULL,
    estado VARCHAR(20) NOT NULL
);