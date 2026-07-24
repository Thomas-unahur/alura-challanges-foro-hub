package foro.hub.api.RepositoryTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import foro.hub.api.domain.usuarios.Usuario;
import foro.hub.api.domain.usuarios.UsuarioRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UsuarioRepositoryTest {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private Usuario usuarioPrueba;

    @BeforeEach
    void setUp(){
      usuarioPrueba = registrarUsuario("usuarioPrueba", "123456","ricardo@gmail.com");
    }

    @Test
    @DisplayName("Deberia retornar el usuario cuando existe en la base de datos")
    void testFindByLoginExitoso(){

        var usuarioEncontrado = usuarioRepository.findByLogin("usuarioPrueba");

        assertNotNull(usuarioEncontrado);
        assertEquals("usuarioPrueba", usuarioEncontrado.get().getUsername());
    }

    @Test
    @DisplayName("Deberia retornar un optional vacio si no encuentra al usuario en la base de datos")
    void testFindByLoginFallido(){

        var usuarioEncontrado = usuarioRepository.findByLogin("usuarioErroneo");

        assertTrue(usuarioEncontrado.isEmpty());
    }

    @Test
    @DisplayName("Deberia retornar el usuario cuando existe en la base de datos por su email")
    void testFindByEmailExitoso(){

        var usuarioEncontrado = usuarioRepository.findByEmail("ricardo@gmail.com");

        assertNotNull(usuarioEncontrado);
        assertEquals("ricardo@gmail.com", usuarioEncontrado.get().getEmail());
    }

    @Test
    @DisplayName("Deberia retornar un optional empty si no encuentra al usuario en la base de datos por su email")
    void testFindByEmailFallido(){

        var usuarioEncontrado = usuarioRepository.findByEmail("jose@gmail.com");

        assertTrue(usuarioEncontrado.isEmpty());
    }

    



    private Usuario registrarUsuario(String login,String clave,String Email){    
        Usuario usuario = new Usuario();
        usuario.setLogin(login);
        usuario.setClave(clave);
        usuario.setEmail(Email);
        testEntityManager.persistAndFlush(usuario);

        return usuario;
    }

}
