# Tarea 4 - Modelado de Objetos ePark

Este repositorio implementa una plantilla de ePark en Java orientada a modelado de objetos y mensajeria entre capas.

La ejecucion principal es interactiva por terminal y comienza preguntando si deseas registrar un usuario o iniciar sesion.

## Objetivo del repositorio

- Mantener una estructura limpia por capas.
- Cubrir los flujos base del punto 7 del curso.
- Ejecutar login, reserva, cobro y factura en consola.

## Flujo interactivo

- Menu inicial:
- Registrar usuario.
- Iniciar sesion.

- Registro:
- solicita nombre, cedula, correo y contrasena.
- valida cedula unica.
- valida correo unico.
- guarda usuario y abre sesion automaticamente.

- Inicio de sesion:
- solicita correo, nombre y contrasena.
- valida que existan y coincidan en usuarios.txt.

- Reserva:
- solicita tipo de vehiculo y placa.
- valida no repeticion de placas.
- solicita parquimetro, tiempo y tarjeta.
- calcula cobro y genera factura.

## Archivo unico de usuarios

Ruta:

- out/usuarios/usuarios.txt

Formato por usuario:

```text
ID_USUARIO=USR-604700374
NOMBRE=Jeffry
CEDULA=6-0470-0374
CORREO=jefaraya@estudiantec.cr
CONTRASENA=123456
FECHA_REGISTRO=2026-04-21 20:17:06
Vehiculos: [Carro: {ABC123; DEF456}; Motocicletas: {No hay}; Scooters: {SCO123}]
```

Reglas de estructura:

- Cada usuario es un bloque.
- Los usuarios se separan por una linea en blanco.

## Seccion Vehiculos

Formato:

```text
Vehiculos: [Carro: {<placa>; <placa>; ...}; Motocicletas: {No hay}; Scooters: {<placa>; <placa>; ...}]
```

Reglas:

- Si no hay placas en una categoria, usar No hay.
- Las placas no se deben repetir.

## Validaciones importantes

- Cedula unica en registro.
- Correo unico en registro.
- Correo, nombre y contrasena deben coincidir en login.
- Placa no repetida entre usuarios.
- Parquimetro de 4 digitos.
- Tarjeta con 13 a 19 digitos.

## Formula de cobro

```text
monto = (tarifaHoraBase / 60) x minutos x factorVehiculo
```

Tarifa base demo:

- CRC 1200.00 por hora.

Factores demo:

- Carro: 1.00.
- Motocicleta: 0.75.
- Scooter Electrico: 0.60.

## Ejecucion por sistema operativo

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

## Documentos

- README.md
- Documentacion.md
- ManualDeUsuario.md
