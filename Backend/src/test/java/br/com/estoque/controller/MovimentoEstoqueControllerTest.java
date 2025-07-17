package br.com.estoque.controller;

import br.com.estoque.model.MovimentoEstoque;
import br.com.estoque.model.Produto;
import br.com.estoque.model.enums.TipoMovimentacao;
import br.com.estoque.security.SecurityTestConfig;
import br.com.estoque.service.MovimentoEstoqueService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@Import(SecurityTestConfig.class)
@WebMvcTest(MovimentoEstoqueController.class)
class MovimentoEstoqueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovimentoEstoqueService movimentoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registrar_deveRetornarMovimento() throws Exception {
        Produto produto = new Produto();
        produto.setId(1L);

        MovimentoEstoque mov = new MovimentoEstoque();
        mov.setId(1L);
        mov.setProduto(produto);
        mov.setTipo(TipoMovimentacao.ENTRADA);
        mov.setQuantidade(10);
        mov.setValorVenda(new BigDecimal("20"));
        mov.setDataVenda(LocalDateTime.now());

        when(movimentoService.registrarMovimento(any(MovimentoEstoque.class))).thenReturn(mov);

        mockMvc.perform(post("/movimentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mov)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mov.getId()))
                .andExpect(jsonPath("$.quantidade").value(10));
    }

    @Test
    void listar_deveRetornarPaginaMovimentos() throws Exception {
        MovimentoEstoque mov = new MovimentoEstoque();
        mov.setId(1L);
        mov.setQuantidade(5);

        List<MovimentoEstoque> lista = List.of(mov);
        PageImpl<MovimentoEstoque> page = new PageImpl<>(lista);

        when(movimentoService.buscarFiltrado(
                any(), any(), any(), any(), any(PageRequest.class))
        ).thenReturn(page);

        mockMvc.perform(get("/movimentos")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].quantidade").value(5));
    }
}

