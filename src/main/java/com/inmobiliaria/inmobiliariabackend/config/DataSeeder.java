package com.inmobiliaria.inmobiliariabackend.config;

import com.inmobiliaria.inmobiliariabackend.model.*;
import com.inmobiliaria.inmobiliariabackend.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class DataSeeder implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    @Value("${app.seed.enabled:false}")
    private boolean seedEnabled;

    @Value("${app.seed.reset:false}")
    private boolean seedReset;

    private final DepartamentoRepository departamentoRepository;
    private final ProvinciaRepository provinciaRepository;
    private final DistritoRepository distritoRepository;
    private final ClienteRepository clienteRepository;
    private final EstadoLoteRepository estadoLoteRepository;
    private final EstadoVentaRepository estadoVentaRepository;
    private final MonedaRepository monedaRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;
    private final TipoLoteRepository tipoLoteRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final UsuarioRepository usuarioRepository;
    private final LoteRepository loteRepository;
    private final VentaRepository ventaRepository;

    public DataSeeder(
            DepartamentoRepository departamentoRepository,
            ProvinciaRepository provinciaRepository,
            DistritoRepository distritoRepository,
            ClienteRepository clienteRepository,
            EstadoLoteRepository estadoLoteRepository,
            EstadoVentaRepository estadoVentaRepository,
            MonedaRepository monedaRepository,
            TipoDocumentoRepository tipoDocumentoRepository,
            TipoLoteRepository tipoLoteRepository,
            UsuarioRolRepository usuarioRolRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            LoteRepository loteRepository,
            VentaRepository ventaRepository) {
        this.departamentoRepository = departamentoRepository;
        this.provinciaRepository = provinciaRepository;
        this.distritoRepository = distritoRepository;
        this.clienteRepository = clienteRepository;
        this.estadoLoteRepository = estadoLoteRepository;
        this.estadoVentaRepository = estadoVentaRepository;
        this.monedaRepository = monedaRepository;
        this.tipoDocumentoRepository = tipoDocumentoRepository;
        this.tipoLoteRepository = tipoLoteRepository;
        this.usuarioRolRepository = usuarioRolRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.loteRepository = loteRepository;
        this.ventaRepository = ventaRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!seedEnabled) {
            System.out.println("🔹 Seed deshabilitado por configuración.");
            return;
        }

        System.out.println("🚀 Ejecutando DataSeeder...");

        if (seedReset) {
            borrarDatos();
        }

        seedEstadoLote();
        seedEstadosVenta();
        seedMonedas();
        seedTiposDocumento();
        seedClientes();
        seedTiposLote();
        seedRoles();
        seedUsuarios();
        seedDepartamentos();
        seedProvincias();
        seedAllDistritos();
        seedLotes();
        seedVentas();

        System.out.println("✅ DataSeeder finalizado.");
    }

    private void borrarDatos() {
        System.out.println("⚠️ Borrando datos previos...");
        estadoLoteRepository.deleteAll();
        estadoVentaRepository.deleteAll();
        monedaRepository.deleteAll();
        tipoDocumentoRepository.deleteAll();
        tipoLoteRepository.deleteAll();
    }

    private void seedClientes() {
        if (clienteRepository.count() == 0) {

            // Asegúrate de tener este registro en catalogo.tiposDocumento
            TipoDocumento dni = tipoDocumentoRepository.findByNombre("DNI")
                    .orElseThrow(() -> new RuntimeException("TipoDocumento 'DNI' no encontrado"));

            Cliente c1 = new Cliente();
            c1.setPrimerNombre("Juan");
            c1.setSegundoNombre("Carlos"); // opcional
            c1.setApellidoPaterno("Pérez");
            c1.setApellidoMaterno("García");
            c1.setTipoDocumento(dni);
            c1.setNumeroDocumento("12345678");
            c1.setCorreo("juan.perez@example.com");
            c1.setTelefono("987654321");
            c1.setVisitasRealizadas(2);
            c1.setLlamadasNoAtendidas(1);
            c1.setDiasDesdeUltimaVisita(5);
            c1.setIngresosMensuales(2500.00);
            c1.setFechaRegistro(LocalDate.now());
            c1.setActivo(true);

            Cliente c2 = new Cliente();
            c2.setPrimerNombre("María");
            c2.setSegundoNombre(null); // puede ser null
            c2.setApellidoPaterno("Lopez");
            c2.setApellidoMaterno("Ramos");
            c2.setTipoDocumento(dni);
            c2.setNumeroDocumento("87654321");
            c2.setCorreo("maria.lopez@example.com");
            c2.setTelefono("912345678");
            c2.setVisitasRealizadas(0);
            c2.setLlamadasNoAtendidas(0);
            c2.setDiasDesdeUltimaVisita(0);
            c2.setIngresosMensuales(3100.00);
            c2.setFechaRegistro(LocalDate.now());
            c2.setActivo(true);

            clienteRepository.saveAll(Arrays.asList(c1, c2));
            System.out.println("✅ Clientes de prueba insertados en crm.clientes");
        } else {
            System.out.println("ℹ️ Ya existen clientes en la base de datos, no se insertaron duplicados.");
        }
    }

    private void seedEstadoLote() {
        if (estadoLoteRepository.count() == 0) {
            EstadoLote disponible = new EstadoLote(
                    null,
                    "Disponible",
                    "El lote está disponible para la venta",
                    true
            );

            EstadoLote reservado = new EstadoLote(
                    null,
                    "Reservado",
                    "El lote ha sido reservado por un cliente",
                    true
            );

            EstadoLote vendido = new EstadoLote(
                    null,
                    "Vendido",
                    "El lote ya fue vendido",
                    true
            );

            estadoLoteRepository.saveAll(Arrays.asList(disponible, reservado, vendido));
            System.out.println("✅ Datos iniciales insertados en catalogo.estadosLote");
        } else {
            System.out.println("ℹ️ Datos de EstadoLote ya existen, no se insertaron duplicados.");
        }
    }

    private void seedEstadosVenta() {
        if (estadoVentaRepository.count() == 0) {
            EstadoVenta pendiente = new EstadoVenta(
                    null,
                    "Pendiente",
                    "La venta está registrada pero aún no confirmada",
                    true
            );
            EstadoVenta confirmada = new EstadoVenta(
                    null,
                    "Confirmada",
                    "La venta fue confirmada y aprobada",
                    true
            );
            EstadoVenta cancelada = new EstadoVenta(
                    null,
                    "Cancelada",
                    "La venta fue anulada antes de su confirmación",
                    true
            );

            estadoVentaRepository.saveAll(Arrays.asList(pendiente, confirmada, cancelada));
            System.out.println("✅ Estados de Venta insertados correctamente");
        }
    }

    private void seedMonedas() {
        if (monedaRepository.count() == 0) {
            List<Moneda> monedas = Arrays.asList(
                    new Moneda(null, "Sol", "S/.", "Moneda de Perú", true),
                    new Moneda(null, "Dólar", "$", "Moneda de Estados Unidos", true),
                    new Moneda(null, "Euro", "€", "Moneda de la Unión Europea", true)
            );
            monedaRepository.saveAll(monedas);
        }
    }

    private void seedTiposDocumento() {
        if (tipoDocumentoRepository.count() == 0) {
            List<TipoDocumento> tipos = Arrays.asList(
                    new TipoDocumento(null, "DNI", "Documento Nacional de Identidad", true),
                    new TipoDocumento(null, "Pasaporte", "Documento de viaje emitido por un país", true),
                    new TipoDocumento(null, "Carnet de Extranjería", "Documento para extranjeros residentes", true),
                    new TipoDocumento(null, "RUC", "Registro Único de Contribuyente", true)
            );
            tipoDocumentoRepository.saveAll(tipos);
            System.out.println("✔ Tipos de Documento iniciales insertados.");
        }
    }

    private void seedTiposLote() {
        if (tipoLoteRepository.count() == 0) {
            TipoLote residencial = new TipoLote(null, "Residencial", "Lote destinado a vivienda", true);
            TipoLote comercial = new TipoLote(null, "Comercial", "Lote destinado a negocios", true);
            TipoLote industrial = new TipoLote(null, "Industrial", "Lote destinado a fábricas o almacenes", true);

            tipoLoteRepository.saveAll(Arrays.asList(residencial, comercial, industrial));
            System.out.println(">>> Se insertaron los tipos de lote por defecto");
        }
    }

    public void seedRoles() {
        if (usuarioRolRepository.count() == 0) {

            List<UsuarioRol> roles = Arrays.asList(
                    new UsuarioRol(null, "01", "ADMIN", true),
                    new UsuarioRol(null, "02", "USER", true)
            );

            usuarioRolRepository.saveAll(roles);

            System.out.println("Roles iniciales creados: " + roles.size());
        }
    }

    private void seedUsuarios() {
        if (usuarioRepository.count() == 0) {

            UsuarioRol rolAdmin = usuarioRolRepository.findByCodigo("01")
                    .orElseThrow(() -> new RuntimeException("Rol ADMIN no encontrado"));
            UsuarioRol rolUser = usuarioRolRepository.findByCodigo("02")
                    .orElseThrow(() -> new RuntimeException("Rol USER no encontrado"));

            List<Usuario> usuarios = Arrays.asList(
                    new Usuario(
                            null,
                            "admin",
                            passwordEncoder.encode("admin"),
                            Set.of(rolAdmin, rolUser), // Admin tiene todos los roles
                            true
                    ),
                    new Usuario(
                            null,
                            "user",
                            passwordEncoder.encode("user"),
                            Set.of(rolUser), // Usuario normal solo ROLE_USER
                            true
                    )
            );
            usuarioRepository.saveAll(usuarios);
        }
    }

    private void seedDepartamentos() {
        if (departamentoRepository.count() == 0) {
            List<Departamento> departamentos = Arrays.asList(
                    new Departamento(null, "Amazonas", "Departamento de Amazonas", true),
                    new Departamento(null, "Áncash", "Departamento de Áncash", true),
                    new Departamento(null, "Apurímac", "Departamento de Apurímac", true),
                    new Departamento(null, "Arequipa", "Departamento de Arequipa", true),
                    new Departamento(null, "Ayacucho", "Departamento de Ayacucho", true),
                    new Departamento(null, "Cajamarca", "Departamento de Cajamarca", true),
                    new Departamento(null, "Callao", "Departamento del Callao", true),
                    new Departamento(null, "Cusco", "Departamento de Cusco", true),
                    new Departamento(null, "Huancavelica", "Departamento de Huancavelica", true),
                    new Departamento(null, "Huánuco", "Departamento de Huánuco", true),
                    new Departamento(null, "Ica", "Departamento de Ica", true),
                    new Departamento(null, "Junín", "Departamento de Junín", true),
                    new Departamento(null, "La Libertad", "Departamento de La Libertad", true),
                    new Departamento(null, "Lambayeque", "Departamento de Lambayeque", true),
                    new Departamento(null, "Lima", "Departamento de Lima", true),
                    new Departamento(null, "Loreto", "Departamento de Loreto", true),
                    new Departamento(null, "Madre de Dios", "Departamento de Madre de Dios", true),
                    new Departamento(null, "Moquegua", "Departamento de Moquegua", true),
                    new Departamento(null, "Pasco", "Departamento de Pasco", true),
                    new Departamento(null, "Piura", "Departamento de Piura", true),
                    new Departamento(null, "Puno", "Departamento de Puno", true),
                    new Departamento(null, "San Martín", "Departamento de San Martín", true),
                    new Departamento(null, "Tacna", "Departamento de Tacna", true),
                    new Departamento(null, "Tumbes", "Departamento de Tumbes", true),
                    new Departamento(null, "Ucayali", "Departamento de Ucayali", true)
            );

            departamentoRepository.saveAll(departamentos);
        }
    }

    private void seedProvincias() {
        if (provinciaRepository.count() == 0) {
            List<Provincia> provincias = Arrays.asList(
                    // Amazonas
                    new Provincia(
                            null,
                            "Chachapoyas",
                            "Provincia de Chachapoyas",
                            true,
                            departamentoRepository.findByNombre("Amazonas")
                                    .orElseThrow(() -> new RuntimeException("Departamento Amazonas no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Bagua",
                            "Provincia de Bagua",
                            true,
                            departamentoRepository.findByNombre("Amazonas")
                                    .orElseThrow(() -> new RuntimeException("Departamento Amazonas no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Bongará",
                            "Provincia de Bongará",
                            true,
                            departamentoRepository.findByNombre("Amazonas")
                                    .orElseThrow(() -> new RuntimeException("Departamento Amazonas no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Condorcanqui",
                            "Provincia de Condorcanqui",
                            true,
                            departamentoRepository.findByNombre("Amazonas")
                                    .orElseThrow(() -> new RuntimeException("Departamento Amazonas no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Luya",
                            "Provincia de Luya",
                            true,
                            departamentoRepository.findByNombre("Amazonas")
                                    .orElseThrow(() -> new RuntimeException("Departamento Amazonas no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Rodríguez de Mendoza",
                            "Provincia de Rodríguez de Mendoza",
                            true,
                            departamentoRepository.findByNombre("Amazonas")
                                    .orElseThrow(() -> new RuntimeException("Departamento Amazonas no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Utcubamba",
                            "Provincia de Utcubamba",
                            true,
                            departamentoRepository.findByNombre("Amazonas")
                                    .orElseThrow(() -> new RuntimeException("Departamento Amazonas no encontrado"))
                    ),

                    // Áncash
                    new Provincia(
                            null,
                            "Huaraz",
                            "Provincia de Huaraz",
                            true,
                            departamentoRepository.findByNombre("Áncash")
                                    .orElseThrow(() -> new RuntimeException("Departamento Áncash no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Aija",
                            "Provincia de Aija",
                            true,
                            departamentoRepository.findByNombre("Áncash")
                                    .orElseThrow(() -> new RuntimeException("Departamento Áncash no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Antonio Raymondi",
                            "Provincia de Antonio Raymondi",
                            true,
                            departamentoRepository.findByNombre("Áncash")
                                    .orElseThrow(() -> new RuntimeException("Departamento Áncash no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Asunción",
                            "Provincia de Asunción",
                            true,
                            departamentoRepository.findByNombre("Áncash")
                                    .orElseThrow(() -> new RuntimeException("Departamento Áncash no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Bolognesi",
                            "Provincia de Bolognesi",
                            true,
                            departamentoRepository.findByNombre("Áncash")
                                    .orElseThrow(() -> new RuntimeException("Departamento Áncash no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Carhuaz",
                            "Provincia de Carhuaz",
                            true,
                            departamentoRepository.findByNombre("Áncash")
                                    .orElseThrow(() -> new RuntimeException("Departamento Áncash no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Carlos Fermín Fitzcarrald",
                            "Provincia de Carlos Fermín Fitzcarrald",
                            true,
                            departamentoRepository.findByNombre("Áncash")
                                    .orElseThrow(() -> new RuntimeException("Departamento Áncash no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Casma",
                            "Provincia de Casma",
                            true,
                            departamentoRepository.findByNombre("Áncash")
                                    .orElseThrow(() -> new RuntimeException("Departamento Áncash no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Corongo",
                            "Provincia de Corongo",
                            true,
                            departamentoRepository.findByNombre("Áncash")
                                    .orElseThrow(() -> new RuntimeException("Departamento Áncash no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Huari",
                            "Provincia de Huari",
                            true,
                            departamentoRepository.findByNombre("Áncash")
                                    .orElseThrow(() -> new RuntimeException("Departamento Áncash no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Huarmey",
                            "Provincia de Huarmey",
                            true,
                            departamentoRepository.findByNombre("Áncash")
                                    .orElseThrow(() -> new RuntimeException("Departamento Áncash no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Huaylas",
                            "Provincia de Huaylas",
                            true,
                            departamentoRepository.findByNombre("Áncash")
                                    .orElseThrow(() -> new RuntimeException("Departamento Áncash no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Mariscal Luzuriaga",
                            "Provincia de Mariscal Luzuriaga",
                            true,
                            departamentoRepository.findByNombre("Áncash")
                                    .orElseThrow(() -> new RuntimeException("Departamento Áncash no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Ocros",
                            "Provincia de Ocros",
                            true,
                            departamentoRepository.findByNombre("Áncash")
                                    .orElseThrow(() -> new RuntimeException("Departamento Áncash no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Pallasca",
                            "Provincia de Pallasca",
                            true,
                            departamentoRepository.findByNombre("Áncash")
                                    .orElseThrow(() -> new RuntimeException("Departamento Áncash no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Pomabamba",
                            "Provincia de Pomabamba",
                            true,
                            departamentoRepository.findByNombre("Áncash")
                                    .orElseThrow(() -> new RuntimeException("Departamento Áncash no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Recuay",
                            "Provincia de Recuay",
                            true,
                            departamentoRepository.findByNombre("Áncash")
                                    .orElseThrow(() -> new RuntimeException("Departamento Áncash no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Santa",
                            "Provincia de Santa",
                            true,
                            departamentoRepository.findByNombre("Áncash")
                                    .orElseThrow(() -> new RuntimeException("Departamento Áncash no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Sihuas",
                            "Provincia de Sihuas",
                            true,
                            departamentoRepository.findByNombre("Áncash")
                                    .orElseThrow(() -> new RuntimeException("Departamento Áncash no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Yungay",
                            "Provincia de Yungay",
                            true,
                            departamentoRepository.findByNombre("Áncash")
                                    .orElseThrow(() -> new RuntimeException("Departamento Áncash no encontrado"))
                    ),

                    // Apurímac
                    new Provincia(
                            null,
                            "Abancay",
                            "Provincia de Abancay",
                            true,
                            departamentoRepository.findByNombre("Apurímac")
                                    .orElseThrow(() -> new RuntimeException("Departamento Apurímac no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Andahuaylas",
                            "Provincia de Andahuaylas",
                            true,
                            departamentoRepository.findByNombre("Apurímac")
                                    .orElseThrow(() -> new RuntimeException("Departamento Apurímac no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Antabamba",
                            "Provincia de Antabamba",
                            true,
                            departamentoRepository.findByNombre("Apurímac")
                                    .orElseThrow(() -> new RuntimeException("Departamento Apurímac no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Aymaraes",
                            "Provincia de Aymaraes",
                            true,
                            departamentoRepository.findByNombre("Apurímac")
                                    .orElseThrow(() -> new RuntimeException("Departamento Apurímac no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Cotabambas",
                            "Provincia de Cotabambas",
                            true,
                            departamentoRepository.findByNombre("Apurímac")
                                    .orElseThrow(() -> new RuntimeException("Departamento Apurímac no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Chincheros",
                            "Provincia de Chincheros",
                            true,
                            departamentoRepository.findByNombre("Apurímac")
                                    .orElseThrow(() -> new RuntimeException("Departamento Apurímac no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Grau",
                            "Provincia de Grau",
                            true,
                            departamentoRepository.findByNombre("Apurímac")
                                    .orElseThrow(() -> new RuntimeException("Departamento Apurímac no encontrado"))
                    ),

                    // Arequipa
                    new Provincia(
                            null,
                            "Arequipa",
                            "Provincia de Arequipa",
                            true,
                            departamentoRepository.findByNombre("Arequipa")
                                    .orElseThrow(() -> new RuntimeException("Departamento Arequipa no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Camaná",
                            "Provincia de Camaná",
                            true,
                            departamentoRepository.findByNombre("Arequipa")
                                    .orElseThrow(() -> new RuntimeException("Departamento Arequipa no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Caravelí",
                            "Provincia de Caravelí",
                            true,
                            departamentoRepository.findByNombre("Arequipa")
                                    .orElseThrow(() -> new RuntimeException("Departamento Arequipa no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Castilla",
                            "Provincia de Castilla",
                            true,
                            departamentoRepository.findByNombre("Arequipa")
                                    .orElseThrow(() -> new RuntimeException("Departamento Arequipa no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Caylloma",
                            "Provincia de Caylloma",
                            true,
                            departamentoRepository.findByNombre("Arequipa")
                                    .orElseThrow(() -> new RuntimeException("Departamento Arequipa no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Condesuyos",
                            "Provincia de Condesuyos",
                            true,
                            departamentoRepository.findByNombre("Arequipa")
                                    .orElseThrow(() -> new RuntimeException("Departamento Arequipa no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Islay",
                            "Provincia de Islay",
                            true,
                            departamentoRepository.findByNombre("Arequipa")
                                    .orElseThrow(() -> new RuntimeException("Departamento Arequipa no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "La Unión",
                            "Provincia de La Unión",
                            true,
                            departamentoRepository.findByNombre("Arequipa")
                                    .orElseThrow(() -> new RuntimeException("Departamento Arequipa no encontrado"))
                    ),

                    // Ayacucho
                    new Provincia(
                            null,
                            "Huamanga",
                            "Provincia de Huamanga",
                            true,
                            departamentoRepository.findByNombre("Ayacucho")
                                    .orElseThrow(() -> new RuntimeException("Departamento Ayacucho no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Cangallo",
                            "Provincia de Cangallo",
                            true,
                            departamentoRepository.findByNombre("Ayacucho")
                                    .orElseThrow(() -> new RuntimeException("Departamento Ayacucho no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Huanca Sancos",
                            "Provincia de Huanca Sancos",
                            true,
                            departamentoRepository.findByNombre("Ayacucho")
                                    .orElseThrow(() -> new RuntimeException("Departamento Ayacucho no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Huanta",
                            "Provincia de Huanta",
                            true,
                            departamentoRepository.findByNombre("Ayacucho")
                                    .orElseThrow(() -> new RuntimeException("Departamento Ayacucho no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "La Mar",
                            "Provincia de La Mar",
                            true,
                            departamentoRepository.findByNombre("Ayacucho")
                                    .orElseThrow(() -> new RuntimeException("Departamento Ayacucho no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Lucanas",
                            "Provincia de Lucanas",
                            true,
                            departamentoRepository.findByNombre("Ayacucho")
                                    .orElseThrow(() -> new RuntimeException("Departamento Ayacucho no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Parinacochas",
                            "Provincia de Parinacochas",
                            true,
                            departamentoRepository.findByNombre("Ayacucho")
                                    .orElseThrow(() -> new RuntimeException("Departamento Ayacucho no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Páucar del Sara Sara",
                            "Provincia de Páucar del Sara Sara",
                            true,
                            departamentoRepository.findByNombre("Ayacucho")
                                    .orElseThrow(() -> new RuntimeException("Departamento Ayacucho no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Sucre",
                            "Provincia de Sucre",
                            true,
                            departamentoRepository.findByNombre("Ayacucho")
                                    .orElseThrow(() -> new RuntimeException("Departamento Ayacucho no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Víctor Fajardo",
                            "Provincia de Víctor Fajardo",
                            true,
                            departamentoRepository.findByNombre("Ayacucho")
                                    .orElseThrow(() -> new RuntimeException("Departamento Ayacucho no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Vilcas Huamán",
                            "Provincia de Vilcas Huamán",
                            true,
                            departamentoRepository.findByNombre("Ayacucho")
                                    .orElseThrow(() -> new RuntimeException("Departamento Ayacucho no encontrado"))
                    ),

                    // Cajamarca
                    new Provincia(
                            null,
                            "Cajamarca",
                            "Provincia de Cajamarca",
                            true,
                            departamentoRepository.findByNombre("Cajamarca")
                                    .orElseThrow(() -> new RuntimeException("Departamento Cajamarca no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Cajabamba",
                            "Provincia de Cajabamba",
                            true,
                            departamentoRepository.findByNombre("Cajamarca")
                                    .orElseThrow(() -> new RuntimeException("Departamento Cajamarca no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Celendín",
                            "Provincia de Celendín",
                            true,
                            departamentoRepository.findByNombre("Cajamarca")
                                    .orElseThrow(() -> new RuntimeException("Departamento Cajamarca no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Chota",
                            "Provincia de Chota",
                            true,
                            departamentoRepository.findByNombre("Cajamarca")
                                    .orElseThrow(() -> new RuntimeException("Departamento Cajamarca no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Contumazá",
                            "Provincia de Contumazá",
                            true,
                            departamentoRepository.findByNombre("Cajamarca")
                                    .orElseThrow(() -> new RuntimeException("Departamento Cajamarca no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Cutervo",
                            "Provincia de Cutervo",
                            true,
                            departamentoRepository.findByNombre("Cajamarca")
                                    .orElseThrow(() -> new RuntimeException("Departamento Cajamarca no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Hualgayoc",
                            "Provincia de Hualgayoc",
                            true,
                            departamentoRepository.findByNombre("Cajamarca")
                                    .orElseThrow(() -> new RuntimeException("Departamento Cajamarca no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Jaén",
                            "Provincia de Jaén",
                            true,
                            departamentoRepository.findByNombre("Cajamarca")
                                    .orElseThrow(() -> new RuntimeException("Departamento Cajamarca no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "San Ignacio",
                            "Provincia de San Ignacio",
                            true,
                            departamentoRepository.findByNombre("Cajamarca")
                                    .orElseThrow(() -> new RuntimeException("Departamento Cajamarca no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "San Marcos",
                            "Provincia de San Marcos",
                            true,
                            departamentoRepository.findByNombre("Cajamarca")
                                    .orElseThrow(() -> new RuntimeException("Departamento Cajamarca no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "San Miguel",
                            "Provincia de San Miguel",
                            true,
                            departamentoRepository.findByNombre("Cajamarca")
                                    .orElseThrow(() -> new RuntimeException("Departamento Cajamarca no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "San Pablo",
                            "Provincia de San Pablo",
                            true,
                            departamentoRepository.findByNombre("Cajamarca")
                                    .orElseThrow(() -> new RuntimeException("Departamento Cajamarca no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Santa Cruz",
                            "Provincia de Santa Cruz",
                            true,
                            departamentoRepository.findByNombre("Cajamarca")
                                    .orElseThrow(() -> new RuntimeException("Departamento Cajamarca no encontrado"))
                    ),

                    // Callao
                    new Provincia(
                            null,
                            "Callao",
                            "Provincia Constitucional del Callao",
                            true,
                            departamentoRepository.findByNombre("Callao")
                                    .orElseThrow(() -> new RuntimeException("Departamento Callao no encontrado"))
                    ),

                    // Cusco
                    new Provincia(
                            null,
                            "Cusco",
                            "Provincia de Cusco",
                            true,
                            departamentoRepository.findByNombre("Cusco")
                                    .orElseThrow(() -> new RuntimeException("Departamento Cusco no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Acomayo",
                            "Provincia de Acomayo",
                            true,
                            departamentoRepository.findByNombre("Cusco")
                                    .orElseThrow(() -> new RuntimeException("Departamento Cusco no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Anta",
                            "Provincia de Anta",
                            true,
                            departamentoRepository.findByNombre("Cusco")
                                    .orElseThrow(() -> new RuntimeException("Departamento Cusco no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Calca",
                            "Provincia de Calca",
                            true,
                            departamentoRepository.findByNombre("Cusco")
                                    .orElseThrow(() -> new RuntimeException("Departamento Cusco no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Canas",
                            "Provincia de Canas",
                            true,
                            departamentoRepository.findByNombre("Cusco")
                                    .orElseThrow(() -> new RuntimeException("Departamento Cusco no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Canchis",
                            "Provincia de Canchis",
                            true,
                            departamentoRepository.findByNombre("Cusco")
                                    .orElseThrow(() -> new RuntimeException("Departamento Cusco no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Chumbivilcas",
                            "Provincia de Chumbivilcas",
                            true,
                            departamentoRepository.findByNombre("Cusco")
                                    .orElseThrow(() -> new RuntimeException("Departamento Cusco no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Espinar",
                            "Provincia de Espinar",
                            true,
                            departamentoRepository.findByNombre("Cusco")
                                    .orElseThrow(() -> new RuntimeException("Departamento Cusco no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "La Convención",
                            "Provincia de La Convención",
                            true,
                            departamentoRepository.findByNombre("Cusco")
                                    .orElseThrow(() -> new RuntimeException("Departamento Cusco no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Paruro",
                            "Provincia de Paruro",
                            true,
                            departamentoRepository.findByNombre("Cusco")
                                    .orElseThrow(() -> new RuntimeException("Departamento Cusco no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Paucartambo",
                            "Provincia de Paucartambo",
                            true,
                            departamentoRepository.findByNombre("Cusco")
                                    .orElseThrow(() -> new RuntimeException("Departamento Cusco no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Quispicanchi",
                            "Provincia de Quispicanchi",
                            true,
                            departamentoRepository.findByNombre("Cusco")
                                    .orElseThrow(() -> new RuntimeException("Departamento Cusco no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Urubamba",
                            "Provincia de Urubamba",
                            true,
                            departamentoRepository.findByNombre("Cusco")
                                    .orElseThrow(() -> new RuntimeException("Departamento Cusco no encontrado"))
                    ),

                    // Huancavelica
                    new Provincia(
                            null,
                            "Huancavelica",
                            "Provincia de Huancavelica",
                            true,
                            departamentoRepository.findByNombre("Huancavelica")
                                    .orElseThrow(() -> new RuntimeException("Departamento Huancavelica no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Acobamba",
                            "Provincia de Acobamba",
                            true,
                            departamentoRepository.findByNombre("Huancavelica")
                                    .orElseThrow(() -> new RuntimeException("Departamento Huancavelica no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Angaraes",
                            "Provincia de Angaraes",
                            true,
                            departamentoRepository.findByNombre("Huancavelica")
                                    .orElseThrow(() -> new RuntimeException("Departamento Huancavelica no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Castrovirreyna",
                            "Provincia de Castrovirreyna",
                            true,
                            departamentoRepository.findByNombre("Huancavelica")
                                    .orElseThrow(() -> new RuntimeException("Departamento Huancavelica no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Churcampa",
                            "Provincia de Churcampa",
                            true,
                            departamentoRepository.findByNombre("Huancavelica")
                                    .orElseThrow(() -> new RuntimeException("Departamento Huancavelica no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Huaytará",
                            "Provincia de Huaytará",
                            true,
                            departamentoRepository.findByNombre("Huancavelica")
                                    .orElseThrow(() -> new RuntimeException("Departamento Huancavelica no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Tayacaja",
                            "Provincia de Tayacaja",
                            true,
                            departamentoRepository.findByNombre("Huancavelica")
                                    .orElseThrow(() -> new RuntimeException("Departamento Huancavelica no encontrado"))
                    ),

                    // Huánuco
                    new Provincia(
                            null,
                            "Huánuco",
                            "Provincia de Huánuco",
                            true,
                            departamentoRepository.findByNombre("Huánuco")
                                    .orElseThrow(() -> new RuntimeException("Departamento Huánuco no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Ambo",
                            "Provincia de Ambo",
                            true,
                            departamentoRepository.findByNombre("Huánuco")
                                    .orElseThrow(() -> new RuntimeException("Departamento Huánuco no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Dos de Mayo",
                            "Provincia de Dos de Mayo",
                            true,
                            departamentoRepository.findByNombre("Huánuco")
                                    .orElseThrow(() -> new RuntimeException("Departamento Huánuco no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Huacaybamba",
                            "Provincia de Huacaybamba",
                            true,
                            departamentoRepository.findByNombre("Huánuco")
                                    .orElseThrow(() -> new RuntimeException("Departamento Huánuco no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Huamalíes",
                            "Provincia de Huamalíes",
                            true,
                            departamentoRepository.findByNombre("Huánuco")
                                    .orElseThrow(() -> new RuntimeException("Departamento Huánuco no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Leoncio Prado",
                            "Provincia de Leoncio Prado",
                            true,
                            departamentoRepository.findByNombre("Huánuco")
                                    .orElseThrow(() -> new RuntimeException("Departamento Huánuco no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Marañón",
                            "Provincia de Marañón",
                            true,
                            departamentoRepository.findByNombre("Huánuco")
                                    .orElseThrow(() -> new RuntimeException("Departamento Huánuco no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Pachitea",
                            "Provincia de Pachitea",
                            true,
                            departamentoRepository.findByNombre("Huánuco")
                                    .orElseThrow(() -> new RuntimeException("Departamento Huánuco no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Puerto Inca",
                            "Provincia de Puerto Inca",
                            true,
                            departamentoRepository.findByNombre("Huánuco")
                                    .orElseThrow(() -> new RuntimeException("Departamento Huánuco no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Lauricocha",
                            "Provincia de Lauricocha",
                            true,
                            departamentoRepository.findByNombre("Huánuco")
                                    .orElseThrow(() -> new RuntimeException("Departamento Huánuco no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Yarowilca",
                            "Provincia de Yarowilca",
                            true,
                            departamentoRepository.findByNombre("Huánuco")
                                    .orElseThrow(() -> new RuntimeException("Departamento Huánuco no encontrado"))
                    ),

                    // Ica
                    new Provincia(
                            null,
                            "Ica",
                            "Provincia de Ica",
                            true,
                            departamentoRepository.findByNombre("Ica")
                                    .orElseThrow(() -> new RuntimeException("Departamento Ica no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Chincha",
                            "Provincia de Chincha",
                            true,
                            departamentoRepository.findByNombre("Ica")
                                    .orElseThrow(() -> new RuntimeException("Departamento Ica no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Nazca",
                            "Provincia de Nazca",
                            true,
                            departamentoRepository.findByNombre("Ica")
                                    .orElseThrow(() -> new RuntimeException("Departamento Ica no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Palpa",
                            "Provincia de Palpa",
                            true,
                            departamentoRepository.findByNombre("Ica")
                                    .orElseThrow(() -> new RuntimeException("Departamento Ica no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Pisco",
                            "Provincia de Pisco",
                            true,
                            departamentoRepository.findByNombre("Ica")
                                    .orElseThrow(() -> new RuntimeException("Departamento Ica no encontrado"))
                    ),

                    // Junín
                    new Provincia(
                            null,
                            "Huancayo",
                            "Provincia de Huancayo",
                            true,
                            departamentoRepository.findByNombre("Junín")
                                    .orElseThrow(() -> new RuntimeException("Departamento Junín no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Concepción",
                            "Provincia de Concepción",
                            true,
                            departamentoRepository.findByNombre("Junín")
                                    .orElseThrow(() -> new RuntimeException("Departamento Junín no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Chanchamayo",
                            "Provincia de Chanchamayo",
                            true,
                            departamentoRepository.findByNombre("Junín")
                                    .orElseThrow(() -> new RuntimeException("Departamento Junín no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Jauja",
                            "Provincia de Jauja",
                            true,
                            departamentoRepository.findByNombre("Junín")
                                    .orElseThrow(() -> new RuntimeException("Departamento Junín no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Junín",
                            "Provincia de Junín",
                            true,
                            departamentoRepository.findByNombre("Junín")
                                    .orElseThrow(() -> new RuntimeException("Departamento Junín no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Satipo",
                            "Provincia de Satipo",
                            true,
                            departamentoRepository.findByNombre("Junín")
                                    .orElseThrow(() -> new RuntimeException("Departamento Junín no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Tarma",
                            "Provincia de Tarma",
                            true,
                            departamentoRepository.findByNombre("Junín")
                                    .orElseThrow(() -> new RuntimeException("Departamento Junín no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Yauli",
                            "Provincia de Yauli",
                            true,
                            departamentoRepository.findByNombre("Junín")
                                    .orElseThrow(() -> new RuntimeException("Departamento Junín no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Chupaca",
                            "Provincia de Chupaca",
                            true,
                            departamentoRepository.findByNombre("Junín")
                                    .orElseThrow(() -> new RuntimeException("Departamento Junín no encontrado"))
                    ),

                    // La Libertad
                    new Provincia(
                            null,
                            "Trujillo",
                            "Provincia de Trujillo",
                            true,
                            departamentoRepository.findByNombre("La Libertad")
                                    .orElseThrow(() -> new RuntimeException("Departamento La Libertad no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Ascope",
                            "Provincia de Ascope",
                            true,
                            departamentoRepository.findByNombre("La Libertad")
                                    .orElseThrow(() -> new RuntimeException("Departamento La Libertad no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Bolívar",
                            "Provincia de Bolívar",
                            true,
                            departamentoRepository.findByNombre("La Libertad")
                                    .orElseThrow(() -> new RuntimeException("Departamento La Libertad no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Chepén",
                            "Provincia de Chepén",
                            true,
                            departamentoRepository.findByNombre("La Libertad")
                                    .orElseThrow(() -> new RuntimeException("Departamento La Libertad no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Julcán",
                            "Provincia de Julcán",
                            true,
                            departamentoRepository.findByNombre("La Libertad")
                                    .orElseThrow(() -> new RuntimeException("Departamento La Libertad no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Otuzco",
                            "Provincia de Otuzco",
                            true,
                            departamentoRepository.findByNombre("La Libertad")
                                    .orElseThrow(() -> new RuntimeException("Departamento La Libertad no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Pacasmayo",
                            "Provincia de Pacasmayo",
                            true,
                            departamentoRepository.findByNombre("La Libertad")
                                    .orElseThrow(() -> new RuntimeException("Departamento La Libertad no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Pataz",
                            "Provincia de Pataz",
                            true,
                            departamentoRepository.findByNombre("La Libertad")
                                    .orElseThrow(() -> new RuntimeException("Departamento La Libertad no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Sánchez Carrión",
                            "Provincia de Sánchez Carrión",
                            true,
                            departamentoRepository.findByNombre("La Libertad")
                                    .orElseThrow(() -> new RuntimeException("Departamento La Libertad no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Santiago de Chuco",
                            "Provincia de Santiago de Chuco",
                            true,
                            departamentoRepository.findByNombre("La Libertad")
                                    .orElseThrow(() -> new RuntimeException("Departamento La Libertad no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Gran Chimú",
                            "Provincia de Gran Chimú",
                            true,
                            departamentoRepository.findByNombre("La Libertad")
                                    .orElseThrow(() -> new RuntimeException("Departamento La Libertad no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Virú",
                            "Provincia de Virú",
                            true,
                            departamentoRepository.findByNombre("La Libertad")
                                    .orElseThrow(() -> new RuntimeException("Departamento La Libertad no encontrado"))
                    ),

                    // Lambayeque
                    new Provincia(
                            null,
                            "Chiclayo",
                            "Provincia de Chiclayo",
                            true,
                            departamentoRepository.findByNombre("Lambayeque")
                                    .orElseThrow(() -> new RuntimeException("Departamento Lambayeque no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Ferreñafe",
                            "Provincia de Ferreñafe",
                            true,
                            departamentoRepository.findByNombre("Lambayeque")
                                    .orElseThrow(() -> new RuntimeException("Departamento Lambayeque no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Lambayeque",
                            "Provincia de Lambayeque",
                            true,
                            departamentoRepository.findByNombre("Lambayeque")
                                    .orElseThrow(() -> new RuntimeException("Departamento Lambayeque no encontrado"))
                    ),

                    // Lima
                    new Provincia(
                            null,
                            "Lima",
                            "Provincia de Lima",
                            true,
                            departamentoRepository.findByNombre("Lima")
                                    .orElseThrow(() -> new RuntimeException("Departamento Lima no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Barranca",
                            "Provincia de Barranca",
                            true,
                            departamentoRepository.findByNombre("Lima")
                                    .orElseThrow(() -> new RuntimeException("Departamento Lima no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Cajatambo",
                            "Provincia de Cajatambo",
                            true,
                            departamentoRepository.findByNombre("Lima")
                                    .orElseThrow(() -> new RuntimeException("Departamento Lima no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Canta",
                            "Provincia de Canta",
                            true,
                            departamentoRepository.findByNombre("Lima")
                                    .orElseThrow(() -> new RuntimeException("Departamento Lima no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Cañete",
                            "Provincia de Cañete",
                            true,
                            departamentoRepository.findByNombre("Lima")
                                    .orElseThrow(() -> new RuntimeException("Departamento Lima no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Huaral",
                            "Provincia de Huaral",
                            true,
                            departamentoRepository.findByNombre("Lima")
                                    .orElseThrow(() -> new RuntimeException("Departamento Lima no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Huarochirí",
                            "Provincia de Huarochirí",
                            true,
                            departamentoRepository.findByNombre("Lima")
                                    .orElseThrow(() -> new RuntimeException("Departamento Lima no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Huaura",
                            "Provincia de Huaura",
                            true,
                            departamentoRepository.findByNombre("Lima")
                                    .orElseThrow(() -> new RuntimeException("Departamento Lima no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Oyón",
                            "Provincia de Oyón",
                            true,
                            departamentoRepository.findByNombre("Lima")
                                    .orElseThrow(() -> new RuntimeException("Departamento Lima no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Yauyos",
                            "Provincia de Yauyos",
                            true,
                            departamentoRepository.findByNombre("Lima")
                                    .orElseThrow(() -> new RuntimeException("Departamento Lima no encontrado"))
                    ),

                    // Loreto
                    new Provincia(
                            null,
                            "Maynas",
                            "Provincia de Maynas",
                            true,
                            departamentoRepository.findByNombre("Loreto")
                                    .orElseThrow(() -> new RuntimeException("Departamento Loreto no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Alto Amazonas",
                            "Provincia de Alto Amazonas",
                            true,
                            departamentoRepository.findByNombre("Loreto")
                                    .orElseThrow(() -> new RuntimeException("Departamento Loreto no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Loreto",
                            "Provincia de Loreto",
                            true,
                            departamentoRepository.findByNombre("Loreto")
                                    .orElseThrow(() -> new RuntimeException("Departamento Loreto no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Mariscal Ramón Castilla",
                            "Provincia de Mariscal Ramón Castilla",
                            true,
                            departamentoRepository.findByNombre("Loreto")
                                    .orElseThrow(() -> new RuntimeException("Departamento Loreto no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Requena",
                            "Provincia de Requena",
                            true,
                            departamentoRepository.findByNombre("Loreto")
                                    .orElseThrow(() -> new RuntimeException("Departamento Loreto no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Ucayali",
                            "Provincia de Ucayali",
                            true,
                            departamentoRepository.findByNombre("Loreto")
                                    .orElseThrow(() -> new RuntimeException("Departamento Loreto no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Datem del Marañón",
                            "Provincia de Datem del Marañón",
                            true,
                            departamentoRepository.findByNombre("Loreto")
                                    .orElseThrow(() -> new RuntimeException("Departamento Loreto no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Putumayo",
                            "Provincia de Putumayo",
                            true,
                            departamentoRepository.findByNombre("Loreto")
                                    .orElseThrow(() -> new RuntimeException("Departamento Loreto no encontrado"))
                    ),

                    // Madre de Dios
                    new Provincia(
                            null,
                            "Tambopata",
                            "Provincia de Tambopata",
                            true,
                            departamentoRepository.findByNombre("Madre de Dios")
                                    .orElseThrow(() -> new RuntimeException("Departamento Madre de Dios no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Manu",
                            "Provincia de Manu",
                            true,
                            departamentoRepository.findByNombre("Madre de Dios")
                                    .orElseThrow(() -> new RuntimeException("Departamento Madre de Dios no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Tahuamanu",
                            "Provincia de Tahuamanu",
                            true,
                            departamentoRepository.findByNombre("Madre de Dios")
                                    .orElseThrow(() -> new RuntimeException("Departamento Madre de Dios no encontrado"))
                    ),

                    // Moquegua
                    new Provincia(
                            null,
                            "Mariscal Nieto",
                            "Provincia de Mariscal Nieto",
                            true,
                            departamentoRepository.findByNombre("Moquegua")
                                    .orElseThrow(() -> new RuntimeException("Departamento Moquegua no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "General Sánchez Cerro",
                            "Provincia de General Sánchez Cerro",
                            true,
                            departamentoRepository.findByNombre("Moquegua")
                                    .orElseThrow(() -> new RuntimeException("Departamento Moquegua no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Ilo",
                            "Provincia de Ilo",
                            true,
                            departamentoRepository.findByNombre("Moquegua")
                                    .orElseThrow(() -> new RuntimeException("Departamento Moquegua no encontrado"))
                    ),

                    // Pasco
                    new Provincia(
                            null,
                            "Pasco",
                            "Provincia de Pasco",
                            true,
                            departamentoRepository.findByNombre("Pasco")
                                    .orElseThrow(() -> new RuntimeException("Departamento Pasco no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Daniel Alcides Carrión",
                            "Provincia de Daniel Alcides Carrión",
                            true,
                            departamentoRepository.findByNombre("Pasco")
                                    .orElseThrow(() -> new RuntimeException("Departamento Pasco no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Oxapampa",
                            "Provincia de Oxapampa",
                            true,
                            departamentoRepository.findByNombre("Pasco")
                                    .orElseThrow(() -> new RuntimeException("Departamento Pasco no encontrado"))
                    ),

                    // Piura
                    new Provincia(
                            null,
                            "Piura",
                            "Provincia de Piura",
                            true,
                            departamentoRepository.findByNombre("Piura")
                                    .orElseThrow(() -> new RuntimeException("Departamento Piura no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Ayabaca",
                            "Provincia de Ayabaca",
                            true,
                            departamentoRepository.findByNombre("Piura")
                                    .orElseThrow(() -> new RuntimeException("Departamento Piura no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Huancabamba",
                            "Provincia de Huancabamba",
                            true,
                            departamentoRepository.findByNombre("Piura")
                                    .orElseThrow(() -> new RuntimeException("Departamento Piura no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Morropón",
                            "Provincia de Morropón",
                            true,
                            departamentoRepository.findByNombre("Piura")
                                    .orElseThrow(() -> new RuntimeException("Departamento Piura no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Paita",
                            "Provincia de Paita",
                            true,
                            departamentoRepository.findByNombre("Piura")
                                    .orElseThrow(() -> new RuntimeException("Departamento Piura no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Sullana",
                            "Provincia de Sullana",
                            true,
                            departamentoRepository.findByNombre("Piura")
                                    .orElseThrow(() -> new RuntimeException("Departamento Piura no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Talara",
                            "Provincia de Talara",
                            true,
                            departamentoRepository.findByNombre("Piura")
                                    .orElseThrow(() -> new RuntimeException("Departamento Piura no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Sechura",
                            "Provincia de Sechura",
                            true,
                            departamentoRepository.findByNombre("Piura")
                                    .orElseThrow(() -> new RuntimeException("Departamento Piura no encontrado"))
                    ),

                    // Puno
                    new Provincia(
                            null,
                            "Puno",
                            "Provincia de Puno",
                            true,
                            departamentoRepository.findByNombre("Puno")
                                    .orElseThrow(() -> new RuntimeException("Departamento Puno no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Azángaro",
                            "Provincia de Azángaro",
                            true,
                            departamentoRepository.findByNombre("Puno")
                                    .orElseThrow(() -> new RuntimeException("Departamento Puno no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Carabaya",
                            "Provincia de Carabaya",
                            true,
                            departamentoRepository.findByNombre("Puno")
                                    .orElseThrow(() -> new RuntimeException("Departamento Puno no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Chucuito",
                            "Provincia de Chucuito",
                            true,
                            departamentoRepository.findByNombre("Puno")
                                    .orElseThrow(() -> new RuntimeException("Departamento Puno no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "El Collao",
                            "Provincia de El Collao",
                            true,
                            departamentoRepository.findByNombre("Puno")
                                    .orElseThrow(() -> new RuntimeException("Departamento Puno no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Huancané",
                            "Provincia de Huancané",
                            true,
                            departamentoRepository.findByNombre("Puno")
                                    .orElseThrow(() -> new RuntimeException("Departamento Puno no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Lampa",
                            "Provincia de Lampa",
                            true,
                            departamentoRepository.findByNombre("Puno")
                                    .orElseThrow(() -> new RuntimeException("Departamento Puno no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Melgar",
                            "Provincia de Melgar",
                            true,
                            departamentoRepository.findByNombre("Puno")
                                    .orElseThrow(() -> new RuntimeException("Departamento Puno no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Moho",
                            "Provincia de Moho",
                            true,
                            departamentoRepository.findByNombre("Puno")
                                    .orElseThrow(() -> new RuntimeException("Departamento Puno no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "San Antonio de Putina",
                            "Provincia de San Antonio de Putina",
                            true,
                            departamentoRepository.findByNombre("Puno")
                                    .orElseThrow(() -> new RuntimeException("Departamento Puno no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "San Román",
                            "Provincia de San Román",
                            true,
                            departamentoRepository.findByNombre("Puno")
                                    .orElseThrow(() -> new RuntimeException("Departamento Puno no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Sandia",
                            "Provincia de Sandia",
                            true,
                            departamentoRepository.findByNombre("Puno")
                                    .orElseThrow(() -> new RuntimeException("Departamento Puno no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Yunguyo",
                            "Provincia de Yunguyo",
                            true,
                            departamentoRepository.findByNombre("Puno")
                                    .orElseThrow(() -> new RuntimeException("Departamento Puno no encontrado"))
                    ),

                    // San Martín
                    new Provincia(
                            null,
                            "Moyobamba",
                            "Provincia de Moyobamba",
                            true,
                            departamentoRepository.findByNombre("San Martín")
                                    .orElseThrow(() -> new RuntimeException("Departamento San Martín no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Bellavista",
                            "Provincia de Bellavista",
                            true,
                            departamentoRepository.findByNombre("San Martín")
                                    .orElseThrow(() -> new RuntimeException("Departamento San Martín no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "El Dorado",
                            "Provincia de El Dorado",
                            true,
                            departamentoRepository.findByNombre("San Martín")
                                    .orElseThrow(() -> new RuntimeException("Departamento San Martín no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Huallaga",
                            "Provincia de Huallaga",
                            true,
                            departamentoRepository.findByNombre("San Martín")
                                    .orElseThrow(() -> new RuntimeException("Departamento San Martín no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Lamas",
                            "Provincia de Lamas",
                            true,
                            departamentoRepository.findByNombre("San Martín")
                                    .orElseThrow(() -> new RuntimeException("Departamento San Martín no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Mariscal Cáceres",
                            "Provincia de Mariscal Cáceres",
                            true,
                            departamentoRepository.findByNombre("San Martín")
                                    .orElseThrow(() -> new RuntimeException("Departamento San Martín no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Picota",
                            "Provincia de Picota",
                            true,
                            departamentoRepository.findByNombre("San Martín")
                                    .orElseThrow(() -> new RuntimeException("Departamento San Martín no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Rioja",
                            "Provincia de Rioja",
                            true,
                            departamentoRepository.findByNombre("San Martín")
                                    .orElseThrow(() -> new RuntimeException("Departamento San Martín no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "San Martín",
                            "Provincia de San Martín",
                            true,
                            departamentoRepository.findByNombre("San Martín")
                                    .orElseThrow(() -> new RuntimeException("Departamento San Martín no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Tocache",
                            "Provincia de Tocache",
                            true,
                            departamentoRepository.findByNombre("San Martín")
                                    .orElseThrow(() -> new RuntimeException("Departamento San Martín no encontrado"))
                    ),

                    // Tacna
                    new Provincia(
                            null,
                            "Tacna",
                            "Provincia de Tacna",
                            true,
                            departamentoRepository.findByNombre("Tacna")
                                    .orElseThrow(() -> new RuntimeException("Departamento Tacna no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Candarave",
                            "Provincia de Candarave",
                            true,
                            departamentoRepository.findByNombre("Tacna")
                                    .orElseThrow(() -> new RuntimeException("Departamento Tacna no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Jorge Basadre",
                            "Provincia de Jorge Basadre",
                            true,
                            departamentoRepository.findByNombre("Tacna")
                                    .orElseThrow(() -> new RuntimeException("Departamento Tacna no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Tarata",
                            "Provincia de Tarata",
                            true,
                            departamentoRepository.findByNombre("Tacna")
                                    .orElseThrow(() -> new RuntimeException("Departamento Tacna no encontrado"))
                    ),

                    // Tumbes
                    new Provincia(
                            null,
                            "Tumbes",
                            "Provincia de Tumbes",
                            true,
                            departamentoRepository.findByNombre("Tumbes")
                                    .orElseThrow(() -> new RuntimeException("Departamento Tumbes no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Contralmirante Villar",
                            "Provincia de Contralmirante Villar",
                            true,
                            departamentoRepository.findByNombre("Tumbes")
                                    .orElseThrow(() -> new RuntimeException("Departamento Tumbes no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Zarumilla",
                            "Provincia de Zarumilla",
                            true,
                            departamentoRepository.findByNombre("Tumbes")
                                    .orElseThrow(() -> new RuntimeException("Departamento Tumbes no encontrado"))
                    ),

                    // Ucayali
                    new Provincia(
                            null,
                            "Coronel Portillo",
                            "Provincia de Coronel Portillo",
                            true,
                            departamentoRepository.findByNombre("Ucayali")
                                    .orElseThrow(() -> new RuntimeException("Departamento Ucayali no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Atalaya",
                            "Provincia de Atalaya",
                            true,
                            departamentoRepository.findByNombre("Ucayali")
                                    .orElseThrow(() -> new RuntimeException("Departamento Ucayali no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Padre Abad",
                            "Provincia de Padre Abad",
                            true,
                            departamentoRepository.findByNombre("Ucayali")
                                    .orElseThrow(() -> new RuntimeException("Departamento Ucayali no encontrado"))
                    ),
                    new Provincia(
                            null,
                            "Purús",
                            "Provincia de Purús",
                            true,
                            departamentoRepository.findByNombre("Ucayali")
                                    .orElseThrow(() -> new RuntimeException("Departamento Ucayali no encontrado"))
                    )
            );
            provinciaRepository.saveAll(provincias);
        }
    }

    private void seedDistritosAmazonas() {
        List<Distrito> distritos = Arrays.asList(
                // Amazonas
                // [Provincia de Chachapoyas]
                new Distrito(null, "Chachapoyas", "Distrito de Chachapoyas", true, provinciaRepository.findByNombre("Chachapoyas").orElseThrow(() -> new RuntimeException("Provincia Chachapoyas no encontrada"))),
                new Distrito(null, "Asunción", "Distrito de Asunción", true, provinciaRepository.findByNombre("Chachapoyas").orElseThrow(() -> new RuntimeException("Provincia Chachapoyas no encontrada"))),
                new Distrito(null, "Balsas", "Distrito de Balsas", true, provinciaRepository.findByNombre("Chachapoyas").orElseThrow(() -> new RuntimeException("Provincia Chachapoyas no encontrada"))),
                new Distrito(null, "Cheto", "Distrito de Cheto", true, provinciaRepository.findByNombre("Chachapoyas").orElseThrow(() -> new RuntimeException("Provincia Chachapoyas no encontrada"))),
                new Distrito(null, "Chiliquín", "Distrito de Chiliquín", true, provinciaRepository.findByNombre("Chachapoyas").orElseThrow(() -> new RuntimeException("Provincia Chachapoyas no encontrada"))),
                new Distrito(null, "Chuquibamba", "Distrito de Chuquibamba", true, provinciaRepository.findByNombre("Chachapoyas").orElseThrow(() -> new RuntimeException("Provincia Condesuyos no encontrada"))),
                new Distrito(null, "Granada", "Distrito de Granada", true, provinciaRepository.findByNombre("Chachapoyas").orElseThrow(() -> new RuntimeException("Provincia Chachapoyas no encontrada"))),
                new Distrito(null, "Huancas", "Distrito de Huancas", true, provinciaRepository.findByNombre("Chachapoyas").orElseThrow(() -> new RuntimeException("Provincia Chachapoyas no encontrada"))),
                new Distrito(null, "La Jalca", "Distrito de La Jalca", true, provinciaRepository.findByNombre("Chachapoyas").orElseThrow(() -> new RuntimeException("Provincia Chachapoyas no encontrada"))),
                new Distrito(null, "Leimebamba", "Distrito de Leimebamba", true, provinciaRepository.findByNombre("Chachapoyas").orElseThrow(() -> new RuntimeException("Provincia Chachapoyas no encontrada"))),
                new Distrito(null, "Levanto", "Distrito de Levanto", true, provinciaRepository.findByNombre("Chachapoyas").orElseThrow(() -> new RuntimeException("Provincia Chachapoyas no encontrada"))),
                new Distrito(null, "Magdalena", "Distrito de Magdalena", true, provinciaRepository.findByNombre("Chachapoyas").orElseThrow(() -> new RuntimeException("Provincia Chachapoyas no encontrada"))),
                new Distrito(null, "Mariscal Castilla", "Distrito de Mariscal Castilla", true, provinciaRepository.findByNombre("Chachapoyas").orElseThrow(() -> new RuntimeException("Provincia Chachapoyas no encontrada"))),
                new Distrito(null, "Molinopampa", "Distrito de Molinopampa", true, provinciaRepository.findByNombre("Chachapoyas").orElseThrow(() -> new RuntimeException("Provincia Chachapoyas no encontrada"))),
                new Distrito(null, "Montevideo", "Distrito de Montevideo", true, provinciaRepository.findByNombre("Chachapoyas").orElseThrow(() -> new RuntimeException("Provincia Chachapoyas no encontrada"))),
                new Distrito(null, "Olleros", "Distrito de Olleros", true, provinciaRepository.findByNombre("Chachapoyas").orElseThrow(() -> new RuntimeException("Provincia Chachapoyas no encontrada"))),
                new Distrito(null, "Quinjalca", "Distrito de Quinjalca", true, provinciaRepository.findByNombre("Chachapoyas").orElseThrow(() -> new RuntimeException("Provincia Chachapoyas no encontrada"))),
                new Distrito(null, "San Francisco de Daguas", "Distrito de San Francisco de Daguas", true, provinciaRepository.findByNombre("Chachapoyas").orElseThrow(() -> new RuntimeException("Provincia Chachapoyas no encontrada"))),
                new Distrito(null, "San Isidro de Maino", "Distrito de San Isidro de Maino", true, provinciaRepository.findByNombre("Chachapoyas").orElseThrow(() -> new RuntimeException("Provincia Chachapoyas no encontrada"))),
                new Distrito(null, "Soloco", "Distrito de Soloco", true, provinciaRepository.findByNombre("Chachapoyas").orElseThrow(() -> new RuntimeException("Provincia Chachapoyas no encontrada"))),
                new Distrito(null, "Sonche", "Distrito de Sonche", true, provinciaRepository.findByNombre("Chachapoyas").orElseThrow(() -> new RuntimeException("Provincia Chachapoyas no encontrada"))),

                // [Provincia de Bagua]
                new Distrito(null, "Bagua", "Distrito de Bagua", true, provinciaRepository.findByNombre("Bagua").orElseThrow(() -> new RuntimeException("Provincia Bagua no encontrada"))),
                new Distrito(null, "Aramango", "Distrito de Aramango", true, provinciaRepository.findByNombre("Bagua").orElseThrow(() -> new RuntimeException("Provincia Bagua no encontrada"))),
                new Distrito(null, "Copallín", "Distrito de Copallín", true, provinciaRepository.findByNombre("Bagua").orElseThrow(() -> new RuntimeException("Provincia Bagua no encontrada"))),
                new Distrito(null, "El Parco", "Distrito de El Parco", true, provinciaRepository.findByNombre("Bagua").orElseThrow(() -> new RuntimeException("Provincia Bagua no encontrada"))),
                new Distrito(null, "Imaza", "Distrito de Imaza", true, provinciaRepository.findByNombre("Bagua").orElseThrow(() -> new RuntimeException("Provincia Bagua no encontrada"))),
                new Distrito(null, "La Peca", "Distrito de La Peca", true, provinciaRepository.findByNombre("Bagua").orElseThrow(() -> new RuntimeException("Provincia Bagua no encontrada"))),

                // [Provincia de Bongará]
                new Distrito(null, "Jumbilla", "Distrito de Jumbilla", true, provinciaRepository.findByNombre("Bongará").orElseThrow(() -> new RuntimeException("Provincia Bongará no encontrada"))),
                new Distrito(null, "Chisquilla", "Distrito de Chisquilla", true, provinciaRepository.findByNombre("Bongará").orElseThrow(() -> new RuntimeException("Provincia Bongará no encontrada"))),
                new Distrito(null, "Churuja", "Distrito de Churuja", true, provinciaRepository.findByNombre("Bongará").orElseThrow(() -> new RuntimeException("Provincia Bongará no encontrada"))),
                new Distrito(null, "Corosha", "Distrito de Corosha", true, provinciaRepository.findByNombre("Bongará").orElseThrow(() -> new RuntimeException("Provincia Bongará no encontrada"))),
                new Distrito(null, "Cuispes", "Distrito de Cuispes", true, provinciaRepository.findByNombre("Bongará").orElseThrow(() -> new RuntimeException("Provincia Bongará no encontrada"))),
                new Distrito(null, "Florida", "Distrito de Florida", true, provinciaRepository.findByNombre("Bongará").orElseThrow(() -> new RuntimeException("Provincia Bongará no encontrada"))),
                new Distrito(null, "Jazan", "Distrito de Jazan", true, provinciaRepository.findByNombre("Bongará").orElseThrow(() -> new RuntimeException("Provincia Bongará no encontrada"))),
                new Distrito(null, "Recta", "Distrito de Recta", true, provinciaRepository.findByNombre("Bongará").orElseThrow(() -> new RuntimeException("Provincia Bongará no encontrada"))),
                new Distrito(null, "San Carlos", "Distrito de San Carlos", true, provinciaRepository.findByNombre("Bongará").orElseThrow(() -> new RuntimeException("Provincia Bongará no encontrada"))),
                new Distrito(null, "Shipasbamba", "Distrito de Shipasbamba", true, provinciaRepository.findByNombre("Bongará").orElseThrow(() -> new RuntimeException("Provincia Bongará no encontrada"))),
                new Distrito(null, "Valera", "Distrito de Valera", true, provinciaRepository.findByNombre("Bongará").orElseThrow(() -> new RuntimeException("Provincia Bongará no encontrada"))),
                new Distrito(null, "Yambrasbamba", "Distrito de Yambrasbamba", true, provinciaRepository.findByNombre("Bongará").orElseThrow(() -> new RuntimeException("Provincia Bongará no encontrada"))),

                // [Provincia de Condorcanqui]
                new Distrito(null, "Nieva", "Distrito de Nieva", true, provinciaRepository.findByNombre("Condorcanqui").orElseThrow(() -> new RuntimeException("Provincia Condorcanqui no encontrada"))),
                new Distrito(null, "El Cenepa", "Distrito de El Cenepa", true, provinciaRepository.findByNombre("Condorcanqui").orElseThrow(() -> new RuntimeException("Provincia Condorcanqui no encontrada"))),
                new Distrito(null, "Río Santiago", "Distrito de Río Santiago", true, provinciaRepository.findByNombre("Condorcanqui").orElseThrow(() -> new RuntimeException("Provincia Condorcanqui no encontrada"))),

                // [Provincia de Luya]
                new Distrito(null, "Lámud", "Distrito de Lámud", true, provinciaRepository.findByNombre("Luya").orElseThrow(() -> new RuntimeException("Provincia Luya no encontrada"))),
                new Distrito(null, "Camporredondo", "Distrito de Camporredondo", true, provinciaRepository.findByNombre("Luya").orElseThrow(() -> new RuntimeException("Provincia Luya no encontrada"))),
                new Distrito(null, "Cocabamba", "Distrito de Cocabamba", true, provinciaRepository.findByNombre("Luya").orElseThrow(() -> new RuntimeException("Provincia Luya no encontrada"))),
                new Distrito(null, "Colcamar", "Distrito de Colcamar", true, provinciaRepository.findByNombre("Luya").orElseThrow(() -> new RuntimeException("Provincia Luya no encontrada"))),
                new Distrito(null, "Conila", "Distrito de Conila", true, provinciaRepository.findByNombre("Luya").orElseThrow(() -> new RuntimeException("Provincia Luya no encontrada"))),
                new Distrito(null, "Inguilpata", "Distrito de Inguilpata", true, provinciaRepository.findByNombre("Luya").orElseThrow(() -> new RuntimeException("Provincia Luya no encontrada"))),
                new Distrito(null, "Longuita", "Distrito de Longuita", true, provinciaRepository.findByNombre("Luya").orElseThrow(() -> new RuntimeException("Provincia Luya no encontrada"))),
                new Distrito(null, "Lonya Chico", "Distrito de Lonya Chico", true, provinciaRepository.findByNombre("Luya").orElseThrow(() -> new RuntimeException("Provincia Luya no encontrada"))),
                new Distrito(null, "Luya", "Distrito de Luya", true, provinciaRepository.findByNombre("Luya").orElseThrow(() -> new RuntimeException("Provincia Luya no encontrada"))),
                new Distrito(null, "Luya Viejo", "Distrito de Luya Viejo", true, provinciaRepository.findByNombre("Luya").orElseThrow(() -> new RuntimeException("Provincia Luya no encontrada"))),
                new Distrito(null, "María", "Distrito de María", true, provinciaRepository.findByNombre("Luya").orElseThrow(() -> new RuntimeException("Provincia Luya no encontrada"))),
                new Distrito(null, "Ocalli", "Distrito de Ocalli", true, provinciaRepository.findByNombre("Luya").orElseThrow(() -> new RuntimeException("Provincia Luya no encontrada"))),
                new Distrito(null, "Ocumal", "Distrito de Ocumal", true, provinciaRepository.findByNombre("Luya").orElseThrow(() -> new RuntimeException("Provincia Luya no encontrada"))),
                new Distrito(null, "Pisuquia", "Distrito de Pisuquia", true, provinciaRepository.findByNombre("Luya").orElseThrow(() -> new RuntimeException("Provincia Luya no encontrada"))),
                new Distrito(null, "Providencia", "Distrito de Providencia", true, provinciaRepository.findByNombre("Luya").orElseThrow(() -> new RuntimeException("Provincia Luya no encontrada"))),
                new Distrito(null, "San Cristóbal", "Distrito de San Cristóbal", true, provinciaRepository.findByNombre("Luya").orElseThrow(() -> new RuntimeException("Provincia Luya no encontrada"))),
                new Distrito(null, "San Francisco de Yeso", "Distrito de San Francisco de Yeso", true, provinciaRepository.findByNombre("Luya").orElseThrow(() -> new RuntimeException("Provincia Luya no encontrada"))),
                new Distrito(null, "San Jerónimo", "Distrito de San Jerónimo", true, provinciaRepository.findByNombre("Luya").orElseThrow(() -> new RuntimeException("Provincia Luya no encontrada"))),
                new Distrito(null, "San Juan de Lopecancha", "Distrito de San Juan de Lopecancha", true, provinciaRepository.findByNombre("Luya").orElseThrow(() -> new RuntimeException("Provincia Luya no encontrada"))),
                new Distrito(null, "Santa Catalina", "Distrito de Santa Catalina", true, provinciaRepository.findByNombre("Luya").orElseThrow(() -> new RuntimeException("Provincia Luya no encontrada"))),
                new Distrito(null, "Santo Tomás", "Distrito de Santo Tomás", true, provinciaRepository.findByNombre("Luya").orElseThrow(() -> new RuntimeException("Provincia Luya no encontrada"))),
                new Distrito(null, "Tingo", "Distrito de Tingo", true, provinciaRepository.findByNombre("Luya").orElseThrow(() -> new RuntimeException("Provincia Luya no encontrada"))),
                new Distrito(null, "Trita", "Distrito de Trita", true, provinciaRepository.findByNombre("Luya").orElseThrow(() -> new RuntimeException("Provincia Luya no encontrada"))),

                // [Provincia de Rodríguez de Mendoza]
                new Distrito(null, "San Nicolás", "Distrito de San Nicolás", true, provinciaRepository.findByNombre("Rodríguez de Mendoza").orElseThrow(() -> new RuntimeException("Provincia Rodríguez de Mendoza no encontrada"))),
                new Distrito(null, "Chirimoto", "Distrito de Chirimoto", true, provinciaRepository.findByNombre("Rodríguez de Mendoza").orElseThrow(() -> new RuntimeException("Provincia Rodríguez de Mendoza no encontrada"))),
                new Distrito(null, "Cochamal", "Distrito de Cochamal", true, provinciaRepository.findByNombre("Rodríguez de Mendoza").orElseThrow(() -> new RuntimeException("Provincia Rodríguez de Mendoza no encontrada"))),
                new Distrito(null, "Huambo", "Distrito de Huambo", true, provinciaRepository.findByNombre("Rodríguez de Mendoza").orElseThrow(() -> new RuntimeException("Provincia Rodríguez de Mendoza no encontrada"))),
                new Distrito(null, "Limabamba", "Distrito de Limabamba", true, provinciaRepository.findByNombre("Rodríguez de Mendoza").orElseThrow(() -> new RuntimeException("Provincia Rodríguez de Mendoza no encontrada"))),
                new Distrito(null, "Longar", "Distrito de Longar", true, provinciaRepository.findByNombre("Rodríguez de Mendoza").orElseThrow(() -> new RuntimeException("Provincia Rodríguez de Mendoza no encontrada"))),
                new Distrito(null, "Mariscal Benavides", "Distrito de Mariscal Benavides", true, provinciaRepository.findByNombre("Rodríguez de Mendoza").orElseThrow(() -> new RuntimeException("Provincia Rodríguez de Mendoza no encontrada"))),
                new Distrito(null, "Milpuc", "Distrito de Milpuc", true, provinciaRepository.findByNombre("Rodríguez de Mendoza").orElseThrow(() -> new RuntimeException("Provincia Rodríguez de Mendoza no encontrada"))),
                new Distrito(null, "Omia", "Distrito de Omia", true, provinciaRepository.findByNombre("Rodríguez de Mendoza").orElseThrow(() -> new RuntimeException("Provincia Rodríguez de Mendoza no encontrada"))),
                new Distrito(null, "Santa Rosa", "Distrito de Santa Rosa", true, provinciaRepository.findByNombre("Rodríguez de Mendoza").orElseThrow(() -> new RuntimeException("Provincia Rodríguez de Mendoza no encontrada"))),
                new Distrito(null, "Totora", "Distrito de Totora", true, provinciaRepository.findByNombre("Rodríguez de Mendoza").orElseThrow(() -> new RuntimeException("Provincia Rodríguez de Mendoza no encontrada"))),
                new Distrito(null, "Vista Alegre", "Distrito de Vista Alegre", true, provinciaRepository.findByNombre("Rodríguez de Mendoza").orElseThrow(() -> new RuntimeException("Provincia Rodríguez de Mendoza no encontrada"))),

                // [Provincia de Utcubamba]
                new Distrito(null, "Bagua Grande", "Distrito de Bagua Grande", true, provinciaRepository.findByNombre("Utcubamba").orElseThrow(() -> new RuntimeException("Provincia Utcubamba no encontrada"))),
                new Distrito(null, "Cajaruro", "Distrito de Cajaruro", true, provinciaRepository.findByNombre("Utcubamba").orElseThrow(() -> new RuntimeException("Provincia Utcubamba no encontrada"))),
                new Distrito(null, "Cumba", "Distrito de Cumba", true, provinciaRepository.findByNombre("Utcubamba").orElseThrow(() -> new RuntimeException("Provincia Utcubamba no encontrada"))),
                new Distrito(null, "El Milagro", "Distrito de El Milagro", true, provinciaRepository.findByNombre("Utcubamba").orElseThrow(() -> new RuntimeException("Provincia Utcubamba no encontrada"))),
                new Distrito(null, "Jamalca", "Distrito de Jamalca", true, provinciaRepository.findByNombre("Utcubamba").orElseThrow(() -> new RuntimeException("Provincia Utcubamba no encontrada"))),
                new Distrito(null, "Lonya Grande", "Distrito de Lonya Grande", true, provinciaRepository.findByNombre("Utcubamba").orElseThrow(() -> new RuntimeException("Provincia Utcubamba no encontrada"))),
                new Distrito(null, "Yamón", "Distrito de Yamón", true, provinciaRepository.findByNombre("Utcubamba").orElseThrow(() -> new RuntimeException("Provincia Utcubamba no encontrada")))
        );
        distritoRepository.saveAll(distritos);
    }

    private void seedDistritosAncash() {
        List<Distrito> distritos = Arrays.asList(
                // Áncash
                // [Provincia de Huaraz]
                new Distrito(null, "Huaraz", "Distrito de Huaraz", true, provinciaRepository.findByNombre("Huaraz").orElseThrow(() -> new RuntimeException("Provincia Huaraz no encontrada"))),
                new Distrito(null, "Cochabamba", "Distrito de Cochabamba", true, provinciaRepository.findByNombre("Huaraz").orElseThrow(() -> new RuntimeException("Provincia Huaraz no encontrada"))),
                new Distrito(null, "Colcabamba", "Distrito de Colcabamba", true, provinciaRepository.findByNombre("Huaraz").orElseThrow(() -> new RuntimeException("Provincia Huaraz no encontrada"))),
                new Distrito(null, "Huanchay", "Distrito de Huanchay", true, provinciaRepository.findByNombre("Huaraz").orElseThrow(() -> new RuntimeException("Provincia Huaraz no encontrada"))),
                new Distrito(null, "Independencia", "Distrito de Independencia", true, provinciaRepository.findByNombre("Huaraz").orElseThrow(() -> new RuntimeException("Provincia Huaraz no encontrada"))),
                new Distrito(null, "Jangas", "Distrito de Jangas", true, provinciaRepository.findByNombre("Huaraz").orElseThrow(() -> new RuntimeException("Provincia Huaraz no encontrada"))),
                new Distrito(null, "La Libertad", "Distrito de La Libertad", true, provinciaRepository.findByNombre("Huaraz").orElseThrow(() -> new RuntimeException("Provincia Huaraz no encontrada"))),
                new Distrito(null, "Olleros", "Distrito de Olleros", true, provinciaRepository.findByNombre("Huaraz").orElseThrow(() -> new RuntimeException("Provincia Huaraz no encontrada"))),
                new Distrito(null, "Pampas Grande", "Distrito de Pampas Grande", true, provinciaRepository.findByNombre("Huaraz").orElseThrow(() -> new RuntimeException("Provincia Huaraz no encontrada"))),
                new Distrito(null, "Pariacoto", "Distrito de Pariacoto", true, provinciaRepository.findByNombre("Huaraz").orElseThrow(() -> new RuntimeException("Provincia Huaraz no encontrada"))),
                new Distrito(null, "Pira", "Distrito de Pira", true, provinciaRepository.findByNombre("Huaraz").orElseThrow(() -> new RuntimeException("Provincia Huaraz no encontrada"))),
                new Distrito(null, "Tarica", "Distrito de Tarica", true, provinciaRepository.findByNombre("Huaraz").orElseThrow(() -> new RuntimeException("Provincia Huaraz no encontrada"))),

                // [Provincia de Aija]
                new Distrito(null, "Aija", "Distrito de Aija", true, provinciaRepository.findByNombre("Aija").orElseThrow(() -> new RuntimeException("Provincia Aija no encontrada"))),
                new Distrito(null, "Coris", "Distrito de Coris", true, provinciaRepository.findByNombre("Aija").orElseThrow(() -> new RuntimeException("Provincia Aija no encontrada"))),
                new Distrito(null, "Huacllán", "Distrito de Huacllán", true, provinciaRepository.findByNombre("Aija").orElseThrow(() -> new RuntimeException("Provincia Aija no encontrada"))),
                new Distrito(null, "La Merced", "Distrito de La Merced", true, provinciaRepository.findByNombre("Aija").orElseThrow(() -> new RuntimeException("Provincia Aija no encontrada"))),
                new Distrito(null, "Succha", "Distrito de Succha", true, provinciaRepository.findByNombre("Aija").orElseThrow(() -> new RuntimeException("Provincia Aija no encontrada"))),

                // [Provincia de Antonio Raymondi]
                new Distrito(null, "Llamellín", "Distrito de Llamellín", true, provinciaRepository.findByNombre("Antonio Raymondi").orElseThrow(() -> new RuntimeException("Provincia Antonio Raymondi no encontrada"))),
                new Distrito(null, "Aczo", "Distrito de Aczo", true, provinciaRepository.findByNombre("Antonio Raymondi").orElseThrow(() -> new RuntimeException("Provincia Antonio Raymondi no encontrada"))),
                new Distrito(null, "Chaccho", "Distrito de Chaccho", true, provinciaRepository.findByNombre("Antonio Raymondi").orElseThrow(() -> new RuntimeException("Provincia Antonio Raymondi no encontrada"))),
                new Distrito(null, "Chingas", "Distrito de Chingas", true, provinciaRepository.findByNombre("Antonio Raymondi").orElseThrow(() -> new RuntimeException("Provincia Antonio Raymondi no encontrada"))),
                new Distrito(null, "Mirgas", "Distrito de Mirgas", true, provinciaRepository.findByNombre("Antonio Raymondi").orElseThrow(() -> new RuntimeException("Provincia Antonio Raymondi no encontrada"))),
                new Distrito(null, "San Juan de Rontoy", "Distrito de San Juan de Rontoy", true, provinciaRepository.findByNombre("Antonio Raymondi").orElseThrow(() -> new RuntimeException("Provincia Antonio Raymondi no encontrada"))),

                // [Provincia de Asunción]
                new Distrito(null, "Chacas", "Distrito de Chacas", true, provinciaRepository.findByNombre("Asunción").orElseThrow(() -> new RuntimeException("Provincia Asunción no encontrada"))),
                new Distrito(null, "Acochaca", "Distrito de Acochaca", true, provinciaRepository.findByNombre("Asunción").orElseThrow(() -> new RuntimeException("Provincia Asunción no encontrada"))),

                // [Provincia de Bolognesi]
                new Distrito(null, "Chiquián", "Distrito de Chiquián", true, provinciaRepository.findByNombre("Bolognesi").orElseThrow(() -> new RuntimeException("Provincia Bolognesi no encontrada"))),
                new Distrito(null, "Abelardo Pardo Lezameta", "Distrito de Abelardo Pardo Lezameta", true, provinciaRepository.findByNombre("Bolognesi").orElseThrow(() -> new RuntimeException("Provincia Bolognesi no encontrada"))),
                new Distrito(null, "Antonio Raymondi", "Distrito de Antonio Raymondi", true, provinciaRepository.findByNombre("Bolognesi").orElseThrow(() -> new RuntimeException("Provincia Bolognesi no encontrada"))),
                new Distrito(null, "Aquia", "Distrito de Aquia", true, provinciaRepository.findByNombre("Bolognesi").orElseThrow(() -> new RuntimeException("Provincia Bolognesi no encontrada"))),
                new Distrito(null, "Cajacay", "Distrito de Cajacay", true, provinciaRepository.findByNombre("Bolognesi").orElseThrow(() -> new RuntimeException("Provincia Bolognesi no encontrada"))),
                new Distrito(null, "Canis", "Distrito de Canis", true, provinciaRepository.findByNombre("Bolognesi").orElseThrow(() -> new RuntimeException("Provincia Bolognesi no encontrada"))),
                new Distrito(null, "Colquioc", "Distrito de Colquioc", true, provinciaRepository.findByNombre("Bolognesi").orElseThrow(() -> new RuntimeException("Provincia Bolognesi no encontrada"))),
                new Distrito(null, "Huallanca", "Distrito de Huallanca", true, provinciaRepository.findByNombre("Bolognesi").orElseThrow(() -> new RuntimeException("Provincia Bolognesi no encontrada"))),
                new Distrito(null, "Huasta", "Distrito de Huasta", true, provinciaRepository.findByNombre("Bolognesi").orElseThrow(() -> new RuntimeException("Provincia Bolognesi no encontrada"))),
                new Distrito(null, "Huayllacayan", "Distrito de Huayllacayan", true, provinciaRepository.findByNombre("Bolognesi").orElseThrow(() -> new RuntimeException("Provincia Bolognesi no encontrada"))),
                new Distrito(null, "La Primavera", "Distrito de La Primavera", true, provinciaRepository.findByNombre("Bolognesi").orElseThrow(() -> new RuntimeException("Provincia Bolognesi no encontrada"))),
                new Distrito(null, "Mangas", "Distrito de Mangas", true, provinciaRepository.findByNombre("Bolognesi").orElseThrow(() -> new RuntimeException("Provincia Bolognesi no encontrada"))),
                new Distrito(null, "Pacllón", "Distrito de Pacllón", true, provinciaRepository.findByNombre("Bolognesi").orElseThrow(() -> new RuntimeException("Provincia Bolognesi no encontrada"))),
                new Distrito(null, "San Miguel de Corpanqui", "Distrito de San Miguel de Corpanqui", true, provinciaRepository.findByNombre("Bolognesi").orElseThrow(() -> new RuntimeException("Provincia Bolognesi no encontrada"))),
                new Distrito(null, "Ticllos", "Distrito de Ticllos", true, provinciaRepository.findByNombre("Bolognesi").orElseThrow(() -> new RuntimeException("Provincia Bolognesi no encontrada"))),

                // [Provincia de Carhuaz]
                new Distrito(null, "Carhuaz", "Distrito de Carhuaz", true, provinciaRepository.findByNombre("Carhuaz").orElseThrow(() -> new RuntimeException("Provincia Carhuaz no encontrada"))),
                new Distrito(null, "Acopampa", "Distrito de Acopampa", true, provinciaRepository.findByNombre("Carhuaz").orElseThrow(() -> new RuntimeException("Provincia Carhuaz no encontrada"))),
                new Distrito(null, "Amashca", "Distrito de Amashca", true, provinciaRepository.findByNombre("Carhuaz").orElseThrow(() -> new RuntimeException("Provincia Carhuaz no encontrada"))),
                new Distrito(null, "Anta", "Distrito de Anta", true, provinciaRepository.findByNombre("Carhuaz").orElseThrow(() -> new RuntimeException("Provincia Carhuaz no encontrada"))),
                new Distrito(null, "Ataquero", "Distrito de Ataquero", true, provinciaRepository.findByNombre("Carhuaz").orElseThrow(() -> new RuntimeException("Provincia Carhuaz no encontrada"))),
                new Distrito(null, "Marcara", "Distrito de Marcara", true, provinciaRepository.findByNombre("Carhuaz").orElseThrow(() -> new RuntimeException("Provincia Carhuaz no encontrada"))),
                new Distrito(null, "Pariahuanca", "Distrito de Pariahuanca", true, provinciaRepository.findByNombre("Carhuaz").orElseThrow(() -> new RuntimeException("Provincia Carhuaz no encontrada"))),
                new Distrito(null, "San Miguel de Aco", "Distrito de San Miguel de Aco", true, provinciaRepository.findByNombre("Carhuaz").orElseThrow(() -> new RuntimeException("Provincia Carhuaz no encontrada"))),
                new Distrito(null, "Shilla", "Distrito de Shilla", true, provinciaRepository.findByNombre("Carhuaz").orElseThrow(() -> new RuntimeException("Provincia Carhuaz no encontrada"))),
                new Distrito(null, "Tinco", "Distrito de Tinco", true, provinciaRepository.findByNombre("Carhuaz").orElseThrow(() -> new RuntimeException("Provincia Carhuaz no encontrada"))),
                new Distrito(null, "Yungar", "Distrito de Yungar", true, provinciaRepository.findByNombre("Carhuaz").orElseThrow(() -> new RuntimeException("Provincia Carhuaz no encontrada"))),

                // [Provincia de Carlos Fermín Fitzcarrald]
                new Distrito(null, "San Luis", "Distrito de San Luis", true, provinciaRepository.findByNombre("Carlos Fermín Fitzcarrald").orElseThrow(() -> new RuntimeException("Provincia Carlos Fermín Fitzcarrald no encontrada"))),
                new Distrito(null, "San Nicolás", "Distrito de San Nicolás", true, provinciaRepository.findByNombre("Carlos Fermín Fitzcarrald").orElseThrow(() -> new RuntimeException("Provincia Carlos Fermín Fitzcarrald no encontrada"))),
                new Distrito(null, "Yauya", "Distrito de Yauya", true, provinciaRepository.findByNombre("Carlos Fermín Fitzcarrald").orElseThrow(() -> new RuntimeException("Provincia Carlos Fermín Fitzcarrald no encontrada"))),

                // [Provincia de Casma]
                new Distrito(null, "Casma", "Distrito de Casma", true, provinciaRepository.findByNombre("Casma").orElseThrow(() -> new RuntimeException("Provincia Casma no encontrada"))),
                new Distrito(null, "Buena Vista Alta", "Distrito de Buena Vista Alta", true, provinciaRepository.findByNombre("Casma").orElseThrow(() -> new RuntimeException("Provincia Casma no encontrada"))),
                new Distrito(null, "Comandante Noel", "Distrito de Comandante Noel", true, provinciaRepository.findByNombre("Casma").orElseThrow(() -> new RuntimeException("Provincia Casma no encontrada"))),
                new Distrito(null, "Yaután", "Distrito de Yaután", true, provinciaRepository.findByNombre("Casma").orElseThrow(() -> new RuntimeException("Provincia Casma no encontrada"))),

                // [Provincia de Corongo]
                new Distrito(null, "Corongo", "Distrito de Corongo", true, provinciaRepository.findByNombre("Corongo").orElseThrow(() -> new RuntimeException("Provincia Corongo no encontrada"))),
                new Distrito(null, "Aco", "Distrito de Aco", true, provinciaRepository.findByNombre("Corongo").orElseThrow(() -> new RuntimeException("Provincia Corongo no encontrada"))),
                new Distrito(null, "Bambas", "Distrito de Bambas", true, provinciaRepository.findByNombre("Corongo").orElseThrow(() -> new RuntimeException("Provincia Corongo no encontrada"))),
                new Distrito(null, "Cusca", "Distrito de Cusca", true, provinciaRepository.findByNombre("Corongo").orElseThrow(() -> new RuntimeException("Provincia Corongo no encontrada"))),
                new Distrito(null, "La Pampa", "Distrito de La Pampa", true, provinciaRepository.findByNombre("Corongo").orElseThrow(() -> new RuntimeException("Provincia Corongo no encontrada"))),
                new Distrito(null, "Yanac", "Distrito de Yanac", true, provinciaRepository.findByNombre("Corongo").orElseThrow(() -> new RuntimeException("Provincia Corongo no encontrada"))),
                new Distrito(null, "Yupan", "Distrito de Yupan", true, provinciaRepository.findByNombre("Corongo").orElseThrow(() -> new RuntimeException("Provincia Corongo no encontrada"))),

                // [Provincia de Huari]
                new Distrito(null, "Huari", "Distrito de Huari", true, provinciaRepository.findByNombre("Huari").orElseThrow(() -> new RuntimeException("Provincia Huari no encontrada"))),
                new Distrito(null, "Anra", "Distrito de Anra", true, provinciaRepository.findByNombre("Huari").orElseThrow(() -> new RuntimeException("Provincia Huari no encontrada"))),
                new Distrito(null, "Cajay", "Distrito de Cajay", true, provinciaRepository.findByNombre("Huari").orElseThrow(() -> new RuntimeException("Provincia Huari no encontrada"))),
                new Distrito(null, "Chavín de Huantar", "Distrito de Chavín de Huantar", true, provinciaRepository.findByNombre("Huari").orElseThrow(() -> new RuntimeException("Provincia Huari no encontrada"))),
                new Distrito(null, "Huacachi", "Distrito de Huacachi", true, provinciaRepository.findByNombre("Huari").orElseThrow(() -> new RuntimeException("Provincia Huari no encontrada"))),
                new Distrito(null, "Huacchis", "Distrito de Huacchis", true, provinciaRepository.findByNombre("Huari").orElseThrow(() -> new RuntimeException("Provincia Huari no encontrada"))),
                new Distrito(null, "Huachis", "Distrito de Huachis", true, provinciaRepository.findByNombre("Huari").orElseThrow(() -> new RuntimeException("Provincia Huari no encontrada"))),
                new Distrito(null, "Huantar", "Distrito de Huantar", true, provinciaRepository.findByNombre("Huari").orElseThrow(() -> new RuntimeException("Provincia Huari no encontrada"))),
                new Distrito(null, "Masin", "Distrito de Masin", true, provinciaRepository.findByNombre("Huari").orElseThrow(() -> new RuntimeException("Provincia Huari no encontrada"))),
                new Distrito(null, "Paucas", "Distrito de Paucas", true, provinciaRepository.findByNombre("Huari").orElseThrow(() -> new RuntimeException("Provincia Huari no encontrada"))),
                new Distrito(null, "Ponto", "Distrito de Ponto", true, provinciaRepository.findByNombre("Huari").orElseThrow(() -> new RuntimeException("Provincia Huari no encontrada"))),
                new Distrito(null, "Rahuapampa", "Distrito de Rahuapampa", true, provinciaRepository.findByNombre("Huari").orElseThrow(() -> new RuntimeException("Provincia Huari no encontrada"))),
                new Distrito(null, "Rapayan", "Distrito de Rapayan", true, provinciaRepository.findByNombre("Huari").orElseThrow(() -> new RuntimeException("Provincia Huari no encontrada"))),
                new Distrito(null, "San Marcos", "Distrito de San Marcos", true, provinciaRepository.findByNombre("Huari").orElseThrow(() -> new RuntimeException("Provincia Huari no encontrada"))),
                new Distrito(null, "San Pedro de Chana", "Distrito de San Pedro de Chana", true, provinciaRepository.findByNombre("Huari").orElseThrow(() -> new RuntimeException("Provincia Huari no encontrada"))),
                new Distrito(null, "Uco", "Distrito de Uco", true, provinciaRepository.findByNombre("Huari").orElseThrow(() -> new RuntimeException("Provincia Huari no encontrada"))),

                // [Provincia de Huarmey]
                new Distrito(null, "Huarmey", "Distrito de Huarmey", true, provinciaRepository.findByNombre("Huarmey").orElseThrow(() -> new RuntimeException("Provincia Huarmey no encontrada"))),
                new Distrito(null, "Cochapetí", "Distrito de Cochapetí", true, provinciaRepository.findByNombre("Huarmey").orElseThrow(() -> new RuntimeException("Provincia Huarmey no encontrada"))),
                new Distrito(null, "Culebras", "Distrito de Culebras", true, provinciaRepository.findByNombre("Huarmey").orElseThrow(() -> new RuntimeException("Provincia Huarmey no encontrada"))),
                new Distrito(null, "Huayan", "Distrito de Huayan", true, provinciaRepository.findByNombre("Huarmey").orElseThrow(() -> new RuntimeException("Provincia Huarmey no encontrada"))),
                new Distrito(null, "Malvas", "Distrito de Malvas", true, provinciaRepository.findByNombre("Huarmey").orElseThrow(() -> new RuntimeException("Provincia Huarmey no encontrada"))),

                // [Provincia de Huaylas]
                new Distrito(null, "Caraz", "Distrito de Caraz", true, provinciaRepository.findByNombre("Huaylas").orElseThrow(() -> new RuntimeException("Provincia Huaylas no encontrada"))),
                new Distrito(null, "Huallanca", "Distrito de Huallanca", true, provinciaRepository.findByNombre("Huaylas").orElseThrow(() -> new RuntimeException("Provincia Huaylas no encontrada"))),
                new Distrito(null, "Huata", "Distrito de Huata", true, provinciaRepository.findByNombre("Huaylas").orElseThrow(() -> new RuntimeException("Provincia Huaylas no encontrada"))),
                new Distrito(null, "Huaylas", "Distrito de Huaylas", true, provinciaRepository.findByNombre("Huaylas").orElseThrow(() -> new RuntimeException("Provincia Huaylas no encontrada"))),
                new Distrito(null, "Mato", "Distrito de Mato", true, provinciaRepository.findByNombre("Huaylas").orElseThrow(() -> new RuntimeException("Provincia Huaylas no encontrada"))),
                new Distrito(null, "Pamparomas", "Distrito de Pamparomas", true, provinciaRepository.findByNombre("Huaylas").orElseThrow(() -> new RuntimeException("Provincia Huaylas no encontrada"))),
                new Distrito(null, "Pueblo Libre", "Distrito de Pueblo Libre", true, provinciaRepository.findByNombre("Huaylas").orElseThrow(() -> new RuntimeException("Provincia Huaylas no encontrada"))),
                new Distrito(null, "Santa Cruz", "Distrito de Santa Cruz", true, provinciaRepository.findByNombre("Huaylas").orElseThrow(() -> new RuntimeException("Provincia Huaylas no encontrada"))),
                new Distrito(null, "Santo Toribio", "Distrito de Santo Toribio", true, provinciaRepository.findByNombre("Huaylas").orElseThrow(() -> new RuntimeException("Provincia Huaylas no encontrada"))),
                new Distrito(null, "Yuracmarca", "Distrito de Yuracmarca", true, provinciaRepository.findByNombre("Huaylas").orElseThrow(() -> new RuntimeException("Provincia Huaylas no encontrada"))),

                // [Provincia de Mariscal Luzuriaga]
                new Distrito(null, "Piscobamba", "Distrito de Piscobamba", true, provinciaRepository.findByNombre("Mariscal Luzuriaga").orElseThrow(() -> new RuntimeException("Provincia Mariscal Luzuriaga no encontrada"))),
                new Distrito(null, "Casca", "Distrito de Casca", true, provinciaRepository.findByNombre("Mariscal Luzuriaga").orElseThrow(() -> new RuntimeException("Provincia Mariscal Luzuriaga no encontrada"))),
                new Distrito(null, "Eleazar Guzmán Barrón", "Distrito de Eleazar Guzmán Barrón", true, provinciaRepository.findByNombre("Mariscal Luzuriaga").orElseThrow(() -> new RuntimeException("Provincia Mariscal Luzuriaga no encontrada"))),
                new Distrito(null, "Fidel Olivas Escudero", "Distrito de Fidel Olivas Escudero", true, provinciaRepository.findByNombre("Mariscal Luzuriaga").orElseThrow(() -> new RuntimeException("Provincia Mariscal Luzuriaga no encontrada"))),
                new Distrito(null, "Llama", "Distrito de Llama", true, provinciaRepository.findByNombre("Mariscal Luzuriaga").orElseThrow(() -> new RuntimeException("Provincia Mariscal Luzuriaga no encontrada"))),
                new Distrito(null, "Llumpa", "Distrito de Llumpa", true, provinciaRepository.findByNombre("Mariscal Luzuriaga").orElseThrow(() -> new RuntimeException("Provincia Mariscal Luzuriaga no encontrada"))),
                new Distrito(null, "Lucma", "Distrito de Lucma", true, provinciaRepository.findByNombre("Mariscal Luzuriaga").orElseThrow(() -> new RuntimeException("Provincia Mariscal Luzuriaga no encontrada"))),
                new Distrito(null, "Musga", "Distrito de Musga", true, provinciaRepository.findByNombre("Mariscal Luzuriaga").orElseThrow(() -> new RuntimeException("Provincia Mariscal Luzuriaga no encontrada"))),

                // [Provincia de Ocros]
                new Distrito(null, "Ocros", "Distrito de Ocros", true, provinciaRepository.findByNombre("Ocros").orElseThrow(() -> new RuntimeException("Provincia Ocros no encontrada"))),
                new Distrito(null, "Acas", "Distrito de Acas", true, provinciaRepository.findByNombre("Ocros").orElseThrow(() -> new RuntimeException("Provincia Ocros no encontrada"))),
                new Distrito(null, "Cajamarquilla", "Distrito de Cajamarquilla", true, provinciaRepository.findByNombre("Ocros").orElseThrow(() -> new RuntimeException("Provincia Ocros no encontrada"))),
                new Distrito(null, "Carhuapampa", "Distrito de Carhuapampa", true, provinciaRepository.findByNombre("Ocros").orElseThrow(() -> new RuntimeException("Provincia Ocros no encontrada"))),
                new Distrito(null, "Cochas", "Distrito de Cochas", true, provinciaRepository.findByNombre("Ocros").orElseThrow(() -> new RuntimeException("Provincia Ocros no encontrada"))),
                new Distrito(null, "Congas", "Distrito de Congas", true, provinciaRepository.findByNombre("Ocros").orElseThrow(() -> new RuntimeException("Provincia Ocros no encontrada"))),
                new Distrito(null, "Llipa", "Distrito de Llipa", true, provinciaRepository.findByNombre("Ocros").orElseThrow(() -> new RuntimeException("Provincia Ocros no encontrada"))),
                new Distrito(null, "San Cristóbal de Raján", "Distrito de San Cristóbal de Raján", true, provinciaRepository.findByNombre("Ocros").orElseThrow(() -> new RuntimeException("Provincia Ocros no encontrada"))),
                new Distrito(null, "San Pedro", "Distrito de San Pedro", true, provinciaRepository.findByNombre("Ocros").orElseThrow(() -> new RuntimeException("Provincia Ocros no encontrada"))),
                new Distrito(null, "Santiago de Chilcas", "Distrito de Santiago de Chilcas", true, provinciaRepository.findByNombre("Ocros").orElseThrow(() -> new RuntimeException("Provincia Ocros no encontrada"))),

                // [Provincia de Pallasca]
                new Distrito(null, "Cabana", "Distrito de Cabana", true, provinciaRepository.findByNombre("Pallasca").orElseThrow(() -> new RuntimeException("Provincia Pallasca no encontrada"))),
                new Distrito(null, "Bolognesi", "Distrito de Bolognesi", true, provinciaRepository.findByNombre("Pallasca").orElseThrow(() -> new RuntimeException("Provincia Pallasca no encontrada"))),
                new Distrito(null, "Conchucos", "Distrito de Conchucos", true, provinciaRepository.findByNombre("Pallasca").orElseThrow(() -> new RuntimeException("Provincia Pallasca no encontrada"))),
                new Distrito(null, "Huacaschuque", "Distrito de Huacaschuque", true, provinciaRepository.findByNombre("Pallasca").orElseThrow(() -> new RuntimeException("Provincia Pallasca no encontrada"))),
                new Distrito(null, "Huandoval", "Distrito de Huandoval", true, provinciaRepository.findByNombre("Pallasca").orElseThrow(() -> new RuntimeException("Provincia Pallasca no encontrada"))),
                new Distrito(null, "Lacabamba", "Distrito de Lacabamba", true, provinciaRepository.findByNombre("Pallasca").orElseThrow(() -> new RuntimeException("Provincia Pallasca no encontrada"))),
                new Distrito(null, "Llapo", "Distrito de Llapo", true, provinciaRepository.findByNombre("Pallasca").orElseThrow(() -> new RuntimeException("Provincia Pallasca no encontrada"))),
                new Distrito(null, "Pallasca", "Distrito de Pallasca", true, provinciaRepository.findByNombre("Pallasca").orElseThrow(() -> new RuntimeException("Provincia Pallasca no encontrada"))),
                new Distrito(null, "Pampas", "Distrito de Pampas", true, provinciaRepository.findByNombre("Pallasca").orElseThrow(() -> new RuntimeException("Provincia Pallasca no encontrada"))),
                new Distrito(null, "Santa Rosa", "Distrito de Santa Rosa", true, provinciaRepository.findByNombre("Pallasca").orElseThrow(() -> new RuntimeException("Provincia Pallasca no encontrada"))),
                new Distrito(null, "Tauca", "Distrito de Tauca", true, provinciaRepository.findByNombre("Pallasca").orElseThrow(() -> new RuntimeException("Provincia Pallasca no encontrada"))),

                // [Provincia de Pomabamba]
                new Distrito(null, "Pomabamba", "Distrito de Pomabamba", true, provinciaRepository.findByNombre("Pomabamba").orElseThrow(() -> new RuntimeException("Provincia Pomabamba no encontrada"))),
                new Distrito(null, "Huayllán", "Distrito de Huayllán", true, provinciaRepository.findByNombre("Pomabamba").orElseThrow(() -> new RuntimeException("Provincia Pomabamba no encontrada"))),
                new Distrito(null, "Parobamba", "Distrito de Parobamba", true, provinciaRepository.findByNombre("Pomabamba").orElseThrow(() -> new RuntimeException("Provincia Pomabamba no encontrada"))),
                new Distrito(null, "Quinuabamba", "Distrito de Quinuabamba", true, provinciaRepository.findByNombre("Pomabamba").orElseThrow(() -> new RuntimeException("Provincia Pomabamba no encontrada"))),

                // [Provincia de Recuay]
                new Distrito(null, "Recuay", "Distrito de Recuay", true, provinciaRepository.findByNombre("Recuay").orElseThrow(() -> new RuntimeException("Provincia Recuay no encontrada"))),
                new Distrito(null, "Catac", "Distrito de Catac", true, provinciaRepository.findByNombre("Recuay").orElseThrow(() -> new RuntimeException("Provincia Recuay no encontrada"))),
                new Distrito(null, "Cotaparaco", "Distrito de Cotaparaco", true, provinciaRepository.findByNombre("Recuay").orElseThrow(() -> new RuntimeException("Provincia Recuay no encontrada"))),
                new Distrito(null, "Huayllapampa", "Distrito de Huayllapampa", true, provinciaRepository.findByNombre("Recuay").orElseThrow(() -> new RuntimeException("Provincia Recuay no encontrada"))),
                new Distrito(null, "Llacllín", "Distrito de Llacllín", true, provinciaRepository.findByNombre("Recuay").orElseThrow(() -> new RuntimeException("Provincia Recuay no encontrada"))),
                new Distrito(null, "Marca", "Distrito de Marca", true, provinciaRepository.findByNombre("Recuay").orElseThrow(() -> new RuntimeException("Provincia Recuay no encontrada"))),
                new Distrito(null, "Pampas Chico", "Distrito de Pampas Chico", true, provinciaRepository.findByNombre("Recuay").orElseThrow(() -> new RuntimeException("Provincia Recuay no encontrada"))),
                new Distrito(null, "Pararín", "Distrito de Pararín", true, provinciaRepository.findByNombre("Recuay").orElseThrow(() -> new RuntimeException("Provincia Recuay no encontrada"))),
                new Distrito(null, "Tapacocha", "Distrito de Tapacocha", true, provinciaRepository.findByNombre("Recuay").orElseThrow(() -> new RuntimeException("Provincia Recuay no encontrada"))),
                new Distrito(null, "Ticapampa", "Distrito de Ticapampa", true, provinciaRepository.findByNombre("Recuay").orElseThrow(() -> new RuntimeException("Provincia Recuay no encontrada"))),

                // [Provincia de Santa]
                new Distrito(null, "Chimbote", "Distrito de Chimbote", true, provinciaRepository.findByNombre("Santa").orElseThrow(() -> new RuntimeException("Provincia Santa no encontrada"))),
                new Distrito(null, "Cáceres del Perú", "Distrito de Cáceres del Perú", true, provinciaRepository.findByNombre("Santa").orElseThrow(() -> new RuntimeException("Provincia Santa no encontrada"))),
                new Distrito(null, "Coishco", "Distrito de Coishco", true, provinciaRepository.findByNombre("Santa").orElseThrow(() -> new RuntimeException("Provincia Santa no encontrada"))),
                new Distrito(null, "Macate", "Distrito de Macate", true, provinciaRepository.findByNombre("Santa").orElseThrow(() -> new RuntimeException("Provincia Santa no encontrada"))),
                new Distrito(null, "Moro", "Distrito de Moro", true, provinciaRepository.findByNombre("Santa").orElseThrow(() -> new RuntimeException("Provincia Santa no encontrada"))),
                new Distrito(null, "Nepeña", "Distrito de Nepeña", true, provinciaRepository.findByNombre("Santa").orElseThrow(() -> new RuntimeException("Provincia Santa no encontrada"))),
                new Distrito(null, "Samanco", "Distrito de Samanco", true, provinciaRepository.findByNombre("Santa").orElseThrow(() -> new RuntimeException("Provincia Santa no encontrada"))),
                new Distrito(null, "Santa", "Distrito de Santa", true, provinciaRepository.findByNombre("Santa").orElseThrow(() -> new RuntimeException("Provincia Santa no encontrada"))),
                new Distrito(null, "Nuevo Chimbote", "Distrito de Nuevo Chimbote", true, provinciaRepository.findByNombre("Santa").orElseThrow(() -> new RuntimeException("Provincia Santa no encontrada"))),

                // [Provincia de Sihuas]
                new Distrito(null, "Sihuas", "Distrito de Sihuas", true, provinciaRepository.findByNombre("Sihuas").orElseThrow(() -> new RuntimeException("Provincia Sihuas no encontrada"))),
                new Distrito(null, "Acobamba", "Distrito de Acobamba", true, provinciaRepository.findByNombre("Sihuas").orElseThrow(() -> new RuntimeException("Provincia Sihuas no encontrada"))),
                new Distrito(null, "Alfonso Ugarte", "Distrito de Alfonso Ugarte", true, provinciaRepository.findByNombre("Sihuas").orElseThrow(() -> new RuntimeException("Provincia Sihuas no encontrada"))),
                new Distrito(null, "Cashapampa", "Distrito de Cashapampa", true, provinciaRepository.findByNombre("Sihuas").orElseThrow(() -> new RuntimeException("Provincia Sihuas no encontrada"))),
                new Distrito(null, "Chingalpo", "Distrito de Chingalpo", true, provinciaRepository.findByNombre("Sihuas").orElseThrow(() -> new RuntimeException("Provincia Sihuas no encontrada"))),
                new Distrito(null, "Huayllabamba", "Distrito de Huayllabamba", true, provinciaRepository.findByNombre("Sihuas").orElseThrow(() -> new RuntimeException("Provincia Sihuas no encontrada"))),
                new Distrito(null, "Quiches", "Distrito de Quiches", true, provinciaRepository.findByNombre("Sihuas").orElseThrow(() -> new RuntimeException("Provincia Sihuas no encontrada"))),
                new Distrito(null, "Ragash", "Distrito de Ragash", true, provinciaRepository.findByNombre("Sihuas").orElseThrow(() -> new RuntimeException("Provincia Sihuas no encontrada"))),
                new Distrito(null, "San Juan", "Distrito de San Juan", true, provinciaRepository.findByNombre("Sihuas").orElseThrow(() -> new RuntimeException("Provincia Sihuas no encontrada"))),
                new Distrito(null, "Sicsibamba", "Distrito de Sicsibamba", true, provinciaRepository.findByNombre("Sihuas").orElseThrow(() -> new RuntimeException("Provincia Sihuas no encontrada"))),

                // [Provincia de Yungay]
                new Distrito(null, "Yungay", "Distrito de Yungay", true, provinciaRepository.findByNombre("Yungay").orElseThrow(() -> new RuntimeException("Provincia Yungay no encontrada"))),
                new Distrito(null, "Cascapara", "Distrito de Cascapara", true, provinciaRepository.findByNombre("Yungay").orElseThrow(() -> new RuntimeException("Provincia Yungay no encontrada"))),
                new Distrito(null, "Mancos", "Distrito de Mancos", true, provinciaRepository.findByNombre("Yungay").orElseThrow(() -> new RuntimeException("Provincia Yungay no encontrada"))),
                new Distrito(null, "Matacoto", "Distrito de Matacoto", true, provinciaRepository.findByNombre("Yungay").orElseThrow(() -> new RuntimeException("Provincia Yungay no encontrada"))),
                new Distrito(null, "Quillo", "Distrito de Quillo", true, provinciaRepository.findByNombre("Yungay").orElseThrow(() -> new RuntimeException("Provincia Yungay no encontrada"))),
                new Distrito(null, "Ranrahirca", "Distrito de Ranrahirca", true, provinciaRepository.findByNombre("Yungay").orElseThrow(() -> new RuntimeException("Provincia Yungay no encontrada"))),
                new Distrito(null, "Shupluy", "Distrito de Shupluy", true, provinciaRepository.findByNombre("Yungay").orElseThrow(() -> new RuntimeException("Provincia Yungay no encontrada"))),
                new Distrito(null, "Yanama", "Distrito de Yanama", true, provinciaRepository.findByNombre("Yungay").orElseThrow(() -> new RuntimeException("Provincia Yungay no encontrada")))
        );
        distritoRepository.saveAll(distritos);
    }

    private void seedDistritosApurimac() {
        List<Distrito> distritos = Arrays.asList(
                // Apurímac
                // [Provincia de Abancay]
                new Distrito(null, "Abancay", "Distrito de Abancay", true, provinciaRepository.findByNombre("Abancay").orElseThrow(() -> new RuntimeException("Provincia Abancay no encontrada"))),
                new Distrito(null, "Chacoche", "Distrito de Chacoche", true, provinciaRepository.findByNombre("Abancay").orElseThrow(() -> new RuntimeException("Provincia Abancay no encontrada"))),
                new Distrito(null, "Circa", "Distrito de Circa", true, provinciaRepository.findByNombre("Abancay").orElseThrow(() -> new RuntimeException("Provincia Abancay no encontrada"))),
                new Distrito(null, "Curahuasi", "Distrito de Curahuasi", true, provinciaRepository.findByNombre("Abancay").orElseThrow(() -> new RuntimeException("Provincia Abancay no encontrada"))),
                new Distrito(null, "Huanipaca", "Distrito de Huanipaca", true, provinciaRepository.findByNombre("Abancay").orElseThrow(() -> new RuntimeException("Provincia Abancay no encontrada"))),
                new Distrito(null, "Lambrama", "Distrito de Lambrama", true, provinciaRepository.findByNombre("Abancay").orElseThrow(() -> new RuntimeException("Provincia Abancay no encontrada"))),
                new Distrito(null, "Pichirhua", "Distrito de Pichirhua", true, provinciaRepository.findByNombre("Abancay").orElseThrow(() -> new RuntimeException("Provincia Abancay no encontrada"))),
                new Distrito(null, "San Pedro de Cachora", "Distrito de San Pedro de Cachora", true, provinciaRepository.findByNombre("Abancay").orElseThrow(() -> new RuntimeException("Provincia Abancay no encontrada"))),
                new Distrito(null, "Tamburco", "Distrito de Tamburco", true, provinciaRepository.findByNombre("Abancay").orElseThrow(() -> new RuntimeException("Provincia Abancay no encontrada"))),

                // [Provincia de Andahuaylas]
                new Distrito(null, "Andahuaylas", "Distrito de Andahuaylas", true, provinciaRepository.findByNombre("Andahuaylas").orElseThrow(() -> new RuntimeException("Provincia Andahuaylas no encontrada"))),
                new Distrito(null, "Andarapa", "Distrito de Andarapa", true, provinciaRepository.findByNombre("Andahuaylas").orElseThrow(() -> new RuntimeException("Provincia Andahuaylas no encontrada"))),
                new Distrito(null, "Chiara", "Distrito de Chiara", true, provinciaRepository.findByNombre("Andahuaylas").orElseThrow(() -> new RuntimeException("Provincia Andahuaylas no encontrada"))),
                new Distrito(null, "Huancarama", "Distrito de Huancarama", true, provinciaRepository.findByNombre("Andahuaylas").orElseThrow(() -> new RuntimeException("Provincia Andahuaylas no encontrada"))),
                new Distrito(null, "Huancaray", "Distrito de Huancaray", true, provinciaRepository.findByNombre("Andahuaylas").orElseThrow(() -> new RuntimeException("Provincia Andahuaylas no encontrada"))),
                new Distrito(null, "Huayana", "Distrito de Huayana", true, provinciaRepository.findByNombre("Andahuaylas").orElseThrow(() -> new RuntimeException("Provincia Andahuaylas no encontrada"))),
                new Distrito(null, "Kishara", "Distrito de Kishara", true, provinciaRepository.findByNombre("Andahuaylas").orElseThrow(() -> new RuntimeException("Provincia Andahuaylas no encontrada"))),
                new Distrito(null, "Pacobamba", "Distrito de Pacobamba", true, provinciaRepository.findByNombre("Andahuaylas").orElseThrow(() -> new RuntimeException("Provincia Andahuaylas no encontrada"))),
                new Distrito(null, "Pacucha", "Distrito de Pacucha", true, provinciaRepository.findByNombre("Andahuaylas").orElseThrow(() -> new RuntimeException("Provincia Andahuaylas no encontrada"))),
                new Distrito(null, "Pampachiri", "Distrito de Pampachiri", true, provinciaRepository.findByNombre("Andahuaylas").orElseThrow(() -> new RuntimeException("Provincia Andahuaylas no encontrada"))),
                new Distrito(null, "Pomacocha", "Distrito de Pomacocha", true, provinciaRepository.findByNombre("Andahuaylas").orElseThrow(() -> new RuntimeException("Provincia Andahuaylas no encontrada"))),
                new Distrito(null, "San Antonio de Cachi", "Distrito de San Antonio de Cachi", true, provinciaRepository.findByNombre("Andahuaylas").orElseThrow(() -> new RuntimeException("Provincia Andahuaylas no encontrada"))),
                new Distrito(null, "San Jerónimo", "Distrito de San Jerónimo", true, provinciaRepository.findByNombre("Andahuaylas").orElseThrow(() -> new RuntimeException("Provincia Andahuaylas no encontrada"))),
                new Distrito(null, "San Miguel de Chaccrampa", "Distrito de San Miguel de Chaccrampa", true, provinciaRepository.findByNombre("Andahuaylas").orElseThrow(() -> new RuntimeException("Provincia Andahuaylas no encontrada"))),
                new Distrito(null, "Santa María de Chicmo", "Distrito de Santa María de Chicmo", true, provinciaRepository.findByNombre("Andahuaylas").orElseThrow(() -> new RuntimeException("Provincia Andahuaylas no encontrada"))),
                new Distrito(null, "Talavera", "Distrito de Talavera", true, provinciaRepository.findByNombre("Andahuaylas").orElseThrow(() -> new RuntimeException("Provincia Andahuaylas no encontrada"))),
                new Distrito(null, "Tumay Huaraca", "Distrito de Tumay Huaraca", true, provinciaRepository.findByNombre("Andahuaylas").orElseThrow(() -> new RuntimeException("Provincia Andahuaylas no encontrada"))),
                new Distrito(null, "Turpo", "Distrito de Turpo", true, provinciaRepository.findByNombre("Andahuaylas").orElseThrow(() -> new RuntimeException("Provincia Andahuaylas no encontrada"))),
                new Distrito(null, "Kaquiabamba", "Distrito de Kaquiabamba", true, provinciaRepository.findByNombre("Andahuaylas").orElseThrow(() -> new RuntimeException("Provincia Andahuaylas no encontrada"))),
                new Distrito(null, "José María Arguedas", "Distrito de José María Arguedas", true, provinciaRepository.findByNombre("Andahuaylas").orElseThrow(() -> new RuntimeException("Provincia Andahuaylas no encontrada"))),

                // [Provincia de Antabamba]
                new Distrito(null, "Antabamba", "Distrito de Antabamba", true, provinciaRepository.findByNombre("Antabamba").orElseThrow(() -> new RuntimeException("Provincia Antabamba no encontrada"))),
                new Distrito(null, "El Oro", "Distrito de El Oro", true, provinciaRepository.findByNombre("Antabamba").orElseThrow(() -> new RuntimeException("Provincia Antabamba no encontrada"))),
                new Distrito(null, "Huaquirca", "Distrito de Huaquirca", true, provinciaRepository.findByNombre("Antabamba").orElseThrow(() -> new RuntimeException("Provincia Antabamba no encontrada"))),
                new Distrito(null, "Juan Espinoza Medrano", "Distrito de Juan Espinoza Medrano", true, provinciaRepository.findByNombre("Antabamba").orElseThrow(() -> new RuntimeException("Provincia Antabamba no encontrada"))),
                new Distrito(null, "Oropesa", "Distrito de Oropesa", true, provinciaRepository.findByNombre("Antabamba").orElseThrow(() -> new RuntimeException("Provincia Antabamba no encontrada"))),
                new Distrito(null, "Pachaconas", "Distrito de Pachaconas", true, provinciaRepository.findByNombre("Antabamba").orElseThrow(() -> new RuntimeException("Provincia Antabamba no encontrada"))),
                new Distrito(null, "Sabaino", "Distrito de Sabaino", true, provinciaRepository.findByNombre("Antabamba").orElseThrow(() -> new RuntimeException("Provincia Antabamba no encontrada"))),

                // [Provincia de Aymaraes]
                new Distrito(null, "Chalhuanca", "Distrito de Chalhuanca", true, provinciaRepository.findByNombre("Aymaraes").orElseThrow(() -> new RuntimeException("Provincia Aymaraes no encontrada"))),
                new Distrito(null, "Capaya", "Distrito de Capaya", true, provinciaRepository.findByNombre("Aymaraes").orElseThrow(() -> new RuntimeException("Provincia Aymaraes no encontrada"))),
                new Distrito(null, "Caraybamba", "Distrito de Caraybamba", true, provinciaRepository.findByNombre("Aymaraes").orElseThrow(() -> new RuntimeException("Provincia Aymaraes no encontrada"))),
                new Distrito(null, "Chapimarca", "Distrito de Chapimarca", true, provinciaRepository.findByNombre("Aymaraes").orElseThrow(() -> new RuntimeException("Provincia Aymaraes no encontrada"))),
                new Distrito(null, "Colcabamba", "Distrito de Colcabamba", true, provinciaRepository.findByNombre("Aymaraes").orElseThrow(() -> new RuntimeException("Provincia Aymaraes no encontrada"))),
                new Distrito(null, "Cotaruse", "Distrito de Cotaruse", true, provinciaRepository.findByNombre("Aymaraes").orElseThrow(() -> new RuntimeException("Provincia Aymaraes no encontrada"))),
                new Distrito(null, "Huayllo", "Distrito de Huayllo", true, provinciaRepository.findByNombre("Aymaraes").orElseThrow(() -> new RuntimeException("Provincia Aymaraes no encontrada"))),
                new Distrito(null, "Justo Apu Sahuaraura", "Distrito de Justo Apu Sahuaraura", true, provinciaRepository.findByNombre("Aymaraes").orElseThrow(() -> new RuntimeException("Provincia Aymaraes no encontrada"))),
                new Distrito(null, "Lucre", "Distrito de Lucre", true, provinciaRepository.findByNombre("Aymaraes").orElseThrow(() -> new RuntimeException("Provincia Aymaraes no encontrada"))),
                new Distrito(null, "Pocohuanca", "Distrito de Pocohuanca", true, provinciaRepository.findByNombre("Aymaraes").orElseThrow(() -> new RuntimeException("Provincia Aymaraes no encontrada"))),
                new Distrito(null, "San Juan de Chacña", "Distrito de San Juan de Chacña", true, provinciaRepository.findByNombre("Aymaraes").orElseThrow(() -> new RuntimeException("Provincia Aymaraes no encontrada"))),
                new Distrito(null, "Sañayca", "Distrito de Sañayca", true, provinciaRepository.findByNombre("Aymaraes").orElseThrow(() -> new RuntimeException("Provincia Aymaraes no encontrada"))),
                new Distrito(null, "Soraya", "Distrito de Soraya", true, provinciaRepository.findByNombre("Aymaraes").orElseThrow(() -> new RuntimeException("Provincia Aymaraes no encontrada"))),
                new Distrito(null, "Tapairihua", "Distrito de Tapairihua", true, provinciaRepository.findByNombre("Aymaraes").orElseThrow(() -> new RuntimeException("Provincia Aymaraes no encontrada"))),
                new Distrito(null, "Tintay", "Distrito de Tintay", true, provinciaRepository.findByNombre("Aymaraes").orElseThrow(() -> new RuntimeException("Provincia Aymaraes no encontrada"))),
                new Distrito(null, "Totora", "Distrito de Totora", true, provinciaRepository.findByNombre("Aymaraes").orElseThrow(() -> new RuntimeException("Provincia Aymaraes no encontrada"))),
                new Distrito(null, "Yanaca", "Distrito de Yanaca", true, provinciaRepository.findByNombre("Aymaraes").orElseThrow(() -> new RuntimeException("Provincia Aymaraes no encontrada"))),

                // [Provincia de Cotabambas]
                new Distrito(null, "Tambobamba", "Distrito de Tambobamba", true, provinciaRepository.findByNombre("Cotabambas").orElseThrow(() -> new RuntimeException("Provincia Cotabambas no encontrada"))),
                new Distrito(null, "Cotabambas", "Distrito de Cotabambas", true, provinciaRepository.findByNombre("Cotabambas").orElseThrow(() -> new RuntimeException("Provincia Cotabambas no encontrada"))),
                new Distrito(null, "Coyllurqui", "Distrito de Coyllurqui", true, provinciaRepository.findByNombre("Cotabambas").orElseThrow(() -> new RuntimeException("Provincia Cotabambas no encontrada"))),
                new Distrito(null, "Haquira", "Distrito de Haquira", true, provinciaRepository.findByNombre("Cotabambas").orElseThrow(() -> new RuntimeException("Provincia Cotabambas no encontrada"))),
                new Distrito(null, "Mara", "Distrito de Mara", true, provinciaRepository.findByNombre("Cotabambas").orElseThrow(() -> new RuntimeException("Provincia Cotabambas no encontrada"))),
                new Distrito(null, "Chalhuahuacho", "Distrito de Chalhuahuacho", true, provinciaRepository.findByNombre("Cotabambas").orElseThrow(() -> new RuntimeException("Provincia Cotabambas no encontrada"))),

                // [Provincia de Chincheros]
                new Distrito(null, "Chincheros", "Distrito de Chincheros", true, provinciaRepository.findByNombre("Chincheros").orElseThrow(() -> new RuntimeException("Provincia Chincheros no encontrada"))),
                new Distrito(null, "Anco Huallo", "Distrito de Anco Huallo", true, provinciaRepository.findByNombre("Chincheros").orElseThrow(() -> new RuntimeException("Provincia Chincheros no encontrada"))),
                new Distrito(null, "Cocharcas", "Distrito de Cocharcas", true, provinciaRepository.findByNombre("Chincheros").orElseThrow(() -> new RuntimeException("Provincia Chincheros no encontrada"))),
                new Distrito(null, "Huaccana", "Distrito de Huaccana", true, provinciaRepository.findByNombre("Chincheros").orElseThrow(() -> new RuntimeException("Provincia Chincheros no encontrada"))),
                new Distrito(null, "Ocobamba", "Distrito de Ocobamba", true, provinciaRepository.findByNombre("Chincheros").orElseThrow(() -> new RuntimeException("Provincia Chincheros no encontrada"))),
                new Distrito(null, "Ongoy", "Distrito de Ongoy", true, provinciaRepository.findByNombre("Chincheros").orElseThrow(() -> new RuntimeException("Provincia Chincheros no encontrada"))),
                new Distrito(null, "Uranmarca", "Distrito de Uranmarca", true, provinciaRepository.findByNombre("Chincheros").orElseThrow(() -> new RuntimeException("Provincia Chincheros no encontrada"))),
                new Distrito(null, "Ranracancha", "Distrito de Ranracancha", true, provinciaRepository.findByNombre("Chincheros").orElseThrow(() -> new RuntimeException("Provincia Chincheros no encontrada"))),

                // [Provincia de Grau]
                new Distrito(null, "Chuquibambilla", "Distrito de Chuquibambilla", true, provinciaRepository.findByNombre("Grau").orElseThrow(() -> new RuntimeException("Provincia Grau no encontrada"))),
                new Distrito(null, "Curpahuasi", "Distrito de Curpahuasi", true, provinciaRepository.findByNombre("Grau").orElseThrow(() -> new RuntimeException("Provincia Grau no encontrada"))),
                new Distrito(null, "Gamarra", "Distrito de Gamarra", true, provinciaRepository.findByNombre("Grau").orElseThrow(() -> new RuntimeException("Provincia Grau no encontrada"))),
                new Distrito(null, "Huayllati", "Distrito de Huayllati", true, provinciaRepository.findByNombre("Grau").orElseThrow(() -> new RuntimeException("Provincia Grau no encontrada"))),
                new Distrito(null, "Mamara", "Distrito de Mamara", true, provinciaRepository.findByNombre("Grau").orElseThrow(() -> new RuntimeException("Provincia Grau no encontrada"))),
                new Distrito(null, "Micaela Bastidas", "Distrito de Micaela Bastidas", true, provinciaRepository.findByNombre("Grau").orElseThrow(() -> new RuntimeException("Provincia Grau no encontrada"))),
                new Distrito(null, "Pataypampa", "Distrito de Pataypampa", true, provinciaRepository.findByNombre("Grau").orElseThrow(() -> new RuntimeException("Provincia Grau no encontrada"))),
                new Distrito(null, "Progreso", "Distrito de Progreso", true, provinciaRepository.findByNombre("Grau").orElseThrow(() -> new RuntimeException("Provincia Grau no encontrada"))),
                new Distrito(null, "San Antonio", "Distrito de San Antonio", true, provinciaRepository.findByNombre("Grau").orElseThrow(() -> new RuntimeException("Provincia Grau no encontrada"))),
                new Distrito(null, "Santa Rosa", "Distrito de Santa Rosa", true, provinciaRepository.findByNombre("Grau").orElseThrow(() -> new RuntimeException("Provincia Grau no encontrada"))),
                new Distrito(null, "Turpay", "Distrito de Turpay", true, provinciaRepository.findByNombre("Grau").orElseThrow(() -> new RuntimeException("Provincia Grau no encontrada"))),
                new Distrito(null, "Vilcabamba", "Distrito de Vilcabamba", true, provinciaRepository.findByNombre("Grau").orElseThrow(() -> new RuntimeException("Provincia Grau no encontrada"))),
                new Distrito(null, "Virundo", "Distrito de Virundo", true, provinciaRepository.findByNombre("Grau").orElseThrow(() -> new RuntimeException("Provincia Grau no encontrada"))),
                new Distrito(null, "Curasco", "Distrito de Curasco", true, provinciaRepository.findByNombre("Grau").orElseThrow(() -> new RuntimeException("Provincia Grau no encontrada")))
        );
        distritoRepository.saveAll(distritos);
    }

    private void seedDistritosArequipa() {
        List<Distrito> distritos = Arrays.asList(
                // Arequipa
                // [Provincia de Arequipa]
                new Distrito(null, "Arequipa", "Distrito de Arequipa", true, provinciaRepository.findByNombre("Arequipa").orElseThrow(() -> new RuntimeException("Provincia Arequipa no encontrada"))),
                new Distrito(null, "Alto Selva Alegre", "Distrito de Alto Selva Alegre", true, provinciaRepository.findByNombre("Arequipa").orElseThrow(() -> new RuntimeException("Provincia Arequipa no encontrada"))),
                new Distrito(null, "Cayma", "Distrito de Cayma", true, provinciaRepository.findByNombre("Arequipa").orElseThrow(() -> new RuntimeException("Provincia Arequipa no encontrada"))),
                new Distrito(null, "Cerro Colorado", "Distrito de Cerro Colorado", true, provinciaRepository.findByNombre("Arequipa").orElseThrow(() -> new RuntimeException("Provincia Arequipa no encontrada"))),
                new Distrito(null, "Characato", "Distrito de Characato", true, provinciaRepository.findByNombre("Arequipa").orElseThrow(() -> new RuntimeException("Provincia Arequipa no encontrada"))),
                new Distrito(null, "Chiguata", "Distrito de Chiguata", true, provinciaRepository.findByNombre("Arequipa").orElseThrow(() -> new RuntimeException("Provincia Arequipa no encontrada"))),
                new Distrito(null, "Jacobo Hunter", "Distrito de Jacobo Hunter", true, provinciaRepository.findByNombre("Arequipa").orElseThrow(() -> new RuntimeException("Provincia Arequipa no encontrada"))),
                new Distrito(null, "La Joya", "Distrito de La Joya", true, provinciaRepository.findByNombre("Arequipa").orElseThrow(() -> new RuntimeException("Provincia Arequipa no encontrada"))),
                new Distrito(null, "Mariano Melgar", "Distrito de Mariano Melgar", true, provinciaRepository.findByNombre("Arequipa").orElseThrow(() -> new RuntimeException("Provincia Arequipa no encontrada"))),
                new Distrito(null, "Miraflores", "Distrito de Miraflores", true, provinciaRepository.findByNombre("Arequipa").orElseThrow(() -> new RuntimeException("Provincia Arequipa no encontrada"))),
                new Distrito(null, "Mollebaya", "Distrito de Mollebaya", true, provinciaRepository.findByNombre("Arequipa").orElseThrow(() -> new RuntimeException("Provincia Arequipa no encontrada"))),
                new Distrito(null, "Paucarpata", "Distrito de Paucarpata", true, provinciaRepository.findByNombre("Arequipa").orElseThrow(() -> new RuntimeException("Provincia Arequipa no encontrada"))),
                new Distrito(null, "Pocsi", "Distrito de Pocsi", true, provinciaRepository.findByNombre("Arequipa").orElseThrow(() -> new RuntimeException("Provincia Arequipa no encontrada"))),
                new Distrito(null, "Polobaya", "Distrito de Polobaya", true, provinciaRepository.findByNombre("Arequipa").orElseThrow(() -> new RuntimeException("Provincia Arequipa no encontrada"))),
                new Distrito(null, "Quequeña", "Distrito de Quequeña", true, provinciaRepository.findByNombre("Arequipa").orElseThrow(() -> new RuntimeException("Provincia Arequipa no encontrada"))),
                new Distrito(null, "Sabandía", "Distrito de Sabandía", true, provinciaRepository.findByNombre("Arequipa").orElseThrow(() -> new RuntimeException("Provincia Arequipa no encontrada"))),
                new Distrito(null, "Sachaca", "Distrito de Sachaca", true, provinciaRepository.findByNombre("Arequipa").orElseThrow(() -> new RuntimeException("Provincia Arequipa no encontrada"))),
                new Distrito(null, "San Juan de Siguas", "Distrito de San Juan de Siguas", true, provinciaRepository.findByNombre("Arequipa").orElseThrow(() -> new RuntimeException("Provincia Arequipa no encontrada"))),
                new Distrito(null, "San Juan de Tarucani", "Distrito de San Juan de Tarucani", true, provinciaRepository.findByNombre("Arequipa").orElseThrow(() -> new RuntimeException("Provincia Arequipa no encontrada"))),
                new Distrito(null, "Santa Isabel de Siguas", "Distrito de Santa Isabel de Siguas", true, provinciaRepository.findByNombre("Arequipa").orElseThrow(() -> new RuntimeException("Provincia Arequipa no encontrada"))),
                new Distrito(null, "Santa Rita de Siguas", "Distrito de Santa Rita de Siguas", true, provinciaRepository.findByNombre("Arequipa").orElseThrow(() -> new RuntimeException("Provincia Arequipa no encontrada"))),
                new Distrito(null, "Socabaya", "Distrito de Socabaya", true, provinciaRepository.findByNombre("Arequipa").orElseThrow(() -> new RuntimeException("Provincia Arequipa no encontrada"))),
                new Distrito(null, "Tiabaya", "Distrito de Tiabaya", true, provinciaRepository.findByNombre("Arequipa").orElseThrow(() -> new RuntimeException("Provincia Arequipa no encontrada"))),
                new Distrito(null, "Uchumayo", "Distrito de Uchumayo", true, provinciaRepository.findByNombre("Arequipa").orElseThrow(() -> new RuntimeException("Provincia Arequipa no encontrada"))),
                new Distrito(null, "Vítor", "Distrito de Vítor", true, provinciaRepository.findByNombre("Arequipa").orElseThrow(() -> new RuntimeException("Provincia Arequipa no encontrada"))),
                new Distrito(null, "Yanahuara", "Distrito de Yanahuara", true, provinciaRepository.findByNombre("Arequipa").orElseThrow(() -> new RuntimeException("Provincia Arequipa no encontrada"))),
                new Distrito(null, "Yarabamba", "Distrito de Yarabamba", true, provinciaRepository.findByNombre("Arequipa").orElseThrow(() -> new RuntimeException("Provincia Arequipa no encontrada"))),
                new Distrito(null, "Yura", "Distrito de Yura", true, provinciaRepository.findByNombre("Arequipa").orElseThrow(() -> new RuntimeException("Provincia Arequipa no encontrada"))),
                new Distrito(null, "José Luis Bustamante Y Rivero", "Distrito de José Luis Bustamante Y Rivero", true, provinciaRepository.findByNombre("Arequipa").orElseThrow(() -> new RuntimeException("Provincia Arequipa no encontrada"))),

                // [Provincia de Camaná]
                new Distrito(null, "Camaná", "Distrito de Camaná", true, provinciaRepository.findByNombre("Camaná").orElseThrow(() -> new RuntimeException("Provincia Camaná no encontrada"))),
                new Distrito(null, "José María Quimper", "Distrito de José María Quimper", true, provinciaRepository.findByNombre("Camaná").orElseThrow(() -> new RuntimeException("Provincia Camaná no encontrada"))),
                new Distrito(null, "Mariano Nicolás Valcárcel", "Distrito de Mariano Nicolás Valcárcel", true, provinciaRepository.findByNombre("Camaná").orElseThrow(() -> new RuntimeException("Provincia Camaná no encontrada"))),
                new Distrito(null, "Mariscal Cáceres", "Distrito de Mariscal Cáceres", true, provinciaRepository.findByNombre("Camaná").orElseThrow(() -> new RuntimeException("Provincia Camaná no encontrada"))),
                new Distrito(null, "Nicolás de Pierola", "Distrito de Nicolás de Pierola", true, provinciaRepository.findByNombre("Camaná").orElseThrow(() -> new RuntimeException("Provincia Camaná no encontrada"))),
                new Distrito(null, "Ocoña", "Distrito de Ocoña", true, provinciaRepository.findByNombre("Camaná").orElseThrow(() -> new RuntimeException("Provincia Camaná no encontrada"))),
                new Distrito(null, "Quilca", "Distrito de Quilca", true, provinciaRepository.findByNombre("Camaná").orElseThrow(() -> new RuntimeException("Provincia Camaná no encontrada"))),
                new Distrito(null, "Samuel Pastor", "Distrito de Samuel Pastor", true, provinciaRepository.findByNombre("Camaná").orElseThrow(() -> new RuntimeException("Provincia Camaná no encontrada"))),

                // [Provincia de Caravelí]
                new Distrito(null, "Caravelí", "Distrito de Caravelí", true, provinciaRepository.findByNombre("Caravelí").orElseThrow(() -> new RuntimeException("Provincia Caravelí no encontrada"))),
                new Distrito(null, "Acarí", "Distrito de Acarí", true, provinciaRepository.findByNombre("Caravelí").orElseThrow(() -> new RuntimeException("Provincia Caravelí no encontrada"))),
                new Distrito(null, "Atico", "Distrito de Atico", true, provinciaRepository.findByNombre("Caravelí").orElseThrow(() -> new RuntimeException("Provincia Caravelí no encontrada"))),
                new Distrito(null, "Atiquipa", "Distrito de Atiquipa", true, provinciaRepository.findByNombre("Caravelí").orElseThrow(() -> new RuntimeException("Provincia Caravelí no encontrada"))),
                new Distrito(null, "Bella Unión", "Distrito de Bella Unión", true, provinciaRepository.findByNombre("Caravelí").orElseThrow(() -> new RuntimeException("Provincia Caravelí no encontrada"))),
                new Distrito(null, "Cahuacho", "Distrito de Cahuacho", true, provinciaRepository.findByNombre("Caravelí").orElseThrow(() -> new RuntimeException("Provincia Caravelí no encontrada"))),
                new Distrito(null, "Chala", "Distrito de Chala", true, provinciaRepository.findByNombre("Caravelí").orElseThrow(() -> new RuntimeException("Provincia Caravelí no encontrada"))),
                new Distrito(null, "Chaparra", "Distrito de Chaparra", true, provinciaRepository.findByNombre("Caravelí").orElseThrow(() -> new RuntimeException("Provincia Caravelí no encontrada"))),
                new Distrito(null, "Huanuhuanu", "Distrito de Huanuhuanu", true, provinciaRepository.findByNombre("Caravelí").orElseThrow(() -> new RuntimeException("Provincia Caravelí no encontrada"))),
                new Distrito(null, "Jaqui", "Distrito de Jaqui", true, provinciaRepository.findByNombre("Caravelí").orElseThrow(() -> new RuntimeException("Provincia Caravelí no encontrada"))),
                new Distrito(null, "Lomas", "Distrito de Lomas", true, provinciaRepository.findByNombre("Caravelí").orElseThrow(() -> new RuntimeException("Provincia Caravelí no encontrada"))),
                new Distrito(null, "Quicacha", "Distrito de Quicacha", true, provinciaRepository.findByNombre("Caravelí").orElseThrow(() -> new RuntimeException("Provincia Caravelí no encontrada"))),
                new Distrito(null, "Yauca", "Distrito de Yauca", true, provinciaRepository.findByNombre("Caravelí").orElseThrow(() -> new RuntimeException("Provincia Caravelí no encontrada"))),

                // [Provincia de Castilla]
                new Distrito(null, "Aplao", "Distrito de Aplao", true, provinciaRepository.findByNombre("Castilla").orElseThrow(() -> new RuntimeException("Provincia Castilla no encontrada"))),
                new Distrito(null, "Andagua", "Distrito de Andagua", true, provinciaRepository.findByNombre("Castilla").orElseThrow(() -> new RuntimeException("Provincia Castilla no encontrada"))),
                new Distrito(null, "Ayo", "Distrito de Ayo", true, provinciaRepository.findByNombre("Castilla").orElseThrow(() -> new RuntimeException("Provincia Castilla no encontrada"))),
                new Distrito(null, "Chachas", "Distrito de Chachas", true, provinciaRepository.findByNombre("Castilla").orElseThrow(() -> new RuntimeException("Provincia Castilla no encontrada"))),
                new Distrito(null, "Chilcaymarca", "Distrito de Chilcaymarca", true, provinciaRepository.findByNombre("Castilla").orElseThrow(() -> new RuntimeException("Provincia Castilla no encontrada"))),
                new Distrito(null, "Choco", "Distrito de Choco", true, provinciaRepository.findByNombre("Castilla").orElseThrow(() -> new RuntimeException("Provincia Castilla no encontrada"))),
                new Distrito(null, "Huancarqui", "Distrito de Huancarqui", true, provinciaRepository.findByNombre("Castilla").orElseThrow(() -> new RuntimeException("Provincia Castilla no encontrada"))),
                new Distrito(null, "Machaguay", "Distrito de Machaguay", true, provinciaRepository.findByNombre("Castilla").orElseThrow(() -> new RuntimeException("Provincia Castilla no encontrada"))),
                new Distrito(null, "Orcopampa", "Distrito de Orcopampa", true, provinciaRepository.findByNombre("Castilla").orElseThrow(() -> new RuntimeException("Provincia Castilla no encontrada"))),
                new Distrito(null, "Pampacolca", "Distrito de Pampacolca", true, provinciaRepository.findByNombre("Castilla").orElseThrow(() -> new RuntimeException("Provincia Castilla no encontrada"))),
                new Distrito(null, "Tipan", "Distrito de Tipan", true, provinciaRepository.findByNombre("Castilla").orElseThrow(() -> new RuntimeException("Provincia Castilla no encontrada"))),
                new Distrito(null, "Uñón", "Distrito de Uñón", true, provinciaRepository.findByNombre("Castilla").orElseThrow(() -> new RuntimeException("Provincia Castilla no encontrada"))),
                new Distrito(null, "Uraca", "Distrito de Uraca", true, provinciaRepository.findByNombre("Castilla").orElseThrow(() -> new RuntimeException("Provincia Castilla no encontrada"))),
                new Distrito(null, "Viraco", "Distrito de Viraco", true, provinciaRepository.findByNombre("Castilla").orElseThrow(() -> new RuntimeException("Provincia Castilla no encontrada"))),

                // [Provincia de Caylloma]
                new Distrito(null, "Chivay", "Distrito de Chivay", true, provinciaRepository.findByNombre("Caylloma").orElseThrow(() -> new RuntimeException("Provincia Caylloma no encontrada"))),
                new Distrito(null, "Achoma", "Distrito de Achoma", true, provinciaRepository.findByNombre("Caylloma").orElseThrow(() -> new RuntimeException("Provincia Caylloma no encontrada"))),
                new Distrito(null, "Cabanaconde", "Distrito de Cabanaconde", true, provinciaRepository.findByNombre("Caylloma").orElseThrow(() -> new RuntimeException("Provincia Caylloma no encontrada"))),
                new Distrito(null, "Callalli", "Distrito de Callalli", true, provinciaRepository.findByNombre("Caylloma").orElseThrow(() -> new RuntimeException("Provincia Caylloma no encontrada"))),
                new Distrito(null, "Caylloma", "Distrito de Caylloma", true, provinciaRepository.findByNombre("Caylloma").orElseThrow(() -> new RuntimeException("Provincia Caylloma no encontrada"))),
                new Distrito(null, "Coporaque", "Distrito de Coporaque", true, provinciaRepository.findByNombre("Caylloma").orElseThrow(() -> new RuntimeException("Provincia Caylloma no encontrada"))),
                new Distrito(null, "Huambo", "Distrito de Huambo", true, provinciaRepository.findByNombre("Caylloma").orElseThrow(() -> new RuntimeException("Provincia Caylloma no encontrada"))),
                new Distrito(null, "Huanca", "Distrito de Huanca", true, provinciaRepository.findByNombre("Caylloma").orElseThrow(() -> new RuntimeException("Provincia Caylloma no encontrada"))),
                new Distrito(null, "Ichupampa", "Distrito de Ichupampa", true, provinciaRepository.findByNombre("Caylloma").orElseThrow(() -> new RuntimeException("Provincia Caylloma no encontrada"))),
                new Distrito(null, "Lari", "Distrito de Lari", true, provinciaRepository.findByNombre("Caylloma").orElseThrow(() -> new RuntimeException("Provincia Caylloma no encontrada"))),
                new Distrito(null, "Lluta", "Distrito de Lluta", true, provinciaRepository.findByNombre("Caylloma").orElseThrow(() -> new RuntimeException("Provincia Caylloma no encontrada"))),
                new Distrito(null, "Maca", "Distrito de Maca", true, provinciaRepository.findByNombre("Caylloma").orElseThrow(() -> new RuntimeException("Provincia Caylloma no encontrada"))),
                new Distrito(null, "Madrigal", "Distrito de Madrigal", true, provinciaRepository.findByNombre("Caylloma").orElseThrow(() -> new RuntimeException("Provincia Caylloma no encontrada"))),
                new Distrito(null, "San Antonio de Chuca", "Distrito de San Antonio de Chuca", true, provinciaRepository.findByNombre("Caylloma").orElseThrow(() -> new RuntimeException("Provincia Caylloma no encontrada"))),
                new Distrito(null, "Sibayo", "Distrito de Sibayo", true, provinciaRepository.findByNombre("Caylloma").orElseThrow(() -> new RuntimeException("Provincia Caylloma no encontrada"))),
                new Distrito(null, "Tapay", "Distrito de Tapay", true, provinciaRepository.findByNombre("Caylloma").orElseThrow(() -> new RuntimeException("Provincia Caylloma no encontrada"))),
                new Distrito(null, "Tisco", "Distrito de Tisco", true, provinciaRepository.findByNombre("Caylloma").orElseThrow(() -> new RuntimeException("Provincia Caylloma no encontrada"))),
                new Distrito(null, "Tuti", "Distrito de Tuti", true, provinciaRepository.findByNombre("Caylloma").orElseThrow(() -> new RuntimeException("Provincia Caylloma no encontrada"))),
                new Distrito(null, "Yanque", "Distrito de Yanque", true, provinciaRepository.findByNombre("Caylloma").orElseThrow(() -> new RuntimeException("Provincia Caylloma no encontrada"))),
                new Distrito(null, "Majes", "Distrito de Majes", true, provinciaRepository.findByNombre("Caylloma").orElseThrow(() -> new RuntimeException("Provincia Caylloma no encontrada"))),

                // [Provincia de Condesuyos]
                new Distrito(null, "Chuquibamba", "Distrito de Chuquibamba", true, provinciaRepository.findByNombre("Condesuyos").orElseThrow(() -> new RuntimeException("Provincia Condesuyos no encontrada"))),
                new Distrito(null, "Andaray", "Distrito de Andaray", true, provinciaRepository.findByNombre("Condesuyos").orElseThrow(() -> new RuntimeException("Provincia Condesuyos no encontrada"))),
                new Distrito(null, "Cayarani", "Distrito de Cayarani", true, provinciaRepository.findByNombre("Condesuyos").orElseThrow(() -> new RuntimeException("Provincia Condesuyos no encontrada"))),
                new Distrito(null, "Chichas", "Distrito de Chichas", true, provinciaRepository.findByNombre("Condesuyos").orElseThrow(() -> new RuntimeException("Provincia Condesuyos no encontrada"))),
                new Distrito(null, "Iray", "Distrito de Iray", true, provinciaRepository.findByNombre("Condesuyos").orElseThrow(() -> new RuntimeException("Provincia Condesuyos no encontrada"))),
                new Distrito(null, "Río Grande", "Distrito de Río Grande", true, provinciaRepository.findByNombre("Condesuyos").orElseThrow(() -> new RuntimeException("Provincia Condesuyos no encontrada"))),
                new Distrito(null, "Salamanca", "Distrito de Salamanca", true, provinciaRepository.findByNombre("Condesuyos").orElseThrow(() -> new RuntimeException("Provincia Condesuyos no encontrada"))),
                new Distrito(null, "Yanaquihua", "Distrito de Yanaquihua", true, provinciaRepository.findByNombre("Condesuyos").orElseThrow(() -> new RuntimeException("Provincia Condesuyos no encontrada"))),

                // [Provincia de Islay]
                new Distrito(null, "Mollendo", "Distrito de Mollendo", true, provinciaRepository.findByNombre("Islay").orElseThrow(() -> new RuntimeException("Provincia Islay no encontrada"))),
                new Distrito(null, "Cocachacra", "Distrito de Cocachacra", true, provinciaRepository.findByNombre("Islay").orElseThrow(() -> new RuntimeException("Provincia Islay no encontrada"))),
                new Distrito(null, "Deán Valdivia", "Distrito de Deán Valdivia", true, provinciaRepository.findByNombre("Islay").orElseThrow(() -> new RuntimeException("Provincia Islay no encontrada"))),
                new Distrito(null, "Islay", "Distrito de Islay", true, provinciaRepository.findByNombre("Islay").orElseThrow(() -> new RuntimeException("Provincia Islay no encontrada"))),
                new Distrito(null, "Mejía", "Distrito de Mejía", true, provinciaRepository.findByNombre("Islay").orElseThrow(() -> new RuntimeException("Provincia Islay no encontrada"))),
                new Distrito(null, "Punta de Bombón", "Distrito de Punta de Bombón", true, provinciaRepository.findByNombre("Islay").orElseThrow(() -> new RuntimeException("Provincia Islay no encontrada"))),

                // [Provincia de La Unión]
                new Distrito(null, "Cotahuasi", "Distrito de Cotahuasi", true, provinciaRepository.findByNombre("La Unión").orElseThrow(() -> new RuntimeException("Provincia La Unión no encontrada"))),
                new Distrito(null, "Alca", "Distrito de Alca", true, provinciaRepository.findByNombre("La Unión").orElseThrow(() -> new RuntimeException("Provincia La Unión no encontrada"))),
                new Distrito(null, "Charcana", "Distrito de Charcana", true, provinciaRepository.findByNombre("La Unión").orElseThrow(() -> new RuntimeException("Provincia La Unión no encontrada"))),
                new Distrito(null, "Huaynacotas", "Distrito de Huaynacotas", true, provinciaRepository.findByNombre("La Unión").orElseThrow(() -> new RuntimeException("Provincia La Unión no encontrada"))),
                new Distrito(null, "Pampamarca", "Distrito de Pampamarca", true, provinciaRepository.findByNombre("La Unión").orElseThrow(() -> new RuntimeException("Provincia La Unión no encontrada"))),
                new Distrito(null, "Puyca", "Distrito de Puyca", true, provinciaRepository.findByNombre("La Unión").orElseThrow(() -> new RuntimeException("Provincia La Unión no encontrada"))),
                new Distrito(null, "Quechualla", "Distrito de Quechualla", true, provinciaRepository.findByNombre("La Unión").orElseThrow(() -> new RuntimeException("Provincia La Unión no encontrada"))),
                new Distrito(null, "Sayla", "Distrito de Sayla", true, provinciaRepository.findByNombre("La Unión").orElseThrow(() -> new RuntimeException("Provincia La Unión no encontrada"))),
                new Distrito(null, "Tauría", "Distrito de Tauría", true, provinciaRepository.findByNombre("La Unión").orElseThrow(() -> new RuntimeException("Provincia La Unión no encontrada"))),
                new Distrito(null, "Tomepampa", "Distrito de Tomepampa", true, provinciaRepository.findByNombre("La Unión").orElseThrow(() -> new RuntimeException("Provincia La Unión no encontrada"))),
                new Distrito(null, "Toro", "Distrito de Toro", true, provinciaRepository.findByNombre("La Unión").orElseThrow(() -> new RuntimeException("Provincia La Unión no encontrada")))
        );
        distritoRepository.saveAll(distritos);
    }

    private void seedDistritosAyacucho() {
        List<Distrito> distritos = Arrays.asList(
                // Ayacucho
                // [Provincia de Huamanga]
                new Distrito(null, "Ayacucho", "Distrito de Ayacucho", true, provinciaRepository.findByNombre("Huamanga").orElseThrow(() -> new RuntimeException("Provincia Huamanga no encontrada"))),
                new Distrito(null, "Acocro", "Distrito de Acocro", true, provinciaRepository.findByNombre("Huamanga").orElseThrow(() -> new RuntimeException("Provincia Huamanga no encontrada"))),
                new Distrito(null, "Acos Vinchos", "Distrito de Acos Vinchos", true, provinciaRepository.findByNombre("Huamanga").orElseThrow(() -> new RuntimeException("Provincia Huamanga no encontrada"))),
                new Distrito(null, "Carmen Alto", "Distrito de Carmen Alto", true, provinciaRepository.findByNombre("Huamanga").orElseThrow(() -> new RuntimeException("Provincia Huamanga no encontrada"))),
                new Distrito(null, "Chiara", "Distrito de Chiara", true, provinciaRepository.findByNombre("Huamanga").orElseThrow(() -> new RuntimeException("Provincia Huamanga no encontrada"))),
                new Distrito(null, "Ocros", "Distrito de Ocros", true, provinciaRepository.findByNombre("Huamanga").orElseThrow(() -> new RuntimeException("Provincia Huamanga no encontrada"))),
                new Distrito(null, "Pacaycasa", "Distrito de Pacaycasa", true, provinciaRepository.findByNombre("Huamanga").orElseThrow(() -> new RuntimeException("Provincia Huamanga no encontrada"))),
                new Distrito(null, "Quinua", "Distrito de Quinua", true, provinciaRepository.findByNombre("Huamanga").orElseThrow(() -> new RuntimeException("Provincia Huamanga no encontrada"))),
                new Distrito(null, "San José de Ticllas", "Distrito de San José de Ticllas", true, provinciaRepository.findByNombre("Huamanga").orElseThrow(() -> new RuntimeException("Provincia Huamanga no encontrada"))),
                new Distrito(null, "San Juan Bautista", "Distrito de San Juan Bautista", true, provinciaRepository.findByNombre("Huamanga").orElseThrow(() -> new RuntimeException("Provincia Huamanga no encontrada"))),
                new Distrito(null, "Santiago de Pischa", "Distrito de Santiago de Pischa", true, provinciaRepository.findByNombre("Huamanga").orElseThrow(() -> new RuntimeException("Provincia Huamanga no encontrada"))),
                new Distrito(null, "Socos", "Distrito de Socos", true, provinciaRepository.findByNombre("Huamanga").orElseThrow(() -> new RuntimeException("Provincia Huamanga no encontrada"))),
                new Distrito(null, "Tambillo", "Distrito de Tambillo", true, provinciaRepository.findByNombre("Huamanga").orElseThrow(() -> new RuntimeException("Provincia Huamanga no encontrada"))),
                new Distrito(null, "Vinchos", "Distrito de Vinchos", true, provinciaRepository.findByNombre("Huamanga").orElseThrow(() -> new RuntimeException("Provincia Huamanga no encontrada"))),
                new Distrito(null, "Jesús Nazareno", "Distrito de Jesús Nazareno", true, provinciaRepository.findByNombre("Huamanga").orElseThrow(() -> new RuntimeException("Provincia Huamanga no encontrada"))),
                new Distrito(null, "Andrés Avelino Cáceres Dorregaray", "Distrito de Andrés Avelino Cáceres Dorregaray", true, provinciaRepository.findByNombre("Huamanga").orElseThrow(() -> new RuntimeException("Provincia Huamanga no encontrada"))),

                // [Provincia de Cangallo]
                new Distrito(null, "Cangallo", "Distrito de Cangallo", true, provinciaRepository.findByNombre("Cangallo").orElseThrow(() -> new RuntimeException("Provincia Cangallo no encontrada"))),
                new Distrito(null, "Chuschi", "Distrito de Chuschi", true, provinciaRepository.findByNombre("Cangallo").orElseThrow(() -> new RuntimeException("Provincia Cangallo no encontrada"))),
                new Distrito(null, "Los Morochucos", "Distrito de Los Morochucos", true, provinciaRepository.findByNombre("Cangallo").orElseThrow(() -> new RuntimeException("Provincia Cangallo no encontrada"))),
                new Distrito(null, "María Parado de Bellido", "Distrito de María Parado de Bellido", true, provinciaRepository.findByNombre("Cangallo").orElseThrow(() -> new RuntimeException("Provincia Cangallo no encontrada"))),
                new Distrito(null, "Paras", "Distrito de Paras", true, provinciaRepository.findByNombre("Cangallo").orElseThrow(() -> new RuntimeException("Provincia Cangallo no encontrada"))),
                new Distrito(null, "Totos", "Distrito de Totos", true, provinciaRepository.findByNombre("Cangallo").orElseThrow(() -> new RuntimeException("Provincia Cangallo no encontrada"))),

                // [Provincia de Huanca Sancos]
                new Distrito(null, "Sancos", "Distrito de Sancos", true, provinciaRepository.findByNombre("Huanca Sancos").orElseThrow(() -> new RuntimeException("Provincia Huanca Sancos no encontrada"))),
                new Distrito(null, "Carapo", "Distrito de Carapo", true, provinciaRepository.findByNombre("Huanca Sancos").orElseThrow(() -> new RuntimeException("Provincia Huanca Sancos no encontrada"))),
                new Distrito(null, "Sacsamarca", "Distrito de Sacsamarca", true, provinciaRepository.findByNombre("Huanca Sancos").orElseThrow(() -> new RuntimeException("Provincia Huanca Sancos no encontrada"))),
                new Distrito(null, "Santiago de Lucanamarca", "Distrito de Santiago de Lucanamarca", true, provinciaRepository.findByNombre("Huanca Sancos").orElseThrow(() -> new RuntimeException("Provincia Huanca Sancos no encontrada"))),

                // [Provincia de Huanta]
                new Distrito(null, "Huanta", "Distrito de Huanta", true, provinciaRepository.findByNombre("Huanta").orElseThrow(() -> new RuntimeException("Provincia Huanta no encontrada"))),
                new Distrito(null, "Ayahuanco", "Distrito de Ayahuanco", true, provinciaRepository.findByNombre("Huanta").orElseThrow(() -> new RuntimeException("Provincia Huanta no encontrada"))),
                new Distrito(null, "Huamanguilla", "Distrito de Huamanguilla", true, provinciaRepository.findByNombre("Huanta").orElseThrow(() -> new RuntimeException("Provincia Huanta no encontrada"))),
                new Distrito(null, "Iguain", "Distrito de Iguain", true, provinciaRepository.findByNombre("Huanta").orElseThrow(() -> new RuntimeException("Provincia Huanta no encontrada"))),
                new Distrito(null, "Luricocha", "Distrito de Luricocha", true, provinciaRepository.findByNombre("Huanta").orElseThrow(() -> new RuntimeException("Provincia Huanta no encontrada"))),
                new Distrito(null, "Santillana", "Distrito de Santillana", true, provinciaRepository.findByNombre("Huanta").orElseThrow(() -> new RuntimeException("Provincia Huanta no encontrada"))),
                new Distrito(null, "Sivia", "Distrito de Sivia", true, provinciaRepository.findByNombre("Huanta").orElseThrow(() -> new RuntimeException("Provincia Huanta no encontrada"))),
                new Distrito(null, "Llochegua", "Distrito de Llochegua", true, provinciaRepository.findByNombre("Huanta").orElseThrow(() -> new RuntimeException("Provincia Huanta no encontrada"))),
                new Distrito(null, "Canayre", "Distrito de Canayre", true, provinciaRepository.findByNombre("Huanta").orElseThrow(() -> new RuntimeException("Provincia Huanta no encontrada"))),
                new Distrito(null, "Uchuraccay", "Distrito de Uchuraccay", true, provinciaRepository.findByNombre("Huanta").orElseThrow(() -> new RuntimeException("Provincia Huanta no encontrada"))),
                new Distrito(null, "Pucacolpa", "Distrito de Pucacolpa", true, provinciaRepository.findByNombre("Huanta").orElseThrow(() -> new RuntimeException("Provincia Huanta no encontrada"))),

                // [Provincia de La Mar]
                new Distrito(null, "San Miguel", "Distrito de San Miguel", true, provinciaRepository.findByNombre("La Mar").orElseThrow(() -> new RuntimeException("Provincia La Mar no encontrada"))),
                new Distrito(null, "Anco", "Distrito de Anco", true, provinciaRepository.findByNombre("La Mar").orElseThrow(() -> new RuntimeException("Provincia La Mar no encontrada"))),
                new Distrito(null, "Ayna", "Distrito de Ayna", true, provinciaRepository.findByNombre("La Mar").orElseThrow(() -> new RuntimeException("Provincia La Mar no encontrada"))),
                new Distrito(null, "Chilcas", "Distrito de Chilcas", true, provinciaRepository.findByNombre("La Mar").orElseThrow(() -> new RuntimeException("Provincia La Mar no encontrada"))),
                new Distrito(null, "Chungui", "Distrito de Chungui", true, provinciaRepository.findByNombre("La Mar").orElseThrow(() -> new RuntimeException("Provincia La Mar no encontrada"))),
                new Distrito(null, "Luis Carranza", "Distrito de Luis Carranza", true, provinciaRepository.findByNombre("La Mar").orElseThrow(() -> new RuntimeException("Provincia La Mar no encontrada"))),
                new Distrito(null, "Santa Rosa", "Distrito de Santa Rosa", true, provinciaRepository.findByNombre("La Mar").orElseThrow(() -> new RuntimeException("Provincia La Mar no encontrada"))),
                new Distrito(null, "Tambo", "Distrito de Tambo", true, provinciaRepository.findByNombre("La Mar").orElseThrow(() -> new RuntimeException("Provincia La Mar no encontrada"))),
                new Distrito(null, "Samugari", "Distrito de Samugari", true, provinciaRepository.findByNombre("La Mar").orElseThrow(() -> new RuntimeException("Provincia La Mar no encontrada"))),
                new Distrito(null, "Anchihuay", "Distrito de Anchihuay", true, provinciaRepository.findByNombre("La Mar").orElseThrow(() -> new RuntimeException("Provincia La Mar no encontrada"))),

                // [Provincia de Lucanas]
                new Distrito(null, "Puquio", "Distrito de Puquio", true, provinciaRepository.findByNombre("Lucanas").orElseThrow(() -> new RuntimeException("Provincia Lucanas no encontrada"))),
                new Distrito(null, "Aucara", "Distrito de Aucara", true, provinciaRepository.findByNombre("Lucanas").orElseThrow(() -> new RuntimeException("Provincia Lucanas no encontrada"))),
                new Distrito(null, "Cabana", "Distrito de Cabana", true, provinciaRepository.findByNombre("Lucanas").orElseThrow(() -> new RuntimeException("Provincia Lucanas no encontrada"))),
                new Distrito(null, "Carmen Salcedo", "Distrito de Carmen Salcedo", true, provinciaRepository.findByNombre("Lucanas").orElseThrow(() -> new RuntimeException("Provincia Lucanas no encontrada"))),
                new Distrito(null, "Chaviña", "Distrito de Chaviña", true, provinciaRepository.findByNombre("Lucanas").orElseThrow(() -> new RuntimeException("Provincia Lucanas no encontrada"))),
                new Distrito(null, "Chipao", "Distrito de Chipao", true, provinciaRepository.findByNombre("Lucanas").orElseThrow(() -> new RuntimeException("Provincia Lucanas no encontrada"))),
                new Distrito(null, "Huac-huas", "Distrito de Huac-huas", true, provinciaRepository.findByNombre("Lucanas").orElseThrow(() -> new RuntimeException("Provincia Lucanas no encontrada"))),
                new Distrito(null, "Laramate", "Distrito de Laramate", true, provinciaRepository.findByNombre("Lucanas").orElseThrow(() -> new RuntimeException("Provincia Lucanas no encontrada"))),
                new Distrito(null, "Leoncio Prado", "Distrito de Leoncio Prado", true, provinciaRepository.findByNombre("Lucanas").orElseThrow(() -> new RuntimeException("Provincia Lucanas no encontrada"))),
                new Distrito(null, "Llauta", "Distrito de Llauta", true, provinciaRepository.findByNombre("Lucanas").orElseThrow(() -> new RuntimeException("Provincia Lucanas no encontrada"))),
                new Distrito(null, "Lucanas", "Distrito de Lucanas", true, provinciaRepository.findByNombre("Lucanas").orElseThrow(() -> new RuntimeException("Provincia Lucanas no encontrada"))),
                new Distrito(null, "Ocaña", "Distrito de Ocaña", true, provinciaRepository.findByNombre("Lucanas").orElseThrow(() -> new RuntimeException("Provincia Lucanas no encontrada"))),
                new Distrito(null, "Otoca", "Distrito de Otoca", true, provinciaRepository.findByNombre("Lucanas").orElseThrow(() -> new RuntimeException("Provincia Lucanas no encontrada"))),
                new Distrito(null, "Saisa", "Distrito de Saisa", true, provinciaRepository.findByNombre("Lucanas").orElseThrow(() -> new RuntimeException("Provincia Lucanas no encontrada"))),
                new Distrito(null, "San Cristóbal", "Distrito de San Cristóbal", true, provinciaRepository.findByNombre("Lucanas").orElseThrow(() -> new RuntimeException("Provincia Lucanas no encontrada"))),
                new Distrito(null, "San Juan", "Distrito de San Juan", true, provinciaRepository.findByNombre("Lucanas").orElseThrow(() -> new RuntimeException("Provincia Lucanas no encontrada"))),
                new Distrito(null, "San Pedro", "Distrito de San Pedro", true, provinciaRepository.findByNombre("Lucanas").orElseThrow(() -> new RuntimeException("Provincia Lucanas no encontrada"))),
                new Distrito(null, "San Pedro de Palco", "Distrito de San Pedro de Palco", true, provinciaRepository.findByNombre("Lucanas").orElseThrow(() -> new RuntimeException("Provincia Lucanas no encontrada"))),
                new Distrito(null, "Sancos", "Distrito de Sancos", true, provinciaRepository.findByNombre("Lucanas").orElseThrow(() -> new RuntimeException("Provincia Lucanas no encontrada"))),
                new Distrito(null, "Santa Ana de Huaycahuacho", "Distrito de Santa Ana de Huaycahuacho", true, provinciaRepository.findByNombre("Lucanas").orElseThrow(() -> new RuntimeException("Provincia Lucanas no encontrada"))),
                new Distrito(null, "Santa Lucía", "Distrito de Santa Lucía", true, provinciaRepository.findByNombre("Lucanas").orElseThrow(() -> new RuntimeException("Provincia Lucanas no encontrada"))),

                // [Provincia de Parinacochas]
                new Distrito(null, "Coracora", "Distrito de Coracora", true, provinciaRepository.findByNombre("Parinacochas").orElseThrow(() -> new RuntimeException("Provincia Parinacochas no encontrada"))),
                new Distrito(null, "Chumpi", "Distrito de Chumpi", true, provinciaRepository.findByNombre("Parinacochas").orElseThrow(() -> new RuntimeException("Provincia Parinacochas no encontrada"))),
                new Distrito(null, "Coronel Castañeda", "Distrito de Coronel Castañeda", true, provinciaRepository.findByNombre("Parinacochas").orElseThrow(() -> new RuntimeException("Provincia Parinacochas no encontrada"))),
                new Distrito(null, "Pacapausa", "Distrito de Pacapausa", true, provinciaRepository.findByNombre("Parinacochas").orElseThrow(() -> new RuntimeException("Provincia Parinacochas no encontrada"))),
                new Distrito(null, "Pullo", "Distrito de Pullo", true, provinciaRepository.findByNombre("Parinacochas").orElseThrow(() -> new RuntimeException("Provincia Parinacochas no encontrada"))),
                new Distrito(null, "Puyusca", "Distrito de Puyusca", true, provinciaRepository.findByNombre("Parinacochas").orElseThrow(() -> new RuntimeException("Provincia Parinacochas no encontrada"))),
                new Distrito(null, "San Francisco de Ravacay", "Distrito de San Francisco de Ravacay", true, provinciaRepository.findByNombre("Parinacochas").orElseThrow(() -> new RuntimeException("Provincia Parinacochas no encontrada"))),
                new Distrito(null, "Upahuacho", "Distrito de Upahuacho", true, provinciaRepository.findByNombre("Parinacochas").orElseThrow(() -> new RuntimeException("Provincia Parinacochas no encontrada"))),

                // [Provincia de Páucar del Sara Sara]
                new Distrito(null, "Pausa", "Distrito de Pausa", true, provinciaRepository.findByNombre("Páucar del Sara Sara").orElseThrow(() -> new RuntimeException("Provincia Páucar del Sara Sara no encontrada"))),
                new Distrito(null, "Colta", "Distrito de Colta", true, provinciaRepository.findByNombre("Páucar del Sara Sara").orElseThrow(() -> new RuntimeException("Provincia Páucar del Sara Sara no encontrada"))),
                new Distrito(null, "Corculla", "Distrito de Corculla", true, provinciaRepository.findByNombre("Páucar del Sara Sara").orElseThrow(() -> new RuntimeException("Provincia Páucar del Sara Sara no encontrada"))),
                new Distrito(null, "Lampa", "Distrito de Lampa", true, provinciaRepository.findByNombre("Páucar del Sara Sara").orElseThrow(() -> new RuntimeException("Provincia Páucar del Sara Sara no encontrada"))),
                new Distrito(null, "Marcabamba", "Distrito de Marcabamba", true, provinciaRepository.findByNombre("Páucar del Sara Sara").orElseThrow(() -> new RuntimeException("Provincia Páucar del Sara Sara no encontrada"))),
                new Distrito(null, "Oyolo", "Distrito de Oyolo", true, provinciaRepository.findByNombre("Páucar del Sara Sara").orElseThrow(() -> new RuntimeException("Provincia Páucar del Sara Sara no encontrada"))),
                new Distrito(null, "Pararca", "Distrito de Pararca", true, provinciaRepository.findByNombre("Páucar del Sara Sara").orElseThrow(() -> new RuntimeException("Provincia Páucar del Sara Sara no encontrada"))),
                new Distrito(null, "San Javier de Alpabamba", "Distrito de San Javier de Alpabamba", true, provinciaRepository.findByNombre("Páucar del Sara Sara").orElseThrow(() -> new RuntimeException("Provincia Páucar del Sara Sara no encontrada"))),
                new Distrito(null, "San José de Ushua", "Distrito de San José de Ushua", true, provinciaRepository.findByNombre("Páucar del Sara Sara").orElseThrow(() -> new RuntimeException("Provincia Páucar del Sara Sara no encontrada"))),
                new Distrito(null, "Sara Sara", "Distrito de Sara Sara", true, provinciaRepository.findByNombre("Páucar del Sara Sara").orElseThrow(() -> new RuntimeException("Provincia Páucar del Sara Sara no encontrada"))),

                // [Provincia de Sucre]
                new Distrito(null, "Querobamba", "Distrito de Querobamba", true, provinciaRepository.findByNombre("Sucre").orElseThrow(() -> new RuntimeException("Provincia Sucre no encontrada"))),
                new Distrito(null, "Belén", "Distrito de Belén", true, provinciaRepository.findByNombre("Sucre").orElseThrow(() -> new RuntimeException("Provincia Sucre no encontrada"))),
                new Distrito(null, "Chalcos", "Distrito de Chalcos", true, provinciaRepository.findByNombre("Sucre").orElseThrow(() -> new RuntimeException("Provincia Sucre no encontrada"))),
                new Distrito(null, "Chilcayoc", "Distrito de Chilcayoc", true, provinciaRepository.findByNombre("Sucre").orElseThrow(() -> new RuntimeException("Provincia Sucre no encontrada"))),
                new Distrito(null, "Huacaña", "Distrito de Huacaña", true, provinciaRepository.findByNombre("Sucre").orElseThrow(() -> new RuntimeException("Provincia Sucre no encontrada"))),
                new Distrito(null, "Morcolla", "Distrito de Morcolla", true, provinciaRepository.findByNombre("Sucre").orElseThrow(() -> new RuntimeException("Provincia Sucre no encontrada"))),
                new Distrito(null, "Paico", "Distrito de Paico", true, provinciaRepository.findByNombre("Sucre").orElseThrow(() -> new RuntimeException("Provincia Sucre no encontrada"))),
                new Distrito(null, "San Pedro de Larcay", "Distrito de San Pedro de Larcay", true, provinciaRepository.findByNombre("Sucre").orElseThrow(() -> new RuntimeException("Provincia Sucre no encontrada"))),
                new Distrito(null, "San Salvador de Quije", "Distrito de San Salvador de Quije", true, provinciaRepository.findByNombre("Sucre").orElseThrow(() -> new RuntimeException("Provincia Sucre no encontrada"))),
                new Distrito(null, "Santiago de Paucaray", "Distrito de Santiago de Paucaray", true, provinciaRepository.findByNombre("Sucre").orElseThrow(() -> new RuntimeException("Provincia Sucre no encontrada"))),
                new Distrito(null, "Soras", "Distrito de Soras", true, provinciaRepository.findByNombre("Sucre").orElseThrow(() -> new RuntimeException("Provincia Sucre no encontrada"))),

                // [Provincia de Víctor Fajardo]
                new Distrito(null, "Huancapi", "Distrito de Huancapi", true, provinciaRepository.findByNombre("Víctor Fajardo").orElseThrow(() -> new RuntimeException("Provincia Víctor Fajardo no encontrada"))),
                new Distrito(null, "Alcamenca", "Distrito de Alcamenca", true, provinciaRepository.findByNombre("Víctor Fajardo").orElseThrow(() -> new RuntimeException("Provincia Víctor Fajardo no encontrada"))),
                new Distrito(null, "Apongo", "Distrito de Apongo", true, provinciaRepository.findByNombre("Víctor Fajardo").orElseThrow(() -> new RuntimeException("Provincia Víctor Fajardo no encontrada"))),
                new Distrito(null, "Asquipata", "Distrito de Asquipata", true, provinciaRepository.findByNombre("Víctor Fajardo").orElseThrow(() -> new RuntimeException("Provincia Víctor Fajardo no encontrada"))),
                new Distrito(null, "Canaria", "Distrito de Canaria", true, provinciaRepository.findByNombre("Víctor Fajardo").orElseThrow(() -> new RuntimeException("Provincia Víctor Fajardo no encontrada"))),
                new Distrito(null, "Cayara", "Distrito de Cayara", true, provinciaRepository.findByNombre("Víctor Fajardo").orElseThrow(() -> new RuntimeException("Provincia Víctor Fajardo no encontrada"))),
                new Distrito(null, "Colca", "Distrito de Colca", true, provinciaRepository.findByNombre("Víctor Fajardo").orElseThrow(() -> new RuntimeException("Provincia Víctor Fajardo no encontrada"))),
                new Distrito(null, "Huamanquiquia", "Distrito de Huamanquiquia", true, provinciaRepository.findByNombre("Víctor Fajardo").orElseThrow(() -> new RuntimeException("Provincia Víctor Fajardo no encontrada"))),
                new Distrito(null, "Huancaraylla", "Distrito de Huancaraylla", true, provinciaRepository.findByNombre("Víctor Fajardo").orElseThrow(() -> new RuntimeException("Provincia Víctor Fajardo no encontrada"))),
                new Distrito(null, "Huaya", "Distrito de Huaya", true, provinciaRepository.findByNombre("Víctor Fajardo").orElseThrow(() -> new RuntimeException("Provincia Víctor Fajardo no encontrada"))),
                new Distrito(null, "Sarhua", "Distrito de Sarhua", true, provinciaRepository.findByNombre("Víctor Fajardo").orElseThrow(() -> new RuntimeException("Provincia Víctor Fajardo no encontrada"))),
                new Distrito(null, "Vilcanchos", "Distrito de Vilcanchos", true, provinciaRepository.findByNombre("Víctor Fajardo").orElseThrow(() -> new RuntimeException("Provincia Víctor Fajardo no encontrada"))),

                // [Provincia de Vilcas Huamán]
                new Distrito(null, "Vilcas Huamán", "Distrito de Vilcas Huamán", true, provinciaRepository.findByNombre("Vilcas Huamán").orElseThrow(() -> new RuntimeException("Provincia Vilcas Huamán no encontrada"))),
                new Distrito(null, "Accomarca", "Distrito de Accomarca", true, provinciaRepository.findByNombre("Vilcas Huamán").orElseThrow(() -> new RuntimeException("Provincia Vilcas Huamán no encontrada"))),
                new Distrito(null, "Carhuanca", "Distrito de Carhuanca", true, provinciaRepository.findByNombre("Vilcas Huamán").orElseThrow(() -> new RuntimeException("Provincia Vilcas Huamán no encontrada"))),
                new Distrito(null, "Concepción", "Distrito de Concepción", true, provinciaRepository.findByNombre("Vilcas Huamán").orElseThrow(() -> new RuntimeException("Provincia Vilcas Huamán no encontrada"))),
                new Distrito(null, "Huambalpa", "Distrito de Huambalpa", true, provinciaRepository.findByNombre("Vilcas Huamán").orElseThrow(() -> new RuntimeException("Provincia Vilcas Huamán no encontrada"))),
                new Distrito(null, "Independencia", "Distrito de Independencia", true, provinciaRepository.findByNombre("Vilcas Huamán").orElseThrow(() -> new RuntimeException("Provincia Vilcas Huamán no encontrada"))),
                new Distrito(null, "Saurama", "Distrito de Saurama", true, provinciaRepository.findByNombre("Vilcas Huamán").orElseThrow(() -> new RuntimeException("Provincia Vilcas Huamán no encontrada"))),
                new Distrito(null, "Vischongo", "Distrito de Vischongo", true, provinciaRepository.findByNombre("Vilcas Huamán").orElseThrow(() -> new RuntimeException("Provincia Vilcas Huamán no encontrada")))
        );
        distritoRepository.saveAll(distritos);
    }

    private void seedDistritosCajamarca() {
        List<Distrito> distritos = Arrays.asList(
                // Cajamarca
                // [Provincia de Cajamarca]
                new Distrito(null, "Cajamarca", "Distrito de Cajamarca", true, provinciaRepository.findByNombre("Cajamarca").orElseThrow(() -> new RuntimeException("Provincia Cajamarca no encontrada"))),
                new Distrito(null, "Asunción", "Distrito de Asunción", true, provinciaRepository.findByNombre("Cajamarca").orElseThrow(() -> new RuntimeException("Provincia Cajamarca no encontrada"))),
                new Distrito(null, "Chetilla", "Distrito de Chetilla", true, provinciaRepository.findByNombre("Cajamarca").orElseThrow(() -> new RuntimeException("Provincia Cajamarca no encontrada"))),
                new Distrito(null, "Cospán", "Distrito de Cospán", true, provinciaRepository.findByNombre("Cajamarca").orElseThrow(() -> new RuntimeException("Provincia Cajamarca no encontrada"))),
                new Distrito(null, "Encañada", "Distrito de Encañada", true, provinciaRepository.findByNombre("Cajamarca").orElseThrow(() -> new RuntimeException("Provincia Cajamarca no encontrada"))),
                new Distrito(null, "Jesús", "Distrito de Jesús", true, provinciaRepository.findByNombre("Cajamarca").orElseThrow(() -> new RuntimeException("Provincia Cajamarca no encontrada"))),
                new Distrito(null, "Llacanora", "Distrito de Llacanora", true, provinciaRepository.findByNombre("Cajamarca").orElseThrow(() -> new RuntimeException("Provincia Cajamarca no encontrada"))),
                new Distrito(null, "Los Baños del Inca", "Distrito de Los Baños del Inca", true, provinciaRepository.findByNombre("Cajamarca").orElseThrow(() -> new RuntimeException("Provincia Cajamarca no encontrada"))),
                new Distrito(null, "Magdalena", "Distrito de Magdalena", true, provinciaRepository.findByNombre("Cajamarca").orElseThrow(() -> new RuntimeException("Provincia Cajamarca no encontrada"))),
                new Distrito(null, "Matara", "Distrito de Matara", true, provinciaRepository.findByNombre("Cajamarca").orElseThrow(() -> new RuntimeException("Provincia Cajamarca no encontrada"))),
                new Distrito(null, "Namora", "Distrito de Namora", true, provinciaRepository.findByNombre("Cajamarca").orElseThrow(() -> new RuntimeException("Provincia Cajamarca no encontrada"))),
                new Distrito(null, "San Juan", "Distrito de San Juan", true, provinciaRepository.findByNombre("Cajamarca").orElseThrow(() -> new RuntimeException("Provincia Cajamarca no encontrada"))),

                // [Provincia de Cajabamba]
                new Distrito(null, "Cajabamba", "Distrito de Cajabamba", true, provinciaRepository.findByNombre("Cajabamba").orElseThrow(() -> new RuntimeException("Provincia Cajabamba no encontrada"))),
                new Distrito(null, "Cachachi", "Distrito de Cachachi", true, provinciaRepository.findByNombre("Cajabamba").orElseThrow(() -> new RuntimeException("Provincia Cajabamba no encontrada"))),
                new Distrito(null, "Condebamba", "Distrito de Condebamba", true, provinciaRepository.findByNombre("Cajabamba").orElseThrow(() -> new RuntimeException("Provincia Cajabamba no encontrada"))),
                new Distrito(null, "Sitacocha", "Distrito de Sitacocha", true, provinciaRepository.findByNombre("Cajabamba").orElseThrow(() -> new RuntimeException("Provincia Cajabamba no encontrada"))),

                // [Provincia de Celendín]
                new Distrito(null, "Celendín", "Distrito de Celendín", true, provinciaRepository.findByNombre("Celendín").orElseThrow(() -> new RuntimeException("Provincia Celendín no encontrada"))),
                new Distrito(null, "Chumuch", "Distrito de Chumuch", true, provinciaRepository.findByNombre("Celendín").orElseThrow(() -> new RuntimeException("Provincia Celendín no encontrada"))),
                new Distrito(null, "Cortegana", "Distrito de Cortegana", true, provinciaRepository.findByNombre("Celendín").orElseThrow(() -> new RuntimeException("Provincia Celendín no encontrada"))),
                new Distrito(null, "Huasmin", "Distrito de Huasmin", true, provinciaRepository.findByNombre("Celendín").orElseThrow(() -> new RuntimeException("Provincia Celendín no encontrada"))),
                new Distrito(null, "Jorge Chávez", "Distrito de Jorge Chávez", true, provinciaRepository.findByNombre("Celendín").orElseThrow(() -> new RuntimeException("Provincia Celendín no encontrada"))),
                new Distrito(null, "José Gálvez", "Distrito de José Gálvez", true, provinciaRepository.findByNombre("Celendín").orElseThrow(() -> new RuntimeException("Provincia Celendín no encontrada"))),
                new Distrito(null, "Miguel Iglesias", "Distrito de Miguel Iglesias", true, provinciaRepository.findByNombre("Celendín").orElseThrow(() -> new RuntimeException("Provincia Celendín no encontrada"))),
                new Distrito(null, "Oxamarca", "Distrito de Oxamarca", true, provinciaRepository.findByNombre("Celendín").orElseThrow(() -> new RuntimeException("Provincia Celendín no encontrada"))),
                new Distrito(null, "Sorochuco", "Distrito de Sorochuco", true, provinciaRepository.findByNombre("Celendín").orElseThrow(() -> new RuntimeException("Provincia Celendín no encontrada"))),
                new Distrito(null, "Sucre", "Distrito de Sucre", true, provinciaRepository.findByNombre("Celendín").orElseThrow(() -> new RuntimeException("Provincia Celendín no encontrada"))),
                new Distrito(null, "Utco", "Distrito de Utco", true, provinciaRepository.findByNombre("Celendín").orElseThrow(() -> new RuntimeException("Provincia Celendín no encontrada"))),
                new Distrito(null, "La Libertad de Pallán", "Distrito de La Libertad de Pallán", true, provinciaRepository.findByNombre("Celendín").orElseThrow(() -> new RuntimeException("Provincia Celendín no encontrada"))),


                // [Provincia de Chota]
                new Distrito(null, "Chota", "Distrito de Chota", true, provinciaRepository.findByNombre("Chota").orElseThrow(() -> new RuntimeException("Provincia Chota no encontrada"))),
                new Distrito(null, "Anguía", "Distrito de Anguía", true, provinciaRepository.findByNombre("Chota").orElseThrow(() -> new RuntimeException("Provincia Chota no encontrada"))),
                new Distrito(null, "Chadin", "Distrito de Chadin", true, provinciaRepository.findByNombre("Chota").orElseThrow(() -> new RuntimeException("Provincia Chota no encontrada"))),
                new Distrito(null, "Chiguirip", "Distrito de Chiguirip", true, provinciaRepository.findByNombre("Chota").orElseThrow(() -> new RuntimeException("Provincia Chota no encontrada"))),
                new Distrito(null, "Chimban", "Distrito de Chimban", true, provinciaRepository.findByNombre("Chota").orElseThrow(() -> new RuntimeException("Provincia Chota no encontrada"))),
                new Distrito(null, "Choropampa", "Distrito de Choropampa", true, provinciaRepository.findByNombre("Chota").orElseThrow(() -> new RuntimeException("Provincia Chota no encontrada"))),
                new Distrito(null, "Cochabamba", "Distrito de Cochabamba", true, provinciaRepository.findByNombre("Chota").orElseThrow(() -> new RuntimeException("Provincia Chota no encontrada"))),
                new Distrito(null, "Conchan", "Distrito de Conchan", true, provinciaRepository.findByNombre("Chota").orElseThrow(() -> new RuntimeException("Provincia Chota no encontrada"))),
                new Distrito(null, "Huambos", "Distrito de Huambos", true, provinciaRepository.findByNombre("Chota").orElseThrow(() -> new RuntimeException("Provincia Chota no encontrada"))),
                new Distrito(null, "Lajas", "Distrito de Lajas", true, provinciaRepository.findByNombre("Chota").orElseThrow(() -> new RuntimeException("Provincia Chota no encontrada"))),
                new Distrito(null, "Llama", "Distrito de Llama", true, provinciaRepository.findByNombre("Chota").orElseThrow(() -> new RuntimeException("Provincia Chota no encontrada"))),
                new Distrito(null, "Miracosta", "Distrito de Miracosta", true, provinciaRepository.findByNombre("Chota").orElseThrow(() -> new RuntimeException("Provincia Chota no encontrada"))),
                new Distrito(null, "Paccha", "Distrito de Paccha", true, provinciaRepository.findByNombre("Chota").orElseThrow(() -> new RuntimeException("Provincia Chota no encontrada"))),
                new Distrito(null, "Pión", "Distrito de Pión", true, provinciaRepository.findByNombre("Chota").orElseThrow(() -> new RuntimeException("Provincia Chota no encontrada"))),
                new Distrito(null, "Querocoto", "Distrito de Querocoto", true, provinciaRepository.findByNombre("Chota").orElseThrow(() -> new RuntimeException("Provincia Chota no encontrada"))),
                new Distrito(null, "San Juan de Licupis", "Distrito de San Juan de Licupis", true, provinciaRepository.findByNombre("Chota").orElseThrow(() -> new RuntimeException("Provincia Chota no encontrada"))),
                new Distrito(null, "Tacabamba", "Distrito de Tacabamba", true, provinciaRepository.findByNombre("Chota").orElseThrow(() -> new RuntimeException("Provincia Chota no encontrada"))),
                new Distrito(null, "Tocmoche", "Distrito de Tocmoche", true, provinciaRepository.findByNombre("Chota").orElseThrow(() -> new RuntimeException("Provincia Chota no encontrada"))),
                new Distrito(null, "Chalamarca", "Distrito de Chalamarca", true, provinciaRepository.findByNombre("Chota").orElseThrow(() -> new RuntimeException("Provincia Chota no encontrada"))),

                // [Provincia de Contumazá]
                new Distrito(null, "Contumazá", "Distrito de Contumazá", true, provinciaRepository.findByNombre("Contumazá").orElseThrow(() -> new RuntimeException("Provincia Contumazá no encontrada"))),
                new Distrito(null, "Chilete", "Distrito de Chilete", true, provinciaRepository.findByNombre("Contumazá").orElseThrow(() -> new RuntimeException("Provincia Contumazá no encontrada"))),
                new Distrito(null, "Cupisnique", "Distrito de Cupisnique", true, provinciaRepository.findByNombre("Contumazá").orElseThrow(() -> new RuntimeException("Provincia Contumazá no encontrada"))),
                new Distrito(null, "Guzmango", "Distrito de Guzmango", true, provinciaRepository.findByNombre("Contumazá").orElseThrow(() -> new RuntimeException("Provincia Contumazá no encontrada"))),
                new Distrito(null, "San Benito", "Distrito de San Benito", true, provinciaRepository.findByNombre("Contumazá").orElseThrow(() -> new RuntimeException("Provincia Contumazá no encontrada"))),
                new Distrito(null, "Santa Cruz de Toledo", "Distrito de Santa Cruz de Toledo", true, provinciaRepository.findByNombre("Contumazá").orElseThrow(() -> new RuntimeException("Provincia Contumazá no encontrada"))),
                new Distrito(null, "Tantará", "Distrito de Tantará", true, provinciaRepository.findByNombre("Contumazá").orElseThrow(() -> new RuntimeException("Provincia Contumazá no encontrada"))),
                new Distrito(null, "Yonan", "Distrito de Yonan", true, provinciaRepository.findByNombre("Contumazá").orElseThrow(() -> new RuntimeException("Provincia Contumazá no encontrada"))),

                // [Provincia de Cutervo]
                new Distrito(null, "Cutervo", "Distrito de Cutervo", true, provinciaRepository.findByNombre("Cutervo").orElseThrow(() -> new RuntimeException("Provincia Cutervo no encontrada"))),
                new Distrito(null, "Callayuc", "Distrito de Callayuc", true, provinciaRepository.findByNombre("Cutervo").orElseThrow(() -> new RuntimeException("Provincia Cutervo no encontrada"))),
                new Distrito(null, "Choros", "Distrito de Choros", true, provinciaRepository.findByNombre("Cutervo").orElseThrow(() -> new RuntimeException("Provincia Cutervo no encontrada"))),
                new Distrito(null, "Cujillo", "Distrito de Cujillo", true, provinciaRepository.findByNombre("Cutervo").orElseThrow(() -> new RuntimeException("Provincia Cutervo no encontrada"))),
                new Distrito(null, "La Ramada", "Distrito de La Ramada", true, provinciaRepository.findByNombre("Cutervo").orElseThrow(() -> new RuntimeException("Provincia Cutervo no encontrada"))),
                new Distrito(null, "Pimpincos", "Distrito de Pimpincos", true, provinciaRepository.findByNombre("Cutervo").orElseThrow(() -> new RuntimeException("Provincia Cutervo no encontrada"))),
                new Distrito(null, "Querecotillo", "Distrito de Querecotillo", true, provinciaRepository.findByNombre("Cutervo").orElseThrow(() -> new RuntimeException("Provincia Cutervo no encontrada"))),
                new Distrito(null, "San Andrés de Cutervo", "Distrito de San Andrés de Cutervo", true, provinciaRepository.findByNombre("Cutervo").orElseThrow(() -> new RuntimeException("Provincia Cutervo no encontrada"))),
                new Distrito(null, "San Juan de Cutervo", "Distrito de San Juan de Cutervo", true, provinciaRepository.findByNombre("Cutervo").orElseThrow(() -> new RuntimeException("Provincia Cutervo no encontrada"))),
                new Distrito(null, "San Luis de Lucma", "Distrito de San Luis de Lucma", true, provinciaRepository.findByNombre("Cutervo").orElseThrow(() -> new RuntimeException("Provincia Cutervo no encontrada"))),
                new Distrito(null, "Santa Cruz", "Distrito de Santa Cruz", true, provinciaRepository.findByNombre("Cutervo").orElseThrow(() -> new RuntimeException("Provincia Cutervo no encontrada"))),
                new Distrito(null, "Santo Domingo de la Capilla", "Distrito de Santo Domingo de la Capilla", true, provinciaRepository.findByNombre("Cutervo").orElseThrow(() -> new RuntimeException("Provincia Cutervo no encontrada"))),
                new Distrito(null, "Santo Tomás", "Distrito de Santo Tomás", true, provinciaRepository.findByNombre("Cutervo").orElseThrow(() -> new RuntimeException("Provincia Cutervo no encontrada"))),
                new Distrito(null, "Sócota", "Distrito de Sócota", true, provinciaRepository.findByNombre("Cutervo").orElseThrow(() -> new RuntimeException("Provincia Cutervo no encontrada"))),
                new Distrito(null, "Toribio Casanova", "Distrito de Toribio Casanova", true, provinciaRepository.findByNombre("Cutervo").orElseThrow(() -> new RuntimeException("Provincia Cutervo no encontrada"))),

                // [Provincia de Hualgayoc]
                new Distrito(null, "Bambamarca", "Distrito de Bambamarca", true, provinciaRepository.findByNombre("Hualgayoc").orElseThrow(() -> new RuntimeException("Provincia Hualgayoc no encontrada"))),
                new Distrito(null, "Chugur", "Distrito de Chugur", true, provinciaRepository.findByNombre("Hualgayoc").orElseThrow(() -> new RuntimeException("Provincia Hualgayoc no encontrada"))),
                new Distrito(null, "Hualgayoc", "Distrito de Hualgayoc", true, provinciaRepository.findByNombre("Hualgayoc").orElseThrow(() -> new RuntimeException("Provincia Hualgayoc no encontrada"))),

                // [Provincia de Jaén]
                new Distrito(null, "Jaén", "Distrito de Jaén", true, provinciaRepository.findByNombre("Jaén").orElseThrow(() -> new RuntimeException("Provincia Jaén no encontrada"))),
                new Distrito(null, "Bellavista", "Distrito de Bellavista", true, provinciaRepository.findByNombre("Jaén").orElseThrow(() -> new RuntimeException("Provincia Jaén no encontrada"))),
                new Distrito(null, "Chontali", "Distrito de Chontali", true, provinciaRepository.findByNombre("Jaén").orElseThrow(() -> new RuntimeException("Provincia Jaén no encontrada"))),
                new Distrito(null, "Colasay", "Distrito de Colasay", true, provinciaRepository.findByNombre("Jaén").orElseThrow(() -> new RuntimeException("Provincia Jaén no encontrada"))),
                new Distrito(null, "Huabal", "Distrito de Huabal", true, provinciaRepository.findByNombre("Jaén").orElseThrow(() -> new RuntimeException("Provincia Jaén no encontrada"))),
                new Distrito(null, "Las Pirias", "Distrito de Las Pirias", true, provinciaRepository.findByNombre("Jaén").orElseThrow(() -> new RuntimeException("Provincia Jaén no encontrada"))),
                new Distrito(null, "Pomahuaca", "Distrito de Pomahuaca", true, provinciaRepository.findByNombre("Jaén").orElseThrow(() -> new RuntimeException("Provincia Jaén no encontrada"))),
                new Distrito(null, "Pucará", "Distrito de Pucará", true, provinciaRepository.findByNombre("Jaén").orElseThrow(() -> new RuntimeException("Provincia Jaén no encontrada"))),
                new Distrito(null, "Sallique", "Distrito de Sallique", true, provinciaRepository.findByNombre("Jaén").orElseThrow(() -> new RuntimeException("Provincia Jaén no encontrada"))),
                new Distrito(null, "San Felipe", "Distrito de San Felipe", true, provinciaRepository.findByNombre("Jaén").orElseThrow(() -> new RuntimeException("Provincia Jaén no encontrada"))),
                new Distrito(null, "San José del Alto", "Distrito de San José del Alto", true, provinciaRepository.findByNombre("Jaén").orElseThrow(() -> new RuntimeException("Provincia Jaén no encontrada"))),
                new Distrito(null, "Santa Rosa", "Distrito de Santa Rosa", true, provinciaRepository.findByNombre("Jaén").orElseThrow(() -> new RuntimeException("Provincia Jaén no encontrada"))),

                // [Provincia de San Ignacio]
                new Distrito(null, "San Ignacio", "Distrito de San Ignacio", true, provinciaRepository.findByNombre("San Ignacio").orElseThrow(() -> new RuntimeException("Provincia San Ignacio no encontrada"))),
                new Distrito(null, "Chirinos", "Distrito de Chirinos", true, provinciaRepository.findByNombre("San Ignacio").orElseThrow(() -> new RuntimeException("Provincia San Ignacio no encontrada"))),
                new Distrito(null, "Huarango", "Distrito de Huarango", true, provinciaRepository.findByNombre("San Ignacio").orElseThrow(() -> new RuntimeException("Provincia San Ignacio no encontrada"))),
                new Distrito(null, "La Coipa", "Distrito de La Coipa", true, provinciaRepository.findByNombre("San Ignacio").orElseThrow(() -> new RuntimeException("Provincia San Ignacio no encontrada"))),
                new Distrito(null, "Namballe", "Distrito de Namballe", true, provinciaRepository.findByNombre("San Ignacio").orElseThrow(() -> new RuntimeException("Provincia San Ignacio no encontrada"))),
                new Distrito(null, "San José de Lourdes", "Distrito de San José de Lourdes", true, provinciaRepository.findByNombre("San Ignacio").orElseThrow(() -> new RuntimeException("Provincia San Ignacio no encontrada"))),
                new Distrito(null, "Tabaconas", "Distrito de Tabaconas", true, provinciaRepository.findByNombre("San Ignacio").orElseThrow(() -> new RuntimeException("Provincia San Ignacio no encontrada"))),

                // [Provincia de San Marcos]
                new Distrito(null, "Pedro Gálvez", "Distrito de Pedro Gálvez", true, provinciaRepository.findByNombre("San Marcos").orElseThrow(() -> new RuntimeException("Provincia San Marcos no encontrada"))),
                new Distrito(null, "Chancay", "Distrito de Chancay", true, provinciaRepository.findByNombre("San Marcos").orElseThrow(() -> new RuntimeException("Provincia San Marcos no encontrada"))),
                new Distrito(null, "Eduardo Villanueva", "Distrito de Eduardo Villanueva", true, provinciaRepository.findByNombre("San Marcos").orElseThrow(() -> new RuntimeException("Provincia San Marcos no encontrada"))),
                new Distrito(null, "Gregorio Pita", "Distrito de Gregorio Pita", true, provinciaRepository.findByNombre("San Marcos").orElseThrow(() -> new RuntimeException("Provincia San Marcos no encontrada"))),
                new Distrito(null, "Ichocán", "Distrito de Ichocán", true, provinciaRepository.findByNombre("San Marcos").orElseThrow(() -> new RuntimeException("Provincia San Marcos no encontrada"))),
                new Distrito(null, "José Manuel Quiroz", "Distrito de José Manuel Quiroz", true, provinciaRepository.findByNombre("San Marcos").orElseThrow(() -> new RuntimeException("Provincia San Marcos no encontrada"))),
                new Distrito(null, "José Sabogal", "Distrito de José Sabogal", true, provinciaRepository.findByNombre("San Marcos").orElseThrow(() -> new RuntimeException("Provincia San Marcos no encontrada"))),

                // [Provincia de San Miguel]
                new Distrito(null, "San Miguel", "Distrito de San Miguel", true, provinciaRepository.findByNombre("San Miguel").orElseThrow(() -> new RuntimeException("Provincia San Miguel no encontrada"))),
                new Distrito(null, "Bolívar", "Distrito de Bolívar", true, provinciaRepository.findByNombre("San Miguel").orElseThrow(() -> new RuntimeException("Provincia San Miguel no encontrada"))),
                new Distrito(null, "Calquís", "Distrito de Calquís", true, provinciaRepository.findByNombre("San Miguel").orElseThrow(() -> new RuntimeException("Provincia San Miguel no encontrada"))),
                new Distrito(null, "Catilluc", "Distrito de Catilluc", true, provinciaRepository.findByNombre("San Miguel").orElseThrow(() -> new RuntimeException("Provincia San Miguel no encontrada"))),
                new Distrito(null, "El Prado", "Distrito de El Prado", true, provinciaRepository.findByNombre("San Miguel").orElseThrow(() -> new RuntimeException("Provincia San Miguel no encontrada"))),
                new Distrito(null, "La Florida", "Distrito de La Florida", true, provinciaRepository.findByNombre("San Miguel").orElseThrow(() -> new RuntimeException("Provincia San Miguel no encontrada"))),
                new Distrito(null, "Llapa", "Distrito de Llapa", true, provinciaRepository.findByNombre("San Miguel").orElseThrow(() -> new RuntimeException("Provincia San Miguel no encontrada"))),
                new Distrito(null, "Nanchoc", "Distrito de Nanchoc", true, provinciaRepository.findByNombre("San Miguel").orElseThrow(() -> new RuntimeException("Provincia San Miguel no encontrada"))),
                new Distrito(null, "Niepos", "Distrito de Niepos", true, provinciaRepository.findByNombre("San Miguel").orElseThrow(() -> new RuntimeException("Provincia San Miguel no encontrada"))),
                new Distrito(null, "San Gregorio", "Distrito de San Gregorio", true, provinciaRepository.findByNombre("San Miguel").orElseThrow(() -> new RuntimeException("Provincia San Miguel no encontrada"))),
                new Distrito(null, "San Silvestre de Cochan", "Distrito de San Silvestre de Cochan", true, provinciaRepository.findByNombre("San Miguel").orElseThrow(() -> new RuntimeException("Provincia San Miguel no encontrada"))),
                new Distrito(null, "Tongod", "Distrito de Tongod", true, provinciaRepository.findByNombre("San Miguel").orElseThrow(() -> new RuntimeException("Provincia San Miguel no encontrada"))),
                new Distrito(null, "Unión Agua Blanca", "Distrito de Unión Agua Blanca", true, provinciaRepository.findByNombre("San Miguel").orElseThrow(() -> new RuntimeException("Provincia San Miguel no encontrada"))),

                // [Provincia de San Pablo]
                new Distrito(null, "San Pablo", "Distrito de San Pablo", true, provinciaRepository.findByNombre("San Pablo").orElseThrow(() -> new RuntimeException("Provincia San Pablo no encontrada"))),
                new Distrito(null, "San Bernardino", "Distrito de San Bernardino", true, provinciaRepository.findByNombre("San Pablo").orElseThrow(() -> new RuntimeException("Provincia San Pablo no encontrada"))),
                new Distrito(null, "San Luis de Lucma", "Distrito de San Luis de Lucma", true, provinciaRepository.findByNombre("San Pablo").orElseThrow(() -> new RuntimeException("Provincia San Pablo no encontrada"))),
                new Distrito(null, "Tumbaden", "Distrito de Tumbaden", true, provinciaRepository.findByNombre("San Pablo").orElseThrow(() -> new RuntimeException("Provincia San Pablo no encontrada"))),

                // [Provincia de Santa Cruz]
                new Distrito(null, "Santa Cruz", "Distrito de Santa Cruz", true, provinciaRepository.findByNombre("Santa Cruz").orElseThrow(() -> new RuntimeException("Provincia Santa Cruz no encontrada"))),
                new Distrito(null, "Andabamba", "Distrito de Andabamba", true, provinciaRepository.findByNombre("Santa Cruz").orElseThrow(() -> new RuntimeException("Provincia Santa Cruz no encontrada"))),
                new Distrito(null, "Catache", "Distrito de Catache", true, provinciaRepository.findByNombre("Santa Cruz").orElseThrow(() -> new RuntimeException("Provincia Santa Cruz no encontrada"))),
                new Distrito(null, "Chancaybaños", "Distrito de Chancaybaños", true, provinciaRepository.findByNombre("Santa Cruz").orElseThrow(() -> new RuntimeException("Provincia Santa Cruz no encontrada"))),
                new Distrito(null, "La Esperanza", "Distrito de La Esperanza", true, provinciaRepository.findByNombre("Santa Cruz").orElseThrow(() -> new RuntimeException("Provincia Santa Cruz no encontrada"))),
                new Distrito(null, "Ninabamba", "Distrito de Ninabamba", true, provinciaRepository.findByNombre("Santa Cruz").orElseThrow(() -> new RuntimeException("Provincia Santa Cruz no encontrada"))),
                new Distrito(null, "Pulan", "Distrito de Pulan", true, provinciaRepository.findByNombre("Santa Cruz").orElseThrow(() -> new RuntimeException("Provincia Santa Cruz no encontrada"))),
                new Distrito(null, "Saucepampa", "Distrito de Saucepampa", true, provinciaRepository.findByNombre("Santa Cruz").orElseThrow(() -> new RuntimeException("Provincia Santa Cruz no encontrada"))),
                new Distrito(null, "Sexi", "Distrito de Sexi", true, provinciaRepository.findByNombre("Santa Cruz").orElseThrow(() -> new RuntimeException("Provincia Santa Cruz no encontrada"))),
                new Distrito(null, "Uticyacu", "Distrito de Uticyacu", true, provinciaRepository.findByNombre("Santa Cruz").orElseThrow(() -> new RuntimeException("Provincia Santa Cruz no encontrada"))),
                new Distrito(null, "Yauyucan", "Distrito de Yauyucan", true, provinciaRepository.findByNombre("Santa Cruz").orElseThrow(() -> new RuntimeException("Provincia Santa Cruz no encontrada")))
        );
        distritoRepository.saveAll(distritos);
    }

    private void seedDistritosCallao() {
        List<Distrito> distritos = Arrays.asList(
                // Callao
                // [Provincia de Callao]
                new Distrito(null, "Callao", "Distrito de Callao", true, provinciaRepository.findByNombre("Callao").orElseThrow(() -> new RuntimeException("Provincia Callao no encontrada"))),
                new Distrito(null, "Bellavista", "Distrito de Bellavista", true, provinciaRepository.findByNombre("Callao").orElseThrow(() -> new RuntimeException("Provincia Callao no encontrada"))),
                new Distrito(null, "Carmen de la Legua Reynoso", "Distrito de Carmen de la Legua Reynoso", true, provinciaRepository.findByNombre("Callao").orElseThrow(() -> new RuntimeException("Provincia Callao no encontrada"))),
                new Distrito(null, "La Perla", "Distrito de La Perla", true, provinciaRepository.findByNombre("Callao").orElseThrow(() -> new RuntimeException("Provincia Callao no encontrada"))),
                new Distrito(null, "La Punta", "Distrito de La Punta", true, provinciaRepository.findByNombre("Callao").orElseThrow(() -> new RuntimeException("Provincia Callao no encontrada"))),
                new Distrito(null, "Ventanilla", "Distrito de Ventanilla", true, provinciaRepository.findByNombre("Callao").orElseThrow(() -> new RuntimeException("Provincia Callao no encontrada"))),
                new Distrito(null, "Mi Perú", "Distrito de Mi Perú", true, provinciaRepository.findByNombre("Callao").orElseThrow(() -> new RuntimeException("Provincia Callao no encontrada")))
        );
        distritoRepository.saveAll(distritos);
    }

    private void seedDistritosCusco() {
        List<Distrito> distritos = Arrays.asList(
                // Cusco
                // [Provincia de Cusco]
                new Distrito(null, "Cusco", "Distrito de Cusco", true, provinciaRepository.findByNombre("Cusco").orElseThrow(() -> new RuntimeException("Provincia Cusco no encontrada"))),
                new Distrito(null, "Ccorca", "Distrito de Ccorca", true, provinciaRepository.findByNombre("Cusco").orElseThrow(() -> new RuntimeException("Provincia Cusco no encontrada"))),
                new Distrito(null, "Poroy", "Distrito de Poroy", true, provinciaRepository.findByNombre("Cusco").orElseThrow(() -> new RuntimeException("Provincia Cusco no encontrada"))),
                new Distrito(null, "San Jerónimo", "Distrito de San Jerónimo", true, provinciaRepository.findByNombre("Cusco").orElseThrow(() -> new RuntimeException("Provincia Cusco no encontrada"))),
                new Distrito(null, "San Sebastián", "Distrito de San Sebastián", true, provinciaRepository.findByNombre("Cusco").orElseThrow(() -> new RuntimeException("Provincia Cusco no encontrada"))),
                new Distrito(null, "Santiago", "Distrito de Santiago", true, provinciaRepository.findByNombre("Cusco").orElseThrow(() -> new RuntimeException("Provincia Cusco no encontrada"))),
                new Distrito(null, "Saylla", "Distrito de Saylla", true, provinciaRepository.findByNombre("Cusco").orElseThrow(() -> new RuntimeException("Provincia Cusco no encontrada"))),
                new Distrito(null, "Wanchaq", "Distrito de Wanchaq", true, provinciaRepository.findByNombre("Cusco").orElseThrow(() -> new RuntimeException("Provincia Cusco no encontrada"))),

                // [Provincia de Acomayo]
                new Distrito(null, "Acomayo", "Distrito de Acomayo", true, provinciaRepository.findByNombre("Acomayo").orElseThrow(() -> new RuntimeException("Provincia Acomayo no encontrada"))),
                new Distrito(null, "Acopia", "Distrito de Acopia", true, provinciaRepository.findByNombre("Acomayo").orElseThrow(() -> new RuntimeException("Provincia Acomayo no encontrada"))),
                new Distrito(null, "Acos", "Distrito de Acos", true, provinciaRepository.findByNombre("Acomayo").orElseThrow(() -> new RuntimeException("Provincia Acomayo no encontrada"))),
                new Distrito(null, "Mosoc Llacta", "Distrito de Mosoc Llacta", true, provinciaRepository.findByNombre("Acomayo").orElseThrow(() -> new RuntimeException("Provincia Acomayo no encontrada"))),
                new Distrito(null, "Pomacanchi", "Distrito de Pomacanchi", true, provinciaRepository.findByNombre("Acomayo").orElseThrow(() -> new RuntimeException("Provincia Acomayo no encontrada"))),
                new Distrito(null, "Rondocan", "Distrito de Rondocan", true, provinciaRepository.findByNombre("Acomayo").orElseThrow(() -> new RuntimeException("Provincia Acomayo no encontrada"))),
                new Distrito(null, "Sangarará", "Distrito de Sangarará", true, provinciaRepository.findByNombre("Acomayo").orElseThrow(() -> new RuntimeException("Provincia Acomayo no encontrada"))),

                // [Provincia de Anta]
                new Distrito(null, "Anta", "Distrito de Anta", true, provinciaRepository.findByNombre("Anta").orElseThrow(() -> new RuntimeException("Provincia Anta no encontrada"))),
                new Distrito(null, "Ancahuasi", "Distrito de Ancahuasi", true, provinciaRepository.findByNombre("Anta").orElseThrow(() -> new RuntimeException("Provincia Anta no encontrada"))),
                new Distrito(null, "Cachimayo", "Distrito de Cachimayo", true, provinciaRepository.findByNombre("Anta").orElseThrow(() -> new RuntimeException("Provincia Anta no encontrada"))),
                new Distrito(null, "Chinchaypujio", "Distrito de Chinchaypujio", true, provinciaRepository.findByNombre("Anta").orElseThrow(() -> new RuntimeException("Provincia Anta no encontrada"))),
                new Distrito(null, "Huarocondo", "Distrito de Huarocondo", true, provinciaRepository.findByNombre("Anta").orElseThrow(() -> new RuntimeException("Provincia Anta no encontrada"))),
                new Distrito(null, "Limatambo", "Distrito de Limatambo", true, provinciaRepository.findByNombre("Anta").orElseThrow(() -> new RuntimeException("Provincia Anta no encontrada"))),
                new Distrito(null, "Mollepata", "Distrito de Mollepata", true, provinciaRepository.findByNombre("Anta").orElseThrow(() -> new RuntimeException("Provincia Anta no encontrada"))),
                new Distrito(null, "Pucyura", "Distrito de Pucyura", true, provinciaRepository.findByNombre("Anta").orElseThrow(() -> new RuntimeException("Provincia Anta no encontrada"))),
                new Distrito(null, "Zurite", "Distrito de Zurite", true, provinciaRepository.findByNombre("Anta").orElseThrow(() -> new RuntimeException("Provincia Anta no encontrada"))),

                // [Provincia de Calca]
                new Distrito(null, "Calca", "Distrito de Calca", true, provinciaRepository.findByNombre("Calca").orElseThrow(() -> new RuntimeException("Provincia Calca no encontrada"))),
                new Distrito(null, "Coya", "Distrito de Coya", true, provinciaRepository.findByNombre("Calca").orElseThrow(() -> new RuntimeException("Provincia Calca no encontrada"))),
                new Distrito(null, "Lamay", "Distrito de Lamay", true, provinciaRepository.findByNombre("Calca").orElseThrow(() -> new RuntimeException("Provincia Calca no encontrada"))),
                new Distrito(null, "Lares", "Distrito de Lares", true, provinciaRepository.findByNombre("Calca").orElseThrow(() -> new RuntimeException("Provincia Calca no encontrada"))),
                new Distrito(null, "Pisac", "Distrito de Pisac", true, provinciaRepository.findByNombre("Calca").orElseThrow(() -> new RuntimeException("Provincia Calca no encontrada"))),
                new Distrito(null, "San Salvador", "Distrito de San Salvador", true, provinciaRepository.findByNombre("Calca").orElseThrow(() -> new RuntimeException("Provincia Calca no encontrada"))),
                new Distrito(null, "Taray", "Distrito de Taray", true, provinciaRepository.findByNombre("Calca").orElseThrow(() -> new RuntimeException("Provincia Calca no encontrada"))),
                new Distrito(null, "Yanatile", "Distrito de Yanatile", true, provinciaRepository.findByNombre("Calca").orElseThrow(() -> new RuntimeException("Provincia Calca no encontrada"))),

                // [Provincia de Canas]
                new Distrito(null, "Yanaoca", "Distrito de Yanaoca", true, provinciaRepository.findByNombre("Canas").orElseThrow(() -> new RuntimeException("Provincia Canas no encontrada"))),
                new Distrito(null, "Checca", "Distrito de Checca", true, provinciaRepository.findByNombre("Canas").orElseThrow(() -> new RuntimeException("Provincia Canas no encontrada"))),
                new Distrito(null, "Kunturkanki", "Distrito de Kunturkanki", true, provinciaRepository.findByNombre("Canas").orElseThrow(() -> new RuntimeException("Provincia Canas no encontrada"))),
                new Distrito(null, "Langui", "Distrito de Langui", true, provinciaRepository.findByNombre("Canas").orElseThrow(() -> new RuntimeException("Provincia Canas no encontrada"))),
                new Distrito(null, "Layo", "Distrito de Layo", true, provinciaRepository.findByNombre("Canas").orElseThrow(() -> new RuntimeException("Provincia Canas no encontrada"))),
                new Distrito(null, "Pampamarca", "Distrito de Pampamarca", true, provinciaRepository.findByNombre("Canas").orElseThrow(() -> new RuntimeException("Provincia Canas no encontrada"))),
                new Distrito(null, "Quehue", "Distrito de Quehue", true, provinciaRepository.findByNombre("Canas").orElseThrow(() -> new RuntimeException("Provincia Canas no encontrada"))),
                new Distrito(null, "Tupac Amaru", "Distrito de Tupac Amaru", true, provinciaRepository.findByNombre("Canas").orElseThrow(() -> new RuntimeException("Provincia Canas no encontrada"))),

                // [Provincia de Canchis]
                new Distrito(null, "Sicuani", "Distrito de Sicuani", true, provinciaRepository.findByNombre("Canchis").orElseThrow(() -> new RuntimeException("Provincia Canchis no encontrada"))),
                new Distrito(null, "Checacupe", "Distrito de Checacupe", true, provinciaRepository.findByNombre("Canchis").orElseThrow(() -> new RuntimeException("Provincia Canchis no encontrada"))),
                new Distrito(null, "Combapata", "Distrito de Combapata", true, provinciaRepository.findByNombre("Canchis").orElseThrow(() -> new RuntimeException("Provincia Canchis no encontrada"))),
                new Distrito(null, "Marangani", "Distrito de Marangani", true, provinciaRepository.findByNombre("Canchis").orElseThrow(() -> new RuntimeException("Provincia Canchis no encontrada"))),
                new Distrito(null, "Pitumarca", "Distrito de Pitumarca", true, provinciaRepository.findByNombre("Canchis").orElseThrow(() -> new RuntimeException("Provincia Canchis no encontrada"))),
                new Distrito(null, "San Pablo", "Distrito de San Pablo", true, provinciaRepository.findByNombre("Canchis").orElseThrow(() -> new RuntimeException("Provincia Canchis no encontrada"))),
                new Distrito(null, "San Pedro", "Distrito de San Pedro", true, provinciaRepository.findByNombre("Canchis").orElseThrow(() -> new RuntimeException("Provincia Canchis no encontrada"))),
                new Distrito(null, "Tinta", "Distrito de Tinta", true, provinciaRepository.findByNombre("Canchis").orElseThrow(() -> new RuntimeException("Provincia Canchis no encontrada"))),

                // [Provincia de Chumbivilcas]
                new Distrito(null, "Santo Tomás", "Distrito de Santo Tomás", true, provinciaRepository.findByNombre("Chumbivilcas").orElseThrow(() -> new RuntimeException("Provincia Chumbivilcas no encontrada"))),
                new Distrito(null, "Capacmarca", "Distrito de Capacmarca", true, provinciaRepository.findByNombre("Chumbivilcas").orElseThrow(() -> new RuntimeException("Provincia Chumbivilcas no encontrada"))),
                new Distrito(null, "Chamaca", "Distrito de Chamaca", true, provinciaRepository.findByNombre("Chumbivilcas").orElseThrow(() -> new RuntimeException("Provincia Chumbivilcas no encontrada"))),
                new Distrito(null, "Colquemarca", "Distrito de Colquemarca", true, provinciaRepository.findByNombre("Chumbivilcas").orElseThrow(() -> new RuntimeException("Provincia Chumbivilcas no encontrada"))),
                new Distrito(null, "Livitaca", "Distrito de Livitaca", true, provinciaRepository.findByNombre("Chumbivilcas").orElseThrow(() -> new RuntimeException("Provincia Chumbivilcas no encontrada"))),
                new Distrito(null, "Llusco", "Distrito de Llusco", true, provinciaRepository.findByNombre("Chumbivilcas").orElseThrow(() -> new RuntimeException("Provincia Chumbivilcas no encontrada"))),
                new Distrito(null, "Quiñota", "Distrito de Quiñota", true, provinciaRepository.findByNombre("Chumbivilcas").orElseThrow(() -> new RuntimeException("Provincia Chumbivilcas no encontrada"))),
                new Distrito(null, "Velille", "Distrito de Velille", true, provinciaRepository.findByNombre("Chumbivilcas").orElseThrow(() -> new RuntimeException("Provincia Chumbivilcas no encontrada"))),

                // [Provincia de Espinar]
                new Distrito(null, "Espinar", "Distrito de Espinar", true, provinciaRepository.findByNombre("Espinar").orElseThrow(() -> new RuntimeException("Provincia Espinar no encontrada"))),
                new Distrito(null, "Condoroma", "Distrito de Condoroma", true, provinciaRepository.findByNombre("Espinar").orElseThrow(() -> new RuntimeException("Provincia Espinar no encontrada"))),
                new Distrito(null, "Coporaque", "Distrito de Coporaque", true, provinciaRepository.findByNombre("Espinar").orElseThrow(() -> new RuntimeException("Provincia Espinar no encontrada"))),
                new Distrito(null, "Ocoruro", "Distrito de Ocoruro", true, provinciaRepository.findByNombre("Espinar").orElseThrow(() -> new RuntimeException("Provincia Espinar no encontrada"))),
                new Distrito(null, "Pallpata", "Distrito de Pallpata", true, provinciaRepository.findByNombre("Espinar").orElseThrow(() -> new RuntimeException("Provincia Espinar no encontrada"))),
                new Distrito(null, "Pichigua", "Distrito de Pichigua", true, provinciaRepository.findByNombre("Espinar").orElseThrow(() -> new RuntimeException("Provincia Espinar no encontrada"))),
                new Distrito(null, "Suyckutambo", "Distrito de Suyckutambo", true, provinciaRepository.findByNombre("Espinar").orElseThrow(() -> new RuntimeException("Provincia Espinar no encontrada"))),
                new Distrito(null, "Alto Pichigua", "Distrito de Alto Pichigua", true, provinciaRepository.findByNombre("Espinar").orElseThrow(() -> new RuntimeException("Provincia Espinar no encontrada"))),

                // [Provincia de La Convención]
                new Distrito(null, "Santa Ana", "Distrito de Santa Ana", true, provinciaRepository.findByNombre("La Convención").orElseThrow(() -> new RuntimeException("Provincia La Convención no encontrada"))),
                new Distrito(null, "Echarate", "Distrito de Echarate", true, provinciaRepository.findByNombre("La Convención").orElseThrow(() -> new RuntimeException("Provincia La Convención no encontrada"))),
                new Distrito(null, "Huayopata", "Distrito de Huayopata", true, provinciaRepository.findByNombre("La Convención").orElseThrow(() -> new RuntimeException("Provincia La Convención no encontrada"))),
                new Distrito(null, "Maranura", "Distrito de Maranura", true, provinciaRepository.findByNombre("La Convención").orElseThrow(() -> new RuntimeException("Provincia La Convención no encontrada"))),
                new Distrito(null, "Ocobamba", "Distrito de Ocobamba", true, provinciaRepository.findByNombre("La Convención").orElseThrow(() -> new RuntimeException("Provincia La Convención no encontrada"))),
                new Distrito(null, "Quellouno", "Distrito de Quellouno", true, provinciaRepository.findByNombre("La Convención").orElseThrow(() -> new RuntimeException("Provincia La Convención no encontrada"))),
                new Distrito(null, "Kimbiri", "Distrito de Kimbiri", true, provinciaRepository.findByNombre("La Convención").orElseThrow(() -> new RuntimeException("Provincia La Convención no encontrada"))),
                new Distrito(null, "Santa Teresa", "Distrito de Santa Teresa", true, provinciaRepository.findByNombre("La Convención").orElseThrow(() -> new RuntimeException("Provincia La Convención no encontrada"))),
                new Distrito(null, "Vilcabamba", "Distrito de Vilcabamba", true, provinciaRepository.findByNombre("La Convención").orElseThrow(() -> new RuntimeException("Provincia La Convención no encontrada"))),
                new Distrito(null, "Pichari", "Distrito de Pichari", true, provinciaRepository.findByNombre("La Convención").orElseThrow(() -> new RuntimeException("Provincia La Convención no encontrada"))),
                new Distrito(null, "Inkawasi", "Distrito de Inkawasi", true, provinciaRepository.findByNombre("La Convención").orElseThrow(() -> new RuntimeException("Provincia La Convención no encontrada"))),
                new Distrito(null, "Villa Virgen", "Distrito de Villa Virgen", true, provinciaRepository.findByNombre("La Convención").orElseThrow(() -> new RuntimeException("Provincia La Convención no encontrada"))),
                new Distrito(null, "Villa Kintiarina", "Distrito de Villa Kintiarina", true, provinciaRepository.findByNombre("La Convención").orElseThrow(() -> new RuntimeException("Provincia La Convención no encontrada"))),

                // [Provincia de Paruro]
                new Distrito(null, "Paruro", "Distrito de Paruro", true, provinciaRepository.findByNombre("Paruro").orElseThrow(() -> new RuntimeException("Provincia Paruro no encontrada"))),
                new Distrito(null, "Accha", "Distrito de Accha", true, provinciaRepository.findByNombre("Paruro").orElseThrow(() -> new RuntimeException("Provincia Paruro no encontrada"))),
                new Distrito(null, "Ccapi", "Distrito de Ccapi", true, provinciaRepository.findByNombre("Paruro").orElseThrow(() -> new RuntimeException("Provincia Paruro no encontrada"))),
                new Distrito(null, "Colcha", "Distrito de Colcha", true, provinciaRepository.findByNombre("Paruro").orElseThrow(() -> new RuntimeException("Provincia Paruro no encontrada"))),
                new Distrito(null, "Huanoquite", "Distrito de Huanoquite", true, provinciaRepository.findByNombre("Paruro").orElseThrow(() -> new RuntimeException("Provincia Paruro no encontrada"))),
                new Distrito(null, "Omacha", "Distrito de Omacha", true, provinciaRepository.findByNombre("Paruro").orElseThrow(() -> new RuntimeException("Provincia Paruro no encontrada"))),
                new Distrito(null, "Paccaritambo", "Distrito de Paccaritambo", true, provinciaRepository.findByNombre("Paruro").orElseThrow(() -> new RuntimeException("Provincia Paruro no encontrada"))),
                new Distrito(null, "Pillpinto", "Distrito de Pillpinto", true, provinciaRepository.findByNombre("Paruro").orElseThrow(() -> new RuntimeException("Provincia Paruro no encontrada"))),
                new Distrito(null, "Yaurisque", "Distrito de Yaurisque", true, provinciaRepository.findByNombre("Paruro").orElseThrow(() -> new RuntimeException("Provincia Paruro no encontrada"))),

                // [Provincia de Paucartambo]
                new Distrito(null, "Paucartambo", "Distrito de Paucartambo", true, provinciaRepository.findByNombre("Paucartambo").orElseThrow(() -> new RuntimeException("Provincia Paucartambo no encontrada"))),
                new Distrito(null, "Caicay", "Distrito de Caicay", true, provinciaRepository.findByNombre("Paucartambo").orElseThrow(() -> new RuntimeException("Provincia Paucartambo no encontrada"))),
                new Distrito(null, "Challabamba", "Distrito de Challabamba", true, provinciaRepository.findByNombre("Paucartambo").orElseThrow(() -> new RuntimeException("Provincia Paucartambo no encontrada"))),
                new Distrito(null, "Colquepata", "Distrito de Colquepata", true, provinciaRepository.findByNombre("Paucartambo").orElseThrow(() -> new RuntimeException("Provincia Paucartambo no encontrada"))),
                new Distrito(null, "Huancarani", "Distrito de Huancarani", true, provinciaRepository.findByNombre("Paucartambo").orElseThrow(() -> new RuntimeException("Provincia Paucartambo no encontrada"))),
                new Distrito(null, "Kosñipata", "Distrito de Kosñipata", true, provinciaRepository.findByNombre("Paucartambo").orElseThrow(() -> new RuntimeException("Provincia Paucartambo no encontrada"))),

                // [Provincia de Quispicanchi]
                new Distrito(null, "Urcos", "Distrito de Urcos", true, provinciaRepository.findByNombre("Quispicanchi").orElseThrow(() -> new RuntimeException("Provincia Quispicanchi no encontrada"))),
                new Distrito(null, "Andahuaylillas", "Distrito de Andahuaylillas", true, provinciaRepository.findByNombre("Quispicanchi").orElseThrow(() -> new RuntimeException("Provincia Quispicanchi no encontrada"))),
                new Distrito(null, "Camanti", "Distrito de Camanti", true, provinciaRepository.findByNombre("Quispicanchi").orElseThrow(() -> new RuntimeException("Provincia Quispicanchi no encontrada"))),
                new Distrito(null, "Ccarhuayo", "Distrito de Ccarhuayo", true, provinciaRepository.findByNombre("Quispicanchi").orElseThrow(() -> new RuntimeException("Provincia Quispicanchi no encontrada"))),
                new Distrito(null, "Ccatca", "Distrito de Ccatca", true, provinciaRepository.findByNombre("Quispicanchi").orElseThrow(() -> new RuntimeException("Provincia Quispicanchi no encontrada"))),
                new Distrito(null, "Cusipata", "Distrito de Cusipata", true, provinciaRepository.findByNombre("Quispicanchi").orElseThrow(() -> new RuntimeException("Provincia Quispicanchi no encontrada"))),
                new Distrito(null, "Huaro", "Distrito de Huaro", true, provinciaRepository.findByNombre("Quispicanchi").orElseThrow(() -> new RuntimeException("Provincia Quispicanchi no encontrada"))),
                new Distrito(null, "Lucre", "Distrito de Lucre", true, provinciaRepository.findByNombre("Quispicanchi").orElseThrow(() -> new RuntimeException("Provincia Quispicanchi no encontrada"))),
                new Distrito(null, "Marcapata", "Distrito de Marcapata", true, provinciaRepository.findByNombre("Quispicanchi").orElseThrow(() -> new RuntimeException("Provincia Quispicanchi no encontrada"))),
                new Distrito(null, "Ocongate", "Distrito de Ocongate", true, provinciaRepository.findByNombre("Quispicanchi").orElseThrow(() -> new RuntimeException("Provincia Quispicanchi no encontrada"))),
                new Distrito(null, "Oropesa", "Distrito de Oropesa", true, provinciaRepository.findByNombre("Quispicanchi").orElseThrow(() -> new RuntimeException("Provincia Quispicanchi no encontrada"))),
                new Distrito(null, "Quiquijana", "Distrito de Quiquijana", true, provinciaRepository.findByNombre("Quispicanchi").orElseThrow(() -> new RuntimeException("Provincia Quispicanchi no encontrada"))),

                // [Provincia de Urubamba]
                new Distrito(null, "Urubamba", "Distrito de Urubamba", true, provinciaRepository.findByNombre("Urubamba").orElseThrow(() -> new RuntimeException("Provincia Urubamba no encontrada"))),
                new Distrito(null, "Chinchero", "Distrito de Chinchero", true, provinciaRepository.findByNombre("Urubamba").orElseThrow(() -> new RuntimeException("Provincia Urubamba no encontrada"))),
                new Distrito(null, "Huayllabamba", "Distrito de Huayllabamba", true, provinciaRepository.findByNombre("Urubamba").orElseThrow(() -> new RuntimeException("Provincia Urubamba no encontrada"))),
                new Distrito(null, "Machupicchu", "Distrito de Machupicchu", true, provinciaRepository.findByNombre("Urubamba").orElseThrow(() -> new RuntimeException("Provincia Urubamba no encontrada"))),
                new Distrito(null, "Maras", "Distrito de Maras", true, provinciaRepository.findByNombre("Urubamba").orElseThrow(() -> new RuntimeException("Provincia Urubamba no encontrada"))),
                new Distrito(null, "Ollantaytambo", "Distrito de Ollantaytambo", true, provinciaRepository.findByNombre("Urubamba").orElseThrow(() -> new RuntimeException("Provincia Urubamba no encontrada"))),
                new Distrito(null, "Yucay", "Distrito de Yucay", true, provinciaRepository.findByNombre("Urubamba").orElseThrow(() -> new RuntimeException("Provincia Urubamba no encontrada")))
        );
        distritoRepository.saveAll(distritos);
    }

    private void seedDistritosHuancavelica() {
        List<Distrito> distritos = Arrays.asList(
                // Huancavelica
                // [Provincia de Huancavelica]
                new Distrito(null, "Huancavelica", "Distrito de Huancavelica", true, provinciaRepository.findByNombre("Huancavelica").orElseThrow(() -> new RuntimeException("Provincia Huancavelica no encontrada"))),
                new Distrito(null, "Acobambilla", "Distrito de Acobambilla", true, provinciaRepository.findByNombre("Huancavelica").orElseThrow(() -> new RuntimeException("Provincia Huancavelica no encontrada"))),
                new Distrito(null, "Acoria", "Distrito de Acoria", true, provinciaRepository.findByNombre("Huancavelica").orElseThrow(() -> new RuntimeException("Provincia Huancavelica no encontrada"))),
                new Distrito(null, "Conayca", "Distrito de Conayca", true, provinciaRepository.findByNombre("Huancavelica").orElseThrow(() -> new RuntimeException("Provincia Huancavelica no encontrada"))),
                new Distrito(null, "Cuenca", "Distrito de Cuenca", true, provinciaRepository.findByNombre("Huancavelica").orElseThrow(() -> new RuntimeException("Provincia Huancavelica no encontrada"))),
                new Distrito(null, "Huachocolpa", "Distrito de Huachocolpa", true, provinciaRepository.findByNombre("Huancavelica").orElseThrow(() -> new RuntimeException("Provincia Huancavelica no encontrada"))),
                new Distrito(null, "Huayllahuara", "Distrito de Huayllahuara", true, provinciaRepository.findByNombre("Huancavelica").orElseThrow(() -> new RuntimeException("Provincia Huancavelica no encontrada"))),
                new Distrito(null, "Izcuchaca", "Distrito de Izcuchaca", true, provinciaRepository.findByNombre("Huancavelica").orElseThrow(() -> new RuntimeException("Provincia Huancavelica no encontrada"))),
                new Distrito(null, "Laria", "Distrito de Laria", true, provinciaRepository.findByNombre("Huancavelica").orElseThrow(() -> new RuntimeException("Provincia Huancavelica no encontrada"))),
                new Distrito(null, "Manta", "Distrito de Manta", true, provinciaRepository.findByNombre("Huancavelica").orElseThrow(() -> new RuntimeException("Provincia Huancavelica no encontrada"))),
                new Distrito(null, "Mariscal Cáceres", "Distrito de Mariscal Cáceres", true, provinciaRepository.findByNombre("Huancavelica").orElseThrow(() -> new RuntimeException("Provincia Huancavelica no encontrada"))),
                new Distrito(null, "Moya", "Distrito de Moya", true, provinciaRepository.findByNombre("Huancavelica").orElseThrow(() -> new RuntimeException("Provincia Huancavelica no encontrada"))),
                new Distrito(null, "Nuevo Occoro", "Distrito de Nuevo Occoro", true, provinciaRepository.findByNombre("Huancavelica").orElseThrow(() -> new RuntimeException("Provincia Huancavelica no encontrada"))),
                new Distrito(null, "Palca", "Distrito de Palca", true, provinciaRepository.findByNombre("Huancavelica").orElseThrow(() -> new RuntimeException("Provincia Huancavelica no encontrada"))),
                new Distrito(null, "Pilchaca", "Distrito de Pilchaca", true, provinciaRepository.findByNombre("Huancavelica").orElseThrow(() -> new RuntimeException("Provincia Huancavelica no encontrada"))),
                new Distrito(null, "Vilca", "Distrito de Vilca", true, provinciaRepository.findByNombre("Huancavelica").orElseThrow(() -> new RuntimeException("Provincia Huancavelica no encontrada"))),
                new Distrito(null, "Yauli", "Distrito de Yauli", true, provinciaRepository.findByNombre("Huancavelica").orElseThrow(() -> new RuntimeException("Provincia Huancavelica no encontrada"))),
                new Distrito(null, "Ascensión", "Distrito de Ascensión", true, provinciaRepository.findByNombre("Huancavelica").orElseThrow(() -> new RuntimeException("Provincia Huancavelica no encontrada"))),
                new Distrito(null, "Huando", "Distrito de Huando", true, provinciaRepository.findByNombre("Huancavelica").orElseThrow(() -> new RuntimeException("Provincia Huancavelica no encontrada"))),

                // [Provincia de Acobamba]
                new Distrito(null, "Acobamba", "Distrito de Acobamba", true, provinciaRepository.findByNombre("Acobamba").orElseThrow(() -> new RuntimeException("Provincia Acobamba no encontrada"))),
                new Distrito(null, "Andabamba", "Distrito de Andabamba", true, provinciaRepository.findByNombre("Acobamba").orElseThrow(() -> new RuntimeException("Provincia Acobamba no encontrada"))),
                new Distrito(null, "Anta", "Distrito de Anta", true, provinciaRepository.findByNombre("Acobamba").orElseThrow(() -> new RuntimeException("Provincia Acobamba no encontrada"))),
                new Distrito(null, "Caja", "Distrito de Caja", true, provinciaRepository.findByNombre("Acobamba").orElseThrow(() -> new RuntimeException("Provincia Acobamba no encontrada"))),
                new Distrito(null, "Marcas", "Distrito de Marcas", true, provinciaRepository.findByNombre("Acobamba").orElseThrow(() -> new RuntimeException("Provincia Acobamba no encontrada"))),
                new Distrito(null, "Paucara", "Distrito de Paucara", true, provinciaRepository.findByNombre("Acobamba").orElseThrow(() -> new RuntimeException("Provincia Acobamba no encontrada"))),
                new Distrito(null, "Pomacocha", "Distrito de Pomacocha", true, provinciaRepository.findByNombre("Acobamba").orElseThrow(() -> new RuntimeException("Provincia Acobamba no encontrada"))),
                new Distrito(null, "Rosario", "Distrito de Rosario", true, provinciaRepository.findByNombre("Acobamba").orElseThrow(() -> new RuntimeException("Provincia Acobamba no encontrada"))),

                // [Provincia de Angaraes]
                new Distrito(null, "Lircay", "Distrito de Lircay", true, provinciaRepository.findByNombre("Angaraes").orElseThrow(() -> new RuntimeException("Provincia Angaraes no encontrada"))),
                new Distrito(null, "Anchonga", "Distrito de Anchonga", true, provinciaRepository.findByNombre("Angaraes").orElseThrow(() -> new RuntimeException("Provincia Angaraes no encontrada"))),
                new Distrito(null, "Callanmarca", "Distrito de Callanmarca", true, provinciaRepository.findByNombre("Angaraes").orElseThrow(() -> new RuntimeException("Provincia Angaraes no encontrada"))),
                new Distrito(null, "Ccochaccasa", "Distrito de Ccochaccasa", true, provinciaRepository.findByNombre("Angaraes").orElseThrow(() -> new RuntimeException("Provincia Angaraes no encontrada"))),
                new Distrito(null, "Chincho", "Distrito de Chincho", true, provinciaRepository.findByNombre("Angaraes").orElseThrow(() -> new RuntimeException("Provincia Angaraes no encontrada"))),
                new Distrito(null, "Congalla", "Distrito de Congalla", true, provinciaRepository.findByNombre("Angaraes").orElseThrow(() -> new RuntimeException("Provincia Angaraes no encontrada"))),
                new Distrito(null, "Huanca-Huanca", "Distrito de Huanca-Huanca", true, provinciaRepository.findByNombre("Angaraes").orElseThrow(() -> new RuntimeException("Provincia Angaraes no encontrada"))),
                new Distrito(null, "Huayllay Grande", "Distrito de Huayllay Grande", true, provinciaRepository.findByNombre("Angaraes").orElseThrow(() -> new RuntimeException("Provincia Angaraes no encontrada"))),
                new Distrito(null, "Julcamarca", "Distrito de Julcamarca", true, provinciaRepository.findByNombre("Angaraes").orElseThrow(() -> new RuntimeException("Provincia Angaraes no encontrada"))),
                new Distrito(null, "San Antonio de Antaparco", "Distrito de San Antonio de Antaparco", true, provinciaRepository.findByNombre("Angaraes").orElseThrow(() -> new RuntimeException("Provincia Angaraes no encontrada"))),
                new Distrito(null, "Santo Tomas de Pata", "Distrito de Santo Tomas de Pata", true, provinciaRepository.findByNombre("Angaraes").orElseThrow(() -> new RuntimeException("Provincia Angaraes no encontrada"))),
                new Distrito(null, "Seclla", "Distrito de Seclla", true, provinciaRepository.findByNombre("Angaraes").orElseThrow(() -> new RuntimeException("Provincia Angaraes no encontrada"))),

                // [Provincia de Castrovirreyna]
                new Distrito(null, "Castrovirreyna", "Distrito de Castrovirreyna", true, provinciaRepository.findByNombre("Castrovirreyna").orElseThrow(() -> new RuntimeException("Provincia Castrovirreyna no encontrada"))),
                new Distrito(null, "Arma", "Distrito de Arma", true, provinciaRepository.findByNombre("Castrovirreyna").orElseThrow(() -> new RuntimeException("Provincia Castrovirreyna no encontrada"))),
                new Distrito(null, "Aurahua", "Distrito de Aurahua", true, provinciaRepository.findByNombre("Castrovirreyna").orElseThrow(() -> new RuntimeException("Provincia Castrovirreyna no encontrada"))),
                new Distrito(null, "Capillas", "Distrito de Capillas", true, provinciaRepository.findByNombre("Castrovirreyna").orElseThrow(() -> new RuntimeException("Provincia Castrovirreyna no encontrada"))),
                new Distrito(null, "Chupamarca", "Distrito de Chupamarca", true, provinciaRepository.findByNombre("Castrovirreyna").orElseThrow(() -> new RuntimeException("Provincia Castrovirreyna no encontrada"))),
                new Distrito(null, "Cocas", "Distrito de Cocas", true, provinciaRepository.findByNombre("Castrovirreyna").orElseThrow(() -> new RuntimeException("Provincia Castrovirreyna no encontrada"))),
                new Distrito(null, "Huachos", "Distrito de Huachos", true, provinciaRepository.findByNombre("Castrovirreyna").orElseThrow(() -> new RuntimeException("Provincia Castrovirreyna no encontrada"))),
                new Distrito(null, "Huamatambo", "Distrito de Huamatambo", true, provinciaRepository.findByNombre("Castrovirreyna").orElseThrow(() -> new RuntimeException("Provincia Castrovirreyna no encontrada"))),
                new Distrito(null, "Mollepampa", "Distrito de Mollepampa", true, provinciaRepository.findByNombre("Castrovirreyna").orElseThrow(() -> new RuntimeException("Provincia Castrovirreyna no encontrada"))),
                new Distrito(null, "San Juan", "Distrito de San Juan", true, provinciaRepository.findByNombre("Castrovirreyna").orElseThrow(() -> new RuntimeException("Provincia Castrovirreyna no encontrada"))),
                new Distrito(null, "Santa Ana", "Distrito de Santa Ana", true, provinciaRepository.findByNombre("Castrovirreyna").orElseThrow(() -> new RuntimeException("Provincia Castrovirreyna no encontrada"))),
                new Distrito(null, "Tantará", "Distrito de Tantará", true, provinciaRepository.findByNombre("Castrovirreyna").orElseThrow(() -> new RuntimeException("Provincia Castrovirreyna no encontrada"))),
                new Distrito(null, "Ticrapo", "Distrito de Ticrapo", true, provinciaRepository.findByNombre("Castrovirreyna").orElseThrow(() -> new RuntimeException("Provincia Castrovirreyna no encontrada"))),

                // [Provincia de Churcampa]
                new Distrito(null, "Churcampa", "Distrito de Churcampa", true, provinciaRepository.findByNombre("Churcampa").orElseThrow(() -> new RuntimeException("Provincia Churcampa no encontrada"))),
                new Distrito(null, "Anco", "Distrito de Anco", true, provinciaRepository.findByNombre("Churcampa").orElseThrow(() -> new RuntimeException("Provincia Churcampa no encontrada"))),
                new Distrito(null, "Chinchihuasi", "Distrito de Chinchihuasi", true, provinciaRepository.findByNombre("Churcampa").orElseThrow(() -> new RuntimeException("Provincia Churcampa no encontrada"))),
                new Distrito(null, "El Carmen", "Distrito de El Carmen", true, provinciaRepository.findByNombre("Churcampa").orElseThrow(() -> new RuntimeException("Provincia Churcampa no encontrada"))),
                new Distrito(null, "La Merced", "Distrito de La Merced", true, provinciaRepository.findByNombre("Churcampa").orElseThrow(() -> new RuntimeException("Provincia Churcampa no encontrada"))),
                new Distrito(null, "Locroja", "Distrito de Locroja", true, provinciaRepository.findByNombre("Churcampa").orElseThrow(() -> new RuntimeException("Provincia Churcampa no encontrada"))),
                new Distrito(null, "Paucarbamba", "Distrito de Paucarbamba", true, provinciaRepository.findByNombre("Churcampa").orElseThrow(() -> new RuntimeException("Provincia Churcampa no encontrada"))),
                new Distrito(null, "San Miguel de Mayocc", "Distrito de San Miguel de Mayocc", true, provinciaRepository.findByNombre("Churcampa").orElseThrow(() -> new RuntimeException("Provincia Churcampa no encontrada"))),
                new Distrito(null, "San Pedro de Coris", "Distrito de San Pedro de Coris", true, provinciaRepository.findByNombre("Churcampa").orElseThrow(() -> new RuntimeException("Provincia Churcampa no encontrada"))),
                new Distrito(null, "Pachamarca", "Distrito de Pachamarca", true, provinciaRepository.findByNombre("Churcampa").orElseThrow(() -> new RuntimeException("Provincia Churcampa no encontrada"))),
                new Distrito(null, "Cosme", "Distrito de Cosme", true, provinciaRepository.findByNombre("Churcampa").orElseThrow(() -> new RuntimeException("Provincia Churcampa no encontrada"))),


                // [Provincia de Huaytará]
                new Distrito(null, "Huaytará", "Distrito de Huaytará", true, provinciaRepository.findByNombre("Huaytará").orElseThrow(() -> new RuntimeException("Provincia Huaytará no encontrada"))),
                new Distrito(null, "Ayaví", "Distrito de Ayaví", true, provinciaRepository.findByNombre("Huaytará").orElseThrow(() -> new RuntimeException("Provincia Huaytará no encontrada"))),
                new Distrito(null, "Córdova", "Distrito de Córdova", true, provinciaRepository.findByNombre("Huaytará").orElseThrow(() -> new RuntimeException("Provincia Huaytará no encontrada"))),
                new Distrito(null, "Huayacundo Arma", "Distrito de Huayacundo Arma", true, provinciaRepository.findByNombre("Huaytará").orElseThrow(() -> new RuntimeException("Provincia Huaytará no encontrada"))),
                new Distrito(null, "Laramarca", "Distrito de Laramarca", true, provinciaRepository.findByNombre("Huaytará").orElseThrow(() -> new RuntimeException("Provincia Huaytará no encontrada"))),
                new Distrito(null, "Ocoyo", "Distrito de Ocoyo", true, provinciaRepository.findByNombre("Huaytará").orElseThrow(() -> new RuntimeException("Provincia Huaytará no encontrada"))),
                new Distrito(null, "Pilpichaca", "Distrito de Pilpichaca", true, provinciaRepository.findByNombre("Huaytará").orElseThrow(() -> new RuntimeException("Provincia Huaytará no encontrada"))),
                new Distrito(null, "Querco", "Distrito de Querco", true, provinciaRepository.findByNombre("Huaytará").orElseThrow(() -> new RuntimeException("Provincia Huaytará no encontrada"))),
                new Distrito(null, "Quito-Arma", "Distrito de Quito-Arma", true, provinciaRepository.findByNombre("Huaytará").orElseThrow(() -> new RuntimeException("Provincia Huaytará no encontrada"))),
                new Distrito(null, "San Antonio de Cusicancha", "Distrito de San Antonio de Cusicancha", true, provinciaRepository.findByNombre("Huaytará").orElseThrow(() -> new RuntimeException("Provincia Huaytará no encontrada"))),
                new Distrito(null, "San Francisco de Sangayaico", "Distrito de San Francisco de Sangayaico", true, provinciaRepository.findByNombre("Huaytará").orElseThrow(() -> new RuntimeException("Provincia Huaytará no encontrada"))),
                new Distrito(null, "San Isidro", "Distrito de San Isidro", true, provinciaRepository.findByNombre("Huaytará").orElseThrow(() -> new RuntimeException("Provincia Huaytará no encontrada"))),
                new Distrito(null, "Santiago de Chocorvos", "Distrito de Santiago de Chocorvos", true, provinciaRepository.findByNombre("Huaytará").orElseThrow(() -> new RuntimeException("Provincia Huaytará no encontrada"))),
                new Distrito(null, "Santiago de Quirahuara", "Distrito de Santiago de Quirahuara", true, provinciaRepository.findByNombre("Huaytará").orElseThrow(() -> new RuntimeException("Provincia Huaytará no encontrada"))),
                new Distrito(null, "Santo Domingo de Capillas", "Distrito de Santo Domingo de Capillas", true, provinciaRepository.findByNombre("Huaytará").orElseThrow(() -> new RuntimeException("Provincia Huaytará no encontrada"))),
                new Distrito(null, "Tambopata", "Distrito de Tambopata", true, provinciaRepository.findByNombre("Huaytará").orElseThrow(() -> new RuntimeException("Provincia Huaytará no encontrada"))),

                // [Provincia de Tayacaja]
                new Distrito(null, "Pampas", "Distrito de Pampas", true, provinciaRepository.findByNombre("Tayacaja").orElseThrow(() -> new RuntimeException("Provincia Tayacaja no encontrada"))),
                new Distrito(null, "Acostambo", "Distrito de Acostambo", true, provinciaRepository.findByNombre("Tayacaja").orElseThrow(() -> new RuntimeException("Provincia Tayacaja no encontrada"))),
                new Distrito(null, "Acraquia", "Distrito de Acraquia", true, provinciaRepository.findByNombre("Tayacaja").orElseThrow(() -> new RuntimeException("Provincia Tayacaja no encontrada"))),
                new Distrito(null, "Ahuaycha", "Distrito de Ahuaycha", true, provinciaRepository.findByNombre("Tayacaja").orElseThrow(() -> new RuntimeException("Provincia Tayacaja no encontrada"))),
                new Distrito(null, "Colcabamba", "Distrito de Colcabamba", true, provinciaRepository.findByNombre("Tayacaja").orElseThrow(() -> new RuntimeException("Provincia Tayacaja no encontrada"))),
                new Distrito(null, "Daniel Hernández", "Distrito de Daniel Hernández", true, provinciaRepository.findByNombre("Tayacaja").orElseThrow(() -> new RuntimeException("Provincia Tayacaja no encontrada"))),
                new Distrito(null, "Huachocolpa", "Distrito de Huachocolpa", true, provinciaRepository.findByNombre("Tayacaja").orElseThrow(() -> new RuntimeException("Provincia Tayacaja no encontrada"))),
                new Distrito(null, "Huaribamba", "Distrito de Huaribamba", true, provinciaRepository.findByNombre("Tayacaja").orElseThrow(() -> new RuntimeException("Provincia Tayacaja no encontrada"))),
                new Distrito(null, "Ñahuimpuquio", "Distrito de Ñahuimpuquio", true, provinciaRepository.findByNombre("Tayacaja").orElseThrow(() -> new RuntimeException("Provincia Tayacaja no encontrada"))),
                new Distrito(null, "Pazos", "Distrito de Pazos", true, provinciaRepository.findByNombre("Tayacaja").orElseThrow(() -> new RuntimeException("Provincia Tayacaja no encontrada"))),
                new Distrito(null, "Quishuar", "Distrito de Quishuar", true, provinciaRepository.findByNombre("Tayacaja").orElseThrow(() -> new RuntimeException("Provincia Tayacaja no encontrada"))),
                new Distrito(null, "Salcabamba", "Distrito de Salcabamba", true, provinciaRepository.findByNombre("Tayacaja").orElseThrow(() -> new RuntimeException("Provincia Tayacaja no encontrada"))),
                new Distrito(null, "Salcahuasi", "Distrito de Salcahuasi", true, provinciaRepository.findByNombre("Tayacaja").orElseThrow(() -> new RuntimeException("Provincia Tayacaja no encontrada"))),
                new Distrito(null, "San Marcos de Rocchac", "Distrito de San Marcos de Rocchac", true, provinciaRepository.findByNombre("Tayacaja").orElseThrow(() -> new RuntimeException("Provincia Tayacaja no encontrada"))),
                new Distrito(null, "Surcubamba", "Distrito de Surcubamba", true, provinciaRepository.findByNombre("Tayacaja").orElseThrow(() -> new RuntimeException("Provincia Tayacaja no encontrada"))),
                new Distrito(null, "Tintay Puncu", "Distrito de Tintay Puncu", true, provinciaRepository.findByNombre("Tayacaja").orElseThrow(() -> new RuntimeException("Provincia Tayacaja no encontrada"))),
                new Distrito(null, "Quichuas", "Distrito de Quichuas", true, provinciaRepository.findByNombre("Tayacaja").orElseThrow(() -> new RuntimeException("Provincia Tayacaja no encontrada"))),
                new Distrito(null, "Andaymarca", "Distrito de Andaymarca", true, provinciaRepository.findByNombre("Tayacaja").orElseThrow(() -> new RuntimeException("Provincia Tayacaja no encontrada")))
        );
        distritoRepository.saveAll(distritos);
    }

    private void seedDistritosHuanuco() {
        List<Distrito> distritos = Arrays.asList(
                // Huánuco
                // [Provincia de Huánuco]
                new Distrito(null, "Huánuco", "Distrito de Huánuco", true, provinciaRepository.findByNombre("Huánuco").orElseThrow(() -> new RuntimeException("Provincia Huánuco no encontrada"))),
                new Distrito(null, "Amarilis", "Distrito de Amarilis", true, provinciaRepository.findByNombre("Huánuco").orElseThrow(() -> new RuntimeException("Provincia Huánuco no encontrada"))),
                new Distrito(null, "Chinchao", "Distrito de Chinchao", true, provinciaRepository.findByNombre("Huánuco").orElseThrow(() -> new RuntimeException("Provincia Huánuco no encontrada"))),
                new Distrito(null, "Churubamba", "Distrito de Churubamba", true, provinciaRepository.findByNombre("Huánuco").orElseThrow(() -> new RuntimeException("Provincia Huánuco no encontrada"))),
                new Distrito(null, "Margos", "Distrito de Margos", true, provinciaRepository.findByNombre("Huánuco").orElseThrow(() -> new RuntimeException("Provincia Huánuco no encontrada"))),
                new Distrito(null, "Quisqui", "Distrito de Quisqui", true, provinciaRepository.findByNombre("Huánuco").orElseThrow(() -> new RuntimeException("Provincia Huánuco no encontrada"))),
                new Distrito(null, "San Francisco de Cayrán", "Distrito de San Francisco de Cayrán", true, provinciaRepository.findByNombre("Huánuco").orElseThrow(() -> new RuntimeException("Provincia Huánuco no encontrada"))),
                new Distrito(null, "San Pedro de Chaulán", "Distrito de San Pedro de Chaulán", true, provinciaRepository.findByNombre("Huánuco").orElseThrow(() -> new RuntimeException("Provincia Huánuco no encontrada"))),
                new Distrito(null, "Santa María del Valle", "Distrito de Santa María del Valle", true, provinciaRepository.findByNombre("Huánuco").orElseThrow(() -> new RuntimeException("Provincia Huánuco no encontrada"))),
                new Distrito(null, "Yarumayo", "Distrito de Yarumayo", true, provinciaRepository.findByNombre("Huánuco").orElseThrow(() -> new RuntimeException("Provincia Huánuco no encontrada"))),
                new Distrito(null, "Pillco Marca", "Distrito de Pillco Marca", true, provinciaRepository.findByNombre("Huánuco").orElseThrow(() -> new RuntimeException("Provincia Huánuco no encontrada"))),
                new Distrito(null, "Yacus", "Distrito de Yacus", true, provinciaRepository.findByNombre("Huánuco").orElseThrow(() -> new RuntimeException("Provincia Huánuco no encontrada"))),

                // [Provincia de Ambo]
                new Distrito(null, "Ambo", "Distrito de Ambo", true, provinciaRepository.findByNombre("Ambo").orElseThrow(() -> new RuntimeException("Provincia Ambo no encontrada"))),
                new Distrito(null, "Cayna", "Distrito de Cayna", true, provinciaRepository.findByNombre("Ambo").orElseThrow(() -> new RuntimeException("Provincia Ambo no encontrada"))),
                new Distrito(null, "Colpas", "Distrito de Colpas", true, provinciaRepository.findByNombre("Ambo").orElseThrow(() -> new RuntimeException("Provincia Ambo no encontrada"))),
                new Distrito(null, "Conchamarca", "Distrito de Conchamarca", true, provinciaRepository.findByNombre("Ambo").orElseThrow(() -> new RuntimeException("Provincia Ambo no encontrada"))),
                new Distrito(null, "Huacar", "Distrito de Huacar", true, provinciaRepository.findByNombre("Ambo").orElseThrow(() -> new RuntimeException("Provincia Ambo no encontrada"))),
                new Distrito(null, "San Francisco", "Distrito de San Francisco", true, provinciaRepository.findByNombre("Ambo").orElseThrow(() -> new RuntimeException("Provincia Ambo no encontrada"))),
                new Distrito(null, "San Rafael", "Distrito de San Rafael", true, provinciaRepository.findByNombre("Ambo").orElseThrow(() -> new RuntimeException("Provincia Ambo no encontrada"))),
                new Distrito(null, "Tomay Kichwa", "Distrito de Tomay Kichwa", true, provinciaRepository.findByNombre("Ambo").orElseThrow(() -> new RuntimeException("Provincia Ambo no encontrada"))),

                // [Provincia de Dos de Mayo]
                new Distrito(null, "La Unión", "Distrito de La Unión", true, provinciaRepository.findByNombre("Dos de Mayo").orElseThrow(() -> new RuntimeException("Provincia Dos de Mayo no encontrada"))),
                new Distrito(null, "Chuquis", "Distrito de Chuquis", true, provinciaRepository.findByNombre("Dos de Mayo").orElseThrow(() -> new RuntimeException("Provincia Dos de Mayo no encontrada"))),
                new Distrito(null, "Marías", "Distrito de Marías", true, provinciaRepository.findByNombre("Dos de Mayo").orElseThrow(() -> new RuntimeException("Provincia Dos de Mayo no encontrada"))),
                new Distrito(null, "Pachas", "Distrito de Pachas", true, provinciaRepository.findByNombre("Dos de Mayo").orElseThrow(() -> new RuntimeException("Provincia Dos de Mayo no encontrada"))),
                new Distrito(null, "Quivilla", "Distrito de Quivilla", true, provinciaRepository.findByNombre("Dos de Mayo").orElseThrow(() -> new RuntimeException("Provincia Dos de Mayo no encontrada"))),
                new Distrito(null, "Ripán", "Distrito de Ripán", true, provinciaRepository.findByNombre("Dos de Mayo").orElseThrow(() -> new RuntimeException("Provincia Dos de Mayo no encontrada"))),
                new Distrito(null, "Shunqui", "Distrito de Shunqui", true, provinciaRepository.findByNombre("Dos de Mayo").orElseThrow(() -> new RuntimeException("Provincia Dos de Mayo no encontrada"))),
                new Distrito(null, "Sillapata", "Distrito de Sillapata", true, provinciaRepository.findByNombre("Dos de Mayo").orElseThrow(() -> new RuntimeException("Provincia Dos de Mayo no encontrada"))),
                new Distrito(null, "Yanas", "Distrito de Yanas", true, provinciaRepository.findByNombre("Dos de Mayo").orElseThrow(() -> new RuntimeException("Provincia Dos de Mayo no encontrada"))),

                // [Provincia de Huacaybamba]
                new Distrito(null, "Huacaybamba", "Distrito de Huacaybamba", true, provinciaRepository.findByNombre("Huacaybamba").orElseThrow(() -> new RuntimeException("Provincia Huacaybamba no encontrada"))),
                new Distrito(null, "Canchabamba", "Distrito de Canchabamba", true, provinciaRepository.findByNombre("Huacaybamba").orElseThrow(() -> new RuntimeException("Provincia Huacaybamba no encontrada"))),
                new Distrito(null, "Cochabamba", "Distrito de Cochabamba", true, provinciaRepository.findByNombre("Huacaybamba").orElseThrow(() -> new RuntimeException("Provincia Huacaybamba no encontrada"))),
                new Distrito(null, "Pinra", "Distrito de Pinra", true, provinciaRepository.findByNombre("Huacaybamba").orElseThrow(() -> new RuntimeException("Provincia Huacaybamba no encontrada"))),

                // [Provincia de Huamalíes]
                new Distrito(null, "Llata", "Distrito de Llata", true, provinciaRepository.findByNombre("Huamalíes").orElseThrow(() -> new RuntimeException("Provincia Huamalíes no encontrada"))),
                new Distrito(null, "Arancay", "Distrito de Arancay", true, provinciaRepository.findByNombre("Huamalíes").orElseThrow(() -> new RuntimeException("Provincia Huamalíes no encontrada"))),
                new Distrito(null, "Chavín de Pariarca", "Distrito de Chavín de Pariarca", true, provinciaRepository.findByNombre("Huamalíes").orElseThrow(() -> new RuntimeException("Provincia Huamalíes no encontrada"))),
                new Distrito(null, "Jacas Grande", "Distrito de Jacas Grande", true, provinciaRepository.findByNombre("Huamalíes").orElseThrow(() -> new RuntimeException("Provincia Huamalíes no encontrada"))),
                new Distrito(null, "Jircan", "Distrito de Jircan", true, provinciaRepository.findByNombre("Huamalíes").orElseThrow(() -> new RuntimeException("Provincia Huamalíes no encontrada"))),
                new Distrito(null, "Miraflores", "Distrito de Miraflores", true, provinciaRepository.findByNombre("Huamalíes").orElseThrow(() -> new RuntimeException("Provincia Huamalíes no encontrada"))),
                new Distrito(null, "Monzón", "Distrito de Monzón", true, provinciaRepository.findByNombre("Huamalíes").orElseThrow(() -> new RuntimeException("Provincia Huamalíes no encontrada"))),
                new Distrito(null, "Punchao", "Distrito de Punchao", true, provinciaRepository.findByNombre("Huamalíes").orElseThrow(() -> new RuntimeException("Provincia Huamalíes no encontrada"))),
                new Distrito(null, "Puños", "Distrito de Puños", true, provinciaRepository.findByNombre("Huamalíes").orElseThrow(() -> new RuntimeException("Provincia Huamalíes no encontrada"))),
                new Distrito(null, "Singa", "Distrito de Singa", true, provinciaRepository.findByNombre("Huamalíes").orElseThrow(() -> new RuntimeException("Provincia Huamalíes no encontrada"))),
                new Distrito(null, "Tantamayo", "Distrito de Tantamayo", true, provinciaRepository.findByNombre("Huamalíes").orElseThrow(() -> new RuntimeException("Provincia Huamalíes no encontrada"))),

                // [Provincia de Leoncio Prado]
                new Distrito(null, "Rupa-Rupa", "Distrito de Rupa-Rupa", true, provinciaRepository.findByNombre("Leoncio Prado").orElseThrow(() -> new RuntimeException("Provincia Leoncio Prado no encontrada"))),
                new Distrito(null, "Daniel Alomía Robles", "Distrito de Daniel Alomía Robles", true, provinciaRepository.findByNombre("Leoncio Prado").orElseThrow(() -> new RuntimeException("Provincia Leoncio Prado no encontrada"))),
                new Distrito(null, "Hermilio Valdizán", "Distrito de Hermilio Valdizán", true, provinciaRepository.findByNombre("Leoncio Prado").orElseThrow(() -> new RuntimeException("Provincia Leoncio Prado no encontrada"))),
                new Distrito(null, "José Crespo y Castillo", "Distrito de José Crespo y Castillo", true, provinciaRepository.findByNombre("Leoncio Prado").orElseThrow(() -> new RuntimeException("Provincia Leoncio Prado no encontrada"))),
                new Distrito(null, "Luyando", "Distrito de Luyando", true, provinciaRepository.findByNombre("Leoncio Prado").orElseThrow(() -> new RuntimeException("Provincia Leoncio Prado no encontrada"))),
                new Distrito(null, "Mariano Damaso Beraun", "Distrito de Mariano Damaso Beraun", true, provinciaRepository.findByNombre("Leoncio Prado").orElseThrow(() -> new RuntimeException("Provincia Leoncio Prado no encontrada"))),

                // [Provincia de Marañón]
                new Distrito(null, "Huacrachuco", "Distrito de Huacrachuco", true, provinciaRepository.findByNombre("Marañón").orElseThrow(() -> new RuntimeException("Provincia Marañón no encontrada"))),
                new Distrito(null, "Cholon", "Distrito de Cholon", true, provinciaRepository.findByNombre("Marañón").orElseThrow(() -> new RuntimeException("Provincia Marañón no encontrada"))),
                new Distrito(null, "San Buenaventura", "Distrito de San Buenaventura", true, provinciaRepository.findByNombre("Marañón").orElseThrow(() -> new RuntimeException("Provincia Marañón no encontrada"))),

                // [Provincia de Pachitea]
                new Distrito(null, "Panao", "Distrito de Panao", true, provinciaRepository.findByNombre("Pachitea").orElseThrow(() -> new RuntimeException("Provincia Pachitea no encontrada"))),
                new Distrito(null, "Chaglla", "Distrito de Chaglla", true, provinciaRepository.findByNombre("Pachitea").orElseThrow(() -> new RuntimeException("Provincia Pachitea no encontrada"))),
                new Distrito(null, "Molino", "Distrito de Molino", true, provinciaRepository.findByNombre("Pachitea").orElseThrow(() -> new RuntimeException("Provincia Pachitea no encontrada"))),
                new Distrito(null, "Umari", "Distrito de Umari", true, provinciaRepository.findByNombre("Pachitea").orElseThrow(() -> new RuntimeException("Provincia Pachitea no encontrada"))),

                // [Provincia de Puerto Inca]
                new Distrito(null, "Puerto Inca", "Distrito de Puerto Inca", true, provinciaRepository.findByNombre("Puerto Inca").orElseThrow(() -> new RuntimeException("Provincia Puerto Inca no encontrada"))),
                new Distrito(null, "Codo del Pozuzo", "Distrito de Codo del Pozuzo", true, provinciaRepository.findByNombre("Puerto Inca").orElseThrow(() -> new RuntimeException("Provincia Puerto Inca no encontrada"))),
                new Distrito(null, "Honoria", "Distrito de Honoria", true, provinciaRepository.findByNombre("Puerto Inca").orElseThrow(() -> new RuntimeException("Provincia Puerto Inca no encontrada"))),
                new Distrito(null, "Tournavista", "Distrito de Tournavista", true, provinciaRepository.findByNombre("Puerto Inca").orElseThrow(() -> new RuntimeException("Provincia Puerto Inca no encontrada"))),
                new Distrito(null, "Yuyapichis", "Distrito de Yuyapichis", true, provinciaRepository.findByNombre("Puerto Inca").orElseThrow(() -> new RuntimeException("Provincia Puerto Inca no encontrada"))),

                // [Provincia de Lauricocha]
                new Distrito(null, "Jesús", "Distrito de Jesús", true, provinciaRepository.findByNombre("Lauricocha").orElseThrow(() -> new RuntimeException("Provincia Lauricocha no encontrada"))),
                new Distrito(null, "Baños", "Distrito de Baños", true, provinciaRepository.findByNombre("Lauricocha").orElseThrow(() -> new RuntimeException("Provincia Lauricocha no encontrada"))),
                new Distrito(null, "Jivia", "Distrito de Jivia", true, provinciaRepository.findByNombre("Lauricocha").orElseThrow(() -> new RuntimeException("Provincia Lauricocha no encontrada"))),
                new Distrito(null, "Queropalca", "Distrito de Queropalca", true, provinciaRepository.findByNombre("Lauricocha").orElseThrow(() -> new RuntimeException("Provincia Lauricocha no encontrada"))),
                new Distrito(null, "Rondos", "Distrito de Rondos", true, provinciaRepository.findByNombre("Lauricocha").orElseThrow(() -> new RuntimeException("Provincia Lauricocha no encontrada"))),
                new Distrito(null, "San Francisco de Asís", "Distrito de San Francisco de Asís", true, provinciaRepository.findByNombre("Lauricocha").orElseThrow(() -> new RuntimeException("Provincia Lauricocha no encontrada"))),
                new Distrito(null, "San Miguel de Cauri", "Distrito de San Miguel de Cauri", true, provinciaRepository.findByNombre("Lauricocha").orElseThrow(() -> new RuntimeException("Provincia Lauricocha no encontrada"))),

                // [Provincia de Yarowilca]
                new Distrito(null, "Chavinillo", "Distrito de Chavinillo", true, provinciaRepository.findByNombre("Yarowilca").orElseThrow(() -> new RuntimeException("Provincia Yarowilca no encontrada"))),
                new Distrito(null, "Cahuac", "Distrito de Cahuac", true, provinciaRepository.findByNombre("Yarowilca").orElseThrow(() -> new RuntimeException("Provincia Yarowilca no encontrada"))),
                new Distrito(null, "Chacabamba", "Distrito de Chacabamba", true, provinciaRepository.findByNombre("Yarowilca").orElseThrow(() -> new RuntimeException("Provincia Yarowilca no encontrada"))),
                new Distrito(null, "Aparicio Pomares", "Distrito de Aparicio Pomares", true, provinciaRepository.findByNombre("Yarowilca").orElseThrow(() -> new RuntimeException("Provincia Yarowilca no encontrada"))),
                new Distrito(null, "Jacas Chico", "Distrito de Jacas Chico", true, provinciaRepository.findByNombre("Yarowilca").orElseThrow(() -> new RuntimeException("Provincia Yarowilca no encontrada"))),
                new Distrito(null, "Obas", "Distrito de Obas", true, provinciaRepository.findByNombre("Yarowilca").orElseThrow(() -> new RuntimeException("Provincia Yarowilca no encontrada"))),
                new Distrito(null, "Pampamarca", "Distrito de Pampamarca", true, provinciaRepository.findByNombre("Yarowilca").orElseThrow(() -> new RuntimeException("Provincia Yarowilca no encontrada"))),
                new Distrito(null, "Choras", "Distrito de Choras", true, provinciaRepository.findByNombre("Yarowilca").orElseThrow(() -> new RuntimeException("Provincia Yarowilca no encontrada")))
        );
        distritoRepository.saveAll(distritos);
    }

    private void seedDistritosIca() {
        List<Distrito> distritos = Arrays.asList(
                // Ica
                // [Provincia de Ica]
                new Distrito(null, "Ica", "Distrito de Ica", true, provinciaRepository.findByNombre("Ica").orElseThrow(() -> new RuntimeException("Provincia Ica no encontrada"))),
                new Distrito(null, "La Tinguiña", "Distrito de La Tinguiña", true, provinciaRepository.findByNombre("Ica").orElseThrow(() -> new RuntimeException("Provincia Ica no encontrada"))),
                new Distrito(null, "Los Aquijes", "Distrito de Los Aquijes", true, provinciaRepository.findByNombre("Ica").orElseThrow(() -> new RuntimeException("Provincia Ica no encontrada"))),
                new Distrito(null, "Ocucaje", "Distrito de Ocucaje", true, provinciaRepository.findByNombre("Ica").orElseThrow(() -> new RuntimeException("Provincia Ica no encontrada"))),
                new Distrito(null, "Pachacutec", "Distrito de Pachacutec", true, provinciaRepository.findByNombre("Ica").orElseThrow(() -> new RuntimeException("Provincia Ica no encontrada"))),
                new Distrito(null, "Parcona", "Distrito de Parcona", true, provinciaRepository.findByNombre("Ica").orElseThrow(() -> new RuntimeException("Provincia Ica no encontrada"))),
                new Distrito(null, "Pueblo Nuevo", "Distrito de Pueblo Nuevo", true, provinciaRepository.findByNombre("Ica").orElseThrow(() -> new RuntimeException("Provincia Ica no encontrada"))),
                new Distrito(null, "Salas", "Distrito de Salas", true, provinciaRepository.findByNombre("Ica").orElseThrow(() -> new RuntimeException("Provincia Ica no encontrada"))),
                new Distrito(null, "San José de los Molinos", "Distrito de San José de los Molinos", true, provinciaRepository.findByNombre("Ica").orElseThrow(() -> new RuntimeException("Provincia Ica no encontrada"))),
                new Distrito(null, "San Juan Bautista", "Distrito de San Juan Bautista", true, provinciaRepository.findByNombre("Ica").orElseThrow(() -> new RuntimeException("Provincia Ica no encontrada"))),
                new Distrito(null, "Santiago", "Distrito de Santiago", true, provinciaRepository.findByNombre("Ica").orElseThrow(() -> new RuntimeException("Provincia Ica no encontrada"))),
                new Distrito(null, "Subtanjalla", "Distrito de Subtanjalla", true, provinciaRepository.findByNombre("Ica").orElseThrow(() -> new RuntimeException("Provincia Ica no encontrada"))),
                new Distrito(null, "Tate", "Distrito de Tate", true, provinciaRepository.findByNombre("Ica").orElseThrow(() -> new RuntimeException("Provincia Ica no encontrada"))),
                new Distrito(null, "Yauca", "Distrito de Yauca", true, provinciaRepository.findByNombre("Ica").orElseThrow(() -> new RuntimeException("Provincia Ica no encontrada"))),

                // [Provincia de Chincha]
                new Distrito(null, "Chincha Alta", "Distrito de Chincha Alta", true, provinciaRepository.findByNombre("Chincha").orElseThrow(() -> new RuntimeException("Provincia Chincha no encontrada"))),
                new Distrito(null, "Alto Larán", "Distrito de Alto Larán", true, provinciaRepository.findByNombre("Chincha").orElseThrow(() -> new RuntimeException("Provincia Chincha no encontrada"))),
                new Distrito(null, "Chavín", "Distrito de Chavín", true, provinciaRepository.findByNombre("Chincha").orElseThrow(() -> new RuntimeException("Provincia Chincha no encontrada"))),
                new Distrito(null, "Chincha Baja", "Distrito de Chincha Baja", true, provinciaRepository.findByNombre("Chincha").orElseThrow(() -> new RuntimeException("Provincia Chincha no encontrada"))),
                new Distrito(null, "El Carmen", "Distrito de El Carmen", true, provinciaRepository.findByNombre("Chincha").orElseThrow(() -> new RuntimeException("Provincia Chincha no encontrada"))),
                new Distrito(null, "Grocio Prado", "Distrito de Grocio Prado", true, provinciaRepository.findByNombre("Chincha").orElseThrow(() -> new RuntimeException("Provincia Chincha no encontrada"))),
                new Distrito(null, "Pueblo Nuevo", "Distrito de Pueblo Nuevo", true, provinciaRepository.findByNombre("Chincha").orElseThrow(() -> new RuntimeException("Provincia Chincha no encontrada"))),
                new Distrito(null, "San Juan de Yanac", "Distrito de San Juan de Yanac", true, provinciaRepository.findByNombre("Chincha").orElseThrow(() -> new RuntimeException("Provincia Chincha no encontrada"))),
                new Distrito(null, "San Pedro de Huacarpana", "Distrito de San Pedro de Huacarpana", true, provinciaRepository.findByNombre("Chincha").orElseThrow(() -> new RuntimeException("Provincia Chincha no encontrada"))),
                new Distrito(null, "Sunampe", "Distrito de Sunampe", true, provinciaRepository.findByNombre("Chincha").orElseThrow(() -> new RuntimeException("Provincia Chincha no encontrada"))),
                new Distrito(null, "Tambo de Mora", "Distrito de Tambo de Mora", true, provinciaRepository.findByNombre("Chincha").orElseThrow(() -> new RuntimeException("Provincia Chincha no encontrada"))),

                // [Provincia de Nazca]
                new Distrito(null, "Nazca", "Distrito de Nazca", true, provinciaRepository.findByNombre("Nazca").orElseThrow(() -> new RuntimeException("Provincia Nazca no encontrada"))),
                new Distrito(null, "Changuillo", "Distrito de Changuillo", true, provinciaRepository.findByNombre("Nazca").orElseThrow(() -> new RuntimeException("Provincia Nazca no encontrada"))),
                new Distrito(null, "El Ingenio", "Distrito de El Ingenio", true, provinciaRepository.findByNombre("Nazca").orElseThrow(() -> new RuntimeException("Provincia Nazca no encontrada"))),
                new Distrito(null, "Marcona", "Distrito de Marcona", true, provinciaRepository.findByNombre("Nazca").orElseThrow(() -> new RuntimeException("Provincia Nazca no encontrada"))),
                new Distrito(null, "Vista Alegre", "Distrito de Vista Alegre", true, provinciaRepository.findByNombre("Nazca").orElseThrow(() -> new RuntimeException("Provincia Nazca no encontrada"))),

                // [Provincia de Palpa]
                new Distrito(null, "Palpa", "Distrito de Palpa", true, provinciaRepository.findByNombre("Palpa").orElseThrow(() -> new RuntimeException("Provincia Palpa no encontrada"))),
                new Distrito(null, "Llipata", "Distrito de Llipata", true, provinciaRepository.findByNombre("Palpa").orElseThrow(() -> new RuntimeException("Provincia Palpa no encontrada"))),
                new Distrito(null, "Río Grande", "Distrito de Río Grande", true, provinciaRepository.findByNombre("Palpa").orElseThrow(() -> new RuntimeException("Provincia Palpa no encontrada"))),
                new Distrito(null, "Santa Cruz", "Distrito de Santa Cruz", true, provinciaRepository.findByNombre("Palpa").orElseThrow(() -> new RuntimeException("Provincia Palpa no encontrada"))),
                new Distrito(null, "Tibillo", "Distrito de Tibillo", true, provinciaRepository.findByNombre("Palpa").orElseThrow(() -> new RuntimeException("Provincia Palpa no encontrada"))),

                // [Provincia de Pisco]
                new Distrito(null, "Pisco", "Distrito de Pisco", true, provinciaRepository.findByNombre("Pisco").orElseThrow(() -> new RuntimeException("Provincia Pisco no encontrada"))),
                new Distrito(null, "Huancano", "Distrito de Huancano", true, provinciaRepository.findByNombre("Pisco").orElseThrow(() -> new RuntimeException("Provincia Pisco no encontrada"))),
                new Distrito(null, "Humay", "Distrito de Humay", true, provinciaRepository.findByNombre("Pisco").orElseThrow(() -> new RuntimeException("Provincia Pisco no encontrada"))),
                new Distrito(null, "Independencia", "Distrito de Independencia", true, provinciaRepository.findByNombre("Pisco").orElseThrow(() -> new RuntimeException("Provincia Pisco no encontrada"))),
                new Distrito(null, "Paracas", "Distrito de Paracas", true, provinciaRepository.findByNombre("Pisco").orElseThrow(() -> new RuntimeException("Provincia Pisco no encontrada"))),
                new Distrito(null, "San Andrés", "Distrito de San Andrés", true, provinciaRepository.findByNombre("Pisco").orElseThrow(() -> new RuntimeException("Provincia Pisco no encontrada"))),
                new Distrito(null, "San Clemente", "Distrito de San Clemente", true, provinciaRepository.findByNombre("Pisco").orElseThrow(() -> new RuntimeException("Provincia Pisco no encontrada"))),
                new Distrito(null, "Tupac Amaru Inca", "Distrito de Tupac Amaru Inca", true, provinciaRepository.findByNombre("Pisco").orElseThrow(() -> new RuntimeException("Provincia Pisco no encontrada")))
        );
        distritoRepository.saveAll(distritos);
    }

    private void seedDistritosJunin() {
        List<Distrito> distritos = Arrays.asList(
                // Junín
                // [Provincia de Huancayo]
                new Distrito(null, "Huancayo", "Distrito de Huancayo", true, provinciaRepository.findByNombre("Huancayo").orElseThrow(() -> new RuntimeException("Provincia Huancayo no encontrada"))),
                new Distrito(null, "Carhuacallanga", "Distrito de Carhuacallanga", true, provinciaRepository.findByNombre("Huancayo").orElseThrow(() -> new RuntimeException("Provincia Huancayo no encontrada"))),
                new Distrito(null, "Chacapampa", "Distrito de Chacapampa", true, provinciaRepository.findByNombre("Huancayo").orElseThrow(() -> new RuntimeException("Provincia Huancayo no encontrada"))),
                new Distrito(null, "Chicche", "Distrito de Chicche", true, provinciaRepository.findByNombre("Huancayo").orElseThrow(() -> new RuntimeException("Provincia Huancayo no encontrada"))),
                new Distrito(null, "Chilca", "Distrito de Chilca", true, provinciaRepository.findByNombre("Huancayo").orElseThrow(() -> new RuntimeException("Provincia Huancayo no encontrada"))),
                new Distrito(null, "Chongos Alto", "Distrito de Chongos Alto", true, provinciaRepository.findByNombre("Huancayo").orElseThrow(() -> new RuntimeException("Provincia Huancayo no encontrada"))),
                new Distrito(null, "Chupuro", "Distrito de Chupuro", true, provinciaRepository.findByNombre("Huancayo").orElseThrow(() -> new RuntimeException("Provincia Huancayo no encontrada"))),
                new Distrito(null, "Colca", "Distrito de Colca", true, provinciaRepository.findByNombre("Huancayo").orElseThrow(() -> new RuntimeException("Provincia Huancayo no encontrada"))),
                new Distrito(null, "Cullhuas", "Distrito de Cullhuas", true, provinciaRepository.findByNombre("Huancayo").orElseThrow(() -> new RuntimeException("Provincia Huancayo no encontrada"))),
                new Distrito(null, "El Tambo", "Distrito de El Tambo", true, provinciaRepository.findByNombre("Huancayo").orElseThrow(() -> new RuntimeException("Provincia Huancayo no encontrada"))),
                new Distrito(null, "Huacrapuquio", "Distrito de Huacrapuquio", true, provinciaRepository.findByNombre("Huancayo").orElseThrow(() -> new RuntimeException("Provincia Huancayo no encontrada"))),
                new Distrito(null, "Hualhuas", "Distrito de Hualhuas", true, provinciaRepository.findByNombre("Huancayo").orElseThrow(() -> new RuntimeException("Provincia Huancayo no encontrada"))),
                new Distrito(null, "Huancán", "Distrito de Huancán", true, provinciaRepository.findByNombre("Huancayo").orElseThrow(() -> new RuntimeException("Provincia Huancayo no encontrada"))),
                new Distrito(null, "Huasicancha", "Distrito de Huasicancha", true, provinciaRepository.findByNombre("Huancayo").orElseThrow(() -> new RuntimeException("Provincia Huancayo no encontrada"))),
                new Distrito(null, "Huayucachi", "Distrito de Huayucachi", true, provinciaRepository.findByNombre("Huancayo").orElseThrow(() -> new RuntimeException("Provincia Huancayo no encontrada"))),
                new Distrito(null, "Ingenio", "Distrito de Ingenio", true, provinciaRepository.findByNombre("Huancayo").orElseThrow(() -> new RuntimeException("Provincia Huancayo no encontrada"))),
                new Distrito(null, "Pariahuanca", "Distrito de Pariahuanca", true, provinciaRepository.findByNombre("Huancayo").orElseThrow(() -> new RuntimeException("Provincia Huancayo no encontrada"))),
                new Distrito(null, "Pilcomayo", "Distrito de Pilcomayo", true, provinciaRepository.findByNombre("Huancayo").orElseThrow(() -> new RuntimeException("Provincia Huancayo no encontrada"))),
                new Distrito(null, "Pucará", "Distrito de Pucará", true, provinciaRepository.findByNombre("Huancayo").orElseThrow(() -> new RuntimeException("Provincia Huancayo no encontrada"))),
                new Distrito(null, "Quichuay", "Distrito de Quichuay", true, provinciaRepository.findByNombre("Huancayo").orElseThrow(() -> new RuntimeException("Provincia Huancayo no encontrada"))),
                new Distrito(null, "Quilcas", "Distrito de Quilcas", true, provinciaRepository.findByNombre("Huancayo").orElseThrow(() -> new RuntimeException("Provincia Huancayo no encontrada"))),
                new Distrito(null, "San Agustín", "Distrito de San Agustín", true, provinciaRepository.findByNombre("Huancayo").orElseThrow(() -> new RuntimeException("Provincia Huancayo no encontrada"))),
                new Distrito(null, "San Jerónimo de Tunán", "Distrito de San Jerónimo de Tunán", true, provinciaRepository.findByNombre("Huancayo").orElseThrow(() -> new RuntimeException("Provincia Huancayo no encontrada"))),
                new Distrito(null, "Saño", "Distrito de Saño", true, provinciaRepository.findByNombre("Huancayo").orElseThrow(() -> new RuntimeException("Provincia Huancayo no encontrada"))),
                new Distrito(null, "Sapallanga", "Distrito de Sapallanga", true, provinciaRepository.findByNombre("Huancayo").orElseThrow(() -> new RuntimeException("Provincia Huancayo no encontrada"))),
                new Distrito(null, "Sicaya", "Distrito de Sicaya", true, provinciaRepository.findByNombre("Huancayo").orElseThrow(() -> new RuntimeException("Provincia Huancayo no encontrada"))),
                new Distrito(null, "Santo Domingo de Acobamba", "Distrito de Santo Domingo de Acobamba", true, provinciaRepository.findByNombre("Huancayo").orElseThrow(() -> new RuntimeException("Provincia Huancayo no encontrada"))),
                new Distrito(null, "Viques", "Distrito de Viques", true, provinciaRepository.findByNombre("Huancayo").orElseThrow(() -> new RuntimeException("Provincia Huancayo no encontrada"))),

                // [Provincia de Concepción]
                new Distrito(null, "Concepción", "Distrito de Concepción", true, provinciaRepository.findByNombre("Concepción").orElseThrow(() -> new RuntimeException("Provincia Concepción no encontrada"))),
                new Distrito(null, "Aco", "Distrito de Aco", true, provinciaRepository.findByNombre("Concepción").orElseThrow(() -> new RuntimeException("Provincia Concepción no encontrada"))),
                new Distrito(null, "Andamarca", "Distrito de Andamarca", true, provinciaRepository.findByNombre("Concepción").orElseThrow(() -> new RuntimeException("Provincia Concepción no encontrada"))),
                new Distrito(null, "Chambara", "Distrito de Chambara", true, provinciaRepository.findByNombre("Concepción").orElseThrow(() -> new RuntimeException("Provincia Concepción no encontrada"))),
                new Distrito(null, "Cochas", "Distrito de Cochas", true, provinciaRepository.findByNombre("Concepción").orElseThrow(() -> new RuntimeException("Provincia Concepción no encontrada"))),
                new Distrito(null, "Comas", "Distrito de Comas", true, provinciaRepository.findByNombre("Concepción").orElseThrow(() -> new RuntimeException("Provincia Concepción no encontrada"))),
                new Distrito(null, "Heroínas Toledo", "Distrito de Heroínas Toledo", true, provinciaRepository.findByNombre("Concepción").orElseThrow(() -> new RuntimeException("Provincia Concepción no encontrada"))),
                new Distrito(null, "Manzanares", "Distrito de Manzanares", true, provinciaRepository.findByNombre("Concepción").orElseThrow(() -> new RuntimeException("Provincia Concepción no encontrada"))),
                new Distrito(null, "Mariscal Castilla", "Distrito de Mariscal Castilla", true, provinciaRepository.findByNombre("Concepción").orElseThrow(() -> new RuntimeException("Provincia Concepción no encontrada"))),
                new Distrito(null, "Matahuasi", "Distrito de Matahuasi", true, provinciaRepository.findByNombre("Concepción").orElseThrow(() -> new RuntimeException("Provincia Concepción no encontrada"))),
                new Distrito(null, "Mito", "Distrito de Mito", true, provinciaRepository.findByNombre("Concepción").orElseThrow(() -> new RuntimeException("Provincia Concepción no encontrada"))),
                new Distrito(null, "Nueve de Julio", "Distrito de Nueve de Julio", true, provinciaRepository.findByNombre("Concepción").orElseThrow(() -> new RuntimeException("Provincia Concepción no encontrada"))),
                new Distrito(null, "Orcotuna", "Distrito de Orcotuna", true, provinciaRepository.findByNombre("Concepción").orElseThrow(() -> new RuntimeException("Provincia Concepción no encontrada"))),
                new Distrito(null, "San José de Quero", "Distrito de San José de Quero", true, provinciaRepository.findByNombre("Concepción").orElseThrow(() -> new RuntimeException("Provincia Concepción no encontrada"))),
                new Distrito(null, "Santa Rosa de Ocopa", "Distrito de Santa Rosa de Ocopa", true, provinciaRepository.findByNombre("Concepción").orElseThrow(() -> new RuntimeException("Provincia Concepción no encontrada"))),

                // [Provincia de Chanchamayo]
                new Distrito(null, "Chanchamayo", "Distrito de Chanchamayo", true, provinciaRepository.findByNombre("Chanchamayo").orElseThrow(() -> new RuntimeException("Provincia Chanchamayo no encontrada"))),
                new Distrito(null, "Perené", "Distrito de Perené", true, provinciaRepository.findByNombre("Chanchamayo").orElseThrow(() -> new RuntimeException("Provincia Chanchamayo no encontrada"))),
                new Distrito(null, "Pichanaqui", "Distrito de Pichanaqui", true, provinciaRepository.findByNombre("Chanchamayo").orElseThrow(() -> new RuntimeException("Provincia Chanchamayo no encontrada"))),
                new Distrito(null, "San Luis de Shuaro", "Distrito de San Luis de Shuaro", true, provinciaRepository.findByNombre("Chanchamayo").orElseThrow(() -> new RuntimeException("Provincia Chanchamayo no encontrada"))),
                new Distrito(null, "San Ramón", "Distrito de San Ramón", true, provinciaRepository.findByNombre("Chanchamayo").orElseThrow(() -> new RuntimeException("Provincia Chanchamayo no encontrada"))),
                new Distrito(null, "Vítoc", "Distrito de Vítoc", true, provinciaRepository.findByNombre("Chanchamayo").orElseThrow(() -> new RuntimeException("Provincia Chanchamayo no encontrada"))),

                // [Provincia de Jauja]
                new Distrito(null, "Jauja", "Distrito de Jauja", true, provinciaRepository.findByNombre("Jauja").orElseThrow(() -> new RuntimeException("Provincia Jauja no encontrada"))),
                new Distrito(null, "Acolla", "Distrito de Acolla", true, provinciaRepository.findByNombre("Jauja").orElseThrow(() -> new RuntimeException("Provincia Jauja no encontrada"))),
                new Distrito(null, "Apata", "Distrito de Apata", true, provinciaRepository.findByNombre("Jauja").orElseThrow(() -> new RuntimeException("Provincia Jauja no encontrada"))),
                new Distrito(null, "Ataura", "Distrito de Ataura", true, provinciaRepository.findByNombre("Jauja").orElseThrow(() -> new RuntimeException("Provincia Jauja no encontrada"))),
                new Distrito(null, "Canchayllo", "Distrito de Canchayllo", true, provinciaRepository.findByNombre("Jauja").orElseThrow(() -> new RuntimeException("Provincia Jauja no encontrada"))),
                new Distrito(null, "Curicaca", "Distrito de Curicaca", true, provinciaRepository.findByNombre("Jauja").orElseThrow(() -> new RuntimeException("Provincia Jauja no encontrada"))),
                new Distrito(null, "El Mantaro", "Distrito de El Mantaro", true, provinciaRepository.findByNombre("Jauja").orElseThrow(() -> new RuntimeException("Provincia Jauja no encontrada"))),
                new Distrito(null, "Huamali", "Distrito de Huamali", true, provinciaRepository.findByNombre("Jauja").orElseThrow(() -> new RuntimeException("Provincia Jauja no encontrada"))),
                new Distrito(null, "Huaripampa", "Distrito de Huaripampa", true, provinciaRepository.findByNombre("Jauja").orElseThrow(() -> new RuntimeException("Provincia Jauja no encontrada"))),
                new Distrito(null, "Huertas", "Distrito de Huertas", true, provinciaRepository.findByNombre("Jauja").orElseThrow(() -> new RuntimeException("Provincia Jauja no encontrada"))),
                new Distrito(null, "Janjaillo", "Distrito de Janjaillo", true, provinciaRepository.findByNombre("Jauja").orElseThrow(() -> new RuntimeException("Provincia Jauja no encontrada"))),
                new Distrito(null, "Julcán", "Distrito de Julcán", true, provinciaRepository.findByNombre("Jauja").orElseThrow(() -> new RuntimeException("Provincia Jauja no encontrada"))),
                new Distrito(null, "Leonor Ordóñez", "Distrito de Leonor Ordóñez", true, provinciaRepository.findByNombre("Jauja").orElseThrow(() -> new RuntimeException("Provincia Jauja no encontrada"))),
                new Distrito(null, "Llocllapampa", "Distrito de Llocllapampa", true, provinciaRepository.findByNombre("Jauja").orElseThrow(() -> new RuntimeException("Provincia Jauja no encontrada"))),
                new Distrito(null, "Marco", "Distrito de Marco", true, provinciaRepository.findByNombre("Jauja").orElseThrow(() -> new RuntimeException("Provincia Jauja no encontrada"))),
                new Distrito(null, "Masma", "Distrito de Masma", true, provinciaRepository.findByNombre("Jauja").orElseThrow(() -> new RuntimeException("Provincia Jauja no encontrada"))),
                new Distrito(null, "Masma Chicche", "Distrito de Masma Chicche", true, provinciaRepository.findByNombre("Jauja").orElseThrow(() -> new RuntimeException("Provincia Jauja no encontrada"))),
                new Distrito(null, "Molinos", "Distrito de Molinos", true, provinciaRepository.findByNombre("Jauja").orElseThrow(() -> new RuntimeException("Provincia Jauja no encontrada"))),
                new Distrito(null, "Monobamba", "Distrito de Monobamba", true, provinciaRepository.findByNombre("Jauja").orElseThrow(() -> new RuntimeException("Provincia Jauja no encontrada"))),
                new Distrito(null, "Muqui", "Distrito de Muqui", true, provinciaRepository.findByNombre("Jauja").orElseThrow(() -> new RuntimeException("Provincia Jauja no encontrada"))),
                new Distrito(null, "Muquiyauyo", "Distrito de Muquiyauyo", true, provinciaRepository.findByNombre("Jauja").orElseThrow(() -> new RuntimeException("Provincia Jauja no encontrada"))),
                new Distrito(null, "Paca", "Distrito de Paca", true, provinciaRepository.findByNombre("Jauja").orElseThrow(() -> new RuntimeException("Provincia Jauja no encontrada"))),
                new Distrito(null, "Paccha", "Distrito de Paccha", true, provinciaRepository.findByNombre("Jauja").orElseThrow(() -> new RuntimeException("Provincia Jauja no encontrada"))),
                new Distrito(null, "Pancán", "Distrito de Pancán", true, provinciaRepository.findByNombre("Jauja").orElseThrow(() -> new RuntimeException("Provincia Jauja no encontrada"))),
                new Distrito(null, "Parco", "Distrito de Parco", true, provinciaRepository.findByNombre("Jauja").orElseThrow(() -> new RuntimeException("Provincia Jauja no encontrada"))),
                new Distrito(null, "Pomacancha", "Distrito de Pomacancha", true, provinciaRepository.findByNombre("Jauja").orElseThrow(() -> new RuntimeException("Provincia Jauja no encontrada"))),
                new Distrito(null, "Ricrán", "Distrito de Ricrán", true, provinciaRepository.findByNombre("Jauja").orElseThrow(() -> new RuntimeException("Provincia Jauja no encontrada"))),
                new Distrito(null, "San Lorenzo", "Distrito de San Lorenzo", true, provinciaRepository.findByNombre("Jauja").orElseThrow(() -> new RuntimeException("Provincia Jauja no encontrada"))),
                new Distrito(null, "San Pedro de Chunan", "Distrito de San Pedro de Chunan", true, provinciaRepository.findByNombre("Jauja").orElseThrow(() -> new RuntimeException("Provincia Jauja no encontrada"))),
                new Distrito(null, "Sausa", "Distrito de Sausa", true, provinciaRepository.findByNombre("Jauja").orElseThrow(() -> new RuntimeException("Provincia Jauja no encontrada"))),
                new Distrito(null, "Sincos", "Distrito de Sincos", true, provinciaRepository.findByNombre("Jauja").orElseThrow(() -> new RuntimeException("Provincia Jauja no encontrada"))),
                new Distrito(null, "Tunan Marca", "Distrito de Tunan Marca", true, provinciaRepository.findByNombre("Jauja").orElseThrow(() -> new RuntimeException("Provincia Jauja no encontrada"))),
                new Distrito(null, "Yauli", "Distrito de Yauli", true, provinciaRepository.findByNombre("Jauja").orElseThrow(() -> new RuntimeException("Provincia Jauja no encontrada"))),
                new Distrito(null, "Yauyos", "Distrito de Yauyos", true, provinciaRepository.findByNombre("Jauja").orElseThrow(() -> new RuntimeException("Provincia Jauja no encontrada"))),

                // [Provincia de Junín]
                new Distrito(null, "Junín", "Distrito de Junín", true, provinciaRepository.findByNombre("Junín").orElseThrow(() -> new RuntimeException("Provincia Junín no encontrada"))),
                new Distrito(null, "Carhuamayo", "Distrito de Carhuamayo", true, provinciaRepository.findByNombre("Junín").orElseThrow(() -> new RuntimeException("Provincia Junín no encontrada"))),
                new Distrito(null, "Ondores", "Distrito de Ondores", true, provinciaRepository.findByNombre("Junín").orElseThrow(() -> new RuntimeException("Provincia Junín no encontrada"))),
                new Distrito(null, "Ulcumayo", "Distrito de Ulcumayo", true, provinciaRepository.findByNombre("Junín").orElseThrow(() -> new RuntimeException("Provincia Junín no encontrada"))),

                // [Provincia de Satipo]
                new Distrito(null, "Satipo", "Distrito de Satipo", true, provinciaRepository.findByNombre("Satipo").orElseThrow(() -> new RuntimeException("Provincia Satipo no encontrada"))),
                new Distrito(null, "Coviriali", "Distrito de Coviriali", true, provinciaRepository.findByNombre("Satipo").orElseThrow(() -> new RuntimeException("Provincia Satipo no encontrada"))),
                new Distrito(null, "Llaylla", "Distrito de Llaylla", true, provinciaRepository.findByNombre("Satipo").orElseThrow(() -> new RuntimeException("Provincia Satipo no encontrada"))),
                new Distrito(null, "Mazamari", "Distrito de Mazamari", true, provinciaRepository.findByNombre("Satipo").orElseThrow(() -> new RuntimeException("Provincia Satipo no encontrada"))),
                new Distrito(null, "Pampa Hermosa", "Distrito de Pampa Hermosa", true, provinciaRepository.findByNombre("Satipo").orElseThrow(() -> new RuntimeException("Provincia Satipo no encontrada"))),
                new Distrito(null, "Pangoa", "Distrito de Pangoa", true, provinciaRepository.findByNombre("Satipo").orElseThrow(() -> new RuntimeException("Provincia Satipo no encontrada"))),
                new Distrito(null, "Río Negro", "Distrito de Río Negro", true, provinciaRepository.findByNombre("Satipo").orElseThrow(() -> new RuntimeException("Provincia Satipo no encontrada"))),
                new Distrito(null, "Río Tambo", "Distrito de Río Tambo", true, provinciaRepository.findByNombre("Satipo").orElseThrow(() -> new RuntimeException("Provincia Satipo no encontrada"))),
                new Distrito(null, "Vizcatán del Ene", "Distrito de Vizcatán del Ene", true, provinciaRepository.findByNombre("Satipo").orElseThrow(() -> new RuntimeException("Provincia Satipo no encontrada"))),

                // [Provincia de Tarma]
                new Distrito(null, "Tarma", "Distrito de Tarma", true, provinciaRepository.findByNombre("Tarma").orElseThrow(() -> new RuntimeException("Provincia Tarma no encontrada"))),
                new Distrito(null, "Acobamba", "Distrito de Acobamba", true, provinciaRepository.findByNombre("Tarma").orElseThrow(() -> new RuntimeException("Provincia Tarma no encontrada"))),
                new Distrito(null, "Huaricolca", "Distrito de Huaricolca", true, provinciaRepository.findByNombre("Tarma").orElseThrow(() -> new RuntimeException("Provincia Tarma no encontrada"))),
                new Distrito(null, "Huasahuasi", "Distrito de Huasahuasi", true, provinciaRepository.findByNombre("Tarma").orElseThrow(() -> new RuntimeException("Provincia Tarma no encontrada"))),
                new Distrito(null, "La Unión", "Distrito de La Unión", true, provinciaRepository.findByNombre("Tarma").orElseThrow(() -> new RuntimeException("Provincia Tarma no encontrada"))),
                new Distrito(null, "Palca", "Distrito de Palca", true, provinciaRepository.findByNombre("Tarma").orElseThrow(() -> new RuntimeException("Provincia Tarma no encontrada"))),
                new Distrito(null, "Palcamayo", "Distrito de Palcamayo", true, provinciaRepository.findByNombre("Tarma").orElseThrow(() -> new RuntimeException("Provincia Tarma no encontrada"))),
                new Distrito(null, "San Pedro de Cajas", "Distrito de San Pedro de Cajas", true, provinciaRepository.findByNombre("Tarma").orElseThrow(() -> new RuntimeException("Provincia Tarma no encontrada"))),
                new Distrito(null, "Tapo", "Distrito de Tapo", true, provinciaRepository.findByNombre("Tarma").orElseThrow(() -> new RuntimeException("Provincia Tarma no encontrada"))),

                // [Provincia de Yauli]
                new Distrito(null, "La Oroya", "Distrito de La Oroya", true, provinciaRepository.findByNombre("Yauli").orElseThrow(() -> new RuntimeException("Provincia Yauli no encontrada"))),
                new Distrito(null, "Chacapalpa", "Distrito de Chacapalpa", true, provinciaRepository.findByNombre("Yauli").orElseThrow(() -> new RuntimeException("Provincia Yauli no encontrada"))),
                new Distrito(null, "Huay-Huay", "Distrito de Huay-Huay", true, provinciaRepository.findByNombre("Yauli").orElseThrow(() -> new RuntimeException("Provincia Yauli no encontrada"))),
                new Distrito(null, "Marcapomacocha", "Distrito de Marcapomacocha", true, provinciaRepository.findByNombre("Yauli").orElseThrow(() -> new RuntimeException("Provincia Yauli no encontrada"))),
                new Distrito(null, "Morococha", "Distrito de Morococha", true, provinciaRepository.findByNombre("Yauli").orElseThrow(() -> new RuntimeException("Provincia Yauli no encontrada"))),
                new Distrito(null, "Paccha", "Distrito de Paccha", true, provinciaRepository.findByNombre("Yauli").orElseThrow(() -> new RuntimeException("Provincia Yauli no encontrada"))),
                new Distrito(null, "Santa Bárbara de Carhuacayán", "Distrito de Santa Bárbara de Carhuacayán", true, provinciaRepository.findByNombre("Yauli").orElseThrow(() -> new RuntimeException("Provincia Yauli no encontrada"))),
                new Distrito(null, "Santa Rosa de Sacco", "Distrito de Santa Rosa de Sacco", true, provinciaRepository.findByNombre("Yauli").orElseThrow(() -> new RuntimeException("Provincia Yauli no encontrada"))),
                new Distrito(null, "Suitucancha", "Distrito de Suitucancha", true, provinciaRepository.findByNombre("Yauli").orElseThrow(() -> new RuntimeException("Provincia Yauli no encontrada"))),
                new Distrito(null, "Yauli", "Distrito de Yauli", true, provinciaRepository.findByNombre("Yauli").orElseThrow(() -> new RuntimeException("Provincia Yauli no encontrada"))),


                // [Provincia de Chupaca]
                new Distrito(null, "Chupaca", "Distrito de Chupaca", true, provinciaRepository.findByNombre("Chupaca").orElseThrow(() -> new RuntimeException("Provincia Chupaca no encontrada"))),
                new Distrito(null, "Ahuac", "Distrito de Ahuac", true, provinciaRepository.findByNombre("Chupaca").orElseThrow(() -> new RuntimeException("Provincia Chupaca no encontrada"))),
                new Distrito(null, "Chongos Bajo", "Distrito de Chongos Bajo", true, provinciaRepository.findByNombre("Chupaca").orElseThrow(() -> new RuntimeException("Provincia Chupaca no encontrada"))),
                new Distrito(null, "Huachac", "Distrito de Huachac", true, provinciaRepository.findByNombre("Chupaca").orElseThrow(() -> new RuntimeException("Provincia Chupaca no encontrada"))),
                new Distrito(null, "Huamancaca Chico", "Distrito de Huamancaca Chico", true, provinciaRepository.findByNombre("Chupaca").orElseThrow(() -> new RuntimeException("Provincia Chupaca no encontrada"))),
                new Distrito(null, "San Juan de Iscos", "Distrito de San Juan de Iscos", true, provinciaRepository.findByNombre("Chupaca").orElseThrow(() -> new RuntimeException("Provincia Chupaca no encontrada"))),
                new Distrito(null, "San Juan de Jarpa", "Distrito de San Juan de Jarpa", true, provinciaRepository.findByNombre("Chupaca").orElseThrow(() -> new RuntimeException("Provincia Chupaca no encontrada"))),
                new Distrito(null, "Tres de Diciembre", "Distrito de Tres de Diciembre", true, provinciaRepository.findByNombre("Chupaca").orElseThrow(() -> new RuntimeException("Provincia Chupaca no encontrada"))),
                new Distrito(null, "Yanacancha", "Distrito de Yanacancha", true, provinciaRepository.findByNombre("Chupaca").orElseThrow(() -> new RuntimeException("Provincia Chupaca no encontrada")))
        );
        distritoRepository.saveAll(distritos);
    }

    private void seedDistritosLaLibertad() {
        List<Distrito> distritos = Arrays.asList(
                // La Libertad
                // [Provincia de Trujillo]
                new Distrito(null, "Trujillo", "Distrito de Trujillo", true, provinciaRepository.findByNombre("Trujillo").orElseThrow(() -> new RuntimeException("Provincia Trujillo no encontrada"))),
                new Distrito(null, "El Porvenir", "Distrito de El Porvenir", true, provinciaRepository.findByNombre("Trujillo").orElseThrow(() -> new RuntimeException("Provincia Trujillo no encontrada"))),
                new Distrito(null, "Florencia de Mora", "Distrito de Florencia de Mora", true, provinciaRepository.findByNombre("Trujillo").orElseThrow(() -> new RuntimeException("Provincia Trujillo no encontrada"))),
                new Distrito(null, "Huanchaco", "Distrito de Huanchaco", true, provinciaRepository.findByNombre("Trujillo").orElseThrow(() -> new RuntimeException("Provincia Trujillo no encontrada"))),
                new Distrito(null, "La Esperanza", "Distrito de La Esperanza", true, provinciaRepository.findByNombre("Trujillo").orElseThrow(() -> new RuntimeException("Provincia Trujillo no encontrada"))),
                new Distrito(null, "Laredo", "Distrito de Laredo", true, provinciaRepository.findByNombre("Trujillo").orElseThrow(() -> new RuntimeException("Provincia Trujillo no encontrada"))),
                new Distrito(null, "Moche", "Distrito de Moche", true, provinciaRepository.findByNombre("Trujillo").orElseThrow(() -> new RuntimeException("Provincia Trujillo no encontrada"))),
                new Distrito(null, "Poroto", "Distrito de Poroto", true, provinciaRepository.findByNombre("Trujillo").orElseThrow(() -> new RuntimeException("Provincia Trujillo no encontrada"))),
                new Distrito(null, "Salaverry", "Distrito de Salaverry", true, provinciaRepository.findByNombre("Trujillo").orElseThrow(() -> new RuntimeException("Provincia Trujillo no encontrada"))),
                new Distrito(null, "Simbal", "Distrito de Simbal", true, provinciaRepository.findByNombre("Trujillo").orElseThrow(() -> new RuntimeException("Provincia Trujillo no encontrada"))),
                new Distrito(null, "Víctor Larco Herrera", "Distrito de Víctor Larco Herrera", true, provinciaRepository.findByNombre("Trujillo").orElseThrow(() -> new RuntimeException("Provincia Trujillo no encontrada"))),

                // [Provincia de Ascope]
                new Distrito(null, "Ascope", "Distrito de Ascope", true, provinciaRepository.findByNombre("Ascope").orElseThrow(() -> new RuntimeException("Provincia Ascope no encontrada"))),
                new Distrito(null, "Chicama", "Distrito de Chicama", true, provinciaRepository.findByNombre("Ascope").orElseThrow(() -> new RuntimeException("Provincia Ascope no encontrada"))),
                new Distrito(null, "Chocope", "Distrito de Chocope", true, provinciaRepository.findByNombre("Ascope").orElseThrow(() -> new RuntimeException("Provincia Ascope no encontrada"))),
                new Distrito(null, "Magdalena de Cao", "Distrito de Magdalena de Cao", true, provinciaRepository.findByNombre("Ascope").orElseThrow(() -> new RuntimeException("Provincia Ascope no encontrada"))),
                new Distrito(null, "Paiján", "Distrito de Paiján", true, provinciaRepository.findByNombre("Ascope").orElseThrow(() -> new RuntimeException("Provincia Ascope no encontrada"))),
                new Distrito(null, "Rázuri", "Distrito de Rázuri", true, provinciaRepository.findByNombre("Ascope").orElseThrow(() -> new RuntimeException("Provincia Ascope no encontrada"))),
                new Distrito(null, "Santiago de Cao", "Distrito de Santiago de Cao", true, provinciaRepository.findByNombre("Ascope").orElseThrow(() -> new RuntimeException("Provincia Ascope no encontrada"))),
                new Distrito(null, "Casa Grande", "Distrito de Casa Grande", true, provinciaRepository.findByNombre("Ascope").orElseThrow(() -> new RuntimeException("Provincia Ascope no encontrada"))),

                // [Provincia de Bolívar]
                new Distrito(null, "Bolívar", "Distrito de Bolívar", true, provinciaRepository.findByNombre("Bolívar").orElseThrow(() -> new RuntimeException("Provincia Bolívar no encontrada"))),
                new Distrito(null, "Bambamarca", "Distrito de Bambamarca", true, provinciaRepository.findByNombre("Bolívar").orElseThrow(() -> new RuntimeException("Provincia Bolívar no encontrada"))),
                new Distrito(null, "Condormarca", "Distrito de Condormarca", true, provinciaRepository.findByNombre("Bolívar").orElseThrow(() -> new RuntimeException("Provincia Bolívar no encontrada"))),
                new Distrito(null, "Longotea", "Distrito de Longotea", true, provinciaRepository.findByNombre("Bolívar").orElseThrow(() -> new RuntimeException("Provincia Bolívar no encontrada"))),
                new Distrito(null, "Uchumarca", "Distrito de Uchumarca", true, provinciaRepository.findByNombre("Bolívar").orElseThrow(() -> new RuntimeException("Provincia Bolívar no encontrada"))),
                new Distrito(null, "Ucuncha", "Distrito de Ucuncha", true, provinciaRepository.findByNombre("Bolívar").orElseThrow(() -> new RuntimeException("Provincia Bolívar no encontrada"))),

                // [Provincia de Chepén]
                new Distrito(null, "Chepén", "Distrito de Chepén", true, provinciaRepository.findByNombre("Chepén").orElseThrow(() -> new RuntimeException("Provincia Chepén no encontrada"))),
                new Distrito(null, "Pacanga", "Distrito de Pacanga", true, provinciaRepository.findByNombre("Chepén").orElseThrow(() -> new RuntimeException("Provincia Chepén no encontrada"))),
                new Distrito(null, "Pueblo Nuevo", "Distrito de Pueblo Nuevo", true, provinciaRepository.findByNombre("Chepén").orElseThrow(() -> new RuntimeException("Provincia Chepén no encontrada"))),

                // [Provincia de Julcán]
                new Distrito(null, "Julcán", "Distrito de Julcán", true, provinciaRepository.findByNombre("Julcán").orElseThrow(() -> new RuntimeException("Provincia Julcán no encontrada"))),
                new Distrito(null, "Calamarca", "Distrito de Calamarca", true, provinciaRepository.findByNombre("Julcán").orElseThrow(() -> new RuntimeException("Provincia Julcán no encontrada"))),
                new Distrito(null, "Carabamba", "Distrito de Carabamba", true, provinciaRepository.findByNombre("Julcán").orElseThrow(() -> new RuntimeException("Provincia Julcán no encontrada"))),
                new Distrito(null, "Huaso", "Distrito de Huaso", true, provinciaRepository.findByNombre("Julcán").orElseThrow(() -> new RuntimeException("Provincia Julcán no encontrada"))),

                // [Provincia de Otuzco]
                new Distrito(null, "Otuzco", "Distrito de Otuzco", true, provinciaRepository.findByNombre("Otuzco").orElseThrow(() -> new RuntimeException("Provincia Otuzco no encontrada"))),
                new Distrito(null, "Agallpampa", "Distrito de Agallpampa", true, provinciaRepository.findByNombre("Otuzco").orElseThrow(() -> new RuntimeException("Provincia Otuzco no encontrada"))),
                new Distrito(null, "Charat", "Distrito de Charat", true, provinciaRepository.findByNombre("Otuzco").orElseThrow(() -> new RuntimeException("Provincia Otuzco no encontrada"))),
                new Distrito(null, "Huaranchal", "Distrito de Huaranchal", true, provinciaRepository.findByNombre("Otuzco").orElseThrow(() -> new RuntimeException("Provincia Otuzco no encontrada"))),
                new Distrito(null, "La Cuesta", "Distrito de La Cuesta", true, provinciaRepository.findByNombre("Otuzco").orElseThrow(() -> new RuntimeException("Provincia Otuzco no encontrada"))),
                new Distrito(null, "Mache", "Distrito de Mache", true, provinciaRepository.findByNombre("Otuzco").orElseThrow(() -> new RuntimeException("Provincia Otuzco no encontrada"))),
                new Distrito(null, "Paranday", "Distrito de Paranday", true, provinciaRepository.findByNombre("Otuzco").orElseThrow(() -> new RuntimeException("Provincia Otuzco no encontrada"))),
                new Distrito(null, "Salpo", "Distrito de Salpo", true, provinciaRepository.findByNombre("Otuzco").orElseThrow(() -> new RuntimeException("Provincia Otuzco no encontrada"))),
                new Distrito(null, "Sinsicap", "Distrito de Sinsicap", true, provinciaRepository.findByNombre("Otuzco").orElseThrow(() -> new RuntimeException("Provincia Otuzco no encontrada"))),
                new Distrito(null, "Usquil", "Distrito de Usquil", true, provinciaRepository.findByNombre("Otuzco").orElseThrow(() -> new RuntimeException("Provincia Otuzco no encontrada"))),

                // [Provincia de Pacasmayo]
                new Distrito(null, "San Pedro de Lloc", "Distrito de San Pedro de Lloc", true, provinciaRepository.findByNombre("Pacasmayo").orElseThrow(() -> new RuntimeException("Provincia Pacasmayo no encontrada"))),
                new Distrito(null, "Guadalupe", "Distrito de Guadalupe", true, provinciaRepository.findByNombre("Pacasmayo").orElseThrow(() -> new RuntimeException("Provincia Pacasmayo no encontrada"))),
                new Distrito(null, "Jequetepeque", "Distrito de Jequetepeque", true, provinciaRepository.findByNombre("Pacasmayo").orElseThrow(() -> new RuntimeException("Provincia Pacasmayo no encontrada"))),
                new Distrito(null, "Pacasmayo", "Distrito de Pacasmayo", true, provinciaRepository.findByNombre("Pacasmayo").orElseThrow(() -> new RuntimeException("Provincia Pacasmayo no encontrada"))),
                new Distrito(null, "San José", "Distrito de San José", true, provinciaRepository.findByNombre("Pacasmayo").orElseThrow(() -> new RuntimeException("Provincia Pacasmayo no encontrada"))),

                // [Provincia de Pataz]
                new Distrito(null, "Tayabamba", "Distrito de Tayabamba", true, provinciaRepository.findByNombre("Pataz").orElseThrow(() -> new RuntimeException("Provincia Pataz no encontrada"))),
                new Distrito(null, "Buldibuyo", "Distrito de Buldibuyo", true, provinciaRepository.findByNombre("Pataz").orElseThrow(() -> new RuntimeException("Provincia Pataz no encontrada"))),
                new Distrito(null, "Chillia", "Distrito de Chillia", true, provinciaRepository.findByNombre("Pataz").orElseThrow(() -> new RuntimeException("Provincia Pataz no encontrada"))),
                new Distrito(null, "Huancaspata", "Distrito de Huancaspata", true, provinciaRepository.findByNombre("Pataz").orElseThrow(() -> new RuntimeException("Provincia Pataz no encontrada"))),
                new Distrito(null, "Huaylillas", "Distrito de Huaylillas", true, provinciaRepository.findByNombre("Pataz").orElseThrow(() -> new RuntimeException("Provincia Pataz no encontrada"))),
                new Distrito(null, "Huayo", "Distrito de Huayo", true, provinciaRepository.findByNombre("Pataz").orElseThrow(() -> new RuntimeException("Provincia Pataz no encontrada"))),
                new Distrito(null, "Ongon", "Distrito de Ongon", true, provinciaRepository.findByNombre("Pataz").orElseThrow(() -> new RuntimeException("Provincia Pataz no encontrada"))),
                new Distrito(null, "Parcoy", "Distrito de Parcoy", true, provinciaRepository.findByNombre("Pataz").orElseThrow(() -> new RuntimeException("Provincia Pataz no encontrada"))),
                new Distrito(null, "Pataz", "Distrito de Pataz", true, provinciaRepository.findByNombre("Pataz").orElseThrow(() -> new RuntimeException("Provincia Pataz no encontrada"))),
                new Distrito(null, "Pias", "Distrito de Pias", true, provinciaRepository.findByNombre("Pataz").orElseThrow(() -> new RuntimeException("Provincia Pataz no encontrada"))),
                new Distrito(null, "Santiago de Challas", "Distrito de Santiago de Challas", true, provinciaRepository.findByNombre("Pataz").orElseThrow(() -> new RuntimeException("Provincia Pataz no encontrada"))),
                new Distrito(null, "Taurija", "Distrito de Taurija", true, provinciaRepository.findByNombre("Pataz").orElseThrow(() -> new RuntimeException("Provincia Pataz no encontrada"))),
                new Distrito(null, "Urpay", "Distrito de Urpay", true, provinciaRepository.findByNombre("Pataz").orElseThrow(() -> new RuntimeException("Provincia Pataz no encontrada"))),

                // [Provincia de Sánchez Carrión]
                new Distrito(null, "Huamachuco", "Distrito de Huamachuco", true, provinciaRepository.findByNombre("Sánchez Carrión").orElseThrow(() -> new RuntimeException("Provincia Sánchez Carrión no encontrada"))),
                new Distrito(null, "Chugay", "Distrito de Chugay", true, provinciaRepository.findByNombre("Sánchez Carrión").orElseThrow(() -> new RuntimeException("Provincia Sánchez Carrión no encontrada"))),
                new Distrito(null, "Cochorco", "Distrito de Cochorco", true, provinciaRepository.findByNombre("Sánchez Carrión").orElseThrow(() -> new RuntimeException("Provincia Sánchez Carrión no encontrada"))),
                new Distrito(null, "Curgos", "Distrito de Curgos", true, provinciaRepository.findByNombre("Sánchez Carrión").orElseThrow(() -> new RuntimeException("Provincia Sánchez Carrión no encontrada"))),
                new Distrito(null, "Marcabal", "Distrito de Marcabal", true, provinciaRepository.findByNombre("Sánchez Carrión").orElseThrow(() -> new RuntimeException("Provincia Sánchez Carrión no encontrada"))),
                new Distrito(null, "Sanagorán", "Distrito de Sanagorán", true, provinciaRepository.findByNombre("Sánchez Carrión").orElseThrow(() -> new RuntimeException("Provincia Sánchez Carrión no encontrada"))),
                new Distrito(null, "Sarín", "Distrito de Sarín", true, provinciaRepository.findByNombre("Sánchez Carrión").orElseThrow(() -> new RuntimeException("Provincia Sánchez Carrión no encontrada"))),
                new Distrito(null, "Sartimbamba", "Distrito de Sartimbamba", true, provinciaRepository.findByNombre("Sánchez Carrión").orElseThrow(() -> new RuntimeException("Provincia Sánchez Carrión no encontrada"))),

                // [Provincia de Santiago de Chuco]
                new Distrito(null, "Santiago de Chuco", "Distrito de Santiago de Chuco", true, provinciaRepository.findByNombre("Santiago de Chuco").orElseThrow(() -> new RuntimeException("Provincia Santiago de Chuco no encontrada"))),
                new Distrito(null, "Angasmarca", "Distrito de Angasmarca", true, provinciaRepository.findByNombre("Santiago de Chuco").orElseThrow(() -> new RuntimeException("Provincia Santiago de Chuco no encontrada"))),
                new Distrito(null, "Cachicadan", "Distrito de Cachicadan", true, provinciaRepository.findByNombre("Santiago de Chuco").orElseThrow(() -> new RuntimeException("Provincia Santiago de Chuco no encontrada"))),
                new Distrito(null, "Mollebamba", "Distrito de Mollebamba", true, provinciaRepository.findByNombre("Santiago de Chuco").orElseThrow(() -> new RuntimeException("Provincia Santiago de Chuco no encontrada"))),
                new Distrito(null, "Mollepata", "Distrito de Mollepata", true, provinciaRepository.findByNombre("Santiago de Chuco").orElseThrow(() -> new RuntimeException("Provincia Santiago de Chuco no encontrada"))),
                new Distrito(null, "Quiruvilca", "Distrito de Quiruvilca", true, provinciaRepository.findByNombre("Santiago de Chuco").orElseThrow(() -> new RuntimeException("Provincia Santiago de Chuco no encontrada"))),
                new Distrito(null, "Santa Cruz de Chuca", "Distrito de Santa Cruz de Chuca", true, provinciaRepository.findByNombre("Santiago de Chuco").orElseThrow(() -> new RuntimeException("Provincia Santiago de Chuco no encontrada"))),
                new Distrito(null, "Sitabamba", "Distrito de Sitabamba", true, provinciaRepository.findByNombre("Santiago de Chuco").orElseThrow(() -> new RuntimeException("Provincia Santiago de Chuco no encontrada"))),

                // [Provincia de Gran Chimú]
                new Distrito(null, "Cascas", "Distrito de Cascas", true, provinciaRepository.findByNombre("Gran Chimú").orElseThrow(() -> new RuntimeException("Provincia Gran Chimú no encontrada"))),
                new Distrito(null, "Lucma", "Distrito de Lucma", true, provinciaRepository.findByNombre("Gran Chimú").orElseThrow(() -> new RuntimeException("Provincia Gran Chimú no encontrada"))),
                new Distrito(null, "Marmot", "Distrito de Marmot", true, provinciaRepository.findByNombre("Gran Chimú").orElseThrow(() -> new RuntimeException("Provincia Gran Chimú no encontrada"))),
                new Distrito(null, "Sayapullo", "Distrito de Sayapullo", true, provinciaRepository.findByNombre("Gran Chimú").orElseThrow(() -> new RuntimeException("Provincia Gran Chimú no encontrada"))),

                // [Provincia de Virú]
                new Distrito(null, "Virú", "Distrito de Virú", true, provinciaRepository.findByNombre("Virú").orElseThrow(() -> new RuntimeException("Provincia Virú no encontrada"))),
                new Distrito(null, "Chao", "Distrito de Chao", true, provinciaRepository.findByNombre("Virú").orElseThrow(() -> new RuntimeException("Provincia Virú no encontrada"))),
                new Distrito(null, "Guadalupito", "Distrito de Guadalupito", true, provinciaRepository.findByNombre("Virú").orElseThrow(() -> new RuntimeException("Provincia Virú no encontrada")))
        );
        distritoRepository.saveAll(distritos);
    }

    private void seedDistritosLambayeque() {
        List<Distrito> distritos = Arrays.asList(
                // Lambayeque
                // [Provincia de Chiclayo]
                new Distrito(null, "Chiclayo", "Distrito de Chiclayo", true, provinciaRepository.findByNombre("Chiclayo").orElseThrow(() -> new RuntimeException("Provincia Chiclayo no encontrada"))),
                new Distrito(null, "Chongoyape", "Distrito de Chongoyape", true, provinciaRepository.findByNombre("Chiclayo").orElseThrow(() -> new RuntimeException("Provincia Chiclayo no encontrada"))),
                new Distrito(null, "Eten", "Distrito de Eten", true, provinciaRepository.findByNombre("Chiclayo").orElseThrow(() -> new RuntimeException("Provincia Chiclayo no encontrada"))),
                new Distrito(null, "Eten Puerto", "Distrito de Eten Puerto", true, provinciaRepository.findByNombre("Chiclayo").orElseThrow(() -> new RuntimeException("Provincia Chiclayo no encontrada"))),
                new Distrito(null, "José Leonardo Ortiz", "Distrito de José Leonardo Ortiz", true, provinciaRepository.findByNombre("Chiclayo").orElseThrow(() -> new RuntimeException("Provincia Chiclayo no encontrada"))),
                new Distrito(null, "La Victoria", "Distrito de La Victoria", true, provinciaRepository.findByNombre("Chiclayo").orElseThrow(() -> new RuntimeException("Provincia Chiclayo no encontrada"))),
                new Distrito(null, "Lagunas", "Distrito de Lagunas", true, provinciaRepository.findByNombre("Chiclayo").orElseThrow(() -> new RuntimeException("Provincia Chiclayo no encontrada"))),
                new Distrito(null, "Monsefú", "Distrito de Monsefú", true, provinciaRepository.findByNombre("Chiclayo").orElseThrow(() -> new RuntimeException("Provincia Chiclayo no encontrada"))),
                new Distrito(null, "Nueva Arica", "Distrito de Nueva Arica", true, provinciaRepository.findByNombre("Chiclayo").orElseThrow(() -> new RuntimeException("Provincia Chiclayo no encontrada"))),
                new Distrito(null, "Oyotun", "Distrito de Oyotun", true, provinciaRepository.findByNombre("Chiclayo").orElseThrow(() -> new RuntimeException("Provincia Chiclayo no encontrada"))),
                new Distrito(null, "Picsi", "Distrito de Picsi", true, provinciaRepository.findByNombre("Chiclayo").orElseThrow(() -> new RuntimeException("Provincia Chiclayo no encontrada"))),
                new Distrito(null, "Pimentel", "Distrito de Pimentel", true, provinciaRepository.findByNombre("Chiclayo").orElseThrow(() -> new RuntimeException("Provincia Chiclayo no encontrada"))),
                new Distrito(null, "Reque", "Distrito de Reque", true, provinciaRepository.findByNombre("Chiclayo").orElseThrow(() -> new RuntimeException("Provincia Chiclayo no encontrada"))),
                new Distrito(null, "Santa Rosa", "Distrito de Santa Rosa", true, provinciaRepository.findByNombre("Chiclayo").orElseThrow(() -> new RuntimeException("Provincia Chiclayo no encontrada"))),
                new Distrito(null, "Saña", "Distrito de Saña", true, provinciaRepository.findByNombre("Chiclayo").orElseThrow(() -> new RuntimeException("Provincia Chiclayo no encontrada"))),
                new Distrito(null, "Cayalti", "Distrito de Cayalti", true, provinciaRepository.findByNombre("Chiclayo").orElseThrow(() -> new RuntimeException("Provincia Chiclayo no encontrada"))),
                new Distrito(null, "Patapo", "Distrito de Patapo", true, provinciaRepository.findByNombre("Chiclayo").orElseThrow(() -> new RuntimeException("Provincia Chiclayo no encontrada"))),
                new Distrito(null, "Pomalca", "Distrito de Pomalca", true, provinciaRepository.findByNombre("Chiclayo").orElseThrow(() -> new RuntimeException("Provincia Chiclayo no encontrada"))),
                new Distrito(null, "Pucala", "Distrito de Pucala", true, provinciaRepository.findByNombre("Chiclayo").orElseThrow(() -> new RuntimeException("Provincia Chiclayo no encontrada"))),
                new Distrito(null, "Tumán", "Distrito de Tumán", true, provinciaRepository.findByNombre("Chiclayo").orElseThrow(() -> new RuntimeException("Provincia Chiclayo no encontrada"))),

                // [Provincia de Ferreñafe]
                new Distrito(null, "Ferreñafe", "Distrito de Ferreñafe", true, provinciaRepository.findByNombre("Ferreñafe").orElseThrow(() -> new RuntimeException("Provincia Ferreñafe no encontrada"))),
                new Distrito(null, "Cañaris", "Distrito de Cañaris", true, provinciaRepository.findByNombre("Ferreñafe").orElseThrow(() -> new RuntimeException("Provincia Ferreñafe no encontrada"))),
                new Distrito(null, "Incahuasi", "Distrito de Incahuasi", true, provinciaRepository.findByNombre("Ferreñafe").orElseThrow(() -> new RuntimeException("Provincia Ferreñafe no encontrada"))),
                new Distrito(null, "Manuel Antonio Mesones Muro", "Distrito de Manuel Antonio Mesones Muro", true, provinciaRepository.findByNombre("Ferreñafe").orElseThrow(() -> new RuntimeException("Provincia Ferreñafe no encontrada"))),
                new Distrito(null, "Pítipo", "Distrito de Pítipo", true, provinciaRepository.findByNombre("Ferreñafe").orElseThrow(() -> new RuntimeException("Provincia Ferreñafe no encontrada"))),
                new Distrito(null, "Pueblo Nuevo", "Distrito de Pueblo Nuevo", true, provinciaRepository.findByNombre("Ferreñafe").orElseThrow(() -> new RuntimeException("Provincia Ferreñafe no encontrada"))),

                // [Provincia de Lambayeque]
                new Distrito(null, "Lambayeque", "Distrito de Lambayeque", true, provinciaRepository.findByNombre("Lambayeque").orElseThrow(() -> new RuntimeException("Provincia Lambayeque no encontrada"))),
                new Distrito(null, "Chóchope", "Distrito de Chóchope", true, provinciaRepository.findByNombre("Lambayeque").orElseThrow(() -> new RuntimeException("Provincia Lambayeque no encontrada"))),
                new Distrito(null, "Illimo", "Distrito de Illimo", true, provinciaRepository.findByNombre("Lambayeque").orElseThrow(() -> new RuntimeException("Provincia Lambayeque no encontrada"))),
                new Distrito(null, "Jayanca", "Distrito de Jayanca", true, provinciaRepository.findByNombre("Lambayeque").orElseThrow(() -> new RuntimeException("Provincia Lambayeque no encontrada"))),
                new Distrito(null, "Mochumí", "Distrito de Mochumí", true, provinciaRepository.findByNombre("Lambayeque").orElseThrow(() -> new RuntimeException("Provincia Lambayeque no encontrada"))),
                new Distrito(null, "Morrope", "Distrito de Morrope", true, provinciaRepository.findByNombre("Lambayeque").orElseThrow(() -> new RuntimeException("Provincia Lambayeque no encontrada"))),
                new Distrito(null, "Motupe", "Distrito de Motupe", true, provinciaRepository.findByNombre("Lambayeque").orElseThrow(() -> new RuntimeException("Provincia Lambayeque no encontrada"))),
                new Distrito(null, "Olmos", "Distrito de Olmos", true, provinciaRepository.findByNombre("Lambayeque").orElseThrow(() -> new RuntimeException("Provincia Lambayeque no encontrada"))),
                new Distrito(null, "Pacora", "Distrito de Pacora", true, provinciaRepository.findByNombre("Lambayeque").orElseThrow(() -> new RuntimeException("Provincia Lambayeque no encontrada"))),
                new Distrito(null, "Salas", "Distrito de Salas", true, provinciaRepository.findByNombre("Lambayeque").orElseThrow(() -> new RuntimeException("Provincia Lambayeque no encontrada"))),
                new Distrito(null, "San José", "Distrito de San José", true, provinciaRepository.findByNombre("Lambayeque").orElseThrow(() -> new RuntimeException("Provincia Lambayeque no encontrada"))),
                new Distrito(null, "Túcume", "Distrito de Túcume", true, provinciaRepository.findByNombre("Lambayeque").orElseThrow(() -> new RuntimeException("Provincia Lambayeque no encontrada")))
        );
        distritoRepository.saveAll(distritos);
    }

    private void seedDistritosLima() {
        List<Distrito> distritos = Arrays.asList(
                // Lima
                // [Provincia de Lima]
                new Distrito(null, "Lima", "Distrito de Lima", true, provinciaRepository.findByNombre("Lima").orElseThrow(() -> new RuntimeException("Provincia Lima no encontrada"))),
                new Distrito(null, "Ancón", "Distrito de Ancón", true, provinciaRepository.findByNombre("Lima").orElseThrow(() -> new RuntimeException("Provincia Lima no encontrada"))),
                new Distrito(null, "Ate", "Distrito de Ate", true, provinciaRepository.findByNombre("Lima").orElseThrow(() -> new RuntimeException("Provincia Lima no encontrada"))),
                new Distrito(null, "Barranco", "Distrito de Barranco", true, provinciaRepository.findByNombre("Lima").orElseThrow(() -> new RuntimeException("Provincia Lima no encontrada"))),
                new Distrito(null, "Breña", "Distrito de Breña", true, provinciaRepository.findByNombre("Lima").orElseThrow(() -> new RuntimeException("Provincia Lima no encontrada"))),
                new Distrito(null, "Carabayllo", "Distrito de Carabayllo", true, provinciaRepository.findByNombre("Lima").orElseThrow(() -> new RuntimeException("Provincia Lima no encontrada"))),
                new Distrito(null, "Chaclacayo", "Distrito de Chaclacayo", true, provinciaRepository.findByNombre("Lima").orElseThrow(() -> new RuntimeException("Provincia Lima no encontrada"))),
                new Distrito(null, "Chorrillos", "Distrito de Chorrillos", true, provinciaRepository.findByNombre("Lima").orElseThrow(() -> new RuntimeException("Provincia Lima no encontrada"))),
                new Distrito(null, "Cieneguilla", "Distrito de Cieneguilla", true, provinciaRepository.findByNombre("Lima").orElseThrow(() -> new RuntimeException("Provincia Lima no encontrada"))),
                new Distrito(null, "Comas", "Distrito de Comas", true, provinciaRepository.findByNombre("Lima").orElseThrow(() -> new RuntimeException("Provincia Lima no encontrada"))),
                new Distrito(null, "El Agustino", "Distrito de El Agustino", true, provinciaRepository.findByNombre("Lima").orElseThrow(() -> new RuntimeException("Provincia Lima no encontrada"))),
                new Distrito(null, "Independencia", "Distrito de Independencia", true, provinciaRepository.findByNombre("Lima").orElseThrow(() -> new RuntimeException("Provincia Lima no encontrada"))),
                new Distrito(null, "Jesús María", "Distrito de Jesús María", true, provinciaRepository.findByNombre("Lima").orElseThrow(() -> new RuntimeException("Provincia Lima no encontrada"))),
                new Distrito(null, "La Molina", "Distrito de La Molina", true, provinciaRepository.findByNombre("Lima").orElseThrow(() -> new RuntimeException("Provincia Lima no encontrada"))),
                new Distrito(null, "La Victoria", "Distrito de La Victoria", true, provinciaRepository.findByNombre("Lima").orElseThrow(() -> new RuntimeException("Provincia Lima no encontrada"))),
                new Distrito(null, "Lince", "Distrito de Lince", true, provinciaRepository.findByNombre("Lima").orElseThrow(() -> new RuntimeException("Provincia Lima no encontrada"))),
                new Distrito(null, "Los Olivos", "Distrito de Los Olivos", true, provinciaRepository.findByNombre("Lima").orElseThrow(() -> new RuntimeException("Provincia Lima no encontrada"))),
                new Distrito(null, "Lurigancho", "Distrito de Lurigancho", true, provinciaRepository.findByNombre("Lima").orElseThrow(() -> new RuntimeException("Provincia Lima no encontrada"))),
                new Distrito(null, "Lurín", "Distrito de Lurín", true, provinciaRepository.findByNombre("Lima").orElseThrow(() -> new RuntimeException("Provincia Lima no encontrada"))),
                new Distrito(null, "Magdalena del Mar", "Distrito de Magdalena del Mar", true, provinciaRepository.findByNombre("Lima").orElseThrow(() -> new RuntimeException("Provincia Lima no encontrada"))),
                new Distrito(null, "Pueblo Libre", "Distrito de Pueblo Libre", true, provinciaRepository.findByNombre("Lima").orElseThrow(() -> new RuntimeException("Provincia Lima no encontrada"))),
                new Distrito(null, "Miraflores", "Distrito de Miraflores", true, provinciaRepository.findByNombre("Lima").orElseThrow(() -> new RuntimeException("Provincia Lima no encontrada"))),
                new Distrito(null, "Pachacámac", "Distrito de Pachacámac", true, provinciaRepository.findByNombre("Lima").orElseThrow(() -> new RuntimeException("Provincia Lima no encontrada"))),
                new Distrito(null, "Pucusana", "Distrito de Pucusana", true, provinciaRepository.findByNombre("Lima").orElseThrow(() -> new RuntimeException("Provincia Lima no encontrada"))),
                new Distrito(null, "Puente Piedra", "Distrito de Puente Piedra", true, provinciaRepository.findByNombre("Lima").orElseThrow(() -> new RuntimeException("Provincia Lima no encontrada"))),
                new Distrito(null, "Punta Hermosa", "Distrito de Punta Hermosa", true, provinciaRepository.findByNombre("Lima").orElseThrow(() -> new RuntimeException("Provincia Lima no encontrada"))),
                new Distrito(null, "Punta Negra", "Distrito de Punta Negra", true, provinciaRepository.findByNombre("Lima").orElseThrow(() -> new RuntimeException("Provincia Lima no encontrada"))),
                new Distrito(null, "Rímac", "Distrito de Rímac", true, provinciaRepository.findByNombre("Lima").orElseThrow(() -> new RuntimeException("Provincia Lima no encontrada"))),
                new Distrito(null, "San Bartolo", "Distrito de San Bartolo", true, provinciaRepository.findByNombre("Lima").orElseThrow(() -> new RuntimeException("Provincia Lima no encontrada"))),
                new Distrito(null, "San Borja", "Distrito de San Borja", true, provinciaRepository.findByNombre("Lima").orElseThrow(() -> new RuntimeException("Provincia Lima no encontrada"))),
                new Distrito(null, "San Isidro", "Distrito de San Isidro", true, provinciaRepository.findByNombre("Lima").orElseThrow(() -> new RuntimeException("Provincia Lima no encontrada"))),
                new Distrito(null, "San Juan de Lurigancho", "Distrito de San Juan de Lurigancho", true, provinciaRepository.findByNombre("Lima").orElseThrow(() -> new RuntimeException("Provincia Lima no encontrada"))),
                new Distrito(null, "San Juan de Miraflores", "Distrito de San Juan de Miraflores", true, provinciaRepository.findByNombre("Lima").orElseThrow(() -> new RuntimeException("Provincia Lima no encontrada"))),
                new Distrito(null, "San Luis", "Distrito de San Luis", true, provinciaRepository.findByNombre("Lima").orElseThrow(() -> new RuntimeException("Provincia Lima no encontrada"))),
                new Distrito(null, "San Martín de Porres", "Distrito de San Martín de Porres", true, provinciaRepository.findByNombre("Lima").orElseThrow(() -> new RuntimeException("Provincia Lima no encontrada"))),
                new Distrito(null, "San Miguel", "Distrito de San Miguel", true, provinciaRepository.findByNombre("Lima").orElseThrow(() -> new RuntimeException("Provincia Lima no encontrada"))),
                new Distrito(null, "Santa Anita", "Distrito de Santa Anita", true, provinciaRepository.findByNombre("Lima").orElseThrow(() -> new RuntimeException("Provincia Lima no encontrada"))),
                new Distrito(null, "Santa María del Mar", "Distrito de Santa María del Mar", true, provinciaRepository.findByNombre("Lima").orElseThrow(() -> new RuntimeException("Provincia Lima no encontrada"))),
                new Distrito(null, "Santa Rosa", "Distrito de Santa Rosa", true, provinciaRepository.findByNombre("Lima").orElseThrow(() -> new RuntimeException("Provincia Lima no encontrada"))),
                new Distrito(null, "Santiago de Surco", "Distrito de Santiago de Surco", true, provinciaRepository.findByNombre("Lima").orElseThrow(() -> new RuntimeException("Provincia Lima no encontrada"))),
                new Distrito(null, "Surquillo", "Distrito de Surquillo", true, provinciaRepository.findByNombre("Lima").orElseThrow(() -> new RuntimeException("Provincia Lima no encontrada"))),
                new Distrito(null, "Villa El Salvador", "Distrito de Villa El Salvador", true, provinciaRepository.findByNombre("Lima").orElseThrow(() -> new RuntimeException("Provincia Lima no encontrada"))),
                new Distrito(null, "Villa María del Triunfo", "Distrito de Villa María del Triunfo", true, provinciaRepository.findByNombre("Lima").orElseThrow(() -> new RuntimeException("Provincia Lima no encontrada"))),

                // [Provincia de Barranca]
                new Distrito(null, "Barranca", "Distrito de Barranca", true, provinciaRepository.findByNombre("Barranca").orElseThrow(() -> new RuntimeException("Provincia Barranca no encontrada"))),
                new Distrito(null, "Paramonga", "Distrito de Paramonga", true, provinciaRepository.findByNombre("Barranca").orElseThrow(() -> new RuntimeException("Provincia Barranca no encontrada"))),
                new Distrito(null, "Pativilca", "Distrito de Pativilca", true, provinciaRepository.findByNombre("Barranca").orElseThrow(() -> new RuntimeException("Provincia Barranca no encontrada"))),
                new Distrito(null, "Supe", "Distrito de Supe", true, provinciaRepository.findByNombre("Barranca").orElseThrow(() -> new RuntimeException("Provincia Barranca no encontrada"))),
                new Distrito(null, "Supe Puerto", "Distrito de Supe Puerto", true, provinciaRepository.findByNombre("Barranca").orElseThrow(() -> new RuntimeException("Provincia Barranca no encontrada"))),

                // [Provincia de Cajatambo]
                new Distrito(null, "Cajatambo", "Distrito de Cajatambo", true, provinciaRepository.findByNombre("Cajatambo").orElseThrow(() -> new RuntimeException("Provincia Cajatambo no encontrada"))),
                new Distrito(null, "Copa", "Distrito de Copa", true, provinciaRepository.findByNombre("Cajatambo").orElseThrow(() -> new RuntimeException("Provincia Cajatambo no encontrada"))),
                new Distrito(null, "Gorgor", "Distrito de Gorgor", true, provinciaRepository.findByNombre("Cajatambo").orElseThrow(() -> new RuntimeException("Provincia Cajatambo no encontrada"))),
                new Distrito(null, "Huancapón", "Distrito de Huancapón", true, provinciaRepository.findByNombre("Cajatambo").orElseThrow(() -> new RuntimeException("Provincia Cajatambo no encontrada"))),
                new Distrito(null, "Manás", "Distrito de Manás", true, provinciaRepository.findByNombre("Cajatambo").orElseThrow(() -> new RuntimeException("Provincia Cajatambo no encontrada"))),

                // [Provincia de Canta]
                new Distrito(null, "Canta", "Distrito de Canta", true, provinciaRepository.findByNombre("Canta").orElseThrow(() -> new RuntimeException("Provincia Canta no encontrada"))),
                new Distrito(null, "Arahuay", "Distrito de Arahuay", true, provinciaRepository.findByNombre("Canta").orElseThrow(() -> new RuntimeException("Provincia Canta no encontrada"))),
                new Distrito(null, "Huamantanga", "Distrito de Huamantanga", true, provinciaRepository.findByNombre("Canta").orElseThrow(() -> new RuntimeException("Provincia Canta no encontrada"))),
                new Distrito(null, "Huaros", "Distrito de Huaros", true, provinciaRepository.findByNombre("Canta").orElseThrow(() -> new RuntimeException("Provincia Canta no encontrada"))),
                new Distrito(null, "Lachaqui", "Distrito de Lachaqui", true, provinciaRepository.findByNombre("Canta").orElseThrow(() -> new RuntimeException("Provincia Canta no encontrada"))),
                new Distrito(null, "San Buenaventura", "Distrito de San Buenaventura", true, provinciaRepository.findByNombre("Canta").orElseThrow(() -> new RuntimeException("Provincia Canta no encontrada"))),
                new Distrito(null, "Santa Rosa de Quives", "Distrito de Santa Rosa de Quives", true, provinciaRepository.findByNombre("Canta").orElseThrow(() -> new RuntimeException("Provincia Canta no encontrada"))),

                // [Provincia de Cañete]
                new Distrito(null, "San Vicente de Cañete", "Distrito de San Vicente de Cañete", true, provinciaRepository.findByNombre("Cañete").orElseThrow(() -> new RuntimeException("Provincia Cañete no encontrada"))),
                new Distrito(null, "Asia", "Distrito de Asia", true, provinciaRepository.findByNombre("Cañete").orElseThrow(() -> new RuntimeException("Provincia Cañete no encontrada"))),
                new Distrito(null, "Calango", "Distrito de Calango", true, provinciaRepository.findByNombre("Cañete").orElseThrow(() -> new RuntimeException("Provincia Cañete no encontrada"))),
                new Distrito(null, "Cerro Azul", "Distrito de Cerro Azul", true, provinciaRepository.findByNombre("Cañete").orElseThrow(() -> new RuntimeException("Provincia Cañete no encontrada"))),
                new Distrito(null, "Chilca", "Distrito de Chilca", true, provinciaRepository.findByNombre("Cañete").orElseThrow(() -> new RuntimeException("Provincia Cañete no encontrada"))),
                new Distrito(null, "Coayllo", "Distrito de Coayllo", true, provinciaRepository.findByNombre("Cañete").orElseThrow(() -> new RuntimeException("Provincia Cañete no encontrada"))),
                new Distrito(null, "Imperial", "Distrito de Imperial", true, provinciaRepository.findByNombre("Cañete").orElseThrow(() -> new RuntimeException("Provincia Cañete no encontrada"))),
                new Distrito(null, "Lunahuaná", "Distrito de Lunahuaná", true, provinciaRepository.findByNombre("Cañete").orElseThrow(() -> new RuntimeException("Provincia Cañete no encontrada"))),
                new Distrito(null, "Mala", "Distrito de Mala", true, provinciaRepository.findByNombre("Cañete").orElseThrow(() -> new RuntimeException("Provincia Cañete no encontrada"))),
                new Distrito(null, "Nuevo Imperial", "Distrito de Nuevo Imperial", true, provinciaRepository.findByNombre("Cañete").orElseThrow(() -> new RuntimeException("Provincia Cañete no encontrada"))),
                new Distrito(null, "Pacarán", "Distrito de Pacarán", true, provinciaRepository.findByNombre("Cañete").orElseThrow(() -> new RuntimeException("Provincia Cañete no encontrada"))),
                new Distrito(null, "Quilmaná", "Distrito de Quilmaná", true, provinciaRepository.findByNombre("Cañete").orElseThrow(() -> new RuntimeException("Provincia Cañete no encontrada"))),
                new Distrito(null, "San Antonio", "Distrito de San Antonio", true, provinciaRepository.findByNombre("Cañete").orElseThrow(() -> new RuntimeException("Provincia Cañete no encontrada"))),
                new Distrito(null, "San Luis", "Distrito de San Luis", true, provinciaRepository.findByNombre("Cañete").orElseThrow(() -> new RuntimeException("Provincia Cañete no encontrada"))),
                new Distrito(null, "Santa Cruz de Flores", "Distrito de Santa Cruz de Flores", true, provinciaRepository.findByNombre("Cañete").orElseThrow(() -> new RuntimeException("Provincia Cañete no encontrada"))),
                new Distrito(null, "Zúñiga", "Distrito de Zúñiga", true, provinciaRepository.findByNombre("Cañete").orElseThrow(() -> new RuntimeException("Provincia Cañete no encontrada"))),

                // [Provincia de Huaral]
                new Distrito(null, "Huaral", "Distrito de Huaral", true, provinciaRepository.findByNombre("Huaral").orElseThrow(() -> new RuntimeException("Provincia Huaral no encontrada"))),
                new Distrito(null, "Atavillos Alto", "Distrito de Atavillos Alto", true, provinciaRepository.findByNombre("Huaral").orElseThrow(() -> new RuntimeException("Provincia Huaral no encontrada"))),
                new Distrito(null, "Atavillos Bajo", "Distrito de Atavillos Bajo", true, provinciaRepository.findByNombre("Huaral").orElseThrow(() -> new RuntimeException("Provincia Huaral no encontrada"))),
                new Distrito(null, "Aucallama", "Distrito de Aucallama", true, provinciaRepository.findByNombre("Huaral").orElseThrow(() -> new RuntimeException("Provincia Huaral no encontrada"))),
                new Distrito(null, "Chancay", "Distrito de Chancay", true, provinciaRepository.findByNombre("Huaral").orElseThrow(() -> new RuntimeException("Provincia Huaral no encontrada"))),
                new Distrito(null, "Ihuarí", "Distrito de Ihuarí", true, provinciaRepository.findByNombre("Huaral").orElseThrow(() -> new RuntimeException("Provincia Huaral no encontrada"))),
                new Distrito(null, "Lampián", "Distrito de Lampián", true, provinciaRepository.findByNombre("Huaral").orElseThrow(() -> new RuntimeException("Provincia Huaral no encontrada"))),
                new Distrito(null, "Pacaraos", "Distrito de Pacaraos", true, provinciaRepository.findByNombre("Huaral").orElseThrow(() -> new RuntimeException("Provincia Huaral no encontrada"))),
                new Distrito(null, "San Miguel de Acos", "Distrito de San Miguel de Acos", true, provinciaRepository.findByNombre("Huaral").orElseThrow(() -> new RuntimeException("Provincia Huaral no encontrada"))),
                new Distrito(null, "Santa Cruz de Andamarca", "Distrito de Santa Cruz de Andamarca", true, provinciaRepository.findByNombre("Huaral").orElseThrow(() -> new RuntimeException("Provincia Huaral no encontrada"))),
                new Distrito(null, "Sumbilca", "Distrito de Sumbilca", true, provinciaRepository.findByNombre("Huaral").orElseThrow(() -> new RuntimeException("Provincia Huaral no encontrada"))),
                new Distrito(null, "Veintisiete de Noviembre", "Distrito de Veintisiete de Noviembre", true, provinciaRepository.findByNombre("Huaral").orElseThrow(() -> new RuntimeException("Provincia Huaral no encontrada"))),

                // [Provincia de Huarochirí]
                new Distrito(null, "Matucana", "Distrito de Matucana", true, provinciaRepository.findByNombre("Huarochirí").orElseThrow(() -> new RuntimeException("Provincia Huarochirí no encontrada"))),
                new Distrito(null, "Antioquía", "Distrito de Antioquía", true, provinciaRepository.findByNombre("Huarochirí").orElseThrow(() -> new RuntimeException("Provincia Huarochirí no encontrada"))),
                new Distrito(null, "Callahuanca", "Distrito de Callahuanca", true, provinciaRepository.findByNombre("Huarochirí").orElseThrow(() -> new RuntimeException("Provincia Huarochirí no encontrada"))),
                new Distrito(null, "Carampoma", "Distrito de Carampoma", true, provinciaRepository.findByNombre("Huarochirí").orElseThrow(() -> new RuntimeException("Provincia Huarochirí no encontrada"))),
                new Distrito(null, "Chicla", "Distrito de Chicla", true, provinciaRepository.findByNombre("Huarochirí").orElseThrow(() -> new RuntimeException("Provincia Huarochirí no encontrada"))),
                new Distrito(null, "Cuenca", "Distrito de Cuenca", true, provinciaRepository.findByNombre("Huarochirí").orElseThrow(() -> new RuntimeException("Provincia Huarochirí no encontrada"))),
                new Distrito(null, "Huachupampa", "Distrito de Huachupampa", true, provinciaRepository.findByNombre("Huarochirí").orElseThrow(() -> new RuntimeException("Provincia Huarochirí no encontrada"))),
                new Distrito(null, "Huanza", "Distrito de Huanza", true, provinciaRepository.findByNombre("Huarochirí").orElseThrow(() -> new RuntimeException("Provincia Huarochirí no encontrada"))),
                new Distrito(null, "Huarochirí", "Distrito de Huarochirí", true, provinciaRepository.findByNombre("Huarochirí").orElseThrow(() -> new RuntimeException("Provincia Huarochirí no encontrada"))),
                new Distrito(null, "Lahuaytambo", "Distrito de Lahuaytambo", true, provinciaRepository.findByNombre("Huarochirí").orElseThrow(() -> new RuntimeException("Provincia Huarochirí no encontrada"))),
                new Distrito(null, "Langa", "Distrito de Langa", true, provinciaRepository.findByNombre("Huarochirí").orElseThrow(() -> new RuntimeException("Provincia Huarochirí no encontrada"))),
                new Distrito(null, "Laraos", "Distrito de Laraos", true, provinciaRepository.findByNombre("Huarochirí").orElseThrow(() -> new RuntimeException("Provincia Huarochirí no encontrada"))),
                new Distrito(null, "Mariatana", "Distrito de Mariatana", true, provinciaRepository.findByNombre("Huarochirí").orElseThrow(() -> new RuntimeException("Provincia Huarochirí no encontrada"))),
                new Distrito(null, "Ricardo Palma", "Distrito de Ricardo Palma", true, provinciaRepository.findByNombre("Huarochirí").orElseThrow(() -> new RuntimeException("Provincia Huarochirí no encontrada"))),
                new Distrito(null, "San Andrés de Tupicocha", "Distrito de San Andrés de Tupicocha", true, provinciaRepository.findByNombre("Huarochirí").orElseThrow(() -> new RuntimeException("Provincia Huarochirí no encontrada"))),
                new Distrito(null, "San Antonio", "Distrito de San Antonio", true, provinciaRepository.findByNombre("Huarochirí").orElseThrow(() -> new RuntimeException("Provincia Huarochirí no encontrada"))),
                new Distrito(null, "San Bartolomé", "Distrito de San Bartolomé", true, provinciaRepository.findByNombre("Huarochirí").orElseThrow(() -> new RuntimeException("Provincia Huarochirí no encontrada"))),
                new Distrito(null, "San Damian", "Distrito de San Damian", true, provinciaRepository.findByNombre("Huarochirí").orElseThrow(() -> new RuntimeException("Provincia Huarochirí no encontrada"))),
                new Distrito(null, "San Juan de Iris", "Distrito de San Juan de Iris", true, provinciaRepository.findByNombre("Huarochirí").orElseThrow(() -> new RuntimeException("Provincia Huarochirí no encontrada"))),
                new Distrito(null, "San Juan de Tantaranche", "Distrito de San Juan de Tantaranche", true, provinciaRepository.findByNombre("Huarochirí").orElseThrow(() -> new RuntimeException("Provincia Huarochirí no encontrada"))),
                new Distrito(null, "San Lorenzo de Quinti", "Distrito de San Lorenzo de Quinti", true, provinciaRepository.findByNombre("Huarochirí").orElseThrow(() -> new RuntimeException("Provincia Huarochirí no encontrada"))),
                new Distrito(null, "San Mateo", "Distrito de San Mateo", true, provinciaRepository.findByNombre("Huarochirí").orElseThrow(() -> new RuntimeException("Provincia Huarochirí no encontrada"))),
                new Distrito(null, "San Mateo de Otao", "Distrito de San Mateo de Otao", true, provinciaRepository.findByNombre("Huarochirí").orElseThrow(() -> new RuntimeException("Provincia Huarochirí no encontrada"))),
                new Distrito(null, "San Pedro de Casta", "Distrito de San Pedro de Casta", true, provinciaRepository.findByNombre("Huarochirí").orElseThrow(() -> new RuntimeException("Provincia Huarochirí no encontrada"))),
                new Distrito(null, "San Pedro de Huancayre", "Distrito de San Pedro de Huancayre", true, provinciaRepository.findByNombre("Huarochirí").orElseThrow(() -> new RuntimeException("Provincia Huarochirí no encontrada"))),
                new Distrito(null, "Sangallaya", "Distrito de Sangallaya", true, provinciaRepository.findByNombre("Huarochirí").orElseThrow(() -> new RuntimeException("Provincia Huarochirí no encontrada"))),
                new Distrito(null, "Santa Cruz de Cocachacra", "Distrito de Santa Cruz de Cocachacra", true, provinciaRepository.findByNombre("Huarochirí").orElseThrow(() -> new RuntimeException("Provincia Huarochirí no encontrada"))),
                new Distrito(null, "Santa Eulalia", "Distrito de Santa Eulalia", true, provinciaRepository.findByNombre("Huarochirí").orElseThrow(() -> new RuntimeException("Provincia Huarochirí no encontrada"))),
                new Distrito(null, "Santiago de Anchucaya", "Distrito de Santiago de Anchucaya", true, provinciaRepository.findByNombre("Huarochirí").orElseThrow(() -> new RuntimeException("Provincia Huarochirí no encontrada"))),
                new Distrito(null, "Santiago de Tuna", "Distrito de Santiago de Tuna", true, provinciaRepository.findByNombre("Huarochirí").orElseThrow(() -> new RuntimeException("Provincia Huarochirí no encontrada"))),
                new Distrito(null, "Santo Domingo de los Olleros", "Distrito de Santo Domingo de los Olleros", true, provinciaRepository.findByNombre("Huarochirí").orElseThrow(() -> new RuntimeException("Provincia Huarochirí no encontrada"))),
                new Distrito(null, "Surco", "Distrito de Surco", true, provinciaRepository.findByNombre("Huarochirí").orElseThrow(() -> new RuntimeException("Provincia Huarochirí no encontrada"))),

                // [Provincia de Huaura]
                new Distrito(null, "Huacho", "Distrito de Huacho", true, provinciaRepository.findByNombre("Huaura").orElseThrow(() -> new RuntimeException("Provincia Huaura no encontrada"))),
                new Distrito(null, "Ambar", "Distrito de Ambar", true, provinciaRepository.findByNombre("Huaura").orElseThrow(() -> new RuntimeException("Provincia Huaura no encontrada"))),
                new Distrito(null, "Caleta de Carquín", "Distrito de Caleta de Carquín", true, provinciaRepository.findByNombre("Huaura").orElseThrow(() -> new RuntimeException("Provincia Huaura no encontrada"))),
                new Distrito(null, "Checras", "Distrito de Checras", true, provinciaRepository.findByNombre("Huaura").orElseThrow(() -> new RuntimeException("Provincia Huaura no encontrada"))),
                new Distrito(null, "Hualmay", "Distrito de Hualmay", true, provinciaRepository.findByNombre("Huaura").orElseThrow(() -> new RuntimeException("Provincia Huaura no encontrada"))),
                new Distrito(null, "Huaura", "Distrito de Huaura", true, provinciaRepository.findByNombre("Huaura").orElseThrow(() -> new RuntimeException("Provincia Huaura no encontrada"))),
                new Distrito(null, "Leoncio Prado", "Distrito de Leoncio Prado", true, provinciaRepository.findByNombre("Huaura").orElseThrow(() -> new RuntimeException("Provincia Huaura no encontrada"))),
                new Distrito(null, "Paccho", "Distrito de Paccho", true, provinciaRepository.findByNombre("Huaura").orElseThrow(() -> new RuntimeException("Provincia Huaura no encontrada"))),
                new Distrito(null, "Santa Leonor", "Distrito de Santa Leonor", true, provinciaRepository.findByNombre("Huaura").orElseThrow(() -> new RuntimeException("Provincia Huaura no encontrada"))),
                new Distrito(null, "Santa María", "Distrito de Santa María", true, provinciaRepository.findByNombre("Huaura").orElseThrow(() -> new RuntimeException("Provincia Huaura no encontrada"))),
                new Distrito(null, "Sayan", "Distrito de Sayan", true, provinciaRepository.findByNombre("Huaura").orElseThrow(() -> new RuntimeException("Provincia Huaura no encontrada"))),
                new Distrito(null, "Vegueta", "Distrito de Vegueta", true, provinciaRepository.findByNombre("Huaura").orElseThrow(() -> new RuntimeException("Provincia Huaura no encontrada"))),

                // [Provincia de Oyón]
                new Distrito(null, "Oyón", "Distrito de Oyón", true, provinciaRepository.findByNombre("Oyón").orElseThrow(() -> new RuntimeException("Provincia Oyón no encontrada"))),
                new Distrito(null, "Andajes", "Distrito de Andajes", true, provinciaRepository.findByNombre("Oyón").orElseThrow(() -> new RuntimeException("Provincia Oyón no encontrada"))),
                new Distrito(null, "Caujul", "Distrito de Caujul", true, provinciaRepository.findByNombre("Oyón").orElseThrow(() -> new RuntimeException("Provincia Oyón no encontrada"))),
                new Distrito(null, "Cochamarca", "Distrito de Cochamarca", true, provinciaRepository.findByNombre("Oyón").orElseThrow(() -> new RuntimeException("Provincia Oyón no encontrada"))),
                new Distrito(null, "Naván", "Distrito de Naván", true, provinciaRepository.findByNombre("Oyón").orElseThrow(() -> new RuntimeException("Provincia Oyón no encontrada"))),
                new Distrito(null, "Pachangara", "Distrito de Pachangara", true, provinciaRepository.findByNombre("Oyón").orElseThrow(() -> new RuntimeException("Provincia Oyón no encontrada"))),

                // [Provincia de Yauyos]
                new Distrito(null, "Yauyos", "Distrito de Yauyos", true, provinciaRepository.findByNombre("Yauyos").orElseThrow(() -> new RuntimeException("Provincia Yauyos no encontrada"))),
                new Distrito(null, "Alis", "Distrito de Alis", true, provinciaRepository.findByNombre("Yauyos").orElseThrow(() -> new RuntimeException("Provincia Yauyos no encontrada"))),
                new Distrito(null, "Ayauca", "Distrito de Ayauca", true, provinciaRepository.findByNombre("Yauyos").orElseThrow(() -> new RuntimeException("Provincia Yauyos no encontrada"))),
                new Distrito(null, "Ayaviri", "Distrito de Ayaviri", true, provinciaRepository.findByNombre("Yauyos").orElseThrow(() -> new RuntimeException("Provincia Yauyos no encontrada"))),
                new Distrito(null, "Azángaro", "Distrito de Azángaro", true, provinciaRepository.findByNombre("Yauyos").orElseThrow(() -> new RuntimeException("Provincia Yauyos no encontrada"))),
                new Distrito(null, "Cacra", "Distrito de Cacra", true, provinciaRepository.findByNombre("Yauyos").orElseThrow(() -> new RuntimeException("Provincia Yauyos no encontrada"))),
                new Distrito(null, "Carania", "Distrito de Carania", true, provinciaRepository.findByNombre("Yauyos").orElseThrow(() -> new RuntimeException("Provincia Yauyos no encontrada"))),
                new Distrito(null, "Catahuasi", "Distrito de Catahuasi", true, provinciaRepository.findByNombre("Yauyos").orElseThrow(() -> new RuntimeException("Provincia Yauyos no encontrada"))),
                new Distrito(null, "Chocos", "Distrito de Chocos", true, provinciaRepository.findByNombre("Yauyos").orElseThrow(() -> new RuntimeException("Provincia Yauyos no encontrada"))),
                new Distrito(null, "Cochas", "Distrito de Cochas", true, provinciaRepository.findByNombre("Yauyos").orElseThrow(() -> new RuntimeException("Provincia Yauyos no encontrada"))),
                new Distrito(null, "Colonia", "Distrito de Colonia", true, provinciaRepository.findByNombre("Yauyos").orElseThrow(() -> new RuntimeException("Provincia Yauyos no encontrada"))),
                new Distrito(null, "Hongos", "Distrito de Hongos", true, provinciaRepository.findByNombre("Yauyos").orElseThrow(() -> new RuntimeException("Provincia Yauyos no encontrada"))),
                new Distrito(null, "Huampara", "Distrito de Huampara", true, provinciaRepository.findByNombre("Yauyos").orElseThrow(() -> new RuntimeException("Provincia Yauyos no encontrada"))),
                new Distrito(null, "Huancaya", "Distrito de Huancaya", true, provinciaRepository.findByNombre("Yauyos").orElseThrow(() -> new RuntimeException("Provincia Yauyos no encontrada"))),
                new Distrito(null, "Huangascar", "Distrito de Huangascar", true, provinciaRepository.findByNombre("Yauyos").orElseThrow(() -> new RuntimeException("Provincia Yauyos no encontrada"))),
                new Distrito(null, "Huantan", "Distrito de Huantan", true, provinciaRepository.findByNombre("Yauyos").orElseThrow(() -> new RuntimeException("Provincia Yauyos no encontrada"))),
                new Distrito(null, "Huañec", "Distrito de Huañec", true, provinciaRepository.findByNombre("Yauyos").orElseThrow(() -> new RuntimeException("Provincia Yauyos no encontrada"))),
                new Distrito(null, "Laraos", "Distrito de Laraos", true, provinciaRepository.findByNombre("Yauyos").orElseThrow(() -> new RuntimeException("Provincia Yauyos no encontrada"))),
                new Distrito(null, "Lincha", "Distrito de Lincha", true, provinciaRepository.findByNombre("Yauyos").orElseThrow(() -> new RuntimeException("Provincia Yauyos no encontrada"))),
                new Distrito(null, "Madean", "Distrito de Madean", true, provinciaRepository.findByNombre("Yauyos").orElseThrow(() -> new RuntimeException("Provincia Yauyos no encontrada"))),
                new Distrito(null, "Miraflores", "Distrito de Miraflores", true, provinciaRepository.findByNombre("Yauyos").orElseThrow(() -> new RuntimeException("Provincia Yauyos no encontrada"))),
                new Distrito(null, "Omas", "Distrito de Omas", true, provinciaRepository.findByNombre("Yauyos").orElseThrow(() -> new RuntimeException("Provincia Yauyos no encontrada"))),
                new Distrito(null, "Putinza", "Distrito de Putinza", true, provinciaRepository.findByNombre("Yauyos").orElseThrow(() -> new RuntimeException("Provincia Yauyos no encontrada"))),
                new Distrito(null, "Quinches", "Distrito de Quinches", true, provinciaRepository.findByNombre("Yauyos").orElseThrow(() -> new RuntimeException("Provincia Yauyos no encontrada"))),
                new Distrito(null, "Quinocay", "Distrito de Quinocay", true, provinciaRepository.findByNombre("Yauyos").orElseThrow(() -> new RuntimeException("Provincia Yauyos no encontrada"))),
                new Distrito(null, "San Joaquín", "Distrito de San Joaquín", true, provinciaRepository.findByNombre("Yauyos").orElseThrow(() -> new RuntimeException("Provincia Yauyos no encontrada"))),
                new Distrito(null, "San Pedro de Pilas", "Distrito de San Pedro de Pilas", true, provinciaRepository.findByNombre("Yauyos").orElseThrow(() -> new RuntimeException("Provincia Yauyos no encontrada"))),
                new Distrito(null, "Tanta", "Distrito de Tanta", true, provinciaRepository.findByNombre("Yauyos").orElseThrow(() -> new RuntimeException("Provincia Yauyos no encontrada"))),
                new Distrito(null, "Tauripampa", "Distrito de Tauripampa", true, provinciaRepository.findByNombre("Yauyos").orElseThrow(() -> new RuntimeException("Provincia Yauyos no encontrada"))),
                new Distrito(null, "Tomas", "Distrito de Tomas", true, provinciaRepository.findByNombre("Yauyos").orElseThrow(() -> new RuntimeException("Provincia Yauyos no encontrada"))),
                new Distrito(null, "Tupe", "Distrito de Tupe", true, provinciaRepository.findByNombre("Yauyos").orElseThrow(() -> new RuntimeException("Provincia Yauyos no encontrada"))),
                new Distrito(null, "Viñac", "Distrito de Viñac", true, provinciaRepository.findByNombre("Yauyos").orElseThrow(() -> new RuntimeException("Provincia Yauyos no encontrada"))),
                new Distrito(null, "Vitis", "Distrito de Vitis", true, provinciaRepository.findByNombre("Yauyos").orElseThrow(() -> new RuntimeException("Provincia Yauyos no encontrada")))
        );
        distritoRepository.saveAll(distritos);
    }

    private void seedDistritosLoreto() {
        List<Distrito> distritos = Arrays.asList(
                // Loreto
                // [Provincia de Maynas]
                new Distrito(null, "Iquitos", "Distrito de Iquitos", true, provinciaRepository.findByNombre("Maynas").orElseThrow(() -> new RuntimeException("Provincia Maynas no encontrada"))),
                new Distrito(null, "Alto Nanay", "Distrito de Alto Nanay", true, provinciaRepository.findByNombre("Maynas").orElseThrow(() -> new RuntimeException("Provincia Maynas no encontrada"))),
                new Distrito(null, "Fernando Lores", "Distrito de Fernando Lores", true, provinciaRepository.findByNombre("Maynas").orElseThrow(() -> new RuntimeException("Provincia Maynas no encontrada"))),
                new Distrito(null, "Indiana", "Distrito de Indiana", true, provinciaRepository.findByNombre("Maynas").orElseThrow(() -> new RuntimeException("Provincia Maynas no encontrada"))),
                new Distrito(null, "Las Amazonas", "Distrito de Las Amazonas", true, provinciaRepository.findByNombre("Maynas").orElseThrow(() -> new RuntimeException("Provincia Maynas no encontrada"))),
                new Distrito(null, "Mazán", "Distrito de Mazán", true, provinciaRepository.findByNombre("Maynas").orElseThrow(() -> new RuntimeException("Provincia Maynas no encontrada"))),
                new Distrito(null, "Napo", "Distrito de Napo", true, provinciaRepository.findByNombre("Maynas").orElseThrow(() -> new RuntimeException("Provincia Maynas no encontrada"))),
                new Distrito(null, "Punchana", "Distrito de Punchana", true, provinciaRepository.findByNombre("Maynas").orElseThrow(() -> new RuntimeException("Provincia Maynas no encontrada"))),
                new Distrito(null, "Torres Causana", "Distrito de Torres Causana", true, provinciaRepository.findByNombre("Maynas").orElseThrow(() -> new RuntimeException("Provincia Maynas no encontrada"))),
                new Distrito(null, "Belén", "Distrito de Belén", true, provinciaRepository.findByNombre("Maynas").orElseThrow(() -> new RuntimeException("Provincia Maynas no encontrada"))),
                new Distrito(null, "San Juan Bautista", "Distrito de San Juan Bautista", true, provinciaRepository.findByNombre("Maynas").orElseThrow(() -> new RuntimeException("Provincia Maynas no encontrada"))),

                // [Provincia de Alto Amazonas]
                new Distrito(null, "Yurimaguas", "Distrito de Yurimaguas", true, provinciaRepository.findByNombre("Alto Amazonas").orElseThrow(() -> new RuntimeException("Provincia Alto Amazonas no encontrada"))),
                new Distrito(null, "Balsapuerto", "Distrito de Balsapuerto", true, provinciaRepository.findByNombre("Alto Amazonas").orElseThrow(() -> new RuntimeException("Provincia Alto Amazonas no encontrada"))),
                new Distrito(null, "Jeberos", "Distrito de Jeberos", true, provinciaRepository.findByNombre("Alto Amazonas").orElseThrow(() -> new RuntimeException("Provincia Alto Amazonas no encontrada"))),
                new Distrito(null, "Lagunas", "Distrito de Lagunas", true, provinciaRepository.findByNombre("Alto Amazonas").orElseThrow(() -> new RuntimeException("Provincia Alto Amazonas no encontrada"))),
                new Distrito(null, "Santa Cruz", "Distrito de Santa Cruz", true, provinciaRepository.findByNombre("Alto Amazonas").orElseThrow(() -> new RuntimeException("Provincia Alto Amazonas no encontrada"))),
                new Distrito(null, "Teniente César López Rojas", "Distrito de Teniente César López Rojas", true, provinciaRepository.findByNombre("Alto Amazonas").orElseThrow(() -> new RuntimeException("Provincia Alto Amazonas no encontrada"))),

                // [Provincia de Loreto]
                new Distrito(null, "Nauta", "Distrito de Nauta", true, provinciaRepository.findByNombre("Loreto").orElseThrow(() -> new RuntimeException("Provincia Loreto no encontrada"))),
                new Distrito(null, "Parinari", "Distrito de Parinari", true, provinciaRepository.findByNombre("Loreto").orElseThrow(() -> new RuntimeException("Provincia Loreto no encontrada"))),
                new Distrito(null, "Tigre", "Distrito de Tigre", true, provinciaRepository.findByNombre("Loreto").orElseThrow(() -> new RuntimeException("Provincia Loreto no encontrada"))),
                new Distrito(null, "Trompeteros", "Distrito de Trompeteros", true, provinciaRepository.findByNombre("Loreto").orElseThrow(() -> new RuntimeException("Provincia Loreto no encontrada"))),
                new Distrito(null, "Urarinas", "Distrito de Urarinas", true, provinciaRepository.findByNombre("Loreto").orElseThrow(() -> new RuntimeException("Provincia Loreto no encontrada"))),

                // [Provincia de Mariscal Ramón Castilla]
                new Distrito(null, "Ramón Castilla", "Distrito de Ramón Castilla", true, provinciaRepository.findByNombre("Mariscal Ramón Castilla").orElseThrow(() -> new RuntimeException("Provincia Mariscal Ramón Castilla no encontrada"))),
                new Distrito(null, "Pebas", "Distrito de Pebas", true, provinciaRepository.findByNombre("Mariscal Ramón Castilla").orElseThrow(() -> new RuntimeException("Provincia Mariscal Ramón Castilla no encontrada"))),
                new Distrito(null, "Yavarí", "Distrito de Yavarí", true, provinciaRepository.findByNombre("Mariscal Ramón Castilla").orElseThrow(() -> new RuntimeException("Provincia Mariscal Ramón Castilla no encontrada"))),
                new Distrito(null, "San Pablo", "Distrito de San Pablo", true, provinciaRepository.findByNombre("Mariscal Ramón Castilla").orElseThrow(() -> new RuntimeException("Provincia Mariscal Ramón Castilla no encontrada"))),

                // [Provincia de Requena]
                new Distrito(null, "Requena", "Distrito de Requena", true, provinciaRepository.findByNombre("Requena").orElseThrow(() -> new RuntimeException("Provincia Requena no encontrada"))),
                new Distrito(null, "Alto Tapiche", "Distrito de Alto Tapiche", true, provinciaRepository.findByNombre("Requena").orElseThrow(() -> new RuntimeException("Provincia Requena no encontrada"))),
                new Distrito(null, "Capelo", "Distrito de Capelo", true, provinciaRepository.findByNombre("Requena").orElseThrow(() -> new RuntimeException("Provincia Requena no encontrada"))),
                new Distrito(null, "Emilio San Martín", "Distrito de Emilio San Martín", true, provinciaRepository.findByNombre("Requena").orElseThrow(() -> new RuntimeException("Provincia Requena no encontrada"))),
                new Distrito(null, "Maquía", "Distrito de Maquía", true, provinciaRepository.findByNombre("Requena").orElseThrow(() -> new RuntimeException("Provincia Requena no encontrada"))),
                new Distrito(null, "Puinahua", "Distrito de Puinahua", true, provinciaRepository.findByNombre("Requena").orElseThrow(() -> new RuntimeException("Provincia Requena no encontrada"))),
                new Distrito(null, "Saquena", "Distrito de Saquena", true, provinciaRepository.findByNombre("Requena").orElseThrow(() -> new RuntimeException("Provincia Requena no encontrada"))),
                new Distrito(null, "Soplin", "Distrito de Soplin", true, provinciaRepository.findByNombre("Requena").orElseThrow(() -> new RuntimeException("Provincia Requena no encontrada"))),
                new Distrito(null, "Tapiche", "Distrito de Tapiche", true, provinciaRepository.findByNombre("Requena").orElseThrow(() -> new RuntimeException("Provincia Requena no encontrada"))),
                new Distrito(null, "Jenaro Herrera", "Distrito de Jenaro Herrera", true, provinciaRepository.findByNombre("Requena").orElseThrow(() -> new RuntimeException("Provincia Requena no encontrada"))),
                new Distrito(null, "Yaquerana", "Distrito de Yaquerana", true, provinciaRepository.findByNombre("Requena").orElseThrow(() -> new RuntimeException("Provincia Requena no encontrada"))),

                // [Provincia de Ucayali]
                new Distrito(null, "Contamana", "Distrito de Contamana", true, provinciaRepository.findByNombre("Ucayali").orElseThrow(() -> new RuntimeException("Provincia Ucayali no encontrada"))),
                new Distrito(null, "Inahuaya", "Distrito de Inahuaya", true, provinciaRepository.findByNombre("Ucayali").orElseThrow(() -> new RuntimeException("Provincia Ucayali no encontrada"))),
                new Distrito(null, "Padre Márquez", "Distrito de Padre Márquez", true, provinciaRepository.findByNombre("Ucayali").orElseThrow(() -> new RuntimeException("Provincia Ucayali no encontrada"))),
                new Distrito(null, "Pampa Hermosa", "Distrito de Pampa Hermosa", true, provinciaRepository.findByNombre("Ucayali").orElseThrow(() -> new RuntimeException("Provincia Ucayali no encontrada"))),
                new Distrito(null, "Sarayacu", "Distrito de Sarayacu", true, provinciaRepository.findByNombre("Ucayali").orElseThrow(() -> new RuntimeException("Provincia Ucayali no encontrada"))),
                new Distrito(null, "Vargas Guerra", "Distrito de Vargas Guerra", true, provinciaRepository.findByNombre("Ucayali").orElseThrow(() -> new RuntimeException("Provincia Ucayali no encontrada"))),

                // [Provincia de Datem del Marañón]
                new Distrito(null, "Barranca", "Distrito de Barranca", true, provinciaRepository.findByNombre("Datem del Marañón").orElseThrow(() -> new RuntimeException("Provincia Datem del Marañón no encontrada"))),
                new Distrito(null, "Cahuapanas", "Distrito de Cahuapanas", true, provinciaRepository.findByNombre("Datem del Marañón").orElseThrow(() -> new RuntimeException("Provincia Datem del Marañón no encontrada"))),
                new Distrito(null, "Manseriche", "Distrito de Manseriche", true, provinciaRepository.findByNombre("Datem del Marañón").orElseThrow(() -> new RuntimeException("Provincia Datem del Marañón no encontrada"))),
                new Distrito(null, "Morona", "Distrito de Morona", true, provinciaRepository.findByNombre("Datem del Marañón").orElseThrow(() -> new RuntimeException("Provincia Datem del Marañón no encontrada"))),
                new Distrito(null, "Pastaza", "Distrito de Pastaza", true, provinciaRepository.findByNombre("Datem del Marañón").orElseThrow(() -> new RuntimeException("Provincia Datem del Marañón no encontrada"))),
                new Distrito(null, "Andoas", "Distrito de Andoas", true, provinciaRepository.findByNombre("Datem del Marañón").orElseThrow(() -> new RuntimeException("Provincia Datem del Marañón no encontrada"))),

                // [Provincia de Putumayo]
                new Distrito(null, "Putumayo", "Distrito de Putumayo", true, provinciaRepository.findByNombre("Putumayo").orElseThrow(() -> new RuntimeException("Provincia Putumayo no encontrada"))),
                new Distrito(null, "Rosa Panduro", "Distrito de Rosa Panduro", true, provinciaRepository.findByNombre("Putumayo").orElseThrow(() -> new RuntimeException("Provincia Putumayo no encontrada"))),
                new Distrito(null, "Teniente Ricardo Palma", "Distrito de Teniente Ricardo Palma", true, provinciaRepository.findByNombre("Putumayo").orElseThrow(() -> new RuntimeException("Provincia Putumayo no encontrada"))),
                new Distrito(null, "Yaguas", "Distrito de Yaguas", true, provinciaRepository.findByNombre("Putumayo").orElseThrow(() -> new RuntimeException("Provincia Putumayo no encontrada")))
        );
        distritoRepository.saveAll(distritos);
    }

    private void seedDistritosMadreDeDios() {
        List<Distrito> distritos = Arrays.asList(
                // Madre de Dios
                // [Provincia de Tambopata]
                new Distrito(null, "Tambopata", "Distrito de Tambopata", true, provinciaRepository.findByNombre("Tambopata").orElseThrow(() -> new RuntimeException("Provincia Tambopata no encontrada"))),
                new Distrito(null, "Inambari", "Distrito de Inambari", true, provinciaRepository.findByNombre("Tambopata").orElseThrow(() -> new RuntimeException("Provincia Tambopata no encontrada"))),
                new Distrito(null, "Las Piedras", "Distrito de Las Piedras", true, provinciaRepository.findByNombre("Tambopata").orElseThrow(() -> new RuntimeException("Provincia Tambopata no encontrada"))),
                new Distrito(null, "Laberinto", "Distrito de Laberinto", true, provinciaRepository.findByNombre("Tambopata").orElseThrow(() -> new RuntimeException("Provincia Tambopata no encontrada"))),

                // [Provincia de Manu]
                new Distrito(null, "Manu", "Distrito de Manu", true, provinciaRepository.findByNombre("Manu").orElseThrow(() -> new RuntimeException("Provincia Manu no encontrada"))),
                new Distrito(null, "Fitzcarrald", "Distrito de Fitzcarrald", true, provinciaRepository.findByNombre("Manu").orElseThrow(() -> new RuntimeException("Provincia Manu no encontrada"))),
                new Distrito(null, "Madre de Dios", "Distrito de Madre de Dios", true, provinciaRepository.findByNombre("Manu").orElseThrow(() -> new RuntimeException("Provincia Manu no encontrada"))),
                new Distrito(null, "Huepetuhe", "Distrito de Huepetuhe", true, provinciaRepository.findByNombre("Manu").orElseThrow(() -> new RuntimeException("Provincia Manu no encontrada"))),

                // [Provincia de Tahuamanu]
                new Distrito(null, "Iñapari", "Distrito de Iñapari", true, provinciaRepository.findByNombre("Tahuamanu").orElseThrow(() -> new RuntimeException("Provincia Tahuamanu no encontrada"))),
                new Distrito(null, "Iberia", "Distrito de Iberia", true, provinciaRepository.findByNombre("Tahuamanu").orElseThrow(() -> new RuntimeException("Provincia Tahuamanu no encontrada"))),
                new Distrito(null, "Tahuamanu", "Distrito de Tahuamanu", true, provinciaRepository.findByNombre("Tahuamanu").orElseThrow(() -> new RuntimeException("Provincia Tahuamanu no encontrada")))
        );
        distritoRepository.saveAll(distritos);
    }

    private void seedDistritosMoquegua() {
        List<Distrito> distritos = Arrays.asList(
                // Moquegua
                // [Provincia de Mariscal Nieto]
                new Distrito(null, "Moquegua", "Distrito de Moquegua", true, provinciaRepository.findByNombre("Mariscal Nieto").orElseThrow(() -> new RuntimeException("Provincia Mariscal Nieto no encontrada"))),
                new Distrito(null, "Carumas", "Distrito de Carumas", true, provinciaRepository.findByNombre("Mariscal Nieto").orElseThrow(() -> new RuntimeException("Provincia Mariscal Nieto no encontrada"))),
                new Distrito(null, "Cuchumbaya", "Distrito de Cuchumbaya", true, provinciaRepository.findByNombre("Mariscal Nieto").orElseThrow(() -> new RuntimeException("Provincia Mariscal Nieto no encontrada"))),
                new Distrito(null, "Samegua", "Distrito de Samegua", true, provinciaRepository.findByNombre("Mariscal Nieto").orElseThrow(() -> new RuntimeException("Provincia Mariscal Nieto no encontrada"))),
                new Distrito(null, "San Cristóbal", "Distrito de San Cristóbal", true, provinciaRepository.findByNombre("Mariscal Nieto").orElseThrow(() -> new RuntimeException("Provincia Mariscal Nieto no encontrada"))),
                new Distrito(null, "Torata", "Distrito de Torata", true, provinciaRepository.findByNombre("Mariscal Nieto").orElseThrow(() -> new RuntimeException("Provincia Mariscal Nieto no encontrada"))),

                // [Provincia de General Sánchez Cerro]
                new Distrito(null, "Omate", "Distrito de Omate", true, provinciaRepository.findByNombre("General Sánchez Cerro").orElseThrow(() -> new RuntimeException("Provincia General Sánchez Cerro no encontrada"))),
                new Distrito(null, "Chojata", "Distrito de Chojata", true, provinciaRepository.findByNombre("General Sánchez Cerro").orElseThrow(() -> new RuntimeException("Provincia General Sánchez Cerro no encontrada"))),
                new Distrito(null, "Coalaque", "Distrito de Coalaque", true, provinciaRepository.findByNombre("General Sánchez Cerro").orElseThrow(() -> new RuntimeException("Provincia General Sánchez Cerro no encontrada"))),
                new Distrito(null, "Ichuña", "Distrito de Ichuña", true, provinciaRepository.findByNombre("General Sánchez Cerro").orElseThrow(() -> new RuntimeException("Provincia General Sánchez Cerro no encontrada"))),
                new Distrito(null, "La Capilla", "Distrito de La Capilla", true, provinciaRepository.findByNombre("General Sánchez Cerro").orElseThrow(() -> new RuntimeException("Provincia General Sánchez Cerro no encontrada"))),
                new Distrito(null, "Lloque", "Distrito de Lloque", true, provinciaRepository.findByNombre("General Sánchez Cerro").orElseThrow(() -> new RuntimeException("Provincia General Sánchez Cerro no encontrada"))),
                new Distrito(null, "Matalaque", "Distrito de Matalaque", true, provinciaRepository.findByNombre("General Sánchez Cerro").orElseThrow(() -> new RuntimeException("Provincia General Sánchez Cerro no encontrada"))),
                new Distrito(null, "Puquina", "Distrito de Puquina", true, provinciaRepository.findByNombre("General Sánchez Cerro").orElseThrow(() -> new RuntimeException("Provincia General Sánchez Cerro no encontrada"))),
                new Distrito(null, "Quinistaquillas", "Distrito de Quinistaquillas", true, provinciaRepository.findByNombre("General Sánchez Cerro").orElseThrow(() -> new RuntimeException("Provincia General Sánchez Cerro no encontrada"))),
                new Distrito(null, "Ubinas", "Distrito de Ubinas", true, provinciaRepository.findByNombre("General Sánchez Cerro").orElseThrow(() -> new RuntimeException("Provincia General Sánchez Cerro no encontrada"))),
                new Distrito(null, "Yunga", "Distrito de Yunga", true, provinciaRepository.findByNombre("General Sánchez Cerro").orElseThrow(() -> new RuntimeException("Provincia General Sánchez Cerro no encontrada"))),

                // [Provincia de Ilo]
                new Distrito(null, "Ilo", "Distrito de Ilo", true, provinciaRepository.findByNombre("Ilo").orElseThrow(() -> new RuntimeException("Provincia Ilo no encontrada"))),
                new Distrito(null, "El Algarrobal", "Distrito de El Algarrobal", true, provinciaRepository.findByNombre("Ilo").orElseThrow(() -> new RuntimeException("Provincia Ilo no encontrada"))),
                new Distrito(null, "Pacocha", "Distrito de Pacocha", true, provinciaRepository.findByNombre("Ilo").orElseThrow(() -> new RuntimeException("Provincia Ilo no encontrada")))
        );
        distritoRepository.saveAll(distritos);
    }

    private void seedDistritosPasco() {
        List<Distrito> distritos = Arrays.asList(
                // Pasco
                // [Provincia de Pasco]
                new Distrito(null, "Chaupimarca", "Distrito de Chaupimarca", true, provinciaRepository.findByNombre("Pasco").orElseThrow(() -> new RuntimeException("Provincia Pasco no encontrada"))),
                new Distrito(null, "Huachon", "Distrito de Huachon", true, provinciaRepository.findByNombre("Pasco").orElseThrow(() -> new RuntimeException("Provincia Pasco no encontrada"))),
                new Distrito(null, "Huariaca", "Distrito de Huariaca", true, provinciaRepository.findByNombre("Pasco").orElseThrow(() -> new RuntimeException("Provincia Pasco no encontrada"))),
                new Distrito(null, "Huayllay", "Distrito de Huayllay", true, provinciaRepository.findByNombre("Pasco").orElseThrow(() -> new RuntimeException("Provincia Pasco no encontrada"))),
                new Distrito(null, "Ninacaca", "Distrito de Ninacaca", true, provinciaRepository.findByNombre("Pasco").orElseThrow(() -> new RuntimeException("Provincia Pasco no encontrada"))),
                new Distrito(null, "Pallanchacra", "Distrito de Pallanchacra", true, provinciaRepository.findByNombre("Pasco").orElseThrow(() -> new RuntimeException("Provincia Pasco no encontrada"))),
                new Distrito(null, "Paucartambo", "Distrito de Paucartambo", true, provinciaRepository.findByNombre("Pasco").orElseThrow(() -> new RuntimeException("Provincia Pasco no encontrada"))),
                new Distrito(null, "San Francisco de Asís de Yarusyacán", "Distrito de San Francisco de Asís de Yarusyacán", true, provinciaRepository.findByNombre("Pasco").orElseThrow(() -> new RuntimeException("Provincia Pasco no encontrada"))),
                new Distrito(null, "Simón Bolívar", "Distrito de Simón Bolívar", true, provinciaRepository.findByNombre("Pasco").orElseThrow(() -> new RuntimeException("Provincia Pasco no encontrada"))),
                new Distrito(null, "Ticlacayan", "Distrito de Ticlacayan", true, provinciaRepository.findByNombre("Pasco").orElseThrow(() -> new RuntimeException("Provincia Pasco no encontrada"))),
                new Distrito(null, "Tinyahuarco", "Distrito de Tinyahuarco", true, provinciaRepository.findByNombre("Pasco").orElseThrow(() -> new RuntimeException("Provincia Pasco no encontrada"))),
                new Distrito(null, "Vicco", "Distrito de Vicco", true, provinciaRepository.findByNombre("Pasco").orElseThrow(() -> new RuntimeException("Provincia Pasco no encontrada"))),
                new Distrito(null, "Yanacancha", "Distrito de Yanacancha", true, provinciaRepository.findByNombre("Pasco").orElseThrow(() -> new RuntimeException("Provincia Pasco no encontrada"))),

                // [Provincia de Daniel Alcides Carrión]
                new Distrito(null, "Yanahuanca", "Distrito de Yanahuanca", true, provinciaRepository.findByNombre("Daniel Alcides Carrión").orElseThrow(() -> new RuntimeException("Provincia Daniel Alcides Carrión no encontrada"))),
                new Distrito(null, "Chacayan", "Distrito de Chacayan", true, provinciaRepository.findByNombre("Daniel Alcides Carrión").orElseThrow(() -> new RuntimeException("Provincia Daniel Alcides Carrión no encontrada"))),
                new Distrito(null, "Goyllarisquizga", "Distrito de Goyllarisquizga", true, provinciaRepository.findByNombre("Daniel Alcides Carrión").orElseThrow(() -> new RuntimeException("Provincia Daniel Alcides Carrión no encontrada"))),
                new Distrito(null, "Paucar", "Distrito de Paucar", true, provinciaRepository.findByNombre("Daniel Alcides Carrión").orElseThrow(() -> new RuntimeException("Provincia Daniel Alcides Carrión no encontrada"))),
                new Distrito(null, "San Pedro de Pillao", "Distrito de San Pedro de Pillao", true, provinciaRepository.findByNombre("Daniel Alcides Carrión").orElseThrow(() -> new RuntimeException("Provincia Daniel Alcides Carrión no encontrada"))),
                new Distrito(null, "Santa Ana de Tusi", "Distrito de Santa Ana de Tusi", true, provinciaRepository.findByNombre("Daniel Alcides Carrión").orElseThrow(() -> new RuntimeException("Provincia Daniel Alcides Carrión no encontrada"))),
                new Distrito(null, "Tapuc", "Distrito de Tapuc", true, provinciaRepository.findByNombre("Daniel Alcides Carrión").orElseThrow(() -> new RuntimeException("Provincia Daniel Alcides Carrión no encontrada"))),
                new Distrito(null, "Vilcabamba", "Distrito de Vilcabamba", true, provinciaRepository.findByNombre("Daniel Alcides Carrión").orElseThrow(() -> new RuntimeException("Provincia Daniel Alcides Carrión no encontrada"))),

                // [Provincia de Oxapampa]
                new Distrito(null, "Oxapampa", "Distrito de Oxapampa", true, provinciaRepository.findByNombre("Oxapampa").orElseThrow(() -> new RuntimeException("Provincia Oxapampa no encontrada"))),
                new Distrito(null, "Chontabamba", "Distrito de Chontabamba", true, provinciaRepository.findByNombre("Oxapampa").orElseThrow(() -> new RuntimeException("Provincia Oxapampa no encontrada"))),
                new Distrito(null, "Huancabamba", "Distrito de Huancabamba", true, provinciaRepository.findByNombre("Oxapampa").orElseThrow(() -> new RuntimeException("Provincia Oxapampa no encontrada"))),
                new Distrito(null, "Palcazu", "Distrito de Palcazu", true, provinciaRepository.findByNombre("Oxapampa").orElseThrow(() -> new RuntimeException("Provincia Oxapampa no encontrada"))),
                new Distrito(null, "Pozuzo", "Distrito de Pozuzo", true, provinciaRepository.findByNombre("Oxapampa").orElseThrow(() -> new RuntimeException("Provincia Oxapampa no encontrada"))),
                new Distrito(null, "Puerto Bermúdez", "Distrito de Puerto Bermúdez", true, provinciaRepository.findByNombre("Oxapampa").orElseThrow(() -> new RuntimeException("Provincia Oxapampa no encontrada"))),
                new Distrito(null, "Villa Rica", "Distrito de Villa Rica", true, provinciaRepository.findByNombre("Oxapampa").orElseThrow(() -> new RuntimeException("Provincia Oxapampa no encontrada"))),
                new Distrito(null, "Constitución", "Distrito de Constitución", true, provinciaRepository.findByNombre("Oxapampa").orElseThrow(() -> new RuntimeException("Provincia Oxapampa no encontrada")))
        );
        distritoRepository.saveAll(distritos);
    }

    private void seedDistritosPiura() {
        List<Distrito> distritos = Arrays.asList(
                // Piura
                // [Provincia de Piura]
                new Distrito(null, "Piura", "Distrito de Piura", true, provinciaRepository.findByNombre("Piura").orElseThrow(() -> new RuntimeException("Provincia Piura no encontrada"))),
                new Distrito(null, "Castilla", "Distrito de Castilla", true, provinciaRepository.findByNombre("Piura").orElseThrow(() -> new RuntimeException("Provincia Piura no encontrada"))),
                new Distrito(null, "Catacaos", "Distrito de Catacaos", true, provinciaRepository.findByNombre("Piura").orElseThrow(() -> new RuntimeException("Provincia Piura no encontrada"))),
                new Distrito(null, "Cura Mori", "Distrito de Cura Mori", true, provinciaRepository.findByNombre("Piura").orElseThrow(() -> new RuntimeException("Provincia Piura no encontrada"))),
                new Distrito(null, "El Tallán", "Distrito de El Tallán", true, provinciaRepository.findByNombre("Piura").orElseThrow(() -> new RuntimeException("Provincia Piura no encontrada"))),
                new Distrito(null, "La Arena", "Distrito de La Arena", true, provinciaRepository.findByNombre("Piura").orElseThrow(() -> new RuntimeException("Provincia Piura no encontrada"))),
                new Distrito(null, "La Unión", "Distrito de La Unión", true, provinciaRepository.findByNombre("Piura").orElseThrow(() -> new RuntimeException("Provincia Piura no encontrada"))),
                new Distrito(null, "Las Lomas", "Distrito de Las Lomas", true, provinciaRepository.findByNombre("Piura").orElseThrow(() -> new RuntimeException("Provincia Piura no encontrada"))),
                new Distrito(null, "Tambogrande", "Distrito de Tambogrande", true, provinciaRepository.findByNombre("Piura").orElseThrow(() -> new RuntimeException("Provincia Piura no encontrada"))),
                new Distrito(null, "Veintiseis de Octubre", "Distrito de Veintiseis de Octubre", true, provinciaRepository.findByNombre("Piura").orElseThrow(() -> new RuntimeException("Provincia Piura no encontrada"))),

                // [Provincia de Ayabaca]
                new Distrito(null, "Ayabaca", "Distrito de Ayabaca", true, provinciaRepository.findByNombre("Ayabaca").orElseThrow(() -> new RuntimeException("Provincia Ayabaca no encontrada"))),
                new Distrito(null, "Frías", "Distrito de Frías", true, provinciaRepository.findByNombre("Ayabaca").orElseThrow(() -> new RuntimeException("Provincia Ayabaca no encontrada"))),
                new Distrito(null, "Jilili", "Distrito de Jilili", true, provinciaRepository.findByNombre("Ayabaca").orElseThrow(() -> new RuntimeException("Provincia Ayabaca no encontrada"))),
                new Distrito(null, "Lagunas", "Distrito de Lagunas", true, provinciaRepository.findByNombre("Ayabaca").orElseThrow(() -> new RuntimeException("Provincia Ayabaca no encontrada"))),
                new Distrito(null, "Montero", "Distrito de Montero", true, provinciaRepository.findByNombre("Ayabaca").orElseThrow(() -> new RuntimeException("Provincia Ayabaca no encontrada"))),
                new Distrito(null, "Pacaipampa", "Distrito de Pacaipampa", true, provinciaRepository.findByNombre("Ayabaca").orElseThrow(() -> new RuntimeException("Provincia Ayabaca no encontrada"))),
                new Distrito(null, "Paimas", "Distrito de Paimas", true, provinciaRepository.findByNombre("Ayabaca").orElseThrow(() -> new RuntimeException("Provincia Ayabaca no encontrada"))),
                new Distrito(null, "Sapillica", "Distrito de Sapillica", true, provinciaRepository.findByNombre("Ayabaca").orElseThrow(() -> new RuntimeException("Provincia Ayabaca no encontrada"))),
                new Distrito(null, "Sicchez", "Distrito de Sicchez", true, provinciaRepository.findByNombre("Ayabaca").orElseThrow(() -> new RuntimeException("Provincia Ayabaca no encontrada"))),
                new Distrito(null, "Suyo", "Distrito de Suyo", true, provinciaRepository.findByNombre("Ayabaca").orElseThrow(() -> new RuntimeException("Provincia Ayabaca no encontrada"))),

                // [Provincia de Huancabamba]
                new Distrito(null, "Huancabamba", "Distrito de Huancabamba", true, provinciaRepository.findByNombre("Huancabamba").orElseThrow(() -> new RuntimeException("Provincia Huancabamba no encontrada"))),
                new Distrito(null, "Canchaque", "Distrito de Canchaque", true, provinciaRepository.findByNombre("Huancabamba").orElseThrow(() -> new RuntimeException("Provincia Huancabamba no encontrada"))),
                new Distrito(null, "El Carmen de la Frontera", "Distrito de El Carmen de la Frontera", true, provinciaRepository.findByNombre("Huancabamba").orElseThrow(() -> new RuntimeException("Provincia Huancabamba no encontrada"))),
                new Distrito(null, "Huarmaca", "Distrito de Huarmaca", true, provinciaRepository.findByNombre("Huancabamba").orElseThrow(() -> new RuntimeException("Provincia Huancabamba no encontrada"))),
                new Distrito(null, "Lalaquiz", "Distrito de Lalaquiz", true, provinciaRepository.findByNombre("Huancabamba").orElseThrow(() -> new RuntimeException("Provincia Huancabamba no encontrada"))),
                new Distrito(null, "San Miguel de El Faique", "Distrito de San Miguel de El Faique", true, provinciaRepository.findByNombre("Huancabamba").orElseThrow(() -> new RuntimeException("Provincia Huancabamba no encontrada"))),
                new Distrito(null, "Sondor", "Distrito de Sondor", true, provinciaRepository.findByNombre("Huancabamba").orElseThrow(() -> new RuntimeException("Provincia Huancabamba no encontrada"))),
                new Distrito(null, "Sondorillo", "Distrito de Sondorillo", true, provinciaRepository.findByNombre("Huancabamba").orElseThrow(() -> new RuntimeException("Provincia Huancabamba no encontrada"))),

                // [Provincia de Morropón]
                new Distrito(null, "Chulucanas", "Distrito de Chulucanas", true, provinciaRepository.findByNombre("Morropón").orElseThrow(() -> new RuntimeException("Provincia Morropón no encontrada"))),
                new Distrito(null, "Buenos Aires", "Distrito de Buenos Aires", true, provinciaRepository.findByNombre("Morropón").orElseThrow(() -> new RuntimeException("Provincia Morropón no encontrada"))),
                new Distrito(null, "Chalaco", "Distrito de Chalaco", true, provinciaRepository.findByNombre("Morropón").orElseThrow(() -> new RuntimeException("Provincia Morropón no encontrada"))),
                new Distrito(null, "La Matanza", "Distrito de La Matanza", true, provinciaRepository.findByNombre("Morropón").orElseThrow(() -> new RuntimeException("Provincia Morropón no encontrada"))),
                new Distrito(null, "Morropón", "Distrito de Morropón", true, provinciaRepository.findByNombre("Morropón").orElseThrow(() -> new RuntimeException("Provincia Morropón no encontrada"))),
                new Distrito(null, "Salitral", "Distrito de Salitral", true, provinciaRepository.findByNombre("Morropón").orElseThrow(() -> new RuntimeException("Provincia Morropón no encontrada"))),
                new Distrito(null, "San Juan de Bigote", "Distrito de San Juan de Bigote", true, provinciaRepository.findByNombre("Morropón").orElseThrow(() -> new RuntimeException("Provincia Morropón no encontrada"))),
                new Distrito(null, "Santa Catalina de Mossa", "Distrito de Santa Catalina de Mossa", true, provinciaRepository.findByNombre("Morropón").orElseThrow(() -> new RuntimeException("Provincia Morropón no encontrada"))),
                new Distrito(null, "Santo Domingo", "Distrito de Santo Domingo", true, provinciaRepository.findByNombre("Morropón").orElseThrow(() -> new RuntimeException("Provincia Morropón no encontrada"))),
                new Distrito(null, "Yamango", "Distrito de Yamango", true, provinciaRepository.findByNombre("Morropón").orElseThrow(() -> new RuntimeException("Provincia Morropón no encontrada"))),

                // [Provincia de Paita]
                new Distrito(null, "Paita", "Distrito de Paita", true, provinciaRepository.findByNombre("Paita").orElseThrow(() -> new RuntimeException("Provincia Paita no encontrada"))),
                new Distrito(null, "Amotape", "Distrito de Amotape", true, provinciaRepository.findByNombre("Paita").orElseThrow(() -> new RuntimeException("Provincia Paita no encontrada"))),
                new Distrito(null, "Arenal", "Distrito de Arenal", true, provinciaRepository.findByNombre("Paita").orElseThrow(() -> new RuntimeException("Provincia Paita no encontrada"))),
                new Distrito(null, "Colán", "Distrito de Colán", true, provinciaRepository.findByNombre("Paita").orElseThrow(() -> new RuntimeException("Provincia Paita no encontrada"))),
                new Distrito(null, "La Huaca", "Distrito de La Huaca", true, provinciaRepository.findByNombre("Paita").orElseThrow(() -> new RuntimeException("Provincia Paita no encontrada"))),
                new Distrito(null, "Tamarindo", "Distrito de Tamarindo", true, provinciaRepository.findByNombre("Paita").orElseThrow(() -> new RuntimeException("Provincia Paita no encontrada"))),
                new Distrito(null, "Vichayal", "Distrito de Vichayal", true, provinciaRepository.findByNombre("Paita").orElseThrow(() -> new RuntimeException("Provincia Paita no encontrada"))),

                // [Provincia de Sullana]
                new Distrito(null, "Sullana", "Distrito de Sullana", true, provinciaRepository.findByNombre("Sullana").orElseThrow(() -> new RuntimeException("Provincia Sullana no encontrada"))),
                new Distrito(null, "Bellavista", "Distrito de Bellavista", true, provinciaRepository.findByNombre("Sullana").orElseThrow(() -> new RuntimeException("Provincia Sullana no encontrada"))),
                new Distrito(null, "Ignacio Escudero", "Distrito de Ignacio Escudero", true, provinciaRepository.findByNombre("Sullana").orElseThrow(() -> new RuntimeException("Provincia Sullana no encontrada"))),
                new Distrito(null, "Lancones", "Distrito de Lancones", true, provinciaRepository.findByNombre("Sullana").orElseThrow(() -> new RuntimeException("Provincia Sullana no encontrada"))),
                new Distrito(null, "Marcavelica", "Distrito de Marcavelica", true, provinciaRepository.findByNombre("Sullana").orElseThrow(() -> new RuntimeException("Provincia Sullana no encontrada"))),
                new Distrito(null, "Miguel Checa", "Distrito de Miguel Checa", true, provinciaRepository.findByNombre("Sullana").orElseThrow(() -> new RuntimeException("Provincia Sullana no encontrada"))),
                new Distrito(null, "Querecotillo", "Distrito de Querecotillo", true, provinciaRepository.findByNombre("Sullana").orElseThrow(() -> new RuntimeException("Provincia Sullana no encontrada"))),
                new Distrito(null, "Salitral", "Distrito de Salitral", true, provinciaRepository.findByNombre("Sullana").orElseThrow(() -> new RuntimeException("Provincia Sullana no encontrada"))),

                // [Provincia de Talara]
                new Distrito(null, "Pariñas", "Distrito de Pariñas", true, provinciaRepository.findByNombre("Talara").orElseThrow(() -> new RuntimeException("Provincia Talara no encontrada"))),
                new Distrito(null, "El Alto", "Distrito de El Alto", true, provinciaRepository.findByNombre("Talara").orElseThrow(() -> new RuntimeException("Provincia Talara no encontrada"))),
                new Distrito(null, "La Brea", "Distrito de La Brea", true, provinciaRepository.findByNombre("Talara").orElseThrow(() -> new RuntimeException("Provincia Talara no encontrada"))),
                new Distrito(null, "Lobitos", "Distrito de Lobitos", true, provinciaRepository.findByNombre("Talara").orElseThrow(() -> new RuntimeException("Provincia Talara no encontrada"))),
                new Distrito(null, "Los Órganos", "Distrito de Los Órganos", true, provinciaRepository.findByNombre("Talara").orElseThrow(() -> new RuntimeException("Provincia Talara no encontrada"))),
                new Distrito(null, "Máncora", "Distrito de Máncora", true, provinciaRepository.findByNombre("Talara").orElseThrow(() -> new RuntimeException("Provincia Talara no encontrada"))),

                // [Provincia de Sechura]
                new Distrito(null, "Sechura", "Distrito de Sechura", true, provinciaRepository.findByNombre("Sechura").orElseThrow(() -> new RuntimeException("Provincia Sechura no encontrada"))),
                new Distrito(null, "Bellavista de la Unión", "Distrito de Bellavista de la Unión", true, provinciaRepository.findByNombre("Sechura").orElseThrow(() -> new RuntimeException("Provincia Sechura no encontrada"))),
                new Distrito(null, "Bernal", "Distrito de Bernal", true, provinciaRepository.findByNombre("Sechura").orElseThrow(() -> new RuntimeException("Provincia Sechura no encontrada"))),
                new Distrito(null, "Cristo Nos Valga", "Distrito de Cristo Nos Valga", true, provinciaRepository.findByNombre("Sechura").orElseThrow(() -> new RuntimeException("Provincia Sechura no encontrada"))),
                new Distrito(null, "Vice", "Distrito de Vice", true, provinciaRepository.findByNombre("Sechura").orElseThrow(() -> new RuntimeException("Provincia Sechura no encontrada"))),
                new Distrito(null, "Rinconada Llicuar", "Distrito de Rinconada Llicuar", true, provinciaRepository.findByNombre("Sechura").orElseThrow(() -> new RuntimeException("Provincia Sechura no encontrada")))
        );
        distritoRepository.saveAll(distritos);
    }

    private void seedDistritosPuno() {
        List<Distrito> distritos = Arrays.asList(
                // Puno
                // [Provincia de Puno]
                new Distrito(null, "Puno", "Distrito de Puno", true, provinciaRepository.findByNombre("Puno").orElseThrow(() -> new RuntimeException("Provincia Puno no encontrada"))),
                new Distrito(null, "Acora", "Distrito de Acora", true, provinciaRepository.findByNombre("Puno").orElseThrow(() -> new RuntimeException("Provincia Puno no encontrada"))),
                new Distrito(null, "Amantani", "Distrito de Amantani", true, provinciaRepository.findByNombre("Puno").orElseThrow(() -> new RuntimeException("Provincia Puno no encontrada"))),
                new Distrito(null, "Atuncolla", "Distrito de Atuncolla", true, provinciaRepository.findByNombre("Puno").orElseThrow(() -> new RuntimeException("Provincia Puno no encontrada"))),
                new Distrito(null, "Capachica", "Distrito de Capachica", true, provinciaRepository.findByNombre("Puno").orElseThrow(() -> new RuntimeException("Provincia Puno no encontrada"))),
                new Distrito(null, "Chucuito", "Distrito de Chucuito", true, provinciaRepository.findByNombre("Puno").orElseThrow(() -> new RuntimeException("Provincia Puno no encontrada"))),
                new Distrito(null, "Coata", "Distrito de Coata", true, provinciaRepository.findByNombre("Puno").orElseThrow(() -> new RuntimeException("Provincia Puno no encontrada"))),
                new Distrito(null, "Huata", "Distrito de Huata", true, provinciaRepository.findByNombre("Puno").orElseThrow(() -> new RuntimeException("Provincia Puno no encontrada"))),
                new Distrito(null, "Mañazo", "Distrito de Mañazo", true, provinciaRepository.findByNombre("Puno").orElseThrow(() -> new RuntimeException("Provincia Puno no encontrada"))),
                new Distrito(null, "Paucarcolla", "Distrito de Paucarcolla", true, provinciaRepository.findByNombre("Puno").orElseThrow(() -> new RuntimeException("Provincia Puno no encontrada"))),
                new Distrito(null, "Pichacani", "Distrito de Pichacani", true, provinciaRepository.findByNombre("Puno").orElseThrow(() -> new RuntimeException("Provincia Puno no encontrada"))),
                new Distrito(null, "Platería", "Distrito de Platería", true, provinciaRepository.findByNombre("Puno").orElseThrow(() -> new RuntimeException("Provincia Puno no encontrada"))),
                new Distrito(null, "San Antonio", "Distrito de San Antonio", true, provinciaRepository.findByNombre("Puno").orElseThrow(() -> new RuntimeException("Provincia Puno no encontrada"))),
                new Distrito(null, "Tiquillaca", "Distrito de Tiquillaca", true, provinciaRepository.findByNombre("Puno").orElseThrow(() -> new RuntimeException("Provincia Puno no encontrada"))),
                new Distrito(null, "Vilque", "Distrito de Vilque", true, provinciaRepository.findByNombre("Puno").orElseThrow(() -> new RuntimeException("Provincia Puno no encontrada"))),

                // [Provincia de Azángaro]
                new Distrito(null, "Azángaro", "Distrito de Azángaro", true, provinciaRepository.findByNombre("Azángaro").orElseThrow(() -> new RuntimeException("Provincia Azángaro no encontrada"))),
                new Distrito(null, "Achaya", "Distrito de Achaya", true, provinciaRepository.findByNombre("Azángaro").orElseThrow(() -> new RuntimeException("Provincia Azángaro no encontrada"))),
                new Distrito(null, "Arapa", "Distrito de Arapa", true, provinciaRepository.findByNombre("Azángaro").orElseThrow(() -> new RuntimeException("Provincia Azángaro no encontrada"))),
                new Distrito(null, "Asillo", "Distrito de Asillo", true, provinciaRepository.findByNombre("Azángaro").orElseThrow(() -> new RuntimeException("Provincia Azángaro no encontrada"))),
                new Distrito(null, "Caminaca", "Distrito de Caminaca", true, provinciaRepository.findByNombre("Azángaro").orElseThrow(() -> new RuntimeException("Provincia Azángaro no encontrada"))),
                new Distrito(null, "Chupa", "Distrito de Chupa", true, provinciaRepository.findByNombre("Azángaro").orElseThrow(() -> new RuntimeException("Provincia Azángaro no encontrada"))),
                new Distrito(null, "José Domingo Choquehuanca", "Distrito de José Domingo Choquehuanca", true, provinciaRepository.findByNombre("Azángaro").orElseThrow(() -> new RuntimeException("Provincia Azángaro no encontrada"))),
                new Distrito(null, "Muñani", "Distrito de Muñani", true, provinciaRepository.findByNombre("Azángaro").orElseThrow(() -> new RuntimeException("Provincia Azángaro no encontrada"))),
                new Distrito(null, "Potoni", "Distrito de Potoni", true, provinciaRepository.findByNombre("Azángaro").orElseThrow(() -> new RuntimeException("Provincia Azángaro no encontrada"))),
                new Distrito(null, "Saman", "Distrito de Saman", true, provinciaRepository.findByNombre("Azángaro").orElseThrow(() -> new RuntimeException("Provincia Azángaro no encontrada"))),
                new Distrito(null, "San Antón", "Distrito de San Antón", true, provinciaRepository.findByNombre("Azángaro").orElseThrow(() -> new RuntimeException("Provincia Azángaro no encontrada"))),
                new Distrito(null, "San José", "Distrito de San José", true, provinciaRepository.findByNombre("Azángaro").orElseThrow(() -> new RuntimeException("Provincia Azángaro no encontrada"))),
                new Distrito(null, "San Juan de Salinas", "Distrito de San Juan de Salinas", true, provinciaRepository.findByNombre("Azángaro").orElseThrow(() -> new RuntimeException("Provincia Azángaro no encontrada"))),
                new Distrito(null, "Santiago de Pupuja", "Distrito de Santiago de Pupuja", true, provinciaRepository.findByNombre("Azángaro").orElseThrow(() -> new RuntimeException("Provincia Azángaro no encontrada"))),
                new Distrito(null, "Tirapata", "Distrito de Tirapata", true, provinciaRepository.findByNombre("Azángaro").orElseThrow(() -> new RuntimeException("Provincia Azángaro no encontrada"))),

                // [Provincia de Carabaya]
                new Distrito(null, "Macusani", "Distrito de Macusani", true, provinciaRepository.findByNombre("Carabaya").orElseThrow(() -> new RuntimeException("Provincia Carabaya no encontrada"))),
                new Distrito(null, "Ajoyani", "Distrito de Ajoyani", true, provinciaRepository.findByNombre("Carabaya").orElseThrow(() -> new RuntimeException("Provincia Carabaya no encontrada"))),
                new Distrito(null, "Ayapata", "Distrito de Ayapata", true, provinciaRepository.findByNombre("Carabaya").orElseThrow(() -> new RuntimeException("Provincia Carabaya no encontrada"))),
                new Distrito(null, "Coasa", "Distrito de Coasa", true, provinciaRepository.findByNombre("Carabaya").orElseThrow(() -> new RuntimeException("Provincia Carabaya no encontrada"))),
                new Distrito(null, "Corani", "Distrito de Corani", true, provinciaRepository.findByNombre("Carabaya").orElseThrow(() -> new RuntimeException("Provincia Carabaya no encontrada"))),
                new Distrito(null, "Crucero", "Distrito de Crucero", true, provinciaRepository.findByNombre("Carabaya").orElseThrow(() -> new RuntimeException("Provincia Carabaya no encontrada"))),
                new Distrito(null, "Ituata", "Distrito de Ituata", true, provinciaRepository.findByNombre("Carabaya").orElseThrow(() -> new RuntimeException("Provincia Carabaya no encontrada"))),
                new Distrito(null, "Ollachea", "Distrito de Ollachea", true, provinciaRepository.findByNombre("Carabaya").orElseThrow(() -> new RuntimeException("Provincia Carabaya no encontrada"))),
                new Distrito(null, "San Gabán", "Distrito de San Gabán", true, provinciaRepository.findByNombre("Carabaya").orElseThrow(() -> new RuntimeException("Provincia Carabaya no encontrada"))),
                new Distrito(null, "Usicayos", "Distrito de Usicayos", true, provinciaRepository.findByNombre("Carabaya").orElseThrow(() -> new RuntimeException("Provincia Carabaya no encontrada"))),

                // [Provincia de Chucuito]
                new Distrito(null, "Juli", "Distrito de Juli", true, provinciaRepository.findByNombre("Chucuito").orElseThrow(() -> new RuntimeException("Provincia Chucuito no encontrada"))),
                new Distrito(null, "Desaguadero", "Distrito de Desaguadero", true, provinciaRepository.findByNombre("Chucuito").orElseThrow(() -> new RuntimeException("Provincia Chucuito no encontrada"))),
                new Distrito(null, "Huacullani", "Distrito de Huacullani", true, provinciaRepository.findByNombre("Chucuito").orElseThrow(() -> new RuntimeException("Provincia Chucuito no encontrada"))),
                new Distrito(null, "Kelluyo", "Distrito de Kelluyo", true, provinciaRepository.findByNombre("Chucuito").orElseThrow(() -> new RuntimeException("Provincia Chucuito no encontrada"))),
                new Distrito(null, "Pisacoma", "Distrito de Pisacoma", true, provinciaRepository.findByNombre("Chucuito").orElseThrow(() -> new RuntimeException("Provincia Chucuito no encontrada"))),
                new Distrito(null, "Pomata", "Distrito de Pomata", true, provinciaRepository.findByNombre("Chucuito").orElseThrow(() -> new RuntimeException("Provincia Chucuito no encontrada"))),
                new Distrito(null, "Zepita", "Distrito de Zepita", true, provinciaRepository.findByNombre("Chucuito").orElseThrow(() -> new RuntimeException("Provincia Chucuito no encontrada"))),

                // [Provincia de El Collao]
                new Distrito(null, "Ilave", "Distrito de Ilave", true, provinciaRepository.findByNombre("El Collao").orElseThrow(() -> new RuntimeException("Provincia El Collao no encontrada"))),
                new Distrito(null, "Capaso", "Distrito de Capaso", true, provinciaRepository.findByNombre("El Collao").orElseThrow(() -> new RuntimeException("Provincia El Collao no encontrada"))),
                new Distrito(null, "Pilcuyo", "Distrito de Pilcuyo", true, provinciaRepository.findByNombre("El Collao").orElseThrow(() -> new RuntimeException("Provincia El Collao no encontrada"))),
                new Distrito(null, "Santa Rosa", "Distrito de Santa Rosa", true, provinciaRepository.findByNombre("El Collao").orElseThrow(() -> new RuntimeException("Provincia El Collao no encontrada"))),
                new Distrito(null, "Conduriri", "Distrito de Conduriri", true, provinciaRepository.findByNombre("El Collao").orElseThrow(() -> new RuntimeException("Provincia El Collao no encontrada"))),

                // [Provincia de Huancané]
                new Distrito(null, "Huancané", "Distrito de Huancané", true, provinciaRepository.findByNombre("Huancané").orElseThrow(() -> new RuntimeException("Provincia Huancané no encontrada"))),
                new Distrito(null, "Cojata", "Distrito de Cojata", true, provinciaRepository.findByNombre("Huancané").orElseThrow(() -> new RuntimeException("Provincia Huancané no encontrada"))),
                new Distrito(null, "Huatasani", "Distrito de Huatasani", true, provinciaRepository.findByNombre("Huancané").orElseThrow(() -> new RuntimeException("Provincia Huancané no encontrada"))),
                new Distrito(null, "Inchupalla", "Distrito de Inchupalla", true, provinciaRepository.findByNombre("Huancané").orElseThrow(() -> new RuntimeException("Provincia Huancané no encontrada"))),
                new Distrito(null, "Pusi", "Distrito de Pusi", true, provinciaRepository.findByNombre("Huancané").orElseThrow(() -> new RuntimeException("Provincia Huancané no encontrada"))),
                new Distrito(null, "Rosaspata", "Distrito de Rosaspata", true, provinciaRepository.findByNombre("Huancané").orElseThrow(() -> new RuntimeException("Provincia Huancané no encontrada"))),
                new Distrito(null, "Taraco", "Distrito de Taraco", true, provinciaRepository.findByNombre("Huancané").orElseThrow(() -> new RuntimeException("Provincia Huancané no encontrada"))),
                new Distrito(null, "Vilque Chico", "Distrito de Vilque Chico", true, provinciaRepository.findByNombre("Huancané").orElseThrow(() -> new RuntimeException("Provincia Huancané no encontrada"))),


                // [Provincia de Lampa]
                new Distrito(null, "Lampa", "Distrito de Lampa", true, provinciaRepository.findByNombre("Lampa").orElseThrow(() -> new RuntimeException("Provincia Lampa no encontrada"))),
                new Distrito(null, "Cabanilla", "Distrito de Cabanilla", true, provinciaRepository.findByNombre("Lampa").orElseThrow(() -> new RuntimeException("Provincia Lampa no encontrada"))),
                new Distrito(null, "Calapuja", "Distrito de Calapuja", true, provinciaRepository.findByNombre("Lampa").orElseThrow(() -> new RuntimeException("Provincia Lampa no encontrada"))),
                new Distrito(null, "Nicasio", "Distrito de Nicasio", true, provinciaRepository.findByNombre("Lampa").orElseThrow(() -> new RuntimeException("Provincia Lampa no encontrada"))),
                new Distrito(null, "Ocuviri", "Distrito de Ocuviri", true, provinciaRepository.findByNombre("Lampa").orElseThrow(() -> new RuntimeException("Provincia Lampa no encontrada"))),
                new Distrito(null, "Palca", "Distrito de Palca", true, provinciaRepository.findByNombre("Lampa").orElseThrow(() -> new RuntimeException("Provincia Lampa no encontrada"))),
                new Distrito(null, "Paratía", "Distrito de Paratía", true, provinciaRepository.findByNombre("Lampa").orElseThrow(() -> new RuntimeException("Provincia Lampa no encontrada"))),
                new Distrito(null, "Pucará", "Distrito de Pucará", true, provinciaRepository.findByNombre("Lampa").orElseThrow(() -> new RuntimeException("Provincia Lampa no encontrada"))),
                new Distrito(null, "Santa Lucía", "Distrito de Santa Lucía", true, provinciaRepository.findByNombre("Lampa").orElseThrow(() -> new RuntimeException("Provincia Lampa no encontrada"))),
                new Distrito(null, "Vilavila", "Distrito de Vilavila", true, provinciaRepository.findByNombre("Lampa").orElseThrow(() -> new RuntimeException("Provincia Lampa no encontrada"))),

                // [Provincia de Melgar]
                new Distrito(null, "Ayaviri", "Distrito de Ayaviri", true, provinciaRepository.findByNombre("Melgar").orElseThrow(() -> new RuntimeException("Provincia Melgar no encontrada"))),
                new Distrito(null, "Antauta", "Distrito de Antauta", true, provinciaRepository.findByNombre("Melgar").orElseThrow(() -> new RuntimeException("Provincia Melgar no encontrada"))),
                new Distrito(null, "Cupi", "Distrito de Cupi", true, provinciaRepository.findByNombre("Melgar").orElseThrow(() -> new RuntimeException("Provincia Melgar no encontrada"))),
                new Distrito(null, "Llalli", "Distrito de Llalli", true, provinciaRepository.findByNombre("Melgar").orElseThrow(() -> new RuntimeException("Provincia Melgar no encontrada"))),
                new Distrito(null, "Macari", "Distrito de Macari", true, provinciaRepository.findByNombre("Melgar").orElseThrow(() -> new RuntimeException("Provincia Melgar no encontrada"))),
                new Distrito(null, "Nuñoa", "Distrito de Nuñoa", true, provinciaRepository.findByNombre("Melgar").orElseThrow(() -> new RuntimeException("Provincia Melgar no encontrada"))),
                new Distrito(null, "Orurillo", "Distrito de Orurillo", true, provinciaRepository.findByNombre("Melgar").orElseThrow(() -> new RuntimeException("Provincia Melgar no encontrada"))),
                new Distrito(null, "Santa Rosa", "Distrito de Santa Rosa", true, provinciaRepository.findByNombre("Melgar").orElseThrow(() -> new RuntimeException("Provincia Melgar no encontrada"))),
                new Distrito(null, "Umachiri", "Distrito de Umachiri", true, provinciaRepository.findByNombre("Melgar").orElseThrow(() -> new RuntimeException("Provincia Melgar no encontrada"))),

                // [Provincia de Moho]
                new Distrito(null, "Moho", "Distrito de Moho", true, provinciaRepository.findByNombre("Moho").orElseThrow(() -> new RuntimeException("Provincia Moho no encontrada"))),
                new Distrito(null, "Conima", "Distrito de Conima", true, provinciaRepository.findByNombre("Moho").orElseThrow(() -> new RuntimeException("Provincia Moho no encontrada"))),
                new Distrito(null, "Huayrapata", "Distrito de Huayrapata", true, provinciaRepository.findByNombre("Moho").orElseThrow(() -> new RuntimeException("Provincia Moho no encontrada"))),
                new Distrito(null, "Tilali", "Distrito de Tilali", true, provinciaRepository.findByNombre("Moho").orElseThrow(() -> new RuntimeException("Provincia Moho no encontrada"))),

                // [Provincia de San Antonio de Putina]
                new Distrito(null, "Putina", "Distrito de Putina", true, provinciaRepository.findByNombre("San Antonio de Putina").orElseThrow(() -> new RuntimeException("Provincia San Antonio de Putina no encontrada"))),
                new Distrito(null, "Ananea", "Distrito de Ananea", true, provinciaRepository.findByNombre("San Antonio de Putina").orElseThrow(() -> new RuntimeException("Provincia San Antonio de Putina no encontrada"))),
                new Distrito(null, "Pedro Vilca Apaza", "Distrito de Pedro Vilca Apaza", true, provinciaRepository.findByNombre("San Antonio de Putina").orElseThrow(() -> new RuntimeException("Provincia San Antonio de Putina no encontrada"))),
                new Distrito(null, "Quilcapuncu", "Distrito de Quiaca", true, provinciaRepository.findByNombre("San Antonio de Putina").orElseThrow(() -> new RuntimeException("Provincia San Antonio de Putina no encontrada"))),
                new Distrito(null, "Sina", "Distrito de Sina", true, provinciaRepository.findByNombre("San Antonio de Putina").orElseThrow(() -> new RuntimeException("Provincia San Antonio de Putina no encontrada"))),

                // [Provincia de San Román]
                new Distrito(null, "Juliaca", "Distrito de Juliaca", true, provinciaRepository.findByNombre("San Román").orElseThrow(() -> new RuntimeException("Provincia San Román no encontrada"))),
                new Distrito(null, "Cabana", "Distrito de Cabana", true, provinciaRepository.findByNombre("San Román").orElseThrow(() -> new RuntimeException("Provincia San Román no encontrada"))),
                new Distrito(null, "Cabanillas", "Distrito de Cabanillas", true, provinciaRepository.findByNombre("San Román").orElseThrow(() -> new RuntimeException("Provincia San Román no encontrada"))),
                new Distrito(null, "Caracoto", "Distrito de Caracoto", true, provinciaRepository.findByNombre("San Román").orElseThrow(() -> new RuntimeException("Provincia San Román no encontrada"))),

                // [Provincia de Sandia]
                new Distrito(null, "Sandia", "Distrito de Sandia", true, provinciaRepository.findByNombre("Sandia").orElseThrow(() -> new RuntimeException("Provincia Sandia no encontrada"))),
                new Distrito(null, "Cuyocuyo", "Distrito de Cuyocuyo", true, provinciaRepository.findByNombre("Sandia").orElseThrow(() -> new RuntimeException("Provincia Sandia no encontrada"))),
                new Distrito(null, "Limbani", "Distrito de Limbani", true, provinciaRepository.findByNombre("Sandia").orElseThrow(() -> new RuntimeException("Provincia Sandia no encontrada"))),
                new Distrito(null, "Patambuco", "Distrito de Patambuco", true, provinciaRepository.findByNombre("Sandia").orElseThrow(() -> new RuntimeException("Provincia Sandia no encontrada"))),
                new Distrito(null, "Phara", "Distrito de Phara", true, provinciaRepository.findByNombre("Sandia").orElseThrow(() -> new RuntimeException("Provincia Sandia no encontrada"))),
                new Distrito(null, "Quiaca", "Distrito de Quiaca", true, provinciaRepository.findByNombre("Sandia").orElseThrow(() -> new RuntimeException("Provincia Sandia no encontrada"))),
                new Distrito(null, "San Juan del Oro", "Distrito de San Juan del Oro", true, provinciaRepository.findByNombre("Sandia").orElseThrow(() -> new RuntimeException("Provincia Sandia no encontrada"))),
                new Distrito(null, "Yanahuaya", "Distrito de Yanahuaya", true, provinciaRepository.findByNombre("Sandia").orElseThrow(() -> new RuntimeException("Provincia Sandia no encontrada"))),
                new Distrito(null, "Alto Inambari", "Distrito de Alto Inambari", true, provinciaRepository.findByNombre("Sandia").orElseThrow(() -> new RuntimeException("Provincia Sandia no encontrada"))),
                new Distrito(null, "San Pedro de Putina Punco", "Distrito de San Pedro de Putina Punco", true, provinciaRepository.findByNombre("Sandia").orElseThrow(() -> new RuntimeException("Provincia Sandia no encontrada"))),

                // [Provincia de Yunguyo]
                new Distrito(null, "Yunguyo", "Distrito de Yunguyo", true, provinciaRepository.findByNombre("Yunguyo").orElseThrow(() -> new RuntimeException("Provincia Yunguyo no encontrada"))),
                new Distrito(null, "Anapia", "Distrito de Anapia", true, provinciaRepository.findByNombre("Yunguyo").orElseThrow(() -> new RuntimeException("Provincia Yunguyo no encontrada"))),
                new Distrito(null, "Copani", "Distrito de Copani", true, provinciaRepository.findByNombre("Yunguyo").orElseThrow(() -> new RuntimeException("Provincia Yunguyo no encontrada"))),
                new Distrito(null, "Cuturapi", "Distrito de Cuturapi", true, provinciaRepository.findByNombre("Yunguyo").orElseThrow(() -> new RuntimeException("Provincia Yunguyo no encontrada"))),
                new Distrito(null, "Ollaraya", "Distrito de Ollaraya", true, provinciaRepository.findByNombre("Yunguyo").orElseThrow(() -> new RuntimeException("Provincia Yunguyo no encontrada"))),
                new Distrito(null, "Tinicachi", "Distrito de Tinicachi", true, provinciaRepository.findByNombre("Yunguyo").orElseThrow(() -> new RuntimeException("Provincia Yunguyo no encontrada"))),
                new Distrito(null, "Unicachi", "Distrito de Unicachi", true, provinciaRepository.findByNombre("Yunguyo").orElseThrow(() -> new RuntimeException("Provincia Yunguyo no encontrada")))
        );
        distritoRepository.saveAll(distritos);
    }

    private void seedDistritosSanMartin() {
        List<Distrito> distritos = Arrays.asList(
                // San Martín
                // [Provincia de Moyobamba]
                new Distrito(null, "Moyobamba", "Distrito de Moyobamba", true, provinciaRepository.findByNombre("Moyobamba").orElseThrow(() -> new RuntimeException("Provincia Moyobamba no encontrada"))),
                new Distrito(null, "Calzada", "Distrito de Calzada", true, provinciaRepository.findByNombre("Moyobamba").orElseThrow(() -> new RuntimeException("Provincia Moyobamba no encontrada"))),
                new Distrito(null, "Habana", "Distrito de Habana", true, provinciaRepository.findByNombre("Moyobamba").orElseThrow(() -> new RuntimeException("Provincia Moyobamba no encontrada"))),
                new Distrito(null, "Jepelacio", "Distrito de Jepelacio", true, provinciaRepository.findByNombre("Moyobamba").orElseThrow(() -> new RuntimeException("Provincia Moyobamba no encontrada"))),
                new Distrito(null, "Soritor", "Distrito de Soritor", true, provinciaRepository.findByNombre("Moyobamba").orElseThrow(() -> new RuntimeException("Provincia Moyobamba no encontrada"))),
                new Distrito(null, "Yantalo", "Distrito de Yantalo", true, provinciaRepository.findByNombre("Moyobamba").orElseThrow(() -> new RuntimeException("Provincia Moyobamba no encontrada"))),

                // [Provincia de Bellavista]
                new Distrito(null, "Bellavista", "Distrito de Bellavista", true, provinciaRepository.findByNombre("Bellavista").orElseThrow(() -> new RuntimeException("Provincia Bellavista no encontrada"))),
                new Distrito(null, "Alto Biavo", "Distrito de Alto Biavo", true, provinciaRepository.findByNombre("Bellavista").orElseThrow(() -> new RuntimeException("Provincia Bellavista no encontrada"))),
                new Distrito(null, "Bajo Biavo", "Distrito de Bajo Biavo", true, provinciaRepository.findByNombre("Bellavista").orElseThrow(() -> new RuntimeException("Provincia Bellavista no encontrada"))),
                new Distrito(null, "Huallaga", "Distrito de Huallaga", true, provinciaRepository.findByNombre("Bellavista").orElseThrow(() -> new RuntimeException("Provincia Bellavista no encontrada"))),
                new Distrito(null, "San Pablo", "Distrito de San Pablo", true, provinciaRepository.findByNombre("Bellavista").orElseThrow(() -> new RuntimeException("Provincia Bellavista no encontrada"))),
                new Distrito(null, "San Rafael", "Distrito de San Rafael", true, provinciaRepository.findByNombre("Bellavista").orElseThrow(() -> new RuntimeException("Provincia Bellavista no encontrada"))),

                // [Provincia de El Dorado]
                new Distrito(null, "San José de Sisa", "Distrito de San José de Sisa", true, provinciaRepository.findByNombre("El Dorado").orElseThrow(() -> new RuntimeException("Provincia El Dorado no encontrada"))),
                new Distrito(null, "Agua Blanca", "Distrito de Agua Blanca", true, provinciaRepository.findByNombre("El Dorado").orElseThrow(() -> new RuntimeException("Provincia El Dorado no encontrada"))),
                new Distrito(null, "San Martín", "Distrito de San Martín", true, provinciaRepository.findByNombre("El Dorado").orElseThrow(() -> new RuntimeException("Provincia El Dorado no encontrada"))),
                new Distrito(null, "Santa Rosa", "Distrito de Santa Rosa", true, provinciaRepository.findByNombre("El Dorado").orElseThrow(() -> new RuntimeException("Provincia El Dorado no encontrada"))),
                new Distrito(null, "Shatoja", "Distrito de Shatoja", true, provinciaRepository.findByNombre("El Dorado").orElseThrow(() -> new RuntimeException("Provincia El Dorado no encontrada"))),

                // [Provincia de Huallaga]
                new Distrito(null, "Saposoa", "Distrito de Saposoa", true, provinciaRepository.findByNombre("Huallaga").orElseThrow(() -> new RuntimeException("Provincia Huallaga no encontrada"))),
                new Distrito(null, "Alto Saposoa", "Distrito de Alto Saposoa", true, provinciaRepository.findByNombre("Huallaga").orElseThrow(() -> new RuntimeException("Provincia Huallaga no encontrada"))),
                new Distrito(null, "El Eslabón", "Distrito de El Eslabón", true, provinciaRepository.findByNombre("Huallaga").orElseThrow(() -> new RuntimeException("Provincia Huallaga no encontrada"))),
                new Distrito(null, "Piscoyacu", "Distrito de Piscoyacu", true, provinciaRepository.findByNombre("Huallaga").orElseThrow(() -> new RuntimeException("Provincia Huallaga no encontrada"))),
                new Distrito(null, "Sacanche", "Distrito de Sacanche", true, provinciaRepository.findByNombre("Huallaga").orElseThrow(() -> new RuntimeException("Provincia Huallaga no encontrada"))),
                new Distrito(null, "Tingo de Saposoa", "Distrito de Tingo de Saposoa", true, provinciaRepository.findByNombre("Huallaga").orElseThrow(() -> new RuntimeException("Provincia Huallaga no encontrada"))),

                // [Provincia de Lamas]
                new Distrito(null, "Lamas", "Distrito de Lamas", true, provinciaRepository.findByNombre("Lamas").orElseThrow(() -> new RuntimeException("Provincia Lamas no encontrada"))),
                new Distrito(null, "Alonso de Alvarado", "Distrito de Alonso de Alvarado", true, provinciaRepository.findByNombre("Lamas").orElseThrow(() -> new RuntimeException("Provincia Lamas no encontrada"))),
                new Distrito(null, "Barranquita", "Distrito de Barranquita", true, provinciaRepository.findByNombre("Lamas").orElseThrow(() -> new RuntimeException("Provincia Lamas no encontrada"))),
                new Distrito(null, "Caynarachi", "Distrito de Caynarachi", true, provinciaRepository.findByNombre("Lamas").orElseThrow(() -> new RuntimeException("Provincia Lamas no encontrada"))),
                new Distrito(null, "Cuñumbuqui", "Distrito de Cuñumbuqui", true, provinciaRepository.findByNombre("Lamas").orElseThrow(() -> new RuntimeException("Provincia Lamas no encontrada"))),
                new Distrito(null, "Pinto Recodo", "Distrito de Pinto Recodo", true, provinciaRepository.findByNombre("Lamas").orElseThrow(() -> new RuntimeException("Provincia Lamas no encontrada"))),
                new Distrito(null, "Rumisapa", "Distrito de Rumisapa", true, provinciaRepository.findByNombre("Lamas").orElseThrow(() -> new RuntimeException("Provincia Lamas no encontrada"))),
                new Distrito(null, "San Roque de Cumbaza", "Distrito de San Roque de Cumbaza", true, provinciaRepository.findByNombre("Lamas").orElseThrow(() -> new RuntimeException("Provincia Lamas no encontrada"))),
                new Distrito(null, "Shanao", "Distrito de Shanao", true, provinciaRepository.findByNombre("Lamas").orElseThrow(() -> new RuntimeException("Provincia Lamas no encontrada"))),
                new Distrito(null, "Tabalosos", "Distrito de Tabalosos", true, provinciaRepository.findByNombre("Lamas").orElseThrow(() -> new RuntimeException("Provincia Lamas no encontrada"))),
                new Distrito(null, "Zapatero", "Distrito de Zapatero", true, provinciaRepository.findByNombre("Lamas").orElseThrow(() -> new RuntimeException("Provincia Lamas no encontrada"))),

                // [Provincia de Mariscal Cáceres]
                new Distrito(null, "Juanjuí", "Distrito de Juanjuí", true, provinciaRepository.findByNombre("Mariscal Cáceres").orElseThrow(() -> new RuntimeException("Provincia Mariscal Cáceres no encontrada"))),
                new Distrito(null, "Campanilla", "Distrito de Campanilla", true, provinciaRepository.findByNombre("Mariscal Cáceres").orElseThrow(() -> new RuntimeException("Provincia Mariscal Cáceres no encontrada"))),
                new Distrito(null, "Huicungo", "Distrito de Huicungo", true, provinciaRepository.findByNombre("Mariscal Cáceres").orElseThrow(() -> new RuntimeException("Provincia Mariscal Cáceres no encontrada"))),
                new Distrito(null, "Pachiza", "Distrito de Pachiza", true, provinciaRepository.findByNombre("Mariscal Cáceres").orElseThrow(() -> new RuntimeException("Provincia Mariscal Cáceres no encontrada"))),
                new Distrito(null, "Pajarillo", "Distrito de Pajarillo", true, provinciaRepository.findByNombre("Mariscal Cáceres").orElseThrow(() -> new RuntimeException("Provincia Mariscal Cáceres no encontrada"))),

                // [Provincia de Picota]
                new Distrito(null, "Picota", "Distrito de Picota", true, provinciaRepository.findByNombre("Picota").orElseThrow(() -> new RuntimeException("Provincia Picota no encontrada"))),
                new Distrito(null, "Buenos Aires", "Distrito de Buenos Aires", true, provinciaRepository.findByNombre("Picota").orElseThrow(() -> new RuntimeException("Provincia Picota no encontrada"))),
                new Distrito(null, "Caspisapa", "Distrito de Caspisapa", true, provinciaRepository.findByNombre("Picota").orElseThrow(() -> new RuntimeException("Provincia Picota no encontrada"))),
                new Distrito(null, "Pilluana", "Distrito de Pilluana", true, provinciaRepository.findByNombre("Picota").orElseThrow(() -> new RuntimeException("Provincia Picota no encontrada"))),
                new Distrito(null, "Pucacaca", "Distrito de Pucacaca", true, provinciaRepository.findByNombre("Picota").orElseThrow(() -> new RuntimeException("Provincia Picota no encontrada"))),
                new Distrito(null, "San Cristóbal", "Distrito de San Cristóbal", true, provinciaRepository.findByNombre("Picota").orElseThrow(() -> new RuntimeException("Provincia Picota no encontrada"))),
                new Distrito(null, "San Hilarión", "Distrito de San Hilarión", true, provinciaRepository.findByNombre("Picota").orElseThrow(() -> new RuntimeException("Provincia Picota no encontrada"))),
                new Distrito(null, "Shamboyacu", "Distrito de Shamboyacu", true, provinciaRepository.findByNombre("Picota").orElseThrow(() -> new RuntimeException("Provincia Picota no encontrada"))),
                new Distrito(null, "Tingo de Ponasa", "Distrito de Tingo de Ponasa", true, provinciaRepository.findByNombre("Picota").orElseThrow(() -> new RuntimeException("Provincia Picota no encontrada"))),
                new Distrito(null, "Tres Unidos", "Distrito de Tres Unidos", true, provinciaRepository.findByNombre("Picota").orElseThrow(() -> new RuntimeException("Provincia Picota no encontrada"))),

                // [Provincia de Rioja]
                new Distrito(null, "Rioja", "Distrito de Rioja", true, provinciaRepository.findByNombre("Rioja").orElseThrow(() -> new RuntimeException("Provincia Rioja no encontrada"))),
                new Distrito(null, "Awajún", "Distrito de Awajún", true, provinciaRepository.findByNombre("Rioja").orElseThrow(() -> new RuntimeException("Provincia Rioja no encontrada"))),
                new Distrito(null, "Elías Soplín Vargas", "Distrito de Elías Soplín Vargas", true, provinciaRepository.findByNombre("Rioja").orElseThrow(() -> new RuntimeException("Provincia Rioja no encontrada"))),
                new Distrito(null, "Nueva Cajamarca", "Distrito de Nueva Cajamarca", true, provinciaRepository.findByNombre("Rioja").orElseThrow(() -> new RuntimeException("Provincia Rioja no encontrada"))),
                new Distrito(null, "Pardo Miguel", "Distrito de Pardo Miguel", true, provinciaRepository.findByNombre("Rioja").orElseThrow(() -> new RuntimeException("Provincia Rioja no encontrada"))),
                new Distrito(null, "Posic", "Distrito de Posic", true, provinciaRepository.findByNombre("Rioja").orElseThrow(() -> new RuntimeException("Provincia Rioja no encontrada"))),
                new Distrito(null, "San Fernando", "Distrito de San Fernando", true, provinciaRepository.findByNombre("Rioja").orElseThrow(() -> new RuntimeException("Provincia Rioja no encontrada"))),
                new Distrito(null, "Yorongos", "Distrito de Yorongos", true, provinciaRepository.findByNombre("Rioja").orElseThrow(() -> new RuntimeException("Provincia Rioja no encontrada"))),
                new Distrito(null, "Yuracyacu", "Distrito de Yuracyacu", true, provinciaRepository.findByNombre("Rioja").orElseThrow(() -> new RuntimeException("Provincia Rioja no encontrada"))),

                // [Provincia de San Martín]
                new Distrito(null, "Tarapoto", "Distrito de Tarapoto", true, provinciaRepository.findByNombre("San Martín").orElseThrow(() -> new RuntimeException("Provincia San Martín no encontrada"))),
                new Distrito(null, "Alberto Leveau", "Distrito de Alberto Leveau", true, provinciaRepository.findByNombre("San Martín").orElseThrow(() -> new RuntimeException("Provincia San Martín no encontrada"))),
                new Distrito(null, "Cacatachi", "Distrito de Cacatachi", true, provinciaRepository.findByNombre("San Martín").orElseThrow(() -> new RuntimeException("Provincia San Martín no encontrada"))),
                new Distrito(null, "Chazuta", "Distrito de Chazuta", true, provinciaRepository.findByNombre("San Martín").orElseThrow(() -> new RuntimeException("Provincia San Martín no encontrada"))),
                new Distrito(null, "Chipurana", "Distrito de Chipurana", true, provinciaRepository.findByNombre("San Martín").orElseThrow(() -> new RuntimeException("Provincia San Martín no encontrada"))),
                new Distrito(null, "El Porvenir", "Distrito de El Porvenir", true, provinciaRepository.findByNombre("San Martín").orElseThrow(() -> new RuntimeException("Provincia San Martín no encontrada"))),
                new Distrito(null, "Huimbayoc", "Distrito de Huimbayoc", true, provinciaRepository.findByNombre("San Martín").orElseThrow(() -> new RuntimeException("Provincia San Martín no encontrada"))),
                new Distrito(null, "Juan Guerra", "Distrito de Juan Guerra", true, provinciaRepository.findByNombre("San Martín").orElseThrow(() -> new RuntimeException("Provincia San Martín no encontrada"))),
                new Distrito(null, "La Banda de Shilcayo", "Distrito de La Banda de Shilcayo", true, provinciaRepository.findByNombre("San Martín").orElseThrow(() -> new RuntimeException("Provincia San Martín no encontrada"))),
                new Distrito(null, "Morales", "Distrito de Morales", true, provinciaRepository.findByNombre("San Martín").orElseThrow(() -> new RuntimeException("Provincia San Martín no encontrada"))),
                new Distrito(null, "Papaplaya", "Distrito de Papaplaya", true, provinciaRepository.findByNombre("San Martín").orElseThrow(() -> new RuntimeException("Provincia San Martín no encontrada"))),
                new Distrito(null, "San Antonio", "Distrito de San Antonio", true, provinciaRepository.findByNombre("San Martín").orElseThrow(() -> new RuntimeException("Provincia San Martín no encontrada"))),
                new Distrito(null, "Sauce", "Distrito de Sauce", true, provinciaRepository.findByNombre("San Martín").orElseThrow(() -> new RuntimeException("Provincia San Martín no encontrada"))),
                new Distrito(null, "Shapaja", "Distrito de Shapaja", true, provinciaRepository.findByNombre("San Martín").orElseThrow(() -> new RuntimeException("Provincia San Martín no encontrada"))),

                // [Provincia de Tocache]
                new Distrito(null, "Tocache", "Distrito de Tocache", true, provinciaRepository.findByNombre("Tocache").orElseThrow(() -> new RuntimeException("Provincia Tocache no encontrada"))),
                new Distrito(null, "Nuevo Progreso", "Distrito de Nuevo Progreso", true, provinciaRepository.findByNombre("Tocache").orElseThrow(() -> new RuntimeException("Provincia Tocache no encontrada"))),
                new Distrito(null, "Pólvora", "Distrito de Pólvora", true, provinciaRepository.findByNombre("Tocache").orElseThrow(() -> new RuntimeException("Provincia Tocache no encontrada"))),
                new Distrito(null, "Shunte", "Distrito de Shunte", true, provinciaRepository.findByNombre("Tocache").orElseThrow(() -> new RuntimeException("Provincia Tocache no encontrada"))),
                new Distrito(null, "Uchiza", "Distrito de Uchiza", true, provinciaRepository.findByNombre("Tocache").orElseThrow(() -> new RuntimeException("Provincia Tocache no encontrada")))
        );
        distritoRepository.saveAll(distritos);
    }

    private void seedDistritosTacna() {
        List<Distrito> distritos = Arrays.asList(
                // Tacna
                // [Provincia de Tacna]
                new Distrito(null, "Tacna", "Distrito de Tacna", true, provinciaRepository.findByNombre("Tacna").orElseThrow(() -> new RuntimeException("Provincia Tacna no encontrada"))),
                new Distrito(null, "Alto de la Alianza", "Distrito de Alto de la Alianza", true, provinciaRepository.findByNombre("Tacna").orElseThrow(() -> new RuntimeException("Provincia Tacna no encontrada"))),
                new Distrito(null, "Calana", "Distrito de Calana", true, provinciaRepository.findByNombre("Tacna").orElseThrow(() -> new RuntimeException("Provincia Tacna no encontrada"))),
                new Distrito(null, "Ciudad Nueva", "Distrito de Ciudad Nueva", true, provinciaRepository.findByNombre("Tacna").orElseThrow(() -> new RuntimeException("Provincia Tacna no encontrada"))),
                new Distrito(null, "Inclán", "Distrito de Inclán", true, provinciaRepository.findByNombre("Tacna").orElseThrow(() -> new RuntimeException("Provincia Tacna no encontrada"))),
                new Distrito(null, "Pachía", "Distrito de Pachía", true, provinciaRepository.findByNombre("Tacna").orElseThrow(() -> new RuntimeException("Provincia Tacna no encontrada"))),
                new Distrito(null, "Palca", "Distrito de Palca", true, provinciaRepository.findByNombre("Tacna").orElseThrow(() -> new RuntimeException("Provincia Tacna no encontrada"))),
                new Distrito(null, "Pocollay", "Distrito de Pocollay", true, provinciaRepository.findByNombre("Tacna").orElseThrow(() -> new RuntimeException("Provincia Tacna no encontrada"))),
                new Distrito(null, "Sama", "Distrito de Sama", true, provinciaRepository.findByNombre("Tacna").orElseThrow(() -> new RuntimeException("Provincia Tacna no encontrada"))),
                new Distrito(null, "Coronel Gregorio Albarracín Lanchipa", "Distrito de Coronel Gregorio Albarracín Lanchipa", true, provinciaRepository.findByNombre("Tacna").orElseThrow(() -> new RuntimeException("Provincia Tacna no encontrada"))),

                // [Provincia de Candarave]
                new Distrito(null, "Candarave", "Distrito de Candarave", true, provinciaRepository.findByNombre("Candarave").orElseThrow(() -> new RuntimeException("Provincia Candarave no encontrada"))),
                new Distrito(null, "Cairani", "Distrito de Cairani", true, provinciaRepository.findByNombre("Candarave").orElseThrow(() -> new RuntimeException("Provincia Candarave no encontrada"))),
                new Distrito(null, "Camilaca", "Distrito de Camilaca", true, provinciaRepository.findByNombre("Candarave").orElseThrow(() -> new RuntimeException("Provincia Candarave no encontrada"))),
                new Distrito(null, "Curibaya", "Distrito de Curibaya", true, provinciaRepository.findByNombre("Candarave").orElseThrow(() -> new RuntimeException("Provincia Candarave no encontrada"))),
                new Distrito(null, "Huanuara", "Distrito de Huanuara", true, provinciaRepository.findByNombre("Candarave").orElseThrow(() -> new RuntimeException("Provincia Candarave no encontrada"))),
                new Distrito(null, "Quillahuani", "Distrito de Quillahuani", true, provinciaRepository.findByNombre("Candarave").orElseThrow(() -> new RuntimeException("Provincia Candarave no encontrada"))),

                // [Provincia de Jorge Basadre]
                new Distrito(null, "Locumba", "Distrito de Locumba", true, provinciaRepository.findByNombre("Jorge Basadre").orElseThrow(() -> new RuntimeException("Provincia Jorge Basadre no encontrada"))),
                new Distrito(null, "Ilabaya", "Distrito de Ilabaya", true, provinciaRepository.findByNombre("Jorge Basadre").orElseThrow(() -> new RuntimeException("Provincia Jorge Basadre no encontrada"))),
                new Distrito(null, "Ite", "Distrito de Ite", true, provinciaRepository.findByNombre("Jorge Basadre").orElseThrow(() -> new RuntimeException("Provincia Jorge Basadre no encontrada"))),

                // [Provincia de Tarata]
                new Distrito(null, "Tarata", "Distrito de Tarata", true, provinciaRepository.findByNombre("Tarata").orElseThrow(() -> new RuntimeException("Provincia Tarata no encontrada"))),
                new Distrito(null, "Héroes Albarracín", "Distrito de Héroes Albarracín", true, provinciaRepository.findByNombre("Tarata").orElseThrow(() -> new RuntimeException("Provincia Tarata no encontrada"))),
                new Distrito(null, "Estique", "Distrito de Estique", true, provinciaRepository.findByNombre("Tarata").orElseThrow(() -> new RuntimeException("Provincia Tarata no encontrada"))),
                new Distrito(null, "Estique Pampa", "Distrito de Estique Pampa", true, provinciaRepository.findByNombre("Tarata").orElseThrow(() -> new RuntimeException("Provincia Tarata no encontrada"))),
                new Distrito(null, "Sitajara", "Distrito de Sitajara", true, provinciaRepository.findByNombre("Tarata").orElseThrow(() -> new RuntimeException("Provincia Tarata no encontrada"))),
                new Distrito(null, "Susapaya", "Distrito de Susapaya", true, provinciaRepository.findByNombre("Tarata").orElseThrow(() -> new RuntimeException("Provincia Tarata no encontrada"))),
                new Distrito(null, "Tarucachi", "Distrito de Tarucachi", true, provinciaRepository.findByNombre("Tarata").orElseThrow(() -> new RuntimeException("Provincia Tarata no encontrada"))),
                new Distrito(null, "Ticaco", "Distrito de Ticaco", true, provinciaRepository.findByNombre("Tarata").orElseThrow(() -> new RuntimeException("Provincia Tarata no encontrada")))
        );
        distritoRepository.saveAll(distritos);
    }

    private void seedDistritosTumbes() {
        List<Distrito> distritos = Arrays.asList(
                //Tumbes
                // [Provincia de Tumbes]
                new Distrito(null, "Tumbes", "Distrito de Tumbes", true, provinciaRepository.findByNombre("Tumbes").orElseThrow(() -> new RuntimeException("Provincia Tumbes no encontrada"))),
                new Distrito(null, "Corrales", "Distrito de Corrales", true, provinciaRepository.findByNombre("Tumbes").orElseThrow(() -> new RuntimeException("Provincia Tumbes no encontrada"))),
                new Distrito(null, "La Cruz", "Distrito de La Cruz", true, provinciaRepository.findByNombre("Tumbes").orElseThrow(() -> new RuntimeException("Provincia Tumbes no encontrada"))),
                new Distrito(null, "Pampas de Hospital", "Distrito de Pampas de Hospital", true, provinciaRepository.findByNombre("Tumbes").orElseThrow(() -> new RuntimeException("Provincia Tumbes no encontrada"))),
                new Distrito(null, "San Jacinto", "Distrito de San Jacinto", true, provinciaRepository.findByNombre("Tumbes").orElseThrow(() -> new RuntimeException("Provincia Tumbes no encontrada"))),
                new Distrito(null, "San Juan de la Virgen", "Distrito de San Juan de la Virgen", true, provinciaRepository.findByNombre("Tumbes").orElseThrow(() -> new RuntimeException("Provincia Tumbes no encontrada"))),

                // [Provincia de Contralmirante Villar]
                new Distrito(null, "Zorritos", "Distrito de Zorritos", true, provinciaRepository.findByNombre("Contralmirante Villar").orElseThrow(() -> new RuntimeException("Provincia Contralmirante Villar no encontrada"))),
                new Distrito(null, "Casitas", "Distrito de Casitas", true, provinciaRepository.findByNombre("Contralmirante Villar").orElseThrow(() -> new RuntimeException("Provincia Contralmirante Villar no encontrada"))),
                new Distrito(null, "Canoas de Punta Sal", "Distrito de Canoas de Punta Sal", true, provinciaRepository.findByNombre("Contralmirante Villar").orElseThrow(() -> new RuntimeException("Provincia Contralmirante Villar no encontrada"))),

                // [Provincia de Zarumilla]
                new Distrito(null, "Zarumilla", "Distrito de Zarumilla", true, provinciaRepository.findByNombre("Zarumilla").orElseThrow(() -> new RuntimeException("Provincia Zarumilla no encontrada"))),
                new Distrito(null, "Aguas Verdes", "Distrito de Aguas Verdes", true, provinciaRepository.findByNombre("Zarumilla").orElseThrow(() -> new RuntimeException("Provincia Zarumilla no encontrada"))),
                new Distrito(null, "Matapalo", "Distrito de Matapalo", true, provinciaRepository.findByNombre("Zarumilla").orElseThrow(() -> new RuntimeException("Provincia Zarumilla no encontrada"))),
                new Distrito(null, "Papayal", "Distrito de Papayal", true, provinciaRepository.findByNombre("Zarumilla").orElseThrow(() -> new RuntimeException("Provincia Zarumilla no encontrada")))
        );
        distritoRepository.saveAll(distritos);
    }

    private void seedDistritosUcayali() {
        List<Distrito> distritos = Arrays.asList(
                // Ucayali
                // [Provincia de Coronel Portillo]
                new Distrito(null, "Callería", "Distrito de Callería", true, provinciaRepository.findByNombre("Coronel Portillo").orElseThrow(() -> new RuntimeException("Provincia Coronel Portillo no encontrada"))),
                new Distrito(null, "Campoverde", "Distrito de Campoverde", true, provinciaRepository.findByNombre("Coronel Portillo").orElseThrow(() -> new RuntimeException("Provincia Coronel Portillo no encontrada"))),
                new Distrito(null, "Iparía", "Distrito de Iparía", true, provinciaRepository.findByNombre("Coronel Portillo").orElseThrow(() -> new RuntimeException("Provincia Coronel Portillo no encontrada"))),
                new Distrito(null, "Masisea", "Distrito de Masisea", true, provinciaRepository.findByNombre("Coronel Portillo").orElseThrow(() -> new RuntimeException("Provincia Coronel Portillo no encontrada"))),
                new Distrito(null, "Yarinacocha", "Distrito de Yarinacocha", true, provinciaRepository.findByNombre("Coronel Portillo").orElseThrow(() -> new RuntimeException("Provincia Coronel Portillo no encontrada"))),
                new Distrito(null, "Nueva Requena", "Distrito de Nueva Requena", true, provinciaRepository.findByNombre("Coronel Portillo").orElseThrow(() -> new RuntimeException("Provincia Coronel Portillo no encontrada"))),
                new Distrito(null, "Manantay", "Distrito de Manantay", true, provinciaRepository.findByNombre("Coronel Portillo").orElseThrow(() -> new RuntimeException("Provincia Coronel Portillo no encontrada"))),

                // [Provincia de Atalaya]
                new Distrito(null, "Raymondi", "Distrito de Raymondi", true, provinciaRepository.findByNombre("Atalaya").orElseThrow(() -> new RuntimeException("Provincia Atalaya no encontrada"))),
                new Distrito(null, "Sepahua", "Distrito de Sepahua", true, provinciaRepository.findByNombre("Atalaya").orElseThrow(() -> new RuntimeException("Provincia Atalaya no encontrada"))),
                new Distrito(null, "Tahuania", "Distrito de Tahuania", true, provinciaRepository.findByNombre("Atalaya").orElseThrow(() -> new RuntimeException("Provincia Atalaya no encontrada"))),
                new Distrito(null, "Yurúa", "Distrito de Yurúa", true, provinciaRepository.findByNombre("Atalaya").orElseThrow(() -> new RuntimeException("Provincia Atalaya no encontrada"))),

                // [Provincia de Padre Abad]
                new Distrito(null, "Padre Abad", "Distrito de Padre Abad", true, provinciaRepository.findByNombre("Padre Abad").orElseThrow(() -> new RuntimeException("Provincia Padre Abad no encontrada"))),
                new Distrito(null, "Irazola", "Distrito de Irazola", true, provinciaRepository.findByNombre("Padre Abad").orElseThrow(() -> new RuntimeException("Provincia Padre Abad no encontrada"))),
                new Distrito(null, "Curimaná", "Distrito de Curimaná", true, provinciaRepository.findByNombre("Padre Abad").orElseThrow(() -> new RuntimeException("Provincia Padre Abad no encontrada"))),
                new Distrito(null, "Neshuya", "Distrito de Neshuya", true, provinciaRepository.findByNombre("Padre Abad").orElseThrow(() -> new RuntimeException("Provincia Padre Abad no encontrada"))),
                new Distrito(null, "Alexander Von Humboldt", "Distrito de Alexander Von Humboldt", true, provinciaRepository.findByNombre("Padre Abad").orElseThrow(() -> new RuntimeException("Provincia Padre Abad no encontrada"))),

                // [Provincia de Purús]
                new Distrito(null, "Purús", "Distrito de Purús", true, provinciaRepository.findByNombre("Purús").orElseThrow(() -> new RuntimeException("Provincia Purús no encontrada")))
        );
        distritoRepository.saveAll(distritos);
    }

    private void seedAllDistritos() {
        if (distritoRepository.count() == 0) {
            seedDistritosAmazonas();
            seedDistritosAncash();
            seedDistritosApurimac();
            seedDistritosArequipa();
            seedDistritosAyacucho();
            seedDistritosCajamarca();
            seedDistritosCallao();
            seedDistritosCusco();
            seedDistritosHuancavelica();
            seedDistritosHuanuco();
            seedDistritosIca();
            seedDistritosJunin();
            seedDistritosLaLibertad();
            seedDistritosLambayeque();
            seedDistritosLima();
            seedDistritosLoreto();
            seedDistritosMadreDeDios();
            seedDistritosMoquegua();
            seedDistritosPasco();
            seedDistritosPiura();
            seedDistritosPuno();
            seedDistritosSanMartin();
            seedDistritosTacna();
            seedDistritosTumbes();
            seedDistritosUcayali();
        }
    }

    private void seedLotes() {
        if (loteRepository.count() == 0) {
            // Estados de lote posibles (asegúrate de que estén sembrados antes)
            EstadoLote disponible = estadoLoteRepository.findByNombre("Disponible")
                    .orElseThrow(() -> new RuntimeException("EstadoLote 'Disponible' no encontrado"));

            // Ejemplo: tomar un distrito existente (puedes variar esto según tu seed de distritos)
            Distrito distrito = distritoRepository.findByNombre("Pacasmayo")
                    .orElseThrow(() -> new RuntimeException("Distrito 'Pacasmayo' no encontrado"));



            for (int i = 1; i <= 10; i++) {

                Lote lote = new Lote();
                lote.setNombre("Lote " + i);
                lote.setDescripcion("Lote número " + i);
                lote.setPrecio(1000.0 * i);
                lote.setArea(120.0 + (i * 10)); // Ejemplo de áreas diferentes
                lote.setEstadoLote(disponible);
                lote.setDistrito(distrito);
                lote.setDireccion("Mz " + i + " Lt " + (i + 10));
                lote.setActivo(true);

                loteRepository.save(lote);
            }
            System.out.println("✅ Lotes insertados correctamente");
        } else {
            System.out.println("ℹ️ Lotes ya existen en la base de datos, no se insertaron nuevos.");
        }
    }

    private void seedVentas() {
        if (ventaRepository.count() == 0) {
            // 1. Buscar Cliente por numeroDocumento
            Cliente cliente = clienteRepository.findByNumeroDocumento("12345678")
                    .orElseThrow(() -> new RuntimeException("Cliente con documento '12345678' no encontrado"));

            // 2. Buscar Lote de prueba (ajusta según tu repositorio de Lotes)
            Lote lote = loteRepository.findByNombre("Lote 1")
                    .orElseThrow(() -> new RuntimeException("Lote 'Lote 1' no encontrado"));

            // 3. Buscar Estado de Venta (ej. "Pendiente")
            EstadoVenta estadoVenta = estadoVentaRepository.findByNombre("Pendiente")
                    .orElseThrow(() -> new RuntimeException("EstadoVenta 'Pendiente' no encontrado"));

            // 4. Buscar Moneda (ej. "Sol")
            Moneda moneda = monedaRepository.findByNombre("Sol")
                    .orElseThrow(() -> new RuntimeException("Moneda 'Sol' no encontrada"));

            // 5. Crear la venta de prueba
            Venta ventaPrueba = new Venta(
                    null,            // UUID generado automáticamente
                    cliente,
                    lote,
                    estadoVenta,
                    moneda,
                    LocalDate.now(),
                    150000.00,       // monto de prueba
                    true             // activo
            );

            ventaRepository.save(ventaPrueba);
            System.out.println("✅ Venta de prueba insertada en ventas.ventas");
        } else {
            System.out.println("ℹ️ Ya existen ventas en la base de datos, no se insertaron duplicados.");
        }
    }
}
