# SupplyHub Microservicios

Proyecto de microservicios para la gestión de una cadena de suministro.

Integrantes:

- Agustín Pérez
- Nicolás Pérez

## Cómo levantar el proyecto

Requisitos:

- Java 17
- Docker Desktop
- Maven Wrapper incluido en cada microservicio

Desde la carpeta raíz:

```bash
docker compose up --build
```

Para detenerlo:

```bash
docker compose down
```

## Puertos principales

| Servicio | Puerto |
|---|---:|
| Eureka | 8761 |
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

- Eureka: http://localhost:8761
- Gateway: http://localhost:8099
- Swagger Auth: http://localhost:8085/swagger-ui.html
- Swagger Órdenes de compra: http://localhost:8192/swagger-ui.html

El resto de Swagger queda disponible en el puerto de cada microservicio usando `/swagger-ui.html`.

## Rutas por Gateway

Las rutas principales pasan por:

```text
http://localhost:8099
```

Ejemplos:

```text
/auth/**
/api/v1/clientes/**
/api/v1/productos/**
/api/v1/ordenes-compra/**
/api/v1/pagos/**
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

Las pruebas usan configuración de test con H2, por lo que no dependen de MySQL real.
