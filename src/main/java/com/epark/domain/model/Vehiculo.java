package com.epark.domain.model;

import com.epark.domain.enums.TipoVehiculo;

import java.math.BigDecimal;
import java.util.Objects;

public abstract class Vehiculo {
    private final String idVehiculo;
    private final String placa;
    private final Usuario propietario;
    private final TipoVehiculo tipoVehiculo;

    protected Vehiculo(String idVehiculo, String placa, Usuario propietario, TipoVehiculo tipoVehiculo) {
        this.idVehiculo = Objects.requireNonNull(idVehiculo, "idVehiculo es obligatorio");
        this.placa = Objects.requireNonNull(placa, "placa es obligatoria");
        this.propietario = Objects.requireNonNull(propietario, "propietario es obligatorio");
        this.tipoVehiculo = Objects.requireNonNull(tipoVehiculo, "tipoVehiculo es obligatorio");
    }

    public abstract BigDecimal obtenerFactorTarifa();

    public String getIdVehiculo() {
        return idVehiculo;
    }

    public String getPlaca() {
        return placa;
    }

    public Usuario getPropietario() {
        return propietario;
    }

    public TipoVehiculo getTipoVehiculo() {
        return tipoVehiculo;
    }
}
