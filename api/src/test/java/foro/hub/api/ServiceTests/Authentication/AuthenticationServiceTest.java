package foro.hub.api.ServiceTests.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import foro.hub.api.domain.usuarios.Usuario;
import foro.hub.api.domain.usuarios.UsuarioRepository;
import foro.hub.api.infra.security.AuthenticationService;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService  authenticationService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("Deberia retornar UserDetails si el usuario existe")
    void testElUsuarioExisteYDevuelveUnUserDetails(){
        String username= "usuario_prueba";
        Usuario usuarioSimulado = new Usuario();

        when(usuarioRepository.findByLogin(username)).thenReturn(Optional.of(usuarioSimulado));

        UserDetails resultado = authenticationService.loadUserByUsername(username);

        assertNotNull(resultado);
        assertEquals(usuarioSimulado, resultado);
        verify(usuarioRepository, times(1)).findByLogin(username);
    }

    @Test
    @DisplayName("Deberia retornar UserNotFoundExceptionSiElUsuarioNoExiste")
    void testElUsuarioNoExisteYLanzaUserNotFoundException(){
        String username= "usuario_erroneo";

        when(usuarioRepository.findByLogin(username)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, 
            () -> authenticationService.loadUserByUsername(username)
        );
        
    }

}
