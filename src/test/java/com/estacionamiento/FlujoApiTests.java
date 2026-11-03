package com.estacionamiento;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.estacionamiento.models.Usuario;
import com.estacionamiento.services.MailService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootTest
@AutoConfigureMockMvc
class FlujoApiTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /** En los tests no se mandan mails: se guarda la ultima clave temporal enviada a cada usuario. */
    static class MailDePrueba extends MailService {
        private final Map<String, String> claves = new ConcurrentHashMap<>();

        MailDePrueba() {
            super(null, "test@localhost");
        }

        @Override
        public void enviarClaveTemporal(Usuario usuario, String clave) {
            claves.put(usuario.getUsername(), clave);
        }
    }

    @TestConfiguration
    static class ConfigMail {
        @Bean
        @Primary
        MailDePrueba mailDePrueba() {
            return new MailDePrueba();
        }
    }

    @Autowired
    private MailDePrueba mail;

    private String claveEnviadaA(String username) {
        return mail.claves.get(username);
    }

    private String login(String username, String password) throws Exception {
        String cuerpo = mockMvc.perform(post("/api/auth/autenticar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(cuerpo).get("token").asText();
    }

    @Test
    void sinTokenSeResponde401() throws Exception {
        mockMvc.perform(get("/api/admin/socios/listar"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginConClaveIncorrectaResponde401() throws Exception {
        mockMvc.perform(post("/api/auth/autenticar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"incorrecta\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminRegistraSocioYElSocioSoloAccedeASusEndpoints() throws Exception {
        String tokenAdmin = login("admin", "Admin1234");

        mockMvc.perform(post("/api/admin/socios/registrar")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Ana Perez\",\"dni\":30111222,\"direccion\":\"Calle 1\","
                                + "\"telefono\":\"1155550000\",\"username\":\"ana\",\"mail\":\"ana@example.com\"}"))
                .andExpect(status().isCreated());

        // el mismo DNI dos veces es un conflicto
        mockMvc.perform(post("/api/admin/socios/registrar")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Otra\",\"dni\":30111222,\"username\":\"otra\",\"mail\":\"otra@example.com\"}"))
                .andExpect(status().isConflict());

        // le llego una clave temporal por mail: con ella solo puede cambiar la contrasena
        String tokenTemporal = login("ana", claveEnviadaA("ana"));
        mockMvc.perform(get("/api/socio/listarVehiculos")
                        .header("Authorization", "Bearer " + tokenTemporal))
                .andExpect(status().isForbidden());
        enviar(tokenTemporal, "/api/cuenta/cambiarClave", "{\"actual\":\"incorrecta\",\"nueva\":\"clave123\"}", 400);
        enviar(tokenTemporal, "/api/cuenta/cambiarClave",
                "{\"actual\":\"" + claveEnviadaA("ana") + "\",\"nueva\":\"clave123\"}", 204);

        String tokenSocio = login("ana", "clave123");

        mockMvc.perform(get("/api/socio/listarVehiculos")
                        .header("Authorization", "Bearer " + tokenSocio))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/admin/socios/listar")
                        .header("Authorization", "Bearer " + tokenSocio))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/admin/socios/listar")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk());
    }

    private void enviar(String token, String url, String json, int estado) throws Exception {
        var pedido = post(url).header("Authorization", "Bearer " + token);
        if (json != null) {
            pedido.contentType(MediaType.APPLICATION_JSON).content(json);
        }
        mockMvc.perform(pedido).andExpect(status().is(estado));
    }

    /** Busca el id de una marca entre las del tipo indicado (1 = Auto, 2 = Moto en los datos iniciales). */
    private long idMarca(String token, long idTipo, String nombre) throws Exception {
        String json = mockMvc.perform(get("/api/admin/marcas/listar").param("idTipo", String.valueOf(idTipo))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        for (JsonNode m : objectMapper.readTree(json)) {
            if (m.get("marca").asText().equals(nombre)) {
                return m.get("idMarca").asLong();
            }
        }
        throw new AssertionError("No esta la marca " + nombre + " para el tipo " + idTipo);
    }

    @Test
    void unAdministradorCreaOtroQueDebeCambiarLaClave() throws Exception {
        String t = login("admin", "Admin1234");
        enviar(t, "/api/admin/administradores/registrar", "{\"username\":\"admin2\",\"mail\":\"sin-arroba\"}", 400);
        enviar(t, "/api/admin/administradores/registrar", "{\"username\":\"admin2\",\"mail\":\"admin2@example.com\"}", 201);

        String temporal = login("admin2", claveEnviadaA("admin2"));
        mockMvc.perform(get("/api/admin/socios/listar").header("Authorization", "Bearer " + temporal))
                .andExpect(status().isForbidden());
        enviar(temporal, "/api/cuenta/cambiarClave", "{\"actual\":\"" + claveEnviadaA("admin2") + "\",\"nueva\":\"Nueva1234\"}", 204);
        String t2 = login("admin2", "Nueva1234");
        mockMvc.perform(get("/api/admin/socios/listar").header("Authorization", "Bearer " + t2))
                .andExpect(status().isOk());

        // restablecer la clave vuelve a pedir el cambio
        enviar(t, "/api/admin/usuarios/restablecerClave/admin2", null, 204);
        mockMvc.perform(get("/api/admin/socios/listar").header("Authorization", "Bearer " + t2))
                .andExpect(status().isForbidden());

        // nadie se elimina a si mismo; a otro administrador si
        mockMvc.perform(delete("/api/admin/administradores/eliminar/admin").header("Authorization", "Bearer " + t))
                .andExpect(status().isConflict());
        mockMvc.perform(delete("/api/admin/administradores/eliminar/admin2").header("Authorization", "Bearer " + t))
                .andExpect(status().isNoContent());
    }

    @Test
    void laMarcaTieneQueCorresponderAlTipo() throws Exception {
        String t = login("admin", "Admin1234");
        long yamaha = idMarca(t, 2, "Yamaha");
        // Yamaha es de motos: como auto se rechaza, como moto se acepta
        enviar(t, "/api/admin/vehiculos/registrar",
                "{\"patente\":\"MOT001\",\"idMarca\":" + yamaha + ",\"idTipo\":1,\"dimensiones\":\"2x1\"}", 400);
        enviar(t, "/api/admin/vehiculos/registrar",
                "{\"patente\":\"MOT001\",\"idMarca\":" + yamaha + ",\"idTipo\":2,\"dimensiones\":\"2x1\"}", 201);
        // Honda esta en las dos listas
        idMarca(t, 1, "Honda");
        idMarca(t, 2, "Honda");
    }

    @Test
    void vehiculosEntranYSalenPagandoPorHora() throws Exception {
        String t = login("admin", "Admin1234");
        // vehiculo de paso, sin socio
        enviar(t, "/api/admin/vehiculos/registrar",
                "{\"patente\":\"AAA111\",\"idMarca\":" + idMarca(t, 1, "Fiat") + ",\"idTipo\":1,\"dimensiones\":\"4x2\"}", 201);
        // vehiculo de un socio
        enviar(t, "/api/admin/socios/registrar",
                "{\"nombre\":\"Beto\",\"dni\":30222333,\"username\":\"beto\",\"mail\":\"beto@example.com\"}", 201);
        enviar(t, "/api/admin/vehiculos/registrar",
                "{\"patente\":\"BBB222\",\"idMarca\":" + idMarca(t, 1, "Ford") + ",\"idTipo\":1,\"dimensiones\":\"4x2\",\"dniPropietario\":30222333}", 201);

        enviar(t, "/api/admin/ocupantes/entrada/101/AAA111", null, 201);
        enviar(t, "/api/admin/ocupantes/entrada/101/BBB222", null, 409);   // garage ocupado
        enviar(t, "/api/admin/ocupantes/entrada/102/AAA111", null, 409);   // ya esta adentro
        enviar(t, "/api/admin/ocupantes/entrada/102/BBB222", null, 201);

        // zona A: $1500 la hora; el socio tiene 20% de descuento
        mockMvc.perform(post("/api/admin/ocupantes/salida/101").header("Authorization", "Bearer " + t))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.horas").value(1))
                .andExpect(jsonPath("$.importe").value(1500.0));
        mockMvc.perform(post("/api/admin/ocupantes/salida/102").header("Authorization", "Bearer " + t))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.importe").value(1200.0));
        enviar(t, "/api/admin/ocupantes/salida/102", null, 409);           // ya salio

        // garage con abono: solo los vehiculos del socio, y no pagan
        mockMvc.perform(put("/api/admin/garages/asignarSocio/103/30222333").header("Authorization", "Bearer " + t))
                .andExpect(status().isOk());
        enviar(t, "/api/admin/ocupantes/entrada/103/AAA111", null, 409);
        enviar(t, "/api/admin/ocupantes/entrada/103/BBB222", null, 201);
        mockMvc.perform(post("/api/admin/ocupantes/salida/103").header("Authorization", "Bearer " + t))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.importe").value(0.0));

        // un vehiculo con estadias no se puede eliminar
        mockMvc.perform(delete("/api/admin/vehiculos/eliminar/AAA111").header("Authorization", "Bearer " + t))
                .andExpect(status().isConflict());
    }
}
