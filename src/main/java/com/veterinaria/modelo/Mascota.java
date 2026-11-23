package com.veterinaria.modelo;

import com.veterinaria.modelo.excepciones.DatosInvalidosException;
import com.veterinaria.modelo.excepciones.EntidadInactivaException;
import java.time.LocalDate;
import java.util.Optional;

public class Mascota {
    private final Cliente cliente;
    private final String numeroFicha;
    private String nombre;
    private Especie especie;
    private String raza;
    private LocalDate fechaNacimiento;
    private boolean activa = true;

    Mascota(Cliente cliente, String numeroFicha, String nombre, Especie especie,String raza, LocalDate fechaNacimiento) {
        if (cliente == null) {
            throw new DatosInvalidosException("La mascota debe tener un cliente.");
        }
        if (especie == null) {
            throw new DatosInvalidosException("La especie es obligatoria.");
        }
        if (fechaNacimiento == null || fechaNacimiento.isAfter(LocalDate.now())) {
            throw new DatosInvalidosException("La fecha de nacimiento debe existir y no ser futura.");
        }
        this.cliente = cliente;
        this.numeroFicha = Validaciones.textoObligatorio(numeroFicha, "El número de ficha");
        this.nombre = Validaciones.textoObligatorio(nombre, "El nombre");
        this.especie = especie;
        this.raza = Validaciones.textoOpcional(raza);
        this.fechaNacimiento = fechaNacimiento;

    }

   public void editarDatos(String nombre, Especie especie, String raza, LocalDate fechaNacimiento){
        exigirActiva();

        String nuevoNombre = Validaciones.textoObligatorio(nombre, "El nombre");
        String nuevaRaza = Validaciones.textoOpcional(raza);

        if (especie == null) {
            throw new DatosInvalidosException("La especie es obligatoria.");
        }

        if (fechaNacimiento == null || fechaNacimiento.isAfter(LocalDate.now())) {
            throw new DatosInvalidosException("La fecha de nacimiento debe existir y no ser futura.");
        }


        this.nombre = nuevoNombre;
        this.especie = especie;
        this.raza = nuevaRaza;
        this.fechaNacimiento = fechaNacimiento;
        
    }

    public void darDeBaja() { activa = false; }
    public boolean estaActiva() { return activa; }
    public Cliente getCliente() { return cliente; }
    public String getNumeroFicha() { return numeroFicha; }
    public String getNombre() { return nombre; }
    public Especie getEspecie() { return especie; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public Optional<String> getRaza() {
        return raza.isEmpty() ? Optional.empty() : Optional.of(raza);
    }

    private void exigirActiva() {
        if (!activa) {
            throw new EntidadInactivaException("La mascota está dada de baja.");
        }
    }


}
