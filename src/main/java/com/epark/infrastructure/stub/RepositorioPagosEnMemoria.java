package com.epark.infrastructure.stub;

import com.epark.domain.model.Pago;
import com.epark.domain.ports.RepositorioPagos;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class RepositorioPagosEnMemoria implements RepositorioPagos {
    private final List<Pago> base = new CopyOnWriteArrayList<>();

    @Override
    public void guardar(Pago pago) {
        base.add(pago);
    }

    @Override
    public List<Pago> buscarPorTarjetaYFecha(String idTarjeta, LocalDate fecha, String idUsuario) {
        return base.stream()
                .filter(p -> p.getIdTarjeta().equals(idTarjeta))
                .filter(p -> p.getFechaHora().toLocalDate().equals(fecha))
                .filter(p -> p.getIdUsuario().equals(idUsuario))
                .collect(Collectors.toList());
    }
}
