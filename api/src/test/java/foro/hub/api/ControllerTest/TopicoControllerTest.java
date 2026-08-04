package foro.hub.api.ControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;

import foro.hub.api.controller.TopicoController;
import foro.hub.api.domain.curso.Curso;
import foro.hub.api.domain.curso.DTOCurso;
import foro.hub.api.domain.topico.*;
import foro.hub.api.domain.usuarios.DTOInfoUsuario;
import foro.hub.api.domain.usuarios.Usuario;
import foro.hub.api.infra.errores.AuthorizationException;
import foro.hub.api.infra.errores.TratadorDeErrores;
import foro.hub.api.infra.security.SecurityConfigurations;
import foro.hub.api.infra.security.SecurityFilter;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = TopicoController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {SecurityConfigurations.class, SecurityFilter.class}
    ),
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class
    }
)
@AutoConfigureMockMvc(addFilters = false)
@Import(TratadorDeErrores.class)
class TopicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; 

    @MockBean
    private TopicoService topicoService; 

    private Usuario usuarioPrueba;
    private DTOResponseTopic dtoResponseTopic;

    @BeforeEach
    void setUp() {
        usuarioPrueba = new Usuario();
        usuarioPrueba.setId(1L);
        usuarioPrueba.setLogin("usuarioTest");

        dtoResponseTopic = new DTOResponseTopic(
                10L,
                "Titulo Valido",
                "Mensaje de prueba extenso",
                new Date(),
                TopicStatus.OPEN,
                new DTOInfoUsuario(1L, "Alumno"),
                new Curso(),
                0
        );
    }


    @Test
    @DisplayName("Deberia retornar HTTP 201 Created y header Location al registrar un topico valido")
    void testRegistrarTopicoExitoso() throws Exception {
        var dtoCurso = new DTOCurso("Java", "Backend");
        var dtoRegistro = new DTORegistroTopico("Titulo Valido", "Mensaje de prueba extenso", TopicStatus.OPEN, dtoCurso);

        when(topicoService.registrarTopico(any(DTORegistroTopico.class), any())).thenReturn(dtoResponseTopic);

        mockMvc.perform(post("/topicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoRegistro)) 
                        .with(user(usuarioPrueba)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/topicos/10"))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.titulo").value("Titulo Valido"));
    }

    @Test
    @DisplayName("Debería retornar HTTP 400 Bad Request cuando el DTO de registro no cumple las validaciones")
    void testRegistrarTopicoInvalido() throws Exception {
        var dtoRegistroInvalido = new DTORegistroTopico("", "Corto", null, null);

        mockMvc.perform(post("/topicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoRegistroInvalido))
                        .with(user(usuarioPrueba)))
                .andExpect(status().isBadRequest()); 
    }

    @Test
    @DisplayName("Deberia retornar HTTP 200 OK con la pagina de topicos")
    void testListadoTopicosExitoso() throws Exception {
        when(topicoService.listar(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/topicos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("Debería retornar HTTP 200 OK al actualizar un tópico con datos válidos")
    void testActualizarTopicoExitoso() throws Exception {
        var dtoActualizar = new DTOActualizarTopico(10L, "Título Nuevo", "Mensaje Editado Largo", TopicStatus.OPEN);

        when(topicoService.actualizarTopico(any(DTOActualizarTopico.class), any())).thenReturn(dtoResponseTopic);

        mockMvc.perform(put("/topicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoActualizar))
                        .with(user(usuarioPrueba)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    @DisplayName("Deberia retornar HTTP 204 No Content al eliminar un topico exitosamente")
    void testEliminarTopicoExitoso() throws Exception {
        mockMvc.perform(delete("/topicos/10")
                        .with(user(usuarioPrueba)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deberia retornar HTTP 403 Forbidden cuando el servicio lanza AuthorizationException")
    void testEliminarTopicoSinPermisos() throws Exception {
        doThrow(new AuthorizationException("No estás autorizado"))
                .when(topicoService).eliminarTopico(eq(10L), any());

        mockMvc.perform(delete("/topicos/10")
                        .with(user(usuarioPrueba)))
                .andExpect(status().isForbidden()); 
    }

    @Test
    @DisplayName("Deberia retornar HTTP 404 Not Found cuando el servicio lanza EntityNotFoundException")
    void testObtenerTopicoInexistente() throws Exception {
        when(topicoService.retonarDatosTopico(eq(99L), any(Pageable.class)))
                .thenThrow(new EntityNotFoundException("Tópico no encontrado"));

        mockMvc.perform(get("/topicos/99"))
                .andExpect(status().isNotFound()); 
    }
}