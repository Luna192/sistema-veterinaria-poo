package com.veterinaria.modelo;

import java.math.BigDecimal;

public class ConsultaMedica extends Servicio {
    public ConsultaMedica(String nombre, BigDecimal precioBase, int duracionMinutos) {
        super(nombre, precioBase, duracionMinutos);
    }

    @Override
    public boolean requiereRegistroMedico() {
        return true;
    }

    @Override
    public boolean requiereVeterinario() {
        return true;
    }
}
