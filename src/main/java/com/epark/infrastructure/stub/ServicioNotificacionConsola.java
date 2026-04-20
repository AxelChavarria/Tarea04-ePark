package com.epark.infrastructure.stub;

import com.epark.domain.model.Notificacion;
import com.epark.domain.ports.ServicioNotificacion;

import java.util.Objects;

public class ServicioNotificacionConsola implements ServicioNotificacion {
    @Override
    public void enviar(Notificacion notificacion) {
        Objects.requireNonNull(notificacion, "notificacion es obligatoria");
        System.out.println(
                "[Notificacion] usuario=" + notificacion.getIdUsuario()
                        + " canal=" + notificacion.getCanal()
                        + " mensaje=" + notificacion.getMensaje()
        );
        notificacion.marcarEnviada();
    }
}
