package com.epark.application.dto;

import java.util.Objects;

public class SolicitudEstacionamiento {
    private final String idUsuario;
    private final String idVehiculo;
    private final String idParquimetro;
    private final int minutosSolicitados;
    private final String idTarjeta;

    public SolicitudEstacionamiento(
            String idUsuario,
            String idVehiculo,
            String idParquimetro,
            int minutosSolicitados,
            String idTarjeta
    ) {
        this.idUsuario = Objects.requireNonNull(idUsuario, "idUsuario es obligatorio");
        this.idVehiculo = Objects.requireNonNull(idVehiculo, "idVehiculo es obligatorio");
        this.idParquimetro = Objects.requireNonNull(idParquimetro, "idParquimetro es obligatorio");
        if (minutosSolicitados <= 0) {
            throw new IllegalArgumentException("minutosSolicitados debe ser mayor que cero");
        }
        this.minutosSolicitados = minutosSolicitados;
        this.idTarjeta = Objects.requireNonNull(idTarjeta, "idTarjeta es obligatorio");
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public String getIdVehiculo() {
        return idVehiculo;
    }

    public String getIdParquimetro() {
        return idParquimetro;
    }

    public int getMinutosSolicitados() {
        return minutosSolicitados;
    }

    public String getIdTarjeta() {
        return idTarjeta;
    }
}
