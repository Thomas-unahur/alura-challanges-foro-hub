package foro.hub.api.controller;

import foro.hub.api.domain.respuestas.DTOActualizarRespuesta;
import foro.hub.api.domain.respuestas.DTOResponseRespuesta;
import foro.hub.api.domain.respuestas.RespuestaService;
import foro.hub.api.domain.usuarios.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import foro.hub.api.domain.respuestas.DTORegistroRespuesta;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/respuestas")
@SecurityRequirement(name = "bearer-key")
public class RespuestaController {
    @Autowired
    private RespuestaService respuestaService;

    @PostMapping
    @Operation(summary = "Registra una nueva respuesta a un tópico en la base de datos.")
    ResponseEntity<DTOResponseRespuesta> registrarRespuesta(@RequestBody @Valid DTORegistroRespuesta dtoRegistroRespuesta,@AuthenticationPrincipal Usuario usuarioAutenticado,
                                      UriComponentsBuilder uriComponentsBuilder){
        DTOResponseRespuesta nuevaRespuesta = respuestaService.registrarRespuesta(dtoRegistroRespuesta,usuarioAutenticado);
        URI url = uriComponentsBuilder.path("/respuestas/{id}").buildAndExpand(nuevaRespuesta.id()).toUri();

        return ResponseEntity.created(url).body(nuevaRespuesta);

    }

    @PutMapping
    @Operation(summary = "Permite editar una respuesta a un tópico en la base de datos.")
    ResponseEntity<DTOResponseRespuesta> actualizarRespuesta(@RequestBody @Valid DTOActualizarRespuesta dtoActualizarRespuesta,@AuthenticationPrincipal Usuario usuarioAutenticado){
        DTOResponseRespuesta dtoResponseRespuesta = respuestaService.actualizarRespuesta(dtoActualizarRespuesta,usuarioAutenticado);
        return ResponseEntity.ok(dtoResponseRespuesta);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Permite eliminar una respuesta de un usuario")
    ResponseEntity<Void> eliminarRespuesta(@PathVariable Long id,@AuthenticationPrincipal Usuario usuarioAutenticado){
        respuestaService.eliminarRespuesta(id,usuarioAutenticado);
        return ResponseEntity.noContent().build();
    }
}
