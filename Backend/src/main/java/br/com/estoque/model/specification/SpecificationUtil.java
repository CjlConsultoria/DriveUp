package br.com.estoque.model.specification;

import org.springframework.data.jpa.domain.Specification;

public class SpecificationUtil {
    public static <T> Specification<T> likeIgnoreCase(String field, String value) {
        return (root, query, cb) -> value == null ? null :
                cb.like(cb.lower(root.get(field)), "%" + value.toLowerCase() + "%");
    }

    public static <T> Specification<T> equal(String field, Object value) {
        return (root, query, cb) -> value == null ? null :
                cb.equal(root.get(field), value);
    }

    public static <T> Specification<T> joinLikeIgnoreCase(String joinField, String targetField, String value) {
        return (root, query, cb) -> value == null ? null :
                cb.like(cb.lower(root.join(joinField).get(targetField)), "%" + value.toLowerCase() + "%");
    }
}

