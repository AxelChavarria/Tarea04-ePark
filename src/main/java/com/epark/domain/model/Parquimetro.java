package com.epark.domain.model;

import com.epark.domain.enums.EstadoEstadia;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Parquimetro {
    private final String idParquimetro;
    private final ZonaParqueo zonaParqueo;

    public Parquimetro(String idParquimetro, ZonaParqueo zonaParqueo) {
        this.idParquimetro = Objects.requireNonNull(idParquimetro, "idParquimetro es obligatorio");
        this.zonaParqueo = Objects.requireNonNull(zonaParqueo, "zonaParqueo es obligatoria");
    }

    public Estadia crearBorradorEstadia(Usuario usuario, Vehiculo vehiculo, LocalDateTime horaInicio, int minutos) {
        Objects.requireNonNull(usuario, "usuario es obligatorio");
        Objects.requireNonNull(vehiculo, "vehiculo es obligatorio");
        Objects.requireNonNull(horaInicio, "horaInicio es obligatoria");
        if (minutos <= 0) {
            throw new IllegalArgumentException("minutos debe ser mayor que cero");
        }

        if (!zonaParqueo.reservarCupo()) {
            throw new IllegalStateException("No hay cupos disponibles en la zona");
        }

        BigDecimal montoEstimado = zonaParqueo.calcularMontoEstimado(minutos, vehiculo.obtenerFactorTarifa());
        return new Estadia(
                UUID.randomUUID().toString(),
                usuario,
                vehiculo,
                zonaParqueo,
                horaInicio,
                horaInicio.plusMinutes(minutos),
                montoEstimado,
                EstadoEstadia.BORRADOR
        );
    }

    public void liberarCupo() {
        zonaParqueo.liberarCupo();
    }

    public String getIdParquimetro() {
        return idParquimetro;
    }

    public ZonaParqueo getZonaParqueo() {
        return zonaParqueo;
    }
}
