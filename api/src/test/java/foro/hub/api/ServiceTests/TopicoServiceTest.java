package foro.hub.api.ServiceTests;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import foro.hub.api.domain.curso.Curso;
import foro.hub.api.domain.curso.DTOCurso;
import foro.hub.api.domain.perfil.Perfil;
import foro.hub.api.domain.respuestas.RespuestaRepository;
import foro.hub.api.domain.topico.DTOActualizarTopico;
import foro.hub.api.domain.topico.DTORegistroTopico;
import foro.hub.api.domain.topico.TopicStatus;
import foro.hub.api.domain.topico.Topico;
import foro.hub.api.domain.topico.TopicoRepository;
import foro.hub.api.domain.topico.TopicoService;
import foro.hub.api.domain.topico.validaciones.ValidadorDeDuplicados;
import foro.hub.api.domain.usuarios.Usuario;
import foro.hub.api.infra.errores.AuthorizationException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;

@ExtendWith(MockitoExtension.class)
public class TopicoServiceTest {
    @Mock
    private TopicoRepository topicoRepository;

    @Mock
    private RespuestaRepository respuestaRepository;

    @Mock
    private ValidadorDeDuplicados validadorDeDuplicados;

    private TopicoService topicoService;

    private Usuario usuarioAutor;
    private Usuario usuarioAjeno;
    private Topico topicoPrueba;
    private DTOCurso dtoCurso;

    @BeforeEach
    void setUp() {
        topicoService = new TopicoService(
                topicoRepository,
                List.of(validadorDeDuplicados),
                respuestaRepository);

       
        Perfil perfil = new Perfil();
        perfil.setNombre("Alumno");

        usuarioAutor = new Usuario();
        usuarioAutor.setId(1L);
        usuarioAutor.setLogin("autorPrueba");
        usuarioAutor.setPerfil(perfil);

        usuarioAjeno = new Usuario();
        usuarioAjeno.setId(2L);
        usuarioAjeno.setLogin("usuarioAjeno");
        usuarioAjeno.setPerfil(perfil);

        dtoCurso = new DTOCurso("Java Object Oriented", "Programacion");
        Curso curso = new Curso(dtoCurso.nombre(), dtoCurso.categoria());

        topicoPrueba = new Topico();
        topicoPrueba.setId(10L);
        topicoPrueba.setTitulo("Titulo Original");
        topicoPrueba.setMensaje("Mensaje de prueba con más de 10 caracteres");
        topicoPrueba.setFechaCreacion(new Date());
        topicoPrueba.setStatus(TopicStatus.OPEN);
        topicoPrueba.setAutor(usuarioAutor);
        topicoPrueba.setCurso(curso);
        topicoPrueba.setNumRespuestas(0);
    }

    @Test
    @DisplayName("Deberia registrar un topico exitosamente cuando las validaciones pasan")
    void testRegistrarTopicoExitoso() {
        var datosRegistro = new DTORegistroTopico(
                "Titulo Nuevo",
                "Mensaje extenso para cumplir validacion",
                TopicStatus.OPEN,
                dtoCurso
        );

        var response = topicoService.registrarTopico(datosRegistro, usuarioAutor);

        assertNotNull(response);
        assertEquals("Titulo Nuevo", response.titulo());
    
        verify(validadorDeDuplicados, times(1)).validar(datosRegistro);
        verify(topicoRepository, times(1)).save(any(Topico.class));
    }

    @Test
    @DisplayName("No deberia registrar el topico si el validador lanza ValidationException por duplicado")
    void testRegistrarTopicoDuplicadoFallido() {
        var datosRegistro = new DTORegistroTopico(
                "Titulo Duplicado",
                "Mensaje duplicado existente",
                TopicStatus.OPEN,
                dtoCurso
        );

        doThrow(new ValidationException("No se puede duplicar tópicos ya existentes."))
                .when(validadorDeDuplicados).validar(datosRegistro);

        assertThrows(ValidationException.class, () ->
                topicoService.registrarTopico(datosRegistro, usuarioAutor)
        );

        verify(topicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deberia actualizar el topico cuando el usuario autenticado es el autor")
    void testActualizarTopicoExitoso() {
        var datosActualizar = new DTOActualizarTopico(10L, "Titulo Editado", "Mensaje Editado Valido", TopicStatus.OPEN);

        when(topicoRepository.findById(10L)).thenReturn(Optional.of(topicoPrueba));

        var response = topicoService.actualizarTopico(datosActualizar, usuarioAutor);

        assertNotNull(response);
        assertEquals("Titulo Editado", topicoPrueba.getTitulo());
        assertEquals("Mensaje Editado Valido", topicoPrueba.getMensaje());
        verify(validadorDeDuplicados, times(1)).validar(datosActualizar);
    }

    @Test
    @DisplayName("Deberia lanzar AuthorizationException al intentar actualizar un topico que pertenece a otro usuario")
    void testActualizarTopicoSinAutorizacion() {
        var datosActualizar = new DTOActualizarTopico(10L, "Titulo Editado", "Mensaje Editado", TopicStatus.OPEN);

        when(topicoRepository.findById(10L)).thenReturn(Optional.of(topicoPrueba));

        assertThrows(AuthorizationException.class, () ->
                topicoService.actualizarTopico(datosActualizar, usuarioAjeno)
        );

        verify(validadorDeDuplicados, never()).validar(datosActualizar);
    }

    @Test
    @DisplayName("Deberia lanzar EntityNotFoundException si el ID del topico a actualizar no existe")
    void testActualizarTopicoNoEncontrado() {
        var datosActualizar = new DTOActualizarTopico(99L, "Titulo", "Mensaje", TopicStatus.OPEN);

        when(topicoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                topicoService.actualizarTopico(datosActualizar, usuarioAutor)
        );
    }

    @Test
    @DisplayName("Deberia eliminar el topico correctamente cuando el usuario es el autor")
    void testEliminarTopicoExitoso() {
        when(topicoRepository.findById(10L)).thenReturn(Optional.of(topicoPrueba));

        topicoService.eliminarTopico(10L, usuarioAutor);

        verify(topicoRepository, times(1)).deleteById(10L);
    }

    @Test
    @DisplayName("Deberia lanzar AuthorizationException al intentar eliminar un topico de otro usuario")
    void testEliminarTopicoSinAutorizacion() {
        when(topicoRepository.findById(10L)).thenReturn(Optional.of(topicoPrueba));

        assertThrows(AuthorizationException.class, () ->
                topicoService.eliminarTopico(10L, usuarioAjeno)
        );

        verify(topicoRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deberia retornar el detalle del topico con sus respuestas paginadas")
    void testRetornarDatosTopicoExitoso() {
        Pageable pageable = PageRequest.of(0, 2);
        when(topicoRepository.findById(10L)).thenReturn(Optional.of(topicoPrueba));
        when(respuestaRepository.findAllByTopicoIdOrderBySolucionDesc(10L, pageable))
                .thenReturn(Page.empty());

        var resultado = topicoService.retonarDatosTopico(10L, pageable);

        assertNotNull(resultado);
        verify(topicoRepository, times(1)).findById(10L);
        verify(respuestaRepository, times(1)).findAllByTopicoIdOrderBySolucionDesc(10L, pageable);
    }

    @Test
    @DisplayName("Deberia listar los topicos paginados correctamente")
    void testListarTopicosExitoso() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Topico> paginaTopicos = new PageImpl<>(List.of(topicoPrueba));

        when(topicoRepository.findAll(pageable)).thenReturn(paginaTopicos);

        var resultado = topicoService.listar(pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        verify(topicoRepository, times(1)).findAll(pageable);
    }
}
