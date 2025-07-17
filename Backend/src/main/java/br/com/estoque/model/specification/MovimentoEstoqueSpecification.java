package br.com.estoque.model.specification;

import br.com.estoque.model.MovimentoEstoque;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.com.estoque.model.enums.TipoMovimentacao;

public class MovimentoEstoqueSpecification {

    public static Specification<MovimentoEstoque> filtrar(
            String codigoProduto,
            String tipoMovimento,
            LocalDate dataInicio,
            LocalDate dataFim
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (codigoProduto != null && !codigoProduto.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("produto").get("codigo")), "%" + codigoProduto.toLowerCase() + "%"));
            }

            if (tipoMovimento != null && !tipoMovimento.isEmpty()) {
                // Converte string para Enum
                TipoMovimentacao tipoEnum = TipoMovimentacao.valueOf(tipoMovimento);
                predicates.add(cb.equal(root.get("tipo"), tipoEnum));
            }

            if (dataInicio != null) {
                // Usando dataVenda e convertendo para LocalDateTime (início do dia)
                predicates.add(cb.greaterThanOrEqualTo(root.get("dataVenda"), dataInicio.atStartOfDay()));
            }

            if (dataFim != null) {
                // dataFim até o final do dia
                predicates.add(cb.lessThanOrEqualTo(root.get("dataVenda"), dataFim.atTime(23, 59, 59)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
