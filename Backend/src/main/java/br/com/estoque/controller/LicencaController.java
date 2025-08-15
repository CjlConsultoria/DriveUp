package br.com.estoque.controller;

import br.com.estoque.dto.LicencaDTO;
import br.com.estoque.dto.LicencaDetalhadaDTO;
import br.com.estoque.dto.LicencaStatusDTO;
import br.com.estoque.model.TipoLicenca;
import br.com.estoque.service.LicencaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/licencas")
public class LicencaController {

    @Autowired
    private LicencaService licencaService;

    @PostMapping
    public ResponseEntity<LicencaDTO> salvar(@RequestBody LicencaDTO dto) {
        return ResponseEntity.ok(licencaService.criarOuAtualizar(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        licencaService.desativarLicenca(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/empresa/{empresaId}/valida")
    public ResponseEntity<Boolean> isLicencaValida(@PathVariable Long empresaId) {
        return ResponseEntity.ok(licencaService.isLicencaValida(empresaId));
    }

    @GetMapping("/empresa/{empresaId}/status")
    public ResponseEntity<LicencaStatusDTO> getStatus(@PathVariable Long empresaId) {
        return ResponseEntity.ok(licencaService.getStatusLicenca(empresaId));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<LicencaDetalhadaDTO>> buscarPaginadoComFiltro(
            @RequestParam(required = false) String empresaNome,
            @RequestParam(required = false) String usuarioNome,
            @RequestParam(required = false) String cpf,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(licencaService.buscarLicencasPaginadas(empresaNome, usuarioNome, cpf, pageable));
    }

    @GetMapping("/all")
    public ResponseEntity<List<TipoLicenca>> buscarTipoLicencas() {
        return ResponseEntity.ok(licencaService.buscarTipos());
    }
}
