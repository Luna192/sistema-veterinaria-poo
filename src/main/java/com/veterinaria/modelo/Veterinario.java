package com.veterinaria.modelo;

import com.veterinaria.modelo.excepciones.EntidadInactivaException;
import java.util.LinkedHashSet;
import java.util.Set;

public class Veterinario {
    private final String matricula;
    private final String nombre;
    private final String apellido;
    private final Set<Especialidad> especialidades = new LinkedHashSet<>();
    private boolean activo = true;

    public Veterinario(String matricula, String nombre, String apellido) {
        this.matricula = Validaciones.textoObligatorio(matricula, "La matrícula");
        this.nombre = Validaciones.textoObligatorio(nombre, "El nombre");
        this.apellido = Validaciones.textoObligatorio(apellido, "El apellido");
    }

    public void agregarEspecialidad(Especialidad especialidad) {
        exigirActivo();
        if (especialidad == null) {
            throw new IllegalArgumentException("La especialidad es obligatoria.");
        }
        especialidades.add(especialidad);
    }

    public boolean tieneEspecialidad(Especialidad especialidad) {
        return especialidades.contains(especialidad);
    }

    public void darDeBaja() {
        activo = false;
    }

    public boolean estaActivo() {
        return activo;
    }

    public String getMatricula() {
        return matricula;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public Set<Especialidad> getEspecialidades() {
        return Set.copyOf(especialidades);
    }

    private void exigirActivo() {
        if (!activo) {
            throw new EntidadInactivaException("El veterinario está dado de baja.");
        }
    }
}
