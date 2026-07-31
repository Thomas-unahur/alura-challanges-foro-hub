package foro.hub.api.domain.topico;

import foro.hub.api.domain.respuestas.DTOResponseRespuesta;
import org.springframework.data.domain.Page;

public record DTOTopicoYRespuestas(
        DTOResponseTopic topico,
        Page<DTOResponseRespuesta> respuestas
) {
}
