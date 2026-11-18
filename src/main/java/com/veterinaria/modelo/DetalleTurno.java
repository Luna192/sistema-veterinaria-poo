package com.veterinaria.modelo;

import com.veterinaria.modelo.excepciones.DatosInvalidosException;
import java.math.BigDecimal;
import java.util.Optional;

public class DetalleTurno {
    private final Servicio servicio;
    private final BigDecimal precioCongelado;
    private final int duracionMinutos;
    private String observaciones = "";
    private String diagnostico = "";
    private String tratamiento = "";

    public DetalleTurno(Servicio servicio) {
        if (servicio == null) {
            throw new DatosInvalidosException("El servicio es obligatorio.");
        }
        if (!servicio.estaActivo()) {
            throw new DatosInvalidosException("No se puede agregar un servicio inactivo.");
        }
        this.servicio = servicio;
        this.precioCongelado = servicio.calcularImporte();
        this.duracionMinutos = servicio.getDuracionMinutos();
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
}
