# TP-Progra-II-Spring (Sherlock Homes)

API REST (Spring Boot 3.3 / Java 21 / MySQL) para la gestión de un negocio de alquiler de
estacionamiento tipo camping. Tres roles: **ADMINISTRADOR**, **EMPLEADO** y **SOCIO**.

## Cómo correrlo

1. MySQL levantado en `localhost:3306` (la base `sherlock_homes_db` se crea sola).
2. Revisar usuario/contraseña de MySQL en `src/main/resources/application.properties`.
3. Ejecutar `SherlockhomesApplication` desde NetBeans (o `./mvnw spring-boot:run`).
4. Tests (no necesitan MySQL, usan H2 en memoria): `./mvnw test`.

Al arrancar por primera vez se crea un administrador (`app.admin.username` / `app.admin.password`
en `application.properties`; por defecto `admin` / `Admin1234`). **Cambiar esa contraseña.**
En producción definir la variable de entorno `JWT_SECRET` (mínimo 32 caracteres).

## Autenticación

```
POST /api/auth/autenticar   {"username":"admin","password":"Admin1234"}
→ {"token":"eyJ...","tipoUsuario":"ADMINISTRADOR","username":"admin"}
```
Luego, en cada request: `Authorization: Bearer <token>` (vence a las 2 horas, configurable).

| Prefijo            | Rol necesario    |
|--------------------|------------------|
| `/api/auth/**`     | público          |
| `/api/admin/**`    | ADMINISTRADOR    |
| `/api/empleado/**` | EMPLEADO         |
| `/api/socio/**`    | SOCIO            |

Empleados y socios **no** reciben un `{dni}` en la URL: el sistema usa el usuario del token,
así nadie puede ver datos ajenos.

## Endpoints

**Admin – socios** `/api/admin/socios`: `POST /registrar`, `GET /mostrar/{dni}`, `GET /listar`,
`PUT /actualizar/{dni}`, `DELETE /eliminar/{dni}`, `GET /listarVehiculos/{dni}`, `GET /listarGarages/{dni}`

**Admin – empleados** `/api/admin/empleados`: `POST /registrar`, `GET /mostrar/{dni}`, `GET /listar`,
`PUT /actualizar/{dni}`, `DELETE /eliminar/{dni}`, `POST /asignarZona/{dni}/{letra}`,
`DELETE /quitarZona/{dni}/{letra}`, `POST /asignarVehiculo/{dni}/{patente}`,
`DELETE /quitarVehiculo/{dni}/{patente}`, `GET /listarVehiculos/{dni}`, `GET /listarZonas/{dni}`

**Admin – zonas** `/api/admin/zonas`: `POST /registrar`, `GET /mostrar/{letra}`, `GET /listar`,
`PUT /actualizar/{letra}`, `DELETE /eliminar/{letra}`, `POST /asignarEmpleado/{letra}/{dniEmpleado}`,
`DELETE /quitarEmpleado/{letra}/{dniEmpleado}`, `GET /listarGarages/{letra}`, `GET /listarEmpleados/{letra}`

**Admin – garages** `/api/admin/garages`: `POST /registrar`, `GET /mostrar/{numero}`, `GET /listar`,
`PUT /actualizar/{numero}`, `DELETE /eliminar/{numero}`, `PUT /asignarZona/{numero}/{letra}`,
`PUT /asignarSocio/{numero}/{dniSocio}`, `DELETE /quitarSocio/{numero}`,
`POST /asignarVehiculo/{numero}/{patente}`, `DELETE /quitarVehiculo/{numero}`

**Admin – vehículos** `/api/admin/vehiculos`: `POST /registrar`, `GET /mostrar/{patente}`, `GET /listar`,
`PUT /actualizar/{patente}`, `DELETE /eliminar/{patente}`, `POST /asignarGarage/{patente}/{numero}`,
`DELETE /quitarGarage/{patente}`

**Empleado** `/api/empleado`: `GET /listarVehiculos`, `GET /listarGarages`, `GET /listarZonas`

**Socio** `/api/socio`: `GET /perfil`, `GET /listarVehiculos`, `GET /listarGarages`

## Reglas de negocio implementadas

- DNI, código de empleado, patente, letra de zona y número de garage son únicos (409 si se repiten).
- Patentes y letras de zona se guardan en mayúsculas.
- Un vehículo solo se asigna a un garage **libre**, **con propietario**, y cuyo propietario sea el **dueño del vehículo**.
- No se puede cambiar el propietario de un garage ni eliminarlo mientras tenga un vehículo asignado.
- No se puede eliminar un socio con vehículos o garages, ni una zona con garages.
- Al eliminar un empleado se lo desvincula de sus zonas y vehículos.
- Las contraseñas se guardan con BCrypt (mínimo 6 caracteres) y nunca se devuelven en el JSON.

## Errores

Respuesta uniforme: `{"status":409,"mensaje":"...","detalles":[]}` — 400 datos inválidos,
401 sin token/credenciales incorrectas, 403 rol insuficiente, 404 no existe, 409 conflicto.
