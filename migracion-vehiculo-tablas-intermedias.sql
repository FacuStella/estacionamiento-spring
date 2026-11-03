-- Saca de la tabla vehiculos las relaciones (ahora van en vehiculo_socio, vehiculo_garage y vehiculo_empleado)
-- y la fecha de asignacion. Correr con el backend apagado. La tabla vehiculos estaba vacia, no se pierden datos.
USE estacionamiento_db;

ALTER TABLE vehiculos
    DROP FOREIGN KEY FKb8830pwoacfigrba3bimsnp89,  -- socio_id
    DROP FOREIGN KEY FKcu7jke009we5iqdjq1sjr9c7y,  -- empleado_id
    DROP FOREIGN KEY FKeyf8r7epeasceirkd4er9n4gt;  -- garage_id

ALTER TABLE vehiculos
    DROP COLUMN socio_id,
    DROP COLUMN empleado_id,
    DROP COLUMN garage_id,
    DROP COLUMN fecha_asignacion;
