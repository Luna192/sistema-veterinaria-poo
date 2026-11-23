package com.veterinaria.modelo;

import java.math.BigDecimal;

public class Vacunacion extends Servicio {
    
    public Vacunacion(String nombre, BigDecimal precioBase, int duracionMinutos) {
        super(nombre, precioBase, duracionMinutos);  //llama al constructor de Servicio, que valida nombre, precio y duración
    }

    @Override
    public boolean requiereVeterinario() {
        return true;
    }
}