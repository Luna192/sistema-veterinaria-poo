package com.veterinaria.modelo;

import com.veterinaria.modelo.excepciones.DatosInvalidosException;
import com.veterinaria.modelo.excepciones.EntidadInactivaException;
import java.time.LocalDate;
import java.util.Optional;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "mascotas")
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(name = "numero_ficha", nullable = false, unique = true)
    private String numeroFicha;

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Especie especie;

    @Column(nullable = false)
    private String raza;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(nullable = false)
    private boolean activa = true;

    protected Mascota() {
        // Constructor requerido por JPA.
    }

    Mascota(Cliente cliente, String numeroFicha, String nombre,
            Especie especie, String raza, LocalDate fechaNacimiento) {

        if (cliente == null) {
            throw new DatosInvalidosException(
                "La mascota debe tener un cliente."
            );
        }

        if (especie == null) {
            throw new DatosInvalidosException(
                "La especie es obligatoria."
            );
        }

        if (fechaNacimiento == null
                || fechaNacimiento.isAfter(LocalDate.now())) {
            throw new DatosInvalidosException(
                "La fecha de nacimiento debe existir y no ser futura."
            );
        }

        this.cliente = cliente;
        this.numeroFicha = Validaciones.textoObligatorio(
            numeroFicha, "El número de ficha"
        );
        this.nombre = Validaciones.textoObligatorio(nombre, "El nombre");
        this.especie = especie;
        this.raza = Validaciones.textoOpcional(raza);
        this.fechaNacimiento = fechaNacimiento;
    }

    public void editarDatos(String nombre, Especie especie,
                            String raza, LocalDate fechaNacimiento) {
        exigirActiva();

        String nuevoNombre = Validaciones.textoObligatorio(
            nombre, "El nombre"
        );
        String nuevaRaza = Validaciones.textoOpcional(raza);

        if (especie == null) {
            throw new DatosInvalidosException(
                "La especie es obligatoria."
            );
        }

        if (fechaNacimiento == null
                || fechaNacimiento.isAfter(LocalDate.now())) {
            throw new DatosInvalidosException(
                "La fecha de nacimiento debe existir y no ser futura."
            );
        }

        this.nombre = nuevoNombre;
        this.especie = especie;
        this.raza = nuevaRaza;
        this.fechaNacimiento = fechaNacimiento;
    }

    public void darDeBaja() {
        activa = false;
    }

    public boolean estaActiva() {
        return activa;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public String getNumeroFicha() {
        return numeroFicha;
    }

    public String getNombre() {
        return nombre;
    }

    public Especie getEspecie() {
        return especie;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public Optional<String> getRaza() {
        return raza.isEmpty()
            ? Optional.empty()
            : Optional.of(raza);
    }

    private void exigirActiva() {
        if (!activa) {
            throw new EntidadInactivaException(
                "La mascota está dada de baja."
            );
        }
    }
}