CREATE TABLE pago (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_orden_compra BIGINT NOT NULL,
    monto INT NOT NULL,
    metodo_pago VARCHAR(50) NOT NULL,
    estado_pago VARCHAR(20) NOT NULL,
    fecha_pago DATE NOT NULL
);