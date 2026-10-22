# Sistema de Gestión Veterinaria

Trabajo Integrador Final de **Programación Orientada a Objetos I**.

El objetivo es desarrollar una aplicación de escritorio para administrar clientes, mascotas, turnos, servicios, historial médico y vacunaciones de una veterinaria. El proyecto utilizará Java, JavaFX, JPA/Hibernate y una base de datos relacional.

## Estado actual

El proyecto se encuentra en la etapa de **análisis y diseño del dominio**. Todavía no se incorporó el código Java porque primero se están definiendo y justificando las clases, asociaciones, multiplicidades, comportamientos y reglas de negocio.

## Alcance de la interfaz

Se implementarán tres pantallas principales:

1. Gestión de clientes y mascotas.
2. Gestión de turnos.
3. Historial médico y vacunaciones.

Las clases que no tengan una pantalla propia —por ejemplo, `Veterinario`, `Servicio` y `Vacuna`— igualmente formarán parte del modelo y serán necesarias para las relaciones y reglas del sistema.

## Tecnologías previstas

- Java 17 o superior
- Maven
- JavaFX
- JPA con Hibernate
- H2 en modo archivo
- PlantUML para el diagrama de clases

## Documentación

- [Modelo de dominio en PlantUML](docs/modelo_dominio_veterinaria.puml)
- [Reglas de negocio](docs/reglas-negocio.md)
- [Casos de uso](docs/casos-de-uso.md)
- [Bitácora del desarrollo](docs/bitacora-desarrollo-integrador.md)
- [Diagrama de clases](docs/imagenes/diagrama_clases_veterinaria.png)
- [Diagrama de flujo](docs/imagenes/diagrama_flujo_veterinaria.png)

## Próximas etapas

- Revisar el modelo y las decisiones de diseño con el profesor.
- Crear la estructura Maven.
- Implementar enumeraciones, excepciones y entidades del dominio.
- Implementar persistencia con JPA.
- Desarrollar las tres pantallas con JavaFX y MVC.
- Agregar pruebas y preparar la documentación final.

## Criterio de diseño

Se busca un **modelo de dominio rico**: las entidades no serán simples contenedores de datos. Las validaciones y reglas que pertenecen naturalmente a una clase se implementarán dentro de esa clase.

