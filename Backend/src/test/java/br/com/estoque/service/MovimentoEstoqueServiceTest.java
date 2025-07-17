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
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MovimentoEstoqueServiceTest {

    @Mock
    private MovimentoEstoqueRepository movimentoRepo;

    @Mock
    private ProdutoRepository produtoRepo;

    @InjectMocks
    private MovimentoEstoqueService movimentoService;

    private Produto produto;
    private MovimentoEstoque entrada;
    private MovimentoEstoque saida;

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

        entrada = new MovimentoEstoque();
        entrada.setProduto(produto);
        entrada.setTipo(TipoMovimentacao.ENTRADA);
        entrada.setValorVenda(new BigDecimal("15.00"));
        entrada.setDataVenda(LocalDateTime.now());
        entrada.setQuantidade(50);

        saida = new MovimentoEstoque();
        saida.setProduto(produto);
        saida.setTipo(TipoMovimentacao.SAIDA);
        saida.setValorVenda(new BigDecimal("15.00"));
        saida.setDataVenda(LocalDateTime.now());
        saida.setQuantidade(30);
    }


    @Test
    void registrarMovimento_entrada_deveSomarAoEstoque() {
        when(produtoRepo.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        when(movimentoRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        MovimentoEstoque salvo = movimentoService.registrarMovimento(entrada);

        assertEquals(150, salvo.getProduto().getQuantidadeEstoque()); // 100 + 50
        verify(produtoRepo).save(any());
        verify(movimentoRepo).save(any());
    }

    @Test
    void registrarMovimento_saida_comQuantidadeSuficiente_deveSubtrairDoEstoque() {
        when(produtoRepo.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        when(movimentoRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        MovimentoEstoque salvo = movimentoService.registrarMovimento(saida);

        assertEquals(70, salvo.getProduto().getQuantidadeEstoque()); // 100 - 30
        verify(produtoRepo).save(any());
        verify(movimentoRepo).save(any());
    }

    @Test
    void registrarMovimento_saida_comQuantidadeInsuficiente_deveLancarExcecao() {
        saida.setQuantidade(150); // maior que estoque atual 100

        when(produtoRepo.findById(1L)).thenReturn(Optional.of(produto));

        NegocioException e = assertThrows(NegocioException.class, () -> movimentoService.registrarMovimento(saida));
        assertEquals("Quantidade insuficiente em estoque", e.getMessage());
    }

    @Test
    void registrarMovimento_tipoInvalido_deveLancarExcecao() {
        MovimentoEstoque mov = new MovimentoEstoque();
        mov.setProduto(produto);
        mov.setTipo(null); // Tipo inválido
        mov.setValorVenda(new BigDecimal("15.00"));
        mov.setDataVenda(LocalDateTime.now());
        mov.setQuantidade(10);

        when(produtoRepo.findById(1L)).thenReturn(Optional.of(produto));

        NegocioException e = assertThrows(NegocioException.class, () -> movimentoService.registrarMovimento(mov));
        assertEquals("Tipo de movimentação inválido", e.getMessage());
    }

}
