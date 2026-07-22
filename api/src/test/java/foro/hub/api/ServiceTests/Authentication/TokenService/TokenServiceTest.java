package foro.hub.api.ServiceTests.Authentication.TokenService;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import foro.hub.api.domain.usuarios.Usuario;
import foro.hub.api.infra.security.TokenService;

public class TokenServiceTest {
    private TokenService tokenService;
    private Usuario usuario;

    @BeforeEach
    void setUp(){
        tokenService = new TokenService();

        ReflectionTestUtils.setField(tokenService,"apiSecret","123456789"); //simulo la inyeccion de la api key para la creacion de tokens

        usuario = new Usuario();
        usuario.setLogin("usuario_prueba");
    }

    @Test
    @DisplayName("Deberia obtener el subject (login) correcto a partir de un token valido")
    void testGetSubjectExitoso(){
        String token = tokenService.generarToken(usuario);

        String subject = tokenService.getSubject(token);

        assertEquals("usuario_prueba", subject);
    }

    @Test
    @DisplayName("Deberia lanzar RuntimeException cuando el token recibido es null")
    void testGetSubjectTokenNull(){
        assertThrows(RuntimeException.class, 
            () -> tokenService.getSubject(null)
        );
    }

    @Test
    @DisplayName("Deberia lanzar una excepción cuando el token es invalido")
    void testGetSubjectTokenInvalido(){
        String tokenInvalido = "12345679ASDF";
        assertThrows(RuntimeException.class, 
            () -> tokenService.getSubject(tokenInvalido)
        );
    }

}
