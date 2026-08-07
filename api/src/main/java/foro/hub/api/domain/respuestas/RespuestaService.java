package foro.hub.api.domain.respuestas;

import foro.hub.api.domain.respuestas.validaciones.ValidadorDeRespuestas;
import foro.hub.api.domain.topico.TopicoRepository;
import foro.hub.api.domain.usuarios.Usuario;
import foro.hub.api.infra.errores.AuthorizationException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class RespuestaService {


    private final TopicoRepository topicoRepository;


    private final RespuestaRepository respuestaRepository;


    private final List<ValidadorDeRespuestas> validadores;

    public DTOResponseRespuesta registrarRespuesta(DTORegistroRespuesta dtoRegistroRespuesta, Usuario usuarioAutenticado){
        Respuesta nuevaRespuesta = new Respuesta(dtoRegistroRespuesta.mensaje());

        nuevaRespuesta.setAutor(usuarioAutenticado);
        var topicoApuntado = topicoRepository.findById(dtoRegistroRespuesta.topicoID()).orElseThrow(
            () -> new EntityNotFoundException()
        );

        validadores.forEach(v -> v.validar(dtoRegistroRespuesta));
        nuevaRespuesta.setTopico(topicoApuntado);

        respuestaRepository.save(nuevaRespuesta);
        topicoRepository.incrementarNumeroDeRespuestas(nuevaRespuesta.getTopico().getId());

        return mapearResponseDeRespuesta(nuevaRespuesta);
    }

    public DTOResponseRespuesta actualizarRespuesta(DTOActualizarRespuesta dtoActualizarRespuesta,Usuario usuarioAutenticado){

        var respuesta = respuestaRepository.findById(dtoActualizarRespuesta.iDRespuesta()).orElseThrow(
            () -> new EntityNotFoundException()
        );
        validarAutoria(respuesta, usuarioAutenticado, "No estas autorizado a realizar esta accion");
        validadores.forEach(v -> v.validar(dtoActualizarRespuesta));
        respuesta.actualizarDatos(dtoActualizarRespuesta);
        return mapearResponseDeRespuesta(respuesta);
    }

    public void eliminarRespuesta(Long id,Usuario usuarioAutenticado){
        var respuesta = respuestaRepository.findById(id).orElseThrow(
            () -> new EntityNotFoundException()
        );
        
        validarAutoria(respuesta, usuarioAutenticado, "No estas autorizado a realizar esta accion");
        respuestaRepository.deleteById(id);
        topicoRepository.decrementarNumeroDeRespuestas(respuesta.getTopico().getId());
    }

    private void validarAutoria(Respuesta respuesta,Usuario usuarioAutenticado, String mensajeDeError){
        if(!respuesta.getAutor().getId().equals(usuarioAutenticado.getId())){
            throw new AuthorizationException(mensajeDeError);
        }
    }


    private DTOResponseRespuesta mapearResponseDeRespuesta(Respuesta respuesta){
        return new DTOResponseRespuesta(respuesta.getId(),
                respuesta.getMensaje(),
                respuesta.getTopico().getId(),
                respuesta.getFechaCreacion(),
                respuesta.getAutor().getId(),
                respuesta.isSolucion());
    }
}
