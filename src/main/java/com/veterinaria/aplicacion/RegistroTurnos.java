package com.veterinaria.aplicacion;

import com.veterinaria.modelo.Turno;
import com.veterinaria.modelo.excepciones.DatosInvalidosException;
import java.util.ArrayList;
import java.util.List;
import com.veterinaria.modelo.Mascota;
import com.veterinaria.modelo.EstadoTurno;
import java.util.Comparator;
import com.veterinaria.modelo.Guarderia;
import com.veterinaria.modelo.DetalleTurno;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;
import com.veterinaria.modelo.excepciones.EntidadInactivaException;

public class RegistroTurnos {
    private final List<Turno> turnos = new ArrayList<>();

    public void registrar(Turno nuevo) {
        if (nuevo == null) {
            throw new DatosInvalidosException(
                    "El turno es obligatorio.");
        }

        if (!nuevo.getMascota().estaActiva()) {
            throw new EntidadInactivaException(
                    "No se puede registrar un turno para una mascota dada de baja.");
        }

        if (turnos.contains(nuevo)) {
            throw new DatosInvalidosException(
                    "El turno ya está registrado.");
        }

        for (Turno existente : turnos) {
            if (existente.getMascota() == nuevo.getMascota()
                    && existente.seSolapaCon(nuevo)) {
                throw new DatosInvalidosException(
                        "La mascota ya tiene un turno en ese horario.");
            }

            if (existente.seSolapaVeterinarioCon(nuevo)) {
                throw new DatosInvalidosException(
                        "El veterinario no está disponible en ese horario.");
            }
        }

        validarCupoGuarderia(nuevo);
        turnos.add(nuevo);
    }

    public List<Turno> getTurnos() {
        return List.copyOf(turnos);
    }

    public List<Turno> consultarHistorial(Mascota mascota) {
        if (mascota == null) {
            throw new DatosInvalidosException(
                    "La mascota es obligatoria.");
        }

        return turnos.stream()
                .filter(turno -> turno.getMascota() == mascota)
                .filter(turno -> turno.getEstado() == EstadoTurno.ATENDIDO)
                .sorted(Comparator.comparing(Turno::getFechaHoraInicio))
                .toList();
    }

    public int calcularOcupacionMaxima(
            Guarderia guarderia,
            LocalDateTime inicio,
            LocalDateTime fin) {

        if (guarderia == null || inicio == null || fin == null) {
            throw new DatosInvalidosException(
                    "La guarderia y los horarios son obligatorios.");
        }

        if (!fin.isAfter(inicio)) {
            throw new DatosInvalidosException(
                    "El fin debe ser posterior al inicio.");
        }

        Map<LocalDateTime, Integer> eventos = new TreeMap<>();

        for (Turno turno : turnos) {

            if (!turno.ocupaHorario()) {
                continue;
            }

            for (DetalleTurno detalle : turno.getDetalles()) {

                if (!(detalle.getServicio() instanceof Guarderia)) {
                    continue;
                }

                LocalDateTime ingreso = turno.calcularInicioDetalle(detalle);

                LocalDateTime salida = ingreso.plusMinutes(detalle.getDuracionMinutos());

                if (!ingreso.isBefore(fin)
                        || !salida.isAfter(inicio)) {
                    continue;
                }

                LocalDateTime inicioSolapado = ingreso.isBefore(inicio) ? inicio : ingreso;

                LocalDateTime finSolapado = salida.isAfter(fin) ? fin : salida;

                eventos.merge(inicioSolapado, 1, Integer::sum);
                eventos.merge(finSolapado, -1, Integer::sum);
            }
        }

        int ocupacionActual = 0;
        int ocupacionMaxima = 0;

        for (int variacion : eventos.values()) {

            ocupacionActual += variacion;

            ocupacionMaxima = Math.max(
                    ocupacionMaxima, ocupacionActual);
        }

        return ocupacionMaxima;
    }

    private void validarCupoGuarderia(Turno nuevo) {

        for (DetalleTurno detalle : nuevo.getDetalles()) {

            if (detalle.getServicio() instanceof Guarderia guarderia) {

                LocalDateTime inicio = nuevo.calcularInicioDetalle(detalle);

                LocalDateTime fin = inicio.plusMinutes(detalle.getDuracionMinutos());

                if (!inicio.toLocalDate().equals(fin.toLocalDate())) {
                    throw new DatosInvalidosException(
                            "El ingreso y la salida de guardería deben ser el mismo día.");
                }

                int ocupacionMaxima = calcularOcupacionMaxima(
                        guarderia, inicio, fin);

                if (!guarderia.tieneCupo(ocupacionMaxima)) {

                    throw new DatosInvalidosException(
                            "No hay cupos disponibles en la guarderia "
                                    + "durante el horario solicitado.");
                }
            }
        }
    }
}