# SupplyHub Microservicios

Backend desarrollado con arquitectura de microservicios.

## Microservicios y puertos

- ms-auth → 8580
- ms-usuarios → 8581
- ms-clientes → 8582
- ms-proveedores → 8583
- ms-categorias → 8584
- ms-productos → 8585
- ms-inventario → 8586
- ms-cotizaciones → 8587
- ms-ordenes-compra → 8588
- ms-pagos → 8589
- ms-despachos → 8590

## Ejecución

1. Crear las bases de datos.
2. Ejecutar primero `ms-auth`.
3. Levantar los demás microservicios.
4. Probar endpoints con Postman usando JWT.
