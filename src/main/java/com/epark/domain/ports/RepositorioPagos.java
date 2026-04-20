package com.epark.domain.ports;

import com.epark.domain.model.Pago;

import java.time.LocalDate;
import java.util.List;

public interface RepositorioPagos {
    void guardar(Pago pago);

    List<Pago> buscarPorTarjetaYFecha(String idTarjeta, LocalDate fecha, String idUsuario);
}
