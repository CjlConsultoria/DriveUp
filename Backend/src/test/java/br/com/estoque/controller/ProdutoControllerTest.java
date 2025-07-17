package br.com.estoque.controller;

import br.com.estoque.model.Categoria;
import br.com.estoque.model.Produto;
import br.com.estoque.model.enums.TipoProduto;
import br.com.estoque.security.SecurityTestConfig;
import br.com.estoque.service.ProdutoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@Import(SecurityTestConfig.class)
@WebMvcTest(ProdutoController.class)
class ProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProdutoService produtoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void criar_deveRetornarProdutoCriado() throws Exception {

        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("ELETRONICO");

        Produto produto = Produto.builder()
                .id(1L)
                .codigo("001")
                .descricao("Produto Teste")
                .categoria(categoria)
                .valorFornecedor(new BigDecimal("10.00"))
                .quantidadeEstoque(100)
                .ativo(true)
                .build();

        when(produtoService.salvar(any(Produto.class))).thenReturn(produto);

        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(produto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(produto.getId()))
                .andExpect(jsonPath("$.descricao").value(produto.getDescricao()));

    }

    @Test
    void listar_deveRetornarPaginaDeProdutos() throws Exception {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("ELETRONICO");

        Produto produto = Produto.builder()
                .id(1L)
                .codigo("001")
                .descricao("Produto Teste")
                .categoria(categoria)
                .valorFornecedor(new BigDecimal("10.00"))
                .quantidadeEstoque(100)
                .ativo(true)
                .build();

        List<Produto> lista = List.of(produto);
        Page<Produto> pagina = new PageImpl<>(lista);

        when(produtoService.buscarFiltrado(
                any(), any(), any(), any(Pageable.class))
        ).thenReturn(pagina);

        mockMvc.perform(get("/produtos")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].descricao").value("Produto Teste"));
    }


    @Test
    void buscar_deveRetornarProduto() throws Exception {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("ELETRONICO");

        Produto produto = Produto.builder()
                .id(1L)
                .codigo("001")
                .descricao("Produto Teste")
                .categoria(categoria)
                .valorFornecedor(new BigDecimal("10.00"))
                .quantidadeEstoque(100)
                .ativo(true)
                .build();

        when(produtoService.buscarPorId(1L)).thenReturn(produto);

        mockMvc.perform(get("/produtos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao").value("Produto Teste"));
    }
}
