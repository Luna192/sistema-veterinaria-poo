package com.veterinaria.modelo;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AplicacionVacunaTest {

    @Test
    void calculaProximaDosisYControlaVigencia(){
        Cliente ana = new Cliente("Ana", "Pérez", "123", "+54 000 000000", "");
    
    
    Mascota toby = ana.registrarMascota("F-1", "Toby", Especie.PERRO, "",
        LocalDate.of(2020, 3, 15));

    Vacuna vacuna = new Vacuna("Antirrábica", "Rabia", 12);
    Veterinario veterinario = new Veterinario("MAT-123", "Laura", "Sosa");

    AplicacionVacuna aplicacion = new AplicacionVacuna(toby, vacuna, veterinario, LocalDate.of(2025, 11, 22));

    assertEquals(LocalDate.of(2026, 11, 22), aplicacion.getFechaProximaDosis());

    assertTrue(aplicacion.estaVigente(LocalDate.of(2026, 11, 21)));
    assertFalse(aplicacion.estaVigente(LocalDate.of(2026, 11, 22)));
    
    assertTrue(aplicacion.estaProximaAVencer(LocalDate.of(2026, 10, 23)));
    assertFalse(aplicacion.estaProximaAVencer(LocalDate.of(2026, 10, 22)));
    
    }

    public LocalDate getFechaAplicacion() {
        return fechaAplicacion;
    }

    public Mascota getMascota() {
        return mascota;
    }

    public Vacuna getVacuna() {
        return vacuna;
    }

    public Veterinario getVeterinario() {
        return veterinario;
    }


}