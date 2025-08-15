package br.com.estoque.service;

import br.com.estoque.model.Usuario;
import br.com.estoque.repository.UsuarioRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
@Primary
@Service
public class CustomUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UsuarioRepository userRepository;

    public CustomUserDetailsService(UsuarioRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String cpf) throws UsernameNotFoundException {
        Usuario userEntity = userRepository.findByCpf(cpf)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with CPF: " + cpf));

        return new User(
                userEntity.getCpf(), // o CPF será o "username" usado internamente pelo Spring
                userEntity.getSenha(),
                List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority(userEntity.getRole().getName()))
        );
    }
}


