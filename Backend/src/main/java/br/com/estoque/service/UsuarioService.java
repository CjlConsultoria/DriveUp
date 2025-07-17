package br.com.estoque.service;

import br.com.estoque.dto.UsuarioRequest;
import br.com.estoque.model.Endereco;
import br.com.estoque.model.Usuario;
import br.com.estoque.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public Usuario criar(UsuarioRequest request) {
        Usuario usuario = mapearRequestParaUsuario(request);
        usuario.setId(UUID.randomUUID());
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        return usuarioRepository.save(usuario);
    }

    public Usuario buscarPorId(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    private Usuario mapearRequestParaUsuario(UsuarioRequest request) {
        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setTelefone(request.getTelefone());
        usuario.setCpf(request.getCpf());
        usuario.setRole(request.getRole());

        if (request.getEndereco() != null) {
            Endereco endereco = new Endereco();
            endereco.setCep(request.getEndereco().getCep());
            endereco.setRua(request.getEndereco().getRua());
            endereco.setNumero(request.getEndereco().getNumero());
            endereco.setBairro(request.getEndereco().getBairro());
            endereco.setCidade(request.getEndereco().getCidade());
            endereco.setEstado(request.getEndereco().getEstado());
            usuario.setEndereco(endereco);
        }

        return usuario;
    }
}
