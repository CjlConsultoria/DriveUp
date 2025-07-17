package br.com.estoque.model.specification;

import br.com.estoque.model.Cliente;
import org.springframework.data.jpa.domain.Specification;

public class ClienteSpecification {

    public static Specification<Cliente> comFiltros(String nome, String email, String cpfCnpj) {
        return Specification
                .where(nome != null ? contemIgnoreCase("nome", nome) : null)
                .and(email != null ? contemIgnoreCase("email", email) : null)
                .and(cpfCnpj != null ? contemIgnoreCase("cpfCnpj", cpfCnpj) : null);
    }

    private static Specification<Cliente> contemIgnoreCase(String campo, String valor) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get(campo)), "%" + valor.toLowerCase() + "%");
    }
}


