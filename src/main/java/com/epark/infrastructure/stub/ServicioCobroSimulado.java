package com.epark.infrastructure.stub;

import com.epark.domain.model.ResultadoCobro;
import com.epark.domain.ports.ServicioCobro;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class ServicioCobroSimulado implements ServicioCobro {
    @Override
    public ResultadoCobro cobrar(String idUsuario, String idTarjeta, BigDecimal monto, String descripcion) {
        Objects.requireNonNull(idUsuario, "idUsuario es obligatorio");
        Objects.requireNonNull(idTarjeta, "idTarjeta es obligatorio");
        Objects.requireNonNull(monto, "monto es obligatorio");
        Objects.requireNonNull(descripcion, "descripcion es obligatoria");

        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            return ResultadoCobro.rechazado("REF-MONTO-INVALIDO", "Monto invalido");
        }

        if (idTarjeta.endsWith("0000")) {
            return ResultadoCobro.rechazado("REF-TARJETA-BLOQUEADA", "Tarjeta rechazada por simulador");
        }

        String referencia = "CBR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return ResultadoCobro.aprobado(referencia);
    }
}
