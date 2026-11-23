# Plan de implementación — Objetivo: 10 de diciembre de 2026

**Alumnos:** Acosta Araceli Luna  y Braga Juan Ignacio
**Docente:** Claudio Omar Biale  
**Inicio de implementación:** 29 de octubre de 2026

Se dispone de seis semanas. Con una dedicación de 6–8 horas semanales, el presupuesto es de aproximadamente 36–48 horas. El objetivo requiere mantener el alcance de tres pantallas, validar cada incremento y reservar la última semana para integración y defensa. El calendario es una orientación general, no una garantía de finalización ni un inventario que deba actualizarse por cada modificación individual del código.

## Etapas generales

### 1. Modelo de dominio en Java

Implementar las entidades, enumeraciones, relaciones, asociaciones y comportamientos definidos por el diagrama vigente en `docs/dc.json` y su representación visual.

El modelo actual debe mantenerse compilable con Java 17, utilizando Maven Wrapper y las excepciones del dominio previstas. La persistencia y las interfaces gráficas no forman parte de esta etapa.

### 2. Lógica de negocio

Implementar y validar las reglas descritas en `docs/reglas-negocio.md`, respetando las responsabilidades de cada entidad y las decisiones vigentes del diagrama.

Esta etapa incluye estados y transiciones de turnos, precios y duraciones históricos, disponibilidad de veterinario y mascota, cupos de guardería, vacunaciones e información clínica asociada a las prestaciones.

### 3. Persistencia

En una etapa posterior se incorporarán JPA/Hibernate y H2 en archivo, repositorios y transacciones. Esta etapa deberá conservar las asociaciones, identificadores técnicos, baja lógica vigente y datos históricos sin pérdida.

No debe anticiparse persistencia mientras el proyecto se encuentre concentrado en el modelo de dominio y su lógica.

### 4. Interfaz gráfica

En una etapa posterior se desarrollará una aplicación de escritorio con JavaFX y tres pantallas:

- Clientes y mascotas.
- Turnos.
- Historial médico y vacunaciones.

La interfaz deberá apoyarse en las operaciones del dominio sin duplicar reglas de negocio ni modificar directamente el estado de las entidades.

### 5. Integración y pruebas

Realizar pruebas unitarias e integrales, validar los recorridos principales, ejecutar desde otra carpeta, revisar el UML y preparar el material final de entrega y defensa.

Cada incremento debe compilar, superar las pruebas correspondientes y quedar documentado.

## Cronograma orientativo

| Periodo | Objetivo general |
|---|---|
| 29/10–04/11 | Configuración inicial y primeras entidades del dominio. |
| 05/11–11/11 | Servicios, turnos y reglas de disponibilidad. |
| 12/11–18/11 | Persistencia con JPA/Hibernate y H2. |
| 19/11–25/11 | Interfaz JavaFX y recorridos principales. |
| 26/11–02/12 | Funcionalidades finales, validaciones y corrección de errores. |
| 03/12–09/12 | Pruebas integrales, revisión de UML y preparación de entrega. |
| 10/12 | Sistema listo y material de entrega revisado. |

## Decisiones vigentes

- El modelo vigente se define por `docs/dc.json` y su representación visual.
- `docs/reglas-negocio.md` complementa el diagrama con comportamientos y validaciones.
- `DetalleTurno` conserva el precio y la duración histórica de cada prestación.
- El diagnóstico y el tratamiento se registran en `DetalleTurno`; no existe una entidad independiente llamada `RegistroMedico`.
- Las consultas y vacunaciones requieren veterinario; guardería y peluquería no.
- La peluquería registra indicaciones como observaciones opcionales de `DetalleTurno` y no utiliza un catálogo cerrado de tipos de corte.
- El cupo de guardería corresponde a mascotas por día. Cuando una mascota se retira, su lugar queda liberado; la coordinación de reservas corresponde a la lógica encargada de gestionar los turnos.
- El peso no forma parte del modelo vigente de `Mascota`.
- El modelo vigente no contempla baja lógica de `Cliente` ni de `Veterinario`; la baja lógica permanece únicamente donde el diagrama y las reglas la definan.

## Antecedentes históricos reemplazados

Las siguientes ideas formaron parte de diseños anteriores, pero no constituyen requisitos vigentes:

- Peso positivo de `Mascota`.
- Baja lógica de `Cliente`.
- Baja lógica de `Veterinario`.
- Una clase independiente `RegistroMedico`.
- Una clase `TipoCorte`.
- Interpretación de guardería como reutilizable por franja horaria.

Estas decisiones pueden conservarse como antecedente histórico en la bitácora, pero no deben guiar nuevas implementaciones.
