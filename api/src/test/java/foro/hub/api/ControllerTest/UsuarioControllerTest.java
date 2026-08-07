package foro.hub.api.ControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;

import foro.hub.api.controller.UsuarioController;
import foro.hub.api.domain.perfil.Perfil;
import foro.hub.api.domain.usuarios.DTORegistroUsuario;
import foro.hub.api.domain.usuarios.DTOResponseUsuario;
import foro.hub.api.domain.usuarios.UsuarioRepository;
import foro.hub.api.domain.usuarios.UsuarioService;
import foro.hub.api.infra.security.SecurityConfigurations;
import foro.hub.api.infra.security.SecurityFilter;
import foro.hub.api.infra.security.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = UsuarioController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {SecurityConfigurations.class, SecurityFilter.class}
    )
)
@AutoConfigureMockMvc(addFilters = false)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("Debe registrar usuario correctamente y retornar codigo 201 con header Location")
    void registrarUsuarioExito() throws Exception {
        DTORegistroUsuario dtoRegistro = new DTORegistroUsuario("usuarioTest", "Clave1234", "test@email.com", "Usuario123");
        DTOResponseUsuario dtoResponse = new DTOResponseUsuario(1L, "usuarioTest", "test@email.com", new Perfil("Usuario123"));

        when(usuarioService.registrarUsuario(any(DTORegistroUsuario.class)))
                .thenReturn(dtoResponse);

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoRegistro)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/usuarios/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.login").value("usuarioTest"))
                .andExpect(jsonPath("$.email").value("test@email.com"));

        verify(usuarioService).registrarUsuario(any(DTORegistroUsuario.class));
    }

    @Test
    @DisplayName("Debe retornar codigo 400 cuando los datos de registro son invalidos")
    void registrarUsuarioBadRequest() throws Exception {
        DTORegistroUsuario dtoInvalido = new DTORegistroUsuario("", "", "emailInvalido", "");

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());
    }
}