package com.epark.domain.model;

import java.util.Objects;

public class ResultadoCobro {
    private final boolean aprobado;
    private final String referencia;
    private final String motivoRechazo;

    private ResultadoCobro(boolean aprobado, String referencia, String motivoRechazo) {
        this.aprobado = aprobado;
        this.referencia = referencia;
        this.motivoRechazo = motivoRechazo;
    }

    public static ResultadoCobro aprobado(String referencia) {
        return new ResultadoCobro(true, Objects.requireNonNull(referencia, "referencia es obligatoria"), null);
    }

    public static ResultadoCobro rechazado(String referencia, String motivoRechazo) {
        return new ResultadoCobro(
                false,
                Objects.requireNonNull(referencia, "referencia es obligatoria"),
                Objects.requireNonNull(motivoRechazo, "motivoRechazo es obligatorio")
        );
    }

    public boolean isAprobado() {
        return aprobado;
    }

    public String getReferencia() {
        return referencia;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }
}
