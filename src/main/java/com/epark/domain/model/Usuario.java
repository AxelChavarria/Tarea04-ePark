package com.epark.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Usuario {
    private final String idUsuario;
    private final String nombreCompleto;
    private final String correo;
    private final List<TarjetaCredito> tarjetas;

    public Usuario(String idUsuario, String nombreCompleto, String correo) {
        this.idUsuario = Objects.requireNonNull(idUsuario, "idUsuario es obligatorio");
        this.nombreCompleto = Objects.requireNonNull(nombreCompleto, "nombreCompleto es obligatorio");
        this.correo = Objects.requireNonNull(correo, "correo es obligatorio");
        this.tarjetas = new ArrayList<>();
    }

    public void registrarTarjeta(TarjetaCredito tarjeta) {
        tarjetas.add(Objects.requireNonNull(tarjeta, "tarjeta es obligatoria"));
    }

    public Optional<TarjetaCredito> buscarTarjeta(String idTarjeta) {
        return tarjetas.stream()
                .filter(t -> t.getIdTarjeta().equals(idTarjeta))
                .findFirst();
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getCorreo() {
        return correo;
    }

    public List<TarjetaCredito> getTarjetas() {
        return Collections.unmodifiableList(tarjetas);
    }
}
