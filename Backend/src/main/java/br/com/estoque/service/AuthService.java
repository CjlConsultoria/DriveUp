package br.com.estoque.service;

import br.com.estoque.exception.ApiException;
import br.com.estoque.model.Licenca;
import br.com.estoque.model.Usuario;
import br.com.estoque.repository.EmpresaRepository;
import br.com.estoque.repository.LicencaRepository;
import br.com.estoque.repository.UsuarioRepository;
import br.com.estoque.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtTokenUtil;
    private final LicencaRepository licencaRepository;
    private final EmpresaRepository empresaRepository;

    public AuthService(UsuarioRepository usuarioRepository, BCryptPasswordEncoder passwordEncoder, JwtUtil jwtTokenUtil, LicencaRepository licencaRepository, EmpresaRepository empresaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
        this.licencaRepository = licencaRepository;
        this.empresaRepository = empresaRepository;
    }


    public String generateToken(String cpf, String password) {
        Usuario user = usuarioRepository.findByCpf(cpf)
                .orElseThrow(() -> new ApiException.NotFoundException("Usuário não encontrado pelo CPF: " + cpf));

        if (!Boolean.TRUE.equals(user.getAtivo())) {
            throw new ApiException.ForbiddenException("Usuário está inativo.");
        }

        Licenca licenca = (Licenca) licencaRepository.findByEmpresa(user.getEmpresa())
                .orElseThrow(() -> new ApiException.ForbiddenException("Empresa não possui licença ativa."));

        if (!licenca.isValida()) {
            throw new ApiException.ForbiddenException("Licença da empresa está expirada ou inválida.");
        }

        if (passwordEncoder.matches(password, user.getSenha())) {
            return jwtTokenUtil.gerarToken(user.getCpf());
        } else {
            throw new ApiException.UnauthorizedException("CPF ou senha inválidos.");
        }
    }

}
