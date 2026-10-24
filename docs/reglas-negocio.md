# Sistema de Gestión Veterinaria — Reglas de negocio

## 1. Propósito

Este documento convierte el enunciado y el modelo de dominio en reglas verificables. Las reglas identificadas con `RN` deben cumplirse con independencia de la pantalla desde la que se ejecute la operación.

## 2. Clientes y mascotas

### Clientes

- **RN-CLI-01. Identidad técnica:** cada cliente tendrá un `id` técnico único generado al persistirse.
- **RN-CLI-02. Datos obligatorios:** nombre, apellido y DNI no pueden ser nulos ni quedar en blanco.
- **RN-CLI-03. DNI único:** no pueden registrarse dos clientes activos con el mismo DNI.
- **RN-CLI-04. Datos de contacto:** el teléfono y el correo electrónico se validarán antes de actualizarse. Se exigirá al menos un medio de contacto.
- **RN-CLI-05. Mascotas iniciales:** un cliente puede registrarse sin mascotas.
- **RN-CLI-06. Baja lógica:** un cliente desactivado se conserva para mantener el historial y no puede recibir nuevas mascotas ni participar en turnos nuevos.

### Mascotas

- **RN-MAS-01. Propietario obligatorio:** una mascota debe pertenecer exactamente a un cliente existente.
- **RN-MAS-02. Alta consistente:** la mascota se registrará mediante `Cliente.registrarMascota(...)`, estableciendo la referencia al cliente y agregándola a su colección.
- **RN-MAS-03. Número de ficha único:** cada mascota tendrá un número de ficha veterinaria único, independiente de su `id` técnico.
- **RN-MAS-04. Nombres repetidos:** distintas mascotas pueden tener el mismo nombre; el nombre no constituye su identidad.
- **RN-MAS-05. Peso válido:** el peso debe ser mayor que cero.
- **RN-MAS-06. Fecha válida:** la fecha de nacimiento no puede ser posterior a la fecha actual.
- **RN-MAS-07. Edad derivada:** la edad se calcula desde la fecha de nacimiento y no se almacena como atributo permanente.
- **RN-MAS-08. Baja lógica:** una mascota desactivada conserva sus turnos, registros y vacunaciones, pero no puede recibir turnos ni aplicaciones nuevas.

## 3. Veterinarios

- **RN-VET-01. Matrícula obligatoria y única:** todo veterinario tendrá una matrícula profesional no vacía y no repetida.
- **RN-VET-02. Especialidades:** un veterinario puede poseer una o más especialidades sin duplicados.
- **RN-VET-03. Disponibilidad:** un veterinario no puede participar en prestaciones veterinarias con intervalos superpuestos. Se comparan los detalles de consulta y vacunación, no la estadía ni el intervalo general del turno.
- **RN-VET-04. Baja lógica:** un veterinario desactivado conserva las atenciones históricas, pero no puede asignarse a turnos nuevos.
- **RN-VET-05. Alcance de las especialidades:** las especialidades son información profesional y no restringen los servicios asignables al veterinario.

## 4. Servicios

- **RN-SER-01. Servicio válido:** todo servicio debe tener nombre, precio base no negativo y duración positiva en minutos.
- **RN-SER-02. Facturación:** todo servicio es facturable.
- **RN-SER-03. Duración:** cada servicio aporta una duración que se conserva en su detalle. La suma de esas duraciones expresa el total de prestaciones, pero no determina la ocupación del veterinario ni necesariamente el tiempo transcurrido del turno.
- **RN-SER-04. Disponibilidad:** un servicio inactivo no puede agregarse a turnos nuevos.
- **RN-SER-05. Tipos concretos:** no se crean instancias directas de `Servicio`; se utilizan `ConsultaMedica`, `ServicioVacunacion`, `Guarderia` o `Peluqueria`.
- **RN-SER-06. Comportamiento polimórfico:** cada tipo de servicio define si requiere registro médico y aplica sus reglas particulares.
- **RN-SER-07. Participación profesional:** `requiereVeterinario()` devuelve verdadero para consulta y vacunación y falso para guardería y peluquería. El modelo utiliza ese comportamiento para determinar cuándo es obligatoria la asignación de un veterinario.

## 5. Turnos

### Creación y contenido

- **RN-TUR-01. Participantes:** todo turno corresponde a una mascota. Tiene un único veterinario obligatorio si incluye consulta o vacunación. Si contiene exclusivamente guardería o peluquería, no se le asigna veterinario.
- **RN-TUR-02. Servicios obligatorios:** todo turno contiene al menos un servicio; no existen turnos vacíos.
- **RN-TUR-03. Fecha de inicio:** cada detalle de una reserva se programa para una fecha y hora futuras. Para atención sin reserva previa se permite utilizar el momento actual, comprobando la disponibilidad de los intervalos correspondientes. No se programan prestaciones nuevas con inicio pasado.
- **RN-TUR-04. Estado inicial:** todo turno nuevo comienza en estado `PENDIENTE`.
- **RN-TUR-05. Duraciones:** `calcularDuracionTotal()` suma las duraciones congeladas de los detalles. `calcularDuracionTranscurrida()` calcula los minutos entre el primer inicio y el último final, incluyendo esperas y sin sumar dos veces intervalos superpuestos. La suma no se utiliza para bloquear agendas.
- **RN-TUR-06. Inicio y final derivados:** el inicio del turno es el inicio más temprano de sus detalles; su final es el final más tardío. No se almacena otra fecha y hora general independiente de esos datos.
- **RN-TUR-07. Precio final:** el importe del turno es la suma de los precios congelados de todos sus detalles.
- **RN-TUR-08. Precio histórico:** cada `DetalleTurno` conserva el precio que tenía el servicio al incorporarse al turno.
- **RN-TUR-09. Duración histórica:** cada `DetalleTurno` conserva también la duración utilizada al programar el turno.
- **RN-TUR-15. Servicios no repetidos:** un turno admite distintos servicios, pero no dos detalles con la misma identidad de servicio. La comparación no se realiza por el nombre ni solamente por el tipo de servicio. Las vacunaciones de vacunas diferentes pueden coexistir si cada una cumple sus reglas de aplicación.
- **RN-TUR-16. Atención sin reserva:** si el cliente llega sin reserva previa y existe disponibilidad, el personal crea y confirma un turno para el momento de la atención. Los servicios y su importe se registran mediante sus detalles.

### Solapamientos

- **RN-TUR-10. Solapamiento del veterinario:** se comparan únicamente los intervalos de detalles que requieren veterinario, tanto dentro del turno como frente a otras prestaciones asignadas al mismo profesional. Guardería y peluquería no reservan su agenda, aunque pertenezcan a un turno mixto.
- **RN-TUR-11. Solapamiento de la mascota:** las consultas, vacunaciones y peluquerías de una misma mascota no pueden superponerse entre sí, dentro de un turno ni entre turnos diferentes. Una estadía de guardería puede contener esas atenciones sin provocar un conflicto de disponibilidad.
- **RN-TUR-12. Intervalos contiguos:** si un detalle termina exactamente cuando comienza otro, no existe solapamiento. La comparación usa intervalos con inicio incluido y final excluido.
- **RN-TUR-13. Cancelados:** los turnos cancelados liberan el horario y no participan en la validación de solapamientos.
- **RN-TUR-14. Coordinación:** `GestorTurnos` valida los solapamientos porque necesita consultar el conjunto de turnos existentes.
- **RN-TUR-17. Reserva de recursos:** los detalles de turnos `PENDIENTE` y `CONFIRMADO` guardados reservan los recursos que corresponden a cada servicio: intervalo de atención de la mascota, intervalo del veterinario si se requiere y cupo diario de guardería cuando corresponda. La búsqueda de horarios por sí sola no reserva recursos. No se prevé vencimiento automático de pendientes. Las atenciones `ATENDIDO` conservan sus intervalos históricos y el cupo utilizado; `CANCELADO` no ocupa recursos.
- **RN-TUR-18. Horario de cada prestación:** cada `DetalleTurno` guarda `fechaHoraInicio`; su final se calcula sumando su duración congelada. Los servicios pueden tener intervalos diferentes y existir esperas entre ellos.

### Estados

- **RN-EST-01. Confirmación:** solamente un turno `PENDIENTE` puede pasar a `CONFIRMADO`.
- **RN-EST-02. Atención:** solamente un turno `CONFIRMADO` puede pasar a `ATENDIDO`.
- **RN-EST-03. Cancelación:** un turno `PENDIENTE` o `CONFIRMADO` puede pasar a `CANCELADO` si faltan más de 24 horas respecto al inicio calculado del turno, es decir, el primer servicio.
- **RN-EST-04. Límite exacto:** si faltan exactamente 24 horas o menos, no se permite cancelar, siguiendo la redacción “más de veinticuatro horas” del enunciado.
- **RN-EST-05. Estado atendido terminal:** un turno `ATENDIDO` no puede cambiar a otro estado.
- **RN-EST-06. Estado cancelado terminal:** un turno `CANCELADO` no puede reactivarse.
- **RN-EST-07. Conservación:** cancelar modifica el estado; nunca elimina físicamente el turno.
- **RN-EST-08. Registro y confirmación:** la pantalla ofrece “Guardar pendiente” y “Guardar y confirmar”. En la segunda opción se crea el turno `PENDIENTE`, se ejecuta `confirmar()` tras la aceptación del cliente y se guarda todo en una misma transacción. Nunca se omite la transición a `CONFIRMADO`.

## 6. Consulta e historial médico

- **RN-MED-01. Origen:** un `RegistroMedico` corresponde a un turno que incluye una `ConsultaMedica`.
- **RN-MED-02. Cantidad:** un turno puede tener como máximo un registro médico en el modelo actual.
- **RN-MED-03. Atención clínica:** antes de marcar como atendido un turno con consulta, debe registrarse un diagnóstico válido. El tratamiento puede quedar vacío cuando el profesional determine que no corresponde.
- **RN-MED-04. Pertenencia:** el registro médico no se traslada entre turnos y no existe independientemente de la atención que lo originó.
- **RN-MED-05. Historial derivado:** el historial de una mascota se obtiene de sus turnos atendidos, registros médicos y aplicaciones de vacunas; no se necesita una entidad contenedora llamada `HistorialMedico`.
- **RN-MED-06. Orden:** el historial se presenta cronológicamente.
- **RN-MED-07. Permanencia:** los registros de turnos atendidos, diagnósticos, tratamientos y vacunaciones no se eliminan por la baja lógica de otras entidades.

## 7. Vacunaciones

- **RN-VAC-01. Catálogo:** una `Vacuna` define nombre comercial, enfermedad que previene y periodicidad positiva en meses.
- **RN-VAC-02. Servicio específico:** cada `ServicioVacunacion` se asocia con exactamente una vacuna.
- **RN-VAC-03. Aplicación histórica:** `AplicacionVacuna` representa una dosis concreta recibida por una mascota; no representa el catálogo ni el servicio facturable.
- **RN-VAC-04. Datos obligatorios:** toda aplicación indica mascota, vacuna, veterinario responsable, fecha de aplicación y fecha de próxima dosis. El veterinario es obligatorio aunque la aplicación no esté asociada a un turno.
- **RN-VAC-05. Cálculo:** la próxima dosis se calcula sumando la periodicidad de la vacuna a la fecha de aplicación.
- **RN-VAC-06. Vigencia:** si la aplicación anterior de la misma vacuna sigue vigente, no se permite una nueva aplicación.
- **RN-VAC-07. Asociación opcional:** una aplicación puede originarse en un turno o registrarse directamente sobre la mascota, según permite el enunciado.
- **RN-VAC-08. Varias dosis:** una mascota puede tener muchas aplicaciones y una vacuna puede haber sido aplicada a muchas mascotas.
- **RN-VAC-09. Alertas:** una vacuna está próxima a vencer cuando su próxima dosis se encuentra dentro del intervalo configurable, inicialmente 30 días.
- **RN-VAC-10. Baja del catálogo:** una vacuna inactiva no puede aplicarse nuevamente, pero sus aplicaciones históricas permanecen.
- **RN-VAC-11. Profesional responsable:** si la aplicación proviene de un turno, su veterinario coincide con el asignado a ese turno. Para un antecedente sin turno, se identifica al profesional que realizó la aplicación.
- **RN-VAC-12. Origen e importe:** las vacunaciones realizadas por la clínica se registran mediante un turno, aunque el cliente llegue sin reserva previa, conservando el precio del servicio. El registro directo sin turno incorpora aplicaciones anteriores al historial y no genera un nuevo importe por cobrar.

## 8. Guardería y peluquería

- **RN-GUA-01. Capacidad diaria:** la guardería no puede admitir más mascotas que el cupo máximo configurado para una fecha. Se cuentan las mascotas con guardería no cancelada para ese día, incluyendo pendientes, confirmadas y atendidas. No se calcula capacidad por franjas horarias.
- **RN-GUA-02. Cupo positivo:** el cupo máximo debe ser mayor que cero.
- **RN-GUA-03. Liberación:** cancelar la reserva libera el lugar. La salida anticipada de una mascota no habilita otra admisión que supere el total diario.
- **RN-GUA-04. Fecha de cupo:** la fecha del detalle de guardería identifica el día al que se imputa el cupo. Su intervalo describe la estadía prevista, sin habilitar reutilización horaria del lugar. El alcance corresponde a guardería de día.
- **RN-GUA-05. Atención compatible:** una mascota alojada puede recibir consulta, vacunación o peluquería durante la estadía, dentro del mismo turno o mediante otro turno, respetando la disponibilidad de los recursos de esas atenciones.
- **RN-GUA-06. Reserva duplicada:** no se registra una segunda reserva no cancelada de guardería para la misma mascota en la misma fecha.
- **RN-PEL-01. Indicaciones:** las indicaciones de peluquería se registran como observaciones opcionales en el `DetalleTurno`. No se define un catálogo cerrado `TipoCorte` ni la operación `validarTipoCorte()`.
- **RN-PEL-02. Registro médico:** la peluquería no genera obligatoriamente un registro médico.
- **RN-PEL-03. Agenda:** la peluquería reserva el intervalo de atención de la mascota, pero no requiere ni bloquea a un veterinario. No se incorpora una agenda de peluqueros en este alcance.

## 9. Excepciones de dominio previstas

- `DatosInvalidosException`: datos obligatorios ausentes o inválidos.
- `EntidadInactivaException`: intento de operar con mascota, veterinario, servicio o vacuna inactivos.
- `TurnoSinServiciosException`: intento de crear un turno vacío.
- `TurnoSolapadoException`: veterinario o mascota sin disponibilidad.
- `TransicionEstadoInvalidaException`: cambio de estado no permitido.
- `CancelacionFueraDeTerminoException`: cancelación con 24 horas o menos.
- `VacunaVigenteException`: intento de reaplicar una vacuna vigente.
- `CupoGuarderiaAgotadoException`: cupo diario agotado.
- `ServicioRepetidoException`: intento de incorporar nuevamente la misma identidad de servicio a un turno.
- `VeterinarioRequeridoException`: falta de profesional en un turno con consulta o vacunación.

## 10. Aclaraciones del docente y decisiones adoptadas — 22/10/2026

Se le consultó al profesor sobre el cupo diario de mascotas, si era por capacidad física o franja horaria. Según su respuesta se decidió que el cupo se interpreta por día respetando la capacidad física posible de albergar mascotas. Con respecto a si los servicios debían asignarse únicamente dependiendo de la especialidad del veterinario se aclaró que las especialidades son informativas y el veterinario no participa en guardería ni peluquería.

Para reflejar esas aclaraciones se conserva `Turno` con sus servicios, sin crear `ReservaGuarderia`. Se asigna un inicio propio a cada detalle, se deriva el intervalo general y se valida la disponibilidad según el recurso requerido. Estas son decisiones de diseño adoptadas por la alumna, no indicaciones textuales del profesor.

Las consultas de guardería antes pendientes quedan resueltas con este criterio. La implementación Java todavía no comenzó.


