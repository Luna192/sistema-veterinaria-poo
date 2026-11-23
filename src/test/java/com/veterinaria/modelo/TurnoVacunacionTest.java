package  com.veterinaria.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.veterinaria.modelo.excepciones.DatosInvalidosException;

class TurnoVacunacionTest {

    @Test
    void registrarPrimerAplicacionYLaVinculaAlDetalle(){

        Cliente ana = new Cliente ("Ana", "Pérez", "123", "+54 000 000000", "");

        Mascota toby = ana.registrarMascota("F-1", "Toby", Especie.PERRO, "",
            LocalDate.of(2020, 3, 15));

        Veterinario veterinario = new Veterinario("MAT-123", "Laura", "Sosa");

        Vacuna vacuna = new Vacuna("Antirrábica", "Rabia", 12);

        Vacunacion servicio = new Vacunacion("Aplicacion de vacuna", new BigDecimal("15000"), 15);

        DetalleTurno detalle = new DetalleTurno(servicio);

        Turno turno = new Turno(
            toby,
            veterinario,
            List.of(detalle),
            LocalDateTime.now().plusMinutes(5)
        );

        turno.confirmar();

        AplicacionVacuna aplicacion = turno.registrarVacunacion(
            detalle,
            vacuna,
            LocalDate.now(),
            Optional.empty()
        );

        assertSame(
            aplicacion,
            detalle.getAplicacionVacuna().orElseThrow()
        );

        assertSame(toby, aplicacion.getMascota());
        assertSame(vacuna, aplicacion.getVacuna());
        assertSame(veterinario, aplicacion.getVeterinario());

        assertThrows(DatosInvalidosException.class,
            () -> turno.registrarVacunacion(
                detalle,
                vacuna,
                LocalDate.now(),
                Optional.empty()
            )
        );

        assertSame(
            aplicacion,
            detalle.getAplicacionVacuna().orElseThrow()
        );

    }
}
