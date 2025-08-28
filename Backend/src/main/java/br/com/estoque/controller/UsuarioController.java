package br.com.estoque.controller;

import br.com.estoque.dto.UsuarioDTO;
import br.com.estoque.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    // 🔹 Listagem com filtro opcional
    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<UsuarioDTO>> listarPorEmpresa(
            @PathVariable Long empresaId,
            @RequestParam(required = false) String filtro) {
        List<UsuarioDTO> usuarios = usuarioService.listarPorEmpresaComFiltro(empresaId, filtro);
        return ResponseEntity.ok(usuarios);
    }

    // 🔹 Criar ou atualizar usuário
        @PostMapping
        public ResponseEntity<UsuarioDTO> salvarOuAtualizar(@RequestBody UsuarioDTO dto) {
            UsuarioDTO salvo = usuarioService.salvarOuAtualizar(dto);
            return ResponseEntity.ok(salvo);
        }

    // 🔹 Deletar usuário por CPF + empresaId
    @DeleteMapping
    public ResponseEntity<Void> deletar(@RequestParam String cpf,
                                        @RequestParam Long empresaId) {
        usuarioService.deletarUsuarioPorCpfECnpj(cpf, empresaId);
        return ResponseEntity.noContent().build();
    }

    // 🔹 Buscar usuário por ID
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable Long id) {
        // Supondo que você tenha método para buscar DTO por id
        UsuarioDTO usuario = usuarioService.buscarPorIdDTO(id);
        return ResponseEntity.ok(usuario);
    }

    // 🔹 Buscar usuário por CPF + empresaId
// 🔹 Buscar usuário por CPF + empresaId
    @GetMapping("/cpf/{cpf}/empresa/{empresaId}")
    public ResponseEntity<UsuarioDTO> buscarPorCpf(
            @PathVariable String cpf,
            @PathVariable Long empresaId) {
        UsuarioDTO usuario = usuarioService.buscarPorCpfECnpj(cpf, empresaId);
        return ResponseEntity.ok(usuario);
    }


}

