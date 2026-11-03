-- Usuarios con mail y contraseña temporal.
-- Las columnas usuarios.mail y usuarios.debe_cambiar_clave las agrega Hibernate solo (ddl-auto=update)
-- al arrancar el backend. Correr esto DESPUES de arrancarlo una vez, para los usuarios que ya existian.
USE estacionamiento_db;

-- El administrador que ya existia tiene que cambiar su contraseña en el proximo login.
-- Cargale tambien un mail para poder restablecerle la clave mas adelante.
UPDATE usuarios SET debe_cambiar_clave = 1, mail = 'CAMBIAR@example.com' WHERE username = 'admin';

-- Usuarios que quedaron sin mail: hay que cargarselo (desde la app, editando el socio o empleado,
-- o con un UPDATE como el de arriba) antes de poder restablecerles la contraseña.
SELECT username, tipo_usuario, mail, debe_cambiar_clave FROM usuarios ORDER BY tipo_usuario, username;
