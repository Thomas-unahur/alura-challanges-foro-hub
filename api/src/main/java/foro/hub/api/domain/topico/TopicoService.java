package foro.hub.api.domain.topico;

import foro.hub.api.domain.respuestas.DTOResponseRespuesta;
import foro.hub.api.domain.respuestas.RespuestaRepository;
import foro.hub.api.domain.topico.validaciones.ValidadorDeDuplicados;
import foro.hub.api.domain.usuarios.DTOInfoUsuario;
import foro.hub.api.domain.usuarios.Usuario;
import foro.hub.api.infra.errores.AuthorizationException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;



@Service
@RequiredArgsConstructor
public class TopicoService {
    
    private final TopicoRepository topicoRepository;
    
    private final List<ValidadorDeDuplicados> validadores;
    
    private final RespuestaRepository respuestaRepository;

    @Transactional
    public DTOResponseTopic actualizarTopico(DTOActualizarTopico datos,Usuario usuarioAutenticado){
        Topico topico = topicoRepository.findById(datos.id()).orElseThrow(
            () -> new EntityNotFoundException()
        );

        validarAutoria(topico,usuarioAutenticado, "No estas autorizado a realizar esta accion");

        validadores.forEach(v->v.validar(datos));

        topico.actualizarDatos(datos);
        topicoRepository.save(topico);

        return (mapearADTOResponseTopic(topico));

    }

    @Transactional
    public DTOResponseTopic registrarTopico(DTORegistroTopico datos,Usuario usuarioAutenticado){

        Topico topico = new Topico(datos);
        topico.setAutor(usuarioAutenticado);

        validadores.forEach(v->v.validar(datos));
        topicoRepository.save(topico);

        return (mapearADTOResponseTopic(topico));

    }

    @Transactional
    public void eliminarTopico(Long id,Usuario usuarioAutenticado){

        var topico = topicoRepository.findById(id).orElseThrow(
            () -> new EntityNotFoundException()
        );


        validarAutoria(topico, usuarioAutenticado,"No estas autorizado a realizar esta accion");
        topicoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public DTOTopicoYRespuestas retonarDatosTopico(Long id, Pageable pag){
        Topico topico = topicoRepository.findById(id).orElseThrow(
            () -> new EntityNotFoundException()
        );

        DTOResponseTopic dtoResponseTopic = mapearADTOResponseTopic(topico);

        Page<DTOResponseRespuesta> dtoResponseRespuestas = respuestaRepository
        .findAllByTopicoIdOrderBySolucionDesc(id,pag)
        .map(DTOResponseRespuesta::new);


        return new DTOTopicoYRespuestas(dtoResponseTopic,dtoResponseRespuestas);
    }

    @Transactional(readOnly = true)
    public Page<DTOListadoTopico> listar(Pageable paginacion){
        return topicoRepository.findAll(paginacion).map(DTOListadoTopico::new);
    }


    private void validarAutoria(Topico topico,Usuario usuarioAutenticado, String mensajeDeError){
        if(!topico.getAutor().getId().equals(usuarioAutenticado.getId())){
            throw new AuthorizationException(mensajeDeError);
        }
    }

    private DTOResponseTopic mapearADTOResponseTopic(Topico topico) {
        return new DTOResponseTopic(
                topico.getId(),
                topico.getTitulo(),
                topico.getMensaje(),
                topico.getFechaCreacion(),
                topico.getStatus(),
                new DTOInfoUsuario(topico.getAutor().getId(), topico.getAutor().getPerfil().getNombre()), // ID de Autor corregido
                topico.getCurso(),
                topico.getNumRespuestas()
        );
    }



}
