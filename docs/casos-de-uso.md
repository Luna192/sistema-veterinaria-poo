# Sistema de Gestión Veterinaria — Casos de uso

## 1. Alcance

El alcance elegido contiene tres pantallas principales:

1. Clientes y mascotas.
2. Gestión de turnos.
3. Historial médico y vacunaciones.

## 2. Actores

- **Usuario administrativo:** registra clientes y mascotas, programa, confirma y cancela turnos.
- **Veterinario:** registra diagnósticos, tratamientos y aplicaciones de vacunas, y marca la atención como realizada.

En la primera versión ambos pueden utilizar la misma aplicación sin autenticación por roles; los actores expresan responsabilidades del negocio.

---

## CU-01 — Registrar cliente

**Actor principal:** usuario administrativo.

**Objetivo:** incorporar un cliente a la veterinaria.

**Pantalla:** clientes y mascotas.

**Precondiciones:** ninguna.

### Flujo principal

1. El usuario selecciona “Nuevo cliente”.
2. Ingresa nombre, apellido, DNI, teléfono y correo electrónico.
3. Solicita guardar.
4. El sistema valida los datos obligatorios y el medio de contacto.
5. El sistema comprueba que el DNI no esté registrado.
6. Crea el cliente en estado activo.
7. Guarda el cliente.
8. Muestra la confirmación y lo selecciona en la pantalla.

### Flujos alternativos

- **A1. Datos inválidos:** se informa qué campo debe corregirse y no se crea el cliente.
- **A2. DNI duplicado:** se informa que el cliente ya se encuentra registrado.

**Postcondición de éxito:** existe un cliente activo que puede registrar mascotas.

**Reglas relacionadas:** RN-CLI-01 a RN-CLI-05.

---

## CU-02 — Registrar mascota de un cliente

**Actor principal:** usuario administrativo.

**Objetivo:** registrar una mascota bajo la responsabilidad de un cliente existente.

**Pantalla:** clientes y mascotas.

**Precondiciones:** el cliente existe y está activo.

### Flujo principal

1. El usuario busca y selecciona un cliente.
2. Selecciona “Nueva mascota”.
3. Ingresa número de ficha, nombre, especie, raza, fecha de nacimiento y peso.
4. Solicita guardar.
5. El sistema valida número de ficha, peso y fecha de nacimiento.
6. Ejecuta `cliente.registrarMascota(...)`.
7. La mascota referencia al cliente y el cliente la incorpora a su colección.
8. Se guardan los cambios.
9. La mascota aparece en la tabla del cliente.

### Flujos alternativos

- **A1. Cliente inactivo:** se rechaza el registro.
- **A2. Ficha duplicada:** se solicita otro número de ficha.
- **A3. Peso no positivo:** se informa que el peso debe ser mayor que cero.
- **A4. Fecha futura:** se solicita una fecha válida.

**Postcondición de éxito:** existe una mascota activa vinculada exactamente a un cliente.

**Reglas relacionadas:** RN-MAS-01 a RN-MAS-08.

---

## CU-03 — Modificar o dar de baja una mascota

**Actor principal:** usuario administrativo.

**Objetivo:** corregir información o impedir nuevas operaciones sin perder el historial.

**Pantalla:** clientes y mascotas.

**Precondiciones:** la mascota existe.

### Flujo principal de modificación

1. El usuario selecciona una mascota.
2. Modifica los datos permitidos.
3. El sistema valida los nuevos valores.
4. La mascota ejecuta la operación correspondiente, por ejemplo `actualizarPeso(...)`.
5. Se guardan los cambios.

### Flujo principal de baja

1. El usuario selecciona “Dar de baja”.
2. El sistema solicita confirmación.
3. La mascota ejecuta `darDeBaja()`.
4. Se conserva su historial, pero deja de estar disponible para turnos nuevos.

**Postcondición:** la información queda actualizada o la mascota queda inactiva sin borrarse.

---

## CU-04 — Programar turno

**Actor principal:** usuario administrativo.

**Objetivo:** reservar una fecha y hora para una mascota con un veterinario y servicios determinados.

**Pantalla:** gestión de turnos.

**Precondiciones:** existen una mascota activa, un veterinario activo y al menos un servicio activo.

### Flujo principal

1. El usuario selecciona “Nuevo turno”.
2. Selecciona mascota y veterinario.
3. Ingresa una fecha y hora futuras o selecciona atención inmediata para un cliente sin reserva previa.
4. Selecciona uno o más servicios sin repetir la identidad de ninguno.
5. El sistema crea temporalmente los detalles y calcula la duración total.
6. `GestorTurnos` calcula el intervalo del turno.
7. Comprueba la disponibilidad de la mascota.
8. Comprueba la disponibilidad del veterinario.
9. Crea el turno en estado `PENDIENTE`.
10. Cada detalle conserva el precio y la duración vigentes.
11. Si el cliente ya aceptó el horario, el usuario elige “Guardar y confirmar”: se ejecuta `confirmar()` y se guardan la creación y la confirmación en una misma transacción. En caso contrario elige “Guardar pendiente”.
12. El sistema muestra el estado y el importe total. En ambos casos el turno guardado reserva el intervalo.

### Flujos alternativos

- **A1. Sin servicios:** se rechaza el turno.
- **A2. Mascota o veterinario inactivos:** se informa que no pueden seleccionarse.
- **A3. Fecha pasada:** se solicita una fecha futura o la opción de atención inmediata; esta última utiliza el momento actual y vuelve a comprobar la disponibilidad al registrar.
- **A4. Mascota ocupada:** se informa el solapamiento.
- **A5. Veterinario ocupado:** se informa el solapamiento.
- **A6. Guardería sin cupo:** no se permite agregar o confirmar ese servicio.
- **A7. Servicio repetido:** se rechaza el segundo detalle con la misma identidad de servicio.
- **A8. Error al guardar y confirmar:** se revierte la operación completa; no queda una reserva incompleta.

**Postcondición de éxito:** existe un turno pendiente o confirmado, según la opción elegida, con al menos un detalle, servicios sin duplicados y valores históricos congelados.

**Reglas relacionadas:** RN-TUR-01 a RN-TUR-17, RN-EST-08 y RN-SER-01 a RN-SER-06.

---

## CU-05 — Confirmar turno

**Actor principal:** usuario administrativo.

**Objetivo:** confirmar una reserva pendiente.

**Pantalla:** gestión de turnos.

**Precondición:** el turno existe y está `PENDIENTE`.

### Flujo principal

1. El usuario selecciona el turno.
2. Solicita confirmarlo.
3. El turno ejecuta `confirmar()`.
4. El estado cambia a `CONFIRMADO`.
5. Se guarda el cambio.

### Flujo alternativo

- **A1. Estado diferente de pendiente:** se lanza `TransicionEstadoInvalidaException` y no se modifica el turno.

**Postcondición de éxito:** el turno queda confirmado y preparado para ser atendido.

---

## CU-06 — Cancelar turno

**Actor principal:** usuario administrativo.

**Objetivo:** cancelar una reserva respetando el plazo establecido.

**Pantalla:** gestión de turnos.

**Precondición:** el turno está `PENDIENTE` o `CONFIRMADO`.

### Flujo principal

1. El usuario selecciona el turno.
2. Solicita cancelarlo.
3. El sistema proporciona la fecha y hora actual a `turno.cancelar(...)`.
4. El turno verifica que falten más de 24 horas.
5. El estado cambia a `CANCELADO`.
6. Se guarda el cambio y se libera el horario.

### Flujos alternativos

- **A1. Fuera de término:** se lanza `CancelacionFueraDeTerminoException`.
- **A2. Turno atendido o cancelado:** se lanza `TransicionEstadoInvalidaException`.

**Postcondición de éxito:** el turno continúa almacenado como cancelado y su horario queda disponible.

**Reglas relacionadas:** RN-EST-03 a RN-EST-07.

---

## CU-07 — Atender turno

**Actor principal:** veterinario.

**Objetivo:** registrar los resultados de los servicios y finalizar la atención.

**Pantallas:** gestión de turnos e historial médico/vacunaciones.

**Precondición:** el turno está `CONFIRMADO`.

### Flujo principal

1. El veterinario selecciona el turno confirmado.
2. El sistema identifica los tipos concretos de servicios incluidos.
3. Si existe una consulta médica, solicita diagnóstico y tratamiento.
4. `ConsultaMedica` valida y crea el `RegistroMedico`.
5. Por cada servicio de vacunación, el sistema identifica la vacuna correspondiente.
6. `ServicioVacunacion` comprueba que la mascota no tenga esa vacuna vigente.
7. Crea la `AplicacionVacuna` con el veterinario asignado al turno y calcula la próxima dosis.
8. El turno ejecuta `atender()`.
9. El estado cambia a `ATENDIDO`.
10. Se guardan conjuntamente el turno y los registros generados. Las indicaciones particulares, incluidas las de peluquería, pueden registrarse como observaciones opcionales de cada detalle.

### Flujos alternativos

- **A1. Turno no confirmado:** se rechaza la atención.
- **A2. Diagnóstico obligatorio ausente:** no se finaliza el turno con consulta.
- **A3. Vacuna vigente:** se informa el vencimiento y no se crea una nueva aplicación.
- **A4. Error durante el guardado:** la transacción se revierte para evitar una atención incompleta.

**Postcondición de éxito:** el turno queda atendido y sus resultados aparecen en el historial de la mascota.

**Reglas relacionadas:** RN-EST-02, RN-MED-01 a RN-MED-07 y RN-VAC-01 a RN-VAC-10.

---

## CU-08 — Consultar historial de una mascota

**Actor principal:** usuario administrativo o veterinario.

**Objetivo:** visualizar los antecedentes completos de una mascota.

**Pantalla:** historial médico y vacunaciones.

**Precondición:** la mascota existe; puede estar activa o inactiva.

### Flujo principal

1. El usuario busca por nombre o número de ficha.
2. El sistema presenta las mascotas coincidentes.
3. El usuario selecciona una mascota.
4. El sistema recupera sus turnos atendidos, detalles, registros médicos y aplicaciones de vacunas.
5. Ordena los acontecimientos cronológicamente.
6. Muestra fecha, veterinario, servicios, diagnóstico, tratamiento y vacunas correspondientes.
7. Opcionalmente filtra por tipo de servicio o rango de fechas.

### Flujos alternativos

- **A1. Sin resultados:** se informa que no existen mascotas coincidentes.
- **A2. Sin antecedentes:** se muestra la mascota con un historial vacío.

**Postcondición:** ninguna información del dominio es modificada.

---

## CU-09 — Consultar vencimientos de vacunas

**Actor principal:** usuario administrativo o veterinario.

**Objetivo:** identificar vacunas vencidas o próximas a vencer.

**Pantalla:** historial médico y vacunaciones.

**Precondición:** existen aplicaciones de vacunas registradas.

### Flujo principal

1. El usuario abre la sección de vacunaciones.
2. El sistema utiliza la fecha actual como referencia.
3. Consulta las últimas aplicaciones de cada vacuna por mascota.
4. Clasifica las vacunas como vigentes, próximas a vencer o vencidas.
5. Muestra las vencidas y las que vencerán dentro de los próximos 30 días.

**Postcondición:** ninguna información es modificada.

---

## CU-10 — Registrar vacunación directamente

**Actor principal:** veterinario.

**Objetivo:** incorporar al historial una aplicación anterior que no se origina en un turno de la clínica.

**Pantalla:** historial médico y vacunaciones.

**Precondiciones:** la mascota y la vacuna existen y están activas; se identifica al veterinario que realizó la aplicación. El profesional externo debe estar registrado con su matrícula para poder seleccionarlo.

### Flujo principal

1. El usuario selecciona la mascota.
2. Selecciona la vacuna, el veterinario responsable y la fecha real de aplicación.
3. El sistema consulta los antecedentes de esa vacuna para la mascota.
4. Comprueba la periodicidad con respecto a la fecha de aplicación registrada, no respecto a la fecha de carga del antecedente.
5. Calcula la próxima dosis.
6. Crea `AplicacionVacuna` sin asociarla a un turno.
7. Guarda la aplicación y actualiza el historial. No genera un nuevo importe por cobrar; las vacunaciones realizadas por la clínica se registran mediante un turno, incluso sin reserva previa.

### Flujos alternativos

- **A1. Vacuna vigente:** no se registra la nueva aplicación.
- **A2. Mascota o vacuna inactivas:** se rechaza la operación.
- **A3. Veterinario ausente:** se solicita identificar al profesional responsable antes de guardar.

**Postcondición de éxito:** existe una aplicación histórica asociada a la mascota, a la vacuna y al veterinario, sin turno asociado.

---

## 3. Relación entre pantallas y casos de uso

| Pantalla | Casos de uso principales |
|---|---|
| Clientes y mascotas | CU-01, CU-02 y CU-03 |
| Gestión de turnos | CU-04, CU-05, CU-06 y comienzo de CU-07 |
| Historial médico y vacunaciones | final de CU-07, CU-08, CU-09 y CU-10 |


