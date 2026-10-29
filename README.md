# Sistema de Gestión Veterinaria

Trabajo Integrador Final de **Programación Orientada a Objetos I**.

El objetivo es desarrollar una aplicación de escritorio para administrar clientes, mascotas, turnos, servicios, historial médico y vacunaciones de una veterinaria. El proyecto utilizará Java, JavaFX, JPA/Hibernate y una base de datos relacional.

## Estado actual

La implementación comenzó el **29 de octubre de 2026**, con el objetivo de tener el sistema listo el **10 de diciembre de 2026**.

El primer incremento contiene el proyecto Maven, las clases `Cliente` y `Mascota`, tres enumeraciones, excepciones de dominio, pruebas automáticas y una demostración ejecutable en memoria. JavaFX, JPA/Hibernate, H2 y el resto del dominio se incorporarán en los siguientes incrementos; esta versión todavía no guarda datos al cerrar.

## Ejecutar el primer incremento

Requisito: instalar un **JDK 17 o superior** y comprobar `java -version`. Maven Wrapper descarga la versión de Maven fijada por el proyecto; la primera ejecución requiere conexión a Internet.

En Windows PowerShell, desde la carpeta que contiene `pom.xml`:

```powershell
.\mvnw.cmd clean verify
java -jar target/sistema-veterinaria-poo-0.1.0-SNAPSHOT.jar
```

En Linux/macOS:

```sh
sh ./mvnw clean verify
java -jar target/sistema-veterinaria-poo-0.1.0-SNAPSHOT.jar
```

Si Maven ya está instalado, también se puede utilizar `mvn clean verify` o `mvn clean package`.

La demostración crea un cliente y una mascota ficticios, actualiza el peso y muestra que ambos extremos de la asociación conservan las mismas referencias. No representa todavía las pantallas finales.

## Organización del código

- `src/main/java/com/veterinaria/modelo`: entidades, enumeraciones y validaciones del dominio.
- `src/main/java/com/veterinaria/modelo/excepciones`: errores propios del dominio.
- `src/main/java/com/veterinaria/DemostracionDominio.java`: punto de entrada temporal.
- `src/test/java/com/veterinaria/modelo`: pruebas de las reglas de clientes y mascotas.
- `src/main/resources`: configuración y vistas que se incorporarán posteriormente.
- `docs`: análisis, decisiones y registro de avances.

La unicidad global del DNI y del número de ficha, los identificadores técnicos y la recuperación de datos requieren la futura capa de persistencia. No están resueltos por el primer incremento en memoria.

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
- [Plan de implementación hasta el 10 de diciembre](docs/plan-implementacion.md)
- [Diagrama de clases](docs/imagenes/diagrama_clases_veterinaria.png)
- [Diagrama de flujo](docs/imagenes/diagrama_flujo_veterinaria.png)

## Próximas etapas

- Revisar el modelo y las decisiones de diseño con el profesor.
- Completar veterinarios, servicios, turnos y vacunaciones con sus pruebas.
- Implementar persistencia con JPA.
- Desarrollar las tres pantallas con JavaFX y MVC.
- Agregar pruebas y preparar la documentación final.

La interpretación del cupo de guardería quedó nuevamente en revisión el 29/10: admisiones diarias frente a capacidad simultánea. No se implementa esa regla hasta resolverla; se conserva la documentación anterior como antecedente.

## Criterio de diseño

Se busca un **modelo de dominio rico**: las entidades no serán simples contenedores de datos. Las validaciones y reglas que pertenecen naturalmente a una clase se implementarán dentro de esa clase.

