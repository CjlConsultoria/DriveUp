package br.com.estoque.repository;

import br.com.estoque.model.MovimentoEstoque;
import br.com.estoque.model.enums.TipoMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MovimentoEstoqueRepository extends JpaRepository<MovimentoEstoque, Long>, JpaSpecificationExecutor<MovimentoEstoque> {
    @Query("SELECT SUM(m.quantidade) FROM MovimentoEstoque m WHERE m.produto.id = :produtoId AND m.tipo = :tipo")
    Integer somarQuantidadePorProdutoETipo(@Param("produtoId") Long produtoId, @Param("tipo") TipoMovimentacao tipo);

    List<MovimentoEstoque> findByProdutoIdAndTipo(Long produtoId, String saida);

    @Transactional
    void deleteByProdutoId(Long produtoId);

    List<MovimentoEstoque> findByProdutoId(Long produtoId);

    @Query("SELECT m.produto.id, COUNT(m) FROM MovimentoEstoque m WHERE m.tipo = 'SAIDA' GROUP BY m.produto.id")
    List<Object[]> contarSaidasPorProduto();

}

