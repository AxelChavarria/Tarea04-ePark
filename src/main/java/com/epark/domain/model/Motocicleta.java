package com.epark.domain.model;

import com.epark.domain.enums.TipoVehiculo;

import java.math.BigDecimal;

public class Motocicleta extends Vehiculo {
    private static final BigDecimal FACTOR = new BigDecimal("0.75");

    public Motocicleta(String idVehiculo, String placa, Usuario propietario) {
        super(idVehiculo, placa, propietario, TipoVehiculo.MOTOCICLETA);
    }

    @Override
    public BigDecimal obtenerFactorTarifa() {
        return FACTOR;
    }
}
