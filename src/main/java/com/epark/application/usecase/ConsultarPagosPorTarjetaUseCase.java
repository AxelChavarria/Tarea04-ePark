package com.epark.application.usecase;

import com.epark.application.dto.ConsultaPagosTarjeta;
import com.epark.application.dto.ResumenPago;
import com.epark.domain.model.Pago;
import com.epark.domain.ports.RepositorioPagos;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ConsultarPagosPorTarjetaUseCase {
    private final RepositorioPagos repositorioPagos;

    public ConsultarPagosPorTarjetaUseCase(RepositorioPagos repositorioPagos) {
        this.repositorioPagos = Objects.requireNonNull(repositorioPagos, "repositorioPagos es obligatorio");
    }

    public List<ResumenPago> ejecutar(ConsultaPagosTarjeta consulta) {
        Objects.requireNonNull(consulta, "consulta es obligatoria");

        List<Pago> pagos = repositorioPagos.buscarPorTarjetaYFecha(
                consulta.getIdTarjeta(),
                consulta.getFecha(),
                consulta.getIdUsuario()
        );

        return pagos.stream()
                .map(this::toResumen)
                .collect(Collectors.toList());
    }

    private ResumenPago toResumen(Pago pago) {
        return new ResumenPago(
                pago.getIdPago(),
                pago.getMonto(),
                pago.getFechaHora(),
                pago.getEstado(),
                pago.getReferenciaExterna()
        );
    }
}
