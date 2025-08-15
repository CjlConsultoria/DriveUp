package br.com.estoque.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "TB_TIPO", schema = "drive_up")
public class Tipo {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "DESCRICAO", nullable = false, unique = true)
    private String name;

    // Construtor padrão
    public Tipo() {}

    // Construtor para facilitar criação
    public Tipo(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}

