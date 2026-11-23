package com.veterinaria.modelo;

import com.veterinaria.modelo.excepciones.DatosInvalidosException;
import com.veterinaria.modelo.excepciones.EntidadInactivaException;
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
        if (!mascota.estaActiva()) {
            throw new EntidadInactivaException(
                "La mascota debe estar activa para recibir una aplicación."
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

    public LocalDate getFechaAplicacion() {
        return fechaAplicacion;
    }

    public Mascota getMascota() {
        return mascota;
    }

    public Vacuna getVacuna() {
        return vacuna;
    }

    public Veterinario getVeterinario() {
        return veterinario;
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
        return estaProximaAVencer(fechaActual, 30);
    }

    public boolean estaProximaAVencer(LocalDate fechaActual, int diasAlerta) {
        if (diasAlerta <= 0) {
            throw new DatosInvalidosException(
                "Los días de alerta deben ser mayores que cero."
            );
        }
        return estaVigente(fechaActual)
            && !fechaProximaDosis.isAfter(fechaActual.plusDays(diasAlerta));
    }

    public void validarNuevaAplicacion(LocalDate fechaNuevaAplicacion) {
        if (fechaNuevaAplicacion == null || fechaNuevaAplicacion.isBefore(fechaAplicacion)) {
            throw new DatosInvalidosException(
                "La nueva aplicación no puede ser anterior a la última registrada."
            );   
        }

        if (estaVigente(fechaNuevaAplicacion)) {
            throw new DatosInvalidosException(
                "La mascota todavía tiene la vacuna vigente.");
        }
    }

}