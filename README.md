# 🐾 TFI Programación II – Gestión de Mascotas-Microchips

## Trabajo Práctico Integrador – Programación II  
**Aplicación Java con relación 1→1 unidireccional + DAO + MySQL + Transacciones ACID**

![alt text](uml/logo.png)

---

# 📑 Índice

- [🐾 TFI Programación II – Gestión de Mascotas-Microchips](#-tfi-programacion-ii--gestion-de-mascotas-microchips)
  - [Trabajo Práctico Integrador – Programación II](#trabajo-práctico-integrador--programacion-ii)
- [📑 Índice](#-indice)
- [1. Descripción del Proyecto](#1-descripcion-del-proyecto)
- [2. Dominio Seleccionado y Justificación](#2-dominio-seleccionado-y-justificacion)
    - [Dominio elegido](#dominio-elegido)
    - [Justificación funcional](#justificacion-funcional)
    - [Justificación técnica](#justificacion-tecnica)
- [3. Objetivos Académicos](#3-objetivos-academicos)
    - [Arquitectura en capas](#arquitectura-en-capas)
    - [Programación Orientada a Objetos](#programacion-orientada-a-objetos)
    - [Persistencia con JDBC](#persistencia-con-jdbc)
    - [Transacciones ACID](#transacciones-acid)
    - [Validaciones y reglas de negocio](#validaciones-y-reglas-de-negocio)
- [4. Arquitectura del Sistema](#4-arquitectura-del-sistema)
  - [4.1 Paquetes y Responsabilidades](#41-paquetes-y-responsabilidades)
  - [4.2 Diagrama UML (Referencia)](#42-diagrama-uml-referencia)
- [5. Modelo de Datos](#5-modelo-de-datos)
  - [5.1 Estructura de la Base y Restricciones](#51-estructura-de-la-base-y-restricciones)
    - [Tabla `mascotas`](#tabla-mascotas)
    - [Tabla `microchips`](#tabla-microchips)
  - [5.2 Sentencias SQL (Estructura Completa)](#52-sentencias-sql-estructura-completa)
- [6. Transacciones ACID y Lógica de Negocio](#6-transacciones-acid-y-lógica-de-negocio)
    - [Caso transaccional principal: Crear Mascota + Microchip](#caso-transaccional-principal-crear-mascota--microchip)
    - [Ejemplo de fallo simulado](#ejemplo-de-fallo-simulado)
- [7. Descripción de la Aplicación y Funcionalidades](#7-descripcion-de-la-aplicacion-y-funcionalidades)
    - [Menú Mascotas](#menu-mascotas)
    - [Menú Microchips](#menu-microchips)
    - [Menú Transacciones](#menu-transacciones)
    - [Funcionalidades Clave del Menu Mascota](#funcionalidades-clave-del-menu-mascota)
    - [Funcionalidades Clave del Menu Microchip](#funcionalidades-clave-del-menu-microchip)
    - [Funcionalidades Clave del Menu Transacciones](#funcionalidades-clave-del-menu-transacciones)
- [8. Pruebas Realizadas](#8-pruebas-realizadas)
- [9. Requisitos del Sistema, Instalación y Ejecución](#9-requisitos-del-sistema-instalacion-y-ejecucion)
  - [Requisitos](#requisitos)
  - [Instalación de la Base de Datos](#instalacion-de-la-base-de-datos)
  - [Ejecución del Proyecto](#ejecucion-del-proyecto)
    - [Desde un IDE (NetBeans)](#desde-un-ide-netbeans)
    - [Desde línea de comandos (ejemplo general)](#desde-linea-de-comandos-ejemplo-general)
- [10. Conclusiones y Mejoras Futuras](#10-conclusiones-y-mejoras-futuras)
    - [Conclusiones](#conclusiones)
    - [Mejoras Futuras](#mejoras-futuras)
- [11. Video de Presentación](#11-video-de-presentacion)

---

<a id="descripcion-del-proyecto"></a>
# 1. Descripción del Proyecto

Este Trabajo Práctico Integrador desarrolla un sistema completo de gestión para **Mascotas** y **Microchips**, utilizando:

- **Java (JDK 21 recomendado)**  
- **JDBC (sin ORM)**  
- **MySQL**  
- **Patrón DAO + capa Service**  
- **Menú por consola**  
- **Transacciones ACID con commit / rollback**  
- **Relación unidireccional 1→1**  

El objetivo es demostrar dominio de Programación Orientada a Objetos, persistencia con JDBC, diseño en capas, validaciones, manejo de excepciones y modelado de relaciones 1→1, tal como se especifica en las consignas del TFI de Programación II.

---

<a id="dominio-seleccionado-y-justificacion"></a>
# 2. Dominio Seleccionado y Justificación

### Dominio elegido

Se seleccionó el dominio **Mascota → Microchip**, donde cada mascota puede tener asociado exactamente un microchip identificatorio.

### Justificación funcional

- Representa un caso realista de la vida cotidiana (veterinarias, refugios, registros municipales).  
- La relación es naturalmente 1→1: una mascota tiene un microchip principal asociado.  
- Permite aplicar validaciones de unicidad, integridad referencial y vistas combinadas.  

### Justificación técnica

- Se garantiza 1→1 mediante una **clave foránea única** en la tabla `microchips`:
  - `microchips.mascota_id` es `UNIQUE` y `NOT NULL`.  
- La entidad **Mascota** actúa como entidad principal (A).  
- La entidad **Microchip** actúa como entidad dependiente (B), con FK hacia `mascotas.id`.  
- Se utiliza una vista (`vista_mascotas_con_microchip`) para consultar ambas entidades de forma conjunta.  

Este diseño refleja la relación unidireccional 1→1 solicitada en el TFI, aplicando la estrategia recomendada de **FK única en la tabla B**.

---

<a id="objetivos-academicos"></a>
# 3. Objetivos Académicos

El proyecto permite aplicar y consolidar los siguientes conceptos fundamentales de la materia:

### Arquitectura en capas

- Separación clara de responsabilidades:  
  - `config/` → configuración de base de datos y manejo de transacciones  
  - `dao/` → acceso a datos con JDBC y patrón DAO  
  - `service/` → lógica de negocio, validaciones y orquestación de transacciones  
  - `entities/` → modelo de dominio (Mascota, Microchip, Base)  
  - `main/` → menú de consola y flujo de interacción con el usuario  

### Programación Orientada a Objetos

- Uso de una clase base (`Base`) con atributos comunes (`id`, `eliminado`).  
- Encapsulamiento de atributos con getters y setters.  
- Interfaces genéricas `GenericDAO<T>` y `GenericService<T>` para aplicar polimorfismo.  
- Aplicación de responsabilidades únicas por clase (SRP) y separación de capas.

### Persistencia con JDBC

- Conexión a MySQL mediante la clase `DatabaseConnection`.  
- Uso de `PreparedStatement` en todas las operaciones para prevenir SQL injection.  
- Manejo de recursos con try-with-resources.  
- Implementación de CRUD completo para Mascota y Microchip.  

### Transacciones ACID

- Implementación de un caso transaccional: **crear Mascota + Microchip** dentro de una misma transacción.  
- Uso de `setAutoCommit(false)`, `commit()` y `rollback()` encapsulados en `TransactionManager`.  
- Demostración de rollback ante un fallo simulado (por ejemplo, insertar un microchip con código duplicado).  

### Validaciones y reglas de negocio

- Validación de campos obligatorios en la capa de servicio.  
- Control de la relación 1→1 (un único microchip por mascota).  
- Validación de IDs positivos y manejo de entidades inexistentes.  

---

<a id="arquitectura-del-sistema"></a>
# 4. Arquitectura del Sistema

<a id="paquetes-y-responsabilidades"></a>
## 4.1 Paquetes y Responsabilidades

```text
src/main/java/
├── config/
│   ├── DatabaseConnection.java      # Conexión a MySQL (JDBC)
│   └── TransactionManager.java      # Manejo de transacciones (ACID)
├── entities/
│   ├── Base.java                    # Clase base con id y eliminado
│   ├── Mascota.java                 # Entidad principal (A)
│   └── Microchip.java               # Entidad dependiente (B)
├── dao/
│   ├── GenericDAO.java              # Interface genérica CRUD
│   ├── MascotaDAO.java              # Implementación DAO para Mascota
│   └── MicrochipDAO.java            # Implementación DAO para Microchip
├── service/
│   ├── MascotaServiceImpl.java      # Lógica de negocio para Mascota
│   └── MicrochipServiceImpl.java    # Lógica de negocio para Microchip
└── main/
    ├── Main.java                    # Punto de entrada
    ├── MenuDisplay.java             # Presentación de menús por consola
    └── MenuHandler.java             # Manejo de interacciones y llamadas a servicios
```

- La capa **config** abstrae la configuración de la conexión y el control transaccional.  
- La capa **dao** interactúa directamente con la base de datos mediante JDBC.  
- La capa **service** implementa reglas de negocio y coordina operaciones entre múltiples DAOs.  
- La capa **main** maneja la interacción por consola con el usuario.

<a id="diagrama-uml-referencia"></a>
## 4.2 Diagrama UML (Referencia)

> 💡 En el repositorio se incluye el diagrama UML en formato imagen (`uml/Diagrama UML Mascota-Microchip.pdf`).  
> Aquí se muestra una representación simplificada:

```text
+------------------+        1      1     +-----------------+
|     Mascota      |-------------------->|    Microchip    |
+------------------+                     +-----------------+
| - id: Long       |                     | - id: Long      |
| - eliminado: Bool|                     | - eliminado:Bool|
| - nombre: String |                     | - codigo: String|
| - especie: String|                     | - fechaImpl:LocalDate |
| - raza: String   |                     | - veterinaria:String   |
| - fechaNac:LocalDate                  | - observaciones:String|
| - duenio: String |                     | - mascota_id: Long    |
+------------------+                     +-----------------+
```

La relación 1→1 se garantiza a nivel de base de datos con una **FK única** en `microchips.mascota_id`.

---

<a id="modelo-de-datos"></a>
# 5. Modelo de Datos

<a id="estructura-de-la-base-y-restricciones"></a>
## 5.1 Estructura de la Base y Restricciones

Base de datos: **db_mascotas**

### Tabla `mascotas`

| Campo            | Tipo        | Restricción                     |
|------------------|------------|---------------------------------|
| id               | BIGINT     | PK, AUTO_INCREMENT              |
| eliminado        | BOOLEAN    | NOT NULL, DEFAULT FALSE         |
| nombre           | VARCHAR(60)| NOT NULL                        |
| especie          | VARCHAR(30)| NOT NULL                        |
| raza             | VARCHAR(60)| Nullable (opcional)             |
| fecha_nacimiento | DATE       | Nullable (opcional)             |
| duenio           | VARCHAR(120)| NOT NULL                       |

### Tabla `microchips`

| Campo            | Tipo         | Restricción                                        |
|------------------|-------------|----------------------------------------------------|
| id               | BIGINT      | PK, AUTO_INCREMENT                                 |
| eliminado        | BOOLEAN     | NOT NULL, DEFAULT FALSE                            |
| codigo           | VARCHAR(25) | NOT NULL, UNIQUE                                   |
| fecha_implantacion | DATE      | Nullable (opcional)                                |
| veterinaria      | VARCHAR(120)| Nullable (opcional)                                |
| observaciones    | VARCHAR(255)| Nullable (opcional)                                |
| mascota_id       | BIGINT      | NOT NULL, UNIQUE, FK → mascotas(id), ON DELETE CASCADE |

La relación 1→1 se garantiza por:
- `mascota_id` con `UNIQUE`  
- Restricción de clave foránea hacia `mascotas.id`  

<a id="sentencias-sql-estructura-completa"></a>
## 5.2 Sentencias SQL (Estructura Completa)

Estas sentencias se encuentran en el archivo `01_estructura.sql`:

```sql
CREATE DATABASE IF NOT EXISTS db_mascotas
    CHARACTER SET utf8
    COLLATE utf8_spanish_ci;

USE db_mascotas;

CREATE TABLE mascotas (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    eliminado BOOLEAN NOT NULL DEFAULT FALSE,
    nombre VARCHAR(60) NOT NULL,
    especie VARCHAR(30) NOT NULL,
    raza VARCHAR(60),
    fecha_nacimiento DATE,
    duenio VARCHAR(120) NOT NULL
);

CREATE TABLE microchips (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    eliminado BOOLEAN NOT NULL DEFAULT FALSE, 
    codigo VARCHAR(25) NOT NULL UNIQUE,
    fecha_implantacion DATE,
    veterinaria VARCHAR(120),
    observaciones VARCHAR(255),
    mascota_id BIGINT NOT NULL UNIQUE, 
    FOREIGN KEY (mascota_id) 
        REFERENCES mascotas(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE VIEW vista_mascotas_con_microchip AS
SELECT 
    m.id AS id_mascota,
    m.nombre,
    m.especie,
    m.raza,
    m.duenio,
    c.id AS id_microchip,
    c.codigo,
    c.veterinaria
FROM mascotas m
LEFT JOIN microchips c ON m.id = c.mascota_id
WHERE m.eliminado = false;
```

---

<a id="transacciones-acid-y-logica-de-negocio"></a>
# 6. Transacciones ACID y Lógica de Negocio

El sistema implementa transacciones para garantizar las propiedades ACID en operaciones compuestas:

### Caso transaccional principal: Crear Mascota + Microchip

1. Se inicia una transacción (`setAutoCommit(false)`).  
2. Se inserta una nueva mascota en la tabla `mascotas`.  
3. Se recupera el ID generado (`LAST_INSERT_ID()`).  
4. Se inserta un microchip asociado a esa mascota en la tabla `microchips`.  
5. Si ambas operaciones se completan correctamente → `commit()`.  
6. Si ocurre cualquier error (por ejemplo, código de microchip duplicado) → `rollback()`.  

Este flujo se implementa en la capa de servicio utilizando `TransactionManager`, que se encarga de:

- Recibir una `Connection` compartida.  
- Desactivar el autocommit.  
- Ejecutar las operaciones necesarias en `MascotaDAO` y `MicrochipDAO`.  
- Hacer `commit()` o `rollback()` según corresponda.  
- Restaurar el autocommit y cerrar recursos en un bloque finally.

### Ejemplo de fallo simulado

Para demostrar el rollback, se intenta crear una mascota con un microchip cuyo `codigo` ya existe en la base. La restricción `UNIQUE` provoca una excepción SQL y la transacción se revierte por completo.

---

<a id="descripcion-de-la-aplicacion-y-funcionalidades"></a>
# 7. Descripción de la Aplicación y Funcionalidades

La aplicación es una app de consola que permite gestionar:

- **Mascotas** (entidad principal)  
- **Microchips** (entidad dependiente, 1→1)  

Mediante un menú interactivo, se exponen las operaciones CRUD para ambas entidades y funcionalidades adicionales relacionadas con la transacción.

### Menú Principal

```text
------ MENU PRINCIPAL ------ 
[1] Gestionar MASCOTAS
[2] Gestionar MICROCHIPS
[3] Mostrar mascotas con microchips (vista)
[4] Transacciones
[0] Salir
Seleccione una opción:
```

### Menú Mascotas

```text
 ------ GESTION DE MASCOTAS ------ 
[1] Crear mascota
[2] Listar mascota
[3] Buscar mascota por ID
[4] Actualizar mascota
[5] Eliminar mascota
[6] Buscar mascota por nombre
[0] Volver al menu principal
------ --------------------- ------ 
Seleccione una opción:
```

### Menú Microchips

```text
 ------ GESTION DE MICROCHIPS ------ 
[1] Crear microchip
[2] Listar microchips
[3] Buscar microchip por ID
[4] Actualizar microchip
[5] Eliminar microchip
[6] Buscar microchip por código
[0] Volver al menu principal
------ --------------------- ------ 
Seleccione una opción:
```

### Menú Transacciones

```text
------ TRANSACCIONES ------ 
[1] Crear mascota y microchip (Transacción ACID)
[2] Actualizar mascota y microchip
[3] Eliminar mascota y microchip asociado
[0] Volver al menu principal
------ --------------------- ------
```

### Funcionalidades Clave del Menu Mascota

- **Crear Mascota**: Solicita datos obligatorios y opcionales, valida y persiste (opcionalmente permite agregar Microchip).
- **Listar Mascota**: Muestra todas las mascotas activas (eliminado = false).
- **Buscar mascota por ID**: Solicita al usuario un ID y lo muestra. 
- **Actualizar Mascota**: Permite modificar campos, manteniendo los valores anteriores si se deja en blanco.
- **Eliminar Mascota (mediante baja lógica)**: Marca eliminado = true en lugar de borrar físicamente. 
- **Buscar mascota por nombre**: Solicita al usuario un nombre y busca coincidentes totales o parciales.

### Funcionalidades Clave del Menu Microchip

- **Crear Microchip**: Vincula un microchip a una mascota existente, respetando la relación 1→1.
- **Listar Microchips**: Lista todos los microchips activos.
- **Buscar microchip por ID**: Solicita al usuario un ID y muestra el microchip (si existe).
- **Actualizar Microchip**: Permite modificar campos, manteniendo los valores anteriores si se deja en blanco. 
- **Eliminar microchip (mediante baja lógica)**: Marca eliminado = true en lugar de borrar físicamente. - **Buscar microchip por código**: Solicita al usuario un codigo y busca coincidentes totales o parciales. 

### Funcionalidades Clave del Menu Transacciones

- **Crear mascota y microchip (Transacción ACID)**: Crea ambos registros en una única operación ACID.
- **Actualizar mascota y microchip**: Permite actualizar una mascota y su microchip en una única operación ACID (mejora a futuro - pendiente, no obligatorio para el tp).
- **Eliminar mascota y microchip asociado**: Permite eliminar una mascota y su microchip en una única operación ACID (mejora a futuro - pendiente, no obligatorio para el tp). 

---

<a id="pruebas-realizadas"></a>
# 8. Pruebas Realizadas

Se llevaron a cabo pruebas utilizando el archivo `02_consultas_y_transacciones.sql` y operaciones manuales desde el menú:

- Inserción de registros de prueba para `mascotas` y `microchips`. 
- Verificacion de todas las operaciones del menu (Gestionar Mascotar / Gestionar Microchips / Transacciones) 
- Verificación de la vista `vista_mascotas_con_microchip`.  
- Pruebas de CRUD completo para Mascota y Microchip.  
- Prueba de la transacción **crear Mascota + Microchip** con:
  - Caso exitoso (commit).  
  - Caso fallido por código duplicado (rollback).  
- Verificación de la integridad referencial con `ON DELETE CASCADE`.  
- Comprobación de que el campo `eliminado` funciona como baja lógica.  

Los resultados confirman que el sistema cumple con los requisitos de:

- Relación 1→1 unidireccional garantizada.  
- Correctitud de operaciones CRUD.  
- Manejo adecuado de errores y transacciones.

---

<a id="requisitos-del-sistema-instalacion-y-ejecucion"></a>
# 9. Requisitos del Sistema, Instalación y Ejecución

## Requisitos

| Componente     | Versión recomendada |
|----------------|---------------------|
| Java JDK       | 17 o superior       |
| MySQL          | 8.0 o superior      |
| IDE            | NetBeans (recomendado) |
| Driver JDBC    | mysql-connector-j-8.4.0.jar |

## Instalación de la Base de Datos

1. Abrir el cliente de MySQL (Workbench, DBeaver, consola, etc.).  
2. Ejecutar el script `01_estructura.sql` para crear la base, tablas y vista.  
3. Ejecutar el script `02_consultas_y_transacciones.sql` para insertar datos de prueba y probar una transacción.  
4. Verificar con:

```sql
SELECT * FROM mascotas;
SELECT * FROM microchips;
SELECT * FROM vista_mascotas_con_microchip;
```

## Ejecución del Proyecto

### Desde un IDE (NetBeans)

1. Importar el proyecto Java.  
2. Configurar el classpath con el JAR de MySQL (`mysql-connector-j-8.4.0.jar`).  
3. Ejecutar la clase `main.Main`.  

### Desde línea de comandos (ejemplo general)

```bash
javac -cp .;mysql-connector-j-8.x.x.jar -d build src/main/java/**/*.java
java -cp build;mysql-connector-j-8.x.x.jar main.Main
```

(Ajustar rutas y nombres de JAR según el entorno.)

---

<a id="conclusiones-y-mejoras-futuras"></a>
# 10. Conclusiones y Mejoras Futuras

### Conclusiones

- Se implementó correctamente una **relación 1→1 unidireccional** entre Mascota y Microchip.  
- La integridad de datos se garantiza a través de una **FK única** en `microchips.mascota_id`.  
- Se construyó una arquitectura en capas (config, dao, service, entities, main) clara y mantenible.  
- El uso de JDBC con `PreparedStatement` asegura seguridad frente a SQL injection.  
- Las transacciones ACID están implementadas y demostradas mediante un caso de fallo simulado.  

### Mejoras Futuras

- Agregar tablas de catálogo para especies y razas, normalizando aún más la base de datos.  
- Implementar un pool de conexiones (por ejemplo, HikariCP) para mejorar el rendimiento.  
- Incorporar pruebas automatizadas con JUnit para la capa de servicio y DAO.  
- Desarrollar una interfaz gráfica (JavaFX o web) sobre la misma capa de servicios.  
- Agregar reportes (CSV/PDF) con listados de mascotas y microchips.  

---

<a id="video-de-presentacion"></a>
# 11. Video de Presentación

De acuerdo con las consignas del TFI, se incluye un video de entre 10 y 15 minutos donde:

- Se presenta el equipo de trabajo.  
- Se muestra el flujo CRUD de Mascota y Microchip.  
- Se explica el diseño de entidades, DAOs, servicios y menú.  
- Se demuestra una operación transaccional con rollback ante un fallo simulado.

📌 Enlace al video:

```text
https://youtu.be/iFxiG2wpDSc
```

---

Este `README.md` cumple con los requisitos solicitados en el PDF del TFI para la documentación del proyecto:  
- Describe el dominio y la relación 1→1.  
- Explica la arquitectura por capas.  
- Documenta la estructura de la base y las transacciones.  
- Detalla funcionalidades, pruebas y posibles mejoras.

