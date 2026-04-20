package com.epark.domain.model;

import com.epark.domain.enums.TipoVehiculo;

import java.math.BigDecimal;

public class Carro extends Vehiculo {
    private static final BigDecimal FACTOR = new BigDecimal("1.00");

    public Carro(String idVehiculo, String placa, Usuario propietario) {
        super(idVehiculo, placa, propietario, TipoVehiculo.CARRO);
    }

    @Override
    public BigDecimal obtenerFactorTarifa() {
        return FACTOR;
    }
}
