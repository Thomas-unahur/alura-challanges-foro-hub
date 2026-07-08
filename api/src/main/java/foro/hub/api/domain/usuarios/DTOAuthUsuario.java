package foro.hub.api.domain.usuarios;

import jakarta.validation.constraints.NotBlank;

public record DTOAuthUsuario(
    @NotBlank
    String login,
    @NotBlank 
    String clave) {
}
