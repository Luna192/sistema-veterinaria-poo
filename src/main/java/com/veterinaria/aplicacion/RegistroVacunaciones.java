package com.veterinaria.aplicacion;
import com.veterinaria.modelo.AplicacionVacuna;
import java.util.ArrayList;
import java.util.List;
import com.veterinaria.modelo.Mascota;
import com.veterinaria.modelo.Vacuna;
import com.veterinaria.modelo.excepciones.DatosInvalidosException;
import java.util.Optional;
import com.veterinaria.modelo.Veterinario;
import java.time.LocalDate;
import com.veterinaria.modelo.Turno;
import com.veterinaria.modelo.DetalleTurno;
import java.util.Comparator;

public class RegistroVacunaciones{
    private final List<AplicacionVacuna> aplicaciones = new ArrayList<>();

    public Optional<AplicacionVacuna> buscarUltimaAplicacion(
        Mascota mascota, Vacuna vacuna) {

    if (mascota == null || vacuna == null) {
        throw new DatosInvalidosException(
            "Debe indicar la mascota y la vacuna."
        );
    }

    AplicacionVacuna ultima = null;

    for (AplicacionVacuna aplicacion : aplicaciones) {
        if (aplicacion.getMascota() == mascota
                && aplicacion.getVacuna() == vacuna) {

            if (ultima == null
                    || aplicacion.getFechaAplicacion()
                        .isAfter(ultima.getFechaAplicacion())) {
                ultima = aplicacion;
            }
        }
    }

    return Optional.ofNullable(ultima);
    }   

    public AplicacionVacuna registrarSinTurno(
        Mascota mascota, Vacuna vacuna,
        Veterinario veterinario, LocalDate fecha) {

    Optional<AplicacionVacuna> ultima =
        buscarUltimaAplicacion(mascota, vacuna);

    if (ultima.isPresent()) {
        ultima.get().validarNuevaAplicacion(fecha);
    }

    AplicacionVacuna nueva = new AplicacionVacuna(
        mascota, vacuna, veterinario, fecha
    );

    aplicaciones.add(nueva);
    return nueva;
    }

    public AplicacionVacuna registrarConTurno(
        Turno turno, DetalleTurno detalle,
        Vacuna vacuna, LocalDate fecha) {

    if (turno == null) {
        throw new DatosInvalidosException(
            "Debe indicar el turno."
        );
    }

    Optional<AplicacionVacuna> ultima =
        buscarUltimaAplicacion(turno.getMascota(), vacuna);

    if (ultima.isPresent()) {
        ultima.get().validarNuevaAplicacion(fecha);
    }

    AplicacionVacuna nueva = turno.registrarVacunacion(
        detalle, vacuna, fecha, ultima
    );

    aplicaciones.add(nueva);
    return nueva;   
    }

    public List<AplicacionVacuna> consultarAplicaciones(Mascota mascota) {
    if (mascota == null) {
        throw new DatosInvalidosException(
            "La mascota es obligatoria."
        );
    }

    return aplicaciones.stream()
        .filter(aplicacion -> aplicacion.getMascota() == mascota)
        .sorted(Comparator.comparing(
            AplicacionVacuna::getFechaAplicacion
        ))
        .toList(); 
    }

}