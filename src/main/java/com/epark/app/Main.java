package com.epark.app;

import com.epark.application.dto.ConsultaPagosTarjeta;
import com.epark.application.dto.ResultadoReserva;
import com.epark.application.dto.ResumenPago;
import com.epark.application.dto.SolicitudEstacionamiento;
import com.epark.application.usecase.ConsultarPagosPorTarjetaUseCase;
import com.epark.application.usecase.EstacionarVehiculoUseCase;
import com.epark.domain.model.Carro;
import com.epark.domain.model.Motocicleta;
import com.epark.domain.model.Parquimetro;
import com.epark.domain.model.ScooterElectrico;
import com.epark.domain.model.TarjetaCredito;
import com.epark.domain.model.Usuario;
import com.epark.domain.model.Vehiculo;
import com.epark.domain.model.ZonaParqueo;
import com.epark.domain.ports.RelojSistema;
import com.epark.domain.ports.RepositorioEstadias;
import com.epark.domain.ports.RepositorioPagos;
import com.epark.domain.ports.ServicioCobro;
import com.epark.infrastructure.stub.RelojSistemaLocal;
import com.epark.infrastructure.stub.RepositorioEstadiasEnMemoria;
import com.epark.infrastructure.stub.RepositorioPagosEnMemoria;
import com.epark.infrastructure.stub.ServicioCobroSimulado;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static final Pattern CEDULA_CR_PATTERN = Pattern.compile("^[1-9]-\\d{4}-\\d{4}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final Pattern PARQUIMETRO_PATTERN = Pattern.compile("^\\d{4}$");
    private static final Pattern TARJETA_PATTERN = Pattern.compile("^\\d{13,19}$");
    private static final DateTimeFormatter FECHA_HORA_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter FACTURA_ID_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public static void main(String[] args) {
        RepositorioEstadias repositorioEstadias = new RepositorioEstadiasEnMemoria();
        RepositorioPagos repositorioPagos = new RepositorioPagosEnMemoria();
        ServicioCobro servicioCobro = new ServicioCobroSimulado();
        RelojSistema relojSistema = new RelojSistemaLocal();

        EstacionarVehiculoUseCase estacionarVehiculoUseCase = new EstacionarVehiculoUseCase(
                repositorioEstadias,
                repositorioPagos,
                servicioCobro,
                relojSistema
        );

        ConsultarPagosPorTarjetaUseCase consultarPagosPorTarjetaUseCase = new ConsultarPagosPorTarjetaUseCase(
                repositorioPagos
        );

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("========================================");
            System.out.println("ePark - Reserva interactiva de parqueo");
            System.out.println("========================================");

            Path rutaUsuarios = obtenerRutaUsuarios();
            asegurarArchivoUsuarios(rutaUsuarios);
            List<UsuarioArchivo> usuarios = cargarUsuariosDesdeArchivo(rutaUsuarios);
            validarNoPlacasDuplicadasGlobal(usuarios);

            if (usuarios.isEmpty()) {
                throw new IllegalStateException("No hay usuarios disponibles en " + rutaUsuarios.toAbsolutePath());
            }

            UsuarioArchivo usuarioAutenticado = resolverAcceso(scanner, usuarios, rutaUsuarios);
            Usuario usuario = new Usuario(
                    usuarioAutenticado.getIdUsuario(),
                    usuarioAutenticado.getNombreCompleto(),
                    usuarioAutenticado.getCorreo()
            );

            System.out.println("Sesion activa para: " + usuarioAutenticado.getNombreCompleto());
            System.out.println("Usuarios leidos desde: " + rutaUsuarios.toAbsolutePath());
            System.out.println("Fecha de registro del usuario: " + usuarioAutenticado.getFechaRegistro());

            Vehiculo vehiculo = capturarVehiculo(
                    scanner,
                    usuario,
                    usuarioAutenticado,
                    usuarios,
                    rutaUsuarios
            );

            String numeroParquimetro = leerConRegex(
                    scanner,
                    "Numero de parquimetro (0000-9999): ",
                    PARQUIMETRO_PATTERN,
                    "Debe ingresar exactamente 4 digitos entre 0000 y 9999."
            );
            int minutos = leerEnteroPositivo(scanner, "Minutos de reserva: ");

            ZonaParqueo zona = new ZonaParqueo("Z4", "Zona 4", new BigDecimal("1200.00"), 20);
            Parquimetro parquimetro = new Parquimetro(numeroParquimetro, zona);
            BigDecimal montoEstimado = zona.calcularMontoEstimado(minutos, vehiculo.obtenerFactorTarifa());

            System.out.println("\nResumen preliminar de cobro");
            System.out.println("Tipo de vehiculo: " + vehiculo.getTipoVehiculo());
            System.out.println("Factor aplicado: " + formatoMonto(vehiculo.obtenerFactorTarifa()));
            System.out.println("Monto estimado: CRC " + formatoMonto(montoEstimado));

            DatosTarjetaCapturada datosTarjeta = capturarTarjeta(scanner, usuario.getNombreCompleto());
            TarjetaCredito tarjeta = new TarjetaCredito(
                    datosTarjeta.getIdTarjeta(),
                    datosTarjeta.getTitular(),
                    datosTarjeta.getUltimosCuatro(),
                    datosTarjeta.getTokenPasarela()
            );
            usuario.registrarTarjeta(tarjeta);

            SolicitudEstacionamiento solicitud = new SolicitudEstacionamiento(
                    usuario.getIdUsuario(),
                    vehiculo.getIdVehiculo(),
                    parquimetro.getIdParquimetro(),
                    minutos,
                    tarjeta.getIdTarjeta()
            );

            ResultadoReserva resultadoReserva = estacionarVehiculoUseCase.ejecutar(
                    solicitud,
                    usuario,
                    vehiculo,
                    parquimetro
            );

            ConsultaPagosTarjeta consulta = new ConsultaPagosTarjeta(
                    usuario.getIdUsuario(),
                    tarjeta.getIdTarjeta(),
                    LocalDate.now()
            );
            List<ResumenPago> pagosDelDia = consultarPagosPorTarjetaUseCase.ejecutar(consulta);
            ResumenPago pagoReciente = pagosDelDia.stream()
                    .max(Comparator.comparing(ResumenPago::getFechaHora))
                    .orElse(null);

            String factura = construirFactura(
                    usuarioAutenticado,
                    vehiculo,
                    numeroParquimetro,
                    minutos,
                    montoEstimado,
                    datosTarjeta,
                    resultadoReserva,
                    pagoReciente
            );
            Path facturaPath = guardarFactura(factura, resultadoReserva.getIdEstadia());

            System.out.println("\nResultado de la reserva: " + resultadoReserva.getMensaje());
            System.out.println("ID de estadia: " + resultadoReserva.getIdEstadia());
            System.out.println("Monto final: CRC " + formatoMonto(resultadoReserva.getMontoReserva()));
            System.out.println("Pagos encontrados para hoy: " + pagosDelDia.size());
            System.out.println("Factura guardada en: " + facturaPath.toAbsolutePath());
            System.out.println("\n----- FACTURA -----");
            System.out.println(factura);
            System.out.println("-------------------");
        } catch (Exception ex) {
            System.err.println("No fue posible completar el flujo interactivo: " + ex.getMessage());
        }
    }

    private static Path obtenerRutaUsuarios() {
        return Paths.get("out", "usuarios", "usuarios.txt");
    }

    private static void asegurarArchivoUsuarios(Path rutaUsuarios) throws IOException {
        Files.createDirectories(rutaUsuarios.getParent());
        if (Files.exists(rutaUsuarios)) {
            return;
        }

        String plantilla = "ID_USUARIO=USR-604700374" + System.lineSeparator()
                + "NOMBRE=Jeffry" + System.lineSeparator()
                + "CEDULA=6-0470-0374" + System.lineSeparator()
                + "CORREO=jefaraya@estudiantec.cr" + System.lineSeparator()
                + "CONTRASENA=123456" + System.lineSeparator()
                + "FECHA_REGISTRO=2026-04-21 20:17:06" + System.lineSeparator()
                + "Vehiculos: [Carro: {No hay}; Motocicletas: {No hay}; Scooters: {No hay}]" + System.lineSeparator()
                + System.lineSeparator();

        Files.writeString(rutaUsuarios, plantilla, StandardCharsets.UTF_8);
    }

    private static UsuarioArchivo resolverAcceso(Scanner scanner, List<UsuarioArchivo> usuarios, Path rutaUsuarios)
            throws IOException {
        System.out.println("\n1) Desea registrar usuario o iniciar sesion");
        System.out.println("1. Registrar usuario");
        System.out.println("2. Iniciar sesion");

        int opcion = leerOpcion(scanner, "Seleccione una opcion (1-2): ", 1, 2);
        if (opcion == 1) {
            UsuarioArchivo nuevo = registrarUsuario(scanner, usuarios, rutaUsuarios);
            System.out.println("Usuario registrado correctamente. Sesion iniciada automaticamente.");
            return nuevo;
        }

        return iniciarSesion(scanner, usuarios);
    }

    private static UsuarioArchivo registrarUsuario(Scanner scanner, List<UsuarioArchivo> usuarios, Path rutaUsuarios)
            throws IOException {
        System.out.println("\nRegistro de usuario");
        String nombre = leerTextoNoVacio(scanner, "Nombre de usuario: ");

        String cedula;
        while (true) {
            cedula = leerConRegex(
                    scanner,
                    "Cedula (formato Costa Rica x-xxxx-xxxx): ",
                    CEDULA_CR_PATTERN,
                    "Formato invalido. Ejemplo valido: 1-1234-5678"
            );
            if (existeCedula(usuarios, cedula)) {
                System.out.println("La cedula ya existe. Debe ser unica.");
                continue;
            }
            break;
        }

        String correo;
        while (true) {
            correo = leerConRegex(
                    scanner,
                    "Correo electronico: ",
                    EMAIL_PATTERN,
                    "Correo invalido. Ingrese un correo con formato usuario@dominio"
            );
            if (existeCorreo(usuarios, correo)) {
                System.out.println("El correo ya existe. Debe ser unico.");
                continue;
            }
            break;
        }

        String contrasena = leerTextoNoVacio(scanner, "Contrasena: ");
        String fechaRegistro = LocalDateTime.now().format(FECHA_HORA_FORMAT);
        String idUsuario = generarIdUsuarioUnico(usuarios, cedula);

        UsuarioArchivo nuevo = new UsuarioArchivo(
                idUsuario,
                nombre,
                cedula,
                correo,
                contrasena,
                fechaRegistro,
                new LinkedHashSet<>(),
                new LinkedHashSet<>(),
                new LinkedHashSet<>()
        );

        usuarios.add(nuevo);
        guardarUsuariosEnArchivo(usuarios, rutaUsuarios);
        return nuevo;
    }

    private static String generarIdUsuarioUnico(List<UsuarioArchivo> usuarios, String cedula) {
        String base = "USR-" + cedula.replace("-", "");
        String candidato = base;
        int secuencia = 2;

        while (existeIdUsuario(usuarios, candidato)) {
            candidato = base + "-" + secuencia;
            secuencia++;
        }

        return candidato;
    }

    private static boolean existeIdUsuario(List<UsuarioArchivo> usuarios, String idUsuario) {
        for (UsuarioArchivo usuario : usuarios) {
            if (usuario.getIdUsuario().equals(idUsuario)) {
                return true;
            }
        }
        return false;
    }

    private static boolean existeCedula(List<UsuarioArchivo> usuarios, String cedula) {
        String cedulaNormalizada = cedula.replace("-", "");
        return usuarios.stream().anyMatch(
                usuario -> usuario.getCedula().replace("-", "").equals(cedulaNormalizada)
        );
    }

    private static boolean existeCorreo(List<UsuarioArchivo> usuarios, String correo) {
        return usuarios.stream().anyMatch(
                usuario -> usuario.getCorreo().equalsIgnoreCase(correo)
        );
    }

    private static UsuarioArchivo iniciarSesion(Scanner scanner, List<UsuarioArchivo> usuarios) {
        System.out.println("\nInicio de sesion");
        System.out.println("Ingrese correo, nombre de usuario y contrasena registrados en usuarios.txt");

        int maxIntentos = 3;
        for (int intento = 1; intento <= maxIntentos; intento++) {
            String correo = leerConRegex(
                    scanner,
                    "Correo registrado: ",
                    EMAIL_PATTERN,
                    "Formato de correo invalido."
            );
            String nombreUsuario = leerTextoNoVacio(scanner, "Nombre de usuario: ");
            String contrasena = leerTextoNoVacio(scanner, "Contrasena: ");

            UsuarioArchivo usuarioPorCorreo = usuarios.stream()
                    .filter(usuario -> usuario.getCorreo().equalsIgnoreCase(correo))
                    .findFirst()
                    .orElse(null);

            if (usuarioPorCorreo == null) {
                System.out.println("El correo no existe en usuarios.txt.");
                imprimirIntentosRestantes(intento, maxIntentos);
                continue;
            }

            boolean nombreCoincide = usuarioPorCorreo.getNombreCompleto().equals(nombreUsuario);
            boolean contrasenaCoincide = usuarioPorCorreo.getContrasena().equals(contrasena);

            if (!nombreCoincide || !contrasenaCoincide) {
                System.out.println("Nombre de usuario o contrasena incorrectos para ese correo.");
                imprimirIntentosRestantes(intento, maxIntentos);
                continue;
            }

            return usuarioPorCorreo;
        }

        throw new IllegalArgumentException("No se pudo iniciar sesion. Verifique sus credenciales.");
    }

    private static void imprimirIntentosRestantes(int intentoActual, int maxIntentos) {
        int restantes = maxIntentos - intentoActual;
        if (restantes > 0) {
            System.out.println("Intentos restantes: " + restantes);
        }
    }

    private static Vehiculo capturarVehiculo(
            Scanner scanner,
            Usuario usuario,
            UsuarioArchivo usuarioAutenticado,
            List<UsuarioArchivo> usuarios,
            Path rutaUsuarios
    ) throws IOException {
        while (true) {
            System.out.println("\n2) Tipo de vehiculo");
            System.out.println("1. Carro");
            System.out.println("2. Motocicleta");
            System.out.println("3. Scooter Electrico");
            int opcion = leerOpcion(scanner, "Seleccione una opcion (1-3): ", 1, 3);

            String tipo = mapearTipoVehiculo(opcion);
            String placa = leerTextoNoVacio(scanner, "Placa o identificador del vehiculo: ").toUpperCase(Locale.ROOT);

            ResultadoValidacionPlaca validacion = validarYRegistrarPlaca(
                    placa,
                    tipo,
                    usuarioAutenticado,
                    usuarios,
                    rutaUsuarios
            );

            if (!validacion.esValida()) {
                System.out.println(validacion.mensajeError());
                continue;
            }

            String idVehiculo = "VEH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            if (opcion == 1) {
                return new Carro(idVehiculo, placa, usuario);
            }
            if (opcion == 2) {
                return new Motocicleta(idVehiculo, placa, usuario);
            }
            return new ScooterElectrico(idVehiculo, placa, usuario);
        }
    }

    private static String mapearTipoVehiculo(int opcion) {
        if (opcion == 1) {
            return "CARRO";
        }
        if (opcion == 2) {
            return "MOTOCICLETA";
        }
        return "SCOOTER";
    }

    private static ResultadoValidacionPlaca validarYRegistrarPlaca(
            String placa,
            String tipoVehiculo,
            UsuarioArchivo usuarioActual,
            List<UsuarioArchivo> usuarios,
            Path rutaUsuarios
    ) throws IOException {
        for (UsuarioArchivo usuario : usuarios) {
            String tipoRegistrado = usuario.tipoDePlaca(placa);
            if (tipoRegistrado == null) {
                continue;
            }

            if (!usuario.getIdUsuario().equals(usuarioActual.getIdUsuario())) {
                return ResultadoValidacionPlaca.invalida(
                        "La placa ya esta registrada para otro usuario: " + usuario.getNombreCompleto()
                );
            }

            if (!tipoRegistrado.equals(tipoVehiculo)) {
                return ResultadoValidacionPlaca.invalida(
                        "La placa ya existe para este usuario en tipo " + tipoRegistrado + "."
                );
            }

            return ResultadoValidacionPlaca.valida();
        }

        usuarioActual.agregarPlaca(tipoVehiculo, placa);
        guardarUsuariosEnArchivo(usuarios, rutaUsuarios);
        return ResultadoValidacionPlaca.valida();
    }

    private static DatosTarjetaCapturada capturarTarjeta(Scanner scanner, String nombreTitularSugerido) {
        System.out.println("\n6) Tarjeta para el cobro");
        System.out.println("1. Tarjeta de credito");
        System.out.println("2. Tarjeta de debito");
        int opcion = leerOpcion(scanner, "Seleccione una opcion (1-2): ", 1, 2);

        String numeroTarjeta = leerConRegex(
                scanner,
                "Numero de tarjeta (13 a 19 digitos, sin espacios): ",
                TARJETA_PATTERN,
                "Numero invalido. Debe contener solo digitos (13 a 19)."
        );

        System.out.println("Titular sugerido: " + nombreTitularSugerido);
        String titular = leerTextoNoVacio(scanner, "Titular de la tarjeta: ");

        String tipo = opcion == 1 ? "CREDITO" : "DEBITO";
        String prefijo = opcion == 1 ? "TC-" : "TD-";
        String ultimosCuatro = numeroTarjeta.substring(numeroTarjeta.length() - 4);
        String idTarjeta = prefijo + ultimosCuatro;
        String tokenPasarela = "tok-" + UUID.randomUUID().toString().substring(0, 10).toLowerCase(Locale.ROOT);

        return new DatosTarjetaCapturada(tipo, titular, ultimosCuatro, idTarjeta, tokenPasarela);
    }

    private static List<UsuarioArchivo> cargarUsuariosDesdeArchivo(Path rutaUsuarios) throws IOException {
        String contenido = Files.readString(rutaUsuarios, StandardCharsets.UTF_8);
        if (contenido.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String contenidoNormalizado = contenido.replace("\r\n", "\n").trim();
        String[] bloques = contenidoNormalizado.split("\\n\\s*\\n");

        List<UsuarioArchivo> usuarios = new ArrayList<>();
        for (String bloque : bloques) {
            UsuarioArchivo usuario = parsearBloqueUsuario(bloque);
            if (usuario != null) {
                usuarios.add(usuario);
            }
        }

        return usuarios;
    }

    private static UsuarioArchivo parsearBloqueUsuario(String bloque) {
        Map<String, String> campos = new HashMap<>();
        String lineaVehiculos = null;

        String[] lineas = bloque.split("\\n");
        for (String linea : lineas) {
            String lineaLimpia = linea.trim();
            if (lineaLimpia.isEmpty()) {
                continue;
            }

            if (lineaLimpia.toLowerCase(Locale.ROOT).startsWith("vehiculos:")) {
                lineaVehiculos = lineaLimpia;
                continue;
            }

            int separador = lineaLimpia.indexOf('=');
            if (separador <= 0 || separador >= lineaLimpia.length() - 1) {
                continue;
            }

            String clave = lineaLimpia.substring(0, separador).trim().toUpperCase(Locale.ROOT);
            String valor = lineaLimpia.substring(separador + 1).trim();
            campos.put(clave, valor);
        }

        String idUsuario = campos.get("ID_USUARIO");
        String nombre = campos.get("NOMBRE");
        String cedula = campos.get("CEDULA");
        String correo = campos.get("CORREO");
        String contrasena = campos.get("CONTRASENA");
        String fechaRegistro = campos.get("FECHA_REGISTRO");

        if (esVacio(idUsuario)
                || esVacio(nombre)
                || esVacio(cedula)
                || esVacio(correo)
                || esVacio(contrasena)
                || esVacio(fechaRegistro)) {
            return null;
        }

        Set<String> carros = new LinkedHashSet<>();
        Set<String> motocicletas = new LinkedHashSet<>();
        Set<String> scooters = new LinkedHashSet<>();
        parsearLineaVehiculos(lineaVehiculos, carros, motocicletas, scooters);

        return new UsuarioArchivo(
                idUsuario,
                nombre,
                cedula,
                correo,
                contrasena,
                fechaRegistro,
                carros,
                motocicletas,
                scooters
        );
    }

    private static void parsearLineaVehiculos(
            String lineaVehiculos,
            Set<String> carros,
            Set<String> motocicletas,
            Set<String> scooters
    ) {
        if (esVacio(lineaVehiculos)) {
            return;
        }

        String cuerpo = extraerCuerpoVehiculos(lineaVehiculos);
        if (cuerpo == null) {
            return;
        }

        String valoresCarros = extraerBloqueCategoria(cuerpo, "Carro");
        String valoresMotocicletas = extraerBloqueCategoria(cuerpo, "Motocicletas");
        String valoresScooters = extraerBloqueCategoria(cuerpo, "Scooters");

        agregarPlacasDesdeTexto(carros, valoresCarros);
        agregarPlacasDesdeTexto(motocicletas, valoresMotocicletas);
        agregarPlacasDesdeTexto(scooters, valoresScooters);
    }

    private static String extraerCuerpoVehiculos(String lineaVehiculos) {
        int inicio = lineaVehiculos.indexOf('[');
        int fin = lineaVehiculos.lastIndexOf(']');
        if (inicio < 0 || fin <= inicio) {
            return null;
        }
        return lineaVehiculos.substring(inicio + 1, fin);
    }

    private static String extraerBloqueCategoria(String cuerpo, String categoria) {
        Pattern patron = Pattern.compile(categoria + "\\s*:\\s*\\{([^}]*)}", Pattern.CASE_INSENSITIVE);
        Matcher matcher = patron.matcher(cuerpo);
        if (!matcher.find()) {
            return null;
        }
        return matcher.group(1);
    }

    private static void agregarPlacasDesdeTexto(Set<String> destino, String valorCategoria) {
        if (esVacio(valorCategoria)) {
            return;
        }

        String limpio = valorCategoria.trim();
        if (limpio.equalsIgnoreCase("No hay")) {
            return;
        }

        String[] placas = limpio.split(";");
        for (String placa : placas) {
            String placaLimpia = placa.trim().toUpperCase(Locale.ROOT);
            if (!placaLimpia.isEmpty()) {
                destino.add(placaLimpia);
            }
        }
    }

    private static void validarNoPlacasDuplicadasGlobal(List<UsuarioArchivo> usuarios) {
        Map<String, String> propietarioPorPlaca = new HashMap<>();

        for (UsuarioArchivo usuario : usuarios) {
            for (String placa : usuario.todasLasPlacas()) {
                String placaNormalizada = placa.toUpperCase(Locale.ROOT);
                String propietarioPrevio = propietarioPorPlaca.putIfAbsent(placaNormalizada, usuario.getIdUsuario());
                if (propietarioPrevio != null) {
                    throw new IllegalStateException(
                            "La placa " + placa + " esta repetida en usuarios.txt."
                    );
                }
            }
        }
    }

    private static void guardarUsuariosEnArchivo(List<UsuarioArchivo> usuarios, Path rutaUsuarios) throws IOException {
        String salto = System.lineSeparator();
        StringBuilder contenido = new StringBuilder();

        for (UsuarioArchivo usuario : usuarios) {
            contenido.append("ID_USUARIO=").append(usuario.getIdUsuario()).append(salto);
            contenido.append("NOMBRE=").append(usuario.getNombreCompleto()).append(salto);
            contenido.append("CEDULA=").append(usuario.getCedula()).append(salto);
            contenido.append("CORREO=").append(usuario.getCorreo()).append(salto);
            contenido.append("CONTRASENA=").append(usuario.getContrasena()).append(salto);
            contenido.append("FECHA_REGISTRO=").append(usuario.getFechaRegistro()).append(salto);
            contenido.append("Vehiculos: [Carro: {")
                    .append(formatearListaVehiculos(usuario.getCarros()))
                    .append("}; Motocicletas: {")
                    .append(formatearListaVehiculos(usuario.getMotocicletas()))
                    .append("}; Scooters: {")
                    .append(formatearListaVehiculos(usuario.getScooters()))
                    .append("}]")
                    .append(salto)
                    .append(salto);
        }

        Files.writeString(rutaUsuarios, contenido.toString(), StandardCharsets.UTF_8);
    }

    private static String formatearListaVehiculos(Set<String> placas) {
        if (placas.isEmpty()) {
            return "No hay";
        }
        return String.join("; ", placas);
    }

    private static boolean esVacio(String valor) {
        return valor == null || valor.isBlank();
    }

    private static Path guardarFactura(String factura, String idEstadia) throws IOException {
        Path carpetaFacturas = Paths.get("out", "facturas");
        Files.createDirectories(carpetaFacturas);

        String marcaTiempo = LocalDateTime.now().format(FACTURA_ID_FORMAT);
        String idLimpio = idEstadia == null || idEstadia.isBlank()
                ? "SIN_ESTADIA"
                : idEstadia.replaceAll("[^a-zA-Z0-9-]", "");

        Path archivo = carpetaFacturas.resolve("factura_" + marcaTiempo + "_" + idLimpio + ".txt");
        Files.writeString(archivo, factura, StandardCharsets.UTF_8);
        return archivo;
    }

    private static String construirFactura(
            UsuarioArchivo usuarioAutenticado,
            Vehiculo vehiculo,
            String numeroParquimetro,
            int minutos,
            BigDecimal montoEstimado,
            DatosTarjetaCapturada datosTarjeta,
            ResultadoReserva resultadoReserva,
            ResumenPago pagoReciente
    ) {
        String estadoPago = pagoReciente != null
                ? pagoReciente.getEstado().name()
                : (resultadoReserva.isPagoAprobado() ? "APROBADO" : "RECHAZADO");

        String referenciaPago = pagoReciente != null && pagoReciente.getReferencia() != null
                ? pagoReciente.getReferencia()
                : "SIN_REFERENCIA";

        String idPago = pagoReciente != null ? pagoReciente.getIdPago() : "SIN_ID_PAGO";
        String fechaPago = pagoReciente != null
                ? pagoReciente.getFechaHora().format(FECHA_HORA_FORMAT)
                : LocalDateTime.now().format(FECHA_HORA_FORMAT);

        String montoFinal = pagoReciente != null
                ? formatoMonto(pagoReciente.getMonto())
                : formatoMonto(resultadoReserva.getMontoReserva());

        String numeroOculto = "**** **** **** " + datosTarjeta.getUltimosCuatro();

        StringBuilder builder = new StringBuilder();
        builder.append("FACTURA EPARK").append(System.lineSeparator());
        builder.append("Fecha: ").append(LocalDateTime.now().format(FECHA_HORA_FORMAT)).append(System.lineSeparator());
        builder.append("----------------------------------------").append(System.lineSeparator());
        builder.append("Cliente: ").append(usuarioAutenticado.getNombreCompleto()).append(System.lineSeparator());
        builder.append("Cedula: ").append(usuarioAutenticado.getCedula()).append(System.lineSeparator());
        builder.append("Correo: ").append(usuarioAutenticado.getCorreo()).append(System.lineSeparator());
        builder.append("Registro usuario: ").append(usuarioAutenticado.getFechaRegistro()).append(System.lineSeparator());
        builder.append("----------------------------------------").append(System.lineSeparator());
        builder.append("Vehiculo: ").append(vehiculo.getTipoVehiculo()).append(System.lineSeparator());
        builder.append("Placa/ID: ").append(vehiculo.getPlaca()).append(System.lineSeparator());
        builder.append("Parquimetro: ").append(numeroParquimetro).append(System.lineSeparator());
        builder.append("Tiempo reservado (min): ").append(minutos).append(System.lineSeparator());
        builder.append("Tarifa base por hora: CRC 1200.00").append(System.lineSeparator());
        builder.append("Factor de vehiculo: ").append(formatoMonto(vehiculo.obtenerFactorTarifa())).append(System.lineSeparator());
        builder.append("Monto estimado: CRC ").append(formatoMonto(montoEstimado)).append(System.lineSeparator());
        builder.append("Monto final cobrado: CRC ").append(montoFinal).append(System.lineSeparator());
        builder.append("----------------------------------------").append(System.lineSeparator());
        builder.append("Tarjeta: ").append(datosTarjeta.getTipo()).append(" ").append(numeroOculto).append(System.lineSeparator());
        builder.append("Titular: ").append(datosTarjeta.getTitular()).append(System.lineSeparator());
        builder.append("Estado pago: ").append(estadoPago).append(System.lineSeparator());
        builder.append("ID pago: ").append(idPago).append(System.lineSeparator());
        builder.append("Referencia: ").append(referenciaPago).append(System.lineSeparator());
        builder.append("Fecha pago: ").append(fechaPago).append(System.lineSeparator());
        builder.append("----------------------------------------").append(System.lineSeparator());
        builder.append("ID estadia: ").append(resultadoReserva.getIdEstadia()).append(System.lineSeparator());
        builder.append("Vence: ").append(resultadoReserva.getHoraVencimiento().format(FECHA_HORA_FORMAT)).append(System.lineSeparator());
        builder.append("Mensaje del sistema: ").append(resultadoReserva.getMensaje()).append(System.lineSeparator());
        return builder.toString();
    }

    private static String leerTextoNoVacio(Scanner scanner, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String valor = scanner.nextLine().trim();
            if (!valor.isEmpty()) {
                return valor;
            }
            System.out.println("El dato no puede estar vacio.");
        }
    }

    private static String leerConRegex(Scanner scanner, String mensaje, Pattern patron, String error) {
        while (true) {
            String valor = leerTextoNoVacio(scanner, mensaje);
            if (patron.matcher(valor).matches()) {
                return valor;
            }
            System.out.println(error);
        }
    }

    private static int leerEnteroPositivo(Scanner scanner, String mensaje) {
        while (true) {
            String valor = leerTextoNoVacio(scanner, mensaje);
            try {
                int numero = Integer.parseInt(valor);
                if (numero > 0) {
                    return numero;
                }
            } catch (NumberFormatException ignored) {
                // Se controla con el mensaje de validacion.
            }
            System.out.println("Debe ingresar un numero entero mayor que cero.");
        }
    }

    private static int leerOpcion(Scanner scanner, String mensaje, int minimo, int maximo) {
        while (true) {
            int valor = leerEnteroPositivo(scanner, mensaje);
            if (valor >= minimo && valor <= maximo) {
                return valor;
            }
            System.out.println("La opcion debe estar entre " + minimo + " y " + maximo + ".");
        }
    }

    private static String formatoMonto(BigDecimal monto) {
        return monto.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private static final class UsuarioArchivo {
        private final String idUsuario;
        private final String nombreCompleto;
        private final String cedula;
        private final String correo;
        private final String contrasena;
        private final String fechaRegistro;
        private final Set<String> carros;
        private final Set<String> motocicletas;
        private final Set<String> scooters;

        private UsuarioArchivo(
                String idUsuario,
                String nombreCompleto,
                String cedula,
                String correo,
                String contrasena,
                String fechaRegistro,
                Set<String> carros,
                Set<String> motocicletas,
                Set<String> scooters
        ) {
            this.idUsuario = idUsuario;
            this.nombreCompleto = nombreCompleto;
            this.cedula = cedula;
            this.correo = correo;
            this.contrasena = contrasena;
            this.fechaRegistro = fechaRegistro;
            this.carros = carros;
            this.motocicletas = motocicletas;
            this.scooters = scooters;
        }

        public String getIdUsuario() {
            return idUsuario;
        }

        public String getNombreCompleto() {
            return nombreCompleto;
        }

        public String getCedula() {
            return cedula;
        }

        public String getCorreo() {
            return correo;
        }

        public String getContrasena() {
            return contrasena;
        }

        public String getFechaRegistro() {
            return fechaRegistro;
        }

        public Set<String> getCarros() {
            return carros;
        }

        public Set<String> getMotocicletas() {
            return motocicletas;
        }

        public Set<String> getScooters() {
            return scooters;
        }

        public String tipoDePlaca(String placa) {
            String placaNormalizada = placa.toUpperCase(Locale.ROOT);
            if (carros.contains(placaNormalizada)) {
                return "CARRO";
            }
            if (motocicletas.contains(placaNormalizada)) {
                return "MOTOCICLETA";
            }
            if (scooters.contains(placaNormalizada)) {
                return "SCOOTER";
            }
            return null;
        }

        public void agregarPlaca(String tipoVehiculo, String placa) {
            String placaNormalizada = placa.toUpperCase(Locale.ROOT);
            if ("CARRO".equals(tipoVehiculo)) {
                carros.add(placaNormalizada);
                return;
            }
            if ("MOTOCICLETA".equals(tipoVehiculo)) {
                motocicletas.add(placaNormalizada);
                return;
            }
            scooters.add(placaNormalizada);
        }

        public List<String> todasLasPlacas() {
            List<String> placas = new ArrayList<>();
            placas.addAll(carros);
            placas.addAll(motocicletas);
            placas.addAll(scooters);
            return placas;
        }
    }

    private static final class DatosTarjetaCapturada {
        private final String tipo;
        private final String titular;
        private final String ultimosCuatro;
        private final String idTarjeta;
        private final String tokenPasarela;

        private DatosTarjetaCapturada(
                String tipo,
                String titular,
                String ultimosCuatro,
                String idTarjeta,
                String tokenPasarela
        ) {
            this.tipo = tipo;
            this.titular = titular;
            this.ultimosCuatro = ultimosCuatro;
            this.idTarjeta = idTarjeta;
            this.tokenPasarela = tokenPasarela;
        }

        public String getTipo() {
            return tipo;
        }

        public String getTitular() {
            return titular;
        }

        public String getUltimosCuatro() {
            return ultimosCuatro;
        }

        public String getIdTarjeta() {
            return idTarjeta;
        }

        public String getTokenPasarela() {
            return tokenPasarela;
        }
    }

    private static final class ResultadoValidacionPlaca {
        private final boolean valida;
        private final String mensajeError;

        private ResultadoValidacionPlaca(boolean valida, String mensajeError) {
            this.valida = valida;
            this.mensajeError = mensajeError;
        }

        private static ResultadoValidacionPlaca valida() {
            return new ResultadoValidacionPlaca(true, null);
        }

        private static ResultadoValidacionPlaca invalida(String mensajeError) {
            return new ResultadoValidacionPlaca(false, mensajeError);
        }

        private boolean esValida() {
            return valida;
        }

        private String mensajeError() {
            return mensajeError;
        }
    }
}
