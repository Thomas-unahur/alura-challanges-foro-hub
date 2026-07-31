package foro.hub.api.controller;

import foro.hub.api.domain.usuarios.DTOAuthUsuario;
import foro.hub.api.infra.security.AuthLoginService;
import foro.hub.api.infra.security.DTOJWTToken;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
@Tag(name = "Autenticacion", description = "obtiene el token para el usuario asignado que da acceso al resto de endpoints")
public class AutenticacionController {

    @Autowired
    AuthLoginService authLoginService;

    @PostMapping
    public ResponseEntity<DTOJWTToken> autenticarUsuario(@RequestBody @Valid DTOAuthUsuario datosAutenticacionUsuario) {
        DTOJWTToken dtojwtToken = authLoginService.autenticarUsuario(datosAutenticacionUsuario);
        return ResponseEntity.ok(dtojwtToken);
    }
}
