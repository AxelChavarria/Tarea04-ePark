# Manual de Usuario - ePark

## 1. Proposito

Esta guia explica como ejecutar ePark por terminal, registrar o iniciar sesion y completar una reserva con factura.

## 2. Requisitos

- Java JDK 17 o superior.
- java y javac disponibles en PATH.
- Acceso a la carpeta del proyecto.

## 3. Archivo de usuarios

Archivo unico:

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

Reglas:

- Cada usuario ocupa un bloque.
- Cada bloque se separa por una linea en blanco.
- Si una categoria no tiene placas, usar No hay.

## 4. Como ejecutar segun sistema operativo

### 4.1 Windows 11 (PowerShell)

```powershell
cd C:\TareasReque\Tarea04\Tarea04-ePark
New-Item -ItemType Directory -Force out | Out-Null
$fuentes = Get-ChildItem -Recurse -Path src/main/java -Filter *.java | ForEach-Object { $_.FullName }
javac -d out $fuentes
java -cp out com.epark.app.Main
```

### 4.2 WSL (Ubuntu/Debian sobre Windows)

```bash
cd /mnt/c/TareasReque/Tarea04/Tarea04-ePark
mkdir -p out
find src/main/java -name "*.java" > sources.txt
javac -d out @sources.txt
java -cp out com.epark.app.Main
```

### 4.3 Ubuntu (nativo)

```bash
cd /ruta/a/Tarea04-ePark
mkdir -p out
find src/main/java -name "*.java" > sources.txt
javac -d out @sources.txt
java -cp out com.epark.app.Main
```

### 4.4 Debian (nativo)

```bash
cd /ruta/a/Tarea04-ePark
mkdir -p out
find src/main/java -name "*.java" > sources.txt
javac -d out @sources.txt
java -cp out com.epark.app.Main
```

### 4.5 macOS

```bash
cd /ruta/a/Tarea04-ePark
mkdir -p out
find src/main/java -name "*.java" > sources.txt
javac -d out @sources.txt
java -cp out com.epark.app.Main
```

## 5. Menu inicial

Al abrir el programa:

- Opcion 1: Registrar usuario.
- Opcion 2: Iniciar sesion.

## 6. Registrar usuario

El sistema solicita:

- Nombre de usuario.
- Cedula (x-xxxx-xxxx).
- Correo.
- Contrasena.

Validaciones en registro:

- Cedula no repetida.
- Correo no repetido.

Si pasa validaciones, guarda el usuario e inicia sesion automaticamente.

## 7. Iniciar sesion

El sistema solicita:

- Correo.
- Nombre de usuario.
- Contrasena.

Validaciones de login:

- El correo existe en usuarios.txt.
- El nombre coincide con ese correo.
- La contrasena coincide con ese correo.

## 8. Vehiculos y placas

Despues de acceder:

- selecciona tipo de vehiculo (Carro, Motocicleta o Scooter Electrico),
- ingresa placa.

Reglas:

- Las placas no se repiten.
- Si la placa ya pertenece a otro usuario, se rechaza.
- La placa se agrega a la seccion Vehiculos del usuario.

## 9. Reserva, cobro y factura

Luego se solicita:

- numero de parquimetro,
- minutos de reserva,
- tarjeta de pago.

Se calcula monto y se genera factura.

Formula de cobro:

```text
monto = (tarifaHoraBase / 60) x minutos x factorVehiculo
```

Factores:

- Carro: 1.00.
- Motocicleta: 0.75.
- Scooter Electrico: 0.60.

## 10. Archivos usados y generados

- Usuarios: out/usuarios/usuarios.txt.
- Factura: out/facturas/factura_{fechaHora}_{idEstadia}.txt.

## 11. Errores comunes

- Cedula repetida: usar una cedula nueva.
- Correo repetido: usar otro correo.
- Login fallido: verificar correo, nombre y contrasena.
- Placa repetida: ingresar una placa no usada por otro usuario.
