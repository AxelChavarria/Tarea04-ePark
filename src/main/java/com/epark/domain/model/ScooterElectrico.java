package com.epark.domain.model;

import com.epark.domain.enums.TipoVehiculo;

import java.math.BigDecimal;

public class ScooterElectrico extends Vehiculo {
    private static final BigDecimal FACTOR = new BigDecimal("0.60");

    public ScooterElectrico(String idVehiculo, String placa, Usuario propietario) {
        super(idVehiculo, placa, propietario, TipoVehiculo.SCOOTER_ELECTRICO);
    }

    @Override
    public BigDecimal obtenerFactorTarifa() {
        return FACTOR;
    }
}
