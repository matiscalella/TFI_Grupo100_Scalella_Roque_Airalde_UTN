-- Script: consultas_y_transacciones.sql
-- Descripcion: Contiene consultas y transacciones para la gestion de mascotas y microchips.
-- Grupo 100 - UTN TFI Programación II

-- 0. Asegurarse de que se esta utilizando la base de datos correcta
USE db_mascotas;

-- 1. Creacion de vista para mascotas con microchip (si ya existe la vista, la reemplaza)
CREATE OR REPLACE VIEW vista_mascotas_con_microchip AS
SELECT 
    m.id AS id_mascota,
    m.nombre,
    m.especie,
    m.raza,
    m.fecha_nacimiento,
    m.duenio,
    mc.id AS id_microchip,
    mc.codigo,
    mc.fecha_implantacion,
    mc.veterinaria,
    mc.observaciones
FROM 
    mascotas m
LEFT JOIN 
    microchips mc ON m.id = mc.mascota_id
WHERE 
    m.eliminado = FALSE;

-- 2. Eliminar registros de las tablas (si existen)
SET FOREIGN_KEY_CHECKS = 0; -- Desactivar temporalmente las verificaciones de claves foraneas
TRUNCATE TABLE microchips;
TRUNCATE TABLE mascotas;
SET FOREIGN_KEY_CHECKS = 1; -- Reactivar las verificaciones de claves foraneas (reactiva la integridad referencial)
-- Verificar que las tablas esten vacias
SELECT * FROM mascotas; 
SELECT * FROM microchips;

-- 3. Inserciones de prueba para la tabla mascotas
INSERT INTO mascotas (eliminado, nombre, especie, raza, fecha_nacimiento, duenio)
VALUES
(false, 'Akela', 'Perro', 'Dobberman', '2020-05-10', 'Tomas'),
(false, 'Miel', 'Gato', 'Siames', NULL, 'Pablo'), -- Insercion con fecha de nacimiento NULL
(false, 'Dakota', 'Perro', 'Cruza', '2018-04-15', 'Agustina');
SELECT * FROM mascotas; -- Verificar inserciones

-- 4. Inserciones de prueba para la tabla microchips
INSERT INTO microchips (eliminado, codigo, fecha_implantacion, veterinaria, observaciones, mascota_id)
VALUES
(false, 'AAA1001', '2021-06-20', 'Veterinaria Mascotas al Ataque', 'Microchip implantado sin problemas. Se porto bien.', 1),
(false, 'BBB1002', '2022-04-10', 'Animal Planetazo',  'Microchip implantado en pata izquierda. Agendado para control.', 2),
(false, 'CCC1003', '2022-04-10', 'AnimalCare',  'Microchip implantado en pata derecha. Agendado para control.', 3);
SELECT * FROM microchips; -- Verificar inserciones

-- 5. Transaccion de prueba: insertar una nueva mascota y su microchip asociado
-- Asegurarse que la conexion tenga habilitada la opcion de allowMultiQueries=true (por defecto en DBeaver esta en false)

START TRANSACTION;
INSERT INTO mascotas (eliminado, nombre, especie, raza, fecha_nacimiento, duenio)
VALUES (false, 'Coco', 'Loro', 'Ave', '2023-11-23', 'Sofia');

INSERT INTO microchips (eliminado, codigo, fecha_implantacion, veterinaria, observaciones, mascota_id)
VALUES (false, 'DDD1004', '2024-05-15', 'Veterinaria ', 'Microchip implantado en ala derecha.', LAST_INSERT_ID());

COMMIT;
