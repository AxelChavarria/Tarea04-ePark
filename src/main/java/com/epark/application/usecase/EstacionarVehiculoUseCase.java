package com.epark.application.usecase;

import com.epark.application.dto.ResultadoReserva;
import com.epark.application.dto.SolicitudEstacionamiento;
import com.epark.domain.model.Estadia;
import com.epark.domain.model.Pago;
import com.epark.domain.model.Parquimetro;
import com.epark.domain.model.ResultadoCobro;
import com.epark.domain.model.Usuario;
import com.epark.domain.model.Vehiculo;
import com.epark.domain.ports.RelojSistema;
import com.epark.domain.ports.RepositorioEstadias;
import com.epark.domain.ports.RepositorioPagos;
import com.epark.domain.ports.ServicioCobro;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class EstacionarVehiculoUseCase {
    private final RepositorioEstadias repositorioEstadias;
    private final RepositorioPagos repositorioPagos;
    private final ServicioCobro servicioCobro;
    private final RelojSistema relojSistema;

    public EstacionarVehiculoUseCase(
            RepositorioEstadias repositorioEstadias,
            RepositorioPagos repositorioPagos,
            ServicioCobro servicioCobro,
            RelojSistema relojSistema
    ) {
        this.repositorioEstadias = Objects.requireNonNull(repositorioEstadias, "repositorioEstadias es obligatorio");
        this.repositorioPagos = Objects.requireNonNull(repositorioPagos, "repositorioPagos es obligatorio");
        this.servicioCobro = Objects.requireNonNull(servicioCobro, "servicioCobro es obligatorio");
        this.relojSistema = Objects.requireNonNull(relojSistema, "relojSistema es obligatorio");
    }

    public ResultadoReserva ejecutar(
            SolicitudEstacionamiento solicitud,
            Usuario usuario,
            Vehiculo vehiculo,
            Parquimetro parquimetro
    ) {
        Objects.requireNonNull(solicitud, "solicitud es obligatoria");
        Objects.requireNonNull(usuario, "usuario es obligatorio");
        Objects.requireNonNull(vehiculo, "vehiculo es obligatorio");
        Objects.requireNonNull(parquimetro, "parquimetro es obligatorio");
        validarConsistencia(solicitud, usuario, vehiculo, parquimetro);

        LocalDateTime ahora = relojSistema.ahora();
        Estadia estadia = parquimetro.crearBorradorEstadia(usuario, vehiculo, ahora, solicitud.getMinutosSolicitados());
        repositorioEstadias.guardar(estadia);

        ResultadoCobro resultadoCobro = servicioCobro.cobrar(
                solicitud.getIdUsuario(),
                solicitud.getIdTarjeta(),
                estadia.getMontoEstimado(),
                "Reserva de parqueo " + estadia.getIdEstadia()
        );

        Pago pago = new Pago(
                UUID.randomUUID().toString(),
                estadia.getIdEstadia(),
                solicitud.getIdUsuario(),
                solicitud.getIdTarjeta(),
                estadia.getMontoEstimado(),
                ahora
        );

        if (resultadoCobro.isAprobado()) {
            pago.aprobar(resultadoCobro.getReferencia());
            estadia.confirmarPago(estadia.getMontoEstimado());
            estadia.activar();
            repositorioPagos.guardar(pago);
            repositorioEstadias.guardar(estadia);
            return ResultadoReserva.aprobada(
                    estadia.getIdEstadia(),
                    estadia.getMontoEstimado(),
                    estadia.getHoraFinProgramada()
            );
        }

        pago.rechazar(resultadoCobro.getReferencia(), resultadoCobro.getMotivoRechazo());
        estadia.cancelar();
        parquimetro.liberarCupo();
        repositorioPagos.guardar(pago);
        repositorioEstadias.guardar(estadia);
        return ResultadoReserva.rechazada(
                estadia.getIdEstadia(),
                estadia.getMontoEstimado(),
                estadia.getHoraFinProgramada(),
                resultadoCobro.getMotivoRechazo()
        );
    }

    private void validarConsistencia(
            SolicitudEstacionamiento solicitud,
            Usuario usuario,
            Vehiculo vehiculo,
            Parquimetro parquimetro
    ) {
        if (!solicitud.getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new IllegalArgumentException("El idUsuario de la solicitud no coincide con el usuario");
        }
        if (!solicitud.getIdVehiculo().equals(vehiculo.getIdVehiculo())) {
            throw new IllegalArgumentException("El idVehiculo de la solicitud no coincide con el vehiculo");
        }
        if (!solicitud.getIdParquimetro().equals(parquimetro.getIdParquimetro())) {
            throw new IllegalArgumentException("El idParquimetro de la solicitud no coincide con el parquimetro");
        }
    }
}
