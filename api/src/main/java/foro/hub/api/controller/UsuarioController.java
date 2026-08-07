package foro.hub.api.controller;

import foro.hub.api.domain.usuarios.DTORegistroUsuario;
import foro.hub.api.domain.usuarios.DTOResponseUsuario;
import foro.hub.api.domain.usuarios.UsuarioRepository;
import foro.hub.api.domain.usuarios.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/usuarios")
@SecurityRequirement(name = "bearer-key")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Transactional
    @PostMapping
    @Operation(summary = "Registra un nuevo usuario")
    public ResponseEntity<DTOResponseUsuario> registrarUsuario(@RequestBody @Valid DTORegistroUsuario dtoRegistroUsuario,
                                                               UriComponentsBuilder uriComponentsBuilder){
      DTOResponseUsuario nuevoUsuario = usuarioService.registrarUsuario(dtoRegistroUsuario);
      URI url = uriComponentsBuilder.path("/usuarios/{id}").buildAndExpand(nuevoUsuario.id()).toUri();

    return ResponseEntity.created(url).body(nuevoUsuario);
    }
}
