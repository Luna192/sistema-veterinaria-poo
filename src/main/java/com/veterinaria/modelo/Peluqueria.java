package com.veterinaria.modelo;

import java.math.BigDecimal;

public class Peluqueria extends Servicio {
    public Peluqueria(String nombre, BigDecimal precioBase, int duracionMinutos) {
        super(nombre, precioBase, duracionMinutos);
    }

    @Override
    public boolean requiereVeterinario() {
        return false;
    }
}
