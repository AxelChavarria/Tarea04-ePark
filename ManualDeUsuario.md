# Manual de Usuario - ePark (Plantilla)

## 1. Proposito

Este manual explica como ejecutar la plantilla base y como validar de forma simple los tres escenarios de usuario solicitados.

## 2. Requisitos

1. Java JDK 17 o superior instalado.
2. VS Code con Extension Pack for Java o IDE equivalente.

## 3. Ejecucion rapida

### 3.1 Desde IDE

1. Abrir el proyecto.
2. Ejecutar la clase Main ubicada en com.epark.app.

### 3.2 Desde terminal PowerShell

```powershell
New-Item -ItemType Directory -Force out
$fuentes = Get-ChildItem -Recurse -Path src/main/java -Filter *.java | ForEach-Object { $_.FullName }
javac -d out $fuentes
java -cp out com.epark.app.Main
```

## 4. Que valida esta demo

1. HU 7.1:
Se crea una reserva de parqueo y se intenta un cobro simulado.

2. HU 7.2:
Se consulta si hay estadias proximas a vencer para enviar aviso.

3. HU 7.3:
Se listan pagos de una tarjeta para un usuario en la fecha actual.

## 5. Como usarla en equipo de 3 personas

1. Persona 1 corre el flujo de estacionamiento y valida estados.
2. Persona 2 prueba envio de notificaciones y mensajes.
3. Persona 3 valida las consultas de pagos y reportes.

Todos deben integrar semanalmente para evitar divergencias de modelo.

## 6. Resultado esperado en consola

Se deben ver lineas similares a:

1. Mensaje de resultado de reserva.
2. ID de estadia y monto.
3. Cantidad de notificaciones enviadas.
4. Cantidad de pagos encontrados.

## 7. Limitaciones actuales de la plantilla

1. Persistencia solo en memoria.
2. Cobro simulado sin pasarela real.
3. Notificaciones por consola.

## 8. Proximos pasos recomendados

1. Agregar pruebas unitarias por caso de uso.
2. Reemplazar stubs por adaptadores reales.
3. Crear diagrama UML final con los objetos implementados.
