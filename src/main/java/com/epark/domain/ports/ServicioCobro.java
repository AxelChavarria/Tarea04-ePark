package com.epark.domain.ports;

import com.epark.domain.model.ResultadoCobro;

import java.math.BigDecimal;

public interface ServicioCobro {
    ResultadoCobro cobrar(String idUsuario, String idTarjeta, BigDecimal monto, String descripcion);
}
