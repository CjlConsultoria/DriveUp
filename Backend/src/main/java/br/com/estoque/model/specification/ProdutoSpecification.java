package br.com.estoque.model.specification;

import br.com.estoque.model.Produto;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;

public class ProdutoSpecification {

    public static Specification<Produto> filtrar(String codigo, String descricao, String categoria) {
        return (root, query, cb) -> {
            Predicate predicadoAtivo = cb.isTrue(root.get("ativo"));

            Predicate predicadoCodigo = codigo == null || codigo.isEmpty()
                    ? cb.conjunction()
                    : cb.like(cb.lower(root.get("codigo")), "%" + codigo.toLowerCase() + "%");

            Predicate predicadoDescricao = descricao == null || descricao.isEmpty()
                    ? cb.conjunction()
                    : cb.like(cb.lower(root.get("descricao")), "%" + descricao.toLowerCase() + "%");

            Predicate predicadoTipo;

            if ("SEM_CATEGORIA".equalsIgnoreCase(categoria)) {
                predicadoTipo = cb.isNull(root.get("categoria"));
            } else if (categoria == null || categoria.isEmpty()) {
                predicadoTipo = cb.conjunction(); // não aplica filtro
            } else {
                predicadoTipo = cb.equal(cb.upper(root.get("categoria").get("nome")), categoria.toUpperCase());
            }

            return cb.and(predicadoAtivo, predicadoCodigo, predicadoDescricao, predicadoTipo);
        };
    }

    public static Specification<Produto> desativadosComFiltro(String descricao, Long categoriaId) {
        return (root, query, cb) -> {
            // p.ativo = false
            var predicadoAtivo = cb.isFalse(root.get("ativo"));

            // filtro descrição (LIKE)
            var predicadoDescricao = descricao == null || descricao.isEmpty() ?
                    cb.conjunction() : // sem filtro
                    cb.like(cb.lower(root.get("descricao")), "%" + descricao.toLowerCase() + "%");

            // filtro categoria
            var predicadoCategoria = categoriaId == null ?
                    cb.conjunction() :
                    cb.equal(root.get("categoria").get("id"), categoriaId);

            // combinar todos com AND
            return cb.and(predicadoAtivo, predicadoDescricao, predicadoCategoria);
        };
    }
}


