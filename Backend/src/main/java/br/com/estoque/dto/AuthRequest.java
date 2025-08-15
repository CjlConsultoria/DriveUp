package br.com.estoque.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AuthRequest {

    @Schema(description = "CPF de usuário", example = "CPF")
    private String cpf;

    @Schema(description = "Senha do usuário", example = "123456")
    private String password;
}
