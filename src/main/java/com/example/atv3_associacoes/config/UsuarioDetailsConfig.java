package com.example.atv3_associacoes.config;

import com.example.atv3_associacoes.model.entity.Usuario;
import com.example.atv3_associacoes.model.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UsuarioDetailsConfig implements UserDetailsService {

    @Autowired
    private UsuarioRepository repository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Usuario usuario = repository.usuario(login);
        if (usuario == null) {
            throw new UsernameNotFoundException("usuário não encontrado!");
        }

        // Retorna o objeto User padrão do Spring Security alimentado pelo seu banco de dados
        return new User(
                usuario.getLogin(),
                usuario.getPassword(),
                true, true, true, true,
                usuario.getAuthorities()
        );
    }
}