package com.estacionamiento.config;

import com.estacionamiento.TipoUsuario;
import com.estacionamiento.models.Garage;
import com.estacionamiento.models.Marca;
import com.estacionamiento.models.TipoVehiculo;
import com.estacionamiento.models.Zona;
import com.estacionamiento.repositorys.GarageRepository;
import com.estacionamiento.repositorys.MarcaRepository;
import com.estacionamiento.repositorys.TipoVehiculoRepository;
import com.estacionamiento.repositorys.UsuarioRepository;
import com.estacionamiento.repositorys.ZonaRepository;
import com.estacionamiento.services.UsuarioService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Datos iniciales. Se ejecuta en cada arranque, pero solo crea lo que falta:
 * el primer administrador (si no hay ninguno), los tipos de vehiculo y las marcas (si no hay ninguno)
 * y las zonas con sus garages (si no hay ninguna zona).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    /** Una zona por defecto y cuantos garages tiene. */
    private record ZonaInicial(String letra, String tipoVehiculos, int profundidad, int ancho, int planta, int garages,
                               double tarifaHora) {
    }

    /**
     * Cada planta tiene una zona de autos (20 garages, $1500/h), una de motos (40, $800/h) y una de camionetas (10, $2000/h).
     * Plantas: 0 = planta baja, 1 = piso 1. Los garages se numeran por planta: 1..70 en planta baja, 101..170 en el piso 1.
     */
    private static final List<ZonaInicial> ZONAS = List.of(
            new ZonaInicial("A", "Autos", 5, 3, 0, 20, 1500),
            new ZonaInicial("B", "Motos", 3, 2, 0, 40, 800),
            new ZonaInicial("C", "Camionetas", 6, 4, 0, 10, 2000),
            new ZonaInicial("D", "Autos", 5, 3, 1, 20, 1500),
            new ZonaInicial("E", "Motos", 3, 2, 1, 40, 800),
            new ZonaInicial("F", "Camionetas", 6, 4, 1, 10, 2000));

    /** Marcas de autos: se usan para los tipos Auto y Camioneta. Tienen que coincidir con migracion-marcas.sql. */
    private static final List<String> MARCAS_AUTO = List.of("Audi", "BMW", "Chery", "Chevrolet", "Citroën", "Fiat", "Ford", "Honda",
            "Hyundai", "Jeep", "Kia", "Mercedes-Benz", "Nissan", "Peugeot", "RAM", "Renault", "Suzuki", "Toyota", "Volkswagen");

    /** Marcas de motos. Honda, BMW y Suzuki estan en las dos listas: quedan asociadas a los tres tipos. */
    private static final List<String> MARCAS_MOTO = List.of("Bajaj", "Benelli", "BMW", "Corven", "Gilera", "Honda", "Kawasaki",
            "Keller", "KTM", "Motomel", "Royal Enfield", "Suzuki", "Yamaha", "Zanella");

    private final UsuarioRepository usuarioRepository;
    private final ZonaRepository zonaRepository;
    private final GarageRepository garageRepository;
    private final TipoVehiculoRepository tipoVehiculoRepository;
    private final MarcaRepository marcaRepository;
    private final UsuarioService usuarioService;

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.password:}")
    private String adminPassword;

    @Value("${app.admin.mail:}")
    private String adminMail;

    /** El administrador inicial tiene que cambiar la clave de application.properties en su primer login. */
    @Value("${app.admin.debe-cambiar-clave:true}")
    private boolean adminDebeCambiarClave;

    @Override
    public void run(String... args) {
        crearAdministradorInicial();
        crearTiposDeVehiculo();
        crearMarcas();
        crearZonasYGaragesPorDefecto();
    }

    private void crearAdministradorInicial() {
        if (usuarioRepository.existsByTipoUsuario(TipoUsuario.ADMINISTRADOR)) {
            return;
        }
        usuarioRepository.save(
                usuarioService.usuarioConClave(adminUsername, adminPassword, TipoUsuario.ADMINISTRADOR, adminMail, adminDebeCambiarClave));
        log.warn("Se creó el administrador inicial '{}' con la clave de app.admin.password: la tiene que cambiar al ingresar.", adminUsername);
    }

    private void crearTiposDeVehiculo() {
        if (tipoVehiculoRepository.count() > 0) {
            return;
        }
        tipoVehiculoRepository.saveAll(List.of(new TipoVehiculo("Auto"), new TipoVehiculo("Moto"), new TipoVehiculo("Camioneta")));
        log.info("No había tipos de vehículo: se crearon Auto, Moto y Camioneta.");
    }

    private void crearMarcas() {
        if (marcaRepository.count() > 0) {
            return;
        }
        List<TipoVehiculo> tipos = tipoVehiculoRepository.findAll();
        Set<String> nombres = new TreeSet<>(MARCAS_AUTO);
        nombres.addAll(MARCAS_MOTO);
        List<Marca> marcas = new ArrayList<>();
        for (String nombre : nombres) {
            Set<TipoVehiculo> deLaMarca = new HashSet<>();
            for (TipoVehiculo t : tipos) {
                boolean esMoto = t.getTipo().equals("Moto");
                if (esMoto ? MARCAS_MOTO.contains(nombre) : MARCAS_AUTO.contains(nombre)) {
                    deLaMarca.add(t);
                }
            }
            marcas.add(new Marca(nombre, deLaMarca));
        }
        marcaRepository.saveAll(marcas);
        log.info("No había marcas: se crearon {} marcas de autos, camionetas y motos.", marcas.size());
    }

    private void crearZonasYGaragesPorDefecto() {
        if (zonaRepository.count() > 0 || garageRepository.count() > 0) {
            return;
        }
        List<Garage> garages = new ArrayList<>();
        Integer plantaActual = null;
        int numero = 0;
        for (ZonaInicial z : ZONAS) {
            if (plantaActual == null || z.planta() != plantaActual) {
                plantaActual = z.planta();
                numero = plantaActual * 100 + 1;
            }
            Zona zona = zonaRepository.save(new Zona(z.letra(), z.tipoVehiculos(), z.profundidad(), z.ancho(), z.planta(), z.tarifaHora()));
            for (int i = 0; i < z.garages(); i++) {
                garages.add(new Garage(numero++, zona));
            }
        }
        garageRepository.saveAll(garages);
        log.info("No había zonas: se crearon {} zonas en 2 plantas (planta baja y piso 1) y {} garages.", ZONAS.size(), garages.size());
    }
}
