package br.com.estoque.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private UUID id;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private Boolean ativo;
    private Long empresaId;
    private String roleNome;
    private String roleId;

}
