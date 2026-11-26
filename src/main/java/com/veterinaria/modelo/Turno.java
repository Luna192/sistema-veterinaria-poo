package com.veterinaria.modelo;

import com.veterinaria.modelo.excepciones.DatosInvalidosException;
import com.veterinaria.modelo.excepciones.EntidadInactivaException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.time.LocalDate;

public class Turno implements Facturable {
    private EstadoTurno estado = EstadoTurno.PENDIENTE;
    private final LocalDateTime fechaHoraInicio;
    private final Mascota mascota;
    private final Veterinario veterinario;
    private final List<DetalleTurno> detalles;

    public Turno(Mascota mascota, Veterinario veterinario, List<DetalleTurno> detalles,
            LocalDateTime fechaHoraInicio) {
        if (mascota == null) {
            throw new DatosInvalidosException("La mascota es obligatoria.");
        }
        if (!mascota.estaActiva()) {
            throw new EntidadInactivaException("La mascota debe estar activa.");
        }
        if (fechaHoraInicio == null || !fechaHoraInicio.isAfter(LocalDateTime.now())) {
            throw new DatosInvalidosException("El turno debe programarse para una fecha y hora futuras.");
        }
        if (detalles == null || detalles.isEmpty() || detalles.stream().anyMatch(detalle -> detalle == null)) {
            throw new DatosInvalidosException("El turno debe contener al menos un servicio.");
        }

        validarServiciosNoRepetidos(detalles);
        validarEspeciesAdmitidas(mascota, detalles);
        validarVeterinario(veterinario, detalles);

        for (DetalleTurno detalle : detalles) {
            detalle.validarDisponibleParaAsignacion();
        }

        this.mascota = mascota;
        this.veterinario = veterinario;
        this.detalles = List.copyOf(detalles);
        this.fechaHoraInicio = fechaHoraInicio;

        for (DetalleTurno detalle : this.detalles) {
            detalle.asignarTurno(this);
        }
    }

    public void confirmar() {
        exigirEstado(EstadoTurno.PENDIENTE, "Solo un turno pendiente puede confirmarse.");
        if (!mascota.estaActiva()) {
            throw new EntidadInactivaException("La mascota debe estar activa para confirmar el turno.");
        }
        estado = EstadoTurno.CONFIRMADO;
    }

    public void cancelar(LocalDateTime fechaActual) {
        if (estado != EstadoTurno.PENDIENTE && estado != EstadoTurno.CONFIRMADO) {
            throw new DatosInvalidosException("El turno no puede cancelarse en su estado actual.");
        }
        if (fechaActual == null
                || Duration.between(fechaActual, fechaHoraInicio).compareTo(Duration.ofHours(24)) <= 0) {
            throw new DatosInvalidosException("El turno solo puede cancelarse con más de 24 horas de anticipación.");
        }
        estado = EstadoTurno.CANCELADO;
    }

    public void marcarAtendido() {
        exigirEstado(EstadoTurno.CONFIRMADO, "Solo un turno confirmado puede marcarse como atendido.");
        if (!mascota.estaActiva()) {
            throw new EntidadInactivaException("La mascota debe estar activa para marcar el turno como atendido.");
        }
        boolean faltaDiagnostico = detalles.stream()
                .filter(detalle -> detalle.getServicio().requiereRegistroMedico())
                .anyMatch(detalle -> detalle.getDiagnostico().isEmpty());
        if (faltaDiagnostico) {
            throw new DatosInvalidosException("Debe registrarse el diagnóstico de la consulta médica.");
        }
        estado = EstadoTurno.ATENDIDO;
    }

    @Override
    public BigDecimal calcularImporte() {
        return detalles.stream()
                .map(DetalleTurno::getPrecioCongelado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int calcularDuracionTotal() {
        return detalles.stream().mapToInt(DetalleTurno::getDuracionMinutos).sum();
    }

    public LocalDateTime calcularFechaHoraFin() {
        return fechaHoraInicio.plusMinutes(calcularDuracionTotal());
    }

    public LocalDateTime calcularInicioDetalle(DetalleTurno detalle) {
        if (detalle == null || !detalles.contains(detalle)) {
            throw new DatosInvalidosException(
                    "El detalle no pertenece al turno.");
        }

        LocalDateTime inicio = fechaHoraInicio;

        for (DetalleTurno actual : detalles) {
            if (actual == detalle) {
                return inicio;
            }

            inicio = inicio.plusMinutes(actual.getDuracionMinutos());
        }

        throw new DatosInvalidosException(
                "El detalle no pertenece al turno.");
    }

    public LocalDateTime calcularFinDetalle(DetalleTurno detalle) {
        return calcularInicioDetalle(detalle)
                .plusMinutes(detalle.getDuracionMinutos());
    }

    public boolean seSolapaCon(Turno otroTurno) {
        if (otroTurno == null || !ocupaHorario() || !otroTurno.ocupaHorario()) {
            return false;
        }
        return fechaHoraInicio.isBefore(otroTurno.calcularFechaHoraFin())
                && otroTurno.fechaHoraInicio.isBefore(calcularFechaHoraFin());
    }

    public boolean seSolapaVeterinarioCon(Turno otroTurno) {
        if (otroTurno == null
                || !ocupaHorario()
                || !otroTurno.ocupaHorario()
                || veterinario == null
                || veterinario != otroTurno.veterinario) {
            return false;
        }

        for (DetalleTurno propio : detalles) {
            if (!propio.getServicio().requiereVeterinario()) {
                continue;
            }

            LocalDateTime inicioPropio = calcularInicioDetalle(propio);
            LocalDateTime finPropio = calcularFinDetalle(propio);

            for (DetalleTurno ajeno : otroTurno.detalles) {
                if (!ajeno.getServicio().requiereVeterinario()) {
                    continue;
                }

                LocalDateTime inicioAjeno = otroTurno.calcularInicioDetalle(ajeno);
                LocalDateTime finAjeno = otroTurno.calcularFinDetalle(ajeno);

                if (inicioPropio.isBefore(finAjeno)
                        && inicioAjeno.isBefore(finPropio)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean ocupaHorario() {
        return estado != EstadoTurno.CANCELADO;
    }

    public void registrarResultado(DetalleTurno detalle, String diagnostico, String tratamiento) {
        exigirEstado(EstadoTurno.CONFIRMADO, "Solo un turno confirmado puede registrar resultados.");
        if (!mascota.estaActiva()) {
            throw new EntidadInactivaException("La mascota debe estar activa para registrar resultados.");
        }
        if (detalle == null || !detalles.contains(detalle)) {
            throw new DatosInvalidosException("El detalle no pertenece al turno.");
        }
        if (!detalle.getServicio().requiereRegistroMedico()) {
            throw new DatosInvalidosException("El servicio no requiere registro médico.");
        }
        detalle.registrarResultado(diagnostico, tratamiento);
    }

    public AplicacionVacuna registrarVacunacion(DetalleTurno detalle, Vacuna vacuna, LocalDate fecha,
            Optional<AplicacionVacuna> ultimaAplicacion) {

        exigirEstado(
                EstadoTurno.CONFIRMADO, "Solo un turno confirmado puede registrar vacunaciones.");

        if (detalle == null || !detalles.contains(detalle)) {
            throw new DatosInvalidosException("El detalle no pertenece al turno.");
        }

        if (!(detalle.getServicio() instanceof Vacunacion)) {
            throw new DatosInvalidosException("El detalle debe corresponder a un servicio de vacunación.");
        }

        if (vacuna == null) {
            throw new DatosInvalidosException("Debe indicar qué vacuna se aplicó.");
        }

        if (veterinario == null) {
            throw new DatosInvalidosException("La vacunacion requiere un veterinario asignado.");
        }

        if (detalle.getAplicacionVacuna().isPresent()) {
            throw new DatosInvalidosException("Este detalle ya tiene una aplicación de vacuna registrada.");
        }

        if (ultimaAplicacion == null) {
            throw new DatosInvalidosException("Debe indicar si existe una aplicación anterior.");
        }

        if (ultimaAplicacion.isPresent()) {
            AplicacionVacuna anterior = ultimaAplicacion.get();

            if (anterior.getMascota() != mascota
                    || anterior.getVacuna() != vacuna) {
                throw new DatosInvalidosException(
                        "La aplicación anterior debe corresponder a la misma mascota y vacuna.");
            }

            anterior.validarNuevaAplicacion(fecha);
        }

        AplicacionVacuna nuevaAplicacion = new AplicacionVacuna(mascota, vacuna, veterinario, fecha);
        detalle.vincularAplicacionVacuna(nuevaAplicacion);
        return nuevaAplicacion;

    }

    public EstadoTurno getEstado() {
        return estado;
    }

    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public Mascota getMascota() {
        return mascota;
    }

    public Optional<Veterinario> getVeterinario() {
        return Optional.ofNullable(veterinario);
    }

    public List<DetalleTurno> getDetalles() {
        return detalles;
    }

    private void validarVeterinario(Veterinario veterinario, List<DetalleTurno> detalles) {
        boolean requiereVeterinario = detalles.stream()
                .map(DetalleTurno::getServicio)
                .anyMatch(Servicio::requiereVeterinario);
        if (requiereVeterinario && veterinario == null) {
            throw new DatosInvalidosException("El turno requiere un veterinario.");
        }
    }

    private void validarServiciosNoRepetidos(List<DetalleTurno> detalles) {
        Set<Servicio> servicios = new HashSet<>();
        for (DetalleTurno detalle : detalles) {
            if (!servicios.add(detalle.getServicio())) {
                throw new DatosInvalidosException("No se puede repetir un servicio en el mismo turno.");
            }
        }
    }

    private void validarEspeciesAdmitidas(Mascota mascota, List<DetalleTurno> detalles) {
        Especie especie = mascota.getEspecie();
        for (DetalleTurno detalle : detalles) {
            Servicio servicio = detalle.getServicio();
            if (!servicio.admiteEspecie(especie)) {
                throw new DatosInvalidosException(
                        "El servicio " + servicio.getNombre() + " no admite la especie " + especie + ".");
            }
        }
    }

    private void exigirEstado(EstadoTurno estadoEsperado, String mensaje) {
        if (estado != estadoEsperado) {
            throw new DatosInvalidosException(mensaje);
        }
    }

}
