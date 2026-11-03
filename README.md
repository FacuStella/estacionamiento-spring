<<<<<<< HEAD
# Estacionamiento · API
=======
# TP-Progra-II-Spring
>>>>>>> cf1232c9bd8174908f8ca36fc68b23f7f88329fe

API REST (Spring Boot 3.3 / Java 21 / MySQL) para gestionar un **estacionamiento por horas** (estilo shopping
o aeropuerto). Tres roles: **ADMINISTRADOR**, **EMPLEADO** y **SOCIO**. El front está en el proyecto
`estacionamiento-web-boot`.

## El negocio

- **Playa:** varias plantas (0 = planta baja, 1 = piso 1, -1 = subsuelo 1…). Cada planta tiene zonas para
  autos, motos y camionetas, cada zona con su **tarifa por hora** y sus **garages** (cocheras).
  Por defecto: zonas A–C en planta baja y D–F en el piso 1 (20 garages de autos, 40 de motos y 10 de camionetas
  por planta), garages numerados 1..70 y 101..170.
- **Vehículos:** patente, tipo (Auto, Moto, Camioneta), marca (según el tipo) y, opcionalmente, el socio dueño.
- **Estadías (ocupantes):** cada vez que un vehículo entra a un garage se abre una estadía; al salir se cierra
  y se cobra: horas = minutos / 60 redondeado hacia arriba (mínimo 1) × tarifa de la zona.
- **Socios:** tienen un descuento al estacionar por hora (`app.descuento-socio-porcentaje`, 20 % por defecto).
- **Abonos:** un garage puede tener un socio dueño. Ahí solo entran los vehículos de ese socio y no pagan.
- **Empleados:** se asignan a zonas y a vehículos.

## Cómo correrlo

1. MySQL levantado en `localhost:3306` (la base `estacionamiento_db` se crea sola).
2. Definir las variables de entorno `DB_USERNAME` y `DB_PASSWORD` con el usuario de MySQL.
3. Ejecutar `StartApplication` desde NetBeans (o `./mvnw spring-boot:run`). Escucha en el puerto **8082**.
4. Tests (no necesitan MySQL, usan H2 en memoria): `./mvnw test`.

Al arrancar se crea lo que falte: el administrador inicial (`app.admin.username` / `app.admin.password`, por
defecto `admin` / `Admin1234`, que tiene que cambiar la contraseña en su primer ingreso), los tipos de vehículo,
las marcas y las zonas con sus garages.

En producción definir la variable de entorno `JWT_SECRET` (mínimo 32 caracteres).

### Mails

Los usuarios nuevos reciben por mail una contraseña temporal. Si `spring.mail.host` no está configurado, no se
manda nada y el mail se escribe en la consola del backend (sirve para probar). Para mandarlos de verdad, ver el
bloque `spring.mail.*` comentado al final de `application.properties` (ejemplo con Gmail y variables de entorno
`MAIL_USERNAME` / `MAIL_PASSWORD`).

### Migraciones

La base se actualiza con `ddl-auto=update`, que agrega tablas y columnas pero no borra ni transforma datos.
Para una base existente, los cambios de estructura vienen en los `migracion-*.sql` de esta carpeta; cada uno
explica cuándo correrlo (en general, con el backend apagado, desde MySQL Workbench).

## Autenticación

```
POST /api/auth/autenticar   {"username":"admin","password":"Admin1234"}
→ {"token":"eyJ...","tipoUsuario":"ADMINISTRADOR","username":"admin","debeCambiarClave":true}
```
Luego, en cada request: `Authorization: Bearer <token>` (vence a las 2 horas, `app.jwt.expiration-minutes`).

| Prefijo            | Acceso                     |
|--------------------|----------------------------|
| `/api/auth/**`     | público                    |
| `/api/admin/**`    | ADMINISTRADOR              |
| `/api/empleado/**` | EMPLEADO                   |
| `/api/socio/**`    | SOCIO                      |
| `/api/cuenta/**`   | cualquier usuario logueado |

Mientras un usuario tenga una contraseña temporal (`debeCambiarClave`), solo puede usar `/api/cuenta/**`;
el resto responde 403.

Empleados y socios **no** reciben un `{dni}` en la URL: el sistema usa el usuario del token,
así nadie puede ver datos ajenos.

| Clase | Qué hace |
|---|---|
| `security/JwtService` | Genera los tokens (firmados con `app.jwt.secret`) y los verifica. |
| `security/JwtAuthFilter` | En cada request lee el token, identifica al usuario y bloquea a quien tiene contraseña temporal. |
| `security/SecurityConfig` | Qué rol puede usar cada prefijo, API sin sesiones, 401 si no hay usuario. |

## Endpoints

**Cuenta** `/api/cuenta`: `POST /cambiarClave` `{"actual":"...","nueva":"..."}`

**Admin – playa (estadías)** `/api/admin/ocupantes`: `POST /entrada/{numero}/{patente}`, `POST /salida/{numero}`
(devuelve horas, descuento e importe), `GET /activos`, `GET /listar`, `GET /vehiculo/{patente}`

**Admin – socios** `/api/admin/socios`: `POST /registrar`, `GET /mostrar/{dni}`, `GET /listar`,
`PUT /actualizar/{dni}`, `DELETE /eliminar/{dni}`, `GET /listarVehiculos/{dni}`, `GET /listarGarages/{dni}`

**Admin – empleados** `/api/admin/empleados`: `POST /registrar`, `GET /mostrar/{dni}`, `GET /listar`,
`PUT /actualizar/{dni}`, `DELETE /eliminar/{dni}`, `POST /asignarZona/{dni}/{letra}`,
`DELETE /quitarZona/{dni}/{letra}`, `POST /asignarVehiculo/{dni}/{patente}`,
`DELETE /quitarVehiculo/{dni}/{patente}`, `GET /listarVehiculos/{dni}`, `GET /listarZonas/{dni}`

**Admin – administradores** `/api/admin/administradores`: `POST /registrar`, `GET /mostrar/{username}`,
`GET /listar`, `PUT /actualizar/{username}`, `DELETE /eliminar/{username}`

**Admin – usuarios** `/api/admin/usuarios`: `POST /restablecerClave/{username}` (nueva contraseña temporal por mail)

**Admin – zonas** `/api/admin/zonas`: `POST /registrar`, `GET /mostrar/{letra}`, `GET /listar`,
`PUT /actualizar/{letra}`, `DELETE /eliminar/{letra}`, `POST /asignarEmpleado/{letra}/{dniEmpleado}`,
`DELETE /quitarEmpleado/{letra}/{dniEmpleado}`, `GET /listarGarages/{letra}`, `GET /listarEmpleados/{letra}`

**Admin – garages** `/api/admin/garages`: `POST /registrar`, `GET /mostrar/{numero}`, `GET /listar`,
`PUT /actualizar/{numero}`, `DELETE /eliminar/{numero}`, `PUT /asignarZona/{numero}/{letra}`,
`PUT /asignarSocio/{numero}/{dniSocio}` (abono), `DELETE /quitarSocio/{numero}`

**Admin – vehículos** `/api/admin/vehiculos`: `POST /registrar`, `GET /mostrar/{patente}`, `GET /listar`,
`PUT /actualizar/{patente}`, `DELETE /eliminar/{patente}`

**Admin – catálogos**: `GET /api/admin/tiposVehiculo/listar`, `GET /api/admin/marcas/listar?idTipo=` (`idTipo` opcional)

**Empleado** `/api/empleado`: `GET /listarVehiculos`, `GET /listarGarages`, `GET /listarZonas`

**Socio** `/api/socio`: `GET /perfil`, `GET /listarVehiculos`, `GET /listarGarages`

## Reglas de negocio implementadas

- DNI, código de empleado, patente, letra de zona, número de garage y username son únicos (409 si se repiten).
- Patentes y letras de zona se guardan en mayúsculas.
- La marca de un vehículo tiene que corresponder a su tipo (por ejemplo, Yamaha no es una marca de autos).
- Un vehículo no puede entrar si ya está adentro, ni a un garage ocupado, ni a un garage con abono de otro socio.
- Al salir se calcula el importe: tarifa de la zona × horas, menos el descuento de socio (100 % en su propio abono).
- No se puede eliminar un vehículo ni un garage con estadías, ni cambiar el dueño de un garage ocupado,
  ni cambiar el propietario de un vehículo estacionado.
- No se puede eliminar un socio con vehículos o garages, ni una zona con garages.
- Al eliminar un empleado se lo desvincula de sus zonas y vehículos.
- Un administrador no se puede eliminar a sí mismo.
- Las contraseñas se guardan con BCrypt (mínimo 6 caracteres) y nunca se devuelven en el JSON. Las temporales
  se generan al azar, llegan por mail y se tienen que cambiar en el primer ingreso.

## Errores

Respuesta uniforme: `{"status":409,"mensaje":"...","detalles":[]}` — 400 datos inválidos,
401 sin token/credenciales incorrectas, 403 rol insuficiente o contraseña temporal sin cambiar, 404 no existe,
409 conflicto, 502 no se pudo enviar el mail.
