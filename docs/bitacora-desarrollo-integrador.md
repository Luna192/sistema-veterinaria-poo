# Bitácora de desarrollo — Trabajo Integrador de Programación Orientada a Objetos I

## Sistema de Gestión Veterinaria

**Cátedra:** Programación Orientada a Objetos I  
**Docente:** Claudio Omar Biale  
**Alumna:** Acosta Araceli Luna  
**Periodo documentado:** agosto–octubre de 2026  
**Última actualización:** 22 de octubre de 2026

---

## 1. Objetivo de esta bitácora

Este documento registra el proceso seguido para analizar, diseñar y posteriormente implementar el Trabajo Integrador Final. Su finalidad es mostrar la evolución del proyecto, las decisiones tomadas, las modificaciones realizadas y la justificación conceptual de cada cambio.

La bitácora diferencia tres estados:

- **Analizado:** el problema fue estudiado y se tomó una decisión de diseño.
- **Documentado:** la decisión ya fue incorporada al modelo, reglas o casos de uso.
- **Implementado:** la decisión fue traducida a código y verificada mediante pruebas.

Actualmente el proyecto se encuentra principalmente en las etapas de análisis y diseño. La implementación en Java todavía no comenzó.

---

## 2. Resumen de etapas

| Etapa | Objetivo | Resultado principal | Estado |
|---|---|---|---|
| 1. Fundamentos de POO | Comprender los conceptos necesarios antes de diseñar | Objetos, clases, estado, comportamiento, identidad y encapsulamiento | Completada como base; estudio continuo |
| 2. Análisis de la consigna | Identificar requisitos, restricciones y entregables | Resumen del dominio, tecnologías y reglas obligatorias | Completada |
| 3. Descubrimiento de clases | Determinar qué conceptos merecen ser clases | Primer conjunto de entidades y servicios | Completada |
| 4. Asociaciones y multiplicidades | Representar cómo se relacionan los objetos | Relaciones Cliente–Mascota, Mascota–Turno y Veterinario–Turno | Completada |
| 5. Modelado de turnos | Ubicar estados, duración, precios y solapamientos | Turno, DetalleTurno, EstadoTurno y GestorTurnos | Completada |
| 6. Jerarquía de servicios | Aplicar abstracción, herencia y polimorfismo | Servicio abstracto y cuatro subclases | Completada |
| 7. Historial y vacunación | Separar catálogo, servicio y hecho histórico | RegistroMedico, Vacuna y AplicacionVacuna | Completada |
| 8. Revisión del UML | Corregir responsabilidades y tipos de relaciones | Modelo de dominio de trabajo en PlantUML e imagen | Completada; sujeto a ajustes |
| 9. Definición de pantallas | Reducir el alcance sin perder el flujo central | Tres pantallas principales | Completada |
| 10. Reglas y casos de uso | Convertir el modelo en comportamiento verificable | Reglas numeradas y diez casos de uso | Primera versión completada |
| 11. Preparación técnica | Crear repositorio y proyecto Maven | Repositorio creado; Maven pendiente | En curso |
| 12. Implementación | Programar dominio, persistencia y JavaFX | Aplicación ejecutable | Pendiente |

---

## 3. Desarrollo de las etapas

### Etapa 1 — Revisión de los fundamentos de POO

Antes de diseñar el sistema se trabajaron conceptos fundamentales:

- Diferencia entre clase y objeto.
- Identidad, estado y comportamiento.
- Constructores y creación mediante `new`.
- Asignación con `=` y comparación.
- Referencias: dos variables pueden apuntar al mismo objeto.
- Métodos consultores y modificadores.
- Validación de datos dentro de las clases.
- Encapsulamiento y riesgos de los atributos públicos.
- Asociaciones entre objetos.

**Motivo de esta etapa:** comprender estas ideas era necesario para no construir clases que fueran solamente grupos de atributos y `setters` sin reglas propias.

---

### Etapa 2 — Análisis de la consigna

Se revisó el enunciado completo y se identificaron los siguientes requisitos:

- Aplicación de escritorio con JavaFX.
- Java 17 o superior.
- Proyecto Maven.
- Persistencia mediante JPA e Hibernate.
- Base de datos relacional; H2 es la alternativa recomendada.
- Arquitectura MVC con capa de servicios o repositorios.
- Modelo de dominio rico.
- Uso de `java.time`, enumeraciones, `Optional` y excepciones propias.
- Gestión de clientes, mascotas, turnos, servicios, historial y vacunaciones.
- Entrega de código, diagrama UML y documento de decisiones.

También se identificaron reglas obligatorias, como la pertenencia de la mascota a un cliente, el mínimo de un servicio por turno, el control de solapamientos, la máquina de estados, la cancelación con más de 24 horas y la conservación de precios históricos.

---

### Etapa 3 — Identificación inicial de clases

Se aplicaron preguntas de análisis:

1. ¿El concepto tiene identidad propia?
2. ¿Tiene información que debe conservarse?
3. ¿Posee comportamiento o reglas propias?
4. ¿Participa en relaciones relevantes?
5. ¿Necesitamos consultar su historial?

El primer conjunto de conceptos incluyó:

- Cliente.
- Mascota.
- Veterinario.
- Turno.
- Servicio.
- Vacuna.
- Registro médico.

Posteriormente se agregaron clases necesarias para representar hechos históricos y relaciones con datos propios:

- DetalleTurno.
- AplicacionVacuna.
- GestorTurnos.

---

### Etapa 4 — Relación entre Cliente y Mascota

Se definió:

```text
Cliente 1 ───────── 0..* Mascota
```

Interpretación:

- Un cliente puede existir sin mascotas registradas.
- Un cliente puede tener muchas mascotas.
- Cada mascota debe pertenecer exactamente a un cliente.

#### Modificación realizada

La primera idea era recibir una mascota ya creada:

```java
cliente.agregarMascota(mascota);
```

Esto obligaba a comprobar si el objeto ya pertenecía a otro cliente. Se observó que esa comprobación resultaba innecesaria para el flujo normal, porque la mascota estaba siendo registrada por primera vez.

Se reemplazó por:

```java
cliente.registrarMascota(numeroFicha, nombre, especie, raza, fechaNacimiento, peso);
```

La operación crea la mascota con el cliente correcto y establece los dos extremos de la relación en una única acción.

**Justificación:** la asociación nace consistente y no puede existir temporalmente una mascota nueva sin responsable.

---

### Etapa 5 — Modelado de Turno

Se determinó que un turno debe conocer:

- Fecha y hora.
- Estado.
- Mascota.
- Veterinario.
- Servicios contratados.

No se agregó un atributo `cliente` en `Turno`, porque el responsable puede conocerse mediante:

```text
Turno → Mascota → Cliente
```

Agregarlo nuevamente duplicaría información y permitiría contradicciones.

#### Estados del turno

Se evitó utilizar un `setEstado(...)` público. En su lugar se definieron operaciones significativas:

- `confirmar()`.
- `cancelar(fechaActual)`.
- `atender()`.

Flujo permitido:

```text
PENDIENTE → CONFIRMADO → ATENDIDO
     └───────────────→ CANCELADO
              CONFIRMADO → CANCELADO
```

Los estados `ATENDIDO` y `CANCELADO` son terminales.

#### Control de solapamientos

Se concluyó que un objeto `Turno` puede comparar su intervalo con otro turno, pero no conoce por sí mismo todos los turnos existentes. Por eso se agregó `GestorTurnos`, un servicio de dominio responsable de consultar y validar la disponibilidad de la mascota y del veterinario.

---

### Etapa 6 — Incorporación de DetalleTurno

Inicialmente podía pensarse en una relación directa entre `Turno` y `Servicio`. Sin embargo, la consigna exige conservar el precio vigente al momento de crear el turno.

Se incorporó `DetalleTurno`:

```text
Turno 1 ◆──────── 1..* DetalleTurno
DetalleTurno * ───────── 1 Servicio
```

Cada detalle conserva:

- Servicio seleccionado.
- Precio congelado.
- Duración congelada.

**Justificación:** si el precio actual del servicio cambia, un turno anterior mantiene el importe histórico correcto.

La relación entre `Turno` y `DetalleTurno` se modeló como composición porque el detalle no tiene sentido independientemente del turno al cual pertenece.

---

### Etapa 7 — Jerarquía de servicios

Se decidió utilizar una clase abstracta:

```text
Servicio
├── ConsultaMedica
├── ServicioVacunacion
├── Guarderia
└── Peluqueria
```

`Servicio` concentra atributos comunes:

- Nombre.
- Precio base.
- Duración.
- Estado activo.

Y comportamiento común:

- Actualizar precio.
- Calcular importe.
- Informar duración.
- Dar de baja.
- Determinar si requiere registro médico.

Cada subclase incorpora reglas particulares:

- `ConsultaMedica`: genera diagnóstico y tratamiento.
- `ServicioVacunacion`: valida la periodicidad de una vacuna.
- `Guarderia`: controla cupo.
- `Peluqueria`: no requiere registro médico obligatorio. Se retiró la validación del tipo de corte; las indicaciones se conservan como observaciones opcionales del detalle de atención.

También se incorporó la interfaz `Facturable`, implementada por `Servicio` y `Turno`, para representar la capacidad común de calcular un importe.

**Conceptos aplicados:** abstracción, herencia, sobrescritura, interfaz y polimorfismo.

---

### Etapa 8 — Historial médico y vacunaciones

#### Historial médico

Se analizó si `HistorialMedico` debía existir como una entidad contenedora. Se decidió que el historial puede reconstruirse consultando:

- Turnos atendidos.
- Detalles de servicios.
- Registros médicos.
- Aplicaciones de vacunas.

Por ello no se incorporó una clase contenedora `HistorialMedico`. Sí se mantuvo `RegistroMedico`, asociado a la atención concreta que lo originó.

#### Separación de conceptos de vacunación

Se distinguieron tres responsabilidades:

| Concepto | Responsabilidad |
|---|---|
| `Vacuna` | Catálogo: nombre, enfermedad y periodicidad |
| `ServicioVacunacion` | Servicio ofrecido y facturable |
| `AplicacionVacuna` | Dosis concreta recibida por una mascota |

#### Modificación más reciente

Se aclaró que `ServicioVacunacion` mantiene una asociación con `Vacuna`, pero solamente una dependencia de creación con `AplicacionVacuna`:

```text
ServicioVacunacion ─────→ Vacuna
ServicioVacunacion - - -→ AplicacionVacuna : valida y crea
```

`AplicacionVacuna` se asocia permanentemente con `Mascota`, `Vacuna` y el `Veterinario` responsable, y puede estar vinculada opcionalmente a un `Turno`.

El modelo PlantUML explicita las dependencias de creación de `ConsultaMedica` hacia `RegistroMedico` y de `ServicioVacunacion` hacia `AplicacionVacuna`.

**Justificación:** el servicio no contiene las aplicaciones históricas; las valida y crea cuando se realiza la vacunación.

---

### Etapa 9 — Definición del alcance visual

Se eligieron tres pantallas principales:

1. **Clientes y mascotas:** alta, consulta, modificación y baja lógica.
2. **Gestión de turnos:** programación, confirmación, atención y cancelación.
3. **Historial médico y vacunaciones:** antecedentes, diagnósticos, tratamientos, aplicaciones y alertas de vencimiento.

No tendrán pantalla propia:

- Administración de veterinarios.
- Administración del catálogo de servicios.
- Administración del catálogo de vacunas.

Estos conceptos igualmente permanecen en el modelo y en la persistencia. Sus datos podrán cargarse mediante un script de inicialización.

#### Consulta realizada al docente

Se consultó si los módulos sin pantalla debían continuar implementados en el modelo. La respuesta fue:

> Deben estar porque son parte de la solución.

Por lo tanto, reducir la cantidad de pantallas no elimina las clases necesarias para que el sistema funcione.

---

### Etapa 10 — Reglas de negocio y casos de uso

Se redactó una primera versión de reglas numeradas para:

- Clientes y mascotas.
- Veterinarios.
- Servicios.
- Turnos y estados.
- Historial médico.
- Vacunaciones.
- Guardería y peluquería.

También se definieron diez casos de uso distribuidos entre las tres pantallas:

- Registrar cliente.
- Registrar mascota.
- Modificar o dar de baja una mascota.
- Programar turno.
- Confirmar turno.
- Cancelar turno.
- Atender turno.
- Consultar historial.
- Consultar vencimientos.
- Registrar una vacunación directamente.

---

## 4. Registro resumido de modificaciones

| Versión inicial | Modificación | Motivo |
|---|---|---|
| `agregarMascota(Mascota)` | `registrarMascota(datos...)` | Crear la asociación correctamente desde el alta |
| Validar dueño anterior de una mascota nueva | Eliminación de esa validación del flujo de alta | El objeto todavía no existía en el sistema |
| Relación directa Turno–Servicio | Incorporación de `DetalleTurno` | Conservar precio y duración históricos |
| Estado modificable mediante `setEstado` | Métodos `confirmar`, `cancelar` y `atender` | Proteger la máquina de estados |
| Disponibilidad dentro de un turno aislado | Incorporación de `GestorTurnos` | La validación necesita consultar otros turnos |
| Una única idea de vacunación | Separación en `Vacuna`, `ServicioVacunacion` y `AplicacionVacuna` | Diferenciar catálogo, servicio y hecho histórico |
| Posible entidad `HistorialMedico` | Historial calculado desde atenciones y vacunas | Evitar un contenedor redundante |
| Asociación permanente ServicioVacunacion–AplicacionVacuna | Dependencia de creación | El servicio crea aplicaciones, pero no las contiene |
| Eliminación física de registros | Baja lógica y estados terminales | Preservar antecedentes e informes |
| Cinco módulos sugeridos con pantalla | Tres pantallas prioritarias | Mantener un alcance realizable y un flujo completo |

---

## 5. Modelo de dominio actual

El modelo de trabajo contiene:

- `Cliente`.
- `Mascota`.
- `Veterinario`.
- `Turno`.
- `DetalleTurno`.
- `Servicio` como clase abstracta.
- `ConsultaMedica`.
- `ServicioVacunacion`.
- `Guarderia`.
- `Peluqueria`.
- `RegistroMedico`.
- `Vacuna`.
- `AplicacionVacuna`.
- `GestorTurnos` como servicio de dominio.
- `Facturable` como interfaz.
- `EstadoTurno`, `Especie` y `Especialidad` como enumeraciones.

---

## 6. Revisión de decisiones — 22/10/2026

### Decisiones acordadas

| Tema | Decisión | Motivo |
|---|---|---|
| Reserva y confirmación | Todo turno nace `PENDIENTE`. Se permite guardar pendiente o crear y confirmar en una misma transacción, tras la aceptación del cliente. Ambos estados guardados reservan horario. | Conservar el flujo exigido y permitir el registro habitual por el personal. |
| Clientes sin reserva previa | Se registra un turno para el momento de atención, comprobando disponibilidad y confirmándolo. | Conservar servicios, duración e importe aun sin una reserva anticipada. |
| Vacunaciones | Toda aplicación identifica al veterinario responsable. La clínica registra sus prestaciones mediante turnos; el registro directo incorpora aplicaciones anteriores sin generar un nuevo importe. | Identificar al profesional y diferenciar antecedentes de prestaciones actuales. |
| Servicios repetidos | Se impide repetir la identidad de un servicio dentro del turno; se permiten servicios diferentes. | Evitar duplicaciones accidentales de precio y duración. |
| Especialidades | Son informativas y no restringen la asignación de servicios. | No incorporar una restricción que el enunciado no exige. |
| Peluquería | Se retira `validarTipoCorte()` y no se incorpora `TipoCorte`. Las indicaciones se registran como observaciones opcionales de `DetalleTurno`. | Conservar las indicaciones de cada atención sin crear un catálogo no requerido. |

### Consultas pendientes para el docente

1. ¿El cupo diario de guardería limita el total de mascotas recibidas en el día o la cantidad alojada simultáneamente? Con capacidad para diez, ¿pueden recibirse diez por la mañana y otras diez por la tarde si las estadías no se superponen?
2. ¿La guardería bloquea la agenda del veterinario durante toda la estadía o deben distinguirse el horario de alojamiento y el de atención profesional?

El atributo de cupo diario permanece provisional. Hasta obtener respuesta, no se incorpora una excepción a la regla general de no solapamiento del veterinario.

**Archivos afectados:** modelo PlantUML, reglas de negocio, casos de uso y esta bitácora. Las decisiones corresponden al diseño; todavía no se implementaron en Java.

---

## 7. Estado actual del proyecto

| Componente | Estado actual |
|---|---|
| Comprensión inicial de POO | Trabajada; requiere repaso continuo |
| Consigna analizada | Completa |
| Modelo de dominio | Versión de trabajo completa |
| Relaciones y multiplicidades | Definidas |
| Reglas de negocio | Primera versión documentada |
| Casos de uso | Primera versión documentada |
| Alcance de pantallas | Definido |
| Repositorio GitHub | Creado y documentado: `Luna192/sistema-veterinaria-poo` |
| Proyecto Maven | Pendiente |
| Clases Java | Pendientes |
| Pruebas | Pendientes |
| Persistencia JPA/H2 | Pendiente |
| Interfaz JavaFX | Pendiente |

---

## 8. Próximas etapas previstas

1. Revisar y aprobar las reglas de negocio y casos de uso.
2. Actualizar el diagrama UML con las últimas dependencias.
3. Generar la estructura Maven y los paquetes.
4. Implementar enumeraciones y excepciones.
5. Implementar el modelo de dominio sin interfaz gráfica.
6. Crear pruebas para las reglas principales.
7. Incorporar JPA, Hibernate y H2.
8. Implementar repositorios y servicios de aplicación.
9. Construir las tres pantallas JavaFX.
10. Integrar, probar y preparar la entrega final.

---

## 9. Criterio de actualización de la bitácora

La bitácora se actualizará después de cada avance relevante. Cada nueva entrada deberá indicar:

- Fecha.
- Etapa trabajada.
- Decisión o modificación.
- Motivo del cambio.
- Archivos afectados.
- Estado de verificación.

De esta manera, el repositorio no mostrará solamente el resultado final, sino también la evolución razonada del integrador.

