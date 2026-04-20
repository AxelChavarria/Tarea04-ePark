package com.epark.app;

import com.epark.application.dto.ConsultaPagosTarjeta;
import com.epark.application.dto.ResultadoReserva;
import com.epark.application.dto.ResumenPago;
import com.epark.application.dto.SolicitudEstacionamiento;
import com.epark.application.usecase.ConsultarPagosPorTarjetaUseCase;
import com.epark.application.usecase.EstacionarVehiculoUseCase;
import com.epark.application.usecase.NotificarProximoVencimientoUseCase;
import com.epark.domain.model.Carro;
import com.epark.domain.model.Parquimetro;
import com.epark.domain.model.TarjetaCredito;
import com.epark.domain.model.Usuario;
import com.epark.domain.model.Vehiculo;
import com.epark.domain.model.ZonaParqueo;
import com.epark.domain.ports.RelojSistema;
import com.epark.domain.ports.RepositorioEstadias;
import com.epark.domain.ports.RepositorioPagos;
import com.epark.domain.ports.ServicioCobro;
import com.epark.domain.ports.ServicioNotificacion;
import com.epark.infrastructure.stub.RelojSistemaLocal;
import com.epark.infrastructure.stub.RepositorioEstadiasEnMemoria;
import com.epark.infrastructure.stub.RepositorioPagosEnMemoria;
import com.epark.infrastructure.stub.ServicioCobroSimulado;
import com.epark.infrastructure.stub.ServicioNotificacionConsola;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        RepositorioEstadias repositorioEstadias = new RepositorioEstadiasEnMemoria();
        RepositorioPagos repositorioPagos = new RepositorioPagosEnMemoria();
        ServicioCobro servicioCobro = new ServicioCobroSimulado();
        ServicioNotificacion servicioNotificacion = new ServicioNotificacionConsola();
        RelojSistema relojSistema = new RelojSistemaLocal();

        EstacionarVehiculoUseCase estacionarVehiculoUseCase = new EstacionarVehiculoUseCase(
                repositorioEstadias,
                repositorioPagos,
                servicioCobro,
                relojSistema
        );

        NotificarProximoVencimientoUseCase notificarProximoVencimientoUseCase = new NotificarProximoVencimientoUseCase(
                repositorioEstadias,
                servicioNotificacion,
                relojSistema
        );

        ConsultarPagosPorTarjetaUseCase consultarPagosPorTarjetaUseCase = new ConsultarPagosPorTarjetaUseCase(
                repositorioPagos
        );

        Usuario usuario = new Usuario("USR-001", "Ana Perez", "ana@epark.local");
        TarjetaCredito tarjeta = new TarjetaCredito("CARD-1234", "Ana Perez", "1234", "tok-demo");
        usuario.registrarTarjeta(tarjeta);

        Vehiculo vehiculo = new Carro("VEH-001", "ABC123", usuario);
        ZonaParqueo zona = new ZonaParqueo("Z4", "Zona 4", new BigDecimal("1200.00"), 20);
        Parquimetro parquimetro = new Parquimetro("PARQ-001", zona);

        SolicitudEstacionamiento solicitud = new SolicitudEstacionamiento(
                usuario.getIdUsuario(),
                vehiculo.getIdVehiculo(),
                parquimetro.getIdParquimetro(),
                60,
                tarjeta.getIdTarjeta()
        );

        ResultadoReserva resultadoReserva = estacionarVehiculoUseCase.ejecutar(
                solicitud,
                usuario,
                vehiculo,
                parquimetro
        );

        System.out.println("Resultado de la reserva: " + resultadoReserva.getMensaje());
        System.out.println("ID de estadia: " + resultadoReserva.getIdEstadia());
        System.out.println("Monto: " + resultadoReserva.getMontoReserva());

        int notificaciones = notificarProximoVencimientoUseCase.ejecutar(5);
        System.out.println("Notificaciones enviadas: " + notificaciones);

        ConsultaPagosTarjeta consulta = new ConsultaPagosTarjeta(
                usuario.getIdUsuario(),
                tarjeta.getIdTarjeta(),
                LocalDate.now()
        );
        List<ResumenPago> pagosDelDia = consultarPagosPorTarjetaUseCase.ejecutar(consulta);
        System.out.println("Pagos encontrados para hoy: " + pagosDelDia.size());
    }
}
