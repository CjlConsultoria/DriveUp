package br.com.estoque.dto;

import br.com.estoque.model.enums.TipoMovimentacao;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExtratoProdutoCompleto {

    private Long produtoId;
    private String descricao;

    private Integer quantidadeTotalEntrada;
    private Integer quantidadeTotalSaida;
    private Integer saldoAtual;

    private BigDecimal lucroTotal;

    private List<MovimentoDetalhado> movimentos;

    @Data
    public static class MovimentoDetalhado {
        private LocalDateTime dataVenda;
        private TipoMovimentacao tipo;
        private Integer quantidade;
        private BigDecimal valorVenda;
        private BigDecimal lucro;
    }
}
