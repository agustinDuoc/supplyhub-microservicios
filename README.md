# SupplyHub — Arquitectura de Microservicios

Sistema de gestión de cadena de suministro basado en microservicios con Spring Boot, Spring Cloud Gateway y Eureka Service Discovery.

## Integrantes del equipo

> Completar con nombres reales del equipo

- Nombre Apellido 1
- Nombre Apellido 2

## Microservicios implementados

| Servicio | Puerto local | Descripción |
|---|---|---|
| ms-eureka | 8761 | Service Discovery (Eureka Server) |
| ms-gateway | 8099 | API Gateway (Spring Cloud Gateway) |
| ms-auth | 8085 | Autenticación y generación de JWT |
| ms-clientes | 8086 | Gestión de clientes empresariales |
| ms-proveedores | 8087 | Gestión de proveedores |
| ms-categorias | 8088 | Gestión de categorías de productos |
| ms-productos | 8089 | Catálogo de productos |
| ms-cotizaciones | 8090 | Cotizaciones de compra |
| ms-inventario | 8091 | Control de inventario |
| ms-ordenes-compra | 8092 | Órdenes de compra |
| ms-pagos | 8093 | Procesamiento de pagos |
| ms-despachos | 8094 | Gestión de despachos y envíos |

## Rutas principales del API Gateway

Todas las rutas pasan por el Gateway en `http://localhost:8099`.

| Prefijo | Servicio destino |
|---|---|
| `/auth/**` | ms-auth |
| `/api/v1/clientes/**` | ms-clientes |
| `/api/v1/proveedores/**` | ms-proveedores |
| `/api/v1/categorias/**` | ms-categorias |
| `/api/v1/productos/**` | ms-productos |
| `/api/v1/cotizaciones/**` | ms-cotizaciones |
| `/api/v1/inventario/**` | ms-inventario |
| `/api/v1/ordenes-compra/**` | ms-ordenes-compra |
| `/api/v1/pagos/**` | ms-pagos |
| `/api/v1/despachos/**` | ms-despachos |

## Documentación Swagger/OpenAPI

Con los servicios corriendo localmente, acceder a Swagger UI en cada servicio:

| Servicio | URL Swagger |
|---|---|
| ms-auth | http://localhost:8085/swagger-ui.html |
| ms-clientes | http://localhost:8086/swagger-ui.html |
| ms-proveedores | http://localhost:8087/swagger-ui.html |
| ms-categorias | http://localhost:8088/swagger-ui.html |
| ms-productos | http://localhost:8089/swagger-ui.html |
| ms-cotizaciones | http://localhost:8090/swagger-ui.html |
| ms-inventario | http://localhost:8091/swagger-ui.html |
| ms-ordenes-compra | http://localhost:8092/swagger-ui.html |
| ms-pagos | http://localhost:8093/swagger-ui.html |
| ms-despachos | http://localhost:8094/swagger-ui.html |

## Instrucciones de ejecución local

### Opción 1: Docker Compose (recomendado)

Requiere Docker Desktop instalado.

```bash
# Desde la raíz del proyecto
docker-compose up --build
```

Esto levanta todos los servicios y sus bases de datos MySQL.

### Opción 2: Ejecución manual desde IDE

1. Levantar primero **ms-eureka** (puerto 8761)
2. Levantar **ms-auth** (requiere MySQL en puerto 3310)
3. Levantar el resto de microservicios en cualquier orden
4. Levantar **ms-gateway** al final (puerto 8099)

Cada servicio usa H2 en memoria para tests (no requiere MySQL para pruebas).

### Ejecutar pruebas unitarias

```bash
# Desde la carpeta de cada microservicio
./mvnw clean test

# El reporte de cobertura JaCoCo se genera en:
# target/site/jacoco/index.html
```

## Tecnologías utilizadas

- Java 17
- Spring Boot 3.x
- Spring Cloud Gateway (API Gateway)
- Netflix Eureka (Service Discovery)
- Spring Data JPA + Flyway (Persistencia)
- MySQL 8 (Base de datos por servicio)
- H2 (Base de datos en memoria para tests)
- JWT (Autenticación)
- Springdoc OpenAPI / Swagger UI (Documentación)
- Docker + Docker Compose (Despliegue)
- JUnit 5 + Mockito (Pruebas unitarias)
- JaCoCo (Cobertura de código)
