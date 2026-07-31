package foro.hub.api.controller;

import foro.hub.api.domain.respuestas.DTOActualizarRespuesta;
import foro.hub.api.domain.respuestas.DTOResponseRespuesta;
import foro.hub.api.domain.respuestas.RespuestaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import foro.hub.api.domain.respuestas.DTORegistroRespuesta;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/respuestas")
@SecurityRequirement(name = "bearer-key")
public class RespuestaController {
    @Autowired
    private RespuestaService respuestaService;

    @Transactional
    @PostMapping
    @Operation(summary = "Registra una nueva respuesta a un tópico en la base de datos.")
    ResponseEntity<DTOResponseRespuesta> registrarRespuesta(@RequestBody @Valid DTORegistroRespuesta dtoRegistroRespuesta,
                                      UriComponentsBuilder uriComponentsBuilder){
        DTOResponseRespuesta nuevaRespuesta = respuestaService.registrarRespuesta(dtoRegistroRespuesta);
        URI url = uriComponentsBuilder.path("/respuestas/{id}").buildAndExpand(nuevaRespuesta.id()).toUri();

        return ResponseEntity.created(url).body(nuevaRespuesta);

    }

    @Transactional
    @PutMapping
    @Operation(summary = "Permite editar una respuesta a un tópico en la base de datos.")
    ResponseEntity<DTOResponseRespuesta> actualizarRespuesta(@RequestBody @Valid DTOActualizarRespuesta dtoActualizarRespuesta){
        DTOResponseRespuesta dtoResponseRespuesta = respuestaService.actualizarRespuesta(dtoActualizarRespuesta);
        return ResponseEntity.ok(dtoResponseRespuesta);
    }
}
