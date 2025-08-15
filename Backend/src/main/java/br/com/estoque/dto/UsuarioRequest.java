package br.com.estoque.dto;

import br.com.estoque.model.Role;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UsuarioRequest {
    @NotBlank
    private String nome;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String senha;

    @NotBlank
    private String telefone;

    @NotBlank
    private String cpf;

    @NotNull
    private Long roleId;

    private EnderecoDTO endereco;

    // Adiciona o id da empresa associada
    @NotNull
    private Long empresaId;

    @Data
    public static class EnderecoDTO {
        private String cep;
        private String rua;
        private String numero;
        private String bairro;
        private String cidade;
        private String estado;
    }
}
