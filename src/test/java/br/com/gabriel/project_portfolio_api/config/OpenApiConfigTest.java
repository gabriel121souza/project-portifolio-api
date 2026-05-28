package br.com.gabriel.project_portfolio_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpenApiConfigTest {

    @Test
    void shouldCreateCustomOpenApiConfiguration() {
        OpenAPI openAPI = new OpenApiConfig().customOpenAPI();

        assertEquals("Project Portfolio API", openAPI.getInfo().getTitle());
        assertEquals("1.0.0", openAPI.getInfo().getVersion());
        assertEquals("Gabriel de Souza Rodrigues", openAPI.getInfo().getContact().getName());

        SecurityScheme securityScheme = openAPI.getComponents().getSecuritySchemes().get("basicAuth");
        assertNotNull(securityScheme);
        assertEquals(SecurityScheme.Type.HTTP, securityScheme.getType());
        assertEquals("basic", securityScheme.getScheme());
        assertTrue(openAPI.getSecurity().get(0).containsKey("basicAuth"));
    }
}
