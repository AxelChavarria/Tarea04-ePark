# Documentacion Tecnica - ePark

## 1. Contexto

ePark modela interacciones entre Usuario, Vehiculo, Parquimetro y Sistema de Cobro para gestionar estacionamiento urbano.

El objetivo de esta base es priorizar responsabilidades por objeto y mensajeria entre clases, evitando logica centralizada en una clase gigante.

## 2. Alcance funcional inicial

1. HU 7.1 Estacionar un vehiculo en la calle.
2. HU 7.2 Enviar aviso cuando faltan 5 minutos para vencer.
3. HU 7.3 Consultar pagos por tarjeta para un cliente en un dia.

## 3. Modelo de capas

1. domain:
Contiene entidades, enums y puertos del negocio.

2. application:
Contiene DTOs y casos de uso que orquestan el flujo.

3. infrastructure:
Contiene implementaciones stub en memoria para probar la base.

4. app:
Clase Main para ejecucion de ejemplo.

## 4. Objetos principales, responsabilidades y colaboradores

1. Usuario:
Gestiona identidad del cliente y tarjetas registradas.
Colabora con TarjetaCredito, Estadia y Pago.

2. Vehiculo y subtipos:
Representa tipo de vehiculo y factor tarifario.
Colabora con Estadia y ZonaParqueo.

3. ZonaParqueo:
Administra cupos y calcula monto estimado.
Colabora con Parquimetro y Estadia.

4. Parquimetro:
Crea borradores de estadia y reserva cupo.
Colabora con ZonaParqueo y Estadia.

5. Estadia:
Controla ciclo de vida de una reserva de parqueo.
Colabora con Usuario, Vehiculo, ZonaParqueo y Pago.

6. Pago:
Registra el resultado del cobro de una estadia.
Colabora con ServicioCobro y consultas de reportes.

7. Notificacion:
Representa un aviso al usuario.
Colabora con ServicioNotificacion.

## 5. Casos de uso

### 5.1 EstacionarVehiculoUseCase (HU 7.1)

1. Valida consistencia de IDs.
2. Solicita al parquimetro crear una estadia borrador.
3. Ejecuta cobro via ServicioCobro.
4. Si aprueba: activa estadia y guarda pago aprobado.
5. Si rechaza: cancela estadia, libera cupo y guarda pago rechazado.

### 5.2 NotificarProximoVencimientoUseCase (HU 7.2)

1. Consulta estadias activas proximas a vencer.
2. Construye mensaje estandar de aviso.
3. Envia notificacion por ServicioNotificacion.

### 5.3 ConsultarPagosPorTarjetaUseCase (HU 7.3)

1. Recibe filtro de usuario, tarjeta y fecha.
2. Consulta pagos en repositorio.
3. Retorna una lista de ResumenPago.

## 6. Extension a Scooter Electrico

La base ya incluye ScooterElectrico y TipoVehiculo.SCOOTER_ELECTRICO.

Para cambiar tarifa o reglas, solo se toca la clase ScooterElectrico o politicas de tarifa, sin romper los otros tipos de vehiculo.

## 7. Distribucion del trabajo para 3 personas

### Persona 1 - Dominio

1. Reglas de cupos, validaciones y estados de Estadia.
2. Ajustes de tarifas por tipo de vehiculo.
3. Pruebas unitarias de dominio.

### Persona 2 - Mensajeria y alertas

1. Huella de envio de notificaciones para evitar duplicados.
2. Definicion de plantillas de mensaje por canal.
3. Pruebas unitarias y de integracion de notificaciones.

### Persona 3 - Pagos y reportes

1. Mejoras al repositorio de pagos y criterios de consulta.
2. Armado de reportes por usuario y fecha.
3. Pruebas unitarias de consultas.

## 8. Plan de integracion semanal

1. Lunes: refinamiento de tareas y criterios de aceptacion.
2. Miercoles: merge parcial a rama de integracion.
3. Viernes: demo conjunta de HU 7.1, 7.2 y 7.3.

## 9. Criterios de calidad sugeridos

1. Cohesion alta y clases con una sola responsabilidad.
2. Minimo acoplamiento entre capas.
3. Sin if gigante por tipo de vehiculo.
4. Casos de uso con flujo claro y trazable.
