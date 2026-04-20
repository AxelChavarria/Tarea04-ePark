package com.epark.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class ZonaParqueo {
    private final String idZona;
    private final String nombreZona;
    private final BigDecimal tarifaHoraBase;
    private final int cuposTotales;
    private int cuposDisponibles;

    public ZonaParqueo(String idZona, String nombreZona, BigDecimal tarifaHoraBase, int cuposTotales) {
        this.idZona = Objects.requireNonNull(idZona, "idZona es obligatorio");
        this.nombreZona = Objects.requireNonNull(nombreZona, "nombreZona es obligatorio");
        this.tarifaHoraBase = Objects.requireNonNull(tarifaHoraBase, "tarifaHoraBase es obligatoria");
        if (tarifaHoraBase.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("tarifaHoraBase debe ser mayor que cero");
        }
        if (cuposTotales <= 0) {
            throw new IllegalArgumentException("cuposTotales debe ser mayor que cero");
        }
        this.cuposTotales = cuposTotales;
        this.cuposDisponibles = cuposTotales;
    }

    public synchronized boolean reservarCupo() {
        if (cuposDisponibles <= 0) {
            return false;
        }
        cuposDisponibles--;
        return true;
    }

    public synchronized void liberarCupo() {
        if (cuposDisponibles < cuposTotales) {
            cuposDisponibles++;
        }
    }

    public BigDecimal calcularMontoEstimado(int minutos, BigDecimal factorVehiculo) {
        if (minutos <= 0) {
            throw new IllegalArgumentException("minutos debe ser mayor que cero");
        }
        Objects.requireNonNull(factorVehiculo, "factorVehiculo es obligatorio");

        BigDecimal tarifaPorMinuto = tarifaHoraBase.divide(BigDecimal.valueOf(60), 8, RoundingMode.HALF_UP);
        return tarifaPorMinuto
                .multiply(BigDecimal.valueOf(minutos))
                .multiply(factorVehiculo)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public String getIdZona() {
        return idZona;
    }

    public String getNombreZona() {
        return nombreZona;
    }

    public BigDecimal getTarifaHoraBase() {
        return tarifaHoraBase;
    }

    public int getCuposTotales() {
        return cuposTotales;
    }

    public synchronized int getCuposDisponibles() {
        return cuposDisponibles;
    }
}
