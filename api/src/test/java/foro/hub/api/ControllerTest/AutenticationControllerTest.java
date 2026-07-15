package foro.hub.api.ControllerTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import foro.hub.api.controller.AutenticacionController;
import foro.hub.api.domain.usuarios.UsuarioRepository;
import foro.hub.api.infra.errores.AuthenticationFailedException;
import foro.hub.api.infra.errores.TratadorDeErrores;
import foro.hub.api.infra.security.AuthLoginService;
import foro.hub.api.infra.security.DTOJWTToken;
import foro.hub.api.infra.security.SecurityConfigurations;
import foro.hub.api.infra.security.TokenService;

@WebMvcTest(AutenticacionController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(TratadorDeErrores.class)
public class AutenticationControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AuthLoginService authLoginService;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private SecurityConfigurations securityConfiguration;

    @Test
    @DisplayName("Deberia retornar un estado 200 OK cuando las credenciales son validas.")
    void testAutenticarUsuarioEscenarioExitoso() throws Exception{
        var dtoEsperado = new DTOJWTToken("token_de_prueba");
        when(authLoginService.autenticarUsuario(any())).thenReturn(dtoEsperado);

        var json = "{\"login\":\"usuario\", \"clave\":\"123456\"}";
        var response = mvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());
        assertTrue(response.getContentAsString().contains("token_de_prueba"));
    }

    @Test
    @DisplayName("Deberia retornar un error 400 cuando el token es invalido")
    void testAutenticarUsuarioCasoFallido() throws Exception{
        
        var json = "{}";

        var response = mvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andReturn().getResponse();
        
        assertEquals(400, response.getStatus());

    }

    @Test
    @DisplayName("Debería retornar 403 cuando falla la autenticacion")
    void testAutenticarUsuarioCasoCredencialesInvalidas() throws Exception {
        when(authLoginService.autenticarUsuario(any())).thenThrow(new AuthenticationFailedException("Error"));

        var json = "{\"login\":\"usuario\", \"clave\":\"123456\"}";
        
        mvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isForbidden()); 
    }

    @Test
    @DisplayName("Debería retornar 400 cuando el JSON está mal formado")
    void testAutenticarUsuarioCasoJsonInvalido() throws Exception {

        var json = "{\"login\": \"usuario\", "; 

        mvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deberia retornar un error 500 por un error tecnico")
    void testAutenticarUsuarioCasoError500() throws Exception{
        when(authLoginService.autenticarUsuario(any())).thenThrow(new RuntimeException("Error desconocido"));

        var json = "{\"login\":\"usuario\", \"clave\":\"123456\"}";

        mvc.perform(post("/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json))
        .andExpect(status().isInternalServerError());
    }
}
