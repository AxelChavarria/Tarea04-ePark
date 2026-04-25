# Diagramas de Secuencia - Historias de Usuario ePark

## 1. Introduccion

Este documento modela el comportamiento dinamico de las tres historias de usuario del alcance actual:

- HU 7.1 Estacionar vehiculo en calle.
- HU 7.2 Notificar proximidad de vencimiento.
- HU 7.3 Consultar pagos por tarjeta en un dia.

Cada secuencia esta alineada con los mensajes reales observables en los casos de uso y en el flujo de consola.

---

## 2. HU 7.1 - Estacionar vehiculo en calle

### 2.1 Intencion funcional

Permitir que un usuario con sesion activa reserve parqueo, procese el cobro y obtenga resultado de reserva aprobado o rechazado.

### 2.2 Precondiciones

- Usuario autenticado en consola.
- Vehiculo seleccionado y valido.
- Parquimetro existente.
- Minutos de reserva mayores a cero.

### 2.3 Diagrama de secuencia

```mermaid
sequenceDiagram
autonumber
actor U as Usuario
participant M as Main (consola)
participant UC as EstacionarVehiculoUseCase
participant R as RelojSistema
participant PM as Parquimetro
participant Z as ZonaParqueo
participant RE as RepositorioEstadias
participant SC as ServicioCobro
participant RP as RepositorioPagos

U->>M: Ingresa tipo, placa, parquimetro, minutos y tarjeta
M->>M: Construye Usuario, Vehiculo, TarjetaCredito
M->>M: Construye SolicitudEstacionamiento
M->>UC: ejecutar(solicitud, usuario, vehiculo, parquimetro)

UC->>UC: validarConsistencia(...)
UC->>R: ahora()
R-->>UC: LocalDateTime ahora

UC->>PM: crearBorradorEstadia(usuario, vehiculo, ahora, minutos)
PM->>Z: reservarCupo()
Z-->>PM: true/false

alt Sin cupo disponible
  PM-->>UC: IllegalStateException
  UC-->>M: Error de reserva
  M-->>U: Notifica falta de cupo
else Cupo reservado
  PM->>Z: calcularMontoEstimado(minutos, factorVehiculo)
  Z-->>PM: montoEstimado
  PM-->>UC: Estadia BORRADOR

  UC->>RE: guardar(estadia)
  UC->>SC: cobrar(idUsuario, idTarjeta, monto, descripcion)
  SC-->>UC: ResultadoCobro

  UC->>UC: crear Pago(...)
  alt Cobro aprobado
    UC->>UC: pago.aprobar(referencia)
    UC->>UC: estadia.confirmarPago(monto)
    UC->>UC: estadia.activar()
    UC->>RP: guardar(pago)
    UC->>RE: guardar(estadia)
    UC-->>M: ResultadoReserva.aprobada(...)
    M-->>U: Muestra confirmacion e ID de estadia
  else Cobro rechazado
    UC->>UC: pago.rechazar(referencia, motivo)
    UC->>UC: estadia.cancelar()
    UC->>PM: liberarCupo()
    UC->>RP: guardar(pago)
    UC->>RE: guardar(estadia)
    UC-->>M: ResultadoReserva.rechazada(...)
    M-->>U: Muestra rechazo y motivo
  end
end
```

### 2.4 Postcondiciones

- Existe registro de `Pago` en repositorio (aprobado o rechazado).
- Existe registro de `Estadia` persistido con estado consistente.
- Se libera cupo solo en ruta de rechazo.

### 2.5 Riesgos y controles

- Riesgo: inconsistencia entre monto estimado y monto cobrado.
- Control: el caso de uso utiliza `estadia.getMontoEstimado()` como fuente para cobro y confirmacion.
- Riesgo: reserva sin cupo.
- Control: `ZonaParqueo.reservarCupo()` bloquea el flujo antes de crear una estadia activa.

---

## 3. HU 7.2 - Notificar proximidad de vencimiento

### 3.1 Intencion funcional

Detectar estadias activas cercanas al vencimiento y emitir notificaciones al usuario con una ventana de anticipacion definida.

### 3.2 Precondiciones

- `minutosAnticipacion > 0`.
- Repositorio de estadias con estado actualizado.

### 3.3 Diagrama de secuencia

```mermaid
sequenceDiagram
autonumber
actor S as Scheduler/Disparador
participant UCN as NotificarProximoVencimientoUseCase
participant R as RelojSistema
participant RE as RepositorioEstadias
participant SN as ServicioNotificacion

S->>UCN: ejecutar(minutosAnticipacion)

alt minutosAnticipacion <= 0
  UCN-->>S: IllegalArgumentException
else parametro valido
  UCN->>R: ahora()
  R-->>UCN: LocalDateTime ahora

  UCN->>RE: buscarProximasAVencer(ahora, minutosAnticipacion)
  RE-->>UCN: List<Estadia> proximas

  loop Por cada estadia en proximas
    UCN->>UCN: Crear Notificacion(APP_PUSH, mensaje, ahora)
    UCN->>SN: enviar(notificacion)
    SN-->>UCN: Confirmacion envio
  end

  UCN-->>S: totalEnviadas
end
```

### 3.4 Postcondiciones

- Se retorna el total de notificaciones enviadas.
- Cada notificacion tiene trazabilidad por `idNotificacion` y `idUsuario`.

### 3.5 Riesgos y controles

- Riesgo: notificaciones duplicadas en ejecuciones frecuentes.
- Control actual: no existe deduplicacion persistente; depende de la frecuencia del disparador.
- Riesgo: reloj no controlado en pruebas.
- Control: se usa puerto `RelojSistema`, permitiendo inyectar reloj fijo en testing.

---

## 4. HU 7.3 - Consultar pagos por tarjeta en un dia

### 4.1 Intencion funcional

Listar pagos de un usuario para una tarjeta especifica en una fecha dada, devolviendo un resumen orientado a consulta.

### 4.2 Precondiciones

- Consulta con `idUsuario`, `idTarjeta` y `fecha` validos.
- Repositorio de pagos disponible.

### 4.3 Diagrama de secuencia

```mermaid
sequenceDiagram
autonumber
actor U as Usuario
participant M as Main (consola)
participant UCP as ConsultarPagosPorTarjetaUseCase
participant RP as RepositorioPagos

U->>M: Solicita ver pagos del dia por tarjeta
M->>M: Construye ConsultaPagosTarjeta
M->>UCP: ejecutar(consulta)

UCP->>UCP: Validar consulta != null
UCP->>RP: buscarPorTarjetaYFecha(idTarjeta, fecha, idUsuario)
RP-->>UCP: List<Pago>

loop Por cada Pago
  UCP->>UCP: toResumen(pago)
end

UCP-->>M: List<ResumenPago>
M->>M: Selecciona pago mas reciente
M-->>U: Muestra lista y resumen
```

### 4.4 Postcondiciones

- El resultado retorna una coleccion de `ResumenPago` desacoplada de la entidad `Pago`.
- La capa `app.Main` puede ordenar y seleccionar el pago mas reciente sin alterar dominio.

### 4.5 Riesgos y controles

- Riesgo: consulta cruzada de usuarios sobre misma tarjeta.
- Control: filtro incluye `idUsuario` ademas de `idTarjeta` y fecha.
- Riesgo: sobreexposicion de datos internos de `Pago`.
- Control: transformacion a DTO `ResumenPago`.

---

## 5. Observaciones de diseno transversal

- Los tres flujos aplican orquestacion en casos de uso y conservan la logica de estado en dominio.
- El punto mas sensible de transaccion esta en HU 7.1 (reserva + cobro + persistencia), por lo que es candidato a pruebas de integracion con escenarios de falla parcial.
- HU 7.2 y HU 7.3 son buenas candidatas para pruebas unitarias puras con mocks de puertos.
