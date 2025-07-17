package br.com.estoque.controller;

import br.com.estoque.dto.VeiculoDTO;
import br.com.estoque.service.VeiculoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/veiculos")
@SecurityRequirement(name = "bearer-key")
@CrossOrigin
public class VeiculoController {

    @Autowired
    private VeiculoService veiculoService;

    @GetMapping
    public Page<VeiculoDTO> listar(
            @RequestParam(required = false) String placa,
            @RequestParam(required = false) String modelo,
            @RequestParam(required = false) String cpfCnpj,
            Pageable pageable
    ) {
        return veiculoService.listarComFiltro(placa, modelo, cpfCnpj, pageable);
    }


    @PostMapping
    public ResponseEntity<VeiculoDTO> salvar(@RequestBody VeiculoDTO dto) {
        return ResponseEntity.ok(veiculoService.salvar(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        veiculoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

