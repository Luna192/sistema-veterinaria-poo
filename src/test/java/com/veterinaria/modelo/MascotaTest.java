package com.veterinaria.modelo;

import com.veterinaria.modelo.excepciones.DatosInvalidosException;
import com.veterinaria.modelo.excepciones.EntidadInactivaException;
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
                LocalDate.of(2020, 3, 15)
            );
    }


    @ParameterizedTest  // Ejecuta la misma prueba con distintos nombres inválidos para la mascota.
    @NullAndEmptySource  // Prueba con valores nulos y vacíos para el nombre de la mascota.
    @ValueSource(strings = {" "})  // Prueba con un espacio en blanco como nombre de la mascota.
    void rechazaNombreVacioYConservaDatosSiFallaLaEdicion(String nombre) {
        Mascota toby = mascota();

        assertThrows(DatosInvalidosException.class,     // Verifica que se lance una excepción de DatosInvalidosException al intentar cambiar los datos de la mascota con un nombre inválido.
            () -> toby.editarDatos(
            nombre, Especie.PERRO, "Nueva raza",
            LocalDate.of(2020, 3, 15))
        );
        
        assertEquals("Toby", toby.getNombre());         // Verifica que el nombre de la mascota no haya cambiado después de la excepción.
        assertTrue(toby.getRaza().isEmpty());

        assertThrows(DatosInvalidosException.class,     // Verifica que se lance una excepción de DatosInvalidosException al intentar cambiar los datos de la mascota con un nombre inválido.
            () -> cliente().registrarMascota(
                "F-2", nombre, Especie.PERRO, "", 
                LocalDate.of(2020, 1, 1))
            );
    }

    @Test
    void rechazaFechaFuturaNulaYEspecieAusente() {
        Cliente ana = cliente();

        assertThrows(DatosInvalidosException.class, 
            () -> ana.registrarMascota(
                "F-1", "Toby", Especie.PERRO, "", 
                LocalDate.now().plusDays(1))
            );

        assertThrows(DatosInvalidosException.class, 
            () -> ana.registrarMascota(
                "F-1", "Toby", Especie.PERRO, "", null
                )
            );

        assertThrows(DatosInvalidosException.class, 
            () -> ana.registrarMascota(
                "F-1", "Toby", null, "", 
                LocalDate.of(2020, 1, 1))
            );

        assertTrue(ana.getMascotas().isEmpty());
    }

    @Test
    void noPermiteCrearSinCliente() {
        assertThrows(DatosInvalidosException.class, 
            () -> new Mascota(
                null, "F-1", "Toby", Especie.PERRO, "", 
                LocalDate.of(2020, 1, 1)
            )
        );

    }


    @Test  // Verifica que al editar los datos de la mascota, los cambios se reflejen correctamente en el cliente y que la identidad de la mascota se conserve.
    void editarDatosSeObservaDesdeElClienteYConservaIdentidad() {
        Mascota toby = mascota();
        Cliente ana = toby.getCliente();

        toby.editarDatos(
            "Tobías", Especie.GATO, "Siamés",
            LocalDate.of(2021, 5, 10)
        );

        assertEquals("Tobías", ana.getMascotas().get(0).getNombre());       
        assertEquals(Especie.GATO, toby.getEspecie());
        assertEquals("Siamés", toby.getRaza().orElseThrow());
        assertEquals(LocalDate.of(2021, 5, 10), toby.getFechaNacimiento());
        assertEquals("F-1", toby.getNumeroFicha());
        assertSame(ana, toby.getCliente());
        assertSame(toby, ana.getMascotas().get(0));
    }

    @Test
    void bajaLogicaConservaDatosYAsociacionPeroImpideModificaciones() {
        Mascota toby = mascota();
        toby.darDeBaja();

        assertFalse(toby.estaActiva());
        assertSame(toby, toby.getCliente().getMascotas().get(0));
        assertEquals("Toby", toby.getNombre());
        
        
        assertThrows(EntidadInactivaException.class, 
            () -> toby.editarDatos(
                "Otro", Especie.PERRO, "",
                LocalDate.of(2020, 3, 15)
            )
        );
        
        assertEquals("Toby", toby.getNombre());
    }
}
