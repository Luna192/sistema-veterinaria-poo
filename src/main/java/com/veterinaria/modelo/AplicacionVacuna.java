package com.veterinaria.modelo;

import com.veterinaria.modelo.excepciones.DatosInvalidosException;
import java.time.LocalDate;

public class AplicacionVacuna {
    private LocalDate fechaAplicacion;
    private LocalDate fechaProximaDosis;
    private Mascota mascota;
    private Vacuna vacuna;
    private Veterinario veterinario;


    public AplicacionVacuna(Mascota mascota, Vacuna vacuna,Veterinario veterinario,LocalDate fechaAplicacion) {

        if (mascota == null) {
            throw new DatosInvalidosException(
                "La aplicacion debe tener una mascota."
            );   
        }

        if (vacuna == null) {
            throw new DatosInvalidosException(
                "La aplicacion debe indicar qué vacuna se aplicó."
            );   
        }

        if (veterinario == null) {
            throw new DatosInvalidosException(
                "La aplicacion debe indicar qué veterinario aplicó la vacuna."
            );   
        }

        if (fechaAplicacion == null || fechaAplicacion.isAfter(LocalDate.now())) {
            throw new DatosInvalidosException(
                "La fecha de aplicación debe existir y no ser futura."
            );   
        }

        if (fechaAplicacion.isBefore(mascota.getFechaNacimiento())) {
            throw new DatosInvalidosException(
                "La fecha de aplicación no puede ser anterior a la fecha de nacimiento de la mascota."
            );   
        }

        this.mascota = mascota;
        this.vacuna = vacuna;
        this.veterinario = veterinario;
        this.fechaAplicacion = fechaAplicacion;
        this.fechaProximaDosis = calcularFechaProximaDosis();

    }

    private LocalDate calcularFechaProximaDosis() {
        return fechaAplicacion.plusMonths(vacuna.getPeriodicidadMeses());
    }

    public LocalDate getFechaProximaDosis() {
        return fechaProximaDosis;
    }

    public boolean estaVigente(LocalDate fechaActual){
        if (fechaActual == null) {
            throw new DatosInvalidosException(
                "La fecha de consulta es obligatoria."
            );   
        }

        return !fechaActual.isBefore(fechaAplicacion) && fechaActual.isBefore(fechaProximaDosis);
    }

    public boolean estaProximaAVencer(LocalDate fechaActual) {
        return estaVigente(fechaActual)
            && !fechaProximaDosis.isAfter(fechaActual.plusDays(30));

    }

}