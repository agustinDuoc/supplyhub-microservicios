# SupplyHub Microservicios

Proyecto de microservicios para la gestión de una cadena de suministro.

Integrantes:

- Agustín Pérez
- Nicolás Pérez

## Puertos principales

| Servicio | Puerto |
|---|---:|
| Eureka | 8762 externo / 8761 interno |
| Gateway | 8099 |
| Auth | 8085 |
| Clientes | 8086 |
| Proveedores | 8087 |
| Categorías | 8088 |
| Productos | 8089 |
| Cotizaciones | 8090 |
| Inventario | 8091 |
| Órdenes de compra | 8192 |
| Pagos | 8093 |
| Despachos | 8094 |

Nota: `ms-ordenes-compra` corre internamente en el puerto `8092`, pero en Docker se expone como `8192` para evitar conflictos con otros servicios locales.

## URLs para revisar

- Eureka: http://localhost:8762
- Gateway: http://localhost:8099
- Swagger Auth: http://localhost:8085/swagger-ui.html
- Swagger Órdenes de compra: http://localhost:8192/swagger-ui.html

El resto de Swagger queda disponible en el puerto de cada microservicio usando `/swagger-ui.html`.

## Rutas por Gateway

Las rutas principales pasan por:

```text
http://localhost:8099
```

## Pruebas

Para ejecutar pruebas de un microservicio:

```bash
cd ms-productos
./mvnw clean test
```

En Windows:

```powershell
cd ms-productos
.\mvnw.cmd clean test
```
