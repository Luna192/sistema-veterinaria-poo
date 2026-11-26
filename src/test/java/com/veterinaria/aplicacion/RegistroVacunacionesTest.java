package com.veterinaria.aplicacion;

import com.veterinaria.modelo.*;
import com.veterinaria.modelo.excepciones.DatosInvalidosException;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
class RegistroVacunacionesTest {

    @Test
    void registraSinTurnoYRechazaUnaDosisAnticipada() {
        RegistroVacunaciones registro = new RegistroVacunaciones();

        Cliente ana = new Cliente(
            "Ana", "Pérez", "123", "3764123456", ""
        );

        Mascota toby = ana.registrarMascota(
            "F-1", "Toby", Especie.PERRO, "",
            LocalDate.of(2020, 3, 15)
        );

        Vacuna vacuna = new Vacuna("Antirrábica", "Rabia", 12);
        Veterinario laura = new Veterinario("MAT-123", "Laura", "Sosa");

        AplicacionVacuna primera = registro.registrarSinTurno(
        toby, vacuna, laura, LocalDate.of(2025, 11, 22)
        );

        assertSame(
            primera,
            registro.buscarUltimaAplicacion(toby, vacuna).orElseThrow()
        );

        assertThrows(DatosInvalidosException.class,
            () -> registro.registrarSinTurno(
                toby, vacuna, laura, LocalDate.of(2026, 11, 1)
            )
        );

        assertSame(
            primera,
            registro.buscarUltimaAplicacion(toby, vacuna).orElseThrow()
        );

        Vacunacion servicio = new Vacunacion(
        "Aplicación de vacuna", new BigDecimal("15000"), 15
        );

        DetalleTurno detalle = new DetalleTurno(servicio);

        Turno turno = new Turno(
            toby, laura, List.of(detalle),
            LocalDateTime.now().plusMinutes(5)
        );

        turno.confirmar();

        assertThrows(DatosInvalidosException.class,
            () -> registro.registrarConTurno(
            turno, detalle, vacuna, LocalDate.of(2026, 11, 1)
            )
        );

        assertTrue(detalle.getAplicacionVacuna().isEmpty());

        assertSame(
            primera,
            registro.buscarUltimaAplicacion(toby, vacuna).orElseThrow()
        );
    }


    @Test
    void consultaAplicacionesPorMascotaEnOrdenCronologico() {
    RegistroVacunaciones registro = new RegistroVacunaciones();

    LocalDate hoy = LocalDate.now();

    Cliente ana = new Cliente(
        "Ana", "Pérez", "123", "123456", ""
    );

    Mascota toby = ana.registrarMascota(
        "F-1", "Toby", Especie.PERRO, "",
        hoy.minusYears(5)
    );

    Mascota luna = ana.registrarMascota(
        "F-2", "Luna", Especie.GATO, "",
        hoy.minusYears(5)
    );

    Veterinario veterinario = new Veterinario(
        "MAT-1", "Laura", "Sosa"
    );

    Vacuna rabia = new Vacuna("Antirrábica", "Rabia", 12);
    Vacuna otra = new Vacuna("Otra vacuna", "Otra enfermedad", 12);

    AplicacionVacuna reciente = registro.registrarSinTurno(
        toby, rabia, veterinario, hoy.minusMonths(1)
    );

    AplicacionVacuna antigua = registro.registrarSinTurno(
        toby, otra, veterinario, hoy.minusYears(2)
    );

    registro.registrarSinTurno(
        luna, rabia, veterinario, hoy.minusMonths(2)
    );

    toby.darDeBaja();

    var resultado = registro.consultarAplicaciones(toby);

    assertEquals(2, resultado.size());
    assertSame(antigua, resultado.get(0));
    assertSame(reciente, resultado.get(1));
    }

}