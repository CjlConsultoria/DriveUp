package br.com.estoque.model;

import br.com.estoque.model.enums.CodigoTipoLicenca;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
@Data
@Entity
@Table(name = "tipo_licenca")
public class TipoLicenca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome da licença é obrigatório")
    @Column(nullable = false, unique = true)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    @NotNull(message = "Código do tipo de licença é obrigatório")
    private CodigoTipoLicenca codigo;

    @NotNull(message = "Limite de usuários é obrigatório")
    @Min(value = 1, message = "A licença deve permitir no mínimo 1 usuário")
    private Integer limiteUsuarios;

    private String descricao;

    public TipoLicenca() {}

    public TipoLicenca(String nome, CodigoTipoLicenca codigo, Integer limiteUsuarios, String descricao) {
        this.nome = nome;
        this.codigo = codigo;
        this.limiteUsuarios = limiteUsuarios;
        this.descricao = descricao;
    }
}
