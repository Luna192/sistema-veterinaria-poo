package com.veterinaria.modelo;

import com.veterinaria.modelo.excepciones.DatosInvalidosException;
import com.veterinaria.modelo.excepciones.EntidadInactivaException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class ClienteTest {
    private Cliente cliente() {
        return new Cliente("Ana", "Ejemplo", "00000000", "+54 123 456", "");
    }

    private Mascota registrar(Cliente cliente, String ficha) {
        return cliente.registrarMascota(ficha, "Toby", Especie.PERRO, "",
                LocalDate.of(2020, 1, 1), BigDecimal.TEN);
    }

    @Test
    void clienteComienzaActivoSinMascotas() {
        Cliente ana = cliente();
        assertTrue(ana.estaActivo());
        assertTrue(ana.getMascotas().isEmpty());
        assertTrue(ana.getEmail().isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void rechazaNombreApellidoODniAusentes(String dato) {
        assertThrows(DatosInvalidosException.class,
                () -> new Cliente(dato, "Ejemplo", "1", "123", ""));
        assertThrows(DatosInvalidosException.class,
                () -> new Cliente("Ana", dato, "1", "123", ""));
        assertThrows(DatosInvalidosException.class,
                () -> new Cliente("Ana", "Ejemplo", dato, "123", ""));
    }

    @Test
    void exigeAlMenosUnMedioDeContacto() {
        assertThrows(DatosInvalidosException.class,
                () -> new Cliente("Ana", "Ejemplo", "1", null, " "));
        Cliente conEmail = new Cliente("Ana", "Ejemplo", "1", "", "ana@example.org");
        assertTrue(conEmail.getTelefono().isEmpty());
        assertEquals("ana@example.org", conEmail.getEmail().orElseThrow());
    }

    @ParameterizedTest
    @ValueSource(strings = {"sin-arroba", "a@@b.com", "a@b", "a b@c.com"})
    void rechazaCorreoInvalidoSinModificarContactoAnterior(String email) {
        Cliente ana = cliente();
        assertThrows(DatosInvalidosException.class, () -> ana.actualizarContacto("999", email));
        assertEquals("+54 123 456", ana.getTelefono().orElseThrow());
        assertTrue(ana.getEmail().isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "+() .-", "123/456"})
    void rechazaTelefonoInvalido(String telefono) {
        assertThrows(DatosInvalidosException.class,
                () -> cliente().actualizarContacto(telefono, ""));
    }

    @Test
    void registroEstableceAmbosExtremosConLaMismaInstancia() {
        Cliente ana = cliente();
        Mascota toby = registrar(ana, "F-1");
        assertSame(ana, toby.getCliente());
        assertSame(toby, ana.getMascotas().get(0));
    }

    @Test
    void rechazaFichaRepetidaInclusoSiLaMascotaAnteriorEstaInactiva() {
        Cliente ana = cliente();
        registrar(ana, "F-1").darDeBaja();
        assertThrows(DatosInvalidosException.class, () -> registrar(ana, " F-1 "));
        assertEquals(1, ana.getMascotas().size());
    }

    @Test
    void nombresIgualesNoConfundenLaIdentidad() {
        Cliente ana = cliente();
        Mascota primero = registrar(ana, "F-1");
        Mascota segundo = registrar(ana, "F-2");
        assertEquals(primero.getNombre(), segundo.getNombre());
        assertNotSame(primero, segundo);
        primero.actualizarPeso(new BigDecimal("11"));
        assertEquals(BigDecimal.TEN, segundo.getPeso());
    }

    @Test
    void datosInvalidosNoAgreganUnaMascotaParcial() {
        Cliente ana = cliente();
        assertThrows(DatosInvalidosException.class, () -> ana.registrarMascota(
                "F-1", "Toby", Especie.PERRO, "", LocalDate.of(2020, 1, 1), BigDecimal.ZERO));
        assertTrue(ana.getMascotas().isEmpty());
    }

    @Test
    void coleccionExternaNoPermiteRomperLaAsociacion() {
        Cliente ana = cliente();
        Mascota toby = registrar(ana, "F-1");
        List<Mascota> copia = ana.getMascotas();
        assertThrows(UnsupportedOperationException.class, copia::clear);
        registrar(ana, "F-2");
        assertEquals(1, copia.size());
        assertEquals(2, ana.getMascotas().size());
        assertSame(toby, ana.getMascotas().get(0));
    }

    @Test
    void bajaDeClienteConservaMascotasEImpideAltas() {
        Cliente ana = cliente();
        Mascota toby = registrar(ana, "F-1");
        ana.darDeBaja();
        assertFalse(ana.estaActivo());
        assertSame(toby, ana.getMascotas().get(0));
        assertThrows(EntidadInactivaException.class, () -> registrar(ana, "F-2"));
    }
}
