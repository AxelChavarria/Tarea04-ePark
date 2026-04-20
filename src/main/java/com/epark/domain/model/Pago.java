package com.epark.domain.model;

import com.epark.domain.enums.EstadoPago;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Pago {
    private final String idPago;
    private final String idEstadia;
    private final String idUsuario;
    private final String idTarjeta;
    private final BigDecimal monto;
    private final LocalDateTime fechaHora;
    private EstadoPago estado;
    private String referenciaExterna;
    private String motivoRechazo;

    public Pago(
            String idPago,
            String idEstadia,
            String idUsuario,
            String idTarjeta,
            BigDecimal monto,
            LocalDateTime fechaHora
    ) {
        this.idPago = Objects.requireNonNull(idPago, "idPago es obligatorio");
        this.idEstadia = Objects.requireNonNull(idEstadia, "idEstadia es obligatorio");
        this.idUsuario = Objects.requireNonNull(idUsuario, "idUsuario es obligatorio");
        this.idTarjeta = Objects.requireNonNull(idTarjeta, "idTarjeta es obligatorio");
        this.monto = Objects.requireNonNull(monto, "monto es obligatorio");
        this.fechaHora = Objects.requireNonNull(fechaHora, "fechaHora es obligatoria");
        this.estado = EstadoPago.PENDIENTE;
    }

    public void aprobar(String referenciaExterna) {
        this.estado = EstadoPago.APROBADO;
        this.referenciaExterna = referenciaExterna;
        this.motivoRechazo = null;
    }

    public void rechazar(String referenciaExterna, String motivoRechazo) {
        this.estado = EstadoPago.RECHAZADO;
        this.referenciaExterna = referenciaExterna;
        this.motivoRechazo = motivoRechazo;
    }

    public String getIdPago() {
        return idPago;
    }

    public String getIdEstadia() {
        return idEstadia;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public String getIdTarjeta() {
        return idTarjeta;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public EstadoPago getEstado() {
        return estado;
    }

    public String getReferenciaExterna() {
        return referenciaExterna;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }
}
