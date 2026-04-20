package com.epark.application.dto;

import com.epark.domain.enums.EstadoPago;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class ResumenPago {
    private final String idPago;
    private final BigDecimal monto;
    private final LocalDateTime fechaHora;
    private final EstadoPago estado;
    private final String referencia;

    public ResumenPago(String idPago, BigDecimal monto, LocalDateTime fechaHora, EstadoPago estado, String referencia) {
        this.idPago = Objects.requireNonNull(idPago, "idPago es obligatorio");
        this.monto = Objects.requireNonNull(monto, "monto es obligatorio");
        this.fechaHora = Objects.requireNonNull(fechaHora, "fechaHora es obligatoria");
        this.estado = Objects.requireNonNull(estado, "estado es obligatorio");
        this.referencia = referencia;
    }

    public String getIdPago() {
        return idPago;
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

    public String getReferencia() {
        return referencia;
    }
}
