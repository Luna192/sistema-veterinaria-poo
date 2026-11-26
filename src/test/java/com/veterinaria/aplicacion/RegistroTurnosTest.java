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
                                "Ana", "Pérez", "123", "123456", "");

                Mascota toby = ana.registrarMascota(
                                "F-1", "Toby", Especie.PERRO, "",
                                LocalDate.now().minusYears(3));

                Mascota luna = ana.registrarMascota(
                                "F-2", "Luna", Especie.GATO, "",
                                LocalDate.now().minusYears(2));

                Veterinario laura = new Veterinario(
                                "MAT-1", "Laura", "Sosa");

                Veterinario pedro = new Veterinario(
                                "MAT-2", "Pedro", "López");

                ConsultaMedica consulta = new ConsultaMedica(
                                "Consulta", new BigDecimal("20000"), 30);

                LocalDateTime inicio = LocalDate.now().plusDays(2).atTime(10, 0);

                Turno primero = new Turno(
                                toby, laura,
                                List.of(new DetalleTurno(consulta)),
                                inicio);

                registro.registrar(primero);

                Turno mascotaOcupada = new Turno(
                                toby, pedro,
                                List.of(new DetalleTurno(consulta)),
                                inicio.plusMinutes(15));

                assertThrows(DatosInvalidosException.class,
                                () -> registro.registrar(mascotaOcupada));

                Turno veterinarioOcupado = new Turno(
                                luna, laura,
                                List.of(new DetalleTurno(consulta)),
                                inicio.plusMinutes(15));

                assertThrows(DatosInvalidosException.class,
                                () -> registro.registrar(veterinarioOcupado));

                assertEquals(1, registro.getTurnos().size());
                assertSame(primero, registro.getTurnos().get(0));

                Turno contiguo = new Turno(
                                luna, laura,
                                List.of(new DetalleTurno(consulta)),
                                inicio.plusMinutes(30));

                assertDoesNotThrow(() -> registro.registrar(contiguo));
                assertEquals(2, registro.getTurnos().size());
        }

        @Test
        void permiteReservarElHorarioDeUnTurnoCancelado() {
                RegistroTurnos registro = new RegistroTurnos();

                Cliente ana = new Cliente(
                                "Ana", "Pérez", "123", "123456", "");

                Mascota toby = ana.registrarMascota(
                                "F-1", "Toby", Especie.PERRO, "",
                                LocalDate.now().minusYears(3));

                Veterinario laura = new Veterinario(
                                "MAT-1", "Laura", "Sosa");

                ConsultaMedica consulta = new ConsultaMedica(
                                "Consulta", new BigDecimal("20000"), 30);

                LocalDateTime ahora = LocalDateTime.now();
                LocalDateTime inicio = ahora.plusDays(3);

                Turno original = new Turno(
                                toby, laura,
                                List.of(new DetalleTurno(consulta)),
                                inicio);

                registro.registrar(original);
                original.cancelar(ahora);

                Turno reemplazo = new Turno(
                                toby, laura,
                                List.of(new DetalleTurno(consulta)),
                                inicio);

                assertDoesNotThrow(() -> registro.registrar(reemplazo));

                assertEquals(EstadoTurno.CANCELADO, original.getEstado());
                assertEquals(2, registro.getTurnos().size());
                assertSame(original, registro.getTurnos().get(0));
                assertSame(reemplazo, registro.getTurnos().get(1));
        }

        @Test
        void historialIncluyeSoloAtendidosYLosOrdenaPorFecha() {
                RegistroTurnos registro = new RegistroTurnos();

                Cliente ana = new Cliente(
                                "Ana", "Pérez", "123", "123456", "");

                Mascota toby = ana.registrarMascota(
                                "F-1", "Toby", Especie.PERRO, "",
                                LocalDate.now().minusYears(3));

                Mascota luna = ana.registrarMascota(
                                "F-2", "Luna", Especie.GATO, "",
                                LocalDate.now().minusYears(2));

                Peluqueria servicio = new Peluqueria(
                                "Peluquería", new BigDecimal("10000"), 30);

                LocalDateTime inicio = LocalDate.now().plusDays(3).atTime(10, 0);

                Turno primero = new Turno(
                                toby, null, List.of(new DetalleTurno(servicio)), inicio);

                Turno segundo = new Turno(
                                toby, null, List.of(new DetalleTurno(servicio)),
                                inicio.plusDays(1));

                Turno pendiente = new Turno(
                                toby, null, List.of(new DetalleTurno(servicio)),
                                inicio.plusDays(2));

                Turno confirmado = new Turno(
                                toby, null, List.of(new DetalleTurno(servicio)),
                                inicio.plusDays(3));

                Turno cancelado = new Turno(
                                toby, null, List.of(new DetalleTurno(servicio)),
                                inicio.plusDays(4));

                Turno deOtraMascota = new Turno(
                                luna, null, List.of(new DetalleTurno(servicio)),
                                inicio.plusDays(5));

                // Los registramos desordenados para comprobar el orden del historial.
                registro.registrar(segundo);
                registro.registrar(pendiente);
                registro.registrar(primero);
                registro.registrar(confirmado);
                registro.registrar(cancelado);
                registro.registrar(deOtraMascota);

                primero.confirmar();
                primero.marcarAtendido();

                segundo.confirmar();
                segundo.marcarAtendido();

                confirmado.confirmar();
                cancelado.cancelar(inicio.minusDays(3));

                deOtraMascota.confirmar();
                deOtraMascota.marcarAtendido();

                toby.darDeBaja();

                List<Turno> historial = registro.consultarHistorial(toby);

                assertEquals(2, historial.size());
                assertSame(primero, historial.get(0));
                assertSame(segundo, historial.get(1));
        }

        @Test
        void rechazaTurnosCuandoGuarderiaAlcanzaCupoMaximo() {
                RegistroTurnos registro = new RegistroTurnos();

                Cliente ana = new Cliente(
                                "Ana", "Pérez", "123", "123456", "");

                Guarderia guarderia = new Guarderia(
                                "Guardería", new BigDecimal("15000"), 120, 10);

                LocalDateTime inicio = LocalDate.now().plusDays(3).atTime(10, 0);

                for (int i = 1; i <= 10; i++) {
                        Mascota mascota = ana.registrarMascota(
                                        "F-" + i, "Mascota " + i, Especie.PERRO, "",
                                        LocalDate.now().minusYears(2));

                        Turno turno = new Turno(
                                        mascota, null,
                                        List.of(new DetalleTurno(guarderia)),
                                        inicio);

                        registro.registrar(turno);

                        assertEquals(
                                        i,
                                        registro.calcularOcupacionMaxima(
                                                        guarderia, inicio, inicio.plusHours(2)));
                }

                Mascota adicional = ana.registrarMascota(
                                "F-11", "Adicional", Especie.GATO, "",
                                LocalDate.now().minusYears(2));

                Turno rechazado = new Turno(
                                adicional, null,
                                List.of(new DetalleTurno(guarderia)),
                                inicio);

                assertThrows(DatosInvalidosException.class,
                                () -> registro.registrar(rechazado));

                assertEquals(10, registro.getTurnos().size());
                assertFalse(registro.getTurnos().contains(rechazado));
        }

        @Test
        void liberaCupoGuarderiaAlCancelarTurno() {
                RegistroTurnos registro = new RegistroTurnos();

                Cliente ana = new Cliente(
                                "Ana", "Pérez", "123", "123456", "");

                Guarderia guarderia = new Guarderia(
                                "Guardería", new BigDecimal("15000"), 120, 10);

                LocalDateTime ahora = LocalDateTime.now();
                LocalDateTime inicio = ahora.toLocalDate().plusDays(3).atTime(10, 0);

                Mascota toby = ana.registrarMascota(
                                "F-1", "Toby", Especie.PERRO, "",
                                LocalDate.now().minusYears(3));

                Turno original = new Turno(
                                toby, null,
                                List.of(new DetalleTurno(guarderia)),
                                inicio);

                registro.registrar(original);
                original.confirmar();

                for (int i = 2; i <= 10; i++) {
                        Mascota mascota = ana.registrarMascota(
                                        "F-" + i, "Mascota " + i, Especie.PERRO, "",
                                        LocalDate.now().minusYears(2));

                        registro.registrar(new Turno(
                                        mascota, null,
                                        List.of(new DetalleTurno(guarderia)),
                                        inicio));
                }

                Mascota adicional = ana.registrarMascota(
                                "F-11", "Adicional", Especie.GATO, "",
                                LocalDate.now().minusYears(2));

                Turno reemplazo = new Turno(
                                adicional, null,
                                List.of(new DetalleTurno(guarderia)),
                                inicio);

                assertThrows(DatosInvalidosException.class,
                                () -> registro.registrar(reemplazo));

                original.cancelar(ahora);

                assertEquals(
                                9,
                                registro.calcularOcupacionMaxima(
                                                guarderia, inicio, inicio.plusHours(2)));

                assertDoesNotThrow(() -> registro.registrar(reemplazo));

                assertEquals(
                                10,
                                registro.calcularOcupacionMaxima(
                                                guarderia, inicio, inicio.plusHours(2)));

                assertEquals(EstadoTurno.CANCELADO, original.getEstado());
                assertEquals(11, registro.getTurnos().size());
        }

        @Test
        void permiteReservasDeGuarderiaEnFechasDiferentes() {

                RegistroTurnos registro = new RegistroTurnos();

                Cliente ana = new Cliente(
                                "Ana", "Perez", "123", "123456", "");

                Mascota toby = ana.registrarMascota(
                                "F-1", "Toby", Especie.PERRO, "",
                                LocalDate.now().minusYears(3));

                Mascota luna = ana.registrarMascota(
                                "F-2", "Luna", Especie.PERRO, "",
                                LocalDate.now().minusYears(2));

                Guarderia guarderia = new Guarderia(
                                "Guarderia", new BigDecimal("15000"), 120, 10);

                LocalDate fecha = LocalDate.now().plusDays(3);
                LocalDateTime inicio = fecha.atTime(10, 0);

                Turno primero = new Turno(
                                toby, null,
                                List.of(new DetalleTurno(guarderia)),
                                inicio);

                Turno segundo = new Turno(
                                luna, null,
                                List.of(new DetalleTurno(guarderia)),
                                inicio.plusDays(1));

                registro.registrar(primero);

                assertEquals(1, registro.calcularOcupacionMaxima(
                                guarderia,
                                fecha.atStartOfDay(),
                                fecha.plusDays(1).atStartOfDay()));

                assertEquals(0, registro.calcularOcupacionMaxima(
                                guarderia,
                                fecha.plusDays(1).atStartOfDay(),
                                fecha.plusDays(2).atStartOfDay()));

                assertDoesNotThrow(() -> registro.registrar(segundo));

                assertEquals(1, registro.calcularOcupacionMaxima(
                                guarderia,
                                fecha.atStartOfDay(),
                                fecha.plusDays(1).atStartOfDay()));

                assertEquals(1, registro.calcularOcupacionMaxima(
                                guarderia,
                                fecha.plusDays(1).atStartOfDay(),
                                fecha.plusDays(2).atStartOfDay()));

                assertEquals(2, registro.getTurnos().size());
        }

        @Test
        void otrosServiciosNoOcupanCuposDeGuarderia() {

                RegistroTurnos registro = new RegistroTurnos();

                Cliente ana = new Cliente(
                                "Ana", "Perez", "123", "123456", "");

                Mascota toby = ana.registrarMascota(
                                "F-1", "Toby", Especie.PERRO, "",
                                LocalDate.now().minusYears(3));

                Mascota luna = ana.registrarMascota(
                                "F-2", "Luna", Especie.PERRO, "",
                                LocalDate.now().minusYears(2));

                Mascota max = ana.registrarMascota(
                                "F-3", "Max", Especie.PERRO, "",
                                LocalDate.now().minusYears(4));

                Guarderia guarderia = new Guarderia(
                                "Guarderia", new BigDecimal("15000"), 120, 10);

                Peluqueria peluqueria = new Peluqueria(
                                "Peluqueria", new BigDecimal("10000"), 30);

                ConsultaMedica consulta = new ConsultaMedica(
                                "Consulta", new BigDecimal("20000"), 30);

                Veterinario laura = new Veterinario(
                                "MAT-1", "Laura", "Sosa");

                LocalDate fecha = LocalDate.now().plusDays(3);
                LocalDateTime inicio = fecha.atTime(10, 0);

                Turno turnoPeluqueria = new Turno(
                                toby, null,
                                List.of(new DetalleTurno(peluqueria)),
                                inicio);

                Turno turnoConsulta = new Turno(
                                luna, laura,
                                List.of(new DetalleTurno(consulta)),
                                inicio);

                Turno turnoGuarderia = new Turno(
                                max, null,
                                List.of(new DetalleTurno(guarderia)),
                                inicio);

                registro.registrar(turnoPeluqueria);
                registro.registrar(turnoConsulta);

                assertEquals(0, registro.calcularOcupacionMaxima(
                                guarderia,
                                fecha.atStartOfDay(),
                                fecha.plusDays(1).atStartOfDay()));

                assertDoesNotThrow(
                                () -> registro.registrar(turnoGuarderia));

                assertEquals(1, registro.calcularOcupacionMaxima(
                                guarderia, fecha.atStartOfDay(), fecha.plusDays(1).atStartOfDay()));

                assertEquals(3, registro.getTurnos().size());
        }

        @Test
        void turnoConMultiplesServiciosOcupaUnCupoDeGuarderia() {

                RegistroTurnos registro = new RegistroTurnos();

                Cliente ana = new Cliente(
                                "Ana", "Perez", "123", "123456", "");

                Mascota toby = ana.registrarMascota(
                                "F-1", "Toby", Especie.PERRO, "",
                                LocalDate.now().minusYears(3));

                Mascota luna = ana.registrarMascota(
                                "F-2", "Luna", Especie.PERRO, "",
                                LocalDate.now().minusYears(2));

                Veterinario laura = new Veterinario(
                                "MAT-1", "Laura", "Sosa");

                ConsultaMedica consulta = new ConsultaMedica(
                                "Consulta", new BigDecimal("20000"), 30);

                Guarderia guarderia = new Guarderia(
                                "Guarderia", new BigDecimal("15000"), 120, 10);

                LocalDate fecha = LocalDate.now().plusDays(3);
                LocalDateTime inicio = fecha.atTime(9, 0);

                DetalleTurno detalleConsulta = new DetalleTurno(consulta);
                DetalleTurno detalleGuarderia = new DetalleTurno(guarderia);

                Turno combinado = new Turno(
                                toby, laura,
                                List.of(detalleConsulta, detalleGuarderia),
                                inicio);

                assertDoesNotThrow(() -> registro.registrar(combinado));

                assertEquals(1, registro.calcularOcupacionMaxima(
                                guarderia,
                                fecha.atStartOfDay(),
                                fecha.plusDays(1).atStartOfDay()));

                assertEquals(inicio.plusMinutes(30),
                                combinado.calcularInicioDetalle(detalleGuarderia));

                Turno segundo = new Turno(
                                luna, null,
                                List.of(new DetalleTurno(guarderia)),
                                inicio.plusMinutes(30));

                for (int i = 3; i <= 11; i++) {
                        Mascota mascota = ana.registrarMascota(
                                        "F-" + i, "Mascota " + i, Especie.PERRO, "",
                                        fecha.minusYears(3));

                        registro.registrar(new Turno(
                                        mascota, null,
                                        List.of(new DetalleTurno(guarderia)),
                                        inicio.plusMinutes(30)));
                }

                assertEquals(
                                10,
                                registro.calcularOcupacionMaxima(
                                                guarderia,
                                                inicio.plusMinutes(30),
                                                inicio.plusMinutes(150)));

                assertThrows(DatosInvalidosException.class,
                                () -> registro.registrar(segundo));

                assertEquals(10, registro.getTurnos().size());
        }

        @Test
        void permiteReutilizarCuposGuarderiaEnElMismoDia() {
                RegistroTurnos registro = new RegistroTurnos();

                Cliente ana = new Cliente(
                                "Ana", "Pérez", "123", "123456", "");

                Guarderia guarderia = new Guarderia(
                                "Guardería", new BigDecimal("15000"), 120, 10);

                LocalDate fecha = LocalDate.now().plusDays(3);

                for (int i = 1; i <= 10; i++) {
                        Mascota deManana = ana.registrarMascota(
                                        "M-" + i, "Mañana " + i, Especie.PERRO, "",
                                        fecha.minusYears(3));

                        Mascota deTarde = ana.registrarMascota(
                                        "T-" + i, "Tarde " + i, Especie.GATO, "",
                                        fecha.minusYears(3));

                        registro.registrar(new Turno(
                                        deManana, null,
                                        List.of(new DetalleTurno(guarderia, 120)),
                                        fecha.atTime(9, 0)));

                        registro.registrar(new Turno(
                                        deTarde, null,
                                        List.of(new DetalleTurno(guarderia, 180)),
                                        fecha.atTime(11, 0)));
                }

                Mascota adicional = ana.registrarMascota(
                                "F-extra", "Adicional", Especie.PERRO, "",
                                fecha.minusYears(3));

                Turno superpuesto = new Turno(
                                adicional, null,
                                List.of(new DetalleTurno(guarderia, 120)),
                                fecha.atTime(10, 0));

                assertThrows(DatosInvalidosException.class,
                                () -> registro.registrar(superpuesto));

                assertEquals(
                                10,
                                registro.calcularOcupacionMaxima(
                                                guarderia, fecha.atTime(9, 0), fecha.atTime(14, 0)));

                assertEquals(
                                0,
                                registro.calcularOcupacionMaxima(
                                                guarderia, fecha.atTime(14, 0), fecha.atTime(16, 0)));

                assertEquals(20, registro.getTurnos().size());
                assertFalse(registro.getTurnos().contains(superpuesto));
        }

        @Test
        void permiteSolapamientosParcialesSinSuperarCapacidad() {
                RegistroTurnos registro = new RegistroTurnos();

                Cliente ana = new Cliente(
                                "Ana", "Perez", "123", "123456", "");

                LocalDate fecha = LocalDate.now().plusDays(3);

                Mascota toby = ana.registrarMascota(
                                "F-1", "Toby", Especie.PERRO, "",
                                LocalDate.now().minusYears(3));

                Mascota luna = ana.registrarMascota(
                                "F-2", "Luna", Especie.PERRO, "",
                                LocalDate.now().minusYears(2));

                Mascota max = ana.registrarMascota(
                                "F-3", "Max", Especie.PERRO, "",
                                LocalDate.now().minusYears(4));

                Guarderia guarderia = new Guarderia(
                                "Guarderia", new BigDecimal("15000"), 120, 10);

                Turno primero = new Turno(
                                toby, null,
                                List.of(new DetalleTurno(guarderia, 180)),
                                fecha.atTime(8, 0));

                Turno segundo = new Turno(
                                luna, null,
                                List.of(new DetalleTurno(guarderia, 180)),
                                fecha.atTime(11, 0));

                Turno tercero = new Turno(
                                max, null,
                                List.of(new DetalleTurno(guarderia, 240)),
                                fecha.atTime(9, 0));

                for (int i = 4; i <= 11; i++) {
                        Mascota mascota = ana.registrarMascota(
                                        "F-" + i, "Mascota " + i, Especie.PERRO, "",
                                        LocalDate.now().minusYears(3));

                        registro.registrar(new Turno(
                                        mascota, null,
                                        List.of(new DetalleTurno(guarderia, 360)),
                                        fecha.atTime(8, 0)));
                }

                registro.registrar(primero);
                registro.registrar(segundo);

                assertEquals(9, registro.calcularOcupacionMaxima(
                                guarderia, fecha.atTime(9, 0), fecha.atTime(13, 0)));

                assertDoesNotThrow(() -> registro.registrar(tercero));

                assertEquals(10, registro.calcularOcupacionMaxima(
                                guarderia, fecha.atTime(8, 0), fecha.atTime(14, 0)));

                assertEquals(11, registro.getTurnos().size());
        }

        @Test
        void rechazaReservaSiSuperaCapacidadDuranteLaEstadia() {

                RegistroTurnos registro = new RegistroTurnos();

                Cliente ana = new Cliente(
                                "Ana", "Perez", "123", "123456", "");

                Mascota toby = ana.registrarMascota(
                                "F-1", "Toby", Especie.PERRO, "",
                                LocalDate.now().minusYears(3));

                Mascota luna = ana.registrarMascota(
                                "F-2", "Luna", Especie.PERRO, "",
                                LocalDate.now().minusYears(2));

                Mascota max = ana.registrarMascota(
                                "F-3", "Max", Especie.PERRO, "",
                                LocalDate.now().minusYears(4));

                Guarderia guarderia = new Guarderia(
                                "Guarderia", new BigDecimal("15000"), 120, 10);

                LocalDate fecha = LocalDate.now().plusDays(3);

                Turno primero = new Turno(
                                toby, null,
                                List.of(new DetalleTurno(guarderia, 240)),
                                fecha.atTime(8, 0));

                Turno segundo = new Turno(
                                luna, null,
                                List.of(new DetalleTurno(guarderia, 180)),
                                fecha.atTime(11, 0));

                Turno tercero = new Turno(
                                max, null,
                                List.of(new DetalleTurno(guarderia, 240)),
                                fecha.atTime(9, 0));

                for (int i = 4; i <= 11; i++) {
                        Mascota mascota = ana.registrarMascota(
                                        "F-" + i, "Mascota " + i, Especie.PERRO, "",
                                        LocalDate.now().minusYears(3));

                        registro.registrar(new Turno(
                                        mascota, null,
                                        List.of(new DetalleTurno(guarderia, 360)),
                                        fecha.atTime(8, 0)));
                }

                registro.registrar(primero);
                registro.registrar(segundo);

                assertEquals(9, registro.calcularOcupacionMaxima(
                                guarderia,
                                fecha.atTime(9, 0),
                                fecha.atTime(11, 0)));

                assertEquals(10, registro.calcularOcupacionMaxima(
                                guarderia,
                                fecha.atTime(9, 0),
                                fecha.atTime(13, 0)));

                assertThrows(DatosInvalidosException.class,
                                () -> registro.registrar(tercero));

                assertEquals(10, registro.getTurnos().size());

                assertFalse(registro.getTurnos().contains(tercero));
        }

        @Test
        void cancelarTurnoLiberaCapacidadSimultanea() {

                RegistroTurnos registro = new RegistroTurnos();

                Cliente ana = new Cliente(
                                "Ana", "Perez", "123", "123456", "");

                Mascota toby = ana.registrarMascota(
                                "F-1", "Toby", Especie.PERRO, "",
                                LocalDate.now().minusYears(3));

                Mascota luna = ana.registrarMascota(
                                "F-2", "Luna", Especie.PERRO, "",
                                LocalDate.now().minusYears(2));

                Mascota max = ana.registrarMascota(
                                "F-3", "Max", Especie.PERRO, "",
                                LocalDate.now().minusYears(4));

                Guarderia guarderia = new Guarderia(
                                "Guarderia", new BigDecimal("15000"), 120, 10);

                LocalDateTime ahora = LocalDateTime.now();
                LocalDate fecha = ahora.toLocalDate().plusDays(3);

                Turno primero = new Turno(
                                toby, null,
                                List.of(new DetalleTurno(guarderia, 240)),
                                fecha.atTime(8, 0));

                Turno segundo = new Turno(
                                luna, null,
                                List.of(new DetalleTurno(guarderia, 240)),
                                fecha.atTime(10, 0));

                Turno tercero = new Turno(
                                max, null,
                                List.of(new DetalleTurno(guarderia, 120)),
                                fecha.atTime(11, 0));

                for (int i = 4; i <= 11; i++) {

                        Mascota mascota = ana.registrarMascota(
                                        "F-" + i, "Mascota " + i, Especie.PERRO, "",
                                        LocalDate.now().minusYears(3));

                        registro.registrar(new Turno(
                                        mascota, null,
                                        List.of(new DetalleTurno(guarderia, 360)),
                                        fecha.atTime(8, 0)));
                }

                registro.registrar(primero);
                registro.registrar(segundo);

                primero.confirmar();

                assertEquals(10, registro.calcularOcupacionMaxima(
                                guarderia, fecha.atTime(11, 0), fecha.atTime(13, 0)));

                assertThrows(DatosInvalidosException.class,
                                () -> registro.registrar(tercero));

                segundo.cancelar(ahora);

                assertEquals(EstadoTurno.CANCELADO, segundo.getEstado());

                assertEquals(9, registro.calcularOcupacionMaxima(
                                guarderia, fecha.atTime(11, 0), fecha.atTime(13, 0)));

                assertDoesNotThrow(() -> registro.registrar(tercero));

                assertEquals(10, registro.calcularOcupacionMaxima(
                                guarderia, fecha.atTime(11, 0), fecha.atTime(13, 0)));

                assertEquals(11, registro.getTurnos().size());
        }

        @Test
        void distintasGuarderiasCompartenLosDiezLugares() {
                RegistroTurnos registro = new RegistroTurnos();

                Cliente ana = new Cliente(
                                "Ana", "Pérez", "123", "123456", "");

                Guarderia primera = new Guarderia(
                                "Guardería A", new BigDecimal("15000"), 300, 10);

                Guarderia segunda = new Guarderia(
                                "Guardería B", new BigDecimal("18000"), 300, 10);

                LocalDateTime inicio = LocalDate.now().plusDays(3).atTime(9, 0);

                for (int i = 1; i <= 10; i++) {
                        Mascota mascota = ana.registrarMascota(
                                        "F-" + i, "Mascota " + i, Especie.PERRO, "",
                                        LocalDate.now().minusYears(2));

                        Turno turno = new Turno(
                                        mascota, null,
                                        List.of(new DetalleTurno(primera)),
                                        inicio);

                        registro.registrar(turno);
                }

                Mascota adicional = ana.registrarMascota(
                                "F-11", "Adicional", Especie.GATO, "",
                                LocalDate.now().minusYears(2));

                Turno undécimo = new Turno(
                                adicional, null,
                                List.of(new DetalleTurno(segunda)),
                                inicio);

                assertThrows(DatosInvalidosException.class,
                                () -> registro.registrar(undécimo));

                assertEquals(10, registro.getTurnos().size());
        }

}