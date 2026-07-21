package foro.hub.api.infra.security;

import foro.hub.api.domain.usuarios.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService implements UserDetailsService {
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        UserDetails usuarioEncontrado = usuarioRepository.findByLogin(username);

        if(usuarioEncontrado == null){
            throw new UsernameNotFoundException("Error. El usuario es invalido.");
        }
       
        return usuarioEncontrado;
    }
    }


