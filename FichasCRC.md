# Fichas CRC - Objetos Principales de ePark

## 1. Proposito

Las fichas CRC (Clase-Responsabilidad-Colaborador) permiten analizar el diseno orientado a objetos desde una perspectiva de comportamiento colaborativo.

En este documento se describen las clases nucleares del sistema, sus deberes principales y con quienes colaboran para cumplirlos.

## 2. Criterio de seleccion de clases

Se priorizan:

- Entidades de negocio de alto impacto.
- Objetos de control (casos de uso).
- Puntos de extension mediante puertos.
- Objetos con transiciones de estado y reglas criticas.

---

## 3. Fichas CRC

### Ficha CRC 01 - Usuario

- Clase: `Usuario`
- Tipo: Entidad de dominio
- Responsabilidades:
  - Mantener identidad del usuario (`idUsuario`, `nombreCompleto`, `correo`).
  - Gestionar tarjetas asociadas al usuario.
  - Proveer busqueda de tarjeta por identificador.
- Colaboradores:
  - `TarjetaCredito` (agregacion).
  - `Vehiculo` (relacion como propietario).
  - `Main` (creacion y carga de contexto de sesion).
- Mensajes clave:
  - `registrarTarjeta(tarjeta)`
  - `buscarTarjeta(idTarjeta)`

### Ficha CRC 02 - Vehiculo (abstracta)

- Clase: `Vehiculo`
- Tipo: Entidad abstracta de dominio
- Responsabilidades:
  - Definir contrato comun de identidad (`idVehiculo`, `placa`).
  - Mantener referencia al propietario.
  - Exponer polimorfismo de factor tarifario.
- Colaboradores:
  - `Usuario` (propietario).
  - `Parquimetro` y `Estadia` (consumo operativo).
  - `TipoVehiculo` (clasificacion).
- Mensaje clave:
  - `obtenerFactorTarifa()`

### Ficha CRC 03 - Carro / Motocicleta / ScooterElectrico

- Clase: `Carro`, `Motocicleta`, `ScooterElectrico`
- Tipo: Especializaciones concretas de `Vehiculo`
- Responsabilidades:
  - Implementar factor tarifario por tipo.
  - Encapsular variacion de calculo sin condicionales externos.
- Colaboradores:
  - `Vehiculo` (herencia).
  - `ZonaParqueo` (usa factor para monto estimado).
- Mensaje clave:
  - `obtenerFactorTarifa()` con valores 1.00, 0.75 y 0.60.

### Ficha CRC 04 - TarjetaCredito

- Clase: `TarjetaCredito`
- Tipo: Value Object/Entidad liviana de pago
- Responsabilidades:
  - Mantener datos tokenizados de pago (`idTarjeta`, `ultimosCuatro`, `tokenPasarela`).
  - Exponer informacion necesaria para solicitud de cobro sin almacenar numero completo.
- Colaboradores:
  - `Usuario` (registro y consulta).
  - `EstacionarVehiculoUseCase` (consume `idTarjeta` indirectamente por DTO).
  - `ServicioCobro` (destino logico del identificador).

### Ficha CRC 05 - ZonaParqueo

- Clase: `ZonaParqueo`
- Tipo: Entidad de dominio con reglas de capacidad y tarifa
- Responsabilidades:
  - Administrar cupos (`reservarCupo`, `liberarCupo`).
  - Calcular monto estimado en funcion de minutos y factor de vehiculo.
  - Garantizar invariantes de tarifa positiva y cupos validos.
- Colaboradores:
  - `Parquimetro` (delegacion de control operacional).
  - `Vehiculo` (factor tarifario).
  - `Estadia` (contexto de zona aplicada).
- Mensajes clave:
  - `reservarCupo()`
  - `liberarCupo()`
  - `calcularMontoEstimado(minutos, factorVehiculo)`

### Ficha CRC 06 - Parquimetro

- Clase: `Parquimetro`
- Tipo: Entidad/control de dominio local
- Responsabilidades:
  - Crear borradores de estadia con validaciones de entrada.
  - Coordinar reserva/liberacion de cupos de la zona.
  - Encapsular la fabricacion de `Estadia` con datos consistentes.
- Colaboradores:
  - `ZonaParqueo` (cupo y monto).
  - `Estadia` (instanciacion).
  - `Usuario` y `Vehiculo` (datos de asociacion de la reserva).
- Mensajes clave:
  - `crearBorradorEstadia(usuario, vehiculo, horaInicio, minutos)`
  - `liberarCupo()`

### Ficha CRC 07 - Estadia

- Clase: `Estadia`
- Tipo: Entidad de dominio con ciclo de vida
- Responsabilidades:
  - Mantener el estado de una reserva (`BORRADOR`, `ACTIVA`, `VENCIDA`, `CANCELADA`).
  - Gestionar transiciones validas de estado.
  - Registrar monto pagado y ventana de vencimiento.
  - Determinar si esta proxima a vencer para notificacion.
- Colaboradores:
  - `Usuario`, `Vehiculo`, `ZonaParqueo` (composicion contextual).
  - `EstadoEstadia` (maquina de estados).
  - `NotificarProximoVencimientoUseCase` (consulta temporal).
- Mensajes clave:
  - `activar()`
  - `confirmarPago(montoPagado)`
  - `cancelar()`
  - `vencer()`
  - `estaProximaAVencer(ahora, minutosAnticipacion)`

### Ficha CRC 08 - Pago

- Clase: `Pago`
- Tipo: Entidad transaccional
- Responsabilidades:
  - Persistir trazabilidad de cobro por estadia/tarjeta/usuario.
  - Gestionar estado de pago (`PENDIENTE`, `APROBADO`, `RECHAZADO`).
  - Almacenar referencia externa y motivo de rechazo.
- Colaboradores:
  - `EstacionarVehiculoUseCase` (creacion y cambio de estado).
  - `RepositorioPagos` (persistencia).
  - `EstadoPago` (semantica de estado).
- Mensajes clave:
  - `aprobar(referenciaExterna)`
  - `rechazar(referenciaExterna, motivoRechazo)`

### Ficha CRC 09 - EstacionarVehiculoUseCase

- Clase: `EstacionarVehiculoUseCase`
- Tipo: Objeto de control de aplicacion
- Responsabilidades:
  - Orquestar reserva, cobro y persistencia de resultado.
  - Validar consistencia entre DTO y entidades recibidas.
  - Resolver caminos de exito y rechazo con acciones compensatorias.
  - Devolver `ResultadoReserva` para salida por consola en `Main`.
- Colaboradores:
  - `SolicitudEstacionamiento` y `ResultadoReserva`.
  - `RepositorioEstadias`, `RepositorioPagos`.
  - `ServicioCobro`, `RelojSistema`.
  - `Parquimetro`, `Estadia`, `Pago`, `ResultadoCobro`.
- Mensajes clave:
  - `ejecutar(solicitud, usuario, vehiculo, parquimetro)`
  - `validarConsistencia(...)`

### Ficha CRC 10 - ConsultarPagosPorTarjetaUseCase

- Clase: `ConsultarPagosPorTarjetaUseCase`
- Tipo: Objeto de control de aplicacion
- Responsabilidades:
  - Resolver consulta de pagos por tarjeta, fecha y usuario.
  - Transformar entidad `Pago` a DTO de lectura `ResumenPago`.
  - Entregar coleccion lista para consumo en consola.
- Colaboradores:
  - `RepositorioPagos`.
  - `ConsultaPagosTarjeta` (entrada).
  - `Pago` y `ResumenPago`.
- Mensajes clave:
  - `ejecutar(consulta)`
  - `toResumen(pago)`

### Ficha CRC 11 - NotificarProximoVencimientoUseCase

- Clase: `NotificarProximoVencimientoUseCase`
- Tipo: Objeto de control de aplicacion
- Responsabilidades:
  - Buscar estadias proximas a vencer.
  - Construir y emitir notificaciones por canal definido.
  - Retornar total de envios efectuados.
- Colaboradores:
  - `RepositorioEstadias`.
  - `ServicioNotificacion`.
  - `RelojSistema`.
  - `Notificacion` y `Estadia`.
- Mensaje clave:
  - `ejecutar(minutosAnticipacion)`

### Ficha CRC 12 - RepositorioEstadias (puerto)

- Clase: `RepositorioEstadias`
- Tipo: Puerto de dominio (interfaz)
- Responsabilidades:
  - Definir contrato de persistencia de estadias.
  - Exponer consultas por id, activas y proximas a vencer.
- Colaboradores:
  - `EstacionarVehiculoUseCase`.
  - `NotificarProximoVencimientoUseCase`.
  - `RepositorioEstadiasEnMemoria` (implementacion actual).
- Mensajes clave:
  - `guardar(estadia)`
  - `buscarPorId(idEstadia)`
  - `buscarActivas()`
  - `buscarProximasAVencer(ahora, minutosAnticipacion)`

### Ficha CRC 13 - RepositorioPagos (puerto)

- Clase: `RepositorioPagos`
- Tipo: Puerto de dominio (interfaz)
- Responsabilidades:
  - Definir almacenamiento y consulta de pagos.
  - Filtrar pagos por tarjeta, fecha y usuario.
- Colaboradores:
  - `EstacionarVehiculoUseCase`.
  - `ConsultarPagosPorTarjetaUseCase`.
  - `RepositorioPagosEnMemoria` (implementacion actual).
- Mensajes clave:
  - `guardar(pago)`
  - `buscarPorTarjetaYFecha(idTarjeta, fecha, idUsuario)`

### Ficha CRC 14 - ServicioCobro (puerto)

- Clase: `ServicioCobro`
- Tipo: Puerto de integracion
- Responsabilidades:
  - Definir contrato de cobro externo.
  - Retornar resultado de aprobacion/rechazo con trazabilidad.
- Colaboradores:
  - `EstacionarVehiculoUseCase`.
  - `ServicioCobroSimulado`.
  - `ResultadoCobro`.
- Mensaje clave:
  - `cobrar(idUsuario, idTarjeta, monto, descripcion)`

### Ficha CRC 15 - ServicioNotificacion (puerto)

- Clase: `ServicioNotificacion`
- Tipo: Puerto de integracion
- Responsabilidades:
  - Definir envio de notificaciones sin acoplar canal/tecnologia.
- Colaboradores:
  - `NotificarProximoVencimientoUseCase`.
  - `ServicioNotificacionConsola`.
  - `Notificacion`.
- Mensaje clave:
  - `enviar(notificacion)`

---

## 4. Matriz de colaboracion resumida

| Clase controlada | Colaboradores primarios | Resultado de colaboracion |
|---|---|---|
| `EstacionarVehiculoUseCase` | `Parquimetro`, `RepositorioEstadias`, `ServicioCobro`, `RepositorioPagos` | Reserva confirmada o rechazada con trazabilidad completa |
| `NotificarProximoVencimientoUseCase` | `RelojSistema`, `RepositorioEstadias`, `ServicioNotificacion` | Notificaciones emitidas para estadias proximas |
| `ConsultarPagosPorTarjetaUseCase` | `RepositorioPagos` | Lista de `ResumenPago` filtrada por usuario/tarjeta/fecha |

## 5. Conclusiones tecnicas

- Las responsabilidades de negocio mas criticas estan encapsuladas en `Estadia`, `ZonaParqueo` y `EstacionarVehiculoUseCase`.
- Los puertos disminuyen acoplamiento y facilitan pruebas por sustitucion de infraestructura.
- Las especializaciones de `Vehiculo` muestran buen uso de polimorfismo para tarifa sin condicionales duplicados.
- Como mejora academica, puede separarse la autenticacion en su propio caso de uso para reducir carga de `Main`.
