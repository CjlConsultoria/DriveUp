package br.com.estoque.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "TB_CLIENTE", schema = "DriveUp")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String cpfCnpj;
    private String email;
    private String telefone;

    @Embedded
    private Endereco endereco;
}
