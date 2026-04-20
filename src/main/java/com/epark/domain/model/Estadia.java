package com.epark.domain.model;

import com.epark.domain.enums.EstadoEstadia;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Estadia {
    private final String idEstadia;
    private final Usuario usuario;
    private final Vehiculo vehiculo;
    private final ZonaParqueo zonaParqueo;
    private final LocalDateTime horaInicio;
    private final LocalDateTime horaFinProgramada;
    private final BigDecimal montoEstimado;
    private BigDecimal montoPagado;
    private EstadoEstadia estado;

    public Estadia(
            String idEstadia,
            Usuario usuario,
            Vehiculo vehiculo,
            ZonaParqueo zonaParqueo,
            LocalDateTime horaInicio,
            LocalDateTime horaFinProgramada,
            BigDecimal montoEstimado,
            EstadoEstadia estado
    ) {
        this.idEstadia = Objects.requireNonNull(idEstadia, "idEstadia es obligatorio");
        this.usuario = Objects.requireNonNull(usuario, "usuario es obligatorio");
        this.vehiculo = Objects.requireNonNull(vehiculo, "vehiculo es obligatorio");
        this.zonaParqueo = Objects.requireNonNull(zonaParqueo, "zonaParqueo es obligatoria");
        this.horaInicio = Objects.requireNonNull(horaInicio, "horaInicio es obligatoria");
        this.horaFinProgramada = Objects.requireNonNull(horaFinProgramada, "horaFinProgramada es obligatoria");
        this.montoEstimado = Objects.requireNonNull(montoEstimado, "montoEstimado es obligatorio");
        this.estado = Objects.requireNonNull(estado, "estado es obligatorio");
    }

    public synchronized void activar() {
        if (estado != EstadoEstadia.BORRADOR) {
            throw new IllegalStateException("Solo una estadia BORRADOR puede pasar a ACTIVA");
        }
        estado = EstadoEstadia.ACTIVA;
    }

    public synchronized void confirmarPago(BigDecimal montoPagado) {
        this.montoPagado = Objects.requireNonNull(montoPagado, "montoPagado es obligatorio");
    }

    public synchronized void cancelar() {
        if (estado == EstadoEstadia.VENCIDA) {
            throw new IllegalStateException("No se puede cancelar una estadia VENCIDA");
        }
        estado = EstadoEstadia.CANCELADA;
    }

    public synchronized void vencer() {
        if (estado == EstadoEstadia.ACTIVA) {
            estado = EstadoEstadia.VENCIDA;
        }
    }

    public boolean estaProximaAVencer(LocalDateTime ahora, long minutosAnticipacion) {
        Objects.requireNonNull(ahora, "ahora es obligatorio");
        if (minutosAnticipacion <= 0) {
            throw new IllegalArgumentException("minutosAnticipacion debe ser mayor que cero");
        }
        if (estado != EstadoEstadia.ACTIVA) {
            return false;
        }

        LocalDateTime inicioVentana = horaFinProgramada.minusMinutes(minutosAnticipacion);
        boolean dentroDeVentana = (ahora.isEqual(inicioVentana) || ahora.isAfter(inicioVentana))
                && ahora.isBefore(horaFinProgramada);
        return dentroDeVentana;
    }

    public String getIdEstadia() {
        return idEstadia;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public ZonaParqueo getZonaParqueo() {
        return zonaParqueo;
    }

    public LocalDateTime getHoraInicio() {
        return horaInicio;
    }

    public LocalDateTime getHoraFinProgramada() {
        return horaFinProgramada;
    }

    public BigDecimal getMontoEstimado() {
        return montoEstimado;
    }

    public BigDecimal getMontoPagado() {
        return montoPagado;
    }

    public EstadoEstadia getEstado() {
        return estado;
    }
}
