package br.com.estoque.service;

import br.com.estoque.exception.NegocioException;
import br.com.estoque.model.Categoria;
import br.com.estoque.model.MovimentoEstoque;
import br.com.estoque.model.Produto;
import br.com.estoque.model.enums.TipoMovimentacao;
import br.com.estoque.model.enums.TipoProduto;
import br.com.estoque.repository.MovimentoEstoqueRepository;
import br.com.estoque.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepo;

    @Mock
    private MovimentoEstoqueRepository movimentoRepo;

    @InjectMocks
    private ProdutoService produtoService;

    private Produto produto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("ELETRONICO");

        produto = Produto.builder()
                .id(1L)
                .codigo("001")
                .descricao("Produto Teste")
                .categoria(categoria)
                .valorFornecedor(new BigDecimal("10.00"))
                .quantidadeEstoque(100)
                .ativo(true)
                .build();
    }

    @Test
    void salvar_deveSalvarProduto() {
        when(produtoRepo.save(produto)).thenReturn(produto);

        Produto salvo = produtoService.salvar(produto);

        assertEquals(produto, salvo);
        verify(produtoRepo).save(produto);
    }

    @Test
    void listarTodos_deveRetornarTodosProdutos() {
        List<Produto> produtos = List.of(produto);
        when(produtoRepo.findAll()).thenReturn(produtos);

        List<Produto> resultado = produtoService.listarTodos();

        assertEquals(1, resultado.size());
        assertEquals(produto, resultado.get(0));
        verify(produtoRepo).findAll();
    }

    @Test
    void buscarPorId_quandoExiste_deveRetornarProduto() {
        when(produtoRepo.findById(1L)).thenReturn(Optional.of(produto));

        Produto encontrado = produtoService.buscarPorId(1L);

        assertEquals(produto, encontrado);
    }

    @Test
    void buscarPorId_quandoNaoExiste_deveLancarExcecao() {
        when(produtoRepo.findById(1L)).thenReturn(Optional.empty());

        NegocioException e = assertThrows(NegocioException.class, () -> produtoService.buscarPorId(1L));
        assertEquals("Produto não encontrado", e.getMessage());
    }

    @Test
    void atualizar_deveAtualizarProduto() {

        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("ELETRONICO");
        Produto atualizado = Produto.builder()
                .codigo("002")
                .descricao("Novo nome")
                .categoria(categoria)
                .valorFornecedor(new BigDecimal("20.00"))
                .quantidadeEstoque(200)
                .ativo(true)
                .build();

        when(produtoRepo.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepo.save(any(Produto.class))).thenAnswer(i -> i.getArgument(0));

        Produto result = produtoService.atualizar(1L, atualizado);

        assertEquals("002", result.getCodigo());
        assertEquals("Novo nome", result.getDescricao());
        assertEquals(new BigDecimal("20.00"), result.getValorFornecedor());
        assertEquals(200, result.getQuantidadeEstoque());
        verify(produtoRepo).save(any(Produto.class));
    }

    @Test
    void deletar_deveInativarProdutoERegistrarMovimentacao() {
        produto.setAtivo(true);
        produto.setQuantidadeEstoque(10);

        when(produtoRepo.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepo.save(any(Produto.class))).thenReturn(produto);
        when(movimentoRepo.save(any(MovimentoEstoque.class))).thenReturn(new MovimentoEstoque());

        produtoService.deletar(1L);

        // Verifica se o produto foi inativado
        assertFalse(produto.isAtivo());

        // Verifica se o produto foi salvo com o novo estado (inativo)
        verify(produtoRepo).save(produto);

        // Verifica se a movimentação de EXCLUSAO foi registrada
        verify(movimentoRepo).save(argThat(m ->
                m.getProduto().equals(produto) &&
                        m.getTipo() == TipoMovimentacao.EXCLUSAO &&
                        m.getQuantidade().equals(produto.getQuantidadeEstoque())
        ));
    }

    @Test
    void deletar_quandoNaoExiste_deveLancarExcecao() {
        when(produtoRepo.findById(1L)).thenReturn(Optional.empty());

        NegocioException e = assertThrows(NegocioException.class, () -> produtoService.deletar(1L));
        assertEquals("Produto não encontrado", e.getMessage());
    }
}
