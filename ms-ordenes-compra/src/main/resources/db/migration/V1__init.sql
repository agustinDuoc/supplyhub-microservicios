CREATE TABLE orden_compra (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_cliente BIGINT NOT NULL,
    id_inventario BIGINT NOT NULL,
    cantidad INT NOT NULL,
    total INT NOT NULL,
    estado VARCHAR(20) NOT NULL,
    fecha_orden DATE NOT NULL
);