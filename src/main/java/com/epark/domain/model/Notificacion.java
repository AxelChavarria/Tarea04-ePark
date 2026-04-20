package com.epark.domain.model;

import com.epark.domain.enums.CanalNotificacion;

import java.time.LocalDateTime;
import java.util.Objects;

public class Notificacion {
    private final String idNotificacion;
    private final String idUsuario;
    private final CanalNotificacion canal;
    private final String mensaje;
    private final LocalDateTime fechaHora;
    private boolean enviada;

    public Notificacion(
            String idNotificacion,
            String idUsuario,
            CanalNotificacion canal,
            String mensaje,
            LocalDateTime fechaHora
    ) {
        this.idNotificacion = Objects.requireNonNull(idNotificacion, "idNotificacion es obligatorio");
        this.idUsuario = Objects.requireNonNull(idUsuario, "idUsuario es obligatorio");
        this.canal = Objects.requireNonNull(canal, "canal es obligatorio");
        this.mensaje = Objects.requireNonNull(mensaje, "mensaje es obligatorio");
        this.fechaHora = Objects.requireNonNull(fechaHora, "fechaHora es obligatoria");
        this.enviada = false;
    }

    public void marcarEnviada() {
        this.enviada = true;
    }

    public String getIdNotificacion() {
        return idNotificacion;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public CanalNotificacion getCanal() {
        return canal;
    }

    public String getMensaje() {
        return mensaje;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public boolean isEnviada() {
        return enviada;
    }
}
