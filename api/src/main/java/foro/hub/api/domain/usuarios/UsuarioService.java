package foro.hub.api.domain.usuarios;

import foro.hub.api.domain.perfil.Perfil;
import foro.hub.api.domain.perfil.PerfilRepository;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PerfilRepository perfilRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public DTOResponseUsuario registrarUsuario(DTORegistroUsuario dtoRegistroUsuario){

        if(usuarioRepository.findByEmail(dtoRegistroUsuario.email()).isPresent()){
            throw new ValidationException("El email ingresado ya se encuentra registrado");
        }

        if(usuarioRepository.findByUsername(dtoRegistroUsuario.login()).isPresent()){
            throw new ValidationException("El usuario ingresado ya existe");
        }

        if(perfilRepository.findByNombre(dtoRegistroUsuario.nombrePerfil()).isPresent()){
            throw new ValidationException("El nombre de perfil ya esta en uso");
        }

        Usuario nuevoUsuario = new Usuario(dtoRegistroUsuario);

      //  nuevoUsuario.setPerfil(new Perfil(dtoRegistroUsuario.nombrePerfil()));
        nuevoUsuario.setClave(passwordEncoder.encode(nuevoUsuario.getPassword()));

        usuarioRepository.save(nuevoUsuario);

        return (new DTOResponseUsuario(
                nuevoUsuario.getId(),
                nuevoUsuario.getUsername(),
                nuevoUsuario.getEmail(),
                nuevoUsuario.getPerfil()
        ));
    }
}
