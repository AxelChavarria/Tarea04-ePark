package com.epark.domain.model;

import java.util.Objects;

public class TarjetaCredito {
    private final String idTarjeta;
    private final String titular;
    private final String ultimosCuatro;
    private final String tokenPasarela;

    public TarjetaCredito(String idTarjeta, String titular, String ultimosCuatro, String tokenPasarela) {
        this.idTarjeta = Objects.requireNonNull(idTarjeta, "idTarjeta es obligatorio");
        this.titular = Objects.requireNonNull(titular, "titular es obligatorio");
        this.ultimosCuatro = Objects.requireNonNull(ultimosCuatro, "ultimosCuatro es obligatorio");
        this.tokenPasarela = Objects.requireNonNull(tokenPasarela, "tokenPasarela es obligatorio");
    }

    public String getIdTarjeta() {
        return idTarjeta;
    }

    public String getTitular() {
        return titular;
    }

    public String getUltimosCuatro() {
        return ultimosCuatro;
    }

    public String getTokenPasarela() {
        return tokenPasarela;
    }
}
