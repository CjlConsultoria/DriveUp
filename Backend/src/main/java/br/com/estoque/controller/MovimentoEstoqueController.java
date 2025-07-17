package br.com.estoque.controller;

import br.com.estoque.dto.ExtratoProdutoCompleto;
import br.com.estoque.model.MovimentoEstoque;
import br.com.estoque.service.MovimentoEstoqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/movimentos")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Movimentações de Estoque", description = "Endpoints relacionados ao controle de entrada e saída de produtos")
public class MovimentoEstoqueController {

    private final MovimentoEstoqueService movimentoService;

    @Operation(
            summary = "Registrar movimentação de estoque",
            description = "Registra uma entrada ou saída de produto no estoque"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimentação registrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PostMapping
    public ResponseEntity<MovimentoEstoque> registrar(@RequestBody MovimentoEstoque movimento) {
        return ResponseEntity.ok(movimentoService.registrarMovimento(movimento));
    }

    @Operation(
            summary = "Listar movimentações",
            description = "Lista as movimentações filtrando por código do produto, tipo de movimentação e intervalo de datas"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista paginada de movimentações retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping
    public Page<MovimentoEstoque> listar(
            @RequestParam(required = false) String codigoProduto,
            @RequestParam(required = false) String tipoMovimento,
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim,
            Pageable pageable
    ) {
        return movimentoService.buscarFiltrado(codigoProduto, tipoMovimento, dataInicio, dataFim, pageable);
    }

    @Operation(
            summary = "Resumo de saídas por produto",
            description = "Retorna o total de saídas registradas para cada produto"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resumo de saídas retornado com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/saidas")
    public ResponseEntity<Map<Long, Long>> resumoSaidas() {
        Map<Long, Long> saidas = movimentoService.getTotalSaidasPorProduto();
        return ResponseEntity.ok(saidas);
    }

    @Operation(
            summary = "Extrato completo de um produto",
            description = "Retorna um extrato com informações detalhadas sobre as movimentações de um produto"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Extrato do produto retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/extrato/{produtoId}")
    public ResponseEntity<ExtratoProdutoCompleto> getExtratoProduto(@PathVariable Long produtoId) {
        ExtratoProdutoCompleto extrato = movimentoService.gerarExtratoCompleto(produtoId);
        return ResponseEntity.ok(extrato);
    }

    @Operation(
            summary = "Buscar movimentação específica",
            description = "Retorna os detalhes de uma movimentação de estoque pelo ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimentação encontrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Movimentação não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/extrato/movimentacao/{movimentacaoId}")
    public ResponseEntity<MovimentoEstoque> getExtratoMovimentacao(@PathVariable Long movimentacaoId) {
        MovimentoEstoque extrato = movimentoService.gerarExtratoCompletoMovimentacao(movimentacaoId);
        return ResponseEntity.ok(extrato);
    }
}
