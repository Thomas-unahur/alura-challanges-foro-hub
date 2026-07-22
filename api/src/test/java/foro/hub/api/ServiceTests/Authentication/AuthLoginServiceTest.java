package foro.hub.api.ServiceTests.Authentication;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;

import foro.hub.api.domain.usuarios.DTOAuthUsuario;
import foro.hub.api.domain.usuarios.IntentosLoginService;
import foro.hub.api.domain.usuarios.Usuario;
import foro.hub.api.infra.errores.AuthenticationFailedException;
import foro.hub.api.infra.security.AuthLoginService;
import foro.hub.api.infra.security.TokenService;

public class AuthLoginServiceTest {
    
    @InjectMocks
    private AuthLoginService authLoginService;

    @Mock
    private IntentosLoginService intentosLoginService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @Mock 
    Authentication authentication;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deberia retornar un DTOJWTToken cuando la autenticación es exitosa")
    void testAuntenticarUsuarioExitoso(){
        var datos = new DTOAuthUsuario("usuario","123456"); 
        when(intentosLoginService.estaBloqueado(any())).thenReturn(false);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(new Usuario());
        when(tokenService.generarToken(any())).thenReturn("Token_valido");

        var resultado = authLoginService.autenticarUsuario(datos);

        assertNotNull(resultado);
        assertNotNull(resultado.jwtToken());
    }

    @Test
    @DisplayName("Deberia lanzar la excepcion AuthenticationFailedException")
    void testAuntenticarUsuarioEstaBloqueado(){
        var datos = new DTOAuthUsuario("usuario","123456"); 
        when(intentosLoginService.estaBloqueado(any())).thenReturn(true);
    

       assertThrows(AuthenticationFailedException.class,
        () -> authLoginService.autenticarUsuario(datos)
       );
    }

    @Test
    @DisplayName("Debería registrar el intento como fallido y lanzar una excepcion cuando las credenciales son incorrectas")
    void testAutenticarUsuarioCredencialesIncorrectas() {
        var datos = new DTOAuthUsuario("usuario", "clave_incorrecta");
        when(intentosLoginService.estaBloqueado(any())).thenReturn(false);
        
        when(authenticationManager.authenticate(any()))
            .thenThrow(new BadCredentialsException("credenciales invalidas"));


        assertThrows(AuthenticationFailedException.class, () -> authLoginService.autenticarUsuario(datos));
        verify(intentosLoginService, times(1)).inicioDeSesionFallido(datos.login());
    }

    @Test
    @DisplayName("Deberia registrar correctamente el inicio de sesion, pero lanza una excepcion cuando falla la autenticacion")
    void testAutenticarUsuarioFalloelProcesoDeAutenticacion() {
        var datos = new DTOAuthUsuario("usuario", "clave_incorrecta");
        when(intentosLoginService.estaBloqueado(any())).thenReturn(false);
        
        when(authenticationManager.authenticate(any()))
            .thenThrow(new InternalAuthenticationServiceException("Error en el proceso de autenticacion"));


        assertThrows(AuthenticationFailedException.class, () -> authLoginService.autenticarUsuario(datos));
        verify(intentosLoginService, times(1)).inicioDeSesionFallido(datos.login());
    }
}
