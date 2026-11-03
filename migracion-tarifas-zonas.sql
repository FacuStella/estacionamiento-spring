-- Agrega la tarifa por hora a las zonas que ya existen (las nuevas la traen desde DataInitializer).
-- Correr con el backend apagado, antes de levantarlo con el codigo de Ocupante.
USE estacionamiento_db;

ALTER TABLE zonas ADD COLUMN tarifa_hora DOUBLE NOT NULL DEFAULT 0;

UPDATE zonas SET tarifa_hora = 1500 WHERE tipo_vehiculos = 'Autos';
UPDATE zonas SET tarifa_hora = 800  WHERE tipo_vehiculos = 'Motos';
UPDATE zonas SET tarifa_hora = 2000 WHERE tipo_vehiculos = 'Camionetas';

-- Control: ninguna zona deberia quedar en 0
SELECT letra, tipo_vehiculos, planta, tarifa_hora FROM zonas ORDER BY letra;
