# Tarea 4 - Modelado de Objetos ePark

Este repositorio contiene una base inicial para trabajar la Tarea 4 del curso, enfocada en modelado orientado a objetos y mensajeria entre componentes del ecosistema urbano.

La base esta hecha para iniciar rapido con codigo exclusivamente en Java en la capa de implementacion.

## Objetivo del repositorio

1. Dejar una estructura limpia para modelar responsabilidades por clase.
2. Cubrir como plantilla las HU del punto 7:
3. HU 7.1 Estacionar un vehiculo en la calle.
4. HU 7.2 Notificar al usuario cuando faltan 5 minutos para vencer.
5. HU 7.3 Consultar pagos por tarjeta en un dia para un cliente especifico.

## Estructura creada

- src/main/java/com/epark/domain/enums
- src/main/java/com/epark/domain/model
- src/main/java/com/epark/domain/ports
- src/main/java/com/epark/application/dto
- src/main/java/com/epark/application/usecase
- src/main/java/com/epark/infrastructure/stub
- src/main/java/com/epark/app

## Como ejecutar la plantilla

### Opcion IDE

1. Abrir el proyecto en VS Code o IntelliJ.
2. Ejecutar la clase Main en com.epark.app.

### Opcion terminal en Windows PowerShell

```powershell
New-Item -ItemType Directory -Force out
$fuentes = Get-ChildItem -Recurse -Path src/main/java -Filter *.java | ForEach-Object { $_.FullName }
javac -d out $fuentes
java -cp out com.epark.app.Main
```

## Reparto recomendado para 3 personas

### Persona 1 - Dominio y HU 7.1

Responsabilidad principal:
Modelar y fortalecer el flujo de estacionamiento.

Entregables:
1. Validaciones de negocio para Parquimetro, ZonaParqueo y Estadia.
2. Ajustes de tarifa por tipo de vehiculo.
3. Casos de prueba de dominio para estacionamiento.

### Persona 2 - Notificaciones y HU 7.2

Responsabilidad principal:
Implementar y estabilizar las alertas de vencimiento.

Entregables:
1. Estrategia para evitar notificaciones duplicadas.
2. Integracion de canales APP, SMS o EMAIL por configuracion.
3. Casos de prueba del use case NotificarProximoVencimientoUseCase.

### Persona 3 - Pagos, reportes y HU 7.3

Responsabilidad principal:
Completar consulta de pagos y preparar salida para auditoria.

Entregables:
1. Mejora de filtros por fecha, cliente y estado de pago.
2. Formato de reporte reutilizable para exportacion.
3. Casos de prueba del use case ConsultarPagosPorTarjetaUseCase.

## Flujo de trabajo sugerido

1. Cada persona trabaja en su propia rama.
2. Merge semanal hacia una rama de integracion.
3. Demo conjunta al final de cada iteracion con las 3 HU.

## Documentos del proyecto

- README.md: resumen general y arranque rapido.
- Documentacion.md: detalle tecnico y decisiones de modelado.
- ManualDeUsuario.md: guia practica para usar y probar la plantilla.
