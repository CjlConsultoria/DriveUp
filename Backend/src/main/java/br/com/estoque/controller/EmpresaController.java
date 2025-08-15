package br.com.estoque.controller;

import br.com.estoque.exception.EmpresaComUsuariosException;
import br.com.estoque.model.Empresa;
import br.com.estoque.service.EmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

    @Autowired
    private EmpresaService empresaService;


    @GetMapping("/all")
    public ResponseEntity<Page<Empresa>> buscarEmpresasAll(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cnpj,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Empresa> empresas = empresaService.buscarEmpresasAll(nome, cnpj, page, size);
        return ResponseEntity.ok(empresas);
    }



    @PostMapping("/criar")
    public ResponseEntity<Empresa> criarEmpresa(@RequestBody Empresa empresa) {
        Empresa novaEmpresa = empresaService.salvarEmpresa(empresa);
        return ResponseEntity.ok(novaEmpresa);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Empresa> editarEnderecoEmpresa(
            @PathVariable Long id,
            @RequestBody Empresa enderecoAtualizado
    ) {
        Empresa empresaAtualizada = empresaService.atualizarEndereco(id, enderecoAtualizado);
        if (empresaAtualizada == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(empresaAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirEmpresa(@PathVariable Long id) {
        try {
            boolean excluiu = empresaService.excluirEmpresa(id);
            if (excluiu) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        } catch (EmpresaComUsuariosException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
