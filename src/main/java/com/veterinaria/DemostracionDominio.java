package com.veterinaria;

import com.veterinaria.modelo.Cliente;
import com.veterinaria.modelo.Especie;
import com.veterinaria.modelo.Mascota;
import com.veterinaria.modelo.Vacuna;
import com.veterinaria.modelo.Veterinario;
import com.veterinaria.modelo.Especialidad;
import com.veterinaria.modelo.excepciones.EntidadInactivaException;
import com.veterinaria.modelo.excepciones.DatosInvalidosException;
import java.time.LocalDate;

/** Demostración en memoria. La interfaz final se desarrollará con JavaFX. */
public final class DemostracionDominio {
    private DemostracionDominio() { }

    public static void main(String[] args) {
        Cliente ana = new Cliente("Ana", "Ejemplo", "00000000", "+54 000 000000", "");
        Mascota toby = ana.registrarMascota("F-001", "Toby", Especie.PERRO, "Mestizo",
                LocalDate.of(2020, 3, 15));
        
        toby.darDeBaja();

        try {
            toby.editarDatos(
                "Tobias",
                Especie.PERRO,
                "Mestizo",
                LocalDate.of(2020, 3, 15)
            );
        } 
        
        catch (EntidadInactivaException error) {
            System.out.println("No se pudo editar: " + error.getMessage());
        }

        try {
            ana.registrarMascota("F-001", "Luna", Especie.GATO, "Mestiza", LocalDate.of(2022, 5, 10));
        }
        
        catch (DatosInvalidosException error) {
            System.out.println("No se pudo registrar la mascota: " + error.getMessage());
        }

        System.out.println("Primera demostración del dominio veterinario (sin persistencia)");
        System.out.println("Cliente: " + ana.getNombre());
        System.out.println("Mascotas registradas: " + ana.getMascotas().size());
        System.out.println("Mascota: " + toby.getNombre() + " | Ficha: " + toby.getNumeroFicha());
        System.out.println("Asociación consistente: " + (toby.getCliente() == ana));


        Vacuna antirrabica = new Vacuna("Antirrábica", "Rabia", 12);
        System.out.println("Vacuna: " + antirrabica.getNombreComercial());
        System.out.println(
            "Periodicidad: " + antirrabica.getPeriodicidadMeses() + " meses"
        );


        Veterinario laura = new Veterinario(
            "MAT-123",
            "Laura",
            "Sosa"
        );

        laura.agregarEspecialidad(Especialidad.CLINICA_GENERAL);

        System.out.println(
            "Veterinario: " + laura.getNombre() + " " + laura.getApellido()
        );

        System.out.println("Matricula: " + laura.getMatricula());
        System.out.println("Especialidades: " + laura.getEspecialidades());

    }
}
