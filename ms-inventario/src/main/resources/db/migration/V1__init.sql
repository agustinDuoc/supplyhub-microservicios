CREATE TABLE inventario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_producto BIGINT NOT NULL,
    stock_disponible INT NOT NULL,
    stock_minimo INT NOT NULL,
    ubicacion VARCHAR(100),
    estado VARCHAR(20) NOT NULL
);

INSERT INTO inventario (id_producto, stock_disponible, stock_minimo, ubicacion, estado)
VALUES
(1, 50, 10, 'Bodega A', 'ACTIVO');