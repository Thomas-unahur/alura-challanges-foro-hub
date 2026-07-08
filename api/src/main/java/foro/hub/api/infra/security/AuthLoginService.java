package foro.hub.api.infra.security;

import foro.hub.api.domain.usuarios.DTOAuthUsuario;
import foro.hub.api.domain.usuarios.IntentosLoginService;
import foro.hub.api.domain.usuarios.Usuario;
import foro.hub.api.infra.errores.AuthenticationFailedException;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthLoginService {

    @Autowired
    IntentosLoginService intentosLoginService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;


    public DTOJWTToken autenticarUsuario(@RequestBody @Valid DTOAuthUsuario datosAutenticacionUsuario){
        String key = datosAutenticacionUsuario.login();

        if(intentosLoginService.estaBloqueado(key)){
            throw new AuthenticationFailedException("Se han superado la cantidad de intentos permitidos.");
        }

        try{
            Authentication authToken = new UsernamePasswordAuthenticationToken(datosAutenticacionUsuario.login(),datosAutenticacionUsuario.clave());
            Authentication usuarioAutenticado = authenticationManager.authenticate(authToken);
            intentosLoginService.inicioDeSesionExitoso(key);

            var JWTtoken = tokenService.generarToken((Usuario) usuarioAutenticado.getPrincipal());
            return new DTOJWTToken(JWTtoken);

        }catch (AuthenticationException e){
            intentosLoginService.inicioDeSesionFallido(key);
            throw new AuthenticationFailedException("Intento de autenticacion fallido, razón: " + e.getMessage());
        }
    }
}
