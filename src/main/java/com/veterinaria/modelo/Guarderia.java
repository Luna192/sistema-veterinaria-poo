package com.veterinaria.modelo;

import com.veterinaria.modelo.excepciones.DatosInvalidosException;
import java.math.BigDecimal;

public class Guarderia extends Servicio {
    private final int cupoMaximo;

    public Guarderia(String nombre, BigDecimal precioBase, int duracionMinutos, int cupoMaximo) {
        super(nombre, precioBase, duracionMinutos);
        if (cupoMaximo <= 0) {
            throw new DatosInvalidosException("El cupo máximo debe ser mayor que cero.");
        }
        this.cupoMaximo = cupoMaximo;
    }

    public int getCupoMaximo() {
        return cupoMaximo;
    }

    public boolean tieneCupo(int cantidadOcupada) {
        return cantidadOcupada < cupoMaximo;
    }

    @Override
    public boolean requiereVeterinario() {
        return false;
    }
}
