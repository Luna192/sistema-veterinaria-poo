package com.veterinaria.modelo;

import com.veterinaria.modelo.excepciones.DatosInvalidosException;
import com.veterinaria.modelo.excepciones.EntidadInactivaException;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class MascotaTest {
    private Cliente cliente() {
        return new Cliente("Ana", "Ejemplo", "00000000", "123", "");
    }

    private Mascota mascota() {
        return cliente().registrarMascota("F-1", "Toby", Especie.PERRO, null,
                LocalDate.of(2020, 3, 15), BigDecimal.TEN);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "-0.01"})
    void rechazaPesoInvalidoAlCrearYActualizar(String valor) {
        BigDecimal peso = new BigDecimal(valor);
        Cliente ana = cliente();
        assertThrows(DatosInvalidosException.class, () -> ana.registrarMascota(
                "F-1", "Toby", Especie.PERRO, "", LocalDate.of(2020, 1, 1), peso));
        Mascota toby = mascota();
        assertThrows(DatosInvalidosException.class, () -> toby.actualizarPeso(peso));
        assertEquals(BigDecimal.TEN, toby.getPeso());
    }

    @Test
    void rechazaPesoNulo() {
        Mascota toby = mascota();
        assertThrows(DatosInvalidosException.class, () -> toby.actualizarPeso(null));
        assertThrows(DatosInvalidosException.class, () -> cliente().registrarMascota(
                "F-1", "Toby", Especie.PERRO, "", LocalDate.of(2020, 1, 1), null));
    }

    @Test
    void aceptaPesoPositivoMenorAUnKilo() {
        Mascota toby = mascota();
        toby.actualizarPeso(new BigDecimal("0.25"));
        assertEquals(new BigDecimal("0.25"), toby.getPeso());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void rechazaNombreVacioYConservaDatosSiFallaLaEdicion(String nombre) {
        Mascota toby = mascota();
        assertThrows(DatosInvalidosException.class, () -> toby.cambiarDatos(nombre, "Nueva raza"));
        assertEquals("Toby", toby.getNombre());
        assertTrue(toby.getRaza().isEmpty());
        assertThrows(DatosInvalidosException.class, () -> cliente().registrarMascota(
                "F-2", nombre, Especie.PERRO, "", LocalDate.of(2020, 1, 1), BigDecimal.ONE));
    }

    @Test
    void rechazaFechaFuturaNulaYEspecieAusente() {
        Cliente ana = cliente();
        assertThrows(DatosInvalidosException.class, () -> ana.registrarMascota(
                "F-1", "Toby", Especie.PERRO, "", LocalDate.now().plusDays(1), BigDecimal.ONE));
        assertThrows(DatosInvalidosException.class, () -> ana.registrarMascota(
                "F-1", "Toby", Especie.PERRO, "", null, BigDecimal.ONE));
        assertThrows(DatosInvalidosException.class, () -> ana.registrarMascota(
                "F-1", "Toby", null, "", LocalDate.of(2020, 1, 1), BigDecimal.ONE));
        assertTrue(ana.getMascotas().isEmpty());
    }

    @Test
    void noPermiteCrearSinClienteNiConClienteInactivo() {
        assertThrows(DatosInvalidosException.class, () -> new Mascota(null,
                "F-1", "Toby", Especie.PERRO, "", LocalDate.of(2020, 1, 1), BigDecimal.ONE));
        Cliente ana = cliente();
        ana.darDeBaja();
        assertThrows(EntidadInactivaException.class, () -> new Mascota(ana,
                "F-1", "Toby", Especie.PERRO, "", LocalDate.of(2020, 1, 1), BigDecimal.ONE));
    }

    @Test
    void calculaEdadEnAniosCompletosAntesYDespuesDelCumpleanios() {
        Mascota toby = mascota();
        assertEquals(5, toby.calcularEdad(LocalDate.of(2026, 3, 14)));
        assertEquals(6, toby.calcularEdad(LocalDate.of(2026, 3, 15)));
        assertEquals(0, toby.calcularEdad(LocalDate.of(2020, 3, 15)));
        assertThrows(DatosInvalidosException.class,
                () -> toby.calcularEdad(LocalDate.of(2019, 1, 1)));
        assertThrows(DatosInvalidosException.class, () -> toby.calcularEdad(null));
    }

    @Test
    void actualizarPesoMantieneLosOtrosDatosYSeObservaDesdeElCliente() {
        Mascota toby = mascota();
        Cliente ana = toby.getCliente();
        toby.actualizarPeso(new BigDecimal("11.0"));
        assertEquals(new BigDecimal("11.0"), ana.getMascotas().get(0).getPeso());
        assertEquals("Toby", toby.getNombre());
        assertEquals("F-1", toby.getNumeroFicha());
        assertSame(ana, toby.getCliente());
    }

    @Test
    void bajaLogicaConservaDatosYAsociacionPeroImpideModificaciones() {
        Mascota toby = mascota();
        toby.darDeBaja();
        assertFalse(toby.estaActiva());
        assertSame(toby, toby.getCliente().getMascotas().get(0));
        assertEquals("Toby", toby.getNombre());
        assertThrows(EntidadInactivaException.class, () -> toby.actualizarPeso(BigDecimal.ONE));
        assertThrows(EntidadInactivaException.class, () -> toby.cambiarDatos("Otro", ""));
    }
}
