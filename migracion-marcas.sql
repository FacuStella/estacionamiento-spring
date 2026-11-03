-- La marca del vehiculo pasa a elegirse de la tabla marcas, segun el tipo de vehiculo.
--   marcas (id_marca, marca)
--   marca_tipo_vehiculo (id_marca, id_tipo): para que tipos aplica cada marca
--   vehiculos.id_marca reemplaza a la columna de texto vehiculos.marca
-- Correr con el backend apagado. Los vehiculos existentes conservan su marca (se busca por nombre).
-- La lista de marcas es la misma que carga DataInitializer en una base nueva.
USE estacionamiento_db;

CREATE TABLE marcas (
    id_marca BIGINT NOT NULL AUTO_INCREMENT,
    marca VARCHAR(40) NOT NULL,
    PRIMARY KEY (id_marca),
    UNIQUE KEY uk_marcas_marca (marca)
) ENGINE = InnoDB;

CREATE TABLE marca_tipo_vehiculo (
    id_marca BIGINT NOT NULL,
    id_tipo BIGINT NOT NULL,
    PRIMARY KEY (id_marca, id_tipo),
    CONSTRAINT fk_marca_tipo_marca FOREIGN KEY (id_marca) REFERENCES marcas (id_marca),
    CONSTRAINT fk_marca_tipo_tipo FOREIGN KEY (id_tipo) REFERENCES tipos_vehiculo (id_tipo)
) ENGINE = InnoDB;

INSERT INTO marcas (marca) VALUES
    ('Audi'), ('Bajaj'), ('Benelli'), ('BMW'), ('Chery'), ('Chevrolet'), ('Citroën'), ('Corven'), ('Fiat'),
    ('Ford'), ('Gilera'), ('Honda'), ('Hyundai'), ('Jeep'), ('Kawasaki'), ('Keller'), ('Kia'), ('KTM'),
    ('Mercedes-Benz'), ('Motomel'), ('Nissan'), ('Peugeot'), ('RAM'), ('Renault'), ('Royal Enfield'),
    ('Suzuki'), ('Toyota'), ('Volkswagen'), ('Yamaha'), ('Zanella');

-- Marcas de autos -> tipos Auto y Camioneta
INSERT INTO marca_tipo_vehiculo (id_marca, id_tipo)
SELECT m.id_marca, t.id_tipo FROM marcas m JOIN tipos_vehiculo t ON t.tipo IN ('Auto', 'Camioneta')
WHERE m.marca IN ('Audi', 'BMW', 'Chery', 'Chevrolet', 'Citroën', 'Fiat', 'Ford', 'Honda', 'Hyundai', 'Jeep',
                  'Kia', 'Mercedes-Benz', 'Nissan', 'Peugeot', 'RAM', 'Renault', 'Suzuki', 'Toyota', 'Volkswagen');

-- Marcas de motos -> tipo Moto (Honda, BMW y Suzuki quedan en los tres tipos)
INSERT INTO marca_tipo_vehiculo (id_marca, id_tipo)
SELECT m.id_marca, t.id_tipo FROM marcas m JOIN tipos_vehiculo t ON t.tipo = 'Moto'
WHERE m.marca IN ('Bajaj', 'Benelli', 'BMW', 'Corven', 'Gilera', 'Honda', 'Kawasaki', 'Keller', 'KTM',
                  'Motomel', 'Royal Enfield', 'Suzuki', 'Yamaha', 'Zanella');

-- Pasar los vehiculos existentes de la marca en texto a la marca de la tabla
ALTER TABLE vehiculos ADD COLUMN id_marca BIGINT NULL;
UPDATE vehiculos v JOIN marcas m ON m.marca = TRIM(v.marca) SET v.id_marca = m.id_marca;

-- Control: tiene que dar vacio. Si aparece algun vehiculo, su marca no esta en la lista:
-- agregala a marcas (y a marca_tipo_vehiculo) o corregila, y repeti el UPDATE de arriba.
SELECT patente, marca FROM vehiculos WHERE id_marca IS NULL;

-- Si quedo algun vehiculo sin marca, este ALTER falla y no se borra nada.
ALTER TABLE vehiculos
    MODIFY id_marca BIGINT NOT NULL,
    ADD CONSTRAINT fk_vehiculos_marca FOREIGN KEY (id_marca) REFERENCES marcas (id_marca),
    DROP COLUMN marca;

-- Control final
SELECT v.patente, t.tipo, m.marca FROM vehiculos v
JOIN tipos_vehiculo t ON t.id_tipo = v.id_tipo JOIN marcas m ON m.id_marca = v.id_marca;
