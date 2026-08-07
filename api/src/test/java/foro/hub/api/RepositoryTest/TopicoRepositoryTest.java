package foro.hub.api.RepositoryTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import foro.hub.api.domain.curso.Curso;
import foro.hub.api.domain.topico.TopicStatus;
import foro.hub.api.domain.topico.Topico;
import foro.hub.api.domain.topico.TopicoRepository;
import foro.hub.api.domain.usuarios.Usuario;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TopicoRepositoryTest {

    @Autowired
    TopicoRepository topicoRepository;

    @Autowired
    TestEntityManager testEntityManager;

    private Topico topicoPrueba;

    private Usuario usuarioPrueba;

    private Curso cursoPrueba;

    @BeforeEach
    void setUp(){
        usuarioPrueba = registrarUsuario("usuarioPrueba", "123456","ricardo@gmail.com");
        cursoPrueba = new Curso("Progamacion Java","Objetos 2");
        topicoPrueba = registrarTopico("Pensadores","Que pasa cuando dejamos de pensar en lo importante? Podemos analizarlo desde diferentes perspectivas, empezando por....etc.",new Date(),TopicStatus.OPEN,cursoPrueba,usuarioPrueba);
    }


    @Test
    @DisplayName("Deberia retornar el topico si existe en la base de datos")
    void testFindByIdExitoso(){
        Long idBuscado = topicoPrueba.getId();

        var topicoEncontrado = topicoRepository.findById(idBuscado);

        assertFalse(topicoEncontrado.isEmpty());
        assertEquals("Pensadores",topicoEncontrado.get().getTitulo());
    }

    @Test
    @DisplayName("Deberia retornar un optional vacio si el topico no existe en la base de datos")
    void testFindByIdFallido(){
        Long idBuscado = topicoPrueba.getId();

        var topicoEncontrado = topicoRepository.findById(idBuscado + 1);

        assertTrue(topicoEncontrado.isEmpty());
    }

    @Test
    @DisplayName("Deberia retornar el topico si existe en la base de datos por su titulo y contenido")
    void testfindByTituloAndMensajeExitoso(){
        var topicoEncontrado = topicoRepository.findByTituloAndMensaje("Pensadores","Que pasa cuando dejamos de pensar en lo importante? Podemos analizarlo desde diferentes perspectivas, empezando por....etc.");
        
        assertTrue(topicoEncontrado.isPresent());
        assertEquals("Pensadores",topicoEncontrado.get().getTitulo());
    }

    @Test
    @DisplayName("Deberia retornar un optional vacio si el topico no existe en la base de datos por su titulo y contenido")
    void testfindByTituloAndMensajeFallido(){
        var topicoEncontrado = topicoRepository.findByTituloAndMensaje("Pensadores","Que pasa cuando dejamos de pensar en lo importante? Podemos analizarlo desde diferentes perspectivas.");
        
        assertTrue(topicoEncontrado.isEmpty());
    }

    @Test
    @DisplayName("Deberia incrementar correctamente el numero de respuestas del topico")
    void testIncrementarNumeroDeRespuestasExitoso() {
        Long idBuscado = topicoPrueba.getId();

        Integer filasAfectadas = topicoRepository.incrementarNumeroDeRespuestas(idBuscado);
        var topicoEncontrado = topicoRepository.findById(idBuscado);

        assertTrue(topicoEncontrado.isPresent());
        assertEquals(1, filasAfectadas);
        assertEquals(1, topicoEncontrado.get().getNumRespuestas());
    }

    @Test
    @DisplayName("La cantidad de filas afectadas deberia ser 0 al incrementar si el topico no existe")
    void testIncrementarNumeroDeRespuestasFallido() {
        Long idBuscado = topicoPrueba.getId();

        Integer filasAfectadas = topicoRepository.incrementarNumeroDeRespuestas(idBuscado + 1);

        assertEquals(0, filasAfectadas);
    }

    @Test
    @DisplayName("Deberia decrementar correctamente el numero de respuestas del topico")
    void testDecrementarNumeroDeRespuestasExitoso() {
        Long idBuscado = topicoPrueba.getId();
        
        // Arrancamos incrementando a 1 para poder probar el decremento a 0
        topicoRepository.incrementarNumeroDeRespuestas(idBuscado);

        Integer filasAfectadas = topicoRepository.decrementarNumeroDeRespuestas(idBuscado);
        var topicoEncontrado = topicoRepository.findById(idBuscado);

        assertTrue(topicoEncontrado.isPresent());
        assertEquals(1, filasAfectadas);
        assertEquals(0, topicoEncontrado.get().getNumRespuestas());
    }

    @Test
    @DisplayName("La cantidad de filas afectadas deberia ser 0 al decrementar si el topico no existe")
    void testDecrementarNumeroDeRespuestasFallido() {
        Long idBuscado = topicoPrueba.getId();

        Integer filasAfectadas = topicoRepository.decrementarNumeroDeRespuestas(idBuscado + 1);

        assertEquals(0, filasAfectadas);
    }


    private Usuario registrarUsuario(String login,String clave,String Email){    
        Usuario usuario = new Usuario();
        usuario.setLogin(login);
        usuario.setClave(clave);
        usuario.setEmail(Email);
        testEntityManager.persistAndFlush(usuario);
        return usuario;
    }

    private Topico registrarTopico(String titulo,String mensaje,Date fecha_creacion,TopicStatus status,Curso curso,Usuario autor){    
        Topico topico = new Topico();
        topico.setTitulo(titulo);
        topico.setMensaje(mensaje);
        topico.setFechaCreacion(fecha_creacion);
        topico.setStatus(status);
        topico.setAutor(autor);
        topico.setCurso(curso);
        topico.setNumRespuestas(0);
        testEntityManager.persistAndFlush(topico);

        return topico;
    }
}
