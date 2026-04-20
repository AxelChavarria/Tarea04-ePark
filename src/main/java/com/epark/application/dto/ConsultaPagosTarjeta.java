package com.epark.application.dto;

import java.time.LocalDate;
import java.util.Objects;

public class ConsultaPagosTarjeta {
    private final String idUsuario;
    private final String idTarjeta;
    private final LocalDate fecha;

    public ConsultaPagosTarjeta(String idUsuario, String idTarjeta, LocalDate fecha) {
        this.idUsuario = Objects.requireNonNull(idUsuario, "idUsuario es obligatorio");
        this.idTarjeta = Objects.requireNonNull(idTarjeta, "idTarjeta es obligatorio");
        this.fecha = Objects.requireNonNull(fecha, "fecha es obligatoria");
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public String getIdTarjeta() {
        return idTarjeta;
    }

    public LocalDate getFecha() {
        return fecha;
    }
}
