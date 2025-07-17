package br.com.estoque.model;

import br.com.estoque.model.enums.TipoMovimentacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimentoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Produto produto;

    @Column(name = "tipo")
    @Enumerated(EnumType.STRING)
    private TipoMovimentacao tipo;

    private BigDecimal valorVenda;

    private LocalDateTime dataVenda;

    private Integer quantidade;

    @Column(length = 1000)
    private String descricao;

}

