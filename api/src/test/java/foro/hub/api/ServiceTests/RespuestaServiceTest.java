package foro.hub.api.ServiceTests;

import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import foro.hub.api.domain.respuestas.DTOActualizarRespuesta;
import foro.hub.api.domain.respuestas.DTORegistroRespuesta;
import foro.hub.api.domain.respuestas.DTOResponseRespuesta;
import foro.hub.api.domain.respuestas.Respuesta;
import foro.hub.api.domain.respuestas.RespuestaRepository;
import foro.hub.api.domain.respuestas.RespuestaService;
import foro.hub.api.domain.respuestas.validaciones.ValidadorDeRespuestas;
import foro.hub.api.domain.topico.Topico;
import foro.hub.api.domain.topico.TopicoRepository;
import foro.hub.api.domain.usuarios.Usuario;
import foro.hub.api.infra.errores.AuthorizationException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RespuestaServiceTest {

    @Mock
    private TopicoRepository topicoRepository;

    @Mock
    private RespuestaRepository respuestaRepository;

    @Spy
    private List<ValidadorDeRespuestas> validadores = new ArrayList<>();

    @Mock
    private ValidadorDeRespuestas validador;

    @InjectMocks
    private RespuestaService respuestaService;

    private Usuario usuarioAutor;
    private Usuario usuarioAjeno;
    private Topico topico;

    @BeforeEach
    void setUp() {
        usuarioAutor = new Usuario();
        usuarioAutor.setId(1L);

        usuarioAjeno = new Usuario();
        usuarioAjeno.setId(2L);

        topico = new Topico();
        topico.setId(10L);
    }


    @Test
    @DisplayName("Debe registrar respuesta correctamente e incrementar contador del topico")
    void registrarRespuestaExito() {
        DTORegistroRespuesta dto = new DTORegistroRespuesta("Mensaje de prueba", 10L);
        validadores.add(validador);

        when(topicoRepository.findById(10L)).thenReturn(Optional.of(topico));

        DTOResponseRespuesta response = respuestaService.registrarRespuesta(dto, usuarioAutor);

        assertNotNull(response);
        assertEquals("Mensaje de prueba", response.mensaje());
        assertEquals(10L, response.topicoID());
        assertEquals(1L, response.autorRespuestaID());

        verify(validador).validar(dto);
        verify(respuestaRepository).save(any(Respuesta.class));
        verify(topicoRepository).incrementarNumeroDeRespuestas(10L);
    }

    @Test
    @DisplayName("Debe lanzar EntityNotFoundException si el topico no existe al registrar")
    void registrarRespuestaTopicoNoEncontrado() {
        DTORegistroRespuesta dto = new DTORegistroRespuesta("Mensaje de prueba", 99L);
        when(topicoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> respuestaService.registrarRespuesta(dto, usuarioAutor));

        verify(respuestaRepository, never()).save(any());
        verify(topicoRepository, never()).incrementarNumeroDeRespuestas(anyLong());
    }

    @Test
    @DisplayName("Debe actualizar respuesta cuando el usuario es el autor")
    void actualizarRespuestaExito() {
        DTOActualizarRespuesta dto = new DTOActualizarRespuesta(5L, "Mensaje editado");
        validadores.add(validador);

        Respuesta respuestaExistente = crearRespuestaExistente(5L, "Mensaje original", usuarioAutor, topico);
        when(respuestaRepository.findById(5L)).thenReturn(Optional.of(respuestaExistente));

        DTOResponseRespuesta response = respuestaService.actualizarRespuesta(dto, usuarioAutor);

        assertNotNull(response);
        assertEquals("Mensaje editado", response.mensaje());

        verify(validador).validar(dto);
    }

    @Test
    @DisplayName("Debe lanzar AuthorizationException si un usuario ajeno intenta actualizar")
    void actualizarRespuestaSinAutorizacion() {
        DTOActualizarRespuesta dto = new DTOActualizarRespuesta(5L, "Intento de edicion");
        Respuesta respuestaExistente = crearRespuestaExistente(5L, "Mensaje original", usuarioAutor, topico);

        when(respuestaRepository.findById(5L)).thenReturn(Optional.of(respuestaExistente));

        AuthorizationException exception = assertThrows(
                AuthorizationException.class,
                () -> respuestaService.actualizarRespuesta(dto, usuarioAjeno)
        );

        assertEquals("No estas autorizado a realizar esta accion", exception.getMessage());
        verify(validador, never()).validar(any(DTOActualizarRespuesta.class));
    }


@Test
@DisplayName("Debe eliminar la respuesta y decrementar el contador si el usuario es el autor")
void eliminarRespuestaExito() {
    Long respuestaId = 5L;
    Respuesta respuestaExistente = crearRespuestaExistente(respuestaId, "Mensaje a borrar", usuarioAutor, topico);

    when(respuestaRepository.findById(respuestaId)).thenReturn(Optional.of(respuestaExistente));

    respuestaService.eliminarRespuesta(respuestaId, usuarioAutor);

    verify(respuestaRepository).deleteById(respuestaId);
    verify(topicoRepository).decrementarNumeroDeRespuestas(10L);
}

@Test
@DisplayName("Debe lanzar AuthorizationException si un usuario ajeno intenta eliminar")
void eliminarRespuestaSinAutorizacion() {
    Long respuestaId = 5L;
    Respuesta respuestaExistente = crearRespuestaExistente(respuestaId, "Mensaje a borrar", usuarioAutor, topico);

    when(respuestaRepository.findById(respuestaId)).thenReturn(Optional.of(respuestaExistente));

    assertThrows(AuthorizationException.class, () -> respuestaService.eliminarRespuesta(respuestaId, usuarioAjeno));

    verify(respuestaRepository, never()).deleteById(anyLong());
    verify(topicoRepository, never()).decrementarNumeroDeRespuestas(anyLong());
}

    private Respuesta crearRespuestaExistente(Long id, String mensaje, Usuario autor, Topico topico) {
        Respuesta r = new Respuesta(mensaje);
        r.setId(id);
        r.setAutor(autor);
        r.setTopico(topico);
        r.setFechaCreacion(new Date());
        r.setSolucion(false);
        return r;
    }
}