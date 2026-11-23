package com.veterinaria.modelo;

import com.veterinaria.modelo.excepciones.DatosInvalidosException;

final class Validaciones {
    private Validaciones() { }

    static String textoObligatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new DatosInvalidosException(campo + " es obligatorio.");
        }
        return valor.strip();
    }

    static String textoOpcional(String valor) {
        return valor == null ? "" : valor.strip();
    }
}
