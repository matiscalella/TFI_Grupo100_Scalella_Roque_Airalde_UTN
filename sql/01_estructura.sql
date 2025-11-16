-- 1. Creación de la Base de Datos
CREATE DATABASE IF NOT EXISTS db_mascotas
    CHARACTER SET utf8
    COLLATE utf8_spanish_ci;

USE db_mascotas;

DROP DATABASE db_mascotas;

-- 2. Creación de la Tabla A: mascotas
CREATE TABLE mascotas (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    eliminado BOOLEAN NOT NULL DEFAULT FALSE,
    nombre VARCHAR(60) NOT NULL,
    especie VARCHAR(30) NOT NULL,
    raza VARCHAR(60),
    fecha_nacimiento DATE,
    duenio VARCHAR(120) NOT NULL
);

-- 3. Creación de la Tabla B: microchips
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

-- 4. Creacion de vista para mascotas con microchip
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
