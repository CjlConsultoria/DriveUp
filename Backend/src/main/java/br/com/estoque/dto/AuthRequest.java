package br.com.estoque.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AuthRequest {

    @Schema(description = "Nome de usuário", example = "admin")
    private String username;

    @Schema(description = "Senha do usuário", example = "123456")
    private String password;
}
