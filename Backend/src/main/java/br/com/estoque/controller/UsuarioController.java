package br.com.estoque.controller;

import br.com.estoque.dto.UsuarioDTO;
import br.com.estoque.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<UsuarioDTO>> listarPorEmpresa(@PathVariable Long empresaId) {
        return ResponseEntity.ok(usuarioService.listarPorEmpresa(empresaId));
    }


    @PostMapping
    public ResponseEntity<UsuarioDTO> salvar(@RequestBody UsuarioDTO dto) {
        UsuarioDTO usuarioSalvo = usuarioService.salvarOuAtualizarPorCpf(dto);
        return ResponseEntity.ok(usuarioSalvo);
    }


    @DeleteMapping("/cpf")
    public ResponseEntity<Void> deletarPorCpf(
            @RequestParam String cpf,
            @RequestParam Long empresaId) {
        usuarioService.deletarUsuarioPorCpfECnpj(cpf, empresaId);
        return ResponseEntity.noContent().build();
    }


}
