package br.com.estoque.controller;

import br.com.estoque.dto.AuthRequest;
import br.com.estoque.dto.UsuarioDTO;
import br.com.estoque.dto.UsuarioRequest;
import br.com.estoque.model.Usuario;
import br.com.estoque.security.JwtUtil;
import br.com.estoque.service.AuthService;
import br.com.estoque.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            String token = authService.generateToken(authRequest.getCpf(), authRequest.getPassword());
            return ResponseEntity.ok(Map.of("token", token));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body(Map.of("status", 401, "message", "Usuário ou senha inválidos"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> buscarUsuarioLogado(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("status", 401, "message", "Usuário não autenticado"));
        }
        UsuarioDTO usuario = usuarioService.buscarPorCpfDTO(authentication.getName());
        return ResponseEntity.ok(usuario);
    }
}

