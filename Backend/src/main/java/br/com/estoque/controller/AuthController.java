package br.com.estoque.controller;

import br.com.estoque.dto.AuthRequest;
import br.com.estoque.dto.UsuarioRequest;
import br.com.estoque.model.Usuario;
import br.com.estoque.security.JwtUtil;
import br.com.estoque.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );

            // Recupera o usuário autenticado com base no principal
            Usuario usuario = (Usuario) authentication.getPrincipal();

            // Gera o token usando o email do usuário autenticado
            String token = jwtUtil.gerarToken(usuario.getEmail());

            return ResponseEntity.ok(Map.of("token", token));
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Usuário ou senha inválidos");
        }
    }


    @PostMapping
    @Operation(summary = "Cadastrar novo usuário")
    public ResponseEntity<Usuario> criarUsuario(@Valid @RequestBody UsuarioRequest request) {
        Usuario criado = usuarioService.criar(request);
        return ResponseEntity.ok(criado);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable UUID id) {
        Usuario usuario = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/me")
    public ResponseEntity<Usuario> buscarUsuarioLogado(Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioService.buscarPorEmail(email);
        return ResponseEntity.ok(usuario);
    }


}

