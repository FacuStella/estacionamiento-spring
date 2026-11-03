-- Las plantas pasan a numerarse como en un edificio: 0 = planta baja, 1 = piso 1, -1 = subsuelo 1.
-- Las zonas que estaban en las plantas 1 y 2 pasan a planta baja y piso 1, y los garages se
-- renumeran igual que en una base nueva: 1..70 en planta baja y 101..170 en el piso 1.
-- Correr con el backend apagado. Las estadias y los abonos no se pierden (apuntan al id, no al numero).
USE estacionamiento_db;

-- Hibernate habia creado este CHECK (planta >= 1) cuando planta tenia @Min(1); ya no aplica.
ALTER TABLE zonas DROP CHECK zonas_chk_3;

UPDATE zonas SET planta = planta - 1;

-- ORDER BY para no chocar con la restriccion de numero unico mientras se renumera
UPDATE garages SET numero = numero - 100 ORDER BY numero;

-- Control
SELECT z.letra, z.planta, MIN(g.numero) desde, MAX(g.numero) hasta, COUNT(*) cocheras
FROM zonas z JOIN garages g ON g.zona_id = z.id GROUP BY z.letra, z.planta ORDER BY z.letra;
