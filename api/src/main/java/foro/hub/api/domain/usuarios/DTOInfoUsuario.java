package foro.hub.api.domain.usuarios;

public record DTOInfoUsuario(
        Long id,
        String perfil
) {
        public DTOInfoUsuario(Long id,String perfil){
                this.id = id;
                this.perfil = perfil;
        }
}
