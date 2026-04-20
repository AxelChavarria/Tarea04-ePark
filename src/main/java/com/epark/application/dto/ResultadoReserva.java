package com.epark.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class ResultadoReserva {
    private final String idEstadia;
    private final BigDecimal montoReserva;
    private final LocalDateTime horaVencimiento;
    private final boolean pagoAprobado;
    private final String mensaje;

    private ResultadoReserva(
            String idEstadia,
            BigDecimal montoReserva,
            LocalDateTime horaVencimiento,
            boolean pagoAprobado,
            String mensaje
    ) {
        this.idEstadia = Objects.requireNonNull(idEstadia, "idEstadia es obligatorio");
        this.montoReserva = Objects.requireNonNull(montoReserva, "montoReserva es obligatorio");
        this.horaVencimiento = Objects.requireNonNull(horaVencimiento, "horaVencimiento es obligatoria");
        this.pagoAprobado = pagoAprobado;
        this.mensaje = Objects.requireNonNull(mensaje, "mensaje es obligatorio");
    }

    public static ResultadoReserva aprobada(String idEstadia, BigDecimal montoReserva, LocalDateTime horaVencimiento) {
        return new ResultadoReserva(idEstadia, montoReserva, horaVencimiento, true, "Reserva y pago aprobados");
    }

    public static ResultadoReserva rechazada(String idEstadia, BigDecimal montoReserva, LocalDateTime horaVencimiento, String motivo) {
        return new ResultadoReserva(
                idEstadia,
                montoReserva,
                horaVencimiento,
                false,
                "Reserva rechazada: " + Objects.requireNonNull(motivo, "motivo es obligatorio")
        );
    }

    public String getIdEstadia() {
        return idEstadia;
    }

    public BigDecimal getMontoReserva() {
        return montoReserva;
    }

    public LocalDateTime getHoraVencimiento() {
        return horaVencimiento;
    }

    public boolean isPagoAprobado() {
        return pagoAprobado;
    }

    public String getMensaje() {
        return mensaje;
    }
}
