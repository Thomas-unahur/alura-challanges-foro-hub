package foro.hub.api.ServiceTests;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import foro.hub.api.domain.perfil.Perfil;
import foro.hub.api.domain.perfil.PerfilRepository;
import foro.hub.api.domain.usuarios.DTORegistroUsuario;
import foro.hub.api.domain.usuarios.DTOResponseUsuario;
import foro.hub.api.domain.usuarios.Usuario;
import foro.hub.api.domain.usuarios.UsuarioRepository;
import foro.hub.api.domain.usuarios.UsuarioService;
import jakarta.validation.ValidationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PerfilRepository perfilRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private DTORegistroUsuario dtoRegistro;

    @BeforeEach
    void setUp() {
        dtoRegistro = new DTORegistroUsuario("usuarioTest", "123456", "test@email.com", "ROLE_USER");
    }

    @Test
    @DisplayName("Debe registrar usuario correctamente")
    void registrarUsuarioExito() {
        when(usuarioRepository.findByEmail(dtoRegistro.email())).thenReturn(Optional.empty());
        when(usuarioRepository.findByUsername(dtoRegistro.login())).thenReturn(Optional.empty());
        when(perfilRepository.findByNombre(dtoRegistro.nombrePerfil())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(dtoRegistro.clave())).thenReturn("claveEncriptada");

        DTOResponseUsuario response = usuarioService.registrarUsuario(dtoRegistro);

        assertNotNull(response);
        assertEquals("usuarioTest", response.login());
        assertEquals("test@email.com", response.email());

        verify(usuarioRepository).save(any(Usuario.class));
        verify(passwordEncoder).encode("123456");
    }

    @Test
    @DisplayName("Debe lanzar ValidationException cuando el email ya esta registrado")
    void registrarUsuarioEmailDuplicado() {
        when(usuarioRepository.findByEmail(dtoRegistro.email())).thenReturn(Optional.of(new Usuario()));

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> usuarioService.registrarUsuario(dtoRegistro)
        );

        assertEquals("El email ingresado ya se encuentra registrado", exception.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar ValidationException cuando el usuario ya existe")
    void registrarUsuarioUsernameDuplicado() {
        when(usuarioRepository.findByEmail(dtoRegistro.email())).thenReturn(Optional.empty());
        when(usuarioRepository.findByUsername(dtoRegistro.login())).thenReturn(Optional.of(new Usuario()));

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> usuarioService.registrarUsuario(dtoRegistro)
        );

        assertEquals("El usuario ingresado ya existe", exception.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar ValidationException cuando el nombre de perfil ya esta en uso")
    void registrarUsuarioPerfilDuplicado() {
        when(usuarioRepository.findByEmail(dtoRegistro.email())).thenReturn(Optional.empty());
        when(usuarioRepository.findByUsername(dtoRegistro.login())).thenReturn(Optional.empty());
        when(perfilRepository.findByNombre(dtoRegistro.nombrePerfil())).thenReturn(Optional.of(new Perfil()));

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> usuarioService.registrarUsuario(dtoRegistro)
        );

        assertEquals("El nombre de perfil ya esta en uso", exception.getMessage());
        verify(usuarioRepository, never()).save(any());
    }
}