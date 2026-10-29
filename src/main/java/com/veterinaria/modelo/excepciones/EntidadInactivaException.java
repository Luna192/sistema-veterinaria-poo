package com.veterinaria.modelo.excepciones;

public class EntidadInactivaException extends RuntimeException {
    public EntidadInactivaException(String mensaje) {
        super(mensaje);
    }
}
