# Sistema de Gestión Veterinaria — Reglas de negocio

## 1. Clientes y mascotas

- **RN-CLI-01. Registro sin mascotas:** un cliente puede registrarse sin mascotas.
- **RN-CLI-02. Datos obligatorios:** el nombre, el apellido y el DNI son obligatorios.
- **RN-CLI-03. Datos de contacto:** debe existir al menos un medio de contacto válido: teléfono o correo electrónico.
- **RN-CLI-04. Actualización de contacto:** si falla una actualización de contacto, se conservan los datos anteriores de teléfono y correo electrónico.
- **RN-MAS-01. Propietario obligatorio:** cada mascota pertenece a un cliente y se registra mediante ese cliente.
- **RN-MAS-02. Número de ficha:** cada mascota tiene un número de ficha único. Distintas mascotas pueden tener el mismo nombre.
- **RN-MAS-03. Datos obligatorios:** el nombre, la especie y la fecha de nacimiento son obligatorios. La raza es opcional.
- **RN-MAS-04. Fecha de nacimiento:** la fecha de nacimiento no puede ser futura.
- **RN-MAS-05. Peso:** el peso no forma parte del modelo actual.
- **RN-MAS-06. Edición válida:** una edición inválida conserva todos los datos anteriores de la mascota.
- **RN-MAS-07. Baja lógica:** la baja de una mascota conserva sus datos e historial e impide nuevas atenciones y modificaciones.

## 2. Veterinarios y servicios

- **RN-VET-01. Participación profesional:** consulta médica y vacunación requieren un veterinario.
- **RN-VET-02. Servicios sin veterinario:** guardería y peluquería no requieren veterinario ni ocupan su agenda.
- **RN-VET-03. Especialidades:** las especialidades del veterinario son informativas y no restringen los servicios que puede realizar.
- **RN-SER-01. Servicio válido:** todo servicio tiene nombre obligatorio, precio no negativo y duración positiva.
- **RN-SER-02. Clase abstracta:** `Servicio` es abstracto y sus subclases definen su comportamiento.
- **RN-SER-03. Datos históricos:** cada detalle de turno conserva el precio y la duración utilizados al reservar. Los cambios posteriores del servicio no modifican esos valores.
- **RN-SER-04. Vacunación facturable:** `Vacunacion` es un `Servicio` facturable y permite calcular su importe aunque no exista un turno.
- **RN-PEL-01. Especies admitidas:** peluquería admite únicamente perros y gatos.
- **RN-PEL-02. Indicaciones:** las indicaciones de peluquería se registran como observaciones opcionales.

## 3. Turnos y horarios

- **RN-TUR-01. Participantes:** un turno corresponde a una mascota activa y contiene uno o más servicios.
- **RN-TUR-02. Horario único:** el turno tiene un único horario de inicio.
- **RN-TUR-03. Ejecución secuencial:** los servicios se realizan consecutivamente, en el orden registrado. Cada servicio comienza cuando termina el anterior.
- **RN-TUR-04. Duración total:** la duración total es la suma de las duraciones de los detalles.
- **RN-TUR-05. Finalización:** la finalización se calcula sumando la duración total al inicio del turno.
- **RN-TUR-06. Importe total:** el importe total es la suma de los precios congelados de los detalles.
- **RN-TUR-07. Servicios no repetidos:** no puede repetirse la misma identidad de servicio dentro de un turno.
- **RN-TUR-08. Pertenencia:** un detalle pertenece a un único turno.
- **RN-TUR-09. Ocupación del veterinario:** el veterinario queda ocupado únicamente durante los servicios que requieren su participación.
- **RN-TUR-10. Solapamientos:** no deben superponerse prestaciones asignadas al mismo veterinario ni atenciones incompatibles de una misma mascota.
- **RN-TUR-11. Intervalos contiguos:** dos intervalos contiguos no se solapan cuando uno termina exactamente cuando comienza el otro.
- **RN-TUR-12. Cancelación:** los turnos cancelados liberan sus reservas de horario.
- **RN-TUR-13. Horarios individuales:** no se incorporan horarios de inicio individuales en `DetalleTurno`. La ejecución secuencial se calcula acumulando las duraciones de los detalles anteriores.

## 4. Estados y cancelación

- **RN-EST-01. Estado inicial:** todo turno comienza en estado `PENDIENTE`.
- **RN-EST-02. Confirmación:** solo un turno pendiente puede confirmarse.
- **RN-EST-03. Atención:** solo un turno confirmado puede marcarse `ATENDIDO`.
- **RN-EST-04. Cancelación:** un turno pendiente o confirmado puede cancelarse con más de 24 horas de anticipación.
- **RN-EST-05. Límite de cancelación:** con exactamente 24 horas o menos, se rechaza la cancelación.
- **RN-EST-06. Estados terminales:** un turno atendido o cancelado no puede volver a otro estado.
- **RN-EST-07. Conservación:** la cancelación conserva el registro del turno.

## 5. Información médica

- **RN-MED-01. Registro clínico:** el diagnóstico y el tratamiento se registran en el detalle de consulta.
- **RN-MED-02. Servicios autorizados:** solo los servicios que requieren registro médico admiten resultados clínicos.
- **RN-MED-03. Diagnóstico obligatorio:** antes de marcar atendido un turno con consulta, debe existir un diagnóstico válido.
- **RN-MED-04. Resultado sin alteraciones:** puede registrarse «sin alteraciones» cuando corresponda.
- **RN-MED-05. Tratamiento opcional:** el tratamiento puede quedar vacío.
- **RN-MED-06. Historial:** el historial se obtiene de los turnos y aplicaciones de vacunas de la mascota, conservando los registros anteriores.

## 6. Vacunación

- **RN-VAC-01. Catálogo:** una vacuna tiene nombre comercial, enfermedad que previene y periodicidad positiva en meses.
- **RN-VAC-02. Datos de aplicación:** cada aplicación identifica mascota, vacuna, veterinario responsable y fecha.
- **RN-VAC-03. Fecha de aplicación:** la fecha de aplicación no puede ser futura ni anterior al nacimiento de la mascota.
- **RN-VAC-04. Próxima dosis:** la próxima dosis se calcula sumando la periodicidad a la fecha de aplicación.
- **RN-VAC-05. Vigencia:** la aplicación está vigente desde su fecha de aplicación hasta el día anterior a la próxima dosis.
- **RN-VAC-06. Próxima dosis vencida:** el día de la próxima dosis ya se considera vencida.
- **RN-VAC-07. Vigencia previa:** se rechaza una nueva aplicación de la misma vacuna mientras la anterior siga vigente.
- **RN-VAC-08. Antecedentes:** para comprobarlo, se consulta la última aplicación de esa mascota y vacuna, incluyendo registros con turno y sin turno.
- **RN-VAC-09. Registro directo o asociado:** una aplicación puede registrarse mediante un turno o directamente sobre la mascota.
- **RN-VAC-10. Asociación con turno:** si corresponde a un turno, se vincula al detalle de vacunación y utiliza al veterinario asignado.
- **RN-VAC-11. Una aplicación por detalle:** un detalle no puede tener dos aplicaciones vinculadas.
- **RN-VAC-12. Responsabilidad sanitaria de la aplicación:** `AplicacionVacuna` registra exclusivamente información sanitaria y no implementa `Facturable`.
- **RN-VAC-13. Registro directo:** una aplicación registrada directamente sobre la mascota no guarda su importe en `AplicacionVacuna` ni registra un pago.
- **RN-VAC-14. Importe con turno:** cuando una aplicación está vinculada a un turno, se asocia al detalle de vacunación correspondiente. Ese detalle conserva el precio histórico y lo incluye en el importe del turno.
- **RN-VAC-15. Sin pagos independientes:** no existe actualmente un mecanismo de registro o gestión de pagos independientes.
- **RN-VAC-16. Alerta de vencimiento:** la alerta de próximo vencimiento utiliza 30 días por defecto y puede configurarse.

## 7. Guardería

- **RN-GUA-01. Capacidad máxima:** la capacidad máxima es de 10 mascotas simultáneamente.
- **RN-GUA-02. Reserva:** la reserva indica fecha y hora de ingreso y de salida prevista. La salida prevista determina hasta cuándo se reserva el lugar.
- **RN-GUA-03. Disponibilidad:** la disponibilidad se comprueba para todo el intervalo solicitado, considerando las reservas existentes.
- **RN-GUA-04. Exceso de capacidad:** se rechaza cualquier reserva que provoque que haya más de 10 mascotas simultáneamente, aunque sea durante una parte de la estadía.
- **RN-GUA-05. Reservas superpuestas:** una mascota no puede tener reservas de guardería superpuestas.
- **RN-GUA-06. Reserva cancelada:** una reserva cancelada libera el espacio reservado.
- **RN-GUA-07. Intervalos contiguos:** si una estadía termina exactamente cuando comienza otra, pueden utilizar el mismo lugar.
- **RN-GUA-08. Control de cupo:** el cupo se controla por horarios, no por cantidad total de mascotas recibidas durante el día.
- **RN-GUA-09. Exclusividad durante la estadía:** mientras una mascota se encuentra en guardería, no puede recibir otros servicios durante ese intervalo de estadía.
