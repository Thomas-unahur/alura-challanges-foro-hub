package foro.hub.api.domain.topico;

import foro.hub.api.domain.curso.DTOCurso;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DTORegistroTopico(
        @NotBlank
        @Size(min = 3, max = 70, message = "El titulo debe tener en 3 y 70 caracteres")
        String titulo,
        @NotBlank
        @Size(min = 10, max = 300, message = "El mensaje debe tener minimamente 10 caracteres.")
        String mensaje,
        @NotNull
        TopicStatus status,
        @NotNull
        @Valid
        DTOCurso curso) {
}
