package com.veterinaria.modelo;

import com.veterinaria.modelo.excepciones.DatosInvalidosException;
import com.veterinaria.modelo.excepciones.EntidadInactivaException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Cliente {
    private final String nombre;
    private final String apellido;
    private final String dni;
    private String telefono;
    private String email;
    private boolean activo = true;
    private final List<Mascota> mascotas = new ArrayList<>();

    public Cliente(String nombre, String apellido, String dni, String telefono, String email) {
        this.nombre = Validaciones.textoObligatorio(nombre, "El nombre");
        this.apellido = Validaciones.textoObligatorio(apellido, "El apellido");
        this.dni = Validaciones.textoObligatorio(dni, "El DNI");
        actualizarContacto(telefono, email);
    }

    public void actualizarContacto(String telefono, String email) {
        String nuevoTelefono = Validaciones.textoOpcional(telefono);
        String nuevoEmail = Validaciones.textoOpcional(email);
        if (nuevoTelefono.isEmpty() && nuevoEmail.isEmpty()) {
            throw new DatosInvalidosException("Se requiere teléfono o correo electrónico.");
        }
        if (!nuevoTelefono.isEmpty() && !nuevoTelefono.matches("[+0-9() .-]+")) {
            throw new DatosInvalidosException("El teléfono contiene caracteres inválidos.");
        }
        if (!nuevoTelefono.isEmpty() && nuevoTelefono.chars().noneMatch(Character::isDigit)) {
            throw new DatosInvalidosException("El teléfono debe contener números.");
        }
        if (!nuevoEmail.isEmpty() && !nuevoEmail.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
            throw new DatosInvalidosException("El correo electrónico no tiene un formato válido.");
        }
        // Se asignan ambos valores solo después de validar toda la operación.
        this.telefono = nuevoTelefono;
        this.email = nuevoEmail;
    }

    public Mascota registrarMascota(String numeroFicha, String nombre, Especie especie,
                                   String raza, LocalDate fechaNacimiento, BigDecimal peso) {
        if (!activo) {
            throw new EntidadInactivaException("Un cliente inactivo no puede registrar mascotas.");
        }
        String ficha = Validaciones.textoObligatorio(numeroFicha, "El número de ficha");
        if (mascotas.stream().anyMatch(m -> m.getNumeroFicha().equals(ficha))) {
            throw new DatosInvalidosException("El cliente ya tiene una mascota con esa ficha.");
        }
        Mascota mascota = new Mascota(this, ficha, nombre, especie, raza, fechaNacimiento, peso);
        mascotas.add(mascota);
        return mascota;
    }

    public void darDeBaja() { activo = false; }
    public boolean estaActivo() { return activo; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getDni() { return dni; }
    public Optional<String> getTelefono() {
        return telefono.isEmpty() ? Optional.empty() : Optional.of(telefono);
    }
    public Optional<String> getEmail() {
        return email.isEmpty() ? Optional.empty() : Optional.of(email);
    }
    public List<Mascota> getMascotas() { return List.copyOf(mascotas); }
}
