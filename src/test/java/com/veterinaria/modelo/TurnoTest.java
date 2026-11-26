package com.veterinaria.modelo;

import com.veterinaria.modelo.excepciones.DatosInvalidosException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TurnoTest {

    @Test
    void rechazaDetalleCompartidoSinOcuparLosDetallesLibres() {
        Cliente ana = new Cliente(
            "Ana", "Pérez", "123", "123456", ""
        );

        Mascota toby = ana.registrarMascota(
            "F-1", "Toby", Especie.PERRO, "",
            LocalDate.now().minusYears(3)
        );

        Vacunacion primerServicio = new Vacunacion(
            "Vacunación", new BigDecimal("15000"), 15
        );

        Vacunacion segundoServicio = new Vacunacion(
            "Otra vacunación", new BigDecimal("18000"), 20
        );

        Veterinario veterinario = new Veterinario(
            "MAT-123", "Laura", "Sosa"
        );

        DetalleTurno ocupado = new DetalleTurno(primerServicio);
        DetalleTurno libre = new DetalleTurno(segundoServicio);

        LocalDateTime inicio =
            LocalDate.now().plusDays(1).atTime(10, 0);

        Turno primero = new Turno(
            toby, veterinario, List.of(ocupado), inicio
        );

        assertThrows(DatosInvalidosException.class,
            () -> new Turno(
                toby, veterinario,
                List.of(libre, ocupado),
                inicio.plusHours(1)
            )
        );

        Turno segundo = assertDoesNotThrow(
            () -> new Turno(
                toby, veterinario, List.of(libre),
                inicio.plusHours(2)
            )
        );

        assertEquals(1, primero.getDetalles().size());
        assertSame(ocupado, primero.getDetalles().get(0));
        assertSame(libre, segundo.getDetalles().get(0));
    }

    @Test
    void calculaHorariosConsecutivosDeLosServicios() {
        Cliente ana = new Cliente(
            "Ana", "Pérez", "123", "123456", ""
        );

        Mascota toby = ana.registrarMascota(
            "F-1", "Toby", Especie.PERRO, "",
            LocalDate.now().minusYears(3)
        );

        Veterinario veterinario = new Veterinario(
            "MAT-123", "Laura", "Sosa"
        );

        DetalleTurno consulta = new DetalleTurno(
            new ConsultaMedica(
                "Consulta", new BigDecimal("20000"), 30
            )
        );

        DetalleTurno vacunacion = new DetalleTurno(
            new Vacunacion(
                "Vacunación", new BigDecimal("15000"), 15
            )
        );

        DetalleTurno peluqueria = new DetalleTurno(
            new Peluqueria(
                "Peluquería", new BigDecimal("10000"), 45
            )
        );

        LocalDateTime inicio =
            LocalDate.now().plusDays(1).atTime(10, 0);

        Turno turno = new Turno(
            toby, veterinario,
            List.of(consulta, vacunacion, peluqueria),
            inicio
        );

        assertEquals(inicio, turno.calcularInicioDetalle(consulta));
        assertEquals(
            inicio.plusMinutes(30),
            turno.calcularFinDetalle(consulta)
        );

        assertEquals(
            inicio.plusMinutes(30),
            turno.calcularInicioDetalle(vacunacion)
        );
        assertEquals(
            inicio.plusMinutes(45),
            turno.calcularFinDetalle(vacunacion)
        );

        assertEquals(
            inicio.plusMinutes(45),
            turno.calcularInicioDetalle(peluqueria)
        );
        assertEquals(
            inicio.plusMinutes(90),
            turno.calcularFinDetalle(peluqueria)
        );

        assertEquals(90, turno.calcularDuracionTotal());
        assertEquals(
            turno.calcularFinDetalle(peluqueria),
            turno.calcularFechaHoraFin()
        );
    }

        @Test
        void comparaSoloLosIntervalosQueRequierenVeterinario() {
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

        Veterinario veterinario = new Veterinario(
            "MAT-123", "Laura", "Sosa"
        );

        ConsultaMedica consulta = new ConsultaMedica(
            "Consulta", new BigDecimal("20000"), 30
        );

        Peluqueria peluqueria = new Peluqueria(
            "Peluquería", new BigDecimal("10000"), 60
        );

        LocalDateTime inicio =
            LocalDate.now().plusDays(2).atTime(10, 0);

        Turno primero = new Turno(
            toby, veterinario,
            List.of(
                new DetalleTurno(consulta),
                new DetalleTurno(peluqueria)
            ),
            inicio
        );

        Turno superpuesto = new Turno(
            luna, veterinario,
            List.of(new DetalleTurno(consulta)),
            inicio.plusMinutes(15)
        );

        Turno contiguo = new Turno(
            luna, veterinario,
            List.of(new DetalleTurno(consulta)),
            inicio.plusMinutes(30)
        );

        assertTrue(primero.seSolapaVeterinarioCon(superpuesto));
        assertTrue(superpuesto.seSolapaVeterinarioCon(primero));

        assertFalse(primero.seSolapaVeterinarioCon(contiguo));
        assertFalse(contiguo.seSolapaVeterinarioCon(primero));

        assertTrue(primero.seSolapaCon(contiguo));

        superpuesto.cancelar(inicio.minusDays(2));

        assertFalse(primero.seSolapaVeterinarioCon(superpuesto));
    }

}