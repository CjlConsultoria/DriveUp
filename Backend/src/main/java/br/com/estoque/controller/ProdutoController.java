package br.com.estoque.controller;

import br.com.estoque.model.Produto;
import br.com.estoque.model.enums.TipoProduto;
import br.com.estoque.service.ProdutoService;
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

import java.util.List;
// ... (importações permanecem as mesmas)

@RestController
@RequestMapping("/produtos")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Produtos", description = "Operações relacionadas à gestão de produtos no estoque")
public class ProdutoController {

    private final ProdutoService produtoService;

    @Operation(summary = "Criar novo produto", description = "Cria um novo produto no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PostMapping
    public ResponseEntity<Produto> criar(@RequestBody Produto produto) {
        return ResponseEntity.ok(produtoService.salvar(produto));
    }

    @Operation(summary = "Listar produtos com filtro", description = "Lista os produtos com filtros opcionais por código, descrição e tipo, paginados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de produtos paginada retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping
    public Page<Produto> listarProdutos(
            @RequestParam(required = false) String codigo,
            @RequestParam(required = false) String descricao,
            @RequestParam(required = false) String categoria,
            Pageable pageable
    ) {
        return produtoService.buscarFiltrado(codigo, descricao, categoria, pageable);
    }

    @Operation(summary = "Listar produtos por categoria", description = "Lista produtos filtrando por categoria (ELETRONICO, ELETRODOMESTICO, MOVEL)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de produtos filtrados por categoria retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Categoria inválida"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/por-categoria/{nomeCategoria}")
    public ResponseEntity<List<Produto>> listarPorCategoria(@PathVariable String nomeCategoria) {
        List<Produto> produtos = produtoService.buscarPorCategoria(nomeCategoria);
        return ResponseEntity.ok(produtos);
    }

    @Operation(summary = "Buscar produto por ID", description = "Recupera os detalhes de um produto específico pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto encontrado e retornado"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/{id}")
    public Produto buscar(@PathVariable Long id) {
        return produtoService.buscarPorId(id);
    }

    @Operation(summary = "Atualizar produto", description = "Atualiza um produto existente pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizar(@PathVariable Long id, @RequestBody Produto produto) {
        Produto atualizado = produtoService.atualizar(id, produto);
        return ResponseEntity.ok(atualizado);
    }

    @Operation(summary = "Deletar produto", description = "Remove um produto do sistema pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Produto removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Gerar novo código", description = "Gera automaticamente um novo código único para produto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Código gerado com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/gerarCodigo")
    public ResponseEntity<String> novoCodigo() {
        String codigo = produtoService.gerarNovoCodigo();
        return ResponseEntity.ok(codigo);
    }

    @Operation(
            summary = "Listar todos os produtos paginados",
            description = "Retorna uma lista paginada completa de produtos cadastrados no sistema, sem filtros adicionais"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de produtos paginada retornada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/all")
    public Page<Produto> listarTodosPaginado(Pageable pageable) {
        return produtoService.listarTodosPaginado(pageable);
    }

    @Operation(summary = "Reativar produto", description = "Reativa um produto desativado anteriormente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto reativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PutMapping("/{id}/reativar")
    public ResponseEntity<Produto> reativarProduto(@PathVariable Long id) {
        Produto produtoReativado = produtoService.reativarProduto(id);
        return ResponseEntity.ok(produtoReativado);
    }

    @Operation(summary = "Listar produtos desativados com filtro", description = "Lista os produtos desativados com filtros opcionais por descrição e categoria, paginados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de produtos desativados retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/desativados/all")
    public Page<Produto> listarTodosDesativadosComFiltro(
            @RequestParam(required = false) String descricao,
            @RequestParam(required = false) Long categoriaId,
            Pageable pageable
    ) {
        return produtoService.listarDesativadosComFiltro(descricao, categoriaId, pageable);
    }

}
