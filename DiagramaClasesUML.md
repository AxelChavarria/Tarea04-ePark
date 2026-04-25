# Diagrama de Clases UML - ePark

## 1. Objetivo

Este documento presenta una vista UML del sistema ePark, enfocada en:

- estructura por capas,
- dependencias de inversion (puertos),
- entidades de dominio y sus relaciones,
- variaciones por tipo de vehiculo,
- contratos de aplicacion para los 3 casos de uso.

## 2. Alcance modelado

El diagrama incluye los componentes con mayor peso arquitectonico:

- Capa app: `Main`.
- Capa application: DTO y casos de uso.
- Capa domain: entidades, enums y puertos.
- Capa infrastructure: implementaciones stub en memoria/consola.

## 3. Diagrama UML (Mermaid)

```mermaid
classDiagram
direction LR

class Main {
  +main(args: String[]): void
}

class EstacionarVehiculoUseCase {
  -repositorioEstadias: RepositorioEstadias
  -repositorioPagos: RepositorioPagos
  -servicioCobro: ServicioCobro
  -relojSistema: RelojSistema
  +ejecutar(solicitud, usuario, vehiculo, parquimetro): ResultadoReserva
}

class ConsultarPagosPorTarjetaUseCase {
  -repositorioPagos: RepositorioPagos
  +ejecutar(consulta): List~ResumenPago~
}

class NotificarProximoVencimientoUseCase {
  -repositorioEstadias: RepositorioEstadias
  -servicioNotificacion: ServicioNotificacion
  -relojSistema: RelojSistema
  +ejecutar(minutosAnticipacion: long): int
}

class SolicitudEstacionamiento {
  +idUsuario: String
  +idVehiculo: String
  +idParquimetro: String
  +minutosSolicitados: int
  +idTarjeta: String
}

class ResultadoReserva {
  +idEstadia: String
  +montoReserva: BigDecimal
  +horaVencimiento: LocalDateTime
  +pagoAprobado: boolean
  +mensaje: String
  +aprobada(...): ResultadoReserva
  +rechazada(...): ResultadoReserva
}

class ConsultaPagosTarjeta {
  +idUsuario: String
  +idTarjeta: String
  +fecha: LocalDate
}

class ResumenPago {
  +idPago: String
  +monto: BigDecimal
  +fechaHora: LocalDateTime
  +estado: EstadoPago
  +referencia: String
}

class Usuario {
  -idUsuario: String
  -nombreCompleto: String
  -correo: String
  -tarjetas: List~TarjetaCredito~
  +registrarTarjeta(tarjeta): void
  +buscarTarjeta(idTarjeta): Optional~TarjetaCredito~
}

class TarjetaCredito {
  -idTarjeta: String
  -titular: String
  -ultimosCuatro: String
  -tokenPasarela: String
}

class Vehiculo {
  <<abstract>>
  -idVehiculo: String
  -placa: String
  -propietario: Usuario
  -tipoVehiculo: TipoVehiculo
  +obtenerFactorTarifa(): BigDecimal
}

class Carro {
  +obtenerFactorTarifa(): BigDecimal
}

class Motocicleta {
  +obtenerFactorTarifa(): BigDecimal
}

class ScooterElectrico {
  +obtenerFactorTarifa(): BigDecimal
}

class Parquimetro {
  -idParquimetro: String
  -zonaParqueo: ZonaParqueo
  +crearBorradorEstadia(usuario, vehiculo, horaInicio, minutos): Estadia
  +liberarCupo(): void
}

class ZonaParqueo {
  -idZona: String
  -nombreZona: String
  -tarifaHoraBase: BigDecimal
  -cuposTotales: int
  -cuposDisponibles: int
  +reservarCupo(): boolean
  +liberarCupo(): void
  +calcularMontoEstimado(minutos, factorVehiculo): BigDecimal
}

class Estadia {
  -idEstadia: String
  -usuario: Usuario
  -vehiculo: Vehiculo
  -zonaParqueo: ZonaParqueo
  -horaInicio: LocalDateTime
  -horaFinProgramada: LocalDateTime
  -montoEstimado: BigDecimal
  -montoPagado: BigDecimal
  -estado: EstadoEstadia
  +activar(): void
  +confirmarPago(montoPagado): void
  +cancelar(): void
  +vencer(): void
  +estaProximaAVencer(ahora, minutosAnticipacion): boolean
}

class Pago {
  -idPago: String
  -idEstadia: String
  -idUsuario: String
  -idTarjeta: String
  -monto: BigDecimal
  -fechaHora: LocalDateTime
  -estado: EstadoPago
  -referenciaExterna: String
  -motivoRechazo: String
  +aprobar(referenciaExterna): void
  +rechazar(referenciaExterna, motivoRechazo): void
}

class Notificacion {
  -idNotificacion: String
  -idUsuario: String
  -canal: CanalNotificacion
  -mensaje: String
  -fechaHora: LocalDateTime
  -enviada: boolean
  +marcarEnviada(): void
}

class ResultadoCobro {
  -aprobado: boolean
  -referencia: String
  -motivoRechazo: String
  +aprobado(referencia): ResultadoCobro
  +rechazado(referencia, motivo): ResultadoCobro
}

class RelojSistema {
  <<interface>>
  +ahora(): LocalDateTime
}

class RepositorioEstadias {
  <<interface>>
  +guardar(estadia): void
  +buscarPorId(idEstadia): Optional~Estadia~
  +buscarActivas(): List~Estadia~
  +buscarProximasAVencer(ahora, minutos): List~Estadia~
}

class RepositorioPagos {
  <<interface>>
  +guardar(pago): void
  +buscarPorTarjetaYFecha(idTarjeta, fecha, idUsuario): List~Pago~
}

class ServicioCobro {
  <<interface>>
  +cobrar(idUsuario, idTarjeta, monto, descripcion): ResultadoCobro
}

class ServicioNotificacion {
  <<interface>>
  +enviar(notificacion): void
}

class RelojSistemaLocal
class RepositorioEstadiasEnMemoria
class RepositorioPagosEnMemoria
class ServicioCobroSimulado
class ServicioNotificacionConsola

class EstadoEstadia {
  <<enumeration>>
  BORRADOR
  ACTIVA
  VENCIDA
  CANCELADA
}

class EstadoPago {
  <<enumeration>>
  PENDIENTE
  APROBADO
  RECHAZADO
}

class TipoVehiculo {
  <<enumeration>>
  CARRO
  MOTOCICLETA
  SCOOTER_ELECTRICO
}

class CanalNotificacion {
  <<enumeration>>
  APP_PUSH
  SMS
  EMAIL
}

Main ..> EstacionarVehiculoUseCase : invoca
Main ..> ConsultarPagosPorTarjetaUseCase : invoca
Main ..> Usuario : instancia
Main ..> Vehiculo : instancia concreta
Main ..> Parquimetro : instancia
Main ..> TarjetaCredito : instancia
Main ..> SolicitudEstacionamiento : crea
Main ..> ConsultaPagosTarjeta : crea

EstacionarVehiculoUseCase --> RepositorioEstadias : usa
EstacionarVehiculoUseCase --> RepositorioPagos : usa
EstacionarVehiculoUseCase --> ServicioCobro : usa
EstacionarVehiculoUseCase --> RelojSistema : usa
EstacionarVehiculoUseCase ..> SolicitudEstacionamiento : entrada
EstacionarVehiculoUseCase ..> ResultadoReserva : salida

ConsultarPagosPorTarjetaUseCase --> RepositorioPagos : usa
ConsultarPagosPorTarjetaUseCase ..> ConsultaPagosTarjeta : entrada
ConsultarPagosPorTarjetaUseCase ..> ResumenPago : salida

NotificarProximoVencimientoUseCase --> RepositorioEstadias : usa
NotificarProximoVencimientoUseCase --> ServicioNotificacion : usa
NotificarProximoVencimientoUseCase --> RelojSistema : usa
NotificarProximoVencimientoUseCase ..> Notificacion : crea

Vehiculo <|-- Carro
Vehiculo <|-- Motocicleta
Vehiculo <|-- ScooterElectrico

Usuario "1" o-- "0..*" TarjetaCredito : registra
Vehiculo "0..*" --> "1" Usuario : propietario
Parquimetro "1" --> "1" ZonaParqueo : administra
Parquimetro ..> Estadia : crea borrador
Estadia "1" --> "1" Usuario
Estadia "1" --> "1" Vehiculo
Estadia "1" --> "1" ZonaParqueo

Estadia --> EstadoEstadia
Pago --> EstadoPago
Vehiculo --> TipoVehiculo
Notificacion --> CanalNotificacion

ServicioCobro ..> ResultadoCobro

RelojSistemaLocal ..|> RelojSistema
RepositorioEstadiasEnMemoria ..|> RepositorioEstadias
RepositorioPagosEnMemoria ..|> RepositorioPagos
ServicioCobroSimulado ..|> ServicioCobro
ServicioNotificacionConsola ..|> ServicioNotificacion
```

## 4. Lectura academica del modelo

### 4.1 Separacion de responsabilidades

- `application` coordina casos de uso y traduce entradas/salidas con DTO.
- `domain` concentra reglas de negocio y estados.
- `infrastructure` materializa puertos mediante stubs reemplazables.
- `app.Main` actua como adaptador de entrada por consola y persistencia TXT.

### 4.2 Inversion de dependencias

Los casos de uso dependen de interfaces (`RepositorioEstadias`, `RepositorioPagos`, `ServicioCobro`, `ServicioNotificacion`, `RelojSistema`) y no de implementaciones concretas.

Esto habilita:

- pruebas unitarias con dobles,
- sustitucion de infraestructura sin afectar dominio,
- evolucion hacia DB o pasarela real sin reescribir casos de uso.

### 4.3 Reglas de negocio visibles en el diagrama

- `Estadia` controla transiciones de estado (`BORRADOR -> ACTIVA`, cancelacion, vencimiento).
- `Parquimetro` delega calculo tarifario a `ZonaParqueo` y controla cupos.
- `Vehiculo` aplica polimorfismo para factor tarifario por tipo.
- `Pago` encapsula resultado transaccional (aprobado/rechazado) y trazabilidad externa.

### 4.4 Puntos de extension recomendados

- Agregar repositorios persistentes (SQL/NoSQL) implementando puertos actuales.
- Incorporar estrategias de precio dinamico en `ZonaParqueo`.
- Separar autenticacion/registro de `Main` en un caso de uso dedicado.
- Integrar `NotificarProximoVencimientoUseCase` a un scheduler real.
