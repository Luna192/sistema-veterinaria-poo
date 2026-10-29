package com.veterinaria;

import com.veterinaria.modelo.Cliente;
import com.veterinaria.modelo.Especie;
import com.veterinaria.modelo.Mascota;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Demostración en memoria. La interfaz final se desarrollará con JavaFX. */
public final class DemostracionDominio {
    private DemostracionDominio() { }

    public static void main(String[] args) {
        Cliente ana = new Cliente("Ana", "Ejemplo", "00000000", "+54 000 000000", "");
        Mascota toby = ana.registrarMascota("F-001", "Toby", Especie.PERRO, "Mestizo",
                LocalDate.of(2020, 3, 15), new BigDecimal("10.0"));
        toby.actualizarPeso(new BigDecimal("11.0"));
        System.out.println("Primera demostración del dominio veterinario (sin persistencia)");
        System.out.println("Cliente: " + ana.getNombre());
        System.out.println("Mascotas registradas: " + ana.getMascotas().size());
        System.out.println("Mascota: " + toby.getNombre() + " | Peso: " + toby.getPeso() + " kg");
        System.out.println("Asociación consistente: " + (toby.getCliente() == ana));
    }
}
