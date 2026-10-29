package com.veterinaria.modelo;

import com.veterinaria.modelo.excepciones.DatosInvalidosException;
import java.math.BigDecimal;

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

    static BigDecimal pesoPositivo(BigDecimal peso) {
        if (peso == null || peso.signum() <= 0) {
            throw new DatosInvalidosException("El peso debe ser mayor que cero.");
        }
        return peso;
    }
}
