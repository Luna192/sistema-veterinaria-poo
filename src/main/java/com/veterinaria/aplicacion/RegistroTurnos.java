package com.veterinaria.aplicacion;

import com.veterinaria.modelo.Turno;
import com.veterinaria.modelo.excepciones.DatosInvalidosException;
import java.util.ArrayList;
import java.util.List;
import com.veterinaria.modelo.Mascota;
import com.veterinaria.modelo.EstadoTurno;
import java.util.Comparator;

public class RegistroTurnos {
    private final List<Turno> turnos = new ArrayList<>();

    public void registrar(Turno nuevo) {
        if (nuevo == null) {
            throw new DatosInvalidosException(
                "El turno es obligatorio."
            );
        }

        if (turnos.contains(nuevo)) {
            throw new DatosInvalidosException(
                "El turno ya está registrado."
            );
        }

        for (Turno existente : turnos) {
            if (existente.getMascota() == nuevo.getMascota()
                    && existente.seSolapaCon(nuevo)) {
                throw new DatosInvalidosException(
                    "La mascota ya tiene un turno en ese horario."
                );
            }

            if (existente.seSolapaVeterinarioCon(nuevo)) {
                throw new DatosInvalidosException(
                    "El veterinario no está disponible en ese horario."
                );
            }
        }

        turnos.add(nuevo);
    }

    public List<Turno> getTurnos() {
        return List.copyOf(turnos);
    }

    public List<Turno> consultarHistorial(Mascota mascota) {
    if (mascota == null) {
        throw new DatosInvalidosException(
            "La mascota es obligatoria."
        );
    }

    return turnos.stream()
        .filter(turno -> turno.getMascota() == mascota)
        .filter(turno -> turno.getEstado() == EstadoTurno.ATENDIDO)
        .sorted(Comparator.comparing(Turno::getFechaHoraInicio))
        .toList();
    }
}