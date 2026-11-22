package com.veterinaria.modelo;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.util.Optional;
import com.veterinaria.modelo.excepciones.DatosInvalidosException;

public class Cliente {              // Clase que representa a un cliente de la veterinaria.

    private String nombre;
    private String apellido;
    private String dni;
    private String telefono;
    private String email;
    private List<Mascota> mascotas = new ArrayList<>();

    public Cliente(String nombre, String apellido, String dni, String telefono, String email) {     // Constructor de la clase Cliente.
        this.nombre = Validaciones.textoObligatorio(nombre, "El nombre");
        this.apellido = Validaciones.textoObligatorio(apellido, "El apellido");
        this.dni = Validaciones.textoObligatorio(dni, "El DNI");
        actualizarContacto(telefono, email);                   // Llamada al método para actualizar el contacto del cliente.
    }
    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getDni() {
        return dni;
    }

    public void actualizarContacto(String telefono, String email) {         // Método para actualizar el contacto del cliente.
        String nuevoTelefono = Validaciones.textoOpcional(telefono);
        String nuevoEmail = Validaciones.textoOpcional(email);

        if (nuevoTelefono.isEmpty() && nuevoEmail.isEmpty()) {
            throw new DatosInvalidosException("Se requiere teléfono o email de contacto.");     // Validación para asegurar que al menos uno de los campos de contacto esté presente.
        }
        
        if (!nuevoTelefono.isEmpty()                                        // Validación para asegurar que el teléfono contenga números y solo caracteres permitidos.
              && !nuevoTelefono.matches("(?=.*[0-9])[+0-9() .-]+")) {
            throw new DatosInvalidosException("El teléfono debe contener números y solo caracteres permitidos.");
        }

        if (!nuevoEmail.isEmpty()                                       // Validación para asegurar que el correo electrónico tenga un formato válido.
        && !nuevoEmail.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
            throw new DatosInvalidosException("El correo electrónico no tiene un formato válido.");
        }

        this.telefono = nuevoTelefono; 
        this.email = nuevoEmail;  
    }  

    public Optional<String> getTelefono() {  // Método para obtener el teléfono del cliente, devuelve un Optional que puede estar vacío si no hay teléfono registrado.
        if (telefono.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(telefono);
    }

    public Optional<String> getEmail() {
        if (email.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(email);
    }

     public List<Mascota> getMascotas() {
            return List.copyOf(mascotas);         // Devuelve una copia de la lista de mascotas para evitar modificaciones externas.
        }
        
    public Mascota registrarMascota(String numeroFicha, String nombre,
                               Especie especie, String raza,
                               LocalDate fechaNacimiento) {
    String ficha = Validaciones.textoObligatorio(
        numeroFicha, "El número de ficha"
    );

    for (Mascota mascota : mascotas) {
        if (mascota.getNumeroFicha().equals(ficha)) {
            throw new DatosInvalidosException(
                "Ya existe una mascota con el mismo número de ficha."
            );
        }
    }

    Mascota nuevaMascota = new Mascota(
        this, ficha, nombre, especie, raza, fechaNacimiento
    );

    mascotas.add(nuevaMascota);  // Agrega la nueva mascota a la lista de mascotas del cliente.
    return nuevaMascota;        // Devuelve la nueva mascota creada.
}
}
