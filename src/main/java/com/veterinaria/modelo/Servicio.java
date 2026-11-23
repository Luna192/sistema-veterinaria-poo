package com.veterinaria.modelo;

import com.veterinaria.modelo.excepciones.DatosInvalidosException;
import java.math.BigDecimal;

public abstract class Servicio implements Facturable {
    private final String nombre;
    private BigDecimal precioBase;
    private final int duracionMinutos;

    protected Servicio(String nombre, BigDecimal precioBase, int duracionMinutos) {
        this.nombre = Validaciones.textoObligatorio(nombre, "El nombre del servicio");
        this.precioBase = validarPrecio(precioBase);
        if (duracionMinutos <= 0) {
            throw new DatosInvalidosException("La duración debe ser mayor que cero.");
        }
        this.duracionMinutos = duracionMinutos;
    }

    public void actualizarPrecio(BigDecimal nuevoPrecio) {
        precioBase = validarPrecio(nuevoPrecio);
    }

    @Override
    public BigDecimal calcularImporte() {
        return precioBase;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean requiereRegistroMedico() {
        return false;
    }

    public boolean requiereVeterinario() {
        return false;
    }

    public boolean admiteEspecie(Especie especie) {
        return especie != null;
    }

    private BigDecimal validarPrecio(BigDecimal precio) {
        if (precio == null || precio.signum() < 0) {
            throw new DatosInvalidosException("El precio base no puede ser negativo.");
        }
        return precio;
    }
}
