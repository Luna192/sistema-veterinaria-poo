package com.veterinaria.modelo;

import com.veterinaria.modelo.excepciones.DatosInvalidosException;
import java.math.BigDecimal;
import java.util.Optional;
import java.time.Duration;
import java.time.LocalDateTime;

public class DetalleTurno {
    private final Servicio servicio;
    private final BigDecimal precioCongelado;
    private final int duracionMinutos;
    private String observaciones = "";
    private String diagnostico = "";
    private String tratamiento = "";
    private AplicacionVacuna aplicacionVacuna;
    private Turno turno;

    public DetalleTurno(Servicio servicio) {
        if (servicio == null) {
            throw new DatosInvalidosException("El servicio es obligatorio.");
        }
        this.servicio = servicio;
        this.precioCongelado = servicio.calcularImporte();
        this.duracionMinutos = servicio.getDuracionMinutos();
    }

    public DetalleTurno(Servicio servicio, int duracionMinutos) {
        if (servicio == null) {
            throw new DatosInvalidosException("El servicio es obligatorio.");
        }
        if (duracionMinutos <= 0) {
            throw new DatosInvalidosException("La duración debe ser mayor que cero.");
        }
        if (!(servicio instanceof Guarderia)) {
            throw new DatosInvalidosException(
                    "La duración personalizada solo está permitida para servicios de guardería.");
        }
        this.servicio = servicio;
        this.precioCongelado = servicio.calcularImporte();
        this.duracionMinutos = duracionMinutos;
    }

    public void registrarObservaciones(String observaciones) {
        this.observaciones = Validaciones.textoOpcional(observaciones);
    }

    public Servicio getServicio() {
        return servicio;
    }

    public BigDecimal getPrecioCongelado() {
        return precioCongelado;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public Optional<String> getObservaciones() {
        return observaciones.isEmpty() ? Optional.empty() : Optional.of(observaciones);
    }

    public Optional<String> getDiagnostico() {
        return diagnostico.isEmpty() ? Optional.empty() : Optional.of(diagnostico);
    }

    public Optional<String> getTratamiento() {
        return tratamiento.isEmpty() ? Optional.empty() : Optional.of(tratamiento);
    }

    void registrarResultado(String diagnostico, String tratamiento) {
        this.diagnostico = Validaciones.textoObligatorio(diagnostico, "El diagnóstico");
        this.tratamiento = Validaciones.textoOpcional(tratamiento);
    }

    public Optional<AplicacionVacuna> getAplicacionVacuna() {
        return Optional.ofNullable(aplicacionVacuna);
    }

    void vincularAplicacionVacuna(AplicacionVacuna aplicacionVacuna) {
        if (aplicacionVacuna == null) {
            throw new DatosInvalidosException("La aplicación de vacuna es obligatoria.");
        }

        if (this.aplicacionVacuna != null) {
            throw new DatosInvalidosException("Ya existe una aplicación de vacuna vinculada a este detalle de turno.");
        }

        this.aplicacionVacuna = aplicacionVacuna;
    }

    void validarDisponibleParaAsignacion() {
        if (turno != null) {
            throw new DatosInvalidosException(
                    "El detalle ya pertenece a un turno.");
        }
    }

    void asignarTurno(Turno turno) {
        if (turno == null) {
            throw new DatosInvalidosException(
                    "El turno es obligatorio.");
        }

        validarDisponibleParaAsignacion();
        this.turno = turno;
    }

    public static DetalleTurno crearParaGuarderia(
            Guarderia guarderia,
            LocalDateTime ingreso,
            LocalDateTime salida) {

        if (ingreso == null || salida == null) {
            throw new DatosInvalidosException(
                    "El ingreso y la salida son obligatorios.");
        }

        if (!salida.isAfter(ingreso)) {
            throw new DatosInvalidosException(
                    "La salida debe ser posterior al ingreso.");
        }

        if (ingreso.getSecond() != 0 || ingreso.getNano() != 0
                || salida.getSecond() != 0 || salida.getNano() != 0) {
            throw new DatosInvalidosException(
                    "Los horarios deben indicarse con precisión de minutos.");
        }

        long minutos = Duration.between(ingreso, salida).toMinutes();

        if (minutos > Integer.MAX_VALUE) {
            throw new DatosInvalidosException(
                    "La duración de la estadía es demasiado grande.");
        }

        return new DetalleTurno(guarderia, (int) minutos);
    }
}
