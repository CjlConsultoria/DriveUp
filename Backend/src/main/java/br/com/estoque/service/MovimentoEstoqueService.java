package br.com.estoque.service;

import br.com.estoque.dto.ExtratoProdutoCompleto;
import br.com.estoque.exception.NegocioException;
import br.com.estoque.model.MovimentoEstoque;
import br.com.estoque.model.Produto;
import br.com.estoque.model.enums.TipoMovimentacao;
import br.com.estoque.model.specification.MovimentoEstoqueSpecification;
import br.com.estoque.repository.MovimentoEstoqueRepository;
import br.com.estoque.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MovimentoEstoqueService {

    private final MovimentoEstoqueRepository movimentoRepo;
    private final ProdutoRepository produtoRepo;
    NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    @Transactional
    public MovimentoEstoque registrarMovimento(MovimentoEstoque movimento) {
        Produto produto = produtoRepo.findById(movimento.getProduto().getId())
                .orElseThrow(() -> new NegocioException("Produto não encontrado"));

        if (movimento.getTipo() == TipoMovimentacao.SAIDA) {
            if (produto.getQuantidadeEstoque() < movimento.getQuantidade()) {
                throw new NegocioException("Quantidade insuficiente em estoque");
            }
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - movimento.getQuantidade());

            String valorFormatado = formatoMoeda.format(movimento.getValorVenda());
            movimento.setDescricao("Venda de produto no valor de " + valorFormatado);

        } else if (movimento.getTipo() == TipoMovimentacao.ENTRADA) {
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + movimento.getQuantidade());

            String valorFormatado = formatoMoeda.format(movimento.getValorVenda());
            movimento.setDescricao("Entrada de produto pelo fornecedor no valor de " + valorFormatado);
        } else {
            throw new NegocioException("Tipo de movimentação inválido");
        }



        produtoRepo.save(produto);
        if (movimento.getTipo() == TipoMovimentacao.SAIDA && movimento.getValorVenda() == null) {
            throw new NegocioException("Valor de venda obrigatório para saída de produto");
        }

        if (movimento.getDataVenda() == null) {
            movimento.setDataVenda(LocalDateTime.now());
        }

        return movimentoRepo.save(movimento);
    }

    public List<MovimentoEstoque> listarTodos() {
        return movimentoRepo.findAll();
    }

    public Page<MovimentoEstoque> buscarFiltrado(String codigoProduto, String tipoMovimento, LocalDate dataInicio, LocalDate dataFim, Pageable pageable) {
        Specification<MovimentoEstoque> spec = MovimentoEstoqueSpecification.filtrar(codigoProduto, tipoMovimento, dataInicio, dataFim);
        return movimentoRepo.findAll(spec, pageable);
    }


    public Map<Long, Long> getTotalSaidasPorProduto() {
        List<Object[]> resultados = movimentoRepo.contarSaidasPorProduto();
        Map<Long, Long> saidasPorProduto = new HashMap<>();

        for (Object[] row : resultados) {
            Long produtoId = (Long) row[0];
            Long totalSaidas = (Long) row[1];
            saidasPorProduto.put(produtoId, totalSaidas);
        }

        return saidasPorProduto;
    }

    public ExtratoProdutoCompleto gerarExtratoCompleto(Long produtoId) {
        Produto produto = produtoRepo.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        List<MovimentoEstoque> movimentos = movimentoRepo.findByProdutoId(produtoId);

        int entrada = 0;
        int saida = 0;
        BigDecimal lucroTotal = BigDecimal.ZERO;
        List<ExtratoProdutoCompleto.MovimentoDetalhado> listaMovimentos = new ArrayList<>();

        for (MovimentoEstoque m : movimentos) {
            ExtratoProdutoCompleto.MovimentoDetalhado movDet = new ExtratoProdutoCompleto.MovimentoDetalhado();

            movDet.setDataVenda(m.getDataVenda());
            movDet.setTipo(m.getTipo());
            movDet.setQuantidade(m.getQuantidade());
            movDet.setValorVenda(m.getValorVenda());

            if (m.getTipo() == TipoMovimentacao.ENTRADA) {
                entrada += m.getQuantidade();
                movDet.setLucro(BigDecimal.ZERO);
            } else if (m.getTipo() == TipoMovimentacao.SAIDA) {
                saida += m.getQuantidade();

                BigDecimal lucroMov = m.getValorVenda()
                        .subtract(produto.getValorFornecedor())
                        .multiply(BigDecimal.valueOf(m.getQuantidade()));

                movDet.setLucro(lucroMov);
                lucroTotal = lucroTotal.add(lucroMov);
            } else { // EXCLUSAO
                movDet.setLucro(BigDecimal.ZERO);
            }

            listaMovimentos.add(movDet);
        }

        ExtratoProdutoCompleto extrato = new ExtratoProdutoCompleto();
        extrato.setProdutoId(produto.getId());
        extrato.setDescricao(produto.getDescricao());
        extrato.setQuantidadeTotalEntrada(entrada);
        extrato.setQuantidadeTotalSaida(saida);
        extrato.setSaldoAtual(produto.getQuantidadeEstoque());
        extrato.setLucroTotal(lucroTotal);
        extrato.setMovimentos(listaMovimentos);

        return extrato;
    }
    public MovimentoEstoque gerarExtratoCompletoMovimentacao(Long produtoId) {
        MovimentoEstoque movimentoEstoque = movimentoRepo.findById(produtoId).orElseThrow(() -> new NegocioException("Movimentacao não encontrada"));
        return movimentoEstoque;
    }

}
