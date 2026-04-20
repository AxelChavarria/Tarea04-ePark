package com.epark.domain.ports;

import com.epark.domain.model.Notificacion;

public interface ServicioNotificacion {
    void enviar(Notificacion notificacion);
}
