CREATE TABLE despacho (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_orden_compra BIGINT NOT NULL,
    id_pago BIGINT NOT NULL,
    direccion_envio VARCHAR(255) NOT NULL,
    estado_despacho VARCHAR(50) NOT NULL,
    fecha_envio DATE NOT NULL,
    fecha_entrega DATE
);