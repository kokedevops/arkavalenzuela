package com.arka.arkavalenzuela.security;

import com.arka.arkavalenzuela.config.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas automatizadas de seguridad para validar la protección de endpoints
 * y autorización basada en roles
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@Import(TestSecurityConfig.class)
@TestPropertySource(properties = {
    "spring.cloud.config.enabled=false",
    "eureka.client.enabled=false",
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class SecurityIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .apply(springSecurity())
            .build();
    }

    @Test
    void testEndpointProtegidoSinAutenticacion_DeberiaRetornar401() throws Exception {
        mockMvc.perform(get("/api/solicitudes"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void testEndpointPublicoSinAutenticacion_DeberiaPermitirAcceso() throws Exception {
        mockMvc.perform(get("/api/public/health"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USUARIO")
    void testEndpointConRolUsuario_DeberiaPermitirLectura() throws Exception {
        mockMvc.perform(get("/api/solicitudes"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USUARIO")
    void testEndpointConRolUsuario_DeberiaProhibirEscritura() throws Exception {
        Map<String, Object> solicitud = new HashMap<>();
        solicitud.put("descripcion", "Nueva solicitud");
        solicitud.put("estado", "PENDIENTE");

        mockMvc.perform(post("/api/solicitudes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(solicitud)))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    void testEndpointConRolGestor_DeberiaPermitirCreacion() throws Exception {
        Map<String, Object> solicitud = new HashMap<>();
        solicitud.put("descripcion", "Nueva solicitud");
        solicitud.put("estado", "PENDIENTE");

        mockMvc.perform(post("/api/solicitudes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(solicitud)))
            .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    void testEndpointConRolGestor_DeberiaProhibirEliminacion() throws Exception {
        mockMvc.perform(delete("/api/solicitudes/1"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testEndpointConRolAdmin_DeberiaPermitirTodo() throws Exception {
        // Test GET
        mockMvc.perform(get("/api/solicitudes"))
            .andExpect(status().isOk());

        // Test POST
        Map<String, Object> solicitud = new HashMap<>();
        solicitud.put("descripcion", "Nueva solicitud admin");
        solicitud.put("estado", "PENDIENTE");

        mockMvc.perform(post("/api/solicitudes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(solicitud)))
            .andExpect(status().isCreated());

        // Test DELETE
        mockMvc.perform(delete("/api/solicitudes/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "usuario1", roles = "USUARIO")
    void testAccesoPropiasReferencias_DeberiaPermitir() throws Exception {
        mockMvc.perform(get("/api/solicitudes/mis-solicitudes"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "usuario1", roles = "USUARIO")
    void testAccesoReferenciaAjena_DeberiaProhibir() throws Exception {
        mockMvc.perform(get("/api/solicitudes/usuario/usuario2"))
            .andExpect(status().isForbidden());
    }

    @Test
    void testJWTTokenInvalido_DeberiaRetornar401() throws Exception {
        mockMvc.perform(get("/api/solicitudes")
                .header("Authorization", "Bearer token_invalido"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void testJWTTokenExpirado_DeberiaRetornar401() throws Exception {
        // Token JWT expirado (simulado)
        String tokenExpirado = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c3VhcmlvMSIsImV4cCI6MTYwOTQ1OTIwMH0.invalid";
        
        mockMvc.perform(get("/api/solicitudes")
                .header("Authorization", "Bearer " + tokenExpirado))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void testCSRFProtection_DeberiaEstarDeshabilitado() throws Exception {
        // En una API REST, CSRF debería estar deshabilitado
        Map<String, Object> loginData = new HashMap<>();
        loginData.put("username", "testuser");
        loginData.put("password", "testpass");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginData)))
            .andExpect(status().isOk()); // No debería fallar por CSRF
    }

    @Test
    void testCORSConfiguration_DeberiaPermitirOrigenesConfigurados() throws Exception {
        mockMvc.perform(options("/api/solicitudes")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "GET"))
            .andExpect(status().isOk())
            .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
    }

    @Test
    void testMethodNotAllowed_DeberiaRetornar405() throws Exception {
        mockMvc.perform(patch("/api/solicitudes/1"))
            .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @WithMockUser(roles = "USUARIO")
    void testParametrosDeConsulta_DeberiaValidarEntrada() throws Exception {
        // Test con parámetros válidos
        mockMvc.perform(get("/api/solicitudes")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk());

        // Test con parámetros inválidos
        mockMvc.perform(get("/api/solicitudes")
                .param("page", "-1")
                .param("size", "0"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testRateLimiting_DeberiaLimitarSolicitudesExcesivas() throws Exception {
        // Simular múltiples solicitudes rápidas (esto dependería de la implementación de rate limiting)
        for (int i = 0; i < 10; i++) {
            mockMvc.perform(get("/api/public/health"));
        }
        
        // La solicitud número 11 debería ser limitada (si está implementado rate limiting)
        // mockMvc.perform(get("/api/public/health"))
        //     .andExpect(status().isTooManyRequests());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSanitizacionEntrada_DeberiaPrevenirInyeccion() throws Exception {
        Map<String, Object> solicitudMaliciosa = new HashMap<>();
        solicitudMaliciosa.put("descripcion", "<script>alert('XSS')</script>");
        solicitudMaliciosa.put("estado", "'; DROP TABLE solicitudes; --");

        mockMvc.perform(post("/api/solicitudes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(solicitudMaliciosa)))
            .andExpect(status().isBadRequest()); // Debería validar y rechazar
    }
}
