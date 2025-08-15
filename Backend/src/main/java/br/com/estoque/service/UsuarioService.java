package br.com.estoque.service;

import br.com.estoque.dto.UsuarioDTO;
import br.com.estoque.dto.UsuarioRequest;
import br.com.estoque.model.Empresa;
import br.com.estoque.model.Endereco;
import br.com.estoque.model.Role;
import br.com.estoque.model.Usuario;
import br.com.estoque.model.enums.RoleType;
import br.com.estoque.repository.EmpresaRepository;
import br.com.estoque.repository.RoleRepository;
import br.com.estoque.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmpresaRepository empresaRepository;
    private final RoleRepository roleRepository;
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

        // Buscar o Role pela roleId (exemplo, usando RoleRepository)
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role não encontrada"));
        usuario.setRole(role);

        // Buscar a Empresa pelo empresaId (exemplo, usando EmpresaRepository)
        Empresa empresa = empresaRepository.findById(request.getEmpresaId())
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
        usuario.setEmpresa(empresa);

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


    public Usuario buscarPorCpf(String cpf) {
        return usuarioRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public List<UsuarioDTO> listarPorEmpresa(Long empresaId) {
        return usuarioRepository.findByEmpresaId(empresaId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }


    public UsuarioDTO salvarOuAtualizarPorCpf(UsuarioDTO dto) {
        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        Optional<Usuario> existente = usuarioRepository.findByCpfAndEmpresa(dto.getCpf(), empresa);

        Usuario usuario;
        if (existente.isPresent()) {
            usuario = existente.get();
            usuario.setNome(dto.getNome());
            usuario.setEmail(dto.getEmail());
            usuario.setTelefone(dto.getTelefone());
            usuario.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);
            if (dto.getRoleId() != null) {
                Role role = roleRepository.findById(Long.valueOf(dto.getRoleId()))
                        .orElseThrow(() -> new RuntimeException("Role não encontrada"));
                usuario.setRole(role);
            }
        } else {
            usuario = new Usuario();
            usuario.setNome(dto.getNome());
            usuario.setCpf(dto.getCpf());
            usuario.setEmail(dto.getEmail());
            usuario.setTelefone(dto.getTelefone());
            usuario.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);
            usuario.setEmpresa(empresa);
            if (dto.getRoleId() != null) {
                Role role = roleRepository.findById(Long.valueOf(dto.getRoleId()))
                        .orElseThrow(() -> new RuntimeException("Role não encontrada"));
                usuario.setRole(role);
            }
        }

        Usuario salvo = usuarioRepository.save(usuario);
        return toDTO(salvo);
    }

    public void deletarUsuarioPorCpfECnpj(String cpf, Long empresaId) {
        Usuario usuario = (Usuario) usuarioRepository.findByCpfAndEmpresaId(cpf, empresaId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        usuarioRepository.delete(usuario);
    }


    private UsuarioDTO toDTO(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getCpf(),
                usuario.getEmail(),
                usuario.getTelefone(),
                usuario.getAtivo(),
                usuario.getEmpresa() != null ? usuario.getEmpresa().getId() : null,
                usuario.getRole() != null
                        ? RoleType.fromId(usuario.getRole().getId()).getNome()
                        : RoleType.USUARIO.getNome(), // roleNome
                usuario.getRole() != null
                        ? String.valueOf(RoleType.fromId(usuario.getRole().getId()).getId())
                        : String.valueOf(RoleType.USUARIO.getId()) // roleId
        );
    }



}
