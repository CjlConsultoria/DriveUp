package br.com.estoque.dto;

import br.com.estoque.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

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
    private Role role;

    private EnderecoDTO endereco;

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
