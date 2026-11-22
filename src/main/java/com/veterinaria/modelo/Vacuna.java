package com.veterinaria.modelo;
import com.veterinaria.modelo.excepciones.DatosInvalidosException;

public class Vacuna {

    private String nombreComercial;
    private String enfermedadQuePreviene;
    private int periodicidadMeses;


public Vacuna(String nombreComercial, String enfermedadQuePreviene, int periodicidadMeses) {
        this.nombreComercial = Validaciones.textoObligatorio(
            nombreComercial, "El nombre comercial");

        this.enfermedadQuePreviene = Validaciones.textoObligatorio(
            enfermedadQuePreviene, "La enfermedad que previene");

        if (periodicidadMeses <= 0) {
            throw new DatosInvalidosException(
                "La periodicidad debe ser mayor que cero.");
        }   

        this.periodicidadMeses = periodicidadMeses;


    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public String getEnfermedadQuePreviene() {
        return enfermedadQuePreviene;
    }

    public int getPeriodicidadMeses() {
        return periodicidadMeses;
    }

}