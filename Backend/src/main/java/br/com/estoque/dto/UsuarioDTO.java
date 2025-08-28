package br.com.estoque.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UsuarioDTO {
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private Boolean ativo;
    private Long empresaId;
    private EnderecoDTO endereco;
    private Long roleId;

    // só para request, não precisa ir no banco
    private Boolean atualizar;
}

