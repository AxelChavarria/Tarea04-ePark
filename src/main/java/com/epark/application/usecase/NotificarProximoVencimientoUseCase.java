package com.epark.application.usecase;

import com.epark.domain.enums.CanalNotificacion;
import com.epark.domain.model.Estadia;
import com.epark.domain.model.Notificacion;
import com.epark.domain.ports.RelojSistema;
import com.epark.domain.ports.RepositorioEstadias;
import com.epark.domain.ports.ServicioNotificacion;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class NotificarProximoVencimientoUseCase {
    private final RepositorioEstadias repositorioEstadias;
    private final ServicioNotificacion servicioNotificacion;
    private final RelojSistema relojSistema;

    public NotificarProximoVencimientoUseCase(
            RepositorioEstadias repositorioEstadias,
            ServicioNotificacion servicioNotificacion,
            RelojSistema relojSistema
    ) {
        this.repositorioEstadias = Objects.requireNonNull(repositorioEstadias, "repositorioEstadias es obligatorio");
        this.servicioNotificacion = Objects.requireNonNull(servicioNotificacion, "servicioNotificacion es obligatorio");
        this.relojSistema = Objects.requireNonNull(relojSistema, "relojSistema es obligatorio");
    }

    public int ejecutar(long minutosAnticipacion) {
        if (minutosAnticipacion <= 0) {
            throw new IllegalArgumentException("minutosAnticipacion debe ser mayor que cero");
        }

        LocalDateTime ahora = relojSistema.ahora();
        List<Estadia> proximas = repositorioEstadias.buscarProximasAVencer(ahora, minutosAnticipacion);
        int totalEnviadas = 0;

        for (Estadia estadia : proximas) {
            String mensaje = "Tu parqueo vence en " + minutosAnticipacion + " minutos. Estadia: " + estadia.getIdEstadia();
            Notificacion notificacion = new Notificacion(
                    UUID.randomUUID().toString(),
                    estadia.getUsuario().getIdUsuario(),
                    CanalNotificacion.APP_PUSH,
                    mensaje,
                    ahora
            );
            servicioNotificacion.enviar(notificacion);
            totalEnviadas++;
        }

        return totalEnviadas;
    }
}
