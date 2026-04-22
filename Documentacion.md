# Documentacion Tecnica - ePark

## 1. Contexto

ePark modela interacciones entre Usuario, Vehiculo, Parquimetro y Sistema de Cobro para gestionar estacionamiento urbano.

La version actual agrega registro e inicio de sesion por terminal, usando un unico archivo de usuarios y una seccion de vehiculos por usuario.

## 2. Alcance funcional

- HU 7.1 Estacionar un vehiculo en la calle.
- HU 7.2 Base para notificar proximidad de vencimiento.
- HU 7.3 Consultar pagos por tarjeta para un cliente en un dia.
- Registro de usuario con validacion de cedula y correo unicos.
- Login con validacion de correo, nombre y contrasena.

## 3. Arquitectura por capas

- domain: entidades, enums y puertos.
- application: DTOs y casos de uso.
- infrastructure: stubs en memoria.
- app: flujo interactivo, validaciones y persistencia en TXT.

## 4. Flujo tecnico en Main

- Carga usuarios desde out/usuarios/usuarios.txt.
- Muestra menu de acceso.
- Opcion Registrar usuario.
- Opcion Iniciar sesion.
- Continua con reserva, cobro y factura.

Detalle de menu:

- Registrar usuario:
- valida cedula unica.
- valida correo unico.
- crea bloque de usuario con fecha de registro.
- crea seccion Vehiculos inicial.

- Iniciar sesion:
- valida correo existente.
- valida nombre y contrasena contra ese correo.

- Reserva:
- solicita tipo de vehiculo y placa.
- valida placa no repetida.
- registra placa en la categoria correcta.
- solicita parquimetro, minutos y tarjeta.
- procesa pago y genera factura.

## 5. Formato de usuarios.txt

Ruta:

- out/usuarios/usuarios.txt

Campos por usuario:

- ID_USUARIO
- NOMBRE
- CEDULA
- CORREO
- CONTRASENA
- FECHA_REGISTRO
- Vehiculos

Linea de vehiculos:

```text
Vehiculos: [Carro: {<placa>; <placa>; ...}; Motocicletas: {No hay}; Scooters: {<placa>; <placa>; ...}]
```

Reglas:

- Cada usuario es un bloque.
- Cada bloque se separa por una linea en blanco.
- Si no hay placas en una categoria, usar No hay.

## 6. Validaciones implementadas

- Registro:
- cedula con regex ^[1-9]-\\d{4}-\\d{4}$.
- cedula no repetida.
- correo con formato valido.
- correo no repetido.

- Login:
- correo debe existir.
- nombre debe coincidir con ese correo.
- contrasena debe coincidir con ese correo.

- Vehiculos:
- placa no repetida entre usuarios.
- si la placa ya existe para otro usuario, se rechaza.

- Reserva y pago:
- parquimetro con regex ^\\d{4}$.
- minutos mayor que cero.
- tarjeta con regex ^\\d{13,19}$.

## 7. Cobro

Formula:

```text
monto = (tarifaHoraBase / 60) x minutos x factorVehiculo
```

Configuracion demo:

- tarifaHoraBase: CRC 1200.00.
- factor Carro: 1.00.
- factor Motocicleta: 0.75.
- factor ScooterElectrico: 0.60.

## 8. Simulador de cobro

ServicioCobroSimulado:

- rechaza monto <= 0.
- rechaza tarjeta cuyo id termina en 0000.
- aprueba en los demas casos con referencia CBR-XXXXXXXX.

## 9. Archivos relevantes

- Usuarios: out/usuarios/usuarios.txt.
- Facturas: out/facturas/factura_{yyyyMMddHHmmss}_{idEstadia}.txt.

## 10. Ejecucion por sistema operativo

### Windows 11 (PowerShell)

```powershell
cd C:\TareasReque\Tarea04\Tarea04-ePark
New-Item -ItemType Directory -Force out | Out-Null
$fuentes = Get-ChildItem -Recurse -Path src/main/java -Filter *.java | ForEach-Object { $_.FullName }
javac -d out $fuentes
java -cp out com.epark.app.Main
```

### WSL (Ubuntu o Debian sobre Windows)

```bash
cd /mnt/c/TareasReque/Tarea04/Tarea04-ePark
mkdir -p out
find src/main/java -name "*.java" > sources.txt
javac -d out @sources.txt
java -cp out com.epark.app.Main
```

### Ubuntu (nativo)

```bash
cd /ruta/a/Tarea04-ePark
mkdir -p out
find src/main/java -name "*.java" > sources.txt
javac -d out @sources.txt
java -cp out com.epark.app.Main
```

### Debian (nativo)

```bash
cd /ruta/a/Tarea04-ePark
mkdir -p out
find src/main/java -name "*.java" > sources.txt
javac -d out @sources.txt
java -cp out com.epark.app.Main
```

### macOS

```bash
cd /ruta/a/Tarea04-ePark
mkdir -p out
find src/main/java -name "*.java" > sources.txt
javac -d out @sources.txt
java -cp out com.epark.app.Main
```
