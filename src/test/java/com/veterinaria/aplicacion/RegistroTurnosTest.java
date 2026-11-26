package com.veterinaria.aplicacion;

import com.veterinaria.modelo.*;
import com.veterinaria.modelo.excepciones.DatosInvalidosException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RegistroTurnosTest {

    @Test
    void rechazaConflictosSinAgregarLosTurnos() {
        RegistroTurnos registro = new RegistroTurnos();

        Cliente ana = new Cliente(
            "Ana", "Pérez", "123", "123456", ""
        );

        Mascota toby = ana.registrarMascota(
            "F-1", "Toby", Especie.PERRO, "",
            LocalDate.now().minusYears(3)
        );

        Mascota luna = ana.registrarMascota(
            "F-2", "Luna", Especie.GATO, "",
            LocalDate.now().minusYears(2)
        );

        Veterinario laura = new Veterinario(
            "MAT-1", "Laura", "Sosa"
        );

        Veterinario pedro = new Veterinario(
            "MAT-2", "Pedro", "López"
        );

        ConsultaMedica consulta = new ConsultaMedica(
            "Consulta", new BigDecimal("20000"), 30
        );

        LocalDateTime inicio =
            LocalDate.now().plusDays(2).atTime(10, 0);

        Turno primero = new Turno(
            toby, laura,
            List.of(new DetalleTurno(consulta)),
            inicio
        );

        registro.registrar(primero);

        Turno mascotaOcupada = new Turno(
            toby, pedro,
            List.of(new DetalleTurno(consulta)),
            inicio.plusMinutes(15)
        );

        assertThrows(DatosInvalidosException.class,
            () -> registro.registrar(mascotaOcupada)
        );

        Turno veterinarioOcupado = new Turno(
            luna, laura,
            List.of(new DetalleTurno(consulta)),
            inicio.plusMinutes(15)
        );

        assertThrows(DatosInvalidosException.class,
            () -> registro.registrar(veterinarioOcupado)
        );

        assertEquals(1, registro.getTurnos().size());
        assertSame(primero, registro.getTurnos().get(0));

        Turno contiguo = new Turno(
            luna, laura,
            List.of(new DetalleTurno(consulta)),
            inicio.plusMinutes(30)
        );

        assertDoesNotThrow(() -> registro.registrar(contiguo));
        assertEquals(2, registro.getTurnos().size());
    }

        @Test
        void permiteReservarElHorarioDeUnTurnoCancelado() {
        RegistroTurnos registro = new RegistroTurnos();

        Cliente ana = new Cliente(
            "Ana", "Pérez", "123", "123456", ""
        );

        Mascota toby = ana.registrarMascota(
            "F-1", "Toby", Especie.PERRO, "",
            LocalDate.now().minusYears(3)
        );

        Veterinario laura = new Veterinario(
            "MAT-1", "Laura", "Sosa"
        );

        ConsultaMedica consulta = new ConsultaMedica(
            "Consulta", new BigDecimal("20000"), 30
        );

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime inicio = ahora.plusDays(3);

        Turno original = new Turno(
            toby, laura,
            List.of(new DetalleTurno(consulta)),
            inicio
        );

        registro.registrar(original);
        original.cancelar(ahora);

        Turno reemplazo = new Turno(
            toby, laura,
            List.of(new DetalleTurno(consulta)),
            inicio
        );

        assertDoesNotThrow(() -> registro.registrar(reemplazo));

        assertEquals(EstadoTurno.CANCELADO, original.getEstado());
        assertEquals(2, registro.getTurnos().size());
        assertSame(original, registro.getTurnos().get(0));
        assertSame(reemplazo, registro.getTurnos().get(1));
    }

}