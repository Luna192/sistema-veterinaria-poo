# Plan de implementación — Objetivo: 10 de diciembre de 2026

**Alumna:** Acosta Araceli Luna  
**Docente:** Claudio Omar Biale  
**Inicio de implementación:** 29 de octubre de 2026

Se dispone de seis semanas. Con una dedicación de 6–8 horas semanales, el presupuesto es de aproximadamente 36–48 horas. El objetivo requiere mantener el alcance de tres pantallas, validar cada incremento y reservar la última semana para integración y defensa. El calendario es una planificación, no una garantía de finalización.

| Periodo | Resultado verificable |
|---|---|
| 29/10–04/11 | Maven, clientes, mascotas, enumeraciones, excepciones; continuar veterinarios y servicios. Pruebas del dominio. |
| 05/11–11/11 | Turnos, detalles con horarios, estados, precios históricos, aplicaciones de vacunas y validación de disponibilidad. |
| 12/11–18/11 | JPA/Hibernate y H2 en archivo; repositorios y transacciones. Guardar, cerrar y recuperar datos sin pérdida. |
| 19/11–25/11 | Tres pantallas JavaFX: clientes/mascotas, turnos, historial/vacunaciones. Integración con servicios de aplicación. |
| 26/11–02/12 | Completar recorridos, alertas de vacunas, validaciones y corrección de errores. Primera versión funcional completa. |
| 03/12–09/12 | Pruebas integrales, ejecución desde otra carpeta, UML y decisiones finales, ZIP de entrega y preparación de defensa. |
| 10/12 | Sistema listo y material de entrega revisado. |

## Alcance del primer incremento

- Proyecto Maven compilable con Java 17.
- Maven Wrapper para Windows y Linux/macOS.
- `Cliente`, `Mascota`, `Especie`, `Especialidad` y `EstadoTurno`.
- Excepciones de datos inválidos y entidad inactiva.
- Alta bidireccional, contacto, peso positivo, fecha válida, edad calculada y baja lógica.
- Pruebas automáticas y demostración ejecutable en memoria.

Los identificadores técnicos y anotaciones JPA se incorporarán con la persistencia. No se generan identificadores ficticios ni se confunden con el número de ficha.

La verificación global del DNI y del número de ficha requiere consultar el repositorio y agregar restricciones de base de datos. En este incremento solo se impide repetir una ficha dentro del mismo cliente; no se presenta esa comprobación como unicidad global implementada.

## Decisión en revisión: cupo de guardería

El 29/10 se reabrió la interpretación del cupo: total de admisiones diarias frente a capacidad simultánea que se libera al retirarse la mascota. No se implementa todavía ninguna de las dos alternativas. Las reglas y el UML previos documentan la interpretación diaria anterior; esa parte queda provisional hasta confirmar la aclaración. Este punto no bloquea clientes, mascotas, turnos clínicos ni persistencia.

## Criterio de avance

Cada incremento debe compilar, superar sus pruebas y quedar documentado. La interfaz final será JavaFX; la demostración actual por consola solo comprueba el primer incremento del dominio. Las horas de estudio y revisión deben formar parte de la dedicación semanal para que la alumna pueda explicar el código.
