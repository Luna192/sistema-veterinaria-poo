# Sistema de Gestión Veterinaria — Reglas de negocio

## 1. Propósito y alcance

Este documento reúne las reglas de negocio vigentes del Sistema de Gestión Veterinaria.

La versión actual se adapta a la última revisión del diagrama de clases. Por lo tanto, el diagrama vigente es la referencia principal para definir la estructura del modelo, sus atributos, métodos, relaciones y responsabilidades.

Las reglas identificadas con `RN` describen comportamientos que debe respetar el sistema. No todas las reglas tienen que estar representadas como atributos o métodos: algunas corresponden a validaciones y decisiones de la lógica de negocio.

Cuando una decisión anterior contradiga el modelo vigente, prevalece la decisión más reciente. Los puntos que todavía no estén definidos se identifican expresamente como pendientes, sin asumir comportamientos no acordados.

## 2. Clientes y mascotas

### Clientes

- **RN-CLI-01. Identidad técnica:** cada cliente tendrá un `id` técnico único generado al persistirse.
- **RN-CLI-02. Datos obligatorios:** nombre, apellido y DNI no pueden ser nulos ni quedar en blanco.
- **RN-CLI-03. Unicidad del DNI:** la política de unicidad del DNI debe respetarse al registrar clientes. Queda pendiente confirmar si se aplica sobre todos los clientes registrados o si existen excepciones.
- **RN-CLI-04. Datos de contacto:** el cliente dispone de teléfono y correo electrónico. La validación de sus formatos y la exigencia de contar con al menos uno de los dos medios quedan sujetas a confirmación.
- **RN-CLI-05. Registro sin mascotas:** un cliente puede registrarse sin tener mascotas asociadas.
- **RN-CLI-06. Sin baja lógica:** el modelo vigente no contempla una operación de baja lógica para `Cliente`.

### Mascotas

- **RN-MAS-01. Propietario obligatorio:** una mascota debe pertenecer exactamente a un cliente existente.
- **RN-MAS-02. Alta consistente:** la mascota se registra mediante `Cliente.registrarMascota(...)`, estableciendo la referencia al cliente y agregándola a su colección.
- **RN-MAS-03. Número de ficha único:** cada mascota tendrá un número de ficha veterinaria único, independiente de su `id` técnico.
- **RN-MAS-04. Nombres repetidos:** distintas mascotas pueden tener el mismo nombre; el nombre no constituye su identidad.
- **RN-MAS-05. Fecha de nacimiento válida:** la fecha de nacimiento no puede ser posterior a la fecha actual.
- **RN-MAS-06. Edad derivada:** la edad se obtiene a partir de la fecha de nacimiento y no debe almacenarse como un dato permanente.
- **RN-MAS-07. Baja lógica:** una mascota puede darse de baja sin eliminar físicamente sus datos ni su historial.
- **RN-MAS-08. Mascota inactiva:** una mascota dada de baja conserva sus turnos y aplicaciones de vacunas anteriores, pero no puede participar en nuevas operaciones que requieran una mascota activa.

El peso no forma parte de los atributos de `Mascota` en el diagrama vigente y, por lo tanto, no constituye una regla del modelo actual.

## 3. Veterinarios

- **RN-VET-01. Matrícula:** el veterinario posee una matrícula profesional.
- **RN-VET-02. Especialidades:** un veterinario puede tener una o más especialidades. No deben repetirse dentro de su colección.
- **RN-VET-03. Disponibilidad:** un veterinario no puede participar en prestaciones veterinarias con intervalos superpuestos. Se comparan los detalles de consulta y vacunación, no la estadía ni el intervalo general del turno.
- **RN-VET-04. Sin baja lógica:** el modelo vigente no contempla una operación de baja lógica para `Veterinario`.
- **RN-VET-05. Alcance de las especialidades:** las especialidades son información profesional y no restringen los servicios que pueden asignarse al veterinario.

La validación de obligatoriedad y unicidad de la matrícula deberá respetar los requisitos vigentes del proyecto.

## 4. Servicios

- **RN-SER-01. Servicio válido:** todo servicio debe tener nombre, precio base no negativo y duración positiva en minutos.
- **RN-SER-02. Facturación:** todo servicio implementa el comportamiento facturable mediante `Facturable`.
- **RN-SER-03. Duración:** cada servicio aporta una duración que se conserva en su detalle. La suma de esas duraciones expresa el total de prestaciones, pero no determina por sí sola la ocupación del veterinario ni necesariamente el tiempo transcurrido del turno.
- **RN-SER-04. Tipos concretos:** no se crean instancias directas de `Servicio`, que es una clase abstracta. Se utilizan sus subclases concretas: `ConsultaMedica`, `ServicioVacunacion`, `Guarderia` y `Peluqueria`.
- **RN-SER-05. Comportamiento polimórfico:** cada tipo de servicio define su comportamiento mediante los métodos correspondientes, incluyendo si requiere registro médico, si requiere veterinario y si admite una especie determinada.
- **RN-SER-06. Participación profesional:** `requiereVeterinario()` devuelve verdadero para consulta y vacunación y falso para guardería y peluquería. Este comportamiento permite determinar cuándo es obligatoria la asignación de un veterinario.
- **RN-SER-07. Especies admitidas:** cada tipo de servicio puede definir qué especies admite mediante `admiteEspecie(especie)`.

No se contempla en el modelo vigente una baja lógica general de los servicios.

## 5. Turnos

### Creación y contenido

- **RN-TUR-01. Mascota:** todo turno corresponde a una mascota.
- **RN-TUR-02. Veterinario:** el turno debe tener un veterinario cuando incluye prestaciones que requieren su participación, como consulta o vacunación. Los servicios que no requieren veterinario no deben exigir esa asignación.
- **RN-TUR-03. Servicios obligatorios:** todo turno contiene al menos un detalle de servicio; no existen turnos vacíos.
- **RN-TUR-04. Fecha de inicio:** las reservas se programan para fechas y horas futuras. Para una atención sin reserva previa se permite utilizar el momento actual, comprobando la disponibilidad de los recursos correspondientes. No se programan prestaciones nuevas con inicio pasado.
- **RN-TUR-05. Estado inicial:** todo turno nuevo comienza en estado `PENDIENTE`.
- **RN-TUR-06. Duración total:** `calcularDuracionTotal()` suma las duraciones congeladas de los detalles del turno.
- **RN-TUR-07. Inicio y final:** cada detalle conserva su propio horario. El inicio más temprano y el final más tardío de sus prestaciones permiten determinar el intervalo general del turno.
- **RN-TUR-08. Importe:** el importe del turno es la suma de los precios congelados de todos sus detalles.
- **RN-TUR-09. Precio histórico:** cada `DetalleTurno` conserva el precio que tenía el servicio al incorporarse al turno.
- **RN-TUR-10. Duración histórica:** cada `DetalleTurno` conserva también la duración utilizada al programar la prestación.
- **RN-TUR-11. Servicios no repetidos:** un turno admite distintos servicios, pero no dos detalles con la misma identidad de servicio. La comparación no se realiza por el nombre ni solamente por el tipo de servicio. Las vacunaciones de vacunas diferentes pueden coexistir si cada una cumple sus reglas de aplicación.
- **RN-TUR-12. Atención sin reserva:** si el cliente llega sin reserva previa y existe disponibilidad, el personal crea y confirma un turno para el momento de la atención. Los servicios y sus importes se registran mediante los detalles correspondientes.

### Solapamientos

- **RN-TUR-13. Solapamiento del veterinario:** se comparan únicamente los intervalos de detalles que requieren veterinario, tanto dentro del turno como frente a otras prestaciones asignadas al mismo profesional. Guardería y peluquería no reservan la agenda del veterinario, aunque pertenezcan a un turno mixto.
- **RN-TUR-14. Solapamiento de la mascota:** las consultas, vacunaciones y peluquerías de una misma mascota no pueden superponerse entre sí, dentro de un turno ni entre turnos diferentes. Una estadía de guardería puede contener esas atenciones sin provocar un conflicto de disponibilidad.
- **RN-TUR-15. Intervalos contiguos:** si un detalle termina exactamente cuando comienza otro, no existe solapamiento. La comparación utiliza intervalos con inicio incluido y final excluido.
- **RN-TUR-16. Turnos cancelados:** los turnos cancelados liberan los horarios y no participan en la validación de solapamientos.
- **RN-TUR-17. Coordinación:** `GestorTurnos` coordina la validación de disponibilidad porque necesita consultar el conjunto de turnos existentes.
- **RN-TUR-18. Horario de cada prestación:** cada `DetalleTurno` guarda su fecha y hora de inicio. Su final se calcula sumando la duración congelada. Los servicios pueden tener intervalos diferentes y existir esperas entre ellos.

### Estados

- **RN-EST-01. Confirmación:** solamente un turno `PENDIENTE` puede pasar a `CONFIRMADO`.
- **RN-EST-02. Atención:** solamente un turno `CONFIRMADO` puede pasar a `ATENDIDO`.
- **RN-EST-03. Cancelación:** un turno `PENDIENTE` o `CONFIRMADO` puede pasar a `CANCELADO` si faltan más de 24 horas respecto al inicio del turno.
- **RN-EST-04. Límite exacto:** si faltan exactamente 24 horas o menos, no se permite cancelar, siguiendo la condición de más de veinticuatro horas.
- **RN-EST-05. Estado atendido terminal:** un turno `ATENDIDO` no puede cambiar a otro estado.
- **RN-EST-06. Estado cancelado terminal:** un turno `CANCELADO` no puede reactivarse.
- **RN-EST-07. Conservación:** cancelar modifica el estado; nunca elimina físicamente el turno.
- **RN-EST-08. Registro y confirmación:** la operación de registro debe permitir guardar un turno pendiente o guardarlo y confirmarlo. En la segunda opción se crea el turno `PENDIENTE`, se ejecuta `confirmar()` tras la aceptación del cliente y se guarda todo en una misma transacción. Nunca se omite la transición a `CONFIRMADO`.

## 6. Consulta e información médica

- **RN-MED-01. Consulta médica:** `ConsultaMedica` es el servicio que requiere el registro de información clínica.
- **RN-MED-02. Información clínica:** el diagnóstico y el tratamiento se registran en los atributos correspondientes de `DetalleTurno`.
- **RN-MED-03. Diagnóstico obligatorio:** antes de marcar como atendido un turno que incluya una consulta médica, debe registrarse un diagnóstico válido.
- **RN-MED-04. Tratamiento opcional:** el tratamiento puede quedar vacío cuando el profesional determine que no corresponde.
- **RN-MED-05. Historial derivado:** el historial de una mascota se obtiene de sus turnos y de las aplicaciones de vacunas. No se necesita una entidad contenedora independiente llamada `HistorialMedico`.
- **RN-MED-06. Orden:** el historial se presenta cronológicamente.
- **RN-MED-07. Conservación:** los datos clínicos asociados a prestaciones atendidas deben conservarse como parte del historial de la mascota.

El modelo vigente no contempla una clase independiente `RegistroMedico`. La información clínica se representa mediante los atributos de `DetalleTurno`.

## 7. Vacunaciones

- **RN-VAC-01. Catálogo:** una `Vacuna` define la información correspondiente a la vacuna, incluyendo su nombre, la enfermedad que previene y su periodicidad en meses.
- **RN-VAC-02. Servicio específico:** cada `ServicioVacunacion` se asocia con exactamente una vacuna.
- **RN-VAC-03. Aplicación histórica:** `AplicacionVacuna` representa una dosis concreta recibida por una mascota; no representa el catálogo ni el servicio facturable.
- **RN-VAC-04. Datos de aplicación:** una aplicación debe identificar la mascota, la vacuna, el veterinario responsable y la fecha de aplicación. También debe permitir determinar la fecha de próxima dosis.
- **RN-VAC-05. Cálculo:** la próxima dosis se calcula sumando la periodicidad de la vacuna a la fecha de aplicación.
- **RN-VAC-06. Vigencia:** no debe permitirse una nueva aplicación de la misma vacuna si la dosis anterior todavía se encuentra vigente.
- **RN-VAC-07. Asociación con turnos:** una aplicación puede estar asociada a un detalle de turno o registrarse directamente sobre la mascota, según las posibilidades del modelo.
- **RN-VAC-08. Historial:** una mascota puede tener múltiples aplicaciones y una misma vacuna puede aplicarse a distintas mascotas.
- **RN-VAC-09. Alertas:** una vacuna está próxima a vencer cuando su próxima dosis se encuentra dentro del intervalo configurable establecido por el sistema.
- **RN-VAC-10. Profesional responsable:** si la aplicación proviene de un turno, su veterinario debe coincidir con el asignado a ese turno. Para un antecedente sin turno, se identifica al profesional que realizó la aplicación.
- **RN-VAC-11. Origen e importe:** las vacunaciones realizadas por la clínica se registran mediante un turno, incluso cuando el cliente llega sin reserva previa, conservando el precio del servicio. El registro directo de aplicaciones anteriores incorpora antecedentes al historial y no genera un nuevo importe por cobrar.

La periodicidad, el cálculo de vigencia y las alertas deben respetar los datos y las relaciones definidos en el modelo vigente. La existencia de una baja lógica del catálogo de vacunas no se considera una regla confirmada en esta versión.

## 8. Guardería y peluquería

### Guardería

- **RN-GUA-01. Capacidad diaria:** la guardería tiene un cupo máximo de mascotas para cada día. No se calcula la capacidad por franjas horarias.
- **RN-GUA-02. Cupo positivo:** el cupo máximo debe ser mayor que cero.
- **RN-GUA-03. Liberación del lugar:** cuando una mascota se retira de la guardería, el lugar que ocupaba queda liberado y puede utilizarse para admitir otra mascota, siempre respetando el cupo máximo.
- **RN-GUA-04. Fecha de cupo:** la fecha del detalle de guardería identifica el día al que se imputa la ocupación. El intervalo representa la estadía prevista.
- **RN-GUA-05. Atención compatible:** una mascota alojada puede recibir consulta, vacunación o peluquería durante la estadía, dentro del mismo turno o mediante otro turno, respetando la disponibilidad de los recursos de esas atenciones.
- **RN-GUA-06. Reserva duplicada:** no debe registrarse una segunda reserva de guardería para la misma mascota y fecha mientras exista una reserva vigente que no haya sido cancelada ni finalizada mediante su retiro.

La capacidad se comprueba mediante el comportamiento de `Guarderia`, incluyendo `tieneCupo(cantidadOcupada)`. La coordinación de las reservas y de la ocupación corresponde a la lógica encargada de gestionar los turnos.

### Peluquería

- **RN-PEL-01. Indicaciones:** las indicaciones de peluquería se registran como observaciones opcionales en `DetalleTurno`. No se define un catálogo cerrado `TipoCorte` ni la operación `validarTipoCorte()`.
- **RN-PEL-02. Registro médico:** la peluquería no genera obligatoriamente información clínica.
- **RN-PEL-03. Agenda:** la peluquería reserva el intervalo de atención de la mascota, pero no requiere ni bloquea a un veterinario. No se incorpora una agenda de peluqueros en este alcance.
- **RN-PEL-04. Especies admitidas:** la admisión de especies se determina mediante el comportamiento `admiteEspecie(especie)` de `Peluqueria`.

## 9. Excepciones de dominio previstas

Las excepciones representan situaciones inválidas que pueden producirse al ejecutar operaciones del dominio.

- `DatosInvalidosException`: datos obligatorios ausentes o inválidos.
- `EntidadInactivaException`: intento de operar con una mascota dada de baja cuando la operación requiere que esté activa.
- `TurnoSinServiciosException`: intento de crear un turno vacío.
- `TurnoSolapadoException`: conflicto de disponibilidad de veterinario o mascota.
- `TransicionEstadoInvalidaException`: cambio de estado no permitido.
- `CancelacionFueraDeTerminoException`: intento de cancelar un turno cuando faltan 24 horas o menos para su inicio.
- `VacunaVigenteException`: intento de aplicar nuevamente una vacuna que todavía se encuentra vigente.
- `CupoGuarderiaAgotadoException`: intento de superar el cupo disponible de guardería.
- `ServicioRepetidoException`: intento de incorporar nuevamente la misma identidad de servicio a un turno.
- `VeterinarioRequeridoException`: falta de profesional en un turno que contiene prestaciones que requieren veterinario.

La lista deberá mantenerse sincronizada con las excepciones efectivamente definidas en el proyecto. No se deben introducir excepciones para representar reglas que hayan sido descartadas.

## 10. Decisiones pendientes de confirmar

Los siguientes puntos requieren una definición explícita antes de considerarse reglas definitivas:

1. **Unicidad del DNI:** confirmar si se exige para todos los clientes registrados, sin distinción de estado.
2. **Contacto del cliente:** confirmar si teléfono o correo electrónico son obligatorios y si debe existir al menos uno.
3. **Matrícula del veterinario:** confirmar los criterios exactos de obligatoriedad y unicidad.
4. **Fecha de inicio del turno:** precisar la función de `Turno.fechaHoraInicio` en relación con los horarios individuales de sus detalles.
5. **Duración transcurrida:** no se considera parte de la API vigente mientras no figure en el diagrama aprobado.
6. **Vigencia y alertas de vacunas:** confirmar el criterio exacto de vigencia y el intervalo de anticipación para las alertas.
7. **Retiro de guardería:** definir cómo se registra el retiro y cómo se actualiza la ocupación diaria para liberar el lugar.
8. **Reservas de guardería:** precisar cuándo una reserva se considera finalizada y deja de impedir una segunda reserva de la misma mascota para la misma fecha.

Las decisiones pendientes no deben implementarse como reglas definitivas hasta que sean acordadas.

## 11. Criterio de actualización documental

Esta versión reemplaza las reglas anteriores que contradigan el diagrama de clases revisado y aprobado.

Las decisiones descartadas, sus motivos y su evolución deben conservarse en la bitácora de desarrollo, pero no deben permanecer en este documento como reglas vigentes.

Ante una futura modificación del diagrama, las reglas de negocio deberán revisarse nuevamente para mantener la coherencia entre el modelo de dominio, la documentación y la implementación.