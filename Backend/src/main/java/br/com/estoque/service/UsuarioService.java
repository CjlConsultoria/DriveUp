package br.com.estoque.service;

import br.com.estoque.dto.EnderecoDTO;
import br.com.estoque.dto.UsuarioDTO;
import br.com.estoque.dto.UsuarioRequest;
import br.com.estoque.model.*;
import br.com.estoque.model.enums.RoleType;
import br.com.estoque.model.enums.TipoTemplateEmail;
import br.com.estoque.repository.EmpresaRepository;
import br.com.estoque.repository.LicencaRepository;
import br.com.estoque.repository.RoleRepository;
import br.com.estoque.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmpresaRepository empresaRepository;
    private final RoleRepository roleRepository;
    private final EmailService emailService;
    private final LicencaRepository licencaRepository;
    @Transactional
    public UsuarioDTO salvarOuAtualizar(UsuarioDTO dto) {
        // 🔹 Buscar empresa
        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada"));

        // 🔹 Verificar se usuário já existe na empresa
        Optional<Usuario> existente = usuarioRepository.findByCpfAndEmpresa(dto.getCpf(), empresa);

        Usuario usuario;
        String senhaGerada = null;

        if (existente.isPresent()) {
            // 🔹 Usuário existe
            if (dto.getAtualizar() == null || !dto.getAtualizar()) {
                throw new IllegalStateException("Usuário já existe e atualização não foi permitida.");
            }

            // 🔹 Atualizar usuário existente
            usuario = existente.get();
            usuario.setNome(dto.getNome());
            usuario.setEmail(dto.getEmail());
            usuario.setTelefone(dto.getTelefone());
            usuario.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);
            usuario.setEndereco(toEnderecoDTO(dto.getEndereco()));

            if (dto.getRoleId() != null) {
                Role role = roleRepository.findById(dto.getRoleId())
                        .orElseThrow(() -> new EntityNotFoundException("Role não encontrada"));
                usuario.setRole(role);
            }

        } else {
            // 🔹 Buscar licença ativa da empresa
            Licenca licencaAtiva = licencaRepository
                    .findByEmpresaIdAndAtivaTrue(dto.getEmpresaId())
                    .stream()
                    .filter(Licenca::isValida)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Empresa não possui licença ativa."));

            // 🔹 Verificar limite de usuários
            int limiteUsuarios = licencaAtiva.getTipoLicenca().getLimiteUsuarios();
            long qtdUsuariosAtuais = usuarioRepository.countByEmpresa(empresa);

            if (qtdUsuariosAtuais >= limiteUsuarios) {
                throw new IllegalStateException("Limite de usuários da licença atingido para esta empresa.");
            }

            // 🔹 Criar novo usuário
            usuario = new Usuario();
            usuario.setNome(dto.getNome());
            usuario.setCpf(dto.getCpf());
            usuario.setEmail(dto.getEmail());
            usuario.setTelefone(dto.getTelefone());
            usuario.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);
            usuario.setEmpresa(empresa);
            usuario.setEndereco(toEnderecoDTO(dto.getEndereco()));

            if (dto.getRoleId() != null) {
                Role role = roleRepository.findById(dto.getRoleId())
                        .orElseThrow(() -> new EntityNotFoundException("Role não encontrada"));
                usuario.setRole(role);
            }

            // 🔹 Gerar senha temporária
            senhaGerada = UUID.randomUUID().toString().substring(0, 8);
            usuario.setSenha(passwordEncoder.encode(senhaGerada));
        }

        // 🔹 Salvar usuário
        Usuario salvo = usuarioRepository.save(usuario);

        // 🔹 Enviar email caso seja criação
        if (senhaGerada != null) {
            Map<String, Object> templateVars = new HashMap<>();
            templateVars.put("usuario", salvo);
            templateVars.put("senha", senhaGerada);
            emailService.enviarEmailComTemplate(salvo.getEmail(), TipoTemplateEmail.BEM_VINDO, templateVars);
        }

        return toUsuarioDTO(salvo);
    }



    // 🔹 Deletar usuário por CPF + empresaId
    @Transactional
    public void deletarUsuarioPorCpfECnpj(String cpf, Long empresaId) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        Usuario usuario = usuarioRepository.findByCpfAndEmpresa(cpf, empresa)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuarioRepository.delete(usuario);
    }

    // 🔹 Listar usuários por empresa com filtro opcional (nome, email, cpf)
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarPorEmpresaComFiltro(Long empresaId, String filtro) {
        List<Usuario> usuarios = usuarioRepository.findByEmpresaId(empresaId);

        if (filtro != null && !filtro.isEmpty()) {
            String filtroLower = filtro.toLowerCase();
            usuarios = usuarios.stream()
                    .filter(u -> u.getNome().toLowerCase().contains(filtroLower) ||
                            u.getEmail().toLowerCase().contains(filtroLower) ||
                            u.getCpf().contains(filtroLower))
                    .collect(Collectors.toList());
        }

        return usuarios.stream()
                .map((Usuario u) -> toUsuarioDTO(u))   // <- idem aqui
                .collect(Collectors.toList());
    }

    public UsuarioDTO buscarPorCpfECnpj(String cpf, Long empresaId) {
        return usuarioRepository.findByCpfAndEmpresaId(cpf, empresaId)
                .map(this::toUsuarioDTO)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado para CPF " + cpf));
    }



    private UsuarioDTO toUsuarioDTO(Usuario usuario) {
        return UsuarioDTO.builder()
                .nome(usuario.getNome())
                .cpf(usuario.getCpf())
                .email(usuario.getEmail())
                .telefone(usuario.getTelefone())
                .ativo(usuario.getAtivo())
                .empresaId(usuario.getEmpresa() != null ? usuario.getEmpresa().getId() : null)
                .endereco(usuario.getEndereco() != null ? toDTO(usuario.getEndereco()) : null)
                .roleId(usuario.getRole() != null ? usuario.getRole().getId() : null)
                .build();
    }


    private Endereco toEnderecoDTO(EnderecoDTO dto) {
        if (dto == null) return null;
        Endereco endereco = new Endereco();
        endereco.setCep(dto.getCep());
        endereco.setRua(dto.getRua());
        endereco.setNumero(dto.getNumero());
        endereco.setComplemento(dto.getComplemento());
        endereco.setBairro(dto.getBairro());
        endereco.setCidade(dto.getCidade());
        endereco.setEstado(dto.getEstado());
        return endereco;
    }

    private EnderecoDTO toDTO(Endereco endereco) {
        if (endereco == null) return null;
        return new EnderecoDTO(
                endereco.getCep(),
                endereco.getRua(),
                endereco.getNumero(),
                endereco.getComplemento(),
                endereco.getBairro(),
                endereco.getCidade(),
                endereco.getEstado()
        );
    }

    // Retorna DTO por ID
    public UsuarioDTO buscarPorIdDTO(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return toUsuarioDTO(usuario);
    }

    // Retorna DTO por CPF
    public UsuarioDTO buscarPorCpfDTO(String cpf) {
        Usuario usuario = usuarioRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return toUsuarioDTO(usuario);
    }


}
