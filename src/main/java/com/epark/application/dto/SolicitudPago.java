package com.epark.application.dto;

import java.math.BigDecimal;
import java.util.Objects;

public class SolicitudPago {
    private final String idUsuario;
    private final String idTarjeta;
    private final BigDecimal monto;
    private final String descripcion;

    public SolicitudPago(String idUsuario, String idTarjeta, BigDecimal monto, String descripcion) {
        this.idUsuario = Objects.requireNonNull(idUsuario, "idUsuario es obligatorio");
        this.idTarjeta = Objects.requireNonNull(idTarjeta, "idTarjeta es obligatorio");
        this.monto = Objects.requireNonNull(monto, "monto es obligatorio");
        this.descripcion = Objects.requireNonNull(descripcion, "descripcion es obligatoria");
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

    public String getDescripcion() {
        return descripcion;
    }
}
