package foro.hub.api.IntentosLoginTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import foro.hub.api.domain.usuarios.IntentosLoginService;

public class IntentosLoginServiceTest {
    private IntentosLoginService intentosLoginService;
    private TestTicker testTicker;
    private String usuarioPrueba;

    @BeforeEach
    void setUp(){
        testTicker = new TestTicker();
        intentosLoginService = new IntentosLoginService(testTicker);
        usuarioPrueba = "usuario_prueba";
    }

    public void simularFallos(String usuario, Integer cantidad){ 
        for(Integer i=0;i<cantidad;i++){
            intentosLoginService.inicioDeSesionFallido(usuario);
        }   
    }
    public void bloquearUsuario(String usuario){
        simularFallos(usuario, 5);
    }


    @Test
    @DisplayName("El usuario se bloquea despues de 5 intentos fallidos")
    void testElUsusarioFalla5IntentosYEstaBloqueado(){
        bloquearUsuario(usuarioPrueba);
        assertTrue(intentosLoginService.estaBloqueado(usuarioPrueba));
    }


    @Test
    @DisplayName("El usuario se desbloquea despues de 15 minutos")
    void testElUsuarioSeDesbloqueaDespuesDe15Minutos(){
        bloquearUsuario(usuarioPrueba);
        testTicker.advance(16, TimeUnit.MINUTES);
        assertFalse(intentosLoginService.estaBloqueado(usuarioPrueba));
    }

    @Test
    @DisplayName("El usuario no esta bloqueado si no supero los 5 intentos")
    void testElUsarioNoEstaBloqueadoYNoSuperaLos5Intentos(){
        simularFallos(usuarioPrueba,4);
        assertFalse(intentosLoginService.estaBloqueado(usuarioPrueba));
    }

    @Test
    @DisplayName("El usuario continua bloqueado antes del tiempo de expiracion")
    void testElUsuarioPermaneceBloqueadoAntesDeLaExpiracion(){
        bloquearUsuario(usuarioPrueba);
        testTicker.advance(10, TimeUnit.MINUTES);
        assertTrue(intentosLoginService.estaBloqueado(usuarioPrueba));
    }

    @Test
    @DisplayName("El usuario tiene fallos acumulados pero se loguea exitosamente")
    void testElUsuarioTieneFallosAcumuladosPeroSeLogueaExitosamente(){
        simularFallos(usuarioPrueba, 4);
        intentosLoginService.inicioDeSesionExitoso(usuarioPrueba);
        simularFallos(usuarioPrueba, 2);
        assertFalse(intentosLoginService.estaBloqueado(usuarioPrueba));
    }
    
}
