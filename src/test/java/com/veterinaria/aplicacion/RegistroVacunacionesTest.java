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
}