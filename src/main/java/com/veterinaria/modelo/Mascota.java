package com.veterinaria.modelo;

import com.veterinaria.modelo.excepciones.DatosInvalidosException;
import com.veterinaria.modelo.excepciones.EntidadInactivaException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

public class Mascota {
    private final Cliente cliente;
    private final String numeroFicha;
    private String nombre;
    private final Especie especie;
    private String raza;
    private final LocalDate fechaNacimiento;
    private BigDecimal peso;
    private boolean activa = true;

    // El alta pública se realiza a través de Cliente.registrarMascota(...).
    Mascota(Cliente cliente, String numeroFicha, String nombre, Especie especie,
            String raza, LocalDate fechaNacimiento, BigDecimal peso) {
        if (cliente == null) {
            throw new DatosInvalidosException("La mascota debe tener un cliente.");
        }
        if (!cliente.estaActivo()) {
            throw new EntidadInactivaException("El cliente debe estar activo.");
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
        this.peso = Validaciones.pesoPositivo(peso);
    }

    public void actualizarPeso(BigDecimal nuevoPeso) {
        exigirActiva();
        peso = Validaciones.pesoPositivo(nuevoPeso);
    }

    public void cambiarDatos(String nombre, String raza) {
        exigirActiva();
        String nuevoNombre = Validaciones.textoObligatorio(nombre, "El nombre");
        String nuevaRaza = Validaciones.textoOpcional(raza);
        this.nombre = nuevoNombre;
        this.raza = nuevaRaza;
    }

    public int calcularEdad(LocalDate fechaActual) {
        if (fechaActual == null || fechaActual.isBefore(fechaNacimiento)) {
            throw new DatosInvalidosException("La fecha de referencia no puede preceder al nacimiento.");
        }
        return Period.between(fechaNacimiento, fechaActual).getYears();
    }

    public void darDeBaja() { activa = false; }
    public boolean estaActiva() { return activa; }
    public Cliente getCliente() { return cliente; }
    public String getNumeroFicha() { return numeroFicha; }
    public String getNombre() { return nombre; }
    public Especie getEspecie() { return especie; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public BigDecimal getPeso() { return peso; }
    public Optional<String> getRaza() {
        return raza.isEmpty() ? Optional.empty() : Optional.of(raza);
    }

    private void exigirActiva() {
        if (!activa) {
            throw new EntidadInactivaException("La mascota está dada de baja.");
        }
    }
}
