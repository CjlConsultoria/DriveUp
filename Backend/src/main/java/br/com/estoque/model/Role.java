package br.com.estoque.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TB_USUARIO_ROLE")
@Getter
@Setter
public class Role {

    @Id
    private Long id;

    @Column(name = "ROLE", nullable = false, unique = true, length = 100)
    private String name;
}

