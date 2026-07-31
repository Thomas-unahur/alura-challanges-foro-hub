package foro.hub.api.controller;

import foro.hub.api.domain.topico.*;
import foro.hub.api.domain.usuarios.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;


@RestController
@RequestMapping("/topicos")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
public class TopicoController {

    private final TopicoService topicoService;


    @PostMapping
    @Operation(summary = "Registra un nuevo tópico en la base de datos.")
    public ResponseEntity<DTOResponseTopic> registrarTopico(@RequestBody @Valid DTORegistroTopico dtoRegistroTopico,@AuthenticationPrincipal Usuario usuarioAutenticado,
                                                            UriComponentsBuilder uriComponentsBuilder){

        DTOResponseTopic topicoRegistrado = topicoService.registrarTopico(dtoRegistroTopico,usuarioAutenticado);
        URI url = uriComponentsBuilder.path("/topicos/{id}").buildAndExpand(topicoRegistrado.id()).toUri();

        return ResponseEntity.created(url).body(topicoRegistrado);

    }


    @GetMapping
    @Operation(summary = "Lista todos los tópicos registrados en la base de datos.")
    public ResponseEntity<Page<DTOListadoTopico>> listadoTopicos(
        @PageableDefault(size = 2, sort = "fechaCreacion", direction = Sort.Direction.DESC) Pageable paginacion
    ) {

        return ResponseEntity.ok(topicoService.listar(paginacion));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Devuelve un tópico específico junto con todas sus respuestas.")
    public ResponseEntity<DTOTopicoYRespuestas>retornarDatosTopico(
            @PathVariable Long id,
            @PageableDefault(size = 2, sort = "solucion", direction = Sort.Direction.DESC) Pageable paginacion){

       DTOTopicoYRespuestas dtoTopicoYRespuestas = topicoService.retonarDatosTopico(id,paginacion);
       return ResponseEntity.ok(dtoTopicoYRespuestas);
    }

    @PutMapping
    @Operation(summary = "Actualiza un tópico específico en la base de datos.")
    public ResponseEntity<DTOResponseTopic> actualizarTopico(@RequestBody @Valid DTOActualizarTopico dtoActualizarTopico,@AuthenticationPrincipal Usuario usuarioAutenticado){
        DTOResponseTopic topicoActualizado = topicoService.actualizarTopico(dtoActualizarTopico,usuarioAutenticado);
        return ResponseEntity.ok(topicoActualizado);

    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un topico en la base de datos.")
    public ResponseEntity<Void> eliminarTopico(@PathVariable Long id,@AuthenticationPrincipal Usuario usuarioAutenticado){
        topicoService.eliminarTopico(id,usuarioAutenticado);
        return ResponseEntity.noContent().build();
    }

}
