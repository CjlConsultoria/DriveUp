package br.com.estoque.model.specification;

import br.com.estoque.model.Licenca;
import br.com.estoque.model.Usuario;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;

public class LicencaSpecification {

    public static Specification<Licenca> filter(String empresaNome, String usuarioNome, String cpf) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            // Filtra por nome da empresa
            if (empresaNome != null && !empresaNome.isBlank()) {
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("empresa").get("nome")), "%" + empresaNome.toLowerCase() + "%")
                );
            }

            // Filtra por usuário somente se necessário
            if ((usuarioNome != null && !usuarioNome.isBlank()) || (cpf != null && !cpf.isBlank())) {
                Join<Object, Usuario> userJoin = root.join("empresa").join("usuarios", JoinType.LEFT);

                if (usuarioNome != null && !usuarioNome.isBlank()) {
                    predicate = cb.and(predicate,
                            cb.like(cb.lower(userJoin.get("nome")), "%" + usuarioNome.toLowerCase() + "%")
                    );
                }

                if (cpf != null && !cpf.isBlank()) {
                    predicate = cb.and(predicate, cb.equal(userJoin.get("cpf"), cpf));
                }
            }

            // Apenas licenças ativas
            predicate = cb.and(predicate, cb.isTrue(root.get("ativa")));

            return predicate;
        };
    }


}

