-- Deshabilitar constraints temporalmente para inserción
SET REFERENTIAL_INTEGRITY FALSE;

-- Insertar contenedores (sin estado por ahora)
INSERT INTO contenedor (id, estado_id) VALUES (1, NULL);
INSERT INTO contenedor (id, estado_id) VALUES (2, NULL);
INSERT INTO contenedor (id, estado_id) VALUES (3, NULL);
INSERT INTO contenedor (id, estado_id) VALUES (4, NULL);
INSERT INTO contenedor (id, estado_id) VALUES (5, NULL);

-- Insertar estados (con sus contenedores)
-- Estado 1: Verde (45.5 kg - bajo nivel)
INSERT INTO estado (id, cantidad, fecha, estado, contenedor_id)
VALUES (1, 45.5, '2025-01-15 10:00:00', 'Verde', 1);

-- Estado 2: Naranja (85.0 kg - nivel medio-alto)
INSERT INTO estado (id, cantidad, fecha, estado, contenedor_id)
VALUES (2, 85.0, '2025-01-16 11:30:00', 'Naranja', 2);

-- Estado 3: Rojo (95.0 kg - nivel alto)
INSERT INTO estado (id, cantidad, fecha, estado, contenedor_id)
VALUES (3, 95.0, '2025-01-17 14:00:00', 'Rojo', 3);

-- Estado 4: Verde (30.0 kg - bajo nivel)
INSERT INTO estado (id, cantidad, fecha, estado, contenedor_id)
VALUES (4, 30.0, '2025-01-18 09:00:00', 'Verde', 4);

-- Estado 5: Naranja (88.5 kg - nivel medio-alto)
INSERT INTO estado (id, cantidad, fecha, estado, contenedor_id)
VALUES (5, 88.5, '2025-01-18 16:00:00', 'Naranja', 5);

-- Actualizar contenedores con su estado actual
UPDATE contenedor SET estado_id = 1 WHERE id = 1;
UPDATE contenedor SET estado_id = 2 WHERE id = 2;
UPDATE contenedor SET estado_id = 3 WHERE id = 3;
UPDATE contenedor SET estado_id = 4 WHERE id = 4;
UPDATE contenedor SET estado_id = 5 WHERE id = 5;

-- Reactivar constraints
SET REFERENTIAL_INTEGRITY TRUE;